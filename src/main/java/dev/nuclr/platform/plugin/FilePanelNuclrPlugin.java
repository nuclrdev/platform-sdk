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

import lombok.Data;

public non-sealed interface FilePanelNuclrPlugin<T extends NuclrResource> 
														extends BaseNuclrPlugin <T> {

	@Data
	public static class PluginRootMenuItem <T extends NuclrResource> {
		private String text;
		private T path;
		private String uuid;
	}

	@Data
	public static class PluginRootMenuItems <T extends NuclrResource> {
		private List<PluginRootMenuItem<T>> roots = List.of();
		private String title;
	}
	
	@Data
	public static class NuclrResourceData <T extends NuclrResource> {
		
		private List<T> entries = List.of();
		
		private List<String> columnNames = List.of();
		
		public String getValueAt(int rowIndex, int columnIndex) {
			var entry = entries.get(rowIndex);
			return entry.getColumnValue(columnIndex);
		}
		
		public T getEntryAt(int rowIndex) {
			return entries.get(rowIndex);
		}
		
		public int getEntriesCount() {
			return entries.size();
		}
		
		public int getColumnCount() {
			return columnNames.size();
		}
		
		public String getColumnName(int columnIndex) {
			return columnNames.get(columnIndex);
		}
		
	}
	

	/** 
	 * Open/refresh view for the item (do heavy work async, update UI on EDT). 
	 * And return list of children resources to be displayed in the file panel, or null/empty if the resource is not recognized by this plugin.
	 * 
	 * @param resource
	 * @param cancelled
	 * @return
	 */
	NuclrResourceData<T> openResource(T resource, AtomicBoolean cancelled);

	/**
	 * Return list of identifiers that will be displayed in Commander on Alt + F1 /
	 * Alt + F2. For example, for a local file system plugin, this could be "C:",
	 * "D:", etc. For a git plugin, this could be "Git", for a GCP plugin, this is
	 * just: "GCP", etc.
	 */
	PluginRootMenuItems<T> getPluginRootMenuItems();

	/** Return menu items for the given resource, or null/empty if none. */
	default List<NuclrMenuResource> menuItems(T resource) {
		return List.of();
	}

	/**
	 * Return text to be displayed in the location bar for the current resource. For
	 * example, for a local file system plugin, this could be the full path of the
	 * current directory. For a git plugin, this could be the current branch and
	 * path within the repository, etc.
	 */
	String getCurrentLocationDisplayText();

	/**
	 * Return text to be displayed in the selection summary bar for the given list
	 * of selected resources. For example, for a local file system plugin, this
	 * could be something like "3 items selected, 2.5 GB total". For a git plugin,
	 * this could be something like "2 items selected, 1 modified, 1 untracked",
	 * etc.
	 */
	String getSelectionSummaryText(List<T> selectedResources);

	@Override
	default Type type() {
		return Type.FilePanel;
	}

}
