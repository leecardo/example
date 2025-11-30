"""
Natural Language Processing services
"""

import logging
from typing import List, Dict, Any
import asyncio
from functools import wraps

from app.models.ai_models import model_manager

logger = logging.getLogger(__name__)


def async_error_handler(func):
    """Decorator for async error handling"""
    @wraps(func)
    async def wrapper(*args, **kwargs):
        try:
            return await func(*args, **kwargs)
        except Exception as e:
            logger.error(f"Error in {func.__name__}: {e}")
            raise
    return wrapper


class NLPService:
    """Service for Natural Language Processing tasks"""

    def __init__(self):
        self.model_manager = model_manager

    @async_error_handler
    async def create_embeddings(self, texts: List[str]) -> List[List[float]]:
        """
        Create embeddings for multiple texts

        Args:
            texts: List of text strings

        Returns:
            List of embedding vectors
        """
        if not texts:
            return []

        logger.info(f"Creating embeddings for {len(texts)} texts")

        embeddings = await self.model_manager.get_embedding(texts)

        logger.info(f"Successfully created {len(embeddings)} embeddings")
        return embeddings

    @async_error_handler
    async def create_embedding(self, text: str) -> List[float]:
        """
        Create embedding for a single text

        Args:
            text: Text string

        Returns:
            Embedding vector
        """
        logger.info(f"Creating embedding for text: {text[:100]}...")

        embedding = await self.model_manager.get_embedding(text)

        logger.info(f"Embedding created with dimension: {len(embedding)}")
        return embedding

    @async_error_handler
    async def summarize_text(
        self,
        text: str,
        max_length: int = 150,
        min_length: int = 30
    ) -> str:
        """
        Summarize text using AI model

        Args:
            text: Text to summarize
            max_length: Maximum summary length
            min_length: Minimum summary length

        Returns:
            Summarized text
        """
        if len(text.strip()) == 0:
            raise ValueError("Text cannot be empty")

        logger.info(f"Summarizing text (length: {len(text)})")

        summary = await self.model_manager.summarize_text(
            text,
            max_length=max_length,
            min_length=min_length
        )

        logger.info(f"Summary generated (length: {len(summary)})")
        return summary

    @async_error_handler
    async def extract_keywords(self, text: str, max_keywords: int = 10) -> List[str]:
        """
        Extract keywords from text (simple implementation)

        Args:
            text: Text to analyze
            max_keywords: Maximum number of keywords

        Returns:
            List of keywords
        """
        # Simple keyword extraction using TF-IDF-like approach
        import re
        from collections import Counter

        # Clean and tokenize text
        words = re.findall(r'\b[a-zA-Z]+\b', text.lower())

        # Filter out common stop words
        stop_words = {
            'the', 'is', 'at', 'which', 'on', 'a', 'an', 'and', 'or', 'but',
            'in', 'with', 'to', 'for', 'of', 'as', 'by', 'that', 'this',
            'it', 'from', 'are', 'be', 'was', 'were', 'been', 'have', 'has'
        }

        filtered_words = [word for word in words if word not in stop_words and len(word) > 3]

        # Count frequency
        word_counts = Counter(filtered_words)

        # Get most common words
        keywords = [word for word, count in word_counts.most_common(max_keywords)]

        logger.info(f"Extracted {len(keywords)} keywords")
        return keywords

    @async_error_handler
    async def batch_summarize(
        self,
        texts: List[str],
        max_length: int = 150,
        min_length: int = 30
    ) -> List[str]:
        """
        Summarize multiple texts in parallel

        Args:
            texts: List of texts to summarize
            max_length: Maximum summary length
            min_length: Minimum summary length

        Returns:
            List of summaries
        """
        if not texts:
            return []

        logger.info(f"Batch summarizing {len(texts)} texts")

        # Process texts in parallel
        tasks = [
            self.model_manager.summarize_text(text, max_length, min_length)
            for text in texts
        ]

        summaries = await asyncio.gather(*tasks, return_exceptions=True)

        # Handle exceptions
        results = []
        for i, summary in enumerate(summaries):
            if isinstance(summary, Exception):
                logger.error(f"Error summarizing text {i}: {summary}")
                # Fallback to truncated text
                results.append(texts[i][:max_length] + "...")
            else:
                results.append(summary)

        logger.info(f"Batch summarization completed: {len(results)} summaries")
        return results

    async def health_check(self) -> Dict[str, Any]:
        """Check health of NLP service"""
        model_health = await self.model_manager.health_check()

        return {
            "service": "nlp_service",
            "status": "healthy",
            "models": model_health,
            "capabilities": [
                "text_embedding",
                "text_summarization",
                "keyword_extraction",
                "batch_processing"
            ]
        }

    async def get_service_stats(self) -> Dict[str, Any]:
        """Get service statistics"""
        return {
            "service": "nlp_service",
            "version": "1.0.0",
            "model_count": len(self.model_manager.get_loaded_models()),
            "loaded_models": self.model_manager.get_loaded_models()
        }


# Global service instance
nlp_service = NLPService()