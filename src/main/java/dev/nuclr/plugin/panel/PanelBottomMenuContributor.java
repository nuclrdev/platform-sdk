package dev.nuclr.plugin.panel;

import java.util.List;

/**
 * Optional provider hook for context-sensitive bottom function-key menu
 * contributions.
 */
public interface PanelBottomMenuContributor {

    /**
     * Called when panel context changes.
     */
    default List<BottomMenuItem> menuItems(PanelContext context) {
        return List.of();
    }
}
