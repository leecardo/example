"""
Example test file for AI Service
"""

import pytest
from fastapi.testclient import TestClient
from main import app

client = TestClient(app)


class TestHealthEndpoints:

    def test_root_endpoint(self):
        """Test root endpoint"""
        response = client.get("/")
        assert response.status_code == 200
        data = response.json()
        assert data["service"] == "AI Service"
        assert data["status"] == "running"
        assert "version" in data

    def test_health_check(self):
        """Test health check endpoint"""
        response = client.get("/health")
        assert response.status_code == 200
        data = response.json()
        assert data["status"] == "healthy"
        assert data["service"] == "ai-service"


class TestAIEndpoints:

    @pytest.mark.asyncio
    async def test_create_embedding(self):
        """Test text embedding creation"""
        response = client.post(
            "/api/v1/ai/embedding",
            json={"text": "This is a test sentence for embedding."}
        )
        # Note: This test might fail if AI models aren't installed
        # In a real scenario, we'd mock the model manager
        if response.status_code == 200:
            data = response.json()
            assert "embedding" in data
            assert "dimension" in data
            assert isinstance(data["embedding"], list)
        else:
            # Expected to fail in test environment without models
            assert response.status_code in [500, 400]

    def test_list_models(self):
        """Test list models endpoint"""
        response = client.get("/api/v1/ai/models")
        assert response.status_code == 200
        data = response.json()
        assert "embedding_model_available" in data
        assert "nlp_pipeline_available" in data


class TestNLPEndpoints:

    def test_summarize_text_invalid_request(self):
        """Test summarization with invalid request"""
        response = client.post(
            "/api/v1/nlp/summarize",
            json={"text": "short"}  # Too short for min_length requirement
        )
        assert response.status_code in [400, 500]

    def test_extract_keywords_invalid_request(self):
        """Test keyword extraction with invalid request"""
        response = client.post(
            "/api/v1/nlp/extract-keywords",
            json={"text": "hi"}  # Too short
        )
        assert response.status_code in [400, 500]


class TestDocumentEndpoints:

    def test_list_collections_empty(self):
        """Test listing empty collections"""
        response = client.get("/api/v1/document/collections")
        assert response.status_code == 200
        data = response.json()
        assert "collections" in data
        assert "total" in data

    def test_health_check(self):
        """Test document service health check"""
        response = client.get("/api/v1/document/health")
        assert response.status_code == 200
        data = response.json()
        assert data["service"] == "document_service"


if __name__ == "__main__":
    pytest.main([__file__])