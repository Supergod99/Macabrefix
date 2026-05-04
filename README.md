# Macabrefix

Macabrefix is a small Minecraft Forge 1.20.1 addon/fix mod for Macabre.

This project is not a rerelease or fork of the full Macabre mod. It requires the original Macabre mod and is meant to fix bugs, unintended features, performance issues, and server issues as minimally as possible.

Current status: compatibility and performance fixes are implemented, with the mod building successfully through Gradle.

## Implemented Fixes And Optimizations

Macabrefix currently includes these targeted fixes and performance reductions:

- Preserves external attribute and Curios-style stat bonuses by stopping Macabre's generated full-set armor procedure from hard-resetting player attribute base values every tick. Macabre armor bonuses are kept as Macabrefix-owned transient modifiers instead.
- Reduces Macabre player-variable and saved-data network spam by dirty-checking armor and Pit state syncs, and by avoiding saved-data broadcasts for transient boss and Hollow random selector values.
- Restores Macabre tree leaf decay by making Macabre logs count as vanilla logs and by triggering bounded decay support when Macabre trunks are broken, including custom handling for `macabre:soaked_leaves`.
- Removes unused nearest-first sorting work from Baal, Gargamaw, Valamon, and Morphegor boss tick attack branches while preserving the same nearby-entity set and original damage/effect behavior.
- Prevents hidden dungeon and village trigger blocks from launching expensive random-tick template generation. The trigger blocks are still cleaned up at the source position.
- Reduces Hollow Man auto-spawner tick cost and duplicate activations by replacing the generated nearby-player entity-list scan with a server-player proximity check and suppressing duplicate Hollow Man spawns in the same encounter area.
- Bounds Pit scare overlay queued work by replacing nested delayed `PITEFFECT` sync chains with one active sequence runner, dirty-checked syncs, and stale-sequence cancellation.
- Adds safe fallback sounds for Macabre eye entities when an upstream sound registry lookup would otherwise return `null` and crash.
- Simplifies the reported heavy Doombloom and Gloomdoom block models to lightweight cross models while keeping their block behavior and textures.
- Reduces Valley of Eyes eye mob pressure by removing and re-adding the four eye-specific biome spawn entries at lower weight.
- Reduces future Pit random-tick terrain seed density by replacing Mold Stone, Molten Stone, and Waste Grass placed-feature density from upstream count `10` to Macabrefix count `2`.
- Reduces future dense Valley of Eyes eye-structure generation by replacing `macabre:eye_8_feature` placement rarity from chance `33` to chance `264`.
- Reduces Bloodweed worldgen density through resource overrides, changing upstream configured-feature tries from `345` to `32`, placed-feature count from `40` to `20`, and rarity chance from `32` to `64`.
- Adds common-config toggles for the major Java-side fixes so they can fall through to original Macabre behavior when disabled.

The current build also contains merged evaluation behavior from an earlier performance pass:

- A Forge event handler caps nearby spawns for a fixed list of Macabre entities, disables AI/gravity and resets motion for that same list on join, cancels in-wall damage for them, cancels duplicate nearby `macabre:whirlpool` spawns, and globally mutes `macabre:whispers`.
- A full `data/macabre/dimension/the_pit.json` resource override is present. This is broader than the preferred Macabrefix source-fix style and is documented as follow-up review material.

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


