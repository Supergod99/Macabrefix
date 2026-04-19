# Upstream Reference

Reference jar inspected only far enough to identify future bugfix targets. No upstream source was copied into this repository.

## Upstream Mod

- Name: Macabre
- Mod id: `macabre`
- Version: `0.8.4`
- Loader target: Forge `47+`, Minecraft `1.20.1`
- Author in metadata: `Pixelgeist666`
- License in metadata: Academic Free License v3.0
- Date added/inspected: 2026-04-19
- Jar path: `reference/upstream/macabre/macabre-0.8.4-forge-1.20.1.jar`

Correct jar filename on disk:

`reference/upstream/macabre/macabre-0.8.4-forge-1.20.1.jar`

## How Codex Should Use The Jar

- Treat the jar as reference-only.
- Inspect only packages/classes needed for the current bug, performance issue, or hook-point search.
- Record concrete package names, class names, method names, and behavior notes here.
- Do not copy large amounts of upstream code into Macabrefix.
- Prefer small original Forge/API/event-based fixes in this addon.
- Before recommending a Mixin, document why normal Forge/API hooks cannot safely handle the issue.

## Jar Shape

- Main package root: `com.curseforge.macabre`
- Approximate class distribution:
  - `procedures`: 679 classes
  - `block`: 455 classes
  - `entity`: 447 classes
  - `item`: 366 classes
  - `client`: 231 classes
  - `world`: 198 classes
  - `init`: 18 classes
  - `network`: 9 classes
- The jar appears MCreator-style: many small procedure classes, generated registries, generated capabilities/saved data, and generated client renderers.
- The jar includes `META-INF/accesstransformer.cfg`.

## Startup/Load Paths

| Class/File | Role | Notes |
| --- | --- | --- |
| `com.curseforge.macabre.MacabreMod` | Main `@Mod` entrypoint | Registers itself on `MinecraftForge.EVENT_BUS`, then registers deferred registries for sounds, blocks, block entities, items, entities, tabs, features, custom structure feature, mob effects, particles, and menus. |
| `com.curseforge.macabre.MacabreMod#tick(ServerTickEvent)` | Server work queue drain | Runs queued `Runnable`s during `TickEvent.ServerTickEvent` phase `END`. Uses a `ConcurrentLinkedQueue` and decrements integer delays each server tick. Watch for queued work buildup. |
| `com.curseforge.macabre.MacabreMod#PACKET_HANDLER` | SimpleChannel | Channel id `macabre:macabre`, protocol string `1`. |
| `com.curseforge.macabre.network.MacabreModVariables` | Capabilities/network setup | MOD-bus subscriber. Registers player capability plus saved-data/player-variable sync messages. |
| `MacabreModVariables.EventBusVariableHandlers` | Player/saved-data sync | Handles player login, respawn, changed dimension, and clone. Syncs map/world/player variables to clients. |
| `com.curseforge.macabre.init.MacabreModEntityRenderers` | Client renderer registration | CLIENT/MOD-bus subscriber registering many entity renderers. Relevant if dedicated-server client-only crashes appear. |
| `com.curseforge.macabre.procedures.LimboPlayerEntersDimensionProcedure` | Dimension special effects registration | CLIENT/MOD-bus subscriber for `RegisterDimensionSpecialEffectsEvent`; registers effects for `macabre:the_pit` and `macabre:limbo`. |
| `META-INF/accesstransformer.cfg` | Access transformer | Opens `ScatteredOreFeature` constructor and makes `TreeFeature.place` public/non-final. This may matter if worldgen crashes occur under other coremods. |

## Dimension And World Logic

| Class/File | Purpose | Notes |
| --- | --- | --- |
| `data/macabre/dimension/the_pit.json` | The Pit dimension | Noise generator with multi-noise biome source. Uses many Macabre biomes and overworld-like noise router sections. |
| `data/macabre/dimension/limbo.json` | Limbo dimension | Noise generator with single `macabre:voidfield` biome. Default block is `macabre:evaporating_voidturf`; default fluid is air. |
| `data/macabre/dimension_type/the_pit.json` | The Pit dimension type | `min_y=-64`, `height=384`, `logical_height=384`, skylight enabled, bed disabled, respawn anchors enabled, effects `macabre:the_pit`. |
| `data/macabre/dimension_type/limbo.json` | Limbo dimension type | `min_y=-64`, `height=384`, `logical_height=384`, skylight enabled, bed disabled, respawn anchors disabled, effects `macabre:limbo`. |
| `com.curseforge.macabre.world.dimension.ThePitDimension` | Dimension marker/effects class | Class has only a public constructor in inspected signature. Real dimension definition is data-driven. |
| `com.curseforge.macabre.world.dimension.LimboDimension` | Dimension marker/effects class | Class has only a public constructor in inspected signature. Real dimension definition is data-driven. |
| `com.curseforge.macabre.procedures.ThePitPlayerEntersDimensionProcedure` | Pit enter hook candidate | `execute()` is currently a no-op. This is suspicious because the matching leave procedure clears `ENTERPIT`. |
| `com.curseforge.macabre.procedures.ThePitPlayerLeavesDimensionProcedure` | Pit leave hook candidate | `execute(Entity)` sets player capability flag `ENTERPIT=false` and syncs player variables. |
| `com.curseforge.macabre.procedures.DimFallsProcedure` | Fall-damage suppression | Global `@Mod.EventBusSubscriber`; `onEntityFall(LivingFallEvent)` cancels/denies fall damage when player variable `fallreset` is true. |

