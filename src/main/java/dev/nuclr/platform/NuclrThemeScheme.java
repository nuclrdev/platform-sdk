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

/**
 * JSON-backed Swing/FlatLaf override palette.
 *
 * <p>
 * Keys are UIManager defaults (for example {@code "Panel.background"}). Values
 * are CSS-style hex colors (for example {@code "#102a43"}).
 */
public class NuclrThemeScheme {

	private String name;

	private Map<String, String> uiDefaults = new HashMap<>();

	public NuclrThemeScheme() {
		name = name != null && !name.isBlank() ? name : "Unnamed";
		uiDefaults = uiDefaults != null ? Map.copyOf(uiDefaults) : Map.of();
	}

	public Color color(String string, Color defaultColor) {
		return uiDefaults.containsKey(string) ? Color.decode(uiDefaults.get(string)) : defaultColor;
	}

	public Font defaultFont() {
		return UIManager.getFont("defaultFont");
	}
}
