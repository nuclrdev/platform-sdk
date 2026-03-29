package dev.nuclr.platform.settings;

public interface Settings {

	void set(String namespace, String key, Object value);

	<T> T get(String namespace, String key);

	<T> T getOrDefault(String namespace, String key, T defaultValue);

}
