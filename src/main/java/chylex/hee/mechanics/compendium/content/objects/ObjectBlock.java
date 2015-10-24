package chylex.hee.mechanics.compendium.content.objects;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import chylex.hee.system.abstractions.BlockInfo;

public class ObjectBlock implements IObjectHolder<BlockInfo>{
	public static final byte wildcard = -1;
	
	private final BlockInfo info;
	private final ItemStack displayIS;
	
	public ObjectBlock(BlockInfo info){
		this.info = info;
		this.displayIS = new ItemStack(info.block,info.meta);
	}
	
	public ObjectBlock(Block block){
		this.info = new BlockInfo(block,wildcard);
		this.displayIS = new ItemStack(block);
	}
	
	public ObjectBlock(Block block, int metadata){
		this.info = new BlockInfo(block,metadata);
		this.displayIS = new ItemStack(block,1,metadata == wildcard ? 0 : metadata);
	}
	
	public ObjectBlock(Block block, int displayMetadata, boolean useWildcard){
		this.info = new BlockInfo(block,useWildcard ? wildcard : displayMetadata);
		this.displayIS = new ItemStack(block,1,displayMetadata);
	}
	
	@Override
	public ItemStack getDisplayItemStack(){
		return displayIS;
	}
	
	@Override
	public BlockInfo getUnderlyingObject(){
		return info;
	}
	
	@Override
	public boolean checkEquality(Object obj){
		return IObjectHolder.super.checkEquality(obj);
	}
	
	@Override
	public boolean checkEquality(ItemStack is){
		return is.getItem() instanceof ItemBlock && ((ItemBlock)is.getItem()).field_150939_a == info.block && (is.getItemDamage() == info.meta || info.meta == wildcard);
	}
	
	@Override
	public boolean areObjectsEqual(@Nonnull BlockInfo obj){
		return obj.block == info.block && (obj.meta == info.meta || info.meta == wildcard);
	}
}
