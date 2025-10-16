# Memory App

## Memory Flow

```mermaid
sequenceDiagram
    participant Client
    participant APIGateway
    participant MemoryHandler
    participant DynamoDB

    Client->>APIGateway: Request to get memory
    APIGateway->>MemoryHandler: Forward get request
    MemoryHandler->>DynamoDB: Query memory
    DynamoDB-->>MemoryHandler: Return memory
    MemoryHandler-->>APIGateway: Send memory
    APIGateway-->>Client: Respond with memory

    Client->>APIGateway: Request to save memory
    APIGateway->>MemoryHandler: Forward save request
    MemoryHandler->>DynamoDB: Save memory
    DynamoDB-->>MemoryHandler: Confirmation
    MemoryHandler-->>APIGateway: Success response
    APIGateway-->>Client: Acknowledge save
