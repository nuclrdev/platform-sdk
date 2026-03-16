package dev.nuclr.plugin;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Plugins for "quick view" items (e.g. media files, documents, PDFs, images, music tracks, 3d models, etc). 
 * 
 * These are opened in a single shared view, and the plugin is responsible for closing the previous item/session when a new one is opened.	
 */
public interface QuickViewProviderPlugin extends BasePlugin, FocusablePlugin {

	/** Fast check; avoid heavy IO here. */
	boolean supports(PluginPathResource resource);

	/** lower priority providers are preferred when multiple match the same item */
	int getPriority();

	/** Open/refresh view for the item (do heavy work async, update UI on EDT). */
	boolean openItem(PluginPathResource resource, AtomicBoolean cancelled);

	/** Close current item/session (stop playback, cancel background tasks). */
	void closeItem();

}
