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

/**
 * Callback interface passed by the commander to plugin write operations
 * (copy, move, delete). Allows the commander to display progress in its UI
 * and signal cancellation to the plugin.
 *
 * <p>Implementations are provided by the commander host. Plugins must poll
 * {@link #isCancelled()} periodically during chunked writes or recursive
 * directory operations and abort cleanly when it returns {@code true}.
 *
 * <p>All methods may be called from any thread; implementations must be
 * thread-safe. UI updates must be dispatched to the EDT by the implementation.
 */
public interface NuclrTransferCallback {

	/**
	 * Called once when a transfer for {@code resource} is about to begin.
	 */
	void onStart(NuclrResource resource);

	/**
	 * Called periodically while bytes are being transferred for {@code resource}.
	 *
	 * @param resource         the resource being transferred
	 * @param bytesTransferred bytes transferred so far
	 * @param totalBytes       total expected bytes, or {@code -1} if unknown
	 */
	void onProgress(NuclrResource resource, long bytesTransferred, long totalBytes);

	/**
	 * Called when the transfer for {@code resource} has finished successfully.
	 */
	void onComplete(NuclrResource resource);

	/**
	 * Called when the transfer for {@code resource} has failed.
	 *
	 * @param resource the resource that could not be transferred
	 * @param e        the exception that caused the failure
	 */
	void onError(NuclrResource resource, Exception e);

	/**
	 * Return {@code true} if the user has requested cancellation. Plugins must
	 * check this regularly and stop transferring as soon as possible.
	 */
	boolean isCancelled();

}
