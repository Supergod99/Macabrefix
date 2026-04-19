# Tasks

Use this as a short task board. Mark a phase complete only after the expected stop point is satisfied.

- [x] Scaffold workspace
  - Stop point: Gradle wrapper, Forge 1.20.1 metadata, Java stubs, resources, and docs exist.

- [x] Confirm client build
  - Stop point: `.\gradlew build` succeeds.

- [x] Place/reference Macabre jar under `reference/upstream/macabre/`
  - Stop point: `macabre-0.8.4-forge-1.20.1.jar` is present under `reference/upstream/macabre/`.

- [x] Inspect Macabre jar or source
  - Stop point: initial jar metadata, package shape, dimension/world classes, startup paths, tick procedures, saved variables, and candidate hook points are recorded in `docs/UPSTREAM_REFERENCE.md`.

- [x] Document systems and likely bug hotspots
  - Stop point: startup/load, dimension/world, entity/ticking, server, crash/log-spam, and performance-risk candidates are summarized.
  - Investigation note: first likely target is The Pit dimension state because `ThePitPlayerEntersDimensionProcedure.execute()` is a no-op while the leave procedure clears `ENTERPIT`.
  - Investigation note: boss/spawner tick procedures and template-generation procedures are the clearest performance-risk areas so far.

- [x] Choose one minimal fix target
  - Stop point: one concrete issue, expected behavior, actual behavior, and safest hook point are documented.

- [x] Implement the minimal fix
  - Stop point: only the files needed for that one fix are changed.

- [x] Compile/build
  - Stop point: `.\gradlew build` succeeds.

- [ ] Run client test
  - Stop point: client loads with Macabre and Macabrefix, and the target behavior is manually checked.

- [ ] Run server test if applicable
  - Stop point: dedicated server starts, accepts a client, and the target behavior is checked when server-side code is affected.

- [ ] Regression review
  - Stop point: no new crashes, spam logging, lag, broken teleports, broken worldgen, broken mob/structure behavior, or unrelated behavior changes are observed.

## Prioritized Bug And Performance Backlog

Use this queue to choose the next focused fix. Performance candidates are ordered from highest likely server/gameplay impact to lowest, based only on findings already recorded in `docs/UPSTREAM_REFERENCE.md`. Each task should still be reproduced or profiled before implementation.

### Performance-Ordered Fix Queue

1. [x] `MAC-PERF-BOSS-TICK-HOTSPOTS` - Profile and reduce boss entity tick hotspots.
   - Impact ranking: Highest likely open performance impact because boss procedures run from entity tick and combine repeated nearby-entity AABB queries, stream sorting, damage/effect work, particle spawning, and queued server work.
   - Upstream evidence: `BaalOnEntityTickUpdateProcedure`, `GargamawOnEntityTickUpdateProcedure`, `ValamonOnEntityTickUpdateProcedure`, and `MorphegorOnEntityTickUpdateProcedure`.
   - Stop point: one confirmed boss hotspot is reproduced or profiled, then fixed with the smallest hook/source change that preserves intended boss behavior.
   - First approach: profile live boss fights or spawned bosses; evaluate normal `LivingTickEvent`/entity cleanup hooks first; use a Mixin only if a confirmed upstream tick method must be stopped before it performs the expensive work.
   - Implementation note: static bytecode inspection confirmed the generated procedures sort every nearby-entity stream by distance before materializing the list, even though the affected branches iterate all entries. `MacabreBossTickSortMixin` now removes only that unused sort step.

2. [ ] `MAC-PERF-STRUCTURE-GEN-RUNAWAY` - Investigate dungeon/village template generation cost.
   - Impact ranking: Very high if a trigger block remains active, because generation procedures can place many templates, run random loops, and repeat `StructureTemplateManager` lookups.
   - Upstream evidence: `DunGenerationProcedure`, `VilGenerationProcedure`, `DunGenUpdateTickProcedure`, and `VilGenUpdateTickProcedure`.
   - Stop point: identify the exact trigger block/procedure path and prevent repeated or cascading generation without copying upstream template-placement code.
   - First approach: inspect the triggering block and reproduce generation; prefer Forge block tick/chunk/world hooks or targeted cleanup/throttling before considering source intervention.

