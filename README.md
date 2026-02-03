# Mariash

A modular, extensible card game engine for **Mariash** (Czech trick-taking card game) with a deterministic core and multiple UI clients.

Mariash is a traditional Czech card game for 3 players using a 32-card deck. It features bidding, trump selection, and various contract types including Game, Seven, Hundred, Misère, and Slam.

The project is designed around a clear separation between:
- a **Kotlin-based engine and server**
- a **React-based web UI**
- a **Redux-style state model** enabling reproducible state transitions and easy debugging

---

## Key Features

- **Deterministic game engine**
  - Redux-style state container
  - Pure state transitions driven by actions
  - Suitable for replay, debugging, and testing

- **Authentic Mariash rules**
  - Two-phase dealing with trump card selection
  - Bidding ladder with 12 contract levels
  - Doubling system (double/redouble/raise/final raise)
  - See [docs/RULES.md](docs/RULES.md) for complete rules

- **Multi-client UI approach**
  - Web UI implemented in React
  - Android client planned (WebView or native UI)

- **Kotlin-first backend**
  - Strong domain modeling
  - Test-friendly architecture
  - Natural fit for Android

---

## Architecture Overview

The engine acts as the single source of truth.
All clients render state and interact with the engine by dispatching actions.

---

## Project Structure

```
.
├── engine/                 # Game engine (Kotlin)
├── server/                 # REST API server (Ktor)
├── ui/web/                 # React web UI
├── docs/
│   ├── adr/                # Architectural Decision Records
│   ├── API.md              # REST API reference
│   ├── api-tests.http      # API test examples
│   ├── RULES.md            # Mariash game rules
│   └── VOCABULARY.md       # Czech-English terminology
├── work/
│   ├── features/           # Feature definitions
│   └── tasks/              # Implementation tasks
├── build.gradle.kts
└── settings.gradle.kts
```

---

## Documentation

- [Game Rules](docs/RULES.md) - Complete Mariash rules in English
- [Vocabulary](docs/VOCABULARY.md) - Czech-English terminology glossary
- [REST API](docs/API.md) - Server API reference

---

## Running the Server

The server is started using **Gradle**.

From the project root, run:

```bash
./gradlew :server:run
```

Server starts on http://localhost:8080

## Running Web UI

The dev UI runs in **Node.js** and is built with **Vite**.

```bash
cd ui/web
npm install
cp .env.example .env
npm run dev
```

or

```bash
./gradlew :ui-web:dev
```

---

## Game Types

| Type | Description |
|------|-------------|
| Game | Basic contract - declarer needs >50 points |
| Seven | Win last trick with trump 7 |
| Hundred | Declarer needs 100+ points |
| Misère | Declarer must not win any trick |
| Slam | Declarer must win all tricks |

See [VOCABULARY.md](docs/VOCABULARY.md) for complete terminology.
