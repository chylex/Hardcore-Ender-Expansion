package chylex.hee.block;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockIgneousRockOre extends BlockAbstractOre{
	public BlockIgneousRockOre(){
		super();
	}
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return ItemList.igneous_rock;
	}
	
	@Override
	public int quantityDroppedWithBonus(int fortune, Random rand){
		return 1;
	}

	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune){
		super.dropBlockAsItemWithChance(world,x,y,z,meta,chance,fortune);
		dropXpOnBlockBreak(world,x,y,z,MathHelper.getRandomIntegerInRange(world.rand,3,5));
	}

	@Override
	protected void onOreMined(EntityPlayer player, ArrayList<ItemStack> drops, int x, int y, int z, int meta, int fortune){
		if (KnowledgeRegistrations.IGNEOUS_ROCK_ORE.tryUnlockFragment(player,0.08F).stopTrying)return;
		KnowledgeRegistrations.IGNEOUS_ROCK.tryUnlockFragment(player,0.06F,new byte[]{ 0,1,2 });
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){
		for(int a = 0; a < 2; a++)world.spawnParticle("lava",(x-0.425F+1.75F*rand.nextFloat()),(y+1.5F*rand.nextFloat()),(z-0.425F+1.75F*rand.nextFloat()),0D,0D,0D);
	}
}
