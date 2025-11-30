"""
AI model management and utilities
"""

import logging
from typing import List, Union, Optional
import numpy as np

logger = logging.getLogger(__name__)

try:
    from sentence_transformers import SentenceTransformer
    from transformers import pipeline, AutoTokenizer, AutoModel
    SENTENCE_TRANSFORMERS_AVAILABLE = True
except ImportError:
    logger.warning("sentence-transformers not available. Install with: pip install sentence-transformers")
    SENTENCE_TRANSFORMERS_AVAILABLE = False


class AIModelManager:
    """Manages AI models for embeddings and text processing"""

    def __init__(self):
        self._embedding_model = None
        self._nlp_pipeline = None
        self._tokenizer = None
        self._loaded_models = set()

    @property
    def embedding_model(self) -> Optional['SentenceTransformer']:
        """Lazy load embedding model"""
        if self._embedding_model is None and SENTENCE_TRANSFORMERS_AVAILABLE:
            from app.utils.config import settings
            try:
                logger.info(f"Loading embedding model: {settings.EMBEDDING_MODEL}")
                self._embedding_model = SentenceTransformer(settings.EMBEDDING_MODEL)
                self._loaded_models.add(settings.EMBEDDING_MODEL)
                logger.info("Embedding model loaded successfully")
            except Exception as e:
                logger.error(f"Failed to load embedding model: {e}")
                raise
        return self._embedding_model

    @property
    def nlp_pipeline(self) -> Optional:
        """Lazy load NLP pipeline"""
        if self._nlp_pipeline is None:
            from app.utils.config import settings
            try:
                logger.info(f"Loading NLP model: {settings.NLP_MODEL}")
                self._nlp_pipeline = pipeline(
                    "summarization",
                    model=settings.NLP_MODEL,
                    tokenizer=settings.NLP_MODEL
                )
                self._loaded_models.add(settings.NLP_MODEL)
                logger.info("NLP pipeline loaded successfully")
            except Exception as e:
                logger.error(f"Failed to load NLP model: {e}")
                raise
        return self._nlp_pipeline

    async def get_embedding(self, text: Union[str, List[str]]) -> Union[List[float], List[List[float]]]:
        """
        Generate embeddings for text

        Args:
            text: Single text or list of texts

        Returns:
            Embedding vector(s) as list of floats
        """
        if not SENTENCE_TRANSFORMERS_AVAILABLE:
            raise RuntimeError("sentence-transformers not available. Install required dependencies.")

        if not self.embedding_model:
            await self._load_embedding_model()

        try:
            if isinstance(text, str):
                embedding = self.embedding_model.encode(text, convert_to_tensor=False)
                return embedding.tolist()
            else:
                embeddings = self.embedding_model.encode(text, convert_to_tensor=False)
                return [emb.tolist() for emb in embeddings]
        except Exception as e:
            logger.error(f"Failed to generate embeddings: {e}")
            raise

    async def summarize_text(
        self,
        text: str,
        max_length: int = 150,
        min_length: int = 30,
        do_sample: bool = False
    ) -> str:
        """
        Summarize text using NLP model

        Args:
            text: Text to summarize
            max_length: Maximum summary length
            min_length: Minimum summary length
            do_sample: Whether to use sampling

        Returns:
            Summarized text
        """
        if not self.nlp_pipeline:
            raise RuntimeError("NLP pipeline not available")

        try:
            # Handle long texts by chunking
            if len(text) > 1024:
                text = text[:1024] + "..."

            result = self.nlp_pipeline(
                text,
                max_length=max_length,
                min_length=min_length,
                do_sample=do_sample,
                clean_up_tokenization_spaces=True
            )

            return result[0]['summary_text'] if result else text[:max_length]
        except Exception as e:
            logger.error(f"Failed to summarize text: {e}")
            # Fallback to simple truncation
            return text[:max_length] + "..."

    def get_loaded_models(self) -> List[str]:
        """Get list of loaded models"""
        return list(self._loaded_models)

    def unload_model(self, model_name: str) -> None:
        """Unload a model to free memory"""
        # Implementation depends on specific library unloading capabilities
        if model_name in self._loaded_models:
            self._loaded_models.remove(model_name)
            logger.info(f"Model {model_name} unloaded")

    async def health_check(self) -> dict:
        """Check health of loaded models"""
        health = {
            "embedding_model_available": self.embedding_model is not None,
            "nlp_pipeline_available": self.nlp_pipeline is not None,
            "loaded_models": self.get_loaded_models()
        }
        return health


# Global model manager instance
model_manager = AIModelManager()