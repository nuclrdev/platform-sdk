package dev.nuclr.platform.events;

import java.util.Map;

public interface NuclrEventListener {

	void handleMessage(String type, Map<String, Object> event);

	boolean isMessageSupported(String type);
}