## Worldgen And Structure Areas

| Class/File | Purpose | Risk Notes |
| --- | --- | --- |
| `com.curseforge.macabre.init.MacabreModFeatures` | Feature registry | Registers many configured/placed features through generated data. |
| `com.curseforge.macabre.world.features.StructureFeature` | Custom structure feature | Registered separately by `MacabreMod`. Configured features use type `macabre:structure_feature`. |
| `com.curseforge.macabre.world.features.configurations.StructureFeatureConfiguration` | Structure feature config | Used by `StructureFeature`. |
| `data/macabre/structures/*.nbt` | Structure templates | Includes boss spawner structures, dungeons, villages, trees, ruins, and terrain features. |
| `data/macabre/worldgen/configured_feature/big_bad_dungeon_1.json` | Dungeon structure feature | Uses `macabre:structure_feature`, structure `macabre:bigbaddungeon1`, offset `[0,-47,0]`. |
| `data/macabre/worldgen/configured_feature/pyramid_dungeon_1.json` | Dungeon structure feature | Uses structure `macabre:pyramidlabirynth`, offset `[0,-47,0]`. |
| `data/macabre/worldgen/configured_feature/tower_dungeon_1.json` | Dungeon structure feature | Uses structure `macabre:towerdungeon1`, offset `[0,-2,0]`. |
| `data/macabre/worldgen/configured_feature/baal_feature.json` | Boss spawner structure | Places `macabre:baalspawnerstr`, offset `[0,-5,0]`. |
| `data/macabre/worldgen/configured_feature/gargamaw_feature.json` | Boss spawner structure | Places `macabre:gargamawspawnerstr`, offset `[0,-5,0]`. |
| `data/macabre/worldgen/configured_feature/gomoria_feature.json` | Boss spawner structure | Places `macabre:gomoriaspawnerstr`, offset `[0,-5,0]`. |
| `data/macabre/worldgen/configured_feature/valamon_feature.json` | Boss spawner structure | Places `macabre:valamonspawnerstr`, offset `[0,-5,0]`. |
| `data/macabre/worldgen/configured_feature/morphegor_feature.json` | Boss altar/spawner structure | Places `macabre:morphgegoraltar1`, offset `[0,-5,0]`. |

### Locate-Incompatible Structure Placement - 2026-04-19

| Class/File | Finding | Fix Notes |
| --- | --- | --- |
| Jar data paths | The upstream jar contains `data/macabre/structures/*.nbt`, `data/macabre/worldgen/configured_feature/*.json`, `data/macabre/worldgen/placed_feature/*.json`, and `data/macabre/forge/biome_modifier/*.json`. It contains zero entries under `worldgen/structure`, `worldgen/structure_set`, `tags/worldgen/structure`, `worldgen/template_pool`, or `worldgen/processor_list`. | `/locate structure` and Explorer's Compass-style tools query real structure registry data, not arbitrary feature-placed templates. |
| `com.curseforge.macabre.world.features.StructureFeature` | Extends `Feature<StructureFeatureConfiguration>`, not vanilla `Structure`. Its place method loads a `StructureTemplate` from `StructureTemplateManager`, applies rotation/mirror/offset/ignored-block settings, and places the template directly. | This explains why generated landmarks can exist in-world without being registered as locateable structures. |
| `com.curseforge.macabre.world.features.configurations.StructureFeatureConfiguration` | Codec fields are `structure`, `random_rotation`, `random_mirror`, `ignored_blocks`, and `offset`. | Future conversion can reuse these values when creating real structure/template-pool data, but should not copy upstream code. |
| `com.curseforge.macabre.MacabreMod` | Registers both `MacabreModFeatures.REGISTRY` and `StructureFeature.REGISTRY` on the mod event bus. | The custom feature is a normal feature registry entry; no upstream structure type registration was observed. |
| `data/macabre/worldgen/configured_feature/big_bad_dungeon_1.json` | Uses `"type": "macabre:structure_feature"` and template `macabre:bigbaddungeon1`, with random rotation/mirror and offset `[0,-47,0]`. | Candidate first conversion target. |
| `data/macabre/worldgen/configured_feature/pyramid_dungeon_1.json` | Uses template `macabre:pyramidlabirynth`, offset `[0,-47,0]`. | Candidate first conversion target. |
| `data/macabre/worldgen/configured_feature/tower_dungeon_1.json` | Uses template `macabre:towerdungeon1`, offset `[0,-2,0]`. | Candidate first conversion target. |
| Boss landmark configured features | `baal_feature`, `gargamaw_feature`, `gomoria_feature`, `valamon_feature`, and `morphegor_feature` use `macabre:structure_feature` to place boss spawner/altar templates with offset `[0,-5,0]`. | Candidate second conversion group after dungeon validation. |
| Forge biome modifiers | Upstream adds these placed features with `forge:add_features` at generation step `surface_structures`; examples include `big_bad_dungeon_1_biome_modifier`, `pyramid_dungeon_1_biome_modifier`, `tower_dungeon_1_biome_modifier`, and boss feature biome modifiers. | If real structures are added, remove or suppress the matching upstream placed features for converted landmarks to avoid duplicate generation. Prefer normal Forge biome modifier data first. |

Normal data-driven worldgen resources are likely sufficient for future chunks if the goal is to make important Macabre landmarks locateable going forward. This does not retroactively index existing feature-placed templates in already generated chunks. Avoid converting all 80 `macabre:structure_feature` configured features at once; many are decorative trees, rocks, plants, or minor terrain templates and would make structure search noisy.

