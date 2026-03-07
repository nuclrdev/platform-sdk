package dev.nuclr.plugin.panel;

/**
 * Function-key menu entry contribution for a panel context.
 */
public record BottomMenuItem(
        int functionKeyNumber,
        String label,
        String commandId,
        boolean enabled) {
}
