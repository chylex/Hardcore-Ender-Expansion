package chylex.hee.block;
import java.util.Random;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import chylex.hee.item.ItemList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEndPowderOre extends BlockAbstractOre{
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return ItemList.end_powder;
	}

	@Override
	public int quantityDroppedWithBonus(int fortune, Random rand){
		return 1+rand.nextInt(3+fortune);
	}
	
	@Override
	public int getExpDrop(IBlockAccess world, int meta, int fortune){
		return MathHelper.getRandomIntegerInRange(BlockList.blockRandom,2,4);
	}
	
	@Override
	protected int getCausatumLevel(){
		return 5;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){
		for(int a = 0; a < 2; a++)world.spawnParticle("portal",(x-0.425F+1.75F*rand.nextFloat()),(y+1.5F*rand.nextFloat()),(z-0.425F+1.75F*rand.nextFloat()),0D,0D,0D);
	}
}
