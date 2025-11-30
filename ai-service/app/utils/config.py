"""
Configuration settings for AI Service
"""

import os
from typing import Optional
from pydantic import BaseSettings


class Settings(BaseSettings):
    """Application settings"""

    # Service Configuration
    HOST: str = "0.0.0.0"
    PORT: int = 8081
    DEBUG: bool = True

    # Database Configuration (shared with Java app)
    DB_HOST: str = "127.0.0.1"
    DB_PORT: int = 3306
    DB_NAME: str = "test"
    DB_USER: str = "root"
    DB_PASSWORD: str = "root123"

    # Redis Configuration (shared with Java app)
    REDIS_HOST: str = "122.51.168.11"
    REDIS_PORT: int = 6379
    REDIS_PASSWORD: Optional[str] = None
    REDIS_DB: int = 0

    # AI Model Configuration
    EMBEDDING_MODEL: str = "sentence-transformers/paraphrase-multilingual-MiniLM-L12-v2"
    NLP_MODEL: str = "facebook/bart-large-cnn"

    # External Services
    CHROMADB_HOST: str = "localhost"
    CHROMADB_PORT: int = 8000

    # Message Queue (shared with Java app)
    RABBITMQ_HOST: str = "122.51.168.11"
    RABBITMQ_PORT: int = 5672
    RABBITMQ_USER: str = "guest"
    RABBITMQ_PASSWORD: str = "guest"

    # Logging
    LOG_LEVEL: str = "INFO"
    LOG_FORMAT: str = "json"

    class Config:
        env_file = ".env"
        case_sensitive = True


# Global settings instance
settings = Settings()