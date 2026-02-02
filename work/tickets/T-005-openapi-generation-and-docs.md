# T-005: OpenAPI generation and API documentation

Status: Done
Owner: Server

## Goal
Establish a code-first OpenAPI workflow for the server module and provide
clear, executable API documentation for developers and clients.

## Context
The server exposes a REST API over the engine.
During early development, the API is expected to evolve, so a strict
contract-first OpenAPI approach would slow down iteration.

Instead, the project follows a code-first strategy:
- API shape is defined by Kotlin controllers
- OpenAPI is generated from code
- Human-readable documentation lives alongside the code

## Scope
IN:
- Code-first OpenAPI generation from server controllers
- docs/API.md as the authoritative, human-readable API reference
- api-test.http as executable API usage examples

OUT:
- Client SDK generation
- Strict backward-compatibility guarantees
- API versioning beyond initial setup

## Result
- OpenAPI specification is generated from server code
- docs/API.md documents:
    - available endpoints
    - request/response payloads
    - basic behavior expectations
- api-test.http contains working example requests that can be executed manually

## Notes
- docs/API.md and api-test.http are treated as part of the API contract.
- Any API change must update both files.
- OpenAPI may become the primary contract once the API stabilizes or gains external consumers.

## Verification
- Generated OpenAPI spec matches server endpoints
- API described in docs/API.md matches actual behavior
- Requests in api-test.http execute successfully against a running server

## Follow-ups (not part of this ticket)
- Enforce OpenAPI generation and validation in CI
- Introduce API versioning rules
- Generate client SDKs if needed
