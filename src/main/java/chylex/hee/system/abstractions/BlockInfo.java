package chylex.hee.system.abstractions;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import chylex.hee.world.structure.IBlockPicker;

public final class BlockInfo implements IBlockPicker{
	public static final BlockInfo air = new BlockInfo(Blocks.air);
	
	public final Block block;
	public final byte meta;
	
	public BlockInfo(Block block){
		this.block = block;
		this.meta = 0;
	}
	
	public BlockInfo(Block block, int meta){
		this.block = block;
		this.meta = (byte)meta;
	}
	
	@Override
	public BlockInfo pick(Random rand){
		return this;
	}
	
	@Override
	public boolean equals(Object obj){
		if (obj instanceof BlockInfo){
			BlockInfo info = (BlockInfo)obj;
			return info.block == block && info.meta == meta;
		}
		else return false;
	}
	
	@Override
	public int hashCode(){
		return 17*Block.getIdFromBlock(block)+meta;
	}
}