3. [ ] `MAC-PERF-SPAWNER-DUPLICATES` - Verify boss spawner duplicate spawn/scan behavior.
   - Impact ranking: High when duplicated or chunk-loaded spawners repeatedly scan nearby players, emit particles, discard themselves, and spawn boss entities.
   - Upstream evidence: `BossSpawnerEntity`, `BaalSpawnerEntity`, `GomoriaSpawnerEntity`, `ValamonSpawnerEntity`, `MorphegorSpawnerEntity`, `BossSpawnerOnEntityTickUpdateProcedure`, and `BossSpawner2OnEntityTickUpdateProcedure`.
   - Stop point: confirm whether spawners persist, duplicate, or bypass boss counters, then add the narrowest cleanup/rate-limit fix.
   - First approach: evaluate `EntityJoinLevelEvent`, `EntityLeaveLevelEvent`, and `LivingTickEvent` filters for Macabre spawner entity ids.

4. [ ] `MAC-PERF-PITEFFECT-QUEUE-SYNC` - Investigate remaining Pit effect queued work and `PITEFFECT` sync churn.
   - Impact ranking: Medium-high because it runs from player tick, can scale with player count, and uses delayed server work plus player variable sync paths.
   - Upstream evidence: `MacabreEffectProcedure` queues delayed server work and updates `PlayerVariables.PITEFFECT` through lambdas that call `syncPlayerVariables`.
   - Stop point: determine whether `PITEFFECT` remains noisy after the implemented sync throttle and fix only the confirmed source of repeated queue/sync work.
   - First approach: inspect packet/log behavior around entering, staying in, and leaving The Pit; prefer dirty checks or source guarding only for confirmed repeated writes.

5. [ ] `MAC-PERF-PLAYER-TICK-REMAINDERS` - Audit remaining global player tick procedures.
   - Impact ranking: Medium because cost scales with player count, but the largest confirmed packet/attribute sources are already implemented.
   - Upstream evidence: `MacabreEffectProcedure`, `MacabrePotionProcedure`, `Macabreeffect2Procedure`, `SkinrayControlProcedure`, `TossingProcedure`, and nearby player tick procedures listed in the upstream reference.
   - Stop point: choose one reproduced player-state bug or measurable player-tick cost and fix only that source.
   - First approach: inspect the relevant procedure and player variables before changing anything; prefer Forge player/equipment/effect events when they cleanly prevent the issue.

6. [ ] `MAC-PERF-TICK-COLLISION-BLOCKS` - Investigate costly ticking/collision blocks only when a concrete block issue is reported.
   - Impact ranking: Medium-low because block ticking can become expensive in bulk, but no single offending block is confirmed yet.
   - Upstream evidence: `TentacleSpawnBlock`, `GeyserOffBlock`, `GrinningFurnaceBlock`, `Ba4Block`, `ClosedBreatherBlock`, `EmptyWatcherBlock`, `EvaporatingVoidturfBlock`, `RottingDirtBlock`, `SlitOffBlock`, and random-tick plant/terrain blocks.
   - Stop point: reproduce one block-specific lag or runaway behavior and add a bounded, local fix.
   - First approach: inspect only the block tied to the report; avoid broad scans and avoid changing unrelated blocks.

7. [ ] `MAC-PERF-CLIENT-OVERLAYS-EFFECTS` - Investigate client-only overlay/sky/effect cost or crash reports.
   - Impact ranking: Lowest current performance priority because risk appears client-side or report-dependent, not confirmed server tick load.
   - Upstream evidence: `*DisplayOverlayIngameProcedure` classes and custom sky/cloud/rain/star procedures.
   - Stop point: confirm a client render crash, log spam source, or measurable frame-time issue, then fix only that path.
   - First approach: keep common/server code free of client class references and use client-only guards/packages for any future client fix.

### Lower-Performance Bug And Compatibility Queue

1. [ ] `MAC-DIM-PIT-STATE` - Verify The Pit enter/leave capability state.
   - Upstream evidence: `ThePitPlayerEntersDimensionProcedure.execute()` is a no-op while `ThePitPlayerLeavesDimensionProcedure.execute(Entity)` clears `ENTERPIT`.
   - Stop point: confirm whether `ENTERPIT` or related Pit state becomes inconsistent across dimension changes and fix with `PlayerEvent.PlayerChangedDimensionEvent` if it cleanly corrects the root cause.

2. [ ] `MAC-FALL-DAMAGE-FLAGS` - Verify stuck fall-damage suppression flags.
   - Upstream evidence: `DimFallsProcedure` cancels/denies fall damage when player variable `fallreset` is true; related player variables include `fallPrevent` and `blockFall`.
   - Stop point: reproduce a stuck suppression or unexpected fall-damage case and fix through the same `LivingFallEvent` surface if possible.

3. [ ] `MAC-STRUCTURE-LOCATE` - Register major Macabre structures as real structures.
   - Upstream evidence: Macabre places important templates through `macabre:structure_feature`, not vanilla `Structure` registry data, so `/locate structure` and Explorer's Compass-style tools cannot find them.
   - Stop point: implement a small first data-driven set, likely the major dungeons, while avoiding duplicate generation and avoiding decorative/noisy structure entries.

