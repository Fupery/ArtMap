package me.Fupery.ArtMap.Menu.API;

public enum StoragePattern {
    /**
     * Menu is not cached
     */
    JUST_IN_TIME,
    /**
     * Menu is cached for a shorter period of time
     */
    CACHED_WEAKLY,
    /**
     * Menu is cached for a longer period of time
     */
    CACHED_STRONGLY,
    /**
     * Menu is cached indefinitely
     */
    STATIC;
}
