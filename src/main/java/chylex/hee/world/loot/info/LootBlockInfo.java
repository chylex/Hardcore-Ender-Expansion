package chylex.hee.world.loot.info;
import net.minecraft.block.Block;

public class LootBlockInfo<T extends Block>{
	public final T block;
	public final byte metadata;
	public final int fortune;
	
	public LootBlockInfo(T block, int metadata, int fortune){
		this.block = block;
		this.metadata = (byte)metadata;
		this.fortune = fortune;
	}
}
