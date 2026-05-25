# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

`platform-sdk` is the **pure SDK library** for Nuclr Commander plugins — only interfaces, abstract classes, and annotations. It is not an application. All dependencies (Lombok, SLF4J, Jackson) are `provided` scope: they are supplied by the host app at runtime, never bundled into this JAR.

## Build Commands

```bash
mvn clean install                            # Build and install to local Maven repo (for downstream plugin builds)
mvn deploy -s settings.xml                  # Deploy to Sonatype Maven Central (requires GPG key + credentials in settings.xml)
mvn versions:set -DnewVersion="X.Y.Z"       # Bump version in pom.xml before release
```

No test framework is configured — `mvn test` is a no-op.

## Release Workflow

1. `mvn versions:set -DnewVersion="X.Y.Z"` — update pom.xml
2. Commit, push, create a GitHub Release tagged `vX.Y.Z`
3. CI (`maven-publish.yml`) picks up the tag, derives the version (`v3.0.1` → `3.0.1`), runs `mvn deploy -s settings.xml`, auto-publishes to Central, then bumps `main` to the next SNAPSHOT

**GPG signing** is required for deployment. The GPG executable is expected at `C:\Program Files\GnuPG\bin\gpg.exe` (loopback pinentry configured in the workflow).

**CI Java version gotcha:** `.github/workflows/maven-publish.yml` sets up Java 11, but `pom.xml` declares `<source>21</source>` / `<target>21</target>`. If you update the pom's Java version, update the workflow too.

## Plugin Architecture (SDK Surface)

### Sealed hierarchy
`BaseNuclrPlugin` is a sealed interface. Only three permitted subtypes exist:
- `QuickViewNuclrPlugin` — in-pane file preview
- `FilePanelNuclrPlugin` — filesystem/resource browser (drives, S3, Git, etc.)
- `FullscreenNuclrPlugin` — full-screen viewer or editor

Adding a fourth subtype requires modifying the `permits` clause in `BaseNuclrPlugin`.

### Plugin lifecycle (in order)
1. `preinit(NuclrPluginContext context)` — receive platform services
2. `init()` — setup; safe to access context here
3. `openResource(NuclrResource, AtomicBoolean)` — called repeatedly; **must be async**; check `cancelled.get()` for interruption
4. `updateTheme(NuclrThemeScheme)` — optional, default no-op
5. `unload()` — cleanup on shutdown

### Key contracts
- `@ExcludePlugin` — marks a class to skip during plugin discovery
- `NuclrResource` is **abstract** — plugin implementations extend it and must implement `getUuid()`, `isFolder()`, and `getColumnValue(int columnIndex)`. The optional `openInputStream()` defaults to throwing `UnsupportedOperationException`.
- `NuclrSettings` uses a `(namespace, key)` model — always pass a plugin-specific namespace to avoid key collisions
- `NuclrEventBus` events use plain `String` type identifiers and `Map<String, Object>` payloads (no compile-time type safety on payload shapes)
- `singleton()` returns `true` by default; multi-instance plugins must generate unique UUIDs per instance

## Code Conventions

- Lombok throughout: `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor` on DTOs. Use Lombok for new data classes.
- No Spring annotations — this is plain Java, no DI framework
- Package root: `dev.nuclr.platform`
- Sources and Javadoc JARs are attached on every build (via maven-source-plugin and maven-javadoc-plugin)
