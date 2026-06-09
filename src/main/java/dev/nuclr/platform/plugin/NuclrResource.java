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

import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * Abstract base class for all resources displayed in a plugin's file panel.
 * Equality and hashing are based solely on {@link #uuid}.
 *
 * <p>All fields are {@code protected}; Lombok {@code @Data} generates the
 * public getters and setters used by the commander at runtime.
 */
@Data
public abstract class NuclrResource implements Serializable {

	/** Arbitrary key-value metadata attached by the plugin. */
	protected Map<String, Object> metadata = new HashMap<>();

	/** Stable unique identifier for this resource instance. */
	protected String uuid;

	/** Display name of this resource (e.g. the file or directory name). */
	protected String name;

	/** Full path or URI of this resource as a string. */
	protected String fullPath;

	/** Creation time, or {@code null} if not available. */
	protected LocalDateTime createdDateTime;

	/** Last modification time, or {@code null} if not available. */
	protected LocalDateTime lastModifiedDateTime;

	/** Last access time, or {@code null} if not available. */
	protected LocalDateTime lastAccessDateTime;

	/** {@code true} if this resource is a container (directory, bucket, etc.). */
	protected boolean folder;

	/** {@code true} if this resource is a system-level entry. */
	protected boolean system;

	/** {@code true} if this resource is hidden from normal listings. */
	protected boolean hidden;

	/** {@code true} if this resource is a symbolic link or alias. */
	protected boolean link;

	/** {@code true} if the current user has read access to this resource. */
	protected boolean readable = true;

	/** Size of the resource in bytes, or {@code 0} if unknown or not applicable. */
	protected long length;

	/** Local filesystem path backing this resource, or {@code null} for remote resources. */
	protected Path path;

	/**
	 * Creates a resource backed by the given local path.
	 *
	 * @param path the local path for this resource, or {@code null} for remote resources
	 */
	public NuclrResource(Path path) {
		super();
		this.path = path;
	}

	/**
	 * Open an input stream to read the resource's content.
	 *
	 * <p>The default implementation throws {@link UnsupportedOperationException}.
	 * Subclasses that support streaming must override this method.
	 *
	 * @param options open options forwarded to the underlying storage layer
	 * @return an input stream for reading the resource
	 * @throws Exception if the stream cannot be opened
	 * @throws UnsupportedOperationException if this resource does not support streaming
	 */
	public InputStream openInputStream(OpenOption... options) throws Exception {
		throw new UnsupportedOperationException();
	}

	/**
	 * Retrieve a typed metadata value, returning {@code defaultValue} if the
	 * key is absent or the stored value is not assignable to the expected type.
	 *
	 * @param <T>          expected value type, inferred from {@code defaultValue}
	 * @param key          metadata key
	 * @param defaultValue fallback value; also used to determine the expected type
	 * @return the stored value cast to {@code T}, or {@code defaultValue}
	 */
	@SuppressWarnings("unchecked")
	public <T> T getMetadata(String key, T defaultValue) {
		Object value = metadata.get(key);

		if (value == null) {
			return defaultValue;
		}

		if (defaultValue != null && !defaultValue.getClass().isInstance(value)) {
			return defaultValue;
		}

		return (T) value;
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof NuclrResource p && getUuid().equals(p.getUuid());
	}

	@Override
	public int hashCode() {
		return getUuid().hashCode();
	}

	@Override
	public String toString() {
		return "NuclrResource [uuid=" + uuid + ", fullPath=" + fullPath + "]";
	}

}