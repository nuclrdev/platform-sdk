# Nuclr Commander Platform SDK 🔌

Java SDK for building plugins for [Nuclr Commander](https://github.com/nuclrdev/commander), a cross-platform dual-pane file manager. 🗂️

This repository publishes the `dev.nuclr:platform-sdk` artifact used by Nuclr Commander plugins and is available from Maven Central.

## Requirements ✅

- Java 25+
- Maven 3.9+

Lombok is used internally. Since JDK 23, `javac` no longer runs annotation processors that are merely on the classpath, so a build that uses Lombok must opt back in — either with `<proc>full</proc>` on `maven-compiler-plugin`, or by declaring Lombok in `<annotationProcessorPaths>`.

## Maven Dependency 📦

Add the SDK to your plugin project. Use `provided` scope — the host application supplies the SDK at runtime and it must never be bundled into your plugin:

```xml
<dependency>
    <groupId>dev.nuclr</groupId>
    <artifactId>platform-sdk</artifactId>
    <version>4.0.0</version>
    <scope>provided</scope>
</dependency>
```

Find the latest version here:

https://central.sonatype.com/artifact/dev.nuclr/platform-sdk

## ⚠️ Breaking change in 4.0.0

**Everything a plugin merely *declares* about itself has left the Java interface.** A plugin interface now describes only what a plugin *does*; what it *is* belongs to the [`plugin.json`](#the-pluginjson-manifest-) manifest, so the host can read a plugin's identity without loading and instantiating its classes.

Removed from `BaseNuclrPlugin`:

`type()` · `id()` · `name()` · `version()` · `description()` · `author()` · `license()` · `website()` · `pageUrl()` · `docUrl()` · `is(Type)` · `developer()` · `singleton()`

Removed from the subtypes: `priority()` (`QuickViewNuclrPlugin`) and `role()` (`FullscreenNuclrPlugin`).

The `Type`, `Developer` and `Role` enums are still published as the vocabulary for the corresponding manifest values, but no SDK method takes or returns them any more.

Java baseline also moved from 21 to **25**. See [Migrating from 3.x](#migrating-from-3x-to-40-).

## What The SDK Provides 🚀

- `BaseNuclrPlugin` — shared lifecycle and behaviour for every plugin
- `QuickViewNuclrPlugin`, `FilePanelNuclrPlugin`, `FullscreenNuclrPlugin` — the three plugin shapes
- `NuclrPluginContext` — access to the event bus, theme, settings, and locale
- `NuclrResource`, `NuclrMenuResource`, `NuclrContextMenuItem` — common model types
- `NuclrEventBus` — cross-plugin and platform event messaging
- `NuclrTerminalSession` — lets a file panel supply its own shell for the embedded console
- `@ExcludePlugin` — marks a class to be skipped during plugin discovery

## Plugin Types 🧩

`BaseNuclrPlugin` is a **sealed** interface with exactly three permitted subtypes. Implement one of these, never `BaseNuclrPlugin` directly:

- **`QuickViewNuclrPlugin`** — renders a preview in the quick-view side panel
  - Returns a Swing `JComponent` from `panel()`
  - `openResource(NuclrResource, AtomicBoolean)` loads or refreshes the preview
  - When several viewers accept the same resource, the manifest's `priority` breaks the tie; **lower wins**
- **`FilePanelNuclrPlugin`** — provides a browser pane (local drives, archives, SSH, cloud buckets…)
  - `openResource(...)` returns a `NuclrResourceData` (entries + column names)
  - The streaming overload `openResource(resource, cancelled, EntrySink)` paints entries as they are discovered
  - `getPluginMenuItems()` supplies the Alt+F1 / Alt+F2 location menu
  - `getCurrentLocationDisplayText()` and `getSelectionSummaryText(...)` feed the status bars
  - `openTerminal(...)` optionally supplies a shell rooted at the current location
- **`FullscreenNuclrPlugin`** — takes over the whole commander window
  - `panel()` returns the UI, `openResource(...)` opens the file
  - Whether it is a viewer or an editor is declared by the manifest's `role`

All three extend `BaseNuclrPlugin`, which provides:

- lifecycle hooks — `preinit(NuclrPluginContext)`, `init()`, `updateTheme(NuclrThemeScheme)`, `unload()`
- resource handling — `supports(NuclrResource)`, `closeResource()`, `getCurrentResource()`
- focus callbacks — `onFocusGained()`, `onFocusLost()`, `isFocused()`
- instance identity — `uuid()`
- window title — `getWindowTitle()`
- inter-plugin actions — `act(...)` and `contextMenuItems(...)`
- convenience casts — `asQuickView()`, `asFilePanel()`, `asFullscreen()`

### Identifying another plugin 🔍

`act(...)` hands you the other pane's plugin as a `BaseNuclrPlugin`. There is no method to ask it what it is — the hierarchy is sealed, so use `instanceof` against the subtype or the concrete class:

```java
if (other instanceof QuickViewNuclrPlugin) { … }        // which slot it occupies
if (other instanceof LocalFileSystemPlugin peer) { … }  // a peer instance of your own plugin
```

Use `uuid()` to tell *instances* apart — `other.uuid().equals(uuid())` means the action targets this very instance rather than the opposite pane.

### Threading ⚙️

`supports(NuclrResource)` may be called **from a background thread**, because deciding whether a plugin can open something can involve blocking filesystem or network access. Implementations must be thread-safe, must not assume Swing's event-dispatch thread, and must not show modal UI.

`openResource(...)` must do heavy work asynchronously, poll the supplied `AtomicBoolean` regularly, and abort cleanly when it becomes `true`. All UI updates must be dispatched to the EDT.

## Core Model Types 🧱

- **`NuclrResource`** — abstract base for everything shown in a panel. Its only constructor takes a `java.nio.file.Path`, which is `null` for virtual or remote resources. Lombok `@Data` generates the accessors for `uuid`, `name`, `fullPath`, `folder`, `hidden`, `link`, `readable`, `length`, the three timestamps, and a free-form `metadata` map. Equality and hashing are based on `uuid` alone. Override `openInputStream(OpenOption...)` if your resource can stream — the default throws `UnsupportedOperationException`.
- **`NuclrMenuResource`** — describes menu contributions for file panels
- **`NuclrContextMenuItem`** — entries for the right-click menu, dispatched back through `act(...)`
- **`NuclrPluginContext`** — provides `getEventBus()`, `getTheme()`, `getSettings()`, and `getLocale()`
- **`NuclrSettings`** — a `(namespace, key)` store. Always pass a plugin-specific namespace to avoid collisions.
- **`NuclrEventBus`** — `emit(...)` / `subscribe(NuclrEventListener)`. Events are plain `String` type identifiers with `Map<String, Object>` payloads; there is no compile-time type safety on payload shapes.

## The `plugin.json` Manifest 📋

Every bundle carries a `plugin.json` at its root declaring what it contains. Since 4.0.0 this is the **only** source of plugin metadata.

```json
{
  "schemaVersion": 1,
  "version": "1.0.0",
  "platformSdkVersion": "4.0.0",
  "plugins": [
    {
      "class": "com.example.MyQuickViewPlugin",
      "type": "QuickView",
      "id": "com.example.myplugin.quickview",
      "name": "My Quick Viewer",
      "version": "1.0.0",
      "description": "Example quick view plugin.",
      "author": "Your Name",
      "license": "Apache-2.0",
      "website": "https://example.com",
      "pageUrl": "https://example.com/plugins/my-quick-view",
      "docUrl": "https://example.com/plugins/my-quick-view/docs",
      "developer": "Community",
      "singleton": true,
      "priority": 100
    }
  ]
}
```

Top level:

| Field | Meaning |
|---|---|
| `schemaVersion` | Manifest format version. Currently `1`. |
| `version` | Version of the bundle as a whole. |
| `platformSdkVersion` | SDK the bundle was built against. |
| `plugins` | One entry per plugin class in the bundle — a bundle may ship several. |

Per entry:

| Field | Meaning |
|---|---|
| `class` | Fully-qualified class name implementing one of the three plugin interfaces. |
| `type` | `QuickView`, `FilePanel`, or `Fullscreen` — must match the interface the class implements. |
| `id` | Stable unique identifier, e.g. `com.example.myplugin`. |
| `name`, `description`, `author`, `license` | Shown in the plugin manager. |
| `website`, `pageUrl`, `docUrl` | Optional links. |
| `developer` | `Official` or `Community`. |
| `singleton` | Whether at most one instance may exist. Multi-instance plugins must return a distinct `uuid()` per instance. |
| `priority` | Quick-view plugins only; lower wins when several accept the same resource. |
| `role` | Fullscreen plugins only: `Viewer` (read-only) or `Editor`. |

As of 4.0.0 these are declared **only** here — no Java method mirrors them, so there is nothing for the manifest to fall out of step with.

Keep the version out of source control drift by letting Maven fill it in. Declare `"version": "${project.version}"` in `plugin.json` and filter that one file:

```xml
<resources>
    <resource>
        <directory>src/main/resources</directory>
        <filtering>true</filtering>
        <includes><include>plugin.json</include></includes>
    </resource>
    <resource>
        <directory>src/main/resources</directory>
        <filtering>false</filtering>
        <excludes><exclude>plugin.json</exclude></excludes>
    </resource>
</resources>
```

Filter `plugin.json` only — plugin bundles routinely carry icons and native libraries that must be copied byte-for-byte.

## Minimal Quick View Example 💡

```java
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;
import javax.swing.JPanel;

import dev.nuclr.platform.plugin.NuclrPluginContext;
import dev.nuclr.platform.plugin.NuclrResource;
import dev.nuclr.platform.plugin.QuickViewNuclrPlugin;

public final class MyQuickViewPlugin implements QuickViewNuclrPlugin {

    /** Matches the "id" of this class's entry in plugin.json. */
    private static final String PLUGIN_ID = "com.example.myplugin.quickview";

    private final JPanel panel = new JPanel();
    private NuclrPluginContext context;
    private NuclrResource currentResource;
    private boolean focused;

    // --- identity -----------------------------------------------------------

    /** Singleton plugins can use a fixed id; multi-instance ones need a fresh UUID each. */
    @Override
    public String uuid() { return PLUGIN_ID; }

    // --- lifecycle ----------------------------------------------------------

    @Override
    public void preinit(NuclrPluginContext context) { this.context = context; }

    @Override
    public NuclrPluginContext getContext() { return context; }

    @Override
    public void init() {
    }

    @Override
    public void unload() {
        closeResource();
        context = null;
    }

    // --- quick view ---------------------------------------------------------

    @Override
    public JComponent panel() { return panel; }

    /** May be called off the EDT — keep it thread-safe and free of modal UI. */
    @Override
    public boolean supports(NuclrResource resource) {
        String name = resource.getName();
        return name != null && name.toLowerCase().endsWith(".txt");
    }

    @Override
    public boolean openResource(NuclrResource resource, AtomicBoolean cancelled) {
        currentResource = resource;
        // Heavy work belongs on a background thread; poll cancelled and stop
        // early when it flips, then update the panel on the EDT.
        return true;
    }

    @Override
    public NuclrResource getCurrentResource() { return currentResource; }

    @Override
    public void closeResource() { currentResource = null; }

    // --- focus --------------------------------------------------------------

    @Override
    public boolean onFocusGained() { focused = true; return true; }

    @Override
    public void onFocusLost() { focused = false; }

    @Override
    public boolean isFocused() { return focused; }
}
```

## Packaging 📦

A plugin is distributed as a signed ZIP whose root contains:

- `plugin.json` — the manifest described above
- your plugin JAR
- `lib/` — runtime dependencies (never the SDK itself, which is `provided`)
- any assets the plugin needs

Commander verifies each bundle's signature before loading it, then discovers plugin classes from the manifest.

## Migrating from 3.x to 4.0 🔀

1. Build on **JDK 25**, and make sure Lombok still runs — see [Requirements](#requirements-).
2. Delete the declarative methods from every plugin class: `type()`, `id()`, `name()`, `version()`, `description()`, `author()`, `license()`, `website()`, `pageUrl()`, `docUrl()`, `is(Type)`, `developer()`, `singleton()`, plus `priority()` on quick-view plugins and `role()` on fullscreen ones. Remove the fields and constants that only fed them.
3. Move those values into `plugin.json` (see the schema above), one entry per plugin class. A plugin that relied on the old `singleton()` default of `true` should now say `"singleton": true` explicitly.
4. Fix internal callers:
   - `uuid()` implementations that returned `id()` need their own constant.
   - Anything reading `version()` out of a filtered `plugin.properties` can drop it — the version comes from the manifest now.
5. **A plugin can no longer interrogate another plugin.** Both `other.id()` and `other.is(Type.X)` are gone. Replace them with `instanceof`:
   ```java
   // before
   if (other.is(BaseNuclrPlugin.Type.QuickView)) { … }
   if (other.id().equals(MY_PLUGIN_ID))         { … }

   // after
   if (other instanceof QuickViewNuclrPlugin)   { … }
   if (other instanceof MyFilePanelPlugin)      { … }
   ```
   Note the second rewrite is narrower than the original: it matches your own class (and its subclasses) rather than anything that happened to share a plugin id.

## License 📄

Apache-2.0