4. [ ] `MAC-CLIENT-CLASSLOADING` - Investigate dedicated-server client class loading crashes if reported.
   - Upstream evidence: Macabre has many generated client renderers, screens, overlays, and client-only dimension effect subscribers.
   - Stop point: confirm an actual server crash path and guard or relocate only the Macabrefix-side reference that triggers it.

5. [ ] `MAC-WORLDGEN-STRUCTURE-ERRORS` - Investigate missing/corrupt template or worldgen log spam.
   - Upstream evidence: many configured features and procedure-placed templates load `macabre:*` structures by id, sometimes with large offsets.
   - Stop point: identify the exact missing/corrupt template id or placement path from logs and fix only that resource/data issue.

## Investigated Macabre Issues - 2026-04-19

### MAC-ATTR-BASE-RESET - Preserve Player Attribute Bases

- Issue ID / short task name: `MAC-ATTR-BASE-RESET` / Preserve player attribute bases from other mods.
- Symptom: Mods that increase player health can appear to be reverted to default HP. The same risk applies to other attributes, especially movement speed, reach, gravity, swim speed, attack damage, attack speed, knockback resistance, and max health.
- Root cause summary: Confirmed. `BlooodAttribuesProcedure` runs on `TickEvent.PlayerTickEvent` phase `END` and calls `AttributeInstance#setBaseValue` every player tick. When the relevant Macabre armor flags are absent, it resets several attributes to hard-coded vanilla-like values, including max health back to `20.0`.
- Evidence / upstream location: `com.curseforge.macabre.procedures.BlooodAttribuesProcedure#onPlayerTick` and private `execute(...)` in `reference/upstream/macabre/macabre-0.8.4-forge-1.20.1.jar`. A binary scan found `BlooodAttribuesProcedure` as the only upstream class using obfuscated `AttributeInstance.m_22100_`, which is `setBaseValue`.
- Implementation: Added `src/main/java/com/doug/macabrefix/fixes/AttributeCompatibilityFix.java` to reapply Macabre armor full-set bonuses as stable transient additive modifiers owned by Macabrefix. Later hardening added `src/main/java/com/doug/macabrefix/mixin/MacabreAttributeProcedureMixin.java`, which cancels Macabre's `BlooodAttribuesProcedure` before its hard `setBaseValue` writes run. Re-investigation moved the modifier replacement into the cancelled source hook as well, so later same-tick listeners do not wait until Macabrefix's lowest-priority tick listener to see the intended Macabre armor bonuses. The Mixin still marks player ticks where the source write was blocked, so the Forge fallback restore does not undo legitimate same-tick external attribute changes such as Terramity Curios unequip callbacks. The Forge tick repair remains registered as a fallback/compatibility layer if the optional Mixin is skipped against a changed upstream jar.
- Files modified: `build.gradle`, `src/main/java/com/doug/macabrefix/MacabreFix.java`, `src/main/java/com/doug/macabrefix/fixes/FixRegistrar.java`, `src/main/java/com/doug/macabrefix/fixes/AttributeCompatibilityFix.java`, `src/main/java/com/doug/macabrefix/mixin/MacabreAttributeProcedureMixin.java`, `src/main/resources/macabrefix.mixins.json`, `docs/TASKS.md`, `docs/UPSTREAM_REFERENCE.md`, and `docs/FILE_TREE.md`.
- Risk notes: Source-cancelling `BlooodAttribuesProcedure` avoids fighting other mods every tick and avoids repeated attribute base packets from Macabre's hard resets. The fallback restore must only run when Macabre may actually have written the hard-coded base value; otherwise it can preserve a pre-unequip base value from the start of the tick and undo a legitimate Curios removal. The replacement modifier logic must continue preserving Macabre's intended armor bonus magnitudes. Tests still need login, respawn, dimension change, equipment-change, and external Curios charm coverage.
- Mixin assessment: Mixin implemented and narrowly targeted. The normal Forge repair was enough for common post-tick correction, but it still allowed Macabre's bad writes to happen first and left a small event-order gap before the replacement modifiers were applied. Cancelling only `BlooodAttribuesProcedure#execute(Event, Entity)` and applying the replacement modifiers there is the smallest source-level fix for edge cases around equip/unequip timing, health flicker, attribute packet churn, and same-tick attribute readers.
- Status: IMPLEMENTED; `.\gradlew compileJava` and `.\gradlew build` succeed.

### MAC-NET-SYNC-THROTTLE - Stop Per-Tick Variable Sync Spam

