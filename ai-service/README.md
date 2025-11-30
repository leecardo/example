# AI Service

Python-based AI and NLP microservice for the example project, designed to work alongside the existing Java Spring Boot application.

## Features

- **Text Embedding**: Generate high-quality embeddings using sentence-transformers
- **Text Summarization**: AI-powered text summarization with configurable length
- **Keyword Extraction**: Extract relevant keywords from text content
- **Document Processing**: Process PDF, DOCX, and TXT files with chunking
- **Vector Storage**: Integration with ChromaDB for similarity search
- **RAG Support**: Document search and retrieval capabilities

## Architecture

```
ai-service/
├── app/
│   ├── api/v1/          # API endpoints
│   ├── models/          # AI model management
│   ├── services/        # Business logic
│   └── utils/           # Utilities (config, database, logging)
├── config/              # Configuration files
├── tests/               # Unit and integration tests
├── scripts/             # Helper scripts
├── requirements.txt     # Python dependencies
├── Dockerfile          # Container configuration
└── main.py             # Application entry point
```

## API Endpoints

### AI Models
- `POST /api/v1/ai/embedding` - Create text embedding
- `POST /api/v1/ai/embedding/batch` - Create batch embeddings
- `GET /api/v1/ai/models` - List loaded models

### NLP Processing
- `POST /api/v1/nlp/summarize` - Summarize text
- `POST /api/v1/nlp/extract-keywords` - Extract keywords
- `POST /api/v1/nlp/summarize-file` - Summarize uploaded file

### Document Processing
- `POST /api/v1/document/upload` - Upload and process document
- `POST /api/v1/document/search` - Search similar documents
- `GET /api/v1/document/collections` - ChromaDB collections

## Quick Start

### Local Development

1. **Install Dependencies**
   ```bash
   cd ai-service
   pip install -r requirements.txt
   ```

2. **Configure Environment**
   ```bash
   cp .env.example .env
   # Edit .env with your configuration
   ```

3. **Start Services**
   ```bash
   # Start ChromaDB
   chroma run --host localhost --port 8000

   # Start AI Service (in another terminal)
   python main.py
   ```

4. **Test the Service**
   ```bash
   curl http://localhost:8081/health
   ```

### Docker Deployment

1. **Build and Run**
   ```bash
   docker build -t ai-service .
   docker run -p 8081:8081 --env-file .env ai-service
   ```

2. **Or use Docker Compose**
   ```bash
   docker-compose up
   ```

## Configuration

The service shares database and infrastructure configuration with the Java application:

- **Database**: MariaDB (same instance as Java app)
- **Cache**: Redis (shared cache layer)
- **Vector DB**: ChromaDB for embeddings storage
- **Message Queue**: RabbitMQ for async processing

## Integration with Java

### Feign Client Example

```java
@FeignClient(name = "ai-service", url = "${ai.service.url:http://localhost:8081}")
public interface AIServiceClient {

    @PostMapping("/api/v1/ai/embedding")
    EmbeddingResponse createEmbedding(@RequestBody EmbeddingRequest request);

    @PostMapping("/api/v1/nlp/summarize")
    SummaryResponse summarizeText(@RequestBody SummaryRequest request);
}
```

### Service Integration

```java
@Service
public class EnhancedAIService {

    @Autowired
    private AIServiceClient aiServiceClient;

    public List<String> processWithPython(String text) {
        EmbeddingRequest request = new EmbeddingRequest(text);
        EmbeddingResponse response = aiServiceClient.createEmbedding(request);
        return response.getEmbedding();
    }
}
```

## Development

### Running Tests
```bash
pytest tests/
```

### Code Formatting
```bash
black .
isort .
```

### Type Checking
```bash
mypy app/
```

## Production Considerations

- **Model Loading**: Models are loaded on-demand to optimize memory usage
- **Async Processing**: All endpoints are fully async for better performance
- **Error Handling**: Comprehensive error logging and responses
- **Health Checks**: Multiple health endpoints for monitoring
- **CORS Config**: Configured for integration with Java application

## Monitoring

- **Health Endpoints**: `/health`, `/api/v1/*/health`
- **Service Stats**: `/api/v1/nlp/stats`
- **Structured Logging**: JSON format for log aggregation
- **Metrics**: Optional metrics collection available

## Dependencies

Key dependencies include:
- **FastAPI**: Modern async web framework
- **Transformers**: Hugging Face transformers for NLP
- **Sentence-Transformers**: High-quality embeddings
- **ChromaDB**: Vector database for similarity search
- **PyMySQL**: MySQL connectivity
- **Redis**: Caching and session storage

## License

This service is part of the example project and follows the same licensing terms.