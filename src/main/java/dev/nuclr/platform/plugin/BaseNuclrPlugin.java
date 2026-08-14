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
	 * Return the title to display in the plugin's window (e.g. the tab title for a
	 * file-panel plugin). May include the current file name but should not include
	 * frequently changing state such as a "Modified" indicator.
	 *
	 * @return window/tab title, or {@code null} to use the name declared in
	 *         {@code plugin.json}
	 */
	default String getWindowTitle() {
		return null;
	}

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
	 * <p>
	 * The host may call this method from a background thread because capability
	 * detection can involve blocking filesystem or network access. Implementations
	 * must therefore be thread-safe, must not assume Swing's event-dispatch thread,
	 * and must not display modal UI. User-facing errors belong to the subsequent
	 * open operation or to the host after capability detection completes.
	 *
	 * @param resource the resource to check
	 * @return {@code true} if this plugin supports the given resource
	 */
	boolean supports(NuclrResource resource);

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