- Issue ID / short task name: `MAC-NET-SYNC-THROTTLE` / Throttle Macabre variable sync packets.
- Symptom: Packet captures show excessive Macabre traffic that scales with player count, especially `PlayerVariablesSyncMessage` and `SavedDataSyncMessage`.
- Root cause summary: Confirmed. Multiple generated player tick procedures mutate or sync Macabre variables every player tick without dirty checks. `BloodArmorAttributesProcedure` syncs full player variables for each armor flag branch every tick. `MacabreEffectTickProcedure` syncs `ENTERPIT` every tick. `BosstickresetProcedure` randomizes `MapVariables.hollow` and broadcasts saved data every tick. `NumberAbilityProcedure` randomizes five boss-number fields and broadcasts saved data after each field every tick.
- Evidence / upstream location: `MacabreModVariables$PlayerVariables#syncPlayerVariables` sends `PlayerVariablesSyncMessage` to the player. `MacabreModVariables$MapVariables#syncData` sends `SavedDataSyncMessage(type=0)` to `PacketDistributor.ALL`. `BloodArmorAttributesProcedure`, `MacabreEffectTickProcedure`, `BosstickresetProcedure`, and `NumberAbilityProcedure` call these paths from `TickEvent.PlayerTickEvent` phase `END`.
- Minimal implementation plan: First target the confirmed per-tick sources. Replace tick-based syncing with dirty-checked syncing on actual state transitions for armor flags and `ENTERPIT`, and coalesce boss/Hollow random selector regeneration without saved-data broadcasts. Keep packet suppression localized to these generated procedures or their sync calls; leave `PITEFFECT` for a separate focused investigation if it remains noisy after these confirmed sources are fixed.
- Implementation: Added `src/main/java/com/doug/macabrefix/fixes/NetworkSyncThrottleFix.java` plus narrow Mixins for `BloodArmorAttributesProcedure`, `MacabreEffectTickProcedure`, `BosstickresetProcedure`, and `NumberAbilityProcedure`. Armor flags and `ENTERPIT` are still computed from Macabre's original conditions, but `PlayerVariables#syncPlayerVariables` now runs only when those values actually change. Boss/Hollow random selector values are still regenerated once per logical tick from the player-tick source, but the replacement does not call `MapVariables#syncData` because those selector fields are transient server/client tick inputs and were the confirmed saved-data broadcast spam source.
- Files modified: `src/main/java/com/doug/macabrefix/fixes/NetworkSyncThrottleFix.java`, `src/main/java/com/doug/macabrefix/mixin/MacabreArmorSyncProcedureMixin.java`, `src/main/java/com/doug/macabrefix/mixin/MacabreBossRandomSyncProcedureMixin.java`, `src/main/java/com/doug/macabrefix/mixin/MacabreEffectSyncProcedureMixin.java`, `src/main/resources/macabrefix.mixins.json`, `docs/TASKS.md`, `docs/UPSTREAM_REFERENCE.md`, and `docs/FILE_TREE.md`.
- Risk notes: Normal Forge events can observe player ticks but cannot cancel packets that Macabre already sends through its `SimpleChannel`. A post-event fix alone will not reduce packet count. Dirty checks must not break client overlays or boss logic that expects variables to update.
- Mixin assessment: Mixin implemented and narrowly targeted. Normal Forge events were insufficient because they run alongside or after Macabre's generated player tick subscribers and cannot cancel `SimpleChannel.send` calls or `MapVariables#syncData` after the upstream procedure has already invoked them. The Mixins cancel only the four confirmed generated procedures and replace only their noisy sync side effects while preserving the original armor/effect/random selector state calculations.
- Status: IMPLEMENTED; `.\gradlew compileJava` and `.\gradlew build` succeed.

### MAC-TREE-LEAF-DECAY - Make Macabre Tree Leaves Decay

