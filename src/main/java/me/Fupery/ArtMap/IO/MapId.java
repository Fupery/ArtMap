package me.Fupery.ArtMap.IO;

public class MapId {
    protected final short id;
    protected final Integer hash;

    public MapId(short id, int hash) {
        this.id = id;
        this.hash = hash;
    }

    public short getId() {
        return id;
    }

    public Integer getHash() {
        return hash;
    }
}
