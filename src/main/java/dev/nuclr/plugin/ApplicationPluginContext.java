package dev.nuclr.plugin;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.nuclr.plugin.event.bus.PluginEventBus;

public interface ApplicationPluginContext {

	PluginEventBus getEventBus();
	
	Map<String, Object> getGlobalData();
	
	ObjectMapper getObjectMapper();

}