- Issue ID / short task name: `MAC-TREE-LEAF-DECAY` / Restore leaf decay after Macabre trees are chopped.
- Symptom: Breaking a Macabre tree trunk can leave the generated leaves behind instead of letting them decay. Follow-up testing with soaked trees showed side lobes and upper canopy pieces can remain even after all logs in the tested tree are removed.
- Root cause summary: Confirmed for common generated trees. Many upstream structure NBT palettes store Macabre leaves with `persistent=true`, which prevents vanilla `LeavesBlock` decay. Additionally, `SoakedLeavesBlock` extends `Block` with `SimpleWaterloggedBlock`, not `LeavesBlock`, so the many trees using `macabre:soaked_leaves` have no vanilla leaf-decay behavior at all. The upstream `data/minecraft/tags/blocks/leaves.json` includes only `dried_leaves`, `wasted_leaves`, and `teething_leaves`; no upstream logs tag for Macabre logs was found.
- Evidence / upstream location: `com.curseforge.macabre.block.DriedLeavesBlock`, `TeethingLeavesBlock`, and `WastedLeavesBlock` extend `LeavesBlock`; `SoakedLeavesBlock` extends `Block`; `DecayingLeavesBlock` extends `FlowerBlock`. Decompressed structures such as `bigtree.nbt`, `gigatree.nbt`, `normaltree.nbt`, `smalltree.nbt`, `tinytree.nbt`, `ultratree*.nbt`, `desserttree*.nbt`, `opiumtree*.nbt`, and `wastedtree*.nbt` include leaf palettes with `persistent=true`.
- Minimal implementation plan: Prefer a normal Forge/API fix. On `BlockEvent.BreakEvent` for Macabre log/stem/trunk blocks, run a bounded nearby scan for Macabre leaf blocks. For real `LeavesBlock` states, set `PERSISTENT=false` and schedule a tick so vanilla decay can proceed. For `soaked_leaves`, implement a small bounded custom decay check that removes unsupported soaked leaves only when no nearby Macabre trunk/log remains. Add resource tags for Macabre logs/leaves only if needed for distance calculations; do not copy upstream NBT structures.
- Implementation: Added `src/main/java/com/doug/macabrefix/fixes/LeafDecayFix.java` and registered it from `FixRegistrar`. The fix listens to Forge `BlockEvent.BreakEvent` at low priority for Macabre log/wood blocks, then runs a bounded local scan. For Macabre leaves that already extend `LeavesBlock`, it clears `LeavesBlock.PERSISTENT` and schedules a vanilla tick so vanilla distance/decay logic can proceed. For `macabre:soaked_leaves`, which has no vanilla leaf properties, it now seeds from a vertically extended area above the broken log and walks a capped connected soaked-leaf cluster. This covers the common chop order where upper leaves are first considered supported by lower trunk logs, then the final lower log break is too far below the canopy for an 8-block cube seed. Each soaked leaf is removed only when no remaining Macabre support log exists nearby. Added `src/main/resources/data/minecraft/tags/blocks/logs.json` so vanilla leaf distance checks recognize Macabre log/wood blocks.
- Files modified: `src/main/java/com/doug/macabrefix/fixes/LeafDecayFix.java`, `src/main/java/com/doug/macabrefix/fixes/FixRegistrar.java`, `src/main/resources/data/minecraft/tags/blocks/logs.json`, `docs/TASKS.md`, `docs/UPSTREAM_REFERENCE.md`, and `docs/FILE_TREE.md`.
- Risk notes: The scan must be bounded to avoid chopping one tree causing large chunk scans. It must not remove decorative leaves in structures or player builds while a supporting Macabre log remains nearby. Soaked leaves need special handling because vanilla `LeavesBlock` properties are not available on that block class. The soaked follow-up is bounded by horizontal/vertical seed limits, connected-cluster limits, and a maximum leaves-per-break cap.
- Mixin assessment: Mixin not used. Normal Forge block break events and scheduled ticks are sufficient because the fix can correct persistent generated leaves at the moment the supporting Macabre logs are chopped, without patching upstream structure placement or copying upstream NBT.
- Status: IMPLEMENTED; `.\gradlew compileJava` and `.\gradlew build` succeed.

### MAC-PERF-BOSS-TICK-HOTSPOTS - Remove Unused Boss Nearby-Entity Sorting

