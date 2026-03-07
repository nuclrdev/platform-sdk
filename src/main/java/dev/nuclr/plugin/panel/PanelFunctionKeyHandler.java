package dev.nuclr.plugin.panel;

import java.nio.file.Path;

/**
 * Optional hook for provider-specific function-key handling when a panel is
 * currently inside provider-owned paths.
 */
public interface PanelFunctionKeyHandler {

    /**
     * @return {@code true} if the provider handled the key and default UI
     * behavior should be skipped.
     */
    boolean handle(
            int functionKeyNumber,
            boolean shiftDown,
            Path currentDirectory,
            Path selectedPath);

    PanelFunctionKeyHandler NONE = (functionKeyNumber, shiftDown, currentDirectory, selectedPath) -> false;
}
