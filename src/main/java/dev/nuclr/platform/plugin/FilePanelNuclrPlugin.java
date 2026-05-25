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

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.Data;

public non-sealed interface FilePanelNuclrPlugin extends BaseNuclrPlugin {

	@Data
	public static class PluginRoot {
		private String text;
		private NuclrResource path;
		private String uuid;
	}

	@Data
	public static class PluginRoots {
		private List<PluginRoot> roots = List.of();
		private String title;
	}
	
	@Data
	public static class Entries {
		private List<NuclrResource> entries = List.of();
		private List<String> columnNames = List.of();
		public String getValueAt(int rowIndex, int columnIndex) {
			var entry = entries.get(rowIndex);
			return entry.getColumnValue(columnIndex);
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
	Entries openResource(NuclrResource resource, AtomicBoolean cancelled);

	/**
	 * Return list of identifiers that will be displayed in Commander on Alt + F1 /
	 * Alt + F2. For example, for a local file system plugin, this could be "C:",
	 * "D:", etc. For a git plugin, this could be "Git", for a GCP plugin, this is
	 * just: "GCP", etc.
	 */
	PluginRoots getPluginRoots();

	/** Return menu items for the given resource, or null/empty if none. */
	default List<NuclrMenuResource> menuItems(NuclrResource resource) {
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
	String getSelectionSummaryText(List<NuclrResource> selectedResources);

	// -------------------------------------------------------------------------
	// Write-capability (copy / move / delete)
	// -------------------------------------------------------------------------

	/**
	 * Return {@code true} if this plugin supports write operations (copy-in,
	 * delete, create folder, rename). The commander uses this to enable or disable
	 * F5 Copy, F6 Move, and F8 Delete in the UI for this panel.
	 *
	 * <p>Default: {@code false}. Override and return {@code true} together with
	 * implementing the relevant write methods below.
	 */
	default boolean supportsWrite() {
		return false;
	}

	/**
	 * Delete the given resources from this plugin's storage.
	 *
	 * <p>Implementations should delete folders recursively and poll
	 * {@link NuclrTransferCallback#isCancelled()} between items.
	 *
	 * @param resources resources to delete
	 * @param callback  progress and cancellation bridge
	 * @throws UnsupportedOperationException if this plugin is read-only
	 */
	default void delete(List<NuclrResource> resources, NuclrTransferCallback callback) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Write a single file into {@code destinationFolder} under the given
	 * {@code name}, reading its content from {@code data}.
	 *
	 * <p>This is the universal cross-plugin write path. The commander calls it on
	 * the <em>destination</em> plugin, passing an {@link InputStream} obtained from
	 * {@link NuclrResource#openInputStream()} on the source resource. The
	 * destination plugin reads the stream and persists it in its own storage.
	 *
	 * <p>Implementations should call {@link NuclrTransferCallback#onProgress} at
	 * regular intervals and stop writing if {@link NuclrTransferCallback#isCancelled()}
	 * returns {@code true}.
	 *
	 * @param destinationFolder the folder inside this plugin where the file is created
	 * @param name              the target filename (typically {@link NuclrResource#getName()}
	 *                          from the source resource)
	 * @param data              content stream — caller is responsible for closing it
	 * @param callback          progress and cancellation bridge
	 * @return the newly created resource
	 * @throws UnsupportedOperationException if this plugin is read-only
	 */
	default NuclrResource createResource(NuclrResource destinationFolder, String name,
			InputStream data, NuclrTransferCallback callback) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Create an empty folder named {@code name} inside {@code parent}.
	 *
	 * @param parent the parent folder
	 * @param name   the new folder name
	 * @return the newly created folder resource
	 * @throws UnsupportedOperationException if this plugin is read-only
	 */
	default NuclrResource createFolder(NuclrResource parent, String name) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Rename {@code resource} to {@code newName} in place. Used by the commander
	 * for in-panel rename (Shift+F6) and single-plugin move operations.
	 *
	 * @param resource the resource to rename
	 * @param newName  the new name (without path)
	 * @return the resource reflecting the new name
	 * @throws UnsupportedOperationException if this plugin is read-only
	 */
	default NuclrResource rename(NuclrResource resource, String newName) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Optimised bulk copy entirely within this plugin's storage — for example,
	 * a server-side S3 copy or {@code Files.copy()} for a local filesystem plugin.
	 *
	 * <p>The commander calls this instead of the stream-based
	 * {@link #createResource} path when both the source and destination panels
	 * belong to the <em>same</em> plugin instance (or at least the same plugin
	 * type, at the commander's discretion). Implementing this method is optional;
	 * the commander falls back to the {@link #createResource} stream path when
	 * this method throws {@link UnsupportedOperationException}.
	 *
	 * @param sources           resources to copy (may include folders — recurse as needed)
	 * @param destinationFolder target folder within this plugin
	 * @param callback          progress and cancellation bridge
	 * @throws UnsupportedOperationException if no optimised path is available
	 */
	default void copyWithin(List<NuclrResource> sources, NuclrResource destinationFolder,
			NuclrTransferCallback callback) {
		throw new UnsupportedOperationException();
	}

	@Override
	default Type type() {
		return Type.FilePanel;
	}

}
