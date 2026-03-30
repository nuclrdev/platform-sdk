# Nuclr Commander Platform SDK 🔌

Java SDK for building plugins for [Nuclr Commander](https://github.com/nuclrdev/nuclr-commander), a cross-platform dual-pane file manager.

This repository publishes the `dev.nuclr:platform-sdk` artifact used by Nuclr Commander plugins and is available from Maven Central.

## Requirements ✅

- Java 21+
- Maven 3.9+

## Maven Dependency 📦

Add the SDK to your plugin project:

```xml

<dependency>
    <groupId>dev.nuclr</groupId>
    <artifactId>platform-sdk</artifactId>
    <version>2.0.1</version>
</dependency>

```

Find the latest version here:

[https://central.sonatype.com/artifact/dev.nuclr/platform-sdk](https://central.sonatype.com/artifact/dev.nuclr/platform-sdk)

## What The SDK Provides 🚀

- `@NuclrPlugin` to mark plugin entry points
- `ResourceContentPlugin` for content viewers and editors
- `NuclrPluginContext` for access to the event bus, settings, theme data, config, and Jackson `ObjectMapper`
- `PluginPathResource` and `MenuResource` model types used by plugins

## Plugin Shape 🧩

Typical plugins implement `dev.nuclr.plugin.ResourceContentPlugin`, expose a Swing `JComponent` via `panel()`, and handle file/resource lifecycle through:

- `load(...)`
- `openResource(...)`
- `closeResource()`
- `unload()`

Plugins can also contribute resource-specific menu items and custom resources for drive or provider views.

## Minimal Example 💡

```java
@NuclrPlugin
public final class MyPlugin implements ResourceContentPlugin {

    // Implement panel(), supports(...), load(...), openResource(...),
    // closeResource(), unload(), and priority().
}
```

Commander discovers plugin metadata from `plugin.json`. Package that manifest together with your plugin classes and any runtime dependencies required by the plugin.

## License 📄

Apache-2.0
