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

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JComponent;

import dev.nuclr.platform.NuclrThemeScheme;

public interface NuclrPlugin {

	public static enum Developer {
		Official, Community
	}

	default List<NuclrResourcePath> getChangeDriveResources() {
		return List.of();
	}
	
	/** Return true if the component accepts focus */
	boolean onFocusGained();

	void onFocusLost();
	
	boolean isFocused();
	
	String id();

	String name();

	/** Return a semver string (e.g. "1.0.0") */
	String version();

	String description();

	String author();

	String license();

	String website();

	String pageUrl();

	String docUrl();

	Developer type();

	JComponent panel();

	boolean supports(NuclrResourcePath resource);

	/** Return menu items for the given resource, or null/empty if none. */
	default List<NuclrMenuResource> menuItems(NuclrResourcePath resource) {
		return List.of();
	}

	void load(NuclrPluginContext context);

	/** Plugin unload: release global resources. Provider will not be used again. */
	void unload();

	/** Open/refresh view for the item (do heavy work async, update UI on EDT). */
	boolean openResource(NuclrResourcePath resource, AtomicBoolean cancelled);

	/** Close current item/session (stop playback, cancel background tasks). */
	void closeResource();

	/** lower priority providers are preferred when multiple match the same item */
	int priority();

	/**
	 * Called when the user changes the theme. Plugin should update its colors
	 * accordingly.
	 */
	void updateTheme(NuclrThemeScheme themeScheme);

	/**
	 * Return true if this plugin should only have one instance (e.g. a single
	 * viewer for a file type). If false, multiple instances can be opened (e.g.
	 * multiple viewers for the same file type).
	 */
	default boolean singleton() {
		return true;
	}

}
