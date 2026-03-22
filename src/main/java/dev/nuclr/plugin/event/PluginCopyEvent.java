package dev.nuclr.plugin.event;

import java.util.List;

import dev.nuclr.plugin.PanelProviderPlugin;
import dev.nuclr.plugin.PluginPathResource;

public class PluginCopyEvent extends PluginEvent {

	private final PanelProviderPlugin sourceProvider;
	private final List<PluginPathResource> sources;
	private boolean handled;

	public PluginCopyEvent(PanelProviderPlugin sourceProvider, List<PluginPathResource> sources) {
		this.sourceProvider = sourceProvider;
		this.sources = sources != null ? List.copyOf(sources) : List.of();
	}

	public PanelProviderPlugin getSourceProvider() {
		return sourceProvider;
	}

	public List<PluginPathResource> getSources() {
		return sources;
	}

	public boolean isHandled() {
		return handled;
	}

	public void setHandled(boolean handled) {
		this.handled = handled;
	}
}
