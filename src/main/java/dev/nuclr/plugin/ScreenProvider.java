package dev.nuclr.plugin;

import java.nio.file.Path;

import javax.swing.JComponent;

/**
 * Provides a full-screen panel (center area) for a selected item/path.
 */
public interface ScreenProvider {

	/** Unique provider class name used as stable id in host logs. */
	String getPluginClass();

	/** Fast predicate to decide whether this provider can open the path. */
	boolean matches(Path path);

	/** Build or refresh the provider panel for the requested path. */
	JComponent open(Path path) throws Exception;

	/** Applies host theme before/after panel creation. */
	default void applyTheme(PluginTheme theme) {
	}

	/** Called when screen is being closed/switched. */
	default void close() {
	}

	/** Request focus for provider's primary interactive control. */
	default void focus() {
	}

	/** True when current screen content has unsaved changes. */
	default boolean isDirty() {
		return false;
	}

	/**
	 * Saves current content.
	 *
	 * @return true when data was persisted successfully
	 */
	default boolean save() throws Exception {
		return false;
	}

	/** Called when plugin is unloaded. */
	default void unload() {
	}

	/** Lower value = higher priority. */
	default int priority() {
		return 100;
	}
}
