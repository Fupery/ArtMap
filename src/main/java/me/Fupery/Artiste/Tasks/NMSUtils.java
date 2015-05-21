package me.Fupery.Artiste.Tasks;

import net.minecraft.server.v1_8_R2.BlockPosition;
import net.minecraft.server.v1_8_R2.ChunkCoordIntPair;
import net.minecraft.server.v1_8_R2.IBlockData;

import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R2.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Methods that interact with net.minecraft.server classes
 * <p>
 * Class may be broken with new versions
 */
public class NMSUtils {

	@SuppressWarnings("deprecation")
	public static void setBlock(Block b, byte data) {

		int typeId = Material.WOOL.getId();

		Chunk c = b.getChunk();

		net.minecraft.server.v1_8_R2.Chunk chunk = ((CraftChunk) c).getHandle();

		BlockPosition bp = new BlockPosition(b.getX(), b.getY(), b.getZ());

		int i = typeId + (data << 12);

		IBlockData ibd = net.minecraft.server.v1_8_R2.Block.getByCombinedId(i);

		chunk.a(bp, ibd);
	}

	public static void queueChunk(Player player, int cx, int cz) {
		((CraftPlayer) player).getHandle().chunkCoordIntPairQueue
				.add(new ChunkCoordIntPair(cx, cz));
	}
}
