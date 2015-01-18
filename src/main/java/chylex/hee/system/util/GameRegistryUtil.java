package chylex.hee.system.util;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class GameRegistryUtil{
	public static void registerBlock(Block block, String name){
		GameRegistry.registerBlock(block,ItemBlock.class,name);
	}
	
	public static void registerBlock(Block block, String name, Class<? extends ItemBlock> itemBlockClass){
		GameRegistry.registerBlock(block,itemBlockClass,name);
	}
	
	public static void registerItem(Item item, String name){
		GameRegistry.registerItem(item,name,"HardcoreEnderExpansion");
	}
	
	public static void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String name){
		GameRegistry.registerTileEntity(tileEntityClass,"HardcoreEnderExpansion:"+name);
	}
	
	private GameRegistryUtil(){}
}
