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

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import dev.nuclr.platform.NuclrThemeScheme;

/**
 * Root interface for all Nuclr plugins. Sealed to exactly three permitted
 * subtypes: {@link QuickViewNuclrPlugin}, {@link FilePanelNuclrPlugin}, and
 * {@link FullscreenNuclrPlugin}.
 *
 * <p>Plugin lifecycle (in call order):
 * <ol>
 *   <li>{@link #preinit(NuclrPluginContext)} — receive platform services</li>
 *   <li>{@link #init()} — setup; context is available</li>
 *   <li>{@code openResource(NuclrResource, AtomicBoolean)} — called repeatedly; must be async</li>
 *   <li>{@link #updateTheme(NuclrThemeScheme)} — optional</li>
 *   <li>{@link #unload()} — cleanup on shutdown</li>
 * </ol>
 */
public sealed interface BaseNuclrPlugin permits QuickViewNuclrPlugin, FilePanelNuclrPlugin, FullscreenNuclrPlugin {

	/** Identifies the UI slot a plugin occupies. */
	public static enum Type {
		/** Plugin renders in the quick-view side panel. */
		QuickView,
		/** Plugin renders in one of the two file-browser panes. */
		FilePanel,
		/** Plugin takes over the full commander window. */
		Fullscreen
	}

	/** Indicates whether a plugin is maintained by the Nuclr team or the community. */
	public static enum Developer {
		/** Published and maintained by the Nuclr team. */
		Official,
		/** Published by a third-party contributor. */
		Community
	}

	/**
	 * Return the type of this plugin, which determines the UI slot it occupies.
	 *
	 * @return the plugin type, never {@code null}
	 */
	Type type();

	/**
	 * Return the unique plugin identifier (e.g. {@code "com.example.myplugin"}).
	 *
	 * @return plugin id, never {@code null}
	 */
	String id();

	/**
	 * Return the human-readable plugin name shown in the plugin manager.
	 *
	 * @return plugin display name, never {@code null}
	 */
	String name();

	/**
	 * Return the semver version string (e.g. {@code "1.0.0"}).
	 *
	 * @return version string, never {@code null}
	 */
	String version();

	/**
	 * Return a short description of what the plugin does.
	 *
	 * @return plugin description, never {@code null}
	 */
	String description();

	/**
	 * Return the name of the plugin author.
	 *
	 * @return author name, never {@code null}
	 */
	String author();

	/**
	 * Return the SPDX license identifier for this plugin (e.g. {@code "Apache-2.0"}).
	 *
	 * @return license identifier, never {@code null}
	 */
	String license();

	/**
	 * Return the URL of the plugin's home page.
	 *
	 * @return website URL, or {@code null} if not set
	 */
	String website();

	/**
	 * Return the URL of the plugin's marketplace/listing page.
	 *
	 * @return marketplace page URL, or {@code null} if not set
	 */
	String pageUrl();

	/**
	 * Return the URL of the plugin's documentation.
	 *
	 * @return documentation URL, or {@code null} if not set
	 */
	String docUrl();

	/**
	 * Return the title to display in the plugin's window (e.g. the tab title for a
	 * file-panel plugin). May include the current file name but should not include
	 * frequently changing state such as a "Modified" indicator.
	 *
	 * @return window/tab title, or {@code null} to use the plugin name
	 */
	default String getWindowTitle() {
		return null;
	}

	/**
	 * Return whether this plugin is published by the Nuclr team or a community
	 * contributor.
	 *
	 * @return developer category, never {@code null}
	 */
	Developer developer();

	/**
	 * Attempt to give focus to the plugin's UI component.
	 *
	 * @return {@code true} if the component accepted focus
	 */
	boolean onFocusGained();

	/**
	 * Notify the plugin that it has lost focus so it can update its visual state.
	 */
	void onFocusLost();

	/**
	 * Return {@code true} if the plugin's UI component currently holds focus.
	 *
	 * @return {@code true} if focused
	 */
	boolean isFocused();

	/**
	 * Called before {@link #init()}. Provides the plugin with the platform context
	 * it needs to access services such as the event bus and settings store.
	 *
	 * @param context the platform context; store it for later use in {@link #init()}
	 */
	void preinit(NuclrPluginContext context);

	/**
	 * Return the plugin context received during {@link #preinit}. Guaranteed to be
	 * non-null after {@code preinit} has been called.
	 *
	 * @return the plugin context, never {@code null} after preinit
	 */
	NuclrPluginContext getContext();

	/**
	 * Initialise the plugin. Called after {@link #preinit}; the context is
	 * available and may be used safely here.
	 */
	void init();

	/**
	 * Called when the user changes the UI theme. The plugin should update its
	 * colours and fonts accordingly.
	 *
	 * @param themeScheme the new theme palette
	 */
	default void updateTheme(NuclrThemeScheme themeScheme) {
		// default implementation does nothing, plugins can override if needed
	}