## Discovered Key Classes

| Class/Package | Purpose | Notes |
| --- | --- | --- |
| `com.curseforge.macabre.procedures` | Generated gameplay procedures | Most likely bug/performance source. Names map directly to event/tick/collision/item actions. |
| `com.curseforge.macabre.entity.*Entity` | Entities, bosses, projectiles, spawners | Many entities override tick (`m_8107_`) and call matching `*OnEntityTickUpdateProcedure` classes. |
| `com.curseforge.macabre.block.*Block` | Blocks and ticking/collision hooks | Several blocks override random/scheduled tick, collision, neighbor/on-place hooks and dispatch to procedure classes. |
| `com.curseforge.macabre.network.MacabreModVariables.PlayerVariables` | Player capability state | Important flags: `dim`, `fallreset`, `ENTERPIT`, `PITEFFECT`, `fallPrevent`, `blockFall`, boss armor flags, `grossbowSwitch`. |
| `com.curseforge.macabre.network.MacabreModVariables.MapVariables` | Saved world/global state | Important counters/flags: `hollow`, `hollowtentacles`, `holloweyes`, `baalNumber`, `whirlpoolSize`, `gomoriaNumber`, `veintreeSize`, `valamonNumber`, `gargamawNumber`, `morphegorNumber`, `voidParticleSize`. |
| `com.curseforge.macabre.procedures.BloodArmorAttributesProcedure` | Player tick event | Candidate for armor attribute/per-tick state bugs. |
| `com.curseforge.macabre.procedures.BlooodAttribuesProcedure` | Player tick event | Candidate for attribute duplication or repeated sync work. |
| `com.curseforge.macabre.procedures.BosstickresetProcedure` | Player tick event | Candidate for boss counter reset behavior. |
| `com.curseforge.macabre.procedures.MacabreEffectProcedure` | Player tick event | Candidate for per-tick effect cost or desync. |
| `com.curseforge.macabre.procedures.MacabreEffectTickProcedure` | Player tick event | Candidate for per-tick effect cost or desync. |
| `com.curseforge.macabre.procedures.MacabrePotionProcedure` | Player tick event | Candidate for repeated potion/effect logic. |
| `com.curseforge.macabre.procedures.Macabreeffect2Procedure` | Player tick event | Candidate for repeated potion/effect logic. |
| `com.curseforge.macabre.procedures.NumberAbilityProcedure` | Player tick event | Candidate for global variable/cooldown bugs. |
| `com.curseforge.macabre.procedures.SkinrayControlProcedure` | Player tick event | Candidate for mount/control desync. |
| `com.curseforge.macabre.procedures.TossingProcedure` | Player tick event | Candidate for motion/knockback/desync. |

## Performance-Risk Findings

| Area | Evidence | Risk | Follow-up |
| --- | --- | --- | --- |
| Boss entity tick procedures | `BaalOnEntityTickUpdateProcedure`, `GargamawOnEntityTickUpdateProcedure`, `ValamonOnEntityTickUpdateProcedure`, and `MorphegorOnEntityTickUpdateProcedure` use repeated AABB nearby-entity queries, stream sorting, entity damage/effects, particle spawning, and queued server work. | Per-boss tick cost can grow quickly with players/entities nearby. Risk of lag spikes and queued delayed work buildup. | First inspect behavior in a live server/client profile around boss fights or spawned bosses. Prefer event/entity cleanup hooks or throttling via addon events if a concrete lag bug is confirmed. |
| Spawner entity tick procedures | `BossSpawnerEntity`, `BaalSpawnerEntity`, `GomoriaSpawnerEntity`, `ValamonSpawnerEntity`, and `MorphegorSpawnerEntity` call matching tick procedures from entity tick. `BossSpawnerOnEntityTickUpdateProcedure` and `BossSpawner2OnEntityTickUpdateProcedure` scan for players in a 10-block AABB, discard the spawner, spawn `THE_HOLLOW_MAN`, and emit particles. | If spawners are duplicated, stuck, or chunk-loaded repeatedly, they can cause repeated scans/spawns. | Verify whether spawners persist after spawn and whether boss counters prevent duplicates. Likely normal hooks: `LivingTickEvent`, `EntityJoinLevelEvent`, or `EntityLeaveLevelEvent` filters for Macabre spawner entity types. |
| Generated structure procedures | `DunGenerationProcedure`/`VilGenerationProcedure` place many structure templates with random loops and repeated `StructureTemplateManager` lookups. `DunGenUpdateTickProcedure` runs a 97.5% clear-block path and otherwise calls `DunGenerationProcedure`. | If the trigger block ticks too often or remains in-world, structure generation may cause lag, cascading placement, or broken terrain. | Inspect which block calls these procedures and reproduce dungeon/village generation. Candidate fix is likely cleanup/throttle after generation using Forge block tick or chunk/worldgen events. |
| Tick/collision blocks | Blocks such as `TentacleSpawnBlock`, `GeyserOffBlock`, `GrinningFurnaceBlock`, `Ba4Block`, `ClosedBreatherBlock`, `EmptyWatcherBlock`, `EvaporatingVoidturfBlock`, `RottingDirtBlock`, `SlitOffBlock`, and multiple random-tick plant/terrain blocks dispatch procedure logic. | Scheduled/random tick behavior can be costly or repeat unexpectedly if procedures modify blocks and reschedule. | Inspect only the block tied to a reported issue. Prefer Forge block events or targeted state checks from Macabrefix. |
| Player tick subscribers | Several global player tick procedures run every player tick. | Multiplayer cost scales with player count; capability sync or attribute logic can desync or duplicate modifiers. | For player-state bugs, inspect the relevant procedure and player variables before changing anything. |
| Client overlays/effects | Many `*DisplayOverlayIngameProcedure` classes and custom sky/cloud/rain/star procedures exist. | Client-only render/event logic can spam work or crash dedicated servers if referenced from common code. | If a server crash mentions client classes, inspect Dist annotations and call paths first. |

