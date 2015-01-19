package chylex.hee.item.block;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockWithSubtypes extends ItemBlock{
	private final boolean isIBlockSubtypes;
	
	public ItemBlockWithSubtypes(Block block){
		super(block);
		setHasSubtypes(true);
		setUnlocalizedName(block.getUnlocalizedName());
		
		isIBlockSubtypes = block instanceof IBlockSubtypes;
	}

	@Override
	public int getMetadata(int damage){
		return damage;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		if (isIBlockSubtypes)return ((IBlockSubtypes)block).getUnlocalizedName(is);
		else return super.getUnlocalizedName(is);
	}
	
	public static interface IBlockSubtypes{
		String getUnlocalizedName(ItemStack is);
	}
}
