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

import lombok.Data;

public non-sealed interface FilePanelNuclrPlugin extends BaseNuclrPlugin {

	@Data
	public static class PluginRoot {
		private String text;
		private NuclrResourcePath path;
		private String uuid;
	}

	/**
	 * Return list of identifiers that will be displayed in Commander on Alt + F1 /
	 * Alt + F2. For example, for a local file system plugin, this could be "C:",
	 * "D:", etc. For a git plugin, this could be "Git", for a GCP plugin, this is
	 * just: "GCP", etc.
	 */
	List<PluginRoot> getPluginRoots();

	/** Return menu items for the given resource, or null/empty if none. */
	default List<NuclrMenuResource> menuItems(NuclrResourcePath resource) {
		return List.of();
	}
	
	@Override
	default Type type() {
		return Type.FilePanel;
	}

}
