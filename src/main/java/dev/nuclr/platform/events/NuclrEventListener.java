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
package dev.nuclr.platform.events;

import java.util.Map;

import dev.nuclr.platform.plugin.NuclrPluginCallback;

public interface NuclrEventListener {

	/**
	 * Handle an event emitted on the {@link NuclrEventBus}.
	 *
	 * <p>This method is called <strong>synchronously</strong> on the thread that
	 * called {@link NuclrEventBus#emit}. Implementations should do their work
	 * directly here (no need to hand off to another thread) and use
	 * {@code callback} to report progress and check for cancellation.
	 *
	 * <p>Only called when {@link #isMessageSupported(String)} returns
	 * {@code true} for the given {@code type}.
	 *
	 * @param source   the object that emitted the event, or {@code null}
	 * @param type     the event type string (see e.g.
	 *                 {@link dev.nuclr.platform.plugin.NuclrFileOperationEvent})
	 * @param event    the event payload; keys and value types are contract of
	 *                 the specific event type
	 * @param callback progress and cancellation bridge provided by the commander;
	 *                 call {@link NuclrPluginCallback#isCancelled()} regularly and
	 *                 abort cleanly when it returns {@code true}
	 */
	void handleMessage(Object source, String type, Map<String, Object> eventData, NuclrPluginCallback callback);

	/**
	 * Return {@code true} if this listener handles events of the given
	 * {@code type}. The commander calls this before emitting an event to decide
	 * whether to enable the corresponding UI action.
	 */
	boolean isMessageSupported(String type);

}
