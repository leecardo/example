"""
NLP-related API endpoints
"""

import logging
from fastapi import APIRouter, HTTPException, status, UploadFile, File, Form
from fastapi.responses import JSONResponse
from typing import Optional

from app.api.v1.schemas import (
    SummaryRequest, SummaryResponse,
    KeywordExtractionRequest, KeywordExtractionResponse,
    ErrorResponse
)
from app.services.nlp_service import nlp_service

logger = logging.getLogger(__name__)
nlp_router = APIRouter()


def create_error_response(message: str, detail: str = None) -> ErrorResponse:
    """Create standardized error response"""
    return ErrorResponse(error=message, detail=detail)


@nlp_router.post("/summarize", response_model=SummaryResponse)
async def summarize_text(request: SummaryRequest):
    """
    Summarize text using AI model

    - **text**: Text to summarize (50-10240 characters)
    - **max_length**: Maximum summary length (30-500, default: 150)
    - **min_length**: Minimum summary length (10-100, default: 30)
    """
    try:
        logger.info(f"Summarizing text (length: {len(request.text)})")

        summary = await nlp_service.summarize_text(
            text=request.text,
            max_length=request.max_length,
            min_length=request.min_length
        )

        response = SummaryResponse(
            summary=summary,
            original_length=len(request.text),
            summary_length=len(summary),
            compression_ratio=round(len(request.text) / len(summary), 2) if summary else 0
        )

        logger.info(f"Text summarized successfully (compression: {response.compression_ratio})")
        return response

    except ValueError as e:
        logger.error(f"Validation error in summarize_text: {e}")
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=str(e)
        )
    except Exception as e:
        logger.error(f"Unexpected error in summarize_text: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal server error while summarizing text"
        )


@nlp_router.post("/extract-keywords", response_model=KeywordExtractionResponse)
async def extract_keywords(request: KeywordExtractionRequest):
    """
    Extract keywords from text

    - **text**: Text to analyze (10-8192 characters)
    - **max_keywords**: Maximum number of keywords (1-50, default: 10)
    """
    try:
        logger.info(f"Extracting keywords from text (length: {len(request.text)})")

        keywords = await nlp_service.extract_keywords(
            text=request.text,
            max_keywords=request.max_keywords
        )

        response = KeywordExtractionResponse(
            keywords=keywords,
            count=len(keywords)
        )

        logger.info(f"Keywords extracted successfully (count: {response.count})")
        return response

    except ValueError as e:
        logger.error(f"Validation error in extract_keywords: {e}")
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=str(e)
        )
    except Exception as e:
        logger.error(f"Unexpected error in extract_keywords: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal server error while extracting keywords"
        )


@nlp_router.post("/summarize-file")
async def summarize_file(
    file: UploadFile = File(...),
    max_length: int = Form(150),
    min_length: int = Form(30)
):
    """
    Summarize content from uploaded file

    - **file**: File to summarize (supports .txt, .pdf, .doc, .docx)
    - **max_length**: Maximum summary length (default: 150)
    - **min_length**: Minimum summary length (default: 30)
    """
    try:
        # Validate file type
        allowed_types = ['.txt', '.pdf', '.doc', '.docx']
        file_ext = file.filename.split('.')[-1].lower()
        if f'.{file_ext}' not in allowed_types:
            raise HTTPException(
                status_code=status.HTTP_400_BAD_REQUEST,
                detail=f"Unsupported file type. Allowed types: {', '.join(allowed_types)}"
            )

        logger.info(f"Processing file: {file.filename}")

        # Read file content
        import tempfile
        import os

        with tempfile.NamedTemporaryFile(delete=False, suffix=f'.{file_ext}') as temp_file:
            content = await file.read()
            temp_file.write(content)
            temp_file_path = temp_file.name

        try:
            # Extract text using document service
            from app.services.document_service import document_service
            chunks = await document_service.process_document_file(temp_file_path)

            if not chunks:
                raise ValueError("No content extracted from file")

            # Combine all chunk content
            full_text = " ".join([chunk.content for chunk in chunks])

            # Summarize the combined text
            summary = await nlp_service.summarize_text(
                text=full_text,
                max_length=max_length,
                min_length=min_length
            )

            response = {
                "filename": file.filename,
                "summary": summary,
                "original_length": len(full_text),
                "summary_length": len(summary),
                "compression_ratio": round(len(full_text) / len(summary), 2) if summary else 0,
                "chunks_processed": len(chunks)
            }

            logger.info(f"File summarized successfully: {file.filename}")
            return response

        finally:
            # Clean up temporary file
            os.unlink(temp_file_path)

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error processing file {file.filename}: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal server error while processing file"
        )


@nlp_router.get("/health", response_model=dict)
async def nlp_health_check():
    """
    Health check for NLP service
    """
    try:
        health = await nlp_service.health_check()
        return health

    except Exception as e:
        logger.error(f"Error in NLP health check: {e}")
        return {
            "service": "nlp_service",
            "status": "unhealthy",
            "error": str(e)
        }


@nlp_router.get("/stats", response_model=dict)
async def nlp_service_stats():
    """
    Get NLP service statistics
    """
    try:
        stats = await nlp_service.get_service_stats()
        return stats

    except Exception as e:
        logger.error(f"Error getting NLP service stats: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to retrieve service statistics"
        )


# Exception handler for the router
@nlp_router.exception_handler(Exception)
async def nlp_exception_handler(request, exc):
    logger.error(f"Unhandled exception in NLP router: {exc}")
    return JSONResponse(
        status_code=500,
        content=create_error_response("Internal server error", str(exc)).dict()
    )