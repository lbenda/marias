# Marias

# Card Game Engine

A modular, extensible card game engine with a deterministic core and multiple UI clients.

The project is designed around a clear separation between:
- a **Kotlin-based engine and server**
- a **React-based web UI**
- a **Redux-style state model** enabling reproducible state transitions and easy debugging

The architecture allows adding new card games (with different rules and decks) without rewriting the core engine.

---

## Key Features

- **Deterministic game engine**
  - Redux-style state container
  - Pure state transitions driven by actions
  - Suitable for replay, debugging, and testing

- **Extensible card game architecture**
  - Support for multiple card games
  - Pluggable rules, decks, and win conditions
  - Core mechanics shared across games

- **Multi-client UI approach**
  - Web UI implemented in React
  - Android client planned (WebView or native UI – TBD)

- **Kotlin-first backend**
  - Strong domain modeling
  - Test-friendly architecture
  - Natural fit for Android

---

## Architecture Overview

The engine acts as the single source of truth.  
All clients render state and interact with the engine by dispatching actions.

---

## Project Structure (high level)
```
.
├── engine/ # Game engine
├── server/ # Kotlin server
├── ui/web/ # React web UI
├── docs/
│ ├── adr/ # Architectural Decision Records
│ ├── api-tests.http # test examples
│ └── API.md # API description
├── work/ 
│ └── tickets/ # Tickets descibe part of works
├── build.gradle.kts
└── settings.gradle.kts
```

---

## Running the Server

The server is started using **Gradle**.

From the project root, run:

```bash
./gradlew :server:run
```

## Running web ui

The DEV ui is running in **NodeJS** and build with **vita**..js

```bash
cd ui/web
npm install
cp .env.example .env
npm run dev
```