- Issue ID / short task name: `MAC-PERF-BOSS-TICK-HOTSPOTS` / Reduce generated boss tick stream sorting.
- Symptom: Boss attack ticks can spend avoidable time and allocations on nearby-entity AABB query results before applying damage/effects/particles.
- Root cause summary: Confirmed by upstream bytecode inspection. `BaalOnEntityTickUpdateProcedure`, `GargamawOnEntityTickUpdateProcedure`, `ValamonOnEntityTickUpdateProcedure`, and `MorphegorOnEntityTickUpdateProcedure` call `LevelAccessor#getEntitiesOfClass`, convert the result to a stream, sort by distance, materialize with `toList`, and then iterate the whole list. The distance sort is not used to choose a nearest target in the inspected branches.
- Evidence / upstream location: `com.curseforge.macabre.procedures.BaalOnEntityTickUpdateProcedure#execute(LevelAccessor, double, double, double, Entity)`, `GargamawOnEntityTickUpdateProcedure#execute(...)`, `ValamonOnEntityTickUpdateProcedure#execute(...)`, and `MorphegorOnEntityTickUpdateProcedure#execute(...)` in `reference/upstream/macabre/macabre-0.8.4-forge-1.20.1.jar`.
- Minimal implementation plan: Avoid changing boss AI, attack randomization, damage, particles, queued work, or AABB bounds. Use a narrow source Mixin to redirect only `Stream#sorted(Comparator)` in those generated procedures and return the original stream.
- Implementation: Added `src/main/java/com/doug/macabrefix/mixin/MacabreBossTickSortMixin.java` and registered it in `src/main/resources/macabrefix.mixins.json`. The Mixin leaves the original query and per-entity iteration in place while removing comparator creation/sort work from the confirmed hotspot.
- Files modified: `src/main/java/com/doug/macabrefix/mixin/MacabreBossTickSortMixin.java`, `src/main/resources/macabrefix.mixins.json`, `docs/TASKS.md`, `docs/UPSTREAM_REFERENCE.md`, and `docs/FILE_TREE.md`.
- Risk notes: Entity processing order may now follow the original query/list order instead of nearest-first order. The inspected branches process every entity rather than selecting the first or nearest entity, so intended boss effects are preserved while avoiding the unused sort.
- Mixin assessment: Mixin implemented and narrowly targeted. Normal Forge events could observe boss ticks but could not prevent this generated procedure from building and sorting the nearby-entity stream once the boss tick reached the upstream method. Redirecting only the unused sort is smaller than copying or replacing boss procedure logic.
- Status: IMPLEMENTED; `.\gradlew compileJava` and `.\gradlew build` succeed.

### MAC-BUFF-CURIOS-COMPAT - Preserve External Buffs And Curios Stat Effects

- Issue ID / short task name: `MAC-BUFF-CURIOS-COMPAT` / Preserve external buff and Curios-derived stat effects.
- Symptom: Reports say Macabre disables some buffs and breaks Curios-based effects from mods such as Terramity.
- Root cause summary: Likely, but not fully reproduced in this repository. No broad upstream effect-clearing path was found. `MacabrePotionProcedure` only applies Macabre's own `MACABRE` effect in `macabre:the_pit` and removes that same Macabre effect outside the Pit. The strongest likely compatibility source is the same hard attribute base overwrite in `BlooodAttribuesProcedure`, which can make external stat buffs appear disabled when they depend on base values or compete with repeated base resets.
- Evidence / upstream location: Binary scan for effect removal found `MacabrePotionProcedure` and boss death procedures only; no generic `removeAllEffects` path was found. `BlooodAttribuesProcedure` hard-sets affected attribute bases every player tick. `BloodArmorAttributesProcedure` checks only vanilla armor slots for Macabre armor set flags and has no Curios integration.
- Minimal implementation plan: Treat this as an attribute-compatibility validation task first, not a Curios dependency task. Reproduce with a Curios/Terramity item, inspect affected attributes before and after Macabre's player tick, then rely on `MAC-ATTR-BASE-RESET` to preserve external attribute bases/modifiers. Do not add a Curios dependency unless a later reproduction proves the bug is specifically Curios-slot detection rather than attribute overwrites.
- Expected files to modify: likely the same attribute compatibility fix files as `MAC-ATTR-BASE-RESET`; possibly a small validation/debug helper during development, but no permanent Curios dependency expected; documentation updates.
- Risk notes: This report may combine two different behaviors: stat attribute overwrites and Macabre full-set detection ignoring Curios slots. Keep the first implementation limited to preserving external stats; do not broaden into Curios API integration without a focused reproduction.
- Mixin assessment: Mixin likely unnecessary for initial validation; possible only if the hard attribute reset cannot be neutralized reliably by normal Forge hooks, matching `MAC-ATTR-BASE-RESET`.
- Status: INVESTIGATED

### MAC-TERRAMITY-CHARM-ATTRIBUTES - Preserve Terramity Curios Charm Attribute Bonuses

