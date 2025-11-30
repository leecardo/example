"""
AI-related API endpoints
"""

import logging
from fastapi import APIRouter, HTTPException, status
from fastapi.responses import JSONResponse

from app.api.v1.schemas import (
    EmbeddingRequest, EmbeddingResponse,
    BatchEmbeddingRequest, BatchEmbeddingResponse,
    ErrorResponse
)
from app.models.ai_models import model_manager

logger = logging.getLogger(__name__)
ai_router = APIRouter()


def create_error_response(message: str, detail: str = None) -> ErrorResponse:
    """Create standardized error response"""
    return ErrorResponse(error=message, detail=detail)


@ai_router.post("/embedding", response_model=EmbeddingResponse)
async def create_embedding(request: EmbeddingRequest):
    """
    Create embedding for a single text

    - **text**: Text to embed (1-8192 characters)
    """
    try:
        logger.info(f"Creating embedding for text length: {len(request.text)}")

        embedding = await model_manager.get_embedding(request.text)

        response = EmbeddingResponse(
            embedding=embedding,
            dimension=len(embedding)
        )

        logger.info(f"Embedding created successfully (dimension: {response.dimension})")
        return response

    except ValueError as e:
        logger.error(f"Validation error in create_embedding: {e}")
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=str(e)
        )
    except Exception as e:
        logger.error(f"Unexpected error in create_embedding: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal server error while creating embedding"
        )


@ai_router.post("/embedding/batch", response_model=BatchEmbeddingResponse)
async def create_batch_embeddings(request: BatchEmbeddingRequest):
    """
    Create embeddings for multiple texts

    - **texts**: List of texts to embed (1-100 items)
    """
    try:
        logger.info(f"Creating batch embeddings for {len(request.texts)} texts")

        embeddings = await model_manager.get_embedding(request.texts)

        if not embeddings:
            raise ValueError("No embeddings generated")

        dimension = len(embeddings[0]) if isinstance(embeddings[0], list) else len(embeddings)

        response = BatchEmbeddingResponse(
            embeddings=embeddings if isinstance(embeddings[0], list) else [embeddings.tolist()],
            dimensions=dimension,
            count=len(embeddings)
        )

        logger.info(f"Batch embeddings created successfully (count: {response.count})")
        return response

    except ValueError as e:
        logger.error(f"Validation error in create_batch_embeddings: {e}")
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail=str(e)
        )
    except Exception as e:
        logger.error(f"Unexpected error in create_batch_embeddings: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Internal server error while creating batch embeddings"
        )


@ai_router.get("/models", response_model=dict)
async def list_models():
    """
    Get list of loaded AI models and their status
    """
    try:
        model_health = await model_manager.health_check()
        return model_health

    except Exception as e:
        logger.error(f"Error listing models: {e}")
        raise HTTPException(
            status_code=status.HTTP_500_INTERNAL_SERVER_ERROR,
            detail="Failed to retrieve model information"
        )


@ai_router.get("/health", response_model=dict)
async def ai_health_check():
    """
    Health check for AI models
    """
    try:
        health = await model_manager.health_check()
        return health

    except Exception as e:
        logger.error(f"Error in AI health check: {e}")
        return {
            "service": "ai_models",
            "status": "unhealthy",
            "error": str(e)
        }


# Exception handler for the router
@ai_router.exception_handler(Exception)
async def ai_exception_handler(request, exc):
    logger.error(f"Unhandled exception in AI router: {exc}")
    return JSONResponse(
        status_code=500,
        content=create_error_response("Internal server error", str(exc)).dict()
    )