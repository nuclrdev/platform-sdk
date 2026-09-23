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

import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;

/**
 * Plugin type that renders a preview of a resource in the quick-view side
 * panel. Multiple plugins may be registered; among those whose
 * {@link #supports} accepts the resource, the one with the lowest
 * {@code priority} declared in {@code plugin.json} is chosen.
 */
public non-sealed interface QuickViewNuclrPlugin extends BaseNuclrPlugin {

	/**
	 * Return the Swing component that displays the preview.
	 *
	 * @return the plugin's root UI component, never {@code null}
	 */
	JComponent panel();

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

	/**
	 * Whether this plugin can draw still thumbnails through
	 * {@link #thumbnail(NuclrResource, int, int, AtomicBoolean)}.
	 *
	 * <p>Declared separately so a caller can skip instantiating a heavy viewer
	 * only to be handed {@code null}. A plugin that overrides {@code thumbnail}
	 * must override this too.
	 *
	 * @return {@code true} if thumbnails are supported; {@code false} by default
	 */
	default boolean supportsThumbnails() {
		return false;
	}

	/**
	 * Draw a still thumbnail of a resource - the first page, the cover, a frame -
	 * for a caller that wants a picture rather than a live panel, such as a chip
	 * beside a file name.
	 *
	 * <p>This is a separate, stateless path: it must not touch {@link #panel()},
	 * must not disturb the resource currently open, and may be called on any
	 * thread, concurrently, and before {@link #init()} has run. Implementations
	 * that cannot honour that should leave this alone.
	 *
	 * <p>Returning {@code null} means only that no picture was produced, for any
	 * reason; the caller is expected to fall back to an icon rather than treat it
	 * as an error.
	 *
	 * @param resource  the resource to draw
	 * @param maxWidth  the widest the result may be, in pixels
	 * @param maxHeight the tallest the result may be, in pixels
	 * @param cancelled flag set to {@code true} by the caller when the thumbnail
	 *                  is no longer wanted; check regularly and abort cleanly
	 * @return the thumbnail, no larger than the given box and preserving the
	 *         resource's aspect ratio, or {@code null} when none could be drawn
	 */
	default BufferedImage thumbnail(NuclrResource resource, int maxWidth, int maxHeight, AtomicBoolean cancelled) {
		return null;
	}

}