### Boss Tick Sort Hotspot - 2026-04-19

| Class/File | Finding | Fix Notes |
| --- | --- | --- |
| `com.curseforge.macabre.procedures.BaalOnEntityTickUpdateProcedure#execute(LevelAccessor, double, double, double, Entity)` | Generated attack branches query nearby entities with an AABB, call `Stream#sorted(Comparator.comparingDouble(distance))`, call `toList`, then iterate all returned entities. | `src/main/java/com/doug/macabrefix/mixin/MacabreBossTickSortMixin.java` redirects only `Stream#sorted(Comparator)` to return the original stream. The same entities are still processed; only unused nearest-first ordering work is removed. |
| `com.curseforge.macabre.procedures.GargamawOnEntityTickUpdateProcedure#execute(LevelAccessor, double, double, double, Entity)` | Same generated nearby-entity sort pattern appears in attack branches that apply effects/damage to every listed entity. | Covered by the same redirect Mixin; no Gargamaw procedure body is copied. |
| `com.curseforge.macabre.procedures.ValamonOnEntityTickUpdateProcedure#execute(LevelAccessor, double, double, double, Entity)` | Same generated nearby-entity sort pattern appears in Valamon attack branches before iterating all entities. | Covered by the same redirect Mixin; projectile, damage, particle, and queued-work behavior is otherwise left to upstream. |
| `com.curseforge.macabre.procedures.MorphegorOnEntityTickUpdateProcedure#execute(LevelAccessor, double, double, double, Entity)` | Same generated nearby-entity sort pattern appears in directional attack branches before iterating all entities. | Covered by the same redirect Mixin; the fix avoids broad replacement of Morphegor's large generated procedure. |

Normal Forge event alternatives were evaluated and rejected for this hotspot because `LivingTickEvent` or later entity observation cannot prevent the upstream generated `execute(...)` method from already constructing and sorting the nearby-entity stream. A full replacement would copy too much generated boss logic. The redirect is the smallest source fix: it removes comparator/sort work while preserving the AABB query, entity set, damage/effect calls, particles, sounds, queued work, and attack randomization.

## Possible Crash/Log-Spam Sources

| Area | Evidence | Risk | Notes |
| --- | --- | --- | --- |
| Client-only generated classes | `MacabreModEntityRenderers` and `LimboPlayerEntersDimensionProcedure` are CLIENT/MOD-bus subscribers. Many `client.renderer`, `client.screens`, and overlay classes exist. | Dedicated-server crashes may happen if future code accidentally references these from common/server side. | Keep Macabrefix common code free of client class references unless guarded by DistExecutor/client-only packages. |
| Saved data sync | `EventBusVariableHandlers` sends saved-data sync messages on player login and changed dimension. | Desync or class-cast crash possible if events fire with non-server players or unexpected side. | Inspected bytecode checks client side before casts in the visible paths, but dimension-related bugs should still inspect this flow. |
| `MacabreMod.queueServerWork` | Delayed work queue drains at server tick END. | If procedures queue work every tick without bounded conditions, delayed runnables can accumulate. | Search for `MacabreMod.queueServerWork` in the specific procedure before fixing tick/performance bugs. |
| Access transformer | `TreeFeature.place` is made public/non-final and `ScatteredOreFeature` constructor is opened. | Worldgen/coremod conflicts can show up as load or transform errors. | Normal addon fixes probably cannot change AT behavior; document exact crash before considering invasive work. |
| Structure template generation | Many configured features and procedure-placed templates exist. | Missing/corrupt NBT, bad offsets, or repeated placement can cause generation failures or log spam. | Check latest.log for missing `macabre:*` structure ids when worldgen errors occur. |

## Candidate Fix Points

| Issue/Area | Candidate hook point | Why this is minimal | Status |
| --- | --- | --- | --- |
| The Pit enter/leave state may be inconsistent | `PlayerEvent.PlayerChangedDimensionEvent` in Macabrefix, checking from/to `macabre:the_pit` and player capability state | Normal Forge event can observe dimension transitions without patching Macabre. Useful because `ThePitPlayerEntersDimensionProcedure.execute()` is no-op while leave clears `ENTERPIT`. | Candidate first investigation target. |
| Fall damage suppression may stick | `LivingFallEvent`, checking Macabre player capability flags `fallreset`, `fallPrevent`, or `blockFall` | Same event Macabre already uses; addon can correct stuck flags or narrow cancellation if a concrete bug is reproduced. | Candidate after dimension tests. |
| Boss spawner duplicates or lag | `LivingEvent.LivingTickEvent`, `EntityJoinLevelEvent`, or `EntityLeaveLevelEvent` filtered to Macabre spawner entity registry ids | Normal Forge hooks can observe and rate-limit/cleanup addon-side if a bug is confirmed. | Candidate if boss spawn lag/duplication is reported. |
| Boss tick lag | `LivingTickEvent` filtered to boss entity ids plus local cooldown/state in Macabrefix | Less invasive than patching boss procedures; may be enough for cleanup or guard fixes. | Needs profiling/reproduction first. |
| Structure generation runaway | Block tick/chunk/world events around the specific trigger block, or cleanup after generated marker blocks | Normal Forge hooks can inspect block/entity state and prevent repeated triggering if exact trigger is confirmed. | Needs identification of trigger block from live bug. |
| Player tick attribute/effect duplication | `PlayerTickEvent`, `LivingEquipmentChangeEvent`, or capability checks | Normal hooks can remove duplicated modifiers or reset bad state if a specific issue is proven. | Needs reproduction. |
| Locate-incompatible Macabre landmarks | Data-driven structure resources plus targeted Forge biome modifier data to remove duplicate feature placements | Makes a small set of major landmarks visible to vanilla structure lookup without patching Macabre code. | Investigated; first implement dungeons only. |

