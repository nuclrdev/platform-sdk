package dev.nuclr.platform.plugin;

/** 
 * This enum defines the different roles that a plugin can have in the Nuclr platform. 
 * Each role represents a different way that a plugin can interact with the user and the system.
 * 
 * QuickViewer: A plugin that provides a quick preview of a file or data without opening it in a full editor. 
 * FilePanel: A plugin that provides a panel in the file explorer to display additional information or actions for files. 
 * FullScreenViewer: A plugin that provides a full screen view of a file or data, typically for media files or documents. 
 * FullScreenEditor: A plugin that provides a full screen editor for creating or editing files, such as a text editor or image editor.
 */
public enum NuclrPluginRole {
	QuickViewer, FilePanel, FullScreenViewer, FullScreenEditor
}