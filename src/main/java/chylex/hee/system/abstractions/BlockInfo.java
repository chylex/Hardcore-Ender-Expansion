package chylex.hee.system.abstractions;
import net.minecraft.block.Block;

public final class BlockInfo{
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