	/**
	 * Return {@code true} if at most one instance of this plugin may exist at a
	 * time. Return {@code false} for plugins that support multiple simultaneous
	 * instances (each must then return a distinct {@link #uuid()}).
	 *
	 * @return {@code true} by default
	 */
	default boolean singleton() {
		return true;
	}

	/**
	 * Return the unique identifier for this plugin instance. For singleton plugins
	 * this is typically a fixed constant; for multi-instance plugins it must be a
	 * freshly generated UUID per instance.
	 *
	 * @return instance UUID, never {@code null}
	 */
	String uuid();

	/**
	 * Release all global resources held by this plugin. The plugin will not be
	 * used again after this call.
	 */
	void unload();

	/**
	 * Close the currently open resource, if any.
	 */
	void closeResource();

	/**
	 * Return the resource currently open in this plugin, or {@code null} if none.
	 *
	 * @return current resource, or {@code null}
	 */
	NuclrResource getCurrentResource();

	/**
	 * Return {@code true} if this plugin can open the given resource.
	 *
	 * @param resource the resource to check
	 * @return {@code true} if this plugin supports the given resource
	 */
	boolean supports(NuclrResource resource);

	/**
	 * Return {@code true} if this plugin is of the given type. Convenience
	 * alternative to comparing {@link #type()} with the enum constant directly.
	 *
	 * @param type the type to test against
	 * @return {@code true} if {@link #type()} equals {@code type}
	 */
	default boolean is(Type type) {
		return type() == type;
	}

	/**
	 * Cast this plugin to {@link FilePanelNuclrPlugin}.
	 *
	 * @return this plugin as a {@link FilePanelNuclrPlugin}
	 * @throws ClassCastException if this plugin is not a file-panel plugin
	 */
	default FilePanelNuclrPlugin asFilePanel() {
		return (FilePanelNuclrPlugin) this;
	}

	/**
	 * Cast this plugin to {@link QuickViewNuclrPlugin}.
	 *
	 * @return this plugin as a {@link QuickViewNuclrPlugin}
	 * @throws ClassCastException if this plugin is not a quick-view plugin
	 */
	default QuickViewNuclrPlugin asQuickView() {
		return (QuickViewNuclrPlugin) this;
	}

	/**
	 * Cast this plugin to {@link FullscreenNuclrPlugin}.
	 *
	 * @return this plugin as a {@link FullscreenNuclrPlugin}
	 * @throws ClassCastException if this plugin is not a fullscreen plugin
	 */
	default FullscreenNuclrPlugin asFullscreen() {
		return (FullscreenNuclrPlugin) this;
	}
	
	/**
	 * Return the context-menu entries to show when the user right-clicks an entry
	 * (or a multi-selection) in this plugin's panel. Return an empty list (the
	 * default) to suppress the menu entirely.
	 *
	 * <p>This is a pure, fast query &mdash; it is called on the live plugin instance
	 * on the EDT each time the menu is about to pop up, so do not perform blocking
	 * I/O here. Decide entries from the supplied resources alone. When the user
	 * chooses an item, Commander dispatches it through {@link #act} using the item's
	 * {@link NuclrContextMenuItem} actionType; that is where the
	 * (possibly slow, cancellable) work belongs.
	 *
	 * @param focusedResource   the entry under the cursor / with focus; may be
	 *                          {@code null} if the click was on empty panel space
	 * @param selectedResources all currently selected resources; never {@code null},
	 *                          may be empty. For a single right-click this is
	 *                          typically the one focused resource.
	 * @return ordered list of menu items (supports separators and submenus); never
	 *         {@code null}
	 */
	default List<NuclrContextMenuItem> contextMenuItems(
			NuclrResource focusedResource,
			List<NuclrResource> selectedResources) {
		return List.of();
	}

	/**
	 * Perform an action on this plugin. Called when the user activates a button or
	 * context-menu item associated with this plugin. The action type and payload
	 * are determined by the UI element that was activated.
	 *
	 * @param other             the other plugin involved in this action, if any
	 *                          (e.g. the source file-panel for a copy action);
	 *                          {@code null} if not applicable
	 * @param actionType        string identifier of the action to perform
	 * @param selectedResources all currently selected resources; may be empty
	 * @param focusedResource   the resource under the cursor / with focus;
	 *                          may be {@code null}
	 * @param data              additional event data; keys and value types are
	 *                          contract of the specific UI element
	 * @param callback          progress and cancellation bridge; call
	 *                          {@link NuclrPluginCallback#isCancelled()} regularly
	 *                          and abort cleanly when it returns {@code true}
	 */
	default void act(
		BaseNuclrPlugin other, 
		String actionType,
		List<NuclrResource> selectedResources,
		NuclrResource focusedResource,
		Map<String, Object> data, 
		NuclrPluginCallback callback) {
		// default implementation does nothing, plugins can override if needed
	}
}