- Issue ID / short task name: `MAC-TERRAMITY-CHARM-ATTRIBUTES` / Preserve Terramity Curios charm attribute bonuses.
- Symptom: Terramity Curios charms that change attributes, including Gold Medal and Ambrosia, appear not to work or lose their stat bonuses when Macabre is also loaded. The reported visible case is max health charms being reset.
- Root cause summary: Confirmed interaction. Terramity `0.9.8` Curios items apply several bonuses by directly changing `AttributeInstance#setBaseValue` in Curios `onEquip` / `onUnequip` callbacks. Macabre's `BlooodAttribuesProcedure` later runs every player tick at phase `END` and directly resets the same attributes to hard-coded Macabre/vanilla-like bases. For max health, Macabre resets to `20.0`, `40.0`, or `60.0` based on Macabre armor state, which erases Terramity's base-value additions such as Ambrosia's `+8` max health and Gold Medal's `+4` max health.
- Evidence / upstream location: Terramity jar inspected at `D:\Downloads\terramity-0.9.8-forge-1.20.1.jar`. `net.mcreator.terramity.item.AmbrosiaItem#onEquip` calls `AmbrosiaBaubleIsEquippedProcedure#execute`, which adds `8.0` to `Attributes.MAX_HEALTH` via `AttributeInstance#setBaseValue`; `#onUnequip` subtracts `8.0`. `net.mcreator.terramity.item.GoldMedalItem#onEquip` calls `GoldMedalBaubleIsEquippedProcedure#execute`, which adds `4.0` to `Attributes.MAX_HEALTH` and `0.6` to Forge step height; `#onUnequip` subtracts the same values. A targeted scan of Terramity `*BaubleIs*Procedure` classes found many other direct base-value mutations, including max health, gravity, movement speed, armor, armor toughness, attack speed, knockback resistance, and step height. Macabre evidence remains `com.curseforge.macabre.procedures.BlooodAttribuesProcedure#onPlayerTick` in `reference/upstream/macabre/macabre-0.8.4-forge-1.20.1.jar`.
- Minimal implementation plan: Implemented through the source-level `MAC-ATTR-BASE-RESET` hardening. `MacabreAttributeProcedureMixin` prevents Macabre from overwriting Terramity's Curios-applied base values, while `AttributeCompatibilityFix` preserves Macabre armor bonuses as Macabrefix-owned transient modifiers. Follow-up fix: when the Mixin blocks Macabre's attribute procedure for a player, skip the same-tick fallback base restore for that player so Terramity's Ambrosia and Gold Medal unequip subtraction is not restored back to the pre-unequip value. Confirm Gold Medal and Ambrosia across equip, unequip, relog, respawn, and dimension change. If a Terramity charm mutates an attribute that Macabre does not touch, do not include it in the Macabre repair set unless a separate Macabre-caused reset is confirmed. No Curios or Terramity dependency was added.
- Expected files to modify: modified `build.gradle`, `src/main/java/com/doug/macabrefix/mixin/MacabreAttributeProcedureMixin.java`, `src/main/resources/macabrefix.mixins.json`, `docs/TASKS.md`, `docs/UPSTREAM_REFERENCE.md`, and `docs/FILE_TREE.md`.
- Risk notes: Terramity's equip/unequip logic is base-value additive/subtractive rather than stable UUID attribute modifiers. Preventing Macabre's reset at the source avoids the worst case where Terramity unequip subtracts from an already-reset base. The fallback restore must stand down on ticks where the source Mixin actually ran, because otherwise it can restore the tick-start HP base after Terramity correctly subtracts the first equipped HP charm. Existing saves that were already polluted before Macabrefix loads may still require re-equipping affected charms or manual validation because arbitrary lost external base values cannot always be inferred.
- Mixin assessment: Mixin implemented and narrowly targeted through `MAC-ATTR-BASE-RESET`; no Terramity-specific Mixin was added.
- Status: IMPLEMENTED; `.\gradlew compileJava` and `.\gradlew build` succeed. Manual Terramity charm testing is still pending.

### MAC-FULLSET-ATTRIBUTES - Rework Macabre Full Set Bonuses As Modifiers

- Issue ID / short task name: `MAC-FULLSET-ATTRIBUTES` / Convert full set bonuses away from hard base resets.
- Symptom: Full set bonuses are handled by `BlooodAttribuesProcedure` in a way that hard-resets attributes when the player does not have a full bonus, creating incompatibilities with many other mods.
- Root cause summary: Confirmed. `BloodArmorAttributesProcedure` records full Macabre armor set booleans in player capability data, then `BlooodAttribuesProcedure` interprets those booleans by directly setting attribute base values every tick. Absence of a full set is treated as permission to reset attributes to hard-coded fallback values.
- Evidence / upstream location: `BloodArmorAttributesProcedure#onPlayerTick` checks vanilla equipment slots for Macabre armor sets and calls `PlayerVariables#syncPlayerVariables` for `bloodarmor`, `baalArmor`, `gomoriaArmor`, `valamonArmor`, `gargamawArmor`, and `morphegorArmor`. `BlooodAttribuesProcedure#onPlayerTick` then applies hard-coded base values for Macabre armor bonuses and fallback values.
- Implementation: Reused the existing normal Forge post-tick correction in `src/main/java/com/doug/macabrefix/fixes/AttributeCompatibilityFix.java`. Macabrefix applies the confirmed Macabre full-set deltas as stable transient additive modifiers with fixed UUIDs at the cancelled upstream source hook and at the end of player ticks. It restores captured non-Macabre base values only when the source Mixin is skipped and the current base value matches one of Macabre's known hard-coded upstream outputs.
- Files modified: `src/main/java/com/doug/macabrefix/fixes/AttributeCompatibilityFix.java`, `src/main/java/com/doug/macabrefix/mixin/MacabreAttributeProcedureMixin.java`, `docs/TASKS.md`, and `docs/UPSTREAM_REFERENCE.md`.
- Risk notes: Full-set bonuses are user-visible Macabre balance behavior, so preserve Macabre's intended bonus magnitudes while changing only the implementation mechanism. Health changes must clamp current health safely when max health decreases.
- Mixin assessment: Mixin now implemented as hardening for `MAC-ATTR-BASE-RESET`. `BlooodAttribuesProcedure` is source-cancelled before hard base writes, and Macabrefix-owned modifiers for Macabre armor bonuses are applied at that source hook with the normal Forge player tick hook retained as fallback and cleanup.
- Status: IMPLEMENTED; `.\gradlew compileJava` and `.\gradlew build` succeed.

