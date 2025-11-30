"""
Request and response models for AI API
"""

from pydantic import BaseModel, Field
from typing import List, Optional, Dict, Any
from datetime import datetime


class EmbeddingRequest(BaseModel):
    """Request model for text embedding"""
    text: str = Field(..., description="Text to embed", min_length=1, max_length=8192)


class EmbeddingResponse(BaseModel):
    """Response model for text embedding"""
    embedding: List[float] = Field(..., description="Embedding vector")
    dimension: int = Field(..., description="Embedding dimension")


class BatchEmbeddingRequest(BaseModel):
    """Request model for batch text embedding"""
    texts: List[str] = Field(..., description="List of texts to embed", min_items=1, max_items=100)


class BatchEmbeddingResponse(BaseModel):
    """Response model for batch text embedding"""
    embeddings: List[List[float]] = Field(..., description="List of embedding vectors")
    dimensions: int = Field(..., description="Embedding dimension")
    count: int = Field(..., description="Number of embeddings")


class SummaryRequest(BaseModel):
    """Request model for text summarization"""
    text: str = Field(..., description="Text to summarize", min_length=50, max_length=10240)
    max_length: int = Field(150, description="Maximum summary length", ge=30, le=500)
    min_length: int = Field(30, description="Minimum summary length", ge=10, le=100)


class SummaryResponse(BaseModel):
    """Response model for text summarization"""
    summary: str = Field(..., description="Text summary")
    original_length: int = Field(..., description="Original text length")
    summary_length: int = Field(..., description="Summary length")
    compression_ratio: float = Field(..., description="Compression ratio")


class KeywordExtractionRequest(BaseModel):
    """Request model for keyword extraction"""
    text: str = Field(..., description="Text to analyze", min_length=10, max_length=8192)
    max_keywords: int = Field(10, description="Maximum number of keywords", ge=1, le=50)


class KeywordExtractionResponse(BaseModel):
    """Response model for keyword extraction"""
    keywords: List[str] = Field(..., description="Extracted keywords")
    count: int = Field(..., description="Number of keywords")


class DocumentProcessRequest(BaseModel):
    """Request model for document processing"""
    collection_name: Optional[str] = Field("documents", description="ChromaDB collection name")
    chunk_size: int = Field(500, description="Text chunk size", ge=100, le=2000)
    overlap: int = Field(50, description="Chunk overlap", ge=0, le=200)


class DocumentProcessResponse(BaseModel):
    """Response model for document processing"""
    document_path: str = Field(..., description="Processed document path")
    chunk_count: int = Field(..., description="Number of chunks created")
    document_ids: List[str] = Field(..., description="Document IDs in ChromaDB")
    summary: str = Field(..., description="Document summary")
    collection_name: str = Field(..., description="ChromaDB collection name")


class DocumentSearchRequest(BaseModel):
    """Request model for document search"""
    query: str = Field(..., description="Search query", min_length=3, max_length=1000)
    collection_name: str = Field("documents", description="ChromaDB collection name")
    top_results: int = Field(5, description="Number of results", ge=1, le=20)


class DocumentSearchResponse(BaseModel):
    """Response model for document search"""
    results: List[Dict[str, Any]] = Field(..., description="Search results")
    count: int = Field(..., description="Number of results")
    query: str = Field(..., description="Original query")


class HealthResponse(BaseModel):
    """Response model for health check"""
    status: str = Field(..., description="Service status")
    timestamp: datetime = Field(..., description="Check timestamp")
    service: str = Field(..., description="Service name")


class ErrorResponse(BaseModel):
    """Response model for errors"""
    error: str = Field(..., description="Error message")
    detail: Optional[str] = Field(None, description="Error details")
    timestamp: datetime = Field(default_factory=datetime.utcnow, description="Error timestamp")