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
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public abstract class NuclrResource {

	protected Map<String, Object> metadata = new HashMap<>();

	public abstract String getUuid();

	public abstract String getName();

	public abstract String getFullPath();

	public abstract LocalDateTime getCreateDateTime();

	public abstract LocalDateTime getLastModifiedDateTime();

	public abstract LocalDateTime getLastAccessDateTime();

	public abstract boolean isFolder();

	public abstract boolean isSystem();

	public abstract boolean isHidden();

	public abstract boolean isParent();
	
	public abstract boolean isLink();
	
	public abstract long getLength();
	
	public InputStream openInputStream() {
		throw new UnsupportedOperationException();
	}

	public abstract String getColumnValue(int columnIndex);

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

}