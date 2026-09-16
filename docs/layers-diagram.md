# Layer Dependency Diagram — GameZone Unicesar

```mermaid
flowchart TD
    UI["ui"]
    SERVICE["service"]
    PERSISTENCE["persistence"]
    MODEL["model"]

    UI --> SERVICE
    SERVICE --> PERSISTENCE
    SERVICE --> MODEL
    PERSISTENCE --> MODEL
```