package dev.nuclr.plugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;

import dev.nuclr.platform.plugin.NuclrPluginContext;

public interface BasePlugin {

	PluginManifest getPluginManifest();

	List<PluginPathResource> getChangeDriveResources();

	JComponent getPanel();

	boolean supports(PluginPathResource resource);

	/** Return menu items for the given resource, or null/empty if none. */
	List<MenuResource> getMenuItems(PluginPathResource source);

	void load(NuclrPluginContext context);

	/** Plugin unload: release global resources. Provider will not be used again. */
	void unload();

	void onFocusGained();

	void onFocusLost();

	/** Open/refresh view for the item (do heavy work async, update UI on EDT). */
	boolean openItem(PluginPathResource resource, AtomicBoolean cancelled);
	
	/** Close current item/session (stop playback, cancel background tasks). */
	void closeItem();
	
	/** lower priority providers are preferred when multiple match the same item */
	int getPriority();

}
