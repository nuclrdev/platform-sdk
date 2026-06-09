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

/**
 * Application-wide event bus that decouples plugins from each other and from
 * the commander host. Events are identified by plain {@code String} type keys
 * and carry an untyped {@code Map} payload.
 *
 * <p>Listeners register via {@link #subscribe(NuclrEventListener)} and are
 * called synchronously on the emitting thread. The overloaded {@code emit}
 * methods cover common forms of the same broadcast API.
 */
public interface NuclrEventBus {

	/**
	 * Emit an event with a payload, reporting progress via the given callback.
	 *
	 * @param source   the object emitting the event, or {@code null}
	 * @param type     event type identifier
	 * @param event    event payload map
	 * @param callback progress and cancellation bridge
	 */
	void emit(Object source, String type, Map<String, Object> event, NuclrPluginCallback callback);

	/**
	 * Emit an event with a payload and no progress callback.
	 *
	 * @param source the object emitting the event, or {@code null}
	 * @param type   event type identifier
	 * @param event  event payload map
	 */
	void emit(Object source, String type, Map<String, Object> event);

	/**
	 * Emit an event with a payload and a callback, without specifying a source.
	 *
	 * @param type     event type identifier
	 * @param event    event payload map
	 * @param callback progress and cancellation bridge
	 */
	void emit(String type, Map<String, Object> event, NuclrPluginCallback callback);

	/**
	 * Emit a no-payload event with a callback.
	 *
	 * @param type     event type identifier
	 * @param callback progress and cancellation bridge
	 */
	void emit(String type, NuclrPluginCallback callback);

	/**
	 * Emit a fire-and-forget event with no payload and no callback.
	 *
	 * @param type event type identifier
	 */
	void emit(String type);

	/**
	 * Register a listener to receive future events.
	 *
	 * @param listener the listener to register
	 */
	void subscribe(NuclrEventListener listener);

	/**
	 * Remove a previously registered listener.
	 *
	 * @param listener the listener to remove
	 */
	void unsubscribe(NuclrEventListener listener);

}
