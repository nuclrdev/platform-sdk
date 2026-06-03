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

@Data
public abstract class NuclrResource implements Serializable {

	protected Map<String, Object> metadata = new HashMap<>();

	protected String uuid;

	protected String name;

	protected String fullPath;

	protected LocalDateTime createdDateTime;

	protected LocalDateTime lastModifiedDateTime;

	protected LocalDateTime lastAccessDateTime;

	protected boolean folder;

	protected boolean system;

	protected boolean hidden;

	protected boolean link;

	protected long length;

	protected Path path;

	public NuclrResource(Path path) {
		super();
		this.path = path;
	}

	public InputStream openInputStream(OpenOption... options) throws Exception {
		throw new UnsupportedOperationException();
	}

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