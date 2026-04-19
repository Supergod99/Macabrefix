# Codex Guide

Use this guide when opening the project in a fresh Codex chat.

## Target

- Minecraft Forge 1.20.1.
- Java 17.
- Mod id: `macabrefix`.
- Base package: `com.doug.macabrefix`.
- This project requires the original Macabre mod at runtime.

## Non-Negotiable Rules

- Keep fixes minimal and localized.
- Prefer the least invasive implementation possible.
- Do not add unrelated features.
- Do not add new content, mobs, items, blocks, dimensions, mechanics, or balance changes.
- Do not rewrite subsystems unless required by the specific bugfix task.
- Prefer root-cause fixes over symptom patches. The best fix prevents the bad upstream behavior at its source while preserving intended Macabre behavior.
- Evaluate normal Forge/API/event-based fixes first, but do not choose them automatically if they can only repair symptoms after the bad behavior already happened.
- Mixins are allowed when they are the smallest, safest, and most reliable source fix for a confirmed upstream bug.
- If using a Mixin, document why a normal hook was insufficient and keep it narrowly targeted.
- Avoid heavy per-tick allocations or unbounded searches.
- Preserve existing Macabre behavior except where the specific fix requires change.
- Do not copy large amounts of upstream mod code into this repository.

## Change Protocol

- Update `docs/FILE_TREE.md` after any structural change.
- After any structural change, including a new file, rename, move, or delete, update `docs/FILE_TREE.md` immediately.
- `docs/FILE_TREE.md` must always reflect the real repository structure.
- Update `docs/TASKS.md` when a task phase is completed.
- Never silently rename package paths, mod id, resource paths, Gradle identity, or dependency declarations.
- Keep each change tied to one specific bugfix or support fix.
- Before editing code, identify the smallest file set needed for the task.
- After editing code, run `.\gradlew build` unless the task is docs-only.

## Upstream Reference Protocol

- Treat `reference/upstream/macabre/` as reference-only.
- Place the original Macabre jar and any reference-only upstream materials under `reference/upstream/macabre/`.
- If the upstream Macabre jar is added under `reference/upstream/macabre/`, update `docs/FILE_TREE.md` and `docs/UPSTREAM_REFERENCE.md` accordingly.
- Inspect upstream code only as needed to identify bugs, hook points, and safe implementation targets.
- Record useful findings in `docs/UPSTREAM_REFERENCE.md`.
- Do not copy large amounts of upstream code into this repo.
- When a fix is based on upstream behavior, document the observed behavior, exact hook point, and why the chosen approach is minimal.

## Preferred Fix Workflow

1. Reproduce or clearly describe the Macabre issue.
2. Inspect upstream Macabre behavior only as much as needed.
3. Identify the true source of the bug and the smallest behavior that must change.
4. Identify candidate Forge events, APIs, lifecycle hooks, compatibility points, or narrowly targeted Mixin hook points.
5. Choose the narrowest safe fix point. Prefer normal hooks when they prevent or correct the cause cleanly; prefer a narrow source Mixin when normal hooks would require broad symptom tracking, repeated post-facto repair, or compatibility-specific patches.
6. Implement one fix at a time.
7. Build and run the smallest relevant manual test.
8. Update `docs/TASKS.md`, `docs/UPSTREAM_REFERENCE.md`, and `docs/FILE_TREE.md` when relevant.

## Source Fix Policy

Macabrefix should not grow broad compatibility layers that chase every downstream symptom of one upstream bug. If a Macabre procedure, event handler, or generated method is confirmed to be the common source of compatibility failures, prefer fixing that source directly.

A narrow Mixin can be the least invasive option when all of these are true:

- The upstream source behavior is confirmed and documented.
- A normal Forge/API hook cannot prevent the bad behavior before it affects other mods, saves, packets, or player-visible state.
- The Mixin cancels, redirects, or guards only the specific bad behavior.
- Macabre's intended behavior is preserved through original logic where possible or through a small replacement that matches the documented upstream effect.
- The implementation is easier to test and remove later than a broad fallback or mod-specific compatibility layer.
