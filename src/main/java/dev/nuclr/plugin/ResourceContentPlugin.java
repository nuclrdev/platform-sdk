package dev.nuclr.plugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;

import dev.nuclr.platform.plugin.NuclrPluginContext;

public interface ResourceContentPlugin {

	PluginManifest manifest();

	default List<PluginPathResource> getChangeDriveResources() {
		return null;
	}

	JComponent panel();

	boolean supports(PluginPathResource resource);

	/** Return menu items for the given resource, or null/empty if none. */
	default List<MenuResource> menuItems(PluginPathResource resource) {
		return null;
	}

	void load(NuclrPluginContext context);

	/** Plugin unload: release global resources. Provider will not be used again. */
	void unload();

	/** Open/refresh view for the item (do heavy work async, update UI on EDT). */
	boolean openResource(PluginPathResource resource, AtomicBoolean cancelled);

	/** Close current item/session (stop playback, cancel background tasks). */
	void closeResource();

	/** lower priority providers are preferred when multiple match the same item */
	int priority();

}
