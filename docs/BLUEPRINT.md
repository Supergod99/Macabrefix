# Blueprint

## 1. Project Type

Macabrefix is a small Forge 1.20.1 support/fix mod for Macabre.

It requires the original Macabre mod. It is not a rerelease, fork, or replacement of the full Macabre mod.

The project should stay focused on support fixes that make Macabre safer and more stable while preserving original behavior whenever possible.

## 2. Primary Goals

- Fix bugs and unintended behavior in or around Macabre.
- Identify and address likely performance problems.
- Identify and address likely server/singleplayer stability issues.
- Keep implementations minimal and targeted.
- Prefer root-cause fixes over broad symptom patches.
- Make each fix easy to review, test, and remove if upstream Macabre later fixes the issue.

## 3. Investigation Priorities

- Startup/load issues, including dependency failures and client/server class loading mistakes.
- Dimension/world transition issues, including bad teleport targets, unsafe spawn placement, stuck players, or broken returns.
- Entity/ticking/performance hotspots, especially repeated scans, runaway spawns, excessive allocations, and noisy ticking behavior.
- Server-only or multiplayer desync issues.
- Unintended mechanics or broken progression.
- Crash paths and log spam sources.
- Worldgen, structure, mob, and dimension behavior that can break saves or create persistent server load.

## 4. Implementation Philosophy

- Inspect upstream logic first.
- Document the exact hook point before coding.
- Prefer fixes that stop the confirmed bad upstream behavior at its source while preserving the intended Macabre behavior.
- Evaluate narrow Forge-compatible fixes using events, registries, capabilities, lifecycle hooks, or other public APIs before choosing a Mixin.
- Use a normal hook when it cleanly prevents or corrects the root cause. Do not prefer a normal hook when it only patches symptoms after bad state, packets, or compatibility damage already occurred.
- Use a Mixin when a confirmed upstream source issue cannot be fixed reliably through normal hooks and the Mixin is the safest minimal source fix.
- If a Mixin is required, keep it narrowly targeted and document the justification in `docs/UPSTREAM_REFERENCE.md`.
- Solve one issue at a time.
- Avoid speculative refactors.
- Avoid broad compatibility layers until a concrete issue proves they are needed.
- Preserve original Macabre behavior unless the bugfix explicitly requires change.

## 5. Root-Cause Fix Preference

Macabrefix should favor source fixes when one upstream Macabre method or generated procedure creates multiple downstream compatibility symptoms. A source fix is preferred when it is smaller and more compatible than tracking every mod-specific symptom.

Normal Forge/API hooks remain the first option to evaluate, especially for events, registry data, worldgen resources, capabilities, and lifecycle integration. However, if the normal hook can only observe or repair the damage after Macabre has already changed player state, sent packets, written saved data, or broken another mod's callback flow, a narrowly targeted Mixin may be the more compatible and less invasive fix.

When using a Mixin under this policy:

- Target the smallest confirmed upstream method or instruction group.
- Preserve Macabre's intended output through vanilla/Forge mechanisms or a small replacement.
- Avoid patching unrelated generated procedures in the same change.
- Keep fallback code only when it protects against a changed upstream jar or failed optional injection.
- Document the exact upstream method, why normal hooks were insufficient, and how the replacement preserves intended behavior.

## Current Scaffold

- Main mod entrypoint: `com.doug.macabrefix.MacabreFix`.
- Placeholder compatibility class: `com.doug.macabrefix.compat.MacabreCompat`.
- Placeholder fix registrar: `com.doug.macabrefix.fixes.FixRegistrar`.
- Shared constants holder: `com.doug.macabrefix.util.Constants`.
- Forge metadata declares a mandatory dependency on `macabre`.
- No gameplay behavior, Mixins, configs, or fix logic exist yet.
