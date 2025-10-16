# Memory App

## Memory Components

```mermaid
graph TD
Client[Client Application] -->|HTTP Request| APIGateway[API Gateway]
APIGateway -->|Calls| MemoryHandler[Memory Handler]
MemoryHandler -->|Reads/Writes| DynamoDB[DynamoDB]

    style Client fill:#a0c4ff,stroke:#000,stroke-width:1px,color:#000
    style APIGateway fill:#bdb2ff,stroke:#000,stroke-width:1px,color:#000
    style MemoryHandler fill:#caffbf,stroke:#000,stroke-width:1px,color:#000
    style DynamoDB fill:#ffc6ff,stroke:#000,stroke-width:1px,color:#000
```

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
```
