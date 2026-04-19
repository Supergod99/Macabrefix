# Test Plan

Use the smallest relevant set of tests for each fix. A docs-only change does not require a build.

## Manual Tests

- Build: run `.\gradlew build`.
- Client load: run `.\gradlew runClient` with the original Macabre mod available in the dev runtime.
- Server load: run the dedicated server when the fix affects common or server-side behavior.
- Reproduction test: repeat the exact scenario that exposed the Macabre issue.
- Regression test: repeat nearby Macabre behavior that should remain unchanged.

## Expected Outcomes

- Forge loads Macabrefix with mod id `macabrefix`.
- Macabrefix requires Macabre and does not replace it.
- No crash occurs during startup, world load, save, reload, teleport, combat, or shutdown.
- No unrelated Macabre behavior changes.
- Logs remain readable and do not fill with repeated warnings/errors.
- Any fixed behavior changes only in the documented target scenario.

## Crash/Regression Checks

- Check latest log for stack traces, repeated warnings, and error spam.
- Check for client/server class loading mistakes.
- Check for save corruption or load failures after restart.
- Check for behavior changes outside the specific fix target.
- Check for increased lag, runaway ticking, repeated world scans, or unexpected entity growth.

## Server Startup Test

1. Install Forge 1.20.1, Macabre, and Macabrefix in a dedicated server test instance.
2. Start the server.
3. Confirm startup completes.
4. Join with a compatible client.
5. Stop and restart the server.
6. Confirm the world saves and reloads cleanly.

Expected outcome: the server starts, accepts a client, saves, stops, and restarts without crashes or client-only class loading errors.

## Singleplayer World Creation Test

1. Start the Forge client with Macabre and Macabrefix installed.
2. Create a new singleplayer world.
3. Load into the world and wait through initial ticking.
4. Save and quit.
5. Reopen the world.

Expected outcome: world creation, first load, save, and reload complete without crashes, log spam, or broken Macabre loading.

## Dimension Entry/Exit Test

Run this when the fix touches dimensions, teleports, portals, worldgen, structures, spawn placement, or player travel.

1. Enter the relevant Macabre dimension or world area.
2. Move, interact, and wait long enough for nearby chunks/entities to tick.
3. Exit or return using the expected route.
4. Save, reload, and repeat entry/exit once.

Expected outcome: teleports complete safely, players are not stranded, chunks load, return paths work, and no crash or persistent lag appears.

## Entity/AI/Performance Sanity Checks

Run this when the fix touches entities, AI, ticking, spawning, structures, world scans, or repeated events.

- Observe entity counts where relevant.
- Watch for repeated allocations, unbounded searches, or per-tick broad scans.
- Confirm AI goals or ticking behavior do not loop unexpectedly.
- Check server TPS or client frame time during the target scenario.
- Compare log volume before and after the fix.

Expected outcome: no runaway entity behavior, no obvious TPS drop, and no new spam logging.

## Save/Reload Test

1. Trigger the target behavior.
2. Save and quit.
3. Reload the same world.
4. Repeat the target behavior.

Expected outcome: the fix remains stable across save/reload and does not leave invalid persistent state.

## Multiplayer/Server Sanity Checks

Run this when the fix affects common code, server-side behavior, networking-adjacent behavior, entities, dimensions, worldgen, or progression.

- Join a dedicated server with one client.
- If relevant, join with two clients to check visibility and desync.
- Trigger the target behavior from the client.
- Confirm server logs stay clean.
- Confirm all clients agree on the resulting state.

Expected outcome: no disconnects, desync, ghost entities, broken teleports, or server-only crashes.
