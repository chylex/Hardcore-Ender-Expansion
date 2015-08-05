package chylex.hee.world.structure;
import java.util.Random;
import net.minecraft.block.Block;
import chylex.hee.system.abstractions.BlockInfo;

@FunctionalInterface
public interface IBlockPicker{
	public static IBlockPicker basic(final Block block){
		final BlockInfo info = new BlockInfo(block);
		return rand -> info;
	}
	
	public static IBlockPicker basic(final Block block, final int meta){
		final BlockInfo info = new BlockInfo(block,meta);
		return rand -> info;
	}
	
	BlockInfo pick(Random rand);
}
