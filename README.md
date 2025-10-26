# JP Fetcher

**JP Fetcher** is a simple application designed to fetch posts from [JSONPlaceholder](https://jsonplaceholder.typicode.com/) and save them to disk.  
The process is triggered via a REST API call.

---

## Architecture Overview

The project follows a **Component-Based Hexagonal Architecture**

### Core Principles

1. **Isolation of Business Logic**
    - The core integration layer contains only domain logic.
    - Adapters handle all infrastructure-related concerns (I/O, persistence, APIs, etc.).

2. **Dependency Inversion**
    - Components depend on **SPIs** rather than concrete implementations.

3. **Component Independence**
    - Each component is **self-contained**, allowing for independent replacement or modification without affecting others.

---

## Package Rules

To maintain architectural integrity and clean boundaries between components, follow these rules:

1. **Public Exposure**
    - Only the following should be public:
        - `ComponentConfig`
        - `SPI`
        - `API`

2. **Encapsulation**
    - All other classes (internal logic) must be **package-private**.

3. **Dependency Restrictions**
    - **Integration** layers **cannot depend** on other components.
    - **Adapters** may depend **only** on `SPI` or `API` interfaces.

