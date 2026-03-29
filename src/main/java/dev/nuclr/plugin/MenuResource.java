package dev.nuclr.plugin;

import lombok.Data;

@Data
public abstract class MenuResource {

	private String name;

	private String keyStroke;

	public abstract String getEventType();
}