package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.item.block.ItemBlockWithSubtypes.IBlockSubtypes;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.BlockPosM;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockObsidianSpecial extends Block implements IBlockSubtypes{
	@SideOnly(Side.CLIENT)
	private IIcon iconSmooth, iconPillar, iconPillarTop, iconChiseled, iconChiseledTop;
	
	/*
	 * Metadata
	 *   0: smooth
	 *   1: chiseled
	 *   2: pillar - vertical
	 *   3: pillar - NS
	 *   4: pillar - EW
	 *   5: (smooth) downward particle spawner - 4 blocks
	 *   6: (chiseled) upward particle spawner - 5 blocks
	 */
	
	private final boolean isGlowing;
	
	public BlockObsidianSpecial(boolean isGlowing){
		super(Material.rock);
		this.isGlowing = isGlowing;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta){
		if (meta == 1 || meta == 6)return side == 1 ? iconChiseledTop : iconChiseled;
		else if (meta >= 2 && meta <= 4)return (meta == 2 && (side == 0 || side == 1)) || (meta == 3 && (side == 4 || side == 5)) || (meta == 4 && (side == 2 || side == 3)) ? iconPillarTop : iconPillar;
		return iconSmooth;
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta){
		return (meta == 2) ? (side == 0 || side == 1 ? 2 : side == 2 || side == 3 ? 4 : side == 4 || side == 5 ? 3 : meta) : meta;
	}
	
	@Override
	public int damageDropped(int meta){
		return meta == 6 ? 1 : meta == 5 ? 0 : meta == 3 || meta == 4 ? 2 : meta;
	}
	
	@Override
	protected ItemStack createStackedBlock(int meta){
		if (meta == 3 || meta == 4)return new ItemStack(this,1,2);
		else if (meta == 5)return new ItemStack(this,1,0);
		else if (meta == 6)return new ItemStack(this,1,1);
		else return super.createStackedBlock(meta);
	}
	
	@Override
	public int getRenderType(){
		return ModCommonProxy.renderIdObsidianSpecial;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack is){
		switch(is.getItemDamage()){
			case 1: return isGlowing ? "tile.obsidianSpecialGlowing.chiseled" : "tile.obsidianSpecial.chiseled";
			case 2: return isGlowing ? "tile.obsidianSpecialGlowing.pillar" : "tile.obsidianSpecial.pillar";
			default: return isGlowing ? "tile.obsidianSpecialGlowing.smooth" : "tile.obsidianSpecial.smooth";
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){
		int meta = BlockPosM.tmp(x,y,z).getMetadata(world);
		
		if (meta == 5){
			for(int a = 0; a < 10; a++){
				world.spawnParticle("portal",(x+rand.nextFloat()),(y-4F*rand.nextFloat()),(z+rand.nextFloat()),0D,0D,0D);
				world.spawnParticle("largesmoke",(x+rand.nextFloat()),(y-4F*rand.nextFloat()),(z+rand.nextFloat()),0D,0D,0D);
			}
		}
		else if (meta == 6){
			for(int a = 0; a < 30; a++){
				world.spawnParticle("portal",(x+rand.nextFloat()),(y+5F*rand.nextFloat()),(z+rand.nextFloat()),0D,0D,0D);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item item, CreativeTabs tab, List list){
		list.add(new ItemStack(item,1,0));
		list.add(new ItemStack(item,1,1));
		list.add(new ItemStack(item,1,2));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister){
		iconSmooth = iconRegister.registerIcon("hardcoreenderexpansion:obsidian_smooth");
		iconPillar = iconRegister.registerIcon("hardcoreenderexpansion:obsidian_pillar");
		iconPillarTop = iconRegister.registerIcon("hardcoreenderexpansion:obsidian_pillar_top");
		iconChiseled = iconRegister.registerIcon("hardcoreenderexpansion:obsidian_chiseled");
		iconChiseledTop = iconRegister.registerIcon("hardcoreenderexpansion:obsidian_chiseled_top");
	}
}
