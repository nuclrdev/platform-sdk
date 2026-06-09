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

import java.awt.Color;
import java.awt.Font;
import java.util.HashMap;
import java.util.Map;

import javax.swing.UIManager;

import lombok.Data;

/**
 * JSON-backed Swing/FlatLaf override palette.
 *
 * <p>
 * Keys are UIManager defaults (for example {@code "Panel.background"}). Values
 * are CSS-style hex colors (for example {@code "#102a43"}).
 */
@Data
public class NuclrThemeScheme {

	private String name;

	private Map<String, String> uiDefaults = new HashMap<>();

	/** Creates a {@code NuclrThemeScheme} with an empty palette and "Unnamed" as its name. */
	public NuclrThemeScheme() {
		name = name != null && !name.isBlank() ? name : "Unnamed";
		uiDefaults = uiDefaults != null ? Map.copyOf(uiDefaults) : Map.of();
	}

	/**
	 * Creates a {@code NuclrThemeScheme} with the given name and palette.
	 *
	 * @param name       display name for this theme; defaults to "Unnamed" if blank
	 * @param uiDefaults map of UIManager keys to CSS hex color strings; may be
	 *                   {@code null} (treated as empty)
	 */
	public NuclrThemeScheme(String name, Map<String, String> uiDefaults) {
		this.name = name != null && !name.isBlank() ? name : "Unnamed";
		this.uiDefaults = uiDefaults != null ? Map.copyOf(uiDefaults) : Map.of();
	}

	/**
	 * Look up a color from this theme's palette.
	 *
	 * @param string       UIManager key (e.g. {@code "Panel.background"})
	 * @param defaultColor fallback when the key is not in the palette
	 * @return the palette color for the key, or {@code defaultColor}
	 */
	public Color color(String string, Color defaultColor) {
		return uiDefaults.containsKey(string) ? Color.decode(uiDefaults.get(string)) : defaultColor;
	}

	/**
	 * Return the default UI font from the current Look-and-Feel.
	 *
	 * @return the default font, or {@code null} if not set by the L&amp;F
	 */
	public Font defaultFont() {
		return UIManager.getFont("defaultFont");
	}
}
