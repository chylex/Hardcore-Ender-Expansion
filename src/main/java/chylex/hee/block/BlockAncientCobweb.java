package chylex.hee.block;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import chylex.hee.block.material.MaterialCustomWeb;
import chylex.hee.init.ItemList;
import chylex.hee.init.ModInitHandler;
import chylex.hee.world.loot.PercentageLootTable;
import chylex.hee.world.loot.info.LootBlockInfo;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockAncientCobweb extends BlockWeb{
	private static final Material web = new MaterialCustomWeb();
	private static final PercentageLootTable drops = new PercentageLootTable();
	
	static{
		ModInitHandler.afterPreInit(() -> {
			drops.addLoot(Items.string).setChances(obj -> {
				return ((LootBlockInfo)obj).fortune > 0 ? new float[]{ 1F } : new float[]{ 0.9F };
			});
			
			drops.addLoot(ItemList.ancient_dust).setChances(obj -> {
				return ((LootBlockInfo)obj).fortune > 0 ? new float[]{ 0.85F } : new float[]{ 0.6F };
			});
		});
	}
	
	@Override
	public Material getMaterial(){
		return web;
	};
	
	@Override
	public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune){
		return drops.generateLoot(new LootBlockInfo(this,meta,fortune),world.rand);
	}
	
	@SubscribeEvent
	public void onBreakSpeed(BreakSpeed e){
		if (e.block == this && canHarvestBlock(e.entityPlayer,e.metadata))e.newSpeed *= 15F;
	}
	
	@Override
	public boolean canHarvestBlock(EntityPlayer player, int meta){
		ItemStack tool = player.getHeldItem();
		return tool != null && (tool.getItem() instanceof ItemSword || tool.getItem() instanceof ItemShears);
	};
	
	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return null;
	}
	
	@Override
	public int quantityDropped(Random rand){
		return 0;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int colorMultiplier(IBlockAccess world, int x, int y, int z){
		return 0xDFDFDF;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderColor(int meta){
		return 0xDFDFDF;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getBlockColor(){
		return 0xDFDFDF;
	}
}
