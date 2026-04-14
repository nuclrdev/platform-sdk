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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class NuclrResourcePath {

	protected Map<String, String> metadata = new HashMap<>();

	protected String uuid;

	protected String name;

	protected long sizeBytes;

	protected String extension;

	protected String mimeType; // optional, can be null

	protected Path path; // optional

	public NuclrResourcePath(Path path) {
		this.path = path;
	}

	public String getExtension() {
		return extension != null ? extension : path != null ? extensionFromPath() : "";
	}

	private String extensionFromPath() {

		if (path == null || path.getFileName() == null) {
			return "";
		}

		return path.getFileName().toString().contains(".")
				? path.getFileName().toString().substring(path.getFileName().toString().lastIndexOf('.') + 1)
				: "";
	}

	public InputStream openStream() throws IOException {
		if (path != null) {
			return java.nio.file.Files.newInputStream(path);
		} else {
			throw new UnsupportedOperationException("Cannot open stream for PluginPathResource without a path");
		}
	}

}