## Attribute, Sync, Leaves, And Buff Investigation - 2026-04-19

### Attribute Base Resets

| Class/File | Finding | Fix Notes |
| --- | --- | --- |
| `com.curseforge.macabre.procedures.BlooodAttribuesProcedure` | Confirmed player tick subscriber. Runs during `TickEvent.PlayerTickEvent` phase `END` and calls `AttributeInstance#setBaseValue` (`m_22100_`) repeatedly. Affected attributes include Forge gravity, block/entity reach, swim speed, vanilla movement speed, attack damage, attack speed, knockback resistance, attack knockback, and max health. Max health is hard-set to `40.0`, `60.0`, or fallback `20.0` depending on Macabre armor capability flags. | `src/main/java/com/doug/macabrefix/mixin/MacabreAttributeProcedureMixin.java` now cancels `BlooodAttribuesProcedure#execute(Event, Entity)` before the hard base writes run and immediately invokes Macabrefix's modifier replacement at the same source hook. `src/main/java/com/doug/macabrefix/fixes/AttributeCompatibilityFix.java` preserves Macabre full-set bonus magnitudes as stable transient modifiers, skips fallback restoration on ticks where the source write was blocked, and still provides a Forge fallback if the optional Mixin is skipped against a changed upstream jar. |
| `com.curseforge.macabre.procedures.BloodArmorAttributesProcedure` | Confirms full-set armor booleans from vanilla armor slots only: `bloodarmor`, `baalArmor`, `gomoriaArmor`, `valamonArmor`, `gargamawArmor`, and `morphegorArmor`. Each true/false branch calls `PlayerVariables#syncPlayerVariables` even when the value did not change. | Move future Macabrefix logic toward transition-based state checks. If packet suppression is required, dirty-checking the upstream sync calls will likely need a narrow source-level intervention. |
| Macabre full-set base deltas | Confirmed values from `BlooodAttribuesProcedure`: Baal gravity `0.08 -> 0.04` and movement speed `0.1 -> 0.3`; Gomoria block reach `4.5 -> 8.0` and entity reach `3.0 -> 6.0`; Valamon attack knockback `0.0 -> 2.0` and attack speed `4.0 -> 8.0`; Gargamaw knockback resistance `0.0 -> 2.0` and max health `20.0 -> 60.0`; Morphegor swim speed `1.0 -> 7.0` and attack damage `1.0 -> 12.0`; Baal/Gomoria/Valamon max health is `20.0 -> 40.0`. | Macabrefix preserves the same bonus magnitudes as additive modifiers while leaving external base values intact. The Forge fallback can restore captured bases only if the source Mixin is skipped and Macabre writes one of its known hard-coded outputs. |

### Network Sync Sources

| Class/File | Packet/Sync Behavior | Fix Notes |
| --- | --- | --- |
| `MacabreModVariables$PlayerVariables#syncPlayerVariables` | Sends `MacabreModVariables$PlayerVariablesSyncMessage` to the affected `ServerPlayer`. The full player variable NBT includes all tracked booleans/doubles, not a single changed field. | Safe only when called on real changes. Current generated callers invoke it from player tick paths without dirty checks. |
| `MacabreModVariables$MapVariables#syncData` | Marks saved data dirty and sends `MacabreModVariables$SavedDataSyncMessage(type=0)` to `PacketDistributor.ALL`. | This is expensive when called from per-player tick logic because each player can cause an all-player broadcast. |
| `MacabreModVariables$WorldVariables#syncData` | Marks saved data dirty and sends `SavedDataSyncMessage(type=1)` to players in the dimension. `WorldVariables` currently serializes no fields. | Less directly implicated by the supplied packet table, but should not be called repeatedly without a real state change. |
| `com.curseforge.macabre.procedures.BosstickresetProcedure` | Runs on every player tick `END`; randomizes `MapVariables.hollow` and calls `MapVariables#syncData`. | `MacabreBossRandomSyncProcedureMixin` now cancels this generated procedure and delegates to `NetworkSyncThrottleFix`, which updates random selector fields once per logical tick without saved-data sync. |
| `com.curseforge.macabre.procedures.NumberAbilityProcedure` | Runs on every player tick `END`; randomizes `baalNumber`, `gomoriaNumber`, `valamonNumber`, `gargamawNumber`, and `morphegorNumber`, calling `MapVariables#syncData` after each field. | `MacabreBossRandomSyncProcedureMixin` now cancels this generated procedure and coalesces all boss selector regeneration into the same once-per-tick replacement without broadcasting transient selector data. |
| `com.curseforge.macabre.procedures.MacabreEffectTickProcedure` | Runs on every player tick `END`; sets `PlayerVariables.ENTERPIT` based on whether the player has Macabre's custom effect, then calls `syncPlayerVariables` every tick in both true and false branches. | `MacabreEffectSyncProcedureMixin` now preserves the same effect check but syncs player variables only when `ENTERPIT` changes. |
| `com.curseforge.macabre.procedures.MacabreEffectProcedure` | Large Pit-effect player tick procedure. Queues delayed server work and repeatedly updates `PlayerVariables.PITEFFECT` via lambdas that call `syncPlayerVariables`. | Investigate only if `PITEFFECT` remains noisy after the simpler confirmed tick sources are fixed. |

