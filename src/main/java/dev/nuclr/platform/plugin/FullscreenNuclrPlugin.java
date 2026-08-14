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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;

/**
 * Plugin type that occupies the full commander window, used for viewers and
 * editors.
 */
public non-sealed interface FullscreenNuclrPlugin extends BaseNuclrPlugin {

	/**
	 * Indicates whether the plugin presents a read-only view or can modify files.
	 */
	public static enum Role {
		/** The plugin only displays the resource without modification. */
		Viewer,
		/** The plugin can modify the resource. */
		Editor
	}

	/**
	 * Return context-menu items for the given resource, or an empty list if none.
	 *
	 * @param resource the resource being right-clicked
	 * @return ordered list of menu items, never {@code null}
	 */
	default List<NuclrMenuResource> menuItems(NuclrResource resource) {
		return List.of();
	}

	/**
	 * Return the Swing component that displays the plugin's UI.
	 *
	 * @return the plugin's root UI component, never {@code null}
	 */
	JComponent panel();

	/**
	 * Open or refresh the view for the given resource. Heavy work must be done
	 * asynchronously; UI updates must be dispatched to the EDT.
	 *
	 * @param resource  the resource to open
	 * @param cancelled flag set to {@code true} by the commander when the user
	 *                  cancels; check regularly and abort cleanly
	 * @return {@code true} if the resource was recognised and opened by this plugin
	 */
	boolean openResource(NuclrResource resource, AtomicBoolean cancelled);
}
