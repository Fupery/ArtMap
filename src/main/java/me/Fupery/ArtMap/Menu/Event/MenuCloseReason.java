package me.Fupery.ArtMap.Menu.Event;

public enum MenuCloseReason {
    /**
     * Used when a menu is closed in order to open a parent menu
     */
    DEATH(false),
    /**
     * Used when a menu is closed by a player through the menu
     */
    DONE(true),

    /**
     * Used when a menu is closed by a player by pressing escape
     */
    CLIENT(false),

    /**
     * Used by a child menu is closed in order to return to its parent menu
     */
    BACK(true),

    /**
     * Used when a menu is closed in order to open a linked menu
     */
    SWITCH(true),

    /**
     * Used when a menu closes because the player viewing it has quit the game
     */
    QUIT(false),

    /**
     * Used for menu-specific actions
     */
    SPECIAL(true),

    /**
     * Used by the system to close a menu forcibly
     */
    SYSTEM(true);

    private final boolean closeInventory;

    /**
     * @param closeInventory If true, the closed menu's linked inventory is closed
     */
    MenuCloseReason(boolean closeInventory) {
        this.closeInventory = closeInventory;
    }

    /**
     * @return True if the closed menu's linked inventory should be closed
     */
    public boolean shouldCloseInventory() {
        return closeInventory;
    }
}
