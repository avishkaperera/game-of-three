# Game of Three Server
- Java version 21 with Spring Boot
- Websocket was used as the communication medium 
- Events are published to the client based on change in game state hence the system is based on event-driven 
architecture (pub/sub)
- An effort was made to adhere to DDD principles
- An effort was made to implement the server using Hexagonal/ Clean architecture
- To keep the implementation complexity to a minimum,  no database was used instead an in-memory data store was used 
to persist games
- Integration tests could have been added given more time
- Client should be a separate project but for the purpose of this challenge it's included in the same project