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
import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public abstract class NuclrResource {

	protected Map<String, Object> metadata = new HashMap<>();

	public abstract String getUuid();

	public abstract String getName();

	public InputStream openInputStream() {
		throw new UnsupportedOperationException();
	}

	public abstract boolean isFolder();

	public abstract String getColumnValue(int columnIndex);

	@Override
	public boolean equals(Object o) {
		return o instanceof NuclrResource p && getUuid().equals(p.getUuid());
	}

	@Override
	public int hashCode() {
		return getUuid().hashCode();
	}

}