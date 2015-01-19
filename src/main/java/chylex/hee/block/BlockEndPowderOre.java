package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.BlockOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.item.ItemList;

public class BlockEndPowderOre extends BlockOre{
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune){
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
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand){
		for(int a = 0; a < 2; a++)world.spawnParticle(EnumParticleTypes.PORTAL,(x-0.425F+1.75F*rand.nextFloat()),(y+1.5F*rand.nextFloat()),(z-0.425F+1.75F*rand.nextFloat()),0D,0D,0D);
	}
}
