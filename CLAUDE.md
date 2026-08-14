# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

`platform-sdk` is the **pure SDK library** for Nuclr Commander plugins — only interfaces, abstract classes, and annotations. It is not an application. All dependencies (Lombok, SLF4J, Jackson) are `provided` scope: they are supplied by the host app at runtime, never bundled into this JAR.

## Build Commands

```bash
mvn clean install -Dgpg.skip=true           # Build and install locally (for downstream plugin builds)
mvn deploy -s settings.xml                  # Publish to Sonatype Maven Central (GPG key + credentials required)
mvn versions:set -DnewVersion="X.Y.Z"       # Bump version in pom.xml before release
```

**Requires JDK 25** (`maven.compiler.release` is 25). Maven runs on whatever `JAVA_HOME` points at, so an older `JAVA_HOME` fails with `release version 25 not supported` — that error is about Maven's JDK, not the pom.

Use `-Dgpg.skip=true` for ordinary local builds; the signing step has no passphrase outside a release and otherwise fails with `Exit code: 2`.

No test framework is configured — `mvn test` is a no-op.

## Release Workflow

**Releases are published manually from Windows**, not by CI.

1. `mvn versions:set -DnewVersion="X.Y.Z"` — update pom.xml
2. Commit, push, create a GitHub Release tagged `vX.Y.Z`
3. `mvn deploy -s settings.xml` locally — `central-publishing-maven-plugin` uploads and auto-publishes (`waitUntil=published`)
4. Bump `main` to the next `-SNAPSHOT`

**GPG signing** is required for deployment. The pom hardcodes the executable at `C:\Program Files\GnuPG\bin\gpg.exe` with loopback pinentry — another reason releases happen on Windows.

`.github/workflows/maven-publish.yml` also runs `mvn deploy` on a published GitHub Release, but **it cannot currently publish**: it imports no GPG key and sets no passphrase, the hardcoded Windows `gpg.exe` path does not exist on `ubuntu-latest`, and the `settings.xml` that `setup-java` generates declares `server-id: github` while the publishing plugin expects a server with id `central`. Treat it as inert until those three are addressed. Its Java version is kept in step with the pom so it at least compiles.

## Plugin Architecture (SDK Surface)

### Sealed hierarchy
`BaseNuclrPlugin` is a sealed interface. Only three permitted subtypes exist:
- `QuickViewNuclrPlugin` — in-pane file preview
- `FilePanelNuclrPlugin` — filesystem/resource browser (drives, S3, Git, etc.)
- `FullscreenNuclrPlugin` — full-screen viewer or editor

Adding a fourth subtype requires modifying the `permits` clause in `BaseNuclrPlugin`.

### Declarations live in `plugin.json`, not in Java (since 4.0.0)
A plugin interface describes only what a plugin **does**; what it **is** belongs to the bundle's root `plugin.json`, so the host can read a plugin's identity without loading and instantiating its classes.

Removed from `BaseNuclrPlugin` in 4.0.0: `type()`, `id()`, `name()`, `version()`, `description()`, `author()`, `license()`, `website()`, `pageUrl()`, `docUrl()`, `is(Type)`, `developer()`, `singleton()`. Removed from the subtypes: `priority()` (quick-view) and `role()` (fullscreen).

```json
{ "schemaVersion": 1, "version": "1.0.0", "platformSdkVersion": "4.0.0",
  "plugins": [ { "class": "...", "type": "QuickView", "id": "...", "name": "...",
                 "developer": "Official", "singleton": true, "priority": 1 } ] }
```

Nothing in Java mirrors those values any more, so there is no drift to police — but note `singleton` no longer defaults to `true` via a `default` method, so a bundle relying on that must say so explicitly in the manifest.

The `Type`, `Developer` and `Role` enums are still published as the vocabulary for the matching manifest values, but **no SDK method takes or returns them**. They are the only intentionally unreferenced public API here.

The manifest's `version` should be `${project.version}` with Maven resource filtering scoped to `plugin.json` alone; bundles also carry icons and native libraries that must not be filtered.

### Identifying another plugin
`act(...)` passes the other pane's plugin as a `BaseNuclrPlugin`, and there is no longer any method to interrogate it. The hierarchy is sealed, so use `instanceof` — against a subtype for the slot (`other instanceof QuickViewNuclrPlugin`) or against a concrete class for a peer instance of the same plugin. `uuid()` distinguishes *instances*: `other.uuid().equals(uuid())` means the action targets this very instance.

### Plugin lifecycle (in order)
1. `preinit(NuclrPluginContext context)` — receive platform services
2. `init()` — setup; safe to access context here
3. `openResource(NuclrResource, AtomicBoolean)` — called repeatedly; **must be async**; check `cancelled.get()` for interruption
4. `updateTheme(NuclrThemeScheme)` — optional, default no-op
5. `unload()` — cleanup on shutdown

### Key contracts
- `@ExcludePlugin` — marks a class to skip during plugin discovery
- `NuclrResource` is **abstract** but declares no abstract methods — subclasses exist to carry transport-specific state, and need only call the single `NuclrResource(Path)` constructor (pass `null` for virtual/remote resources). Lombok `@Data` generates the accessors (`getUuid()`, `isFolder()`, …); equality and hashing use `uuid` alone. The optional `openInputStream(OpenOption...)` defaults to throwing `UnsupportedOperationException`.
- `supports(NuclrResource)` may be called **off the EDT** — implementations must be thread-safe and must not show modal UI
- `NuclrSettings` uses a `(namespace, key)` model — always pass a plugin-specific namespace to avoid key collisions
- `NuclrEventBus` events use plain `String` type identifiers and `Map<String, Object>` payloads (no compile-time type safety on payload shapes)
- A plugin declared `"singleton": false` must return a distinct `uuid()` per instance

## Code Conventions

- Lombok throughout: `@Data`, `@AllArgsConstructor`, `@NoArgsConstructor` on DTOs. Use Lombok for new data classes.
  Lombok must stay listed in `maven-compiler-plugin`'s `annotationProcessorPaths`: since JDK 23 `javac` ignores annotation processors that are only on the classpath, and dropping it fails the build with `cannot find symbol` on every generated accessor, with nothing pointing at Lombok.
- No Spring annotations — this is plain Java, no DI framework
- Package root: `dev.nuclr.platform`
- Sources and Javadoc JARs are attached on every build (via maven-source-plugin and maven-javadoc-plugin)
- Javadoc runs strict — a `{@link}` to a method you delete fails the build at `attach-javadocs`, not at compile
