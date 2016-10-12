package chylex.hee.block;
import java.util.Random;
import chylex.hee.block.base.BlockAbstractOre;
import net.minecraft.item.Item;

public class BlockEndiumOre extends BlockAbstractOre{
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return Item.getItemFromBlock(this);
	}
	
	@Override
	protected int getCausatumLevel(){
		return 12;
	}
}
