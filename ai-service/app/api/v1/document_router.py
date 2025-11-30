"""
Document processing API endpoints
"""

import logging
import uuid
import os
from fastapi import APIRouter, HTTPException, status, UploadFile, File, Form
from fastapi.responses import JSONResponse
from typing import Optional

from app.api.v1.schemas import (
    DocumentProcessRequest, DocumentProcessResponse,
    DocumentSearchRequest, DocumentSearchResponse,
    ErrorResponse
)
from app.services.document_service import document_service

logger = logging.getLogger(__name__)
document_router = APIRouter()


def create_error_response(message: str, detail: str = None) -> ErrorResponse:
    """Create standardized error response"""
    return ErrorResponse(error=message, detail=detail)


@document_router.post("/upload", response_model=DocumentProcessResponse)
async def upload_and_process_document(
    file: UploadFile = File(...),
    collection_name: str = Form("documents"),
    chunk_size: int = Form(500),
    overlap: int = Form(50)
):
    """
    Upload and process a document for vector storage and search

    - **file**: Document file (.txt, .pdf, .doc, .docx)
    - **collection_name**: ChromaDB collection name (default: "documents")
    - **chunk_size**: Text chunk size (100-2000, default: 500)
    - **overlap**: Chunk overlap (0-200, default: 50)
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

        logger.info(f"Processing document upload: {file.filename}")

        # Save uploaded file temporarily
        upload_dir = "temp_uploads"
        os.makedirs(upload_dir, exist_ok=True)

        # Generate unique filename
        unique_filename = f"{uuid.uuid4()}_{file.filename}"
        temp_file_path = os.path.join(upload_dir, unique_filename)

        try:
            # Save uploaded file
            content = await file.read()
            with open(temp_file_path, 'wb') as temp_file:
                temp_file.write(content)

            # Process and store document
            result = await document_service.process_and_store_document(
                file_path=temp_file_path,
                collection_name=collection_name,
                chunk_size=chunk_size,
                overlap=overlap
            )

            response = DocumentProcessResponse(**result)

            logger.info(f"Document processed successfully: {file.filename}")
            return response

        finally:
            # Clean up temporary file
            if os.path.exists(temp_file_path):
                os.unlink(temp_file_path)

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error processing document {file.filename}: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal server error while processing document"
        )


@document_router.post("/search", response_model=DocumentSearchResponse)
async def search_documents(request: DocumentSearchRequest):
    """
    Search for similar documents in ChromaDB

    - **query**: Search query (3-1000 characters)
    - **collection_name**: ChromaDB collection name (default: "documents")
    - **top_results**: Number of results to return (1-20, default: 5)
    """
    try:
        logger.info(f"Searching documents with query: {request.query}")

        results = await document_service.search_similar_documents(
            query=request.query,
            collection_name=request.collection_name,
            top_results=request.top_results
        )

        response = DocumentSearchResponse(
            results=results,
            count=len(results),
            query=request.query
        )

        logger.info(f"Document search completed: {response.count} results found")
        return response

    except ValueError as e:
        logger.error(f"Validation error in search_documents: {e}")
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=str(e)
        )
    except Exception as e:
        logger.error(f"Unexpected error in search_documents: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal server error while searching documents"
        )


@document_router.post("/process-url")
async def process_document_from_url(
    url: str = Form(...),
    collection_name: str = Form("documents"),
    chunk_size: int = Form(500),
    overlap: int = Form(50)
):
    """
    Process document from URL (for remote files)

    - **url**: URL to the document
    - **collection_name**: ChromaDB collection name (default: "documents")
    - **chunk_size**: Text chunk size (default: 500)
    - **overlap**: Chunk overlap (default: 50)
    """
    try:
        import tempfile
        import aiohttp

        logger.info(f"Processing document from URL: {url}")

        # Download file from URL
        async with aiohttp.ClientSession() as session:
            async with session.get(url) as response:
                if response.status != 200:
                    raise HTTPException(
                        status_code=status.HTTP_400_BAD_REQUEST,
                        detail=f"Failed to download file from URL: {response.status}"
                    )

                content = await response.read()

                # Get filename from URL or generate one
                filename = url.split('/')[-1] or f"document_{uuid.uuid4()}"
                file_ext = filename.split('.')[-1].lower()

                # Validate file type
                allowed_types = ['txt', 'pdf', 'doc', 'docx']
                if file_ext not in allowed_types:
                    raise HTTPException(
                        status_code=status.HTTP_400_BAD_REQUEST,
                        detail=f"Unsupported file type. Allowed types: {', '.join(allowed_types)}"
                    )

                # Save temporarily
                with tempfile.NamedTemporaryFile(delete=False, suffix=f'.{file_ext}') as temp_file:
                    temp_file.write(content)
                    temp_file_path = temp_file.name

        try:
            # Process and store document
            result = await document_service.process_and_store_document(
                file_path=temp_file_path,
                collection_name=collection_name,
                chunk_size=chunk_size,
                overlap=overlap
            )

            result['source_url'] = url  # Add source URL to result
            return result

        finally:
            os.unlink(temp_file_path)

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error processing document from URL {url}: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal server error while processing document from URL"
        )


@document_router.delete("/collection/{collection_name}")
async def delete_collection(collection_name: str):
    """
    Delete a ChromaDB collection

    - **collection_name**: Name of collection to delete
    """
    try:
        logger.info(f"Deleting collection: {collection_name}")

        from app.utils.database import get_chroma_client
        chroma_client = get_chroma_client()

        try:
            chroma_client.delete_collection(name=collection_name)
            logger.info(f"Collection deleted successfully: {collection_name}")
            return {"message": f"Collection '{collection_name}' deleted successfully"}
        except Exception as e:
            if "does not exist" in str(e).lower():
                raise HTTPException(
                    status_code=status.HTTP_404_NOT_FOUND,
                    detail=f"Collection '{collection_name}' not found"
                )
            raise

    except HTTPException:
        raise
    except Exception as e:
        logger.error(f"Error deleting collection {collection_name}: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal server error while deleting collection"
        )


@document_router.get("/collections", response_model=dict)
async def list_collections():
    """
    List all ChromaDB collections
    """
    try:
        from app.utils.database import get_chroma_client
        chroma_client = get_chroma_client()

        collections = chroma_client.list_collections()
        collection_info = []

        for collection in collections:
            try:
                count = collection.count()
                collection_info.append({
                    "name": collection.name,
                    "count": count
                })
            except Exception:
                collection_info.append({
                    "name": collection.name,
                    "count": "unknown"
                })

        return {
            "collections": collection_info,
            "total": len(collection_info)
        }

    except Exception as e:
        logger.error(f"Error listing collections: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal server error while listing collections"
        )


@document_router.get("/health", response_model=dict)
async def document_health_check():
    """
    Health check for document service
    """
    try:
        health = await document_service.health_check()
        return health

    except Exception as e:
        logger.error(f"Error in document health check: {e}")
        return {
            "service": "document_service",
            "status": "unhealthy",
            "error": str(e)
        }


# Exception handler for the router
@document_router.exception_handler(Exception)
async def document_exception_handler(request, exc):
    logger.error(f"Unhandled exception in document router: {exc}")
    return JSONResponse(
        status_code=500,
        content=create_error_response("Internal server error", str(exc)).dict()
    )