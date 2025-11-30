"""
AI Service - Python-based AI and NLP microservice
Works alongside the existing Java Spring Boot application
"""

import uvicorn
from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware
from contextlib import asynccontextmanager

from app.api.v1 import ai_router, nlp_router, document_router
from app.utils.config import settings
from app.utils.logger import setup_logging
from app.utils.database import init_database


@asynccontextmanager
async def lifespan(app: FastAPI):
    """Application lifecycle management"""
    # Startup
    setup_logging()
    await init_database()
    print("🚀 AI Service is starting up...")

    yield

    # Shutdown
    print("🛑 AI Service is shutting down...")


# Create FastAPI application
app = FastAPI(
    title="AI Service API",
    description="Python-based AI and NLP microservice for example project",
    version="1.0.0",
    lifespan=lifespan
)

# CORS middleware - Allow Java Spring Boot app to connect
app.add_middleware(
    CORSMiddleware,
    allow_origins=[
        "http://localhost:8080",  # Java Spring Boot
        "http://127.0.0.1:8080",
        "*"  # Development only
    ],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Include API routers
app.include_router(ai_router, prefix="/api/v1/ai", tags=["AI"])
app.include_router(nlp_router, prefix="/api/v1/nlp", tags=["NLP"])
app.include_router(document_router, prefix="/api/v1/document", tags=["Document Processing"])


@app.get("/")
async def root():
    """Root endpoint - service status"""
    return {
        "service": "AI Service",
        "status": "running",
        "version": "1.0.0",
        "framework": "FastAPI",
        "python_version": "3.11+"
    }


@app.get("/health")
async def health_check():
    """Health check endpoint for monitoring"""
    return {
        "status": "healthy",
        "timestamp": "2024-01-01T00:00:00Z",
        "service": "ai-service"
    }


if __name__ == "__main__":
    uvicorn.run(
        "main:app",
        host=settings.HOST,
        port=settings.PORT,
        reload=settings.DEBUG,
        log_level="info"
    )