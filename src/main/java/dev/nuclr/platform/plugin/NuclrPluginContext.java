package dev.nuclr.platform.plugin;

import dev.nuclr.platform.NuclrSettings;
import dev.nuclr.platform.NuclrThemeScheme;
import dev.nuclr.platform.events.NuclrEventBus;

public interface NuclrPluginContext {

	NuclrEventBus getEventBus();

	NuclrThemeScheme getTheme();

	NuclrSettings getSettings();

}
