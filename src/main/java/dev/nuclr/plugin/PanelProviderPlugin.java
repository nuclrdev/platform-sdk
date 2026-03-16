package dev.nuclr.plugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface PanelProviderPlugin extends BasePlugin, FocusablePlugin {

	List<PluginPathResource> getChangeDriveResources();

	/** Open/refresh view for the item (do heavy work async, update UI on EDT). */
	boolean openItem(PluginPathResource resource, AtomicBoolean cancelled);

}
