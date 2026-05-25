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
 * Generic progress and cancellation bridge passed by the commander to every
 * {@link dev.nuclr.platform.events.NuclrEventListener#handleMessage} call.
 *
 * <p>The commander supplies a concrete implementation that drives its progress
 * bar, status line, and Cancel button. Plugins call these methods to keep the
 * UI responsive during any long-running synchronous operation — file transfers,
 * indexing, remote fetches, etc.
 *
 * <p>All methods may be called from any thread; the commander implementation
 * dispatches UI updates to the EDT internally. Plugins do not need to switch
 * threads before calling these methods.
 *
 * <h3>Typical usage inside handleMessage</h3>
 * <pre>{@code
 * public void handleMessage(Object source, String type,
 *                           Map<String, Object> event,
 *                           NuclrPluginCallback callback) {
 *     var sources = (List<NuclrResource>) event.get(NuclrFileOperationEvent.KEY_SOURCES);
 *     for (var r : sources) {
 *         if (callback.isCancelled()) break;
 *         callback.onStart(r.getName());
 *         // ... do work, report byte progress ...
 *         callback.onProgress(bytesDone, r.getSize());
 *         callback.onComplete();
 *     }
 * }
 * }</pre>
 */
public interface NuclrPluginCallback {

	/**
	 * Called once when processing of a new item begins.
	 *
	 * @param description human-readable label shown in the progress UI
	 *                    (e.g. the name of the file being copied or deleted)
	 */
	void onStart(String description);

	/**
	 * Called periodically to report progress within the current item.
	 *
	 * @param current bytes (or other units) completed so far
	 * @param total   expected total, or {@code -1} if unknown
	 */
	void onProgress(long current, long total);

	/**
	 * Called when the current item finished successfully.
	 */
	void onComplete();

	/**
	 * Called when the current item could not be processed.
	 *
	 * @param description label of the item that failed
	 * @param e           the exception that caused the failure
	 */
	void onError(String description, Exception e);

	/**
	 * Return {@code true} if the user pressed Cancel. Plugins must check this
	 * regularly and stop processing as soon as possible.
	 */
	boolean isCancelled();

}
