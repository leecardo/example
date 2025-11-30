"""
Document processing and analysis services
"""

import logging
import asyncio
import tempfile
import os
from typing import List, Dict, Any, Optional
from dataclasses import dataclass

from app.services.nlp_service import nlp_service
from app.utils.database import get_chroma_client

logger = logging.getLogger(__name__)

try:
    # Optional document processing libraries
    import PyPDF2
    import docx
    PYPDF2_AVAILABLE = True
    DOCX_AVAILABLE = True
except ImportError:
    logger.warning("Document processing libraries not available. Install with: pip install PyPDF2 python-docx")
    PYPDF2_AVAILABLE = False
    DOCX_AVAILABLE = False


@dataclass
class DocumentChunk:
    """Represents a chunk of processed document"""
    content: str
    metadata: Dict[str, Any]
    embedding: Optional[List[float]] = None


class DocumentService:
    """Service for document processing and analysis"""

    def __init__(self):
        self.nlp_service = nlp_service
        self.chroma_client = None

    async def process_document_file(self, file_path: str, chunk_size: int = 500, overlap: int = 50) -> List[DocumentChunk]:
        """
        Process a document file and split into chunks

        Args:
            file_path: Path to the document file
            chunk_size: Size of each text chunk
            overlap: Overlap between chunks

        Returns:
            List of document chunks
        """
        logger.info(f"Processing document: {file_path}")

        # Extract text based on file type
        text = await self._extract_text_from_file(file_path)

        if not text.strip():
            raise ValueError("No text extracted from document")

        # Split text into chunks
        chunks = await self._split_text_into_chunks(text, chunk_size, overlap)

        logger.info(f"Document processed into {len(chunks)} chunks")
        return chunks

    async def _extract_text_from_file(self, file_path: str) -> str:
        """Extract text from different file types"""
        file_ext = os.path.splitext(file_path)[1].lower()

        if file_ext == '.txt':
            return await self._extract_from_txt(file_path)
        elif file_ext == '.pdf':
            return await self._extract_from_pdf(file_path)
        elif file_ext in ['.doc', '.docx']:
            return await self._extract_from_docx(file_path)
        else:
            raise ValueError(f"Unsupported file type: {file_ext}")

    async def _extract_from_txt(self, file_path: str) -> str:
        """Extract text from TXT file"""
        try:
            with open(file_path, 'r', encoding='utf-8') as file:
                return file.read()
        except Exception as e:
            logger.error(f"Error reading TXT file {file_path}: {e}")
            raise

    async def _extract_from_pdf(self, file_path: str) -> str:
        """Extract text from PDF file"""
        if not PYPDF2_AVAILABLE:
            raise RuntimeError("PyPDF2 not available")

        text = ""
        try:
            with open(file_path, 'rb') as file:
                pdf_reader = PyPDF2.PdfReader(file)
                for page_num in range(len(pdf_reader.pages)):
                    page = pdf_reader.pages[page_num]
                    text += page.extract_text() + "\n"
        except Exception as e:
            logger.error(f"Error reading PDF file {file_path}: {e}")
            raise

        return text

    async def _extract_from_docx(self, file_path: str) -> str:
        """Extract text from DOCX file"""
        if not DOCX_AVAILABLE:
            raise RuntimeError("python-docx not available")

        try:
            doc = docx.Document(file_path)
            text = ""
            for paragraph in doc.paragraphs:
                text += paragraph.text + "\n"
        except Exception as e:
            logger.error(f"Error reading DOCX file {file_path}: {e}")
            raise

        return text

    async def _split_text_into_chunks(
        self,
        text: str,
        chunk_size: int = 500,
        overlap: int = 50
    ) -> List[DocumentChunk]:
        """Split text into overlapping chunks"""
        words = text.split()
        chunks = []

        start = 0
        while start < len(words):
            end = start + chunk_size
            chunk_words = words[start:end]
            chunk_text = " ".join(chunk_words)

            metadata = {
                "start_index": start,
                "end_index": end,
                "word_count": len(chunk_words),
                "char_count": len(chunk_text)
            }

            chunk = DocumentChunk(content=chunk_text, metadata=metadata)
            chunks.append(chunk)

            if end >= len(words):
                break

            start = end - overlap

        return chunks

    async def generate_embeddings_for_chunks(self, chunks: List[DocumentChunk]) -> List[DocumentChunk]:
        """Generate embeddings for document chunks"""
        if not chunks:
            return []

        logger.info(f"Generating embeddings for {len(chunks)} chunks")

        texts = [chunk.content for chunk in chunks]
        embeddings = await self.nlp_service.create_embeddings(texts)

        # Attach embeddings to chunks
        for i, chunk in enumerate(chunks):
            chunk.embedding = embeddings[i]

        logger.info("Embeddings generated for all chunks")
        return chunks

    async def summarize_document(self, chunks: List[DocumentChunk], max_summary_length: int = 300) -> str:
        """
        Generate a summary of the document by summarizing chunks

        Args:
            chunks: Document chunks
            max_summary_length: Maximum length of final summary

        Returns:
            Document summary
        """
        if not chunks:
            return ""

        logger.info(f"Summarizing document from {len(chunks)} chunks")

        # Generate summaries for each chunk
        chunk_texts = [chunk.content for chunk in chunks]
        chunk_summaries = await self.nlp_service.batch_summarize(
            chunk_texts,
            max_length=150,
            min_length=30
        )

        # Combine chunk summaries
        combined_summary = " ".join(chunk_summaries)

        # Summarize the combined summary if it's too long
        if len(combined_summary) > max_summary_length:
            final_summary = await self.nlp_service.summarize_text(
                combined_summary,
                max_length=max_summary_length,
                min_length=100
            )
            return final_summary
        else:
            return combined_summary

    async def store_in_chromadb(
        self,
        chunks: List[DocumentChunk],
        collection_name: str = "documents"
    ) -> List[str]:
        """
        Store document chunks in ChromaDB

        Args:
            chunks: Document chunks with embeddings
            collection_name: Name of ChromaDB collection

        Returns:
            List of document IDs
        """
        if not chunks or not all(chunk.embedding for chunk in chunks):
            raise ValueError("All chunks must have embeddings")

        try:
            chroma_client = get_chroma_client()
            collection = chroma_client.get_or_create_collection(name=collection_name)

            documents = [chunk.content for chunk in chunks]
            embeddings = [chunk.embedding for chunk in chunks]
            metadatas = [chunk.metadata for chunk in chunks]
            ids = [f"doc_{i}_{hash(chunk.content) % 1000000}" for i, chunk in enumerate(chunks)]

            collection.add(
                documents=documents,
                embeddings=embeddings,
                metadatas=metadatas,
                ids=ids
            )

            logger.info(f"Stored {len(chunks)} chunks in ChromaDB collection: {collection_name}")
            return ids

        except Exception as e:
            logger.error(f"Failed to store in ChromaDB: {e}")
            raise

    async def search_similar_documents(
        self,
        query: str,
        collection_name: str = "documents",
        top_results: int = 5
    ) -> List[Dict[str, Any]]:
        """
        Search for similar documents in ChromaDB

        Args:
            query: Search query
            collection_name: Name of ChromaDB collection
            top_results: Number of results to return

        Returns:
            List of similar documents with metadata
        """
        try:
            # Generate query embedding
            query_embedding = await self.nlp_service.create_embedding(query)

            # Search in ChromaDB
            chroma_client = get_chroma_client()
            collection = chroma_client.get_collection(name=collection_name)

            results = collection.query(
                query_embeddings=[query_embedding],
                n_results=top_results
            )

            # Format results
            formatted_results = []
            for i in range(len(results['ids'][0])):
                formatted_results.append({
                    'id': results['ids'][0][i],
                    'document': results['documents'][0][i],
                    'metadata': results['metadatas'][0][i],
                    'distance': results['distances'][0][i]
                })

            logger.info(f"Found {len(formatted_results)} similar documents")
            return formatted_results

        except Exception as e:
            logger.error(f"Failed to search ChromaDB: {e}")
            raise

    async def process_and_store_document(
        self,
        file_path: str,
        collection_name: str = "documents",
        chunk_size: int = 500,
        overlap: int = 50
    ) -> Dict[str, Any]:
        """
        Complete workflow: process document and store in ChromaDB

        Args:
            file_path: Path to document file
            collection_name: ChromaDB collection name
            chunk_size: Chunk size for text splitting
            overlap: Overlap between chunks

        Returns:
            Processing results with document IDs and summary
        """
        # Process document
        chunks = await self.process_document_file(file_path, chunk_size, overlap)

        # Generate embeddings
        chunks_with_embeddings = await self.generate_embeddings_for_chunks(chunks)

        # Store in ChromaDB
        document_ids = await self.store_in_chromadb(chunks_with_embeddings, collection_name)

        # Generate summary
        summary = await self.summarize_document(chunks_with_embeddings)

        return {
            'document_path': file_path,
            'chunk_count': len(chunks),
            'document_ids': document_ids,
            'summary': summary,
            'collection_name': collection_name
        }

    async def health_check(self) -> Dict[str, Any]:
        """Check health of document service"""
        capabilities = ["text_chunking", "document_summarization"]
        if PYPDF2_AVAILABLE:
            capabilities.append("pdf_processing")
        if DOCX_AVAILABLE:
            capabilities.append("docx_processing")

        return {
            "service": "document_service",
            "status": "healthy",
            "capabilities": capabilities,
            "pdf_support": PYPDF2_AVAILABLE,
            "docx_support": DOCX_AVAILABLE
        }


# Global service instance
document_service = DocumentService()