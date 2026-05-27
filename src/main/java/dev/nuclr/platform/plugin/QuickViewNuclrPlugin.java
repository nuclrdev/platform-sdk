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

import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;

public non-sealed interface QuickViewNuclrPlugin <R extends NuclrResource> extends BaseNuclrPlugin<R> {

	/** Return a component to display in the quick view panel. */
	JComponent panel();

	/** lower priority providers are preferred when multiple match the same item */
	int priority();

	default Type type() {
		return Type.QuickView;
	}
	
	/** Open/refresh view for the item (do heavy work async, update UI on EDT). And return true if the resource is recognized by this plugin. */
	boolean openResource(R resource, AtomicBoolean cancelled);

}
