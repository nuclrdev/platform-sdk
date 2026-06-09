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
package dev.nuclr.platform;

/**
 * Persistent key-value settings store shared across all plugins. Keys are
 * scoped by a {@code namespace} string so that different plugins do not
 * collide even when using the same key name.
 */
public interface NuclrSettings {

	/**
	 * Store a value under the given namespace and key.
	 *
	 * @param namespace plugin-specific namespace (e.g. {@code "com.example.myplugin"})
	 * @param key       setting key within the namespace
	 * @param value     value to store; must be serialisable
	 */
	void set(String namespace, String key, Object value);

	/**
	 * Retrieve the value stored under the given namespace and key.
	 *
	 * @param <T>       the expected value type
	 * @param namespace plugin-specific namespace
	 * @param key       setting key within the namespace
	 * @return the stored value, or {@code null} if not set
	 */
	<T> T get(String namespace, String key);

	/**
	 * Retrieve the value stored under the given namespace and key, returning
	 * {@code defaultValue} if no value has been stored.
	 *
	 * @param <T>          the expected value type
	 * @param namespace    plugin-specific namespace
	 * @param key          setting key within the namespace
	 * @param defaultValue value to return when no setting is found
	 * @return the stored value, or {@code defaultValue}
	 */
	<T> T getOrDefault(String namespace, String key, T defaultValue);

	/**
	 * Return {@code true} when the commander is running in developer mode (e.g.
	 * extra logging, unsigned plugins allowed).
	 *
	 * @return {@code true} if developer mode is active
	 */
	boolean isDeveloperModeOn();

}
