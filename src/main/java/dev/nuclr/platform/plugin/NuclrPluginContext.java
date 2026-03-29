package dev.nuclr.platform.plugin;

import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

import dev.nuclr.platform.events.NuclrEventBus;
import dev.nuclr.platform.settings.Settings;

public interface NuclrPluginContext {

	NuclrEventBus getEventBus();
	
	Map<String, Object> getGlobalConfig();
	
	ObjectMapper getObjectMapper();
	
	Map<String, Object> getTheme();
	
	Settings getSettings();
	
}
