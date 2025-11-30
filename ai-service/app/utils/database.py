"""
Database connection utilities
"""

import logging
import pymysql
import redis
import chromadb
from typing import Optional

from app.utils.config import settings

logger = logging.getLogger(__name__)

# Database connections
mysql_pool: Optional[pymysql.Pool] = None
redis_client: Optional[redis.Redis] = None
chroma_client: Optional[chromadb.Client] = None


async def init_database() -> None:
    """Initialize database connections"""
    logger.info("Initializing database connections...")

    try:
        # Initialize MySQL connection pool
        await init_mysql_pool()

        # Initialize Redis connection
        await init_redis_connection()

        # Initialize ChromaDB connection
        await init_chromadb_connection()

        logger.info("All database connections initialized successfully")
    except Exception as e:
        logger.error(f"Failed to initialize database connections: {e}")
        raise


async def init_mysql_pool() -> None:
    """Initialize MySQL connection pool"""
    global mysql_pool

    try:
        mysql_pool = pymysql.create_pool(
            host=settings.DB_HOST,
            port=settings.DB_PORT,
            user=settings.DB_USER,
            password=settings.DB_PASSWORD,
            database=settings.DB_NAME,
            minsize=1,
            maxsize=10,
            charset='utf8mb4',
            autocommit=True
        )
        logger.info("MySQL connection pool initialized")
    except Exception as e:
        logger.error(f"Failed to initialize MySQL pool: {e}")
        raise


async def init_redis_connection() -> None:
    """Initialize Redis connection"""
    global redis_client

    try:
        redis_client = redis.Redis(
            host=settings.REDIS_HOST,
            port=settings.REDIS_PORT,
            password=settings.REDIS_PASSWORD,
            db=settings.REDIS_DB,
            decode_responses=True,
            socket_connect_timeout=5,
            socket_timeout=5
        )

        # Test connection
        redis_client.ping()
        logger.info("Redis connection initialized")
    except Exception as e:
        logger.error(f"Failed to initialize Redis connection: {e}")
        raise


async def init_chromadb_connection() -> None:
    """Initialize ChromaDB connection"""
    global chroma_client

    try:
        chroma_client = chromadb.HttpClient(
            host=settings.CHROMADB_HOST,
            port=settings.CHROMADB_PORT
        )

        # Test connection
        chroma_client.list_collections()
        logger.info("ChromaDB connection initialized")
    except Exception as e:
        logger.error(f"Failed to initialize ChromaDB connection: {e}")
        raise


def get_mysql_connection() -> pymysql.Connection:
    """Get MySQL connection from pool"""
    if not mysql_pool:
        raise RuntimeError("MySQL pool not initialized")
    return mysql_pool.get_connection()


def get_redis_client() -> redis.Redis:
    """Get Redis client"""
    if not redis_client:
        raise RuntimeError("Redis client not initialized")
    return redis_client


def get_chroma_client() -> chromadb.Client:
    """Get ChromaDB client"""
    if not chroma_client:
        raise RuntimeError("ChromaDB client not initialized")
    return chroma_client


async def close_database_connections() -> None:
    """Close all database connections"""
    global mysql_pool, redis_client, chroma_client

    try:
        if mysql_pool:
            mysql_pool.close()
            mysql_pool = None

        if redis_client:
            redis_client.close()
            redis_client = None

        if chroma_client:
            # ChromaDB HTTP client doesn't need explicit closing
            chroma_client = None

        logger.info("Database connections closed successfully")
    except Exception as e:
        logger.error(f"Error closing database connections: {e}")