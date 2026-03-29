package dev.nuclr.platform.events;

import java.util.Map;

public interface NuclrEventBus {

	void emit(String type, Map<String, Object> event);

	void subscribe(NuclrEventListener listener);

	void unsubscribe(NuclrEventListener listener);

}
