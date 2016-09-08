package me.Fupery.ArtMap.IO;

import java.util.UUID;

public interface ArtDatabase {

    void close();

    MapArt getArtwork(String title);

    MapArt getArtwork(short mapData);

    boolean containsArtwork(MapArt art, boolean ignoreMapID);

    boolean containsMapID(short mapID);

    boolean deleteArtwork(String title);

    MapArt[] listMapArt(UUID artist);

    UUID[] listArtists(UUID player);

    void addArtwork(MapArt art);

    void addArtworks(MapArt... artworks);
}
