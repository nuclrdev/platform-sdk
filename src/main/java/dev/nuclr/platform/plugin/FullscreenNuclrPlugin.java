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

import javax.swing.JComponent;

public non-sealed interface FullscreenNuclrPlugin extends BaseNuclrPlugin {

	/**
	 * Return the plugin's role: viewer (read-only) or editor (can modify files).
	 */
	public static enum Role {
		Viewer, Editor
	}

	/**
	 * Return the plugin's role: viewer (read-only) or editor (can modify files).
	 */
	Role role();

	/** Return menu items for the given resource, or null/empty if none. */
	default List<NuclrMenuResource> menuItems(NuclrResourcePath resource) {
		return List.of();
	}

	@Override
	default Type type() {
		return Type.Fullscreen;
	}

	/** Return a component to display */
	JComponent panel();
}
