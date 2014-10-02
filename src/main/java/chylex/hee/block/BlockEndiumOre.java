package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.BlockOre;
import net.minecraft.item.Item;

public class BlockEndiumOre extends BlockOre{
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return Item.getItemFromBlock(this);
	}
}