### MAC-STRUCTURE-LOCATE - Register Major Macabre Structures As Real Structures

- Issue ID / short task name: `MAC-STRUCTURE-LOCATE` / Make major Macabre structures locatable.
- Symptom: `/locate structure` and structure-searching tools such as Explorer's Compass cannot find Macabre dungeons, boss altars, and similar generated landmarks.
- Root cause summary: Confirmed. Upstream Macabre stores NBT templates under `data/macabre/structures/`, but places them through a custom `Feature` named `macabre:structure_feature` and Forge biome modifiers that add placed features at the `surface_structures` generation step. The jar contains no `data/*/worldgen/structure/`, `data/*/worldgen/structure_set/`, `data/*/tags/worldgen/structure/`, `data/*/worldgen/template_pool/`, or `data/*/worldgen/processor_list/` entries. Because the vanilla structure registry never sees these placements as structures, locate commands and Explorer's Compass have nothing conventional to query.
- Evidence / upstream location: `com.curseforge.macabre.world.features.StructureFeature` extends `Feature<StructureFeatureConfiguration>` and directly loads a `StructureTemplate` from the configured `structure` id, then places it in `place(...)`. `StructureFeatureConfiguration` stores `structure`, `random_rotation`, `random_mirror`, `ignored_blocks`, and `offset`. `MacabreMod` registers `StructureFeature.REGISTRY` on the mod event bus. Examples include `data/macabre/worldgen/configured_feature/big_bad_dungeon_1.json`, `pyramid_dungeon_1.json`, `tower_dungeon_1.json`, `baal_feature.json`, `gargamaw_feature.json`, `gomoria_feature.json`, `valamon_feature.json`, and `morphegor_feature.json`, all using `"type": "macabre:structure_feature"`.
- Minimal implementation plan: Add a data-driven compatibility layer for a small first set of important landmarks, starting with the three dungeons and boss altar/spawner structures. Define real structure entries, structure sets, template pools if using jigsaw structures, and structure tags under Macabrefix resources while referencing the existing Macabre NBT templates by id. Prevent duplicate generation by removing or suppressing the matching upstream placed features for only the converted structures, preferably with normal Forge biome modifier data such as targeted `remove_features` entries if testing confirms Forge supports the required removal behavior in 1.20.1. Keep spacing/biome choices close to upstream rather than trying to reproduce every feature placement predicate at once.
- Expected files to modify: likely data/resource files under `src/main/resources/data/macabrefix/worldgen/structure/`, `src/main/resources/data/macabrefix/worldgen/structure_set/`, `src/main/resources/data/macabrefix/worldgen/template_pool/`, `src/main/resources/data/macabrefix/tags/worldgen/structure/`, and possibly `src/main/resources/data/macabrefix/forge/biome_modifier/` for targeted feature removal; documentation updates in `docs/TASKS.md`, `docs/UPSTREAM_REFERENCE.md`, and `docs/FILE_TREE.md` after any structural resource additions.
- Risk notes: `/locate` can only find structures known to the structure system; it will not retroactively index old feature-placed structures already generated in existing chunks. Converting every decorative tree, rock, plant, and small template would be overbroad and could pollute compass results, so the first implementation should focus on player-meaningful landmarks. Duplicates are the main risk if feature placement remains active while new real structures are also generated.
- Mixin assessment: Mixin likely unnecessary for the initial fix. Normal data-driven structure registration plus Forge biome modifier data should be tried first. A Mixin would only be worth revisiting if a later requirement is to make already feature-placed historical structures appear in vanilla locate indexes, which is not a good first target.
- Status: INVESTIGATED
