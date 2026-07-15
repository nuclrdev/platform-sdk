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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import lombok.Data;

/**
 * Plugin type that provides a filesystem or resource browser pane (drives, S3,
 * Git, etc.). The commander displays two instances of this type side-by-side in
 * its dual-pane layout.
 */
public non-sealed interface FilePanelNuclrPlugin extends BaseNuclrPlugin {

	/** A single entry in the Alt+F1 / Alt+F2 drive-selector menu. */
	@Data
	public static class MenuItem {

		/** Human-readable label shown in the menu. */
		private String text;

		/** Resource that the commander navigates to when this item is chosen. */
		private NuclrResource path;

		/** Stable identifier for this menu item. */
		private String uuid;

		/** Creates a new {@code MenuItem} with all fields set to {@code null}. */
		public MenuItem() {}
	}

	/** Container returned by {@link FilePanelNuclrPlugin#getPluginMenuItems}. */
	@Data
	public static class MenuItemsHolder {

		private List<MenuItem> menuItems = List.of();

		private String title;

		/**
		 * Return the list of menu items, never {@code null}.
		 *
		 * @return the current list of drive/location entries
		 */
		public List<MenuItem> getMenuItems() {
			return menuItems;
		}

		/** Creates a new {@code MenuItemsHolder} with an empty item list. */
		public MenuItemsHolder() {}
	}

	/**
	 * Snapshot of the resource list returned by
	 * {@link FilePanelNuclrPlugin#openResource}.
	 */
	@Data
	public static class NuclrResourceData {

		private List<NuclrResource> entries = new ArrayList<>();

		private List<String> columnNames = new ArrayList<>();

		/** Creates a new, empty {@code NuclrResourceData}. */
		public NuclrResourceData() {}

		/**
		 * Return the resource at the given row index.
		 *
		 * @param rowIndex zero-based row index
		 * @return the resource at that row
		 */
		public NuclrResource getEntryAt(int rowIndex) {
			return entries.get(rowIndex);
		}

		/**
		 * Return the total number of resource entries.
		 *
		 * @return entry count
		 */
		public int getEntriesCount() {
			return entries.size();
		}

		/**
		 * Return the number of display columns.
		 *
		 * @return column count
		 */
		public int getColumnCount() {
			return columnNames.size();
		}

		/**
		 * Return the display name of the column at the given index.
		 *
		 * @param columnIndex zero-based column index
		 * @return column header label
		 */
		public String getColumnName(int columnIndex) {
			return columnNames.get(columnIndex);
		}

	}

	/**
	 * Open or refresh the view for the given resource. Heavy work must be done
	 * asynchronously; UI updates must be dispatched to the EDT. Return the list of
	 * child resources to display in the file panel, or {@code null}/empty if this
	 * plugin does not recognise the resource.
	 *
	 * @param resourceToOpen the resource to open or navigate to
	 * @param cancelled      flag set to {@code true} by the commander when the
	 *                       user cancels; check regularly and abort cleanly
	 * @return the resource data (entries + column definitions) for the panel, or
	 *         {@code null} if the resource is not handled by this plugin
	 */
	NuclrResourceData openResource(NuclrResource resourceToOpen, AtomicBoolean cancelled);

	/**
	 * Receives directory entries as they are discovered, so the commander can paint
	 * a folder incrementally instead of waiting for the whole listing to complete.
	 *
	 * <p>Implementations of {@link FilePanelNuclrPlugin} push to this sink from
	 * {@link FilePanelNuclrPlugin#openResource(NuclrResource, AtomicBoolean, EntrySink)};
	 * the commander supplies the sink and marshals every call onto the event
	 * dispatch thread.
	 */
	interface EntrySink {

		/**
		 * Declare the column headers for the listing. Call once, before any
		 * {@link #add(NuclrResource)}.
		 *
		 * @param columnNames ordered column labels
		 */
		void columns(List<String> columnNames);

		/**
		 * Publish a single entry for immediate display.
		 *
		 * @param entry the freshly discovered resource
		 */
		void add(NuclrResource entry);
	}

	/**
	 * Streaming variant of {@link #openResource(NuclrResource, AtomicBoolean)}:
	 * entries are pushed to {@code sink} as they are discovered so the panel can
	 * render them progressively. The returned {@link NuclrResourceData} is still the
	 * complete listing, used by the commander for final sorting and cursor
	 * placement.
	 *
	 * <p>The default implementation simply calls the blocking
	 * {@link #openResource(NuclrResource, AtomicBoolean)} and replays its result
	 * into the sink, so plugins that do not override it keep working (their entries
	 * just appear in a single batch when the read completes).
	 *
	 * @param resourceToOpen the resource to open or navigate to
	 * @param cancelled      flag set to {@code true} by the commander when the user
	 *                       cancels; check regularly and abort cleanly
	 * @param sink           receives column definitions and entries as they are
	 *                       discovered; may be {@code null}
	 * @return the complete resource data for the panel, or {@code null} if the
	 *         resource is not handled by this plugin
	 */
	default NuclrResourceData openResource(NuclrResource resourceToOpen, AtomicBoolean cancelled, EntrySink sink) {
		NuclrResourceData data = openResource(resourceToOpen, cancelled);
		if (data != null && sink != null) {
			sink.columns(data.getColumnNames());
			for (NuclrResource entry : data.getEntries()) {
				sink.add(entry);
			}
		}
		return data;
	}

	/**
	 * Return the list of identifiers displayed in Commander on Alt+F1 / Alt+F2.
	 * For a local file system plugin these could be "C:", "D:", etc. For a Git
	 * plugin this could be "Git". Return {@code null} to suppress the menu.
	 *
	 * @return a holder containing the menu items, or {@code null} if not supported
	 */
	default MenuItemsHolder getPluginMenuItems() {
		return null;
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
	 * Return text to display in the location bar for the current resource (e.g.
	 * the full path of the current directory, or branch+path for a Git plugin).
	 *
	 * @return human-readable location string, never {@code null}
	 */
	String getCurrentLocationDisplayText();

	/**
	 * Return text to display in the selection summary bar for the given selection
	 * (e.g. "3 items selected, 2.5 GB total" for a local filesystem plugin).
	 *
	 * @param selectedResources the currently selected resources; never {@code null}
	 * @return human-readable selection summary, never {@code null}
	 */
	String getSelectionSummaryText(List<NuclrResource> selectedResources);

	/**
	 * Open an interactive shell rooted at the panel's current location, to be
	 * rendered in the commander's embedded console (Ctrl+O).
	 *
	 * <p>Return {@code null} — the default — and the commander opens its own
	 * local OS shell instead, exactly as it does today. Plugins that browse the
	 * local filesystem want precisely that and need not implement this method;
	 * plugins that browse somewhere a local shell cannot reach (a server over
	 * SSH, a container, a remote bucket) return a session that speaks to that
	 * place, so Ctrl+O lands the user where the panel already is.
	 *
	 * <p>Called off the event dispatch thread and allowed to block: connecting,
	 * authenticating and prompting the user for credentials are all fair game
	 * here. Throw {@link IOException} when a shell was intended but could not be
	 * started — the commander then opens no console at all, on the assumption
	 * the plugin has already told the user what went wrong. That is the
	 * difference between the two "no session" outcomes: {@code null} means "I
	 * have no shell to offer here, use the local one", an exception means "mine
	 * failed".
	 *
	 * @param cwd     the resource the panel is currently showing; the shell
	 *                should start here
	 * @param columns the terminal's initial width in character cells
	 * @param rows    the terminal's initial height in character cells
	 * @return the shell session, or {@code null} to fall back to a local shell
	 * @throws IOException if this plugin's shell could not be started
	 */
	default NuclrTerminalSession openTerminal(NuclrResource cwd, int columns, int rows) throws IOException {
		return null;
	}

	/**
	 * Recursively walk all descendants of the given resource, invoking the visitor
	 * for each. Heavy/slow transport work; honor the cancelled flag. Used e.g. by
	 * the quick-folder-size plugin to sum sizes lazily.
	 *
	 * @param resource  the root resource whose descendants to walk
	 * @param visitor   called once for each descendant
	 * @param cancelled flag set by the commander when the user cancels; check
	 *                  regularly and stop cleanly when {@code true}
	 * @param recursive if {@code false}, only direct children are visited
	 * @throws IOException if an I/O error occurs during traversal
	 */
	default void walkDescendants(NuclrResource resource, Consumer<NuclrResource> visitor, AtomicBoolean cancelled, boolean recursive)
			throws IOException {
		throw new IOException("walkDescendants not implemented for this plugin");
	}

	@Override
	default Type type() {
		return Type.FilePanel;
	}

}
