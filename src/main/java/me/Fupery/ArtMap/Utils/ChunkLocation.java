package me.Fupery.ArtMap.Utils;

import org.bukkit.Chunk;

public class ChunkLocation {
    final int x, z;

    public ChunkLocation(Chunk chunk) {
        this.x = chunk.getX();
        this.z = chunk.getZ();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ChunkLocation)) return false;
        ChunkLocation chunkLocation = ((ChunkLocation) obj);
        return chunkLocation.x == x && chunkLocation.z == z;
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }
}
