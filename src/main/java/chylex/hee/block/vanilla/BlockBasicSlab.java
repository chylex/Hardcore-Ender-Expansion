package chylex.hee.block.vanilla;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockSlab;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.item.block.ItemBlockSlab.IBlockSlab;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockBasicSlab extends BlockSlab implements IBlockSlab{
	private final Block fullBlock;
	
	public BlockBasicSlab(Block fullBlock){
		super(false,fullBlock.getMaterial());
		this.fullBlock = fullBlock;
	}

	@Override
	public String func_150002_b(int meta){
		return getUnlocalizedName();
	}

	@Override
	public Block getFullBlock(){
		return fullBlock;
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return Item.getItemFromBlock(this);
	}
	
	@Override
	protected ItemStack createStackedBlock(int meta){
		return new ItemStack(Item.getItemFromBlock(this),1,0);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z){
		return Item.getItemFromBlock(this);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		return fullBlock.getIcon(side,0);
	}
}