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

import javax.swing.JComponent;

/**
 * Plugin type that renders a preview of a resource in the quick-view side
 * panel. Multiple plugins may be registered; the one with the lowest
 * {@link #priority()} that {@link #supports} the resource is chosen.
 */
public non-sealed interface QuickViewNuclrPlugin extends BaseNuclrPlugin {

	/**
	 * Return the Swing component that displays the preview.
	 *
	 * @return the plugin's root UI component, never {@code null}
	 */
	JComponent panel();

	/**
	 * Return the selection priority for this provider. When multiple plugins
	 * support the same resource, the one with the <em>lowest</em> priority
	 * value is preferred.
	 *
	 * @return priority value; lower means higher preference
	 */
	int priority();

	/** {@inheritDoc} */
	default Type type() {
		return Type.QuickView;
	}

	/**
	 * Open or refresh the preview for the given resource. Heavy work must be
	 * done asynchronously; UI updates must be dispatched to the EDT.
	 *
	 * @param resource  the resource to preview
	 * @param cancelled flag set to {@code true} by the commander when the user
	 *                  cancels; check regularly and abort cleanly
	 * @return {@code true} if the resource was recognised and the preview was
	 *         started
	 */
	boolean openResource(NuclrResource resource, AtomicBoolean cancelled);

}
