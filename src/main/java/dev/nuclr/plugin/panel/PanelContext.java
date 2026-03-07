package dev.nuclr.plugin.panel;

import java.nio.file.Path;

/**
 * Minimal panel state snapshot for plugin menu contribution.
 */
public record PanelContext(
        PanelSide side,
        Path currentDirectory,
        Path selectedPath,
        int selectedCount) {
}
