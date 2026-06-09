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

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

/**
 * One entry in the right-click context menu shown over an item (or a
 * multi-selection) in a plugin's panel. Returned from
 * {@link BaseNuclrPlugin#contextMenuItems}.
 *
 * <p>When the user clicks the entry, Commander dispatches through
 * {@link BaseNuclrPlugin#act} with {@link #actionType} as the
 * {@code actionType}, the panel's current selection as {@code selectedResources}
 * and the right-clicked entry as {@code focusedResource}. The plugin therefore
 * needs no extra callback wiring &mdash; it just handles the action type in
 * {@code act}.
 *
 * <p>Build with the Lombok builder, e.g.:
 * <pre>
 * NuclrContextMenuItem.builder()
 *     .label("Clone repository")
 *     .actionType("github.clone")
 *     .build();
 *
 * NuclrContextMenuItem.separator();
 *
 * NuclrContextMenuItem.builder()
 *     .label("Copy")
 *     .child(NuclrContextMenuItem.builder().label("Copy URL").actionType("github.copyUrl").build())
 *     .child(NuclrContextMenuItem.builder().label("Copy SSH").actionType("github.copySsh").build())
 *     .build();
 * </pre>
 */
@Data
@Builder
public class NuclrContextMenuItem {

	/** Text shown in the menu. Ignored when {@link #separator} is {@code true}. */
	private String label;

	/**
	 * Identifier passed as the {@code actionType} to {@code act(...)} when this item
	 * is chosen. May be {@code null} for a separator or for a parent
	 * item that only groups {@link #children} (no action of its own).
	 */
	private String actionType;

	/** When {@code true} the item is rendered greyed-out and cannot be chosen. */
	@Builder.Default
	private boolean enabled = true;

	/** When {@code true} this item is a non-interactive divider line. */
	@Builder.Default
	private boolean separator = false;

	/**
	 * Hint that the action is destructive (delete, force-push, &hellip;). Commander
	 * may render it differently (e.g. red) and/or ask for confirmation.
	 */
	@Builder.Default
	private boolean destructive = false;

	/**
	 * Optional icon key resolved by Commander's icon theme (e.g. "delete", "copy").
	 * {@code null} for no icon.
	 */
	private String iconKey;

	/**
	 * Nested entries. When non-empty this item renders as a submenu; its own
	 * {@link #actionType} is typically {@code null}.
	 */
	@Singular
	private List<NuclrContextMenuItem> children;

	/**
	 * Convenience factory for a divider line.
	 *
	 * @return a new separator item
	 */
	public static NuclrContextMenuItem separator() {
		return NuclrContextMenuItem.builder().separator(true).build();
	}

	/**
	 * Return {@code true} if this item has nested children (i.e. renders as a
	 * submenu).
	 *
	 * @return {@code true} when {@link #children} is non-null and non-empty
	 */
	public boolean hasChildren() {
		return children != null && !children.isEmpty();
	}
}
