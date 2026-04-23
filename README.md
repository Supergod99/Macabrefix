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
```