Normal Forge events can observe these ticks but cannot cancel packets already sent through Macabre's `SimpleChannel`. For the packet-count task, a narrowly targeted Mixin or equivalent source-level guard is likely required if real packet suppression is the goal.

### Network Sync Throttle - 2026-04-19

| Class/File | Finding | Fix Notes |
| --- | --- | --- |
| `com.curseforge.macabre.procedures.BloodArmorAttributesProcedure#execute(Event, Entity)` | Confirms six armor capability booleans from vanilla armor slots and calls `PlayerVariables#syncPlayerVariables` after each boolean branch every player tick. | `src/main/java/com/doug/macabrefix/mixin/MacabreArmorSyncProcedureMixin.java` cancels the generated method and calls `NetworkSyncThrottleFix.replaceArmorFlagSync`. The replacement preserves the same full-set checks and sends at most one player-variable sync, only when a flag changes. |
| `com.curseforge.macabre.procedures.MacabreEffectTickProcedure#execute(Event, Entity)` | Sets `ENTERPIT` from presence of Macabre's own `MACABRE` effect and syncs every tick. | `src/main/java/com/doug/macabrefix/mixin/MacabreEffectSyncProcedureMixin.java` preserves the effect check and syncs only on true/false transitions. |
| `com.curseforge.macabre.procedures.BosstickresetProcedure#execute(Event, LevelAccessor)` | Updates `MapVariables.hollow` every player tick, then broadcasts saved data. `hollow` is read by Hollow Man hurt logic as a random attack selector. | `src/main/java/com/doug/macabrefix/mixin/MacabreBossRandomSyncProcedureMixin.java` cancels the generated method. `NetworkSyncThrottleFix.replaceBossRandomSync` updates `hollow` once per logical tick without `syncData`, preserving the transient selector behavior without packet/save spam. |
| `com.curseforge.macabre.procedures.NumberAbilityProcedure#execute(Event, LevelAccessor)` | Updates five boss attack selector fields every player tick and broadcasts saved data after each field. Boss tick procedures read these fields to choose attacks. | The same boss-random Mixin cancels this generated method. The replacement updates all selector fields once per logical tick without calling `MapVariables#syncData`; no upstream boss procedure code is copied. |

Normal Forge event alternatives were evaluated and rejected for these four sources because the packet and saved-data sends originate inside upstream generated player tick subscribers. A Macabrefix listener could observe or repair state later, but it could not prevent `SimpleChannel.send` or `MapVariables#syncData` after those methods had already been called. These Mixins are the smallest source fix because they target only the confirmed noisy generated methods, keep the original state inputs, and avoid a broad network-layer packet filter.

### Tree Leaves And Structure Palettes

| Class/File | Finding | Fix Notes |
| --- | --- | --- |
| `com.curseforge.macabre.block.DriedLeavesBlock` | Extends `LeavesBlock`; random ticking exists through vanilla leaf behavior. | Can likely be repaired by clearing `PERSISTENT` and ensuring Macabre logs are recognized for distance checks. |
| `com.curseforge.macabre.block.TeethingLeavesBlock` | Extends `LeavesBlock`. | Same as dried leaves. |
| `com.curseforge.macabre.block.WastedLeavesBlock` | Extends `LeavesBlock`. | Same as dried leaves. |
| `com.curseforge.macabre.block.SoakedLeavesBlock` | Extends `Block` and implements `SimpleWaterloggedBlock`; it is not a `LeavesBlock` and has no vanilla leaf decay properties/logic. | Needs custom bounded decay behavior if tree cleanup should remove unsupported soaked leaves. |
| `com.curseforge.macabre.block.DecayingLeavesBlock` | Extends `FlowerBlock`; not a tree leaf in vanilla decay terms. It survives only on `macabre:living_dirt`. | Do not assume it can use vanilla leaf decay. |
| `data/minecraft/tags/blocks/leaves.json` in upstream jar | Contains only `macabre:dried_leaves`, `macabre:wasted_leaves`, and `macabre:teething_leaves`. | `soaked_leaves` is absent, and adding a tag alone would not give it `LeavesBlock` behavior. |
| Upstream structures | Decompressed NBT shows many tree structures store leaves with `persistent=true`, including `bigtree.nbt`, `gigatree.nbt`, `normaltree.nbt`, `smalltree.nbt`, `tinytree.nbt`, `ultratree*.nbt`, `desserttree*.nbt`, `opiumtree*.nbt`, and `wastedtree*.nbt`. Most soaked-tree structures use `macabre:soaked_leaves`. | Prefer a normal Forge `BlockEvent.BreakEvent` fix with bounded scans. Avoid copying/replacing upstream NBT structures. |
| `src/main/java/com/doug/macabrefix/fixes/LeafDecayFix.java` | Normal Forge `BlockEvent.BreakEvent` listener for Macabre log/wood blocks. Runs only when a relevant block is broken and scans a bounded nearby area. Follow-up soaked-tree screenshots showed that a small cube around the final broken log could miss side lobes or upper canopy pieces, especially when upper leaves were first checked while lower trunk logs still existed. | Clears `PERSISTENT` and schedules vanilla ticks for `DriedLeavesBlock`, `TeethingLeavesBlock`, and `WastedLeavesBlock`. For `soaked_leaves`, seeds from a vertically extended bounded area above the broken log, walks a capped connected soaked-leaf cluster, and removes only leaves with no remaining Macabre support log nearby. |
| `src/main/resources/data/minecraft/tags/blocks/logs.json` | Macabrefix merges Macabre log and wood blocks into `minecraft:logs`. | Needed so vanilla `LeavesBlock` distance checks can recognize Macabre trunks after generated leaves are converted back to non-persistent decay candidates. |

