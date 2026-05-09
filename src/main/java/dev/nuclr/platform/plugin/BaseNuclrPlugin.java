/*

	Copyright 2026 Sergio, Nuclr (https://nuclr.dev)
	
	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at
	
	http://www.apache.org/licenses/LICENSE-2.0
	
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.

*/
package dev.nuclr.platform.plugin;

import java.util.concurrent.atomic.AtomicBoolean;

import dev.nuclr.platform.NuclrThemeScheme;
import dev.nuclr.platform.plugin.BaseNuclrPlugin.Type;

public sealed interface BaseNuclrPlugin permits QuickViewNuclrPlugin, FilePanelNuclrPlugin, FullscreenNuclrPlugin {

	public static enum Type {
		QuickView, FilePanel, Fullscreen
	}

	public static enum Developer {
		Official, Community
	}

	/**
	 * Return the type of this plugin. This is used to determine where the plugin
	 * should be displayed in the UI (e.g. QuickView plugins are displayed in the
	 * QuickView panel, FilePanel plugins are displayed in the file panel, etc.).
	 */
	Type type();

	String id();

	String name();

	/** Return a semver string (e.g. "1.0.0") */
	String version();

	String description();

	String author();

	String license();

	String website();

	String pageUrl();

	String docUrl();

	/**
	 * Return the developer of this plugin. This is used to determine if the plugin
	 * should be listed in the "Official" or "Community" sections of the plugin
	 * manager.
	 */
	Developer developer();

	/** Return true if the component accepts focus */
	boolean onFocusGained();

	void onFocusLost();

	boolean isFocused();

	void preinit(NuclrPluginContext context);
	
	/** Return the plugin context, which provides access to various services and
	 * resources that the plugin can use (e.g. event bus, theme manager, etc.). This
	 * is guaranteed to be non-null after preinit is called.
	 */
	NuclrPluginContext getContext();

	void init();

	/**
	 * Called when the user changes the theme. Plugin should update its colors
	 * accordingly.
	 */
	default void updateTheme(NuclrThemeScheme themeScheme) {
		// default implementation does nothing, plugins can override if needed
	}

	/**
	 * Return true if this plugin should only have one instance (e.g. a single
	 * viewer for a file type). If false, multiple instances can be opened (e.g.
	 * multiple viewers for the same file type).
	 */
	default boolean singleton() {
		return true;
	}

	/**
	 * This is the unique identifier for this plugin instanceFor non-singleton
	 * plugins, this should return a unique value (e.g. a random UUID).
	 */
	String uuid();

	/** Plugin unload: release global resources. Provider will not be used again. */
	void unload();

	/** Open/refresh view for the item (do heavy work async, update UI on EDT). */
	boolean openResource(NuclrResourcePath resource, AtomicBoolean cancelled);

	/** Close the currently open item, if any. */
	void closeResource();

	/** Return the currently open item, or null if none. */
	NuclrResourcePath getCurrentResource();

	/** Return true if this provider can open the given resource. */
	boolean supports(NuclrResourcePath resource);

	/**
	 * Return true if this plugin is of the given type. This is a convenience method
	 * that can be used to check the plugin type without having to compare against
	 * the enum value directly.
	 */
	default boolean is(Type type) {
		return type() == type;
	}

	default FilePanelNuclrPlugin asFilePanel() {
		return (FilePanelNuclrPlugin) this;
	}

	default QuickViewNuclrPlugin asQuickView() {
		return (QuickViewNuclrPlugin) this;
	}

	default FullscreenNuclrPlugin asFullscreen() {
		return (FullscreenNuclrPlugin) this;
	}

}
