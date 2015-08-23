package chylex.hee.system.util;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntity;
import chylex.hee.HardcoreEnderExpansion;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public final class GameRegistryUtil{
	public static void registerBlock(Block block, String name, Class<? extends ItemBlock> itemBlockClass){
		GameRegistry.registerBlock(block,itemBlockClass,name);
	}
	
	public static void registerItem(Item item, String name){
		GameRegistry.registerItem(item,name,"HardcoreEnderExpansion");
	}
	
	public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String name){
		GameRegistry.registerTileEntity(tileEntityClass,"HardcoreEnderExpansion:"+name);
	}
	
	public static void registerEntity(Class<? extends Entity> entityClass, String entityName, int id, int trackingRange){
		EntityRegistry.registerModEntity(entityClass,entityName,id,HardcoreEnderExpansion.instance,trackingRange,1,true);
	}
	
	public static void registerEntity(Class<? extends Entity> entityClass, String entityName, int id, int trackingRange, int updateFrequency, boolean sendsVelocityUpdates){
		EntityRegistry.registerModEntity(entityClass,entityName,id,HardcoreEnderExpansion.instance,trackingRange,updateFrequency,sendsVelocityUpdates);
	}
	
	public static void replaceVanillaEntity(Class<? extends Entity> newEntityClass, int entityId){
		String name = EntityList.getStringFromID(entityId);
		
		if (name == null || EntityList.stringToClassMapping.remove(name) == null || EntityList.IDtoClassMapping.remove(Integer.valueOf(entityId)) == null){
			throw new IllegalStateException("Error replacing entity with ID "+entityId+", entity entry missing!");
		}
		
		EntityList.addMapping(newEntityClass,name,entityId);
	}
	
	public static void addSmeltingRecipe(ItemStack input, ItemStack output, float experience){
		FurnaceRecipes.smelting().func_151394_a(input,output,experience);
	}
	
	private GameRegistryUtil(){}
}
