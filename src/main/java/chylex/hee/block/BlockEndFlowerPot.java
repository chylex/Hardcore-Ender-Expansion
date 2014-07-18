package chylex.hee.block;
import java.util.Random;
import net.minecraft.block.BlockFlowerPot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.proxy.ModCommonProxy;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEndFlowerPot extends BlockFlowerPot{
	public BlockEndFlowerPot(){
		super();
		setTickRandomly(true);
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
		((BlockEndFlower)BlockList.death_flower).updateFlowerLogic(world,x,y,z,rand);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ){
		return BlockList.death_flower.onBlockActivated(world,x,y,z,player,side,hitX,hitY,hitZ);
	}

	@Override
	public int getDamageValue(World world, int x, int y, int z){
		return world.getBlockMetadata(x,y,z);
	}

	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune){
		super.dropBlockAsItemWithChance(world,x,y,z,0,chance,fortune);
		dropBlockAsItem(world,x,y,z,new ItemStack(BlockList.death_flower,1,meta));
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z){
		return Item.getItemFromBlock(BlockList.death_flower);
	}
	
	@Override
	public int getRenderType(){
		return ModCommonProxy.renderIdFlowerPot;
	}

	public static ItemStack getPlantForMeta(int meta){
		return new ItemStack(BlockList.death_flower,1,meta);
	}

	public static int getMetaForPlant(ItemStack is){
		return is.getItemDamage();
	}
}
