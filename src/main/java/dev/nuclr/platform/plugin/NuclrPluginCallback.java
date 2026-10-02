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

import java.util.Map;

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
 * <h2>Typical usage inside handleMessage</h2>
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
	 *
	 * @return {@code true} if the operation has been cancelled
	 */
	boolean isCancelled();

	/**
	 * Report the result of an action declared in
	 * {@link FilePanelNuclrPlugin#getActionsJson()}. The map must match the
	 * action's {@code output} schema; values are JSON-compatible: {@code String},
	 * {@code Number}, {@code Boolean}, {@code null}, {@code List} and {@code Map}.
	 * Call at most once per invocation, before the final {@link #onComplete()}.
	 * Callers that do not use results ignore it.
	 *
	 * @param result the action's result, never {@code null}
	 * @since 6.1.0
	 */
	default void onResult(Map<String, Object> result) {
		// default implementation ignores the result
	}

	/**
	 * Stream text produced while an action runs - a command's output, say - so it
	 * can be shown as it arrives instead of only in the final result. May be called
	 * any number of times; chunks are not necessarily whole lines.
	 *
	 * @param chunk the next piece of output, never {@code null}
	 * @since 6.1.0
	 */
	default void onOutput(String chunk) {
		// default implementation ignores output
	}

	/**
	 * Ask the user to approve a step that this plugin can judge with certainty -
	 * overwriting an existing file, for example - before taking it. Whether an
	 * action needs approval in general is the commander's decision, made from the
	 * action's declared {@code class}; do not try to classify the danger of
	 * arbitrary input here.
	 *
	 * <p>Blocks until the user answers, so never call it on the event dispatch
	 * thread. Never pass secrets through it: the text may be shown, logged and seen
	 * by the agent that requested the action.
	 *
	 * <p>Returns {@code true} only on explicit approval. Deny, closing the prompt,
	 * cancellation and callers that cannot ask anyone all return {@code false} -
	 * the default - so a step is never taken without a real "yes".
	 *
	 * @param title   one line saying what is about to happen, e.g.
	 *                "Overwrite /etc/nginx/nginx.conf on nuclr-web?"
	 * @param details plain text: where, why, and what cannot be undone; may be
	 *                {@code null}
	 * @param diff    the change as a unified diff (as produced by {@code diff -u}),
	 *                or {@code null} if there is nothing to show
	 * @return {@code true} if the user approved the step
	 * @since 6.1.0
	 */
	default boolean confirm(String title, String details, String diff) {
		return false;
	}

}