### Buffs, Effects, And Curios Compatibility

| Class/File | Finding | Fix Notes |
| --- | --- | --- |
| `com.curseforge.macabre.procedures.MacabrePotionProcedure` | Applies Macabre's custom `MACABRE` effect while the player is in `macabre:the_pit`; removes that same Macabre effect outside the Pit. No broad external-buff clearing was observed here. | Current Curios/Terramity report is more likely tied to attribute base overwrites unless reproduction proves a separate effect-removal path. |
| `com.curseforge.macabre.potion.MacabreMobEffect` | Custom neutral effect. `getCurativeItems()` returns an empty list; `isDurationEffectTick` always true. | This affects Macabre's own effect behavior but does not by itself explain removal of unrelated buffs. |
| Binary scan for effect removal | Found removal paths in `MacabrePotionProcedure` and several boss death procedures. No generic `removeAllEffects` path was found in the scanned Macabre classes. | Keep the future fix focused on attributes first. Do not add Curios dependency unless a focused reproduction proves slot/API integration is needed. |

### Terramity Curios Attribute Charm Findings

Terramity reference jar inspected from `D:\Downloads\terramity-0.9.8-forge-1.20.1.jar` for the reported Gold Medal and Ambrosia compatibility issue. This jar was not copied into the repository.

| Class/File | Finding | Fix Notes |
| --- | --- | --- |
| `net.mcreator.terramity.item.AmbrosiaItem` | Implements Curios `ICurioItem`. `onEquip(SlotContext, ItemStack, ItemStack)` calls `AmbrosiaBaubleIsEquippedProcedure.execute(entity)`, and `onUnequip(...)` calls `AmbrosiaBaubleIsUnequippedProcedure.execute(entity)`. Tooltip advertises `+8 Max Health`. | The bonus is applied from Curios callbacks; no Terramity dependency should be needed in Macabrefix if the generic attribute-base repair preserves the resulting attribute base. |
| `net.mcreator.terramity.procedures.AmbrosiaBaubleIsEquippedProcedure` | Directly reads `Attributes.MAX_HEALTH` base value and calls `AttributeInstance#setBaseValue(current + 8.0)`. | The source-level Macabre attribute Mixin should prevent Macabre's later max-health base reset from erasing this bonus. |
| `net.mcreator.terramity.procedures.AmbrosiaBaubleIsUnequippedProcedure` | Directly reads `Attributes.MAX_HEALTH` base value and calls `setBaseValue(current - 8.0)`. | Source-cancelling Macabre's reset avoids subtracting from an already-reset `20.0` base in normal equip/unequip flows. Macabrefix must also skip its same-tick fallback restore when the source Mixin ran, or it can restore the tick-start `28.0` base after Ambrosia correctly subtracts to `20.0`. |
| `net.mcreator.terramity.item.GoldMedalItem` | Implements Curios `ICurioItem`. `onEquip(...)` calls `GoldMedalBaubleIsEquippedProcedure.execute(entity)`, and `onUnequip(...)` calls `GoldMedalBaubleIsUnequippedProcedure.execute(entity)`. Tooltip advertises damage, damage reduction, `+4 Max Health`, and increased step height. | The max-health portion conflicts with Macabre's max-health base resets. Step height was not observed in Macabre's `BlooodAttribuesProcedure`, so avoid adding step height to a Macabre repair unless a separate Macabre reset is confirmed. |
| `net.mcreator.terramity.procedures.GoldMedalBaubleIsEquippedProcedure` | Directly calls `setBaseValue(current + 4.0)` for `Attributes.MAX_HEALTH` and `setBaseValue(current + 0.6)` for Forge `STEP_HEIGHT_ADDITION`. | Confirms the reported Gold Medal max-health failure shares the same base-reset root cause as Ambrosia. The source-level Macabre attribute Mixin targets the max-health conflict; step height was not observed in Macabre's attribute reset procedure. |
| `net.mcreator.terramity.procedures.GoldMedalBaubleIsUnequippedProcedure` | Directly subtracts `4.0` from max health base and `0.6` from Forge step-height base. | The known Macabre max-health overwrite is stopped at source, and Macabrefix now avoids restoring the pre-unequip `24.0` base on ticks where the source Mixin blocked Macabre. Manual validation with the built jar is still required. |
| `net.mcreator.terramity.init.TerramityModCuriosSlots` | Registers Curios slots through IMC: head `1`, necklace `1`, hands `1`, ring `2`, belt `1`, and charm `2`. | Normal Curios slot setup appears present; the failure is not likely a missing slot registration caused by Macabre. |
| `data/curios/tags/items/charm.json` | Includes `terramity:ambrosia` and many other Terramity charms. | Confirms Ambrosia is intended for a Curios charm slot. |
| `data/curios/tags/items/necklace.json` | Includes `terramity:gold_medal`. | Confirms Gold Medal is intended for a Curios necklace slot. |
| Targeted Terramity bytecode scan | Many `net.mcreator.terramity.procedures.*BaubleIs*Procedure` classes call `AttributeInstance#setBaseValue`, including procedures for Ambrosia, Gold Medal, Anti-Gravity Belt, Ball N Chain, Builder Mitts, milk cartons, Celestial Brew, Chthonic Nectar, diamond accessories, Dr. Binty, Electron Bracelets, Holy Heart Necklace, Honey Necklace, Leather Belt, Neutron Star, Nyx's Necklace, Purity Pendant, Radiant Honey, Sacred Speed Bracelets, Solar Safeguard Pendant, Spring, and Swordsmans Sarashi. Affected attributes include max health, gravity, movement speed, armor, armor toughness, attack speed, knockback resistance, and Forge step height. | Keep Macabrefix focused on attributes Macabre is confirmed to overwrite: max health, gravity, movement speed, reach, attack damage, attack speed, attack knockback, knockback resistance, and swim speed. Do not build a broad Terramity compatibility layer unless a separate issue proves it is needed. |

