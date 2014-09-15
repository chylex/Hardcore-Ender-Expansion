package chylex.hee.block;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.RegistryNamespaced;
import net.minecraft.util.RegistrySimple;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;

public class BlockReplaceHelper{
	public static void replaceBlock(Block toReplace, Class<? extends Block> blockClass){
		Stopwatch.time("BlockReplace");
		
		Class<?>[] classTest = new Class<?>[3];
		Exception exception = null;
		
		try{
			Field modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			
			for(Field blockField:Blocks.class.getDeclaredFields()){
				if (Block.class.isAssignableFrom(blockField.getType())){
					Block block = (Block)blockField.get(null);
					
					if (block == toReplace){
						String registryName = Block.blockRegistry.getNameForObject(block);
						int id = Block.getIdFromBlock(block);
						ItemBlock item = (ItemBlock)Item.getItemFromBlock(block);
						
						Log.debug("Replacing block - $0/$1",id,registryName);
						
						Block newBlock = blockClass.newInstance();
						FMLControlledNamespacedRegistry<Block> registry = GameData.getBlockRegistry();
						Field map = RegistrySimple.class.getDeclaredFields()[1];
						map.setAccessible(true);
						((Map)map.get(registry)).put(registryName,newBlock);
						
						map = RegistryNamespaced.class.getDeclaredFields()[0];
						map.setAccessible(true);
						((ObjectIntIdentityMap)map.get(registry)).func_148746_a(newBlock,id); // OBFUSCATED put object
						
						blockField.setAccessible(true);
						modifiersField.setInt(blockField,blockField.getModifiers() & ~Modifier.FINAL);
						blockField.set(null,newBlock);
						
						Field itemBlockField = ItemBlock.class.getDeclaredFields()[0];
						itemBlockField.setAccessible(true);
						modifiersField.setInt(itemBlockField,itemBlockField.getModifiers() & ~Modifier.FINAL);
						itemBlockField.set(item,newBlock);
						
						classTest[0] = blockField.get(null).getClass();
						classTest[1] = Block.blockRegistry.getObjectById(id).getClass();
						classTest[2] = ((ItemBlock)Item.getItemFromBlock(newBlock)).field_150939_a.getClass();
					}
				}
			}
		}catch(Exception e){
			exception = e;
		}
		
		Stopwatch.finish("BlockReplace");
		
		Log.debug("Check field: $0",classTest[0]);
		Log.debug("Check registry: $0",classTest[1]);
		Log.debug("Check item: $0",classTest[2]);
		
		if (classTest[0] != classTest[1] || classTest[0] != classTest[2] || classTest[0] == null){
			throw new RuntimeException("HardcoreEnderExpansion was unable to replace block "+toReplace.getUnlocalizedName()+"! Debug info to report: "+classTest[0]+","+classTest[1]+","+classTest[2],exception);
		}
	}
}
