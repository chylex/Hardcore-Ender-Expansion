package chylex.hee.mechanics.compendium.objects;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import chylex.hee.mechanics.compendium.objects.ObjectBlock.BlockMetaWrapper;

public class ObjectBlock implements IKnowledgeObjectInstance<BlockMetaWrapper>{
	private final BlockMetaWrapper wrapper;
	
	public ObjectBlock(Block block){
		this.wrapper = new BlockMetaWrapper(block,-1);
	}
	
	public ObjectBlock(Block block, int metadata){
		this.wrapper = new BlockMetaWrapper(block,metadata);
	}

	@Override
	public BlockMetaWrapper getUnderlyingObject(){
		return wrapper;
	}
	
	@Override
	public ItemStack createItemStackToRender(){
		return new ItemStack(wrapper.block,1,wrapper.metadata == OreDictionary.WILDCARD_VALUE ? 0 : wrapper.metadata);
	}

	@Override
	public boolean checkEquality(Object o){
		if (o.getClass() != BlockMetaWrapper.class)return false;
		
		BlockMetaWrapper bmw = (BlockMetaWrapper)o;
		return bmw.block == wrapper.block && (bmw.metadata == wrapper.metadata || bmw.metadata == -1 || wrapper.metadata == -1);
	}
	
	public static class BlockMetaWrapper{
		public Block block;
		public byte metadata;
		
		public BlockMetaWrapper(Block block, int metadata){
			this.block = block;
			this.metadata = (byte)metadata;
		}
		
		@Override
		public boolean equals(Object o){
			return o.getClass() == BlockMetaWrapper.class && ((BlockMetaWrapper)o).block == block;
		}
		
		@Override
		public int hashCode(){
			return block.hashCode();
		}
		
		@Override
		public String toString(){
			return block+"/"+metadata;
		}
	}
}
