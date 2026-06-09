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

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A single entry in a plugin's bottom-bar function-key menu (e.g. F5=Copy,
 * F6=Move). The commander renders these entries as clickable labels that
 * emit the associated {@link #eventType} on the event bus when activated.
 */
@Data
@AllArgsConstructor
public class NuclrMenuResource {

	/** Label shown in the function-key bar. */
	private String name;

	/** Function key label (e.g. {@code "F5"}, {@code "F6"}). */
	private String functionKey;

	/** Event type emitted on the bus when this entry is activated. */
	private String eventType;

	/** Creates a {@code NuclrMenuResource} with all fields set to {@code null}. */
	public NuclrMenuResource() {}

}
