package dev.nuclr.platform.plugin;

import java.util.Locale;

import dev.nuclr.platform.NuclrSettings;
import dev.nuclr.platform.NuclrThemeScheme;
import dev.nuclr.platform.events.NuclrEventBus;

/**
 * Platform services injected into every plugin during
 * {@link BaseNuclrPlugin#preinit}. All methods return non-null values once
 * {@code preinit} has completed.
 */
public interface NuclrPluginContext {

	/**
	 * Return the application-wide event bus.
	 *
	 * @return the event bus, never {@code null}
	 */
	NuclrEventBus getEventBus();

	/**
	 * Return the currently active UI theme palette.
	 *
	 * @return the theme scheme, never {@code null}
	 */
	NuclrThemeScheme getTheme();

	/**
	 * Return the persistent settings store.
	 *
	 * @return the settings instance, never {@code null}
	 */
	NuclrSettings getSettings();

	/**
	 * Return the locale currently selected in the commander UI.
	 *
	 * @return the active locale, never {@code null}
	 */
	Locale getLocale();

}
