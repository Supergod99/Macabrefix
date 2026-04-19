# Macabrefix

Macabrefix is a small Minecraft Forge 1.20.1 addon/fix mod for Macabre.

This project is not a rerelease or fork of the full Macabre mod. It requires the original Macabre mod and is meant to fix bugs, unintended features, performance issues, and server issues as minimally as possible.

Current status: compatibility and performance fixes are implemented, with the mod building successfully through Gradle.

## Build And Run

Run the client:

```powershell
.\gradlew runClient
```

Build the mod:

```powershell
.\gradlew build
```

The built mod jar is written to `build/libs/`.

GitHub also builds the jar on every push to `main`. Open the latest Build workflow run and download the `macabrefix-jar` artifact.

On Unix-like shells:

```bash
./gradlew runClient
./gradlew build
```

## Repo Layout

```text
src/main/java/com/doug/macabrefix/       Minimal Forge mod stubs
src/main/resources/                      Forge metadata and mod assets
docs/                                    Project guides and working notes
reference/upstream/macabre/              Local upstream Macabre reference notes and placeholders
```

Upstream reference jars belong in `reference/upstream/macabre/` for local inspection only and are ignored by Git. Treat anything in that folder as reference-only.

## Docs

- [Codex Guide](docs/CODEX_GUIDE.md)
- [Blueprint](docs/BLUEPRINT.md)
- [Upstream Reference](docs/UPSTREAM_REFERENCE.md)
- [File Tree](docs/FILE_TREE.md)
- [Tasks](docs/TASKS.md)
- [Test Plan](docs/TEST_PLAN.md)
