package dev.nuclr.plugin;

import lombok.Data;

@Data
public class PluginManifest {

	private int schemaVersion;
	private String name;
	private String id;
	private String version;
	private String description;
	private String author;
	private String license;
	private String website;
	private String pageUrl;
	private String docUrl;

}
