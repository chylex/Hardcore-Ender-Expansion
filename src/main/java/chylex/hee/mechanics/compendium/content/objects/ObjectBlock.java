package chylex.hee.mechanics.compendium.content.objects;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import chylex.hee.mechanics.compendium.content.objects.ObjectBlock.BlockMetaWrapper;

public class ObjectBlock implements IKnowledgeObjectInstance<BlockMetaWrapper>{
	private final BlockMetaWrapper wrapper;
	
	public ObjectBlock(Block block){
		this.wrapper = new BlockMetaWrapper(block,-1);
	}
	
	public ObjectBlock(Block block, int metadata){
		this.wrapper = new BlockMetaWrapper(block,metadata);
	}
	
	@Override
	public ItemStack createItemStackToRender(){
		return new ItemStack(wrapper.block,1,wrapper.metadata == OreDictionary.WILDCARD_VALUE ? 0 : wrapper.metadata);
	}

	@Override
	public boolean areObjectsEqual(BlockMetaWrapper obj1, BlockMetaWrapper obj2){
		return obj1.block == obj2.block && (obj1.metadata == obj2.metadata || obj1.metadata == -1 || obj2.metadata == -1);
	}
	
	public static class BlockMetaWrapper{
		public final Block block;
		public final byte metadata;
		
		public BlockMetaWrapper(Block block, int metadata){
			this.block = block;
			this.metadata = (byte)metadata;
		}
	}
}
