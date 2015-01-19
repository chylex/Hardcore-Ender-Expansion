package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.BlockOre;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.item.ItemList;

public class BlockIgneousRockOre extends BlockOre{
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return ItemList.igneous_rock;
	}
	
	@Override
	public int quantityDroppedWithBonus(int fortune, Random rand){
		return 1;
	}
	
	@Override
	public int getExpDrop(IBlockAccess world, int meta, int fortune){
		return MathHelper.getRandomIntegerInRange(BlockList.blockRandom,3,5);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){
		for(int a = 0; a < 2; a++)world.spawnParticle(EnumParticleTypes.LAVA,(x-0.425F+1.75F*rand.nextFloat()),(y+1.5F*rand.nextFloat()),(z-0.425F+1.75F*rand.nextFloat()),0D,0D,0D);
	}
}
