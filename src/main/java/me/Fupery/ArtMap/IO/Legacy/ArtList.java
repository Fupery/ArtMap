package me.Fupery.ArtMap.IO.Legacy;

import me.Fupery.ArtMap.ArtMap;
import me.Fupery.ArtMap.IO.CompressedMap;
import me.Fupery.ArtMap.IO.Database.ArtTable;
import me.Fupery.ArtMap.IO.Database.MapTable;
import me.Fupery.ArtMap.IO.MapArt;

import java.util.ArrayList;
import java.util.List;

class ArtList {
    private final List<MapArt> artworks = new ArrayList<>();
    private final List<CompressedMap> maps = new ArrayList<>();

    List<MapArt> getArtworks() {
        return artworks;
    }

    List<CompressedMap> getMaps() {
        return maps;
    }

    boolean isEmpty() {
        return artworks.size() < 1 && maps.size() < 1;
    }

    void addArtworks() {
        if (artworks.size() > 0 || maps.size() > 0) {
            ArtTable artTable = ArtMap.getArtDatabase().getArtTable();
            MapTable mapTable = ArtMap.getArtDatabase().getMapTable();
            List<MapArt> failedArtworks = artTable.addArtworks(artworks);
            List<CompressedMap> failedMaps = mapTable.addMaps(maps);

            for (MapArt art : failedArtworks) {
                if (mapTable.containsMap(art.getMapId())) mapTable.deleteMap(art.getMapId());
                if (artworks.contains(art)) artworks.remove(art);
            }
            for (CompressedMap map : failedMaps) {
                if (artTable.containsMapID(map.getId())) artTable.deleteArtwork(map.getId());
            }
        }
    }
}
