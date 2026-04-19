# File Tree

This file records the current repository structure for quick orientation.

Update this file after any structural changes. Generated ignored directories such as `.gradle/`, `build/`, and `run/` are not part of this snapshot.

```text
.
+---docs
|   +---.gitkeep
|   +---BLUEPRINT.md
|   +---CODEX_GUIDE.md
|   +---FILE_TREE.md
|   +---TASKS.md
|   +---TEST_PLAN.md
|   \---UPSTREAM_REFERENCE.md
+---gradle
|   \---wrapper
|       +---gradle-wrapper.jar
|       \---gradle-wrapper.properties
+---reference
|   \---upstream
|       \---macabre
|           +---.gitkeep
|           \---macabre-0.8.4-forge-1.20.1.jar
+---src
|   \---main
|       +---java
|       |   \---com
|       |       \---doug
|       |           \---macabrefix
|       |               +---compat
|       |               |   \---MacabreCompat.java
|       |               +---fixes
|       |               |   +---AttributeCompatibilityFix.java
|       |               |   +---FixRegistrar.java
|       |               |   +---LeafDecayFix.java
|       |               |   \---NetworkSyncThrottleFix.java
|       |               +---mixin
|       |               |   +---MacabreArmorSyncProcedureMixin.java
|       |               |   +---MacabreAttributeProcedureMixin.java
|       |               |   +---MacabreBossRandomSyncProcedureMixin.java
|       |               |   +---MacabreBossTickSortMixin.java
|       |               |   \---MacabreEffectSyncProcedureMixin.java
|       |               +---util
|       |               |   \---Constants.java
|       |               \---MacabreFix.java
|       \---resources
|           +---assets
|           |   \---macabrefix
|           |       \---lang
|           |           \---en_us.json
|           +---data
|           |   \---minecraft
|           |       \---tags
|           |           \---blocks
|           |               \---logs.json
|           +---META-INF
|           |   \---mods.toml
|           +---macabrefix.mixins.json
|           \---pack.mcmeta
+---.gitignore
+---build.gradle
+---gradle.properties
+---gradlew
+---gradlew.bat
+---README.md
\---settings.gradle
```
