package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.BlockOre;
import net.minecraft.item.Item;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEndiumOre extends BlockOre{
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return Item.getItemFromBlock(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){
		// TODO maybe some nice effect
	}
}