## Most Important Classes To Inspect Next

1. `com.curseforge.macabre.procedures.ThePitPlayerEntersDimensionProcedure`
2. `com.curseforge.macabre.procedures.ThePitPlayerLeavesDimensionProcedure`
3. `com.curseforge.macabre.procedures.DimFallsProcedure`
4. `com.curseforge.macabre.network.MacabreModVariables$PlayerVariables`
5. `com.curseforge.macabre.network.MacabreModVariables$EventBusVariableHandlers`
6. `com.curseforge.macabre.procedures.BossSpawnerOnEntityTickUpdateProcedure`
7. `com.curseforge.macabre.procedures.BossSpawner2OnEntityTickUpdateProcedure`
8. `com.curseforge.macabre.procedures.BaalOnEntityTickUpdateProcedure`
9. `com.curseforge.macabre.procedures.GargamawOnEntityTickUpdateProcedure`
10. `com.curseforge.macabre.procedures.ValamonOnEntityTickUpdateProcedure`
11. `com.curseforge.macabre.procedures.MorphegorOnEntityTickUpdateProcedure`
12. `com.curseforge.macabre.procedures.DunGenUpdateTickProcedure`
13. `com.curseforge.macabre.procedures.DunGenerationProcedure`
14. `com.curseforge.macabre.procedures.VilGenUpdateTickProcedure`
15. `com.curseforge.macabre.procedures.VilGenerationProcedure`

## Mixin Justification Notes

Mixins have been added for the attribute reset issue, the network sync throttle issue, and the boss tick sort hotspot.

Normal Forge hooks still seem sufficient for dimension, fall-damage, entity observation, and tree leaf cleanup candidates. The 2026-04-19 sync investigation is different: the excessive packets are sent inside upstream generated procedures and `MacabreModVariables` sync methods, so normal Forge events can observe the problem but cannot suppress already-sent packets.

For the network sync throttle issue, `MacabreArmorSyncProcedureMixin` targets `BloodArmorAttributesProcedure#execute(Event, Entity)`, `MacabreEffectSyncProcedureMixin` targets `MacabreEffectTickProcedure#execute(Event, Entity)`, and `MacabreBossRandomSyncProcedureMixin` targets both `BosstickresetProcedure#execute(Event, LevelAccessor)` and `NumberAbilityProcedure#execute(Event, LevelAccessor)`. Normal hooks were insufficient because the upstream procedures send packets or saved-data broadcasts before any later observer can stop them. Macabrefix preserves intended Macabre behavior by keeping the same armor slot checks, the same Macabre effect check, and the same boss/Hollow random selector ranges, while removing redundant per-tick sync calls and avoiding saved-data broadcasts for transient selector values.

For the attribute reset issue, `AttributeCompatibilityFix` implemented the normal Forge post-tick repair first. The Terramity Curios investigation exposed edge cases where allowing Macabre's hard base writes to happen first is still risky: equip/unequip timing, health flicker, repeated attribute packets, and subtracting external bonuses from an already-reset base. A later re-check also found that a pure post-tick replacement leaves a small event-order window after the cancelled upstream subscriber and before Macabrefix's lowest-priority tick listener. `MacabreAttributeProcedureMixin` is therefore narrowly targeted at `BlooodAttribuesProcedure#execute(Event, Entity)`, cancels only that generated hard-base attribute procedure, and immediately applies Macabre's intended armor bonuses as transient modifiers at the same source hook. The normal Forge listener remains as the fallback and end-of-tick cleanup path.

For the boss tick sort hotspot, `MacabreBossTickSortMixin` targets `Stream#sorted(Comparator)` calls inside `BaalOnEntityTickUpdateProcedure#execute(LevelAccessor, double, double, double, Entity)`, `GargamawOnEntityTickUpdateProcedure#execute(...)`, `ValamonOnEntityTickUpdateProcedure#execute(...)`, and `MorphegorOnEntityTickUpdateProcedure#execute(...)`. Normal hooks were insufficient because they cannot stop the upstream generated method from sorting the nearby-entity stream after the boss tick starts. Macabrefix preserves intended behavior by keeping the original AABB query and applying the original boss procedure effects to the same entity set, only without unused nearest-first ordering.

For locate-incompatible Macabre landmarks, no Mixin is justified for the initial implementation. Use standard data-driven structure registration and Forge biome modifier data first. A Mixin should only be reconsidered if the project later decides it must make already-generated feature-placed templates appear in locate indexes, which is outside the minimal first fix.
