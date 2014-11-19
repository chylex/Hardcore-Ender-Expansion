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
	public static void replaceBlock(Block toReplace, Class<? extends Block> blockClass, Class<? extends ItemBlock> itemBlockClass){
		Stopwatch.time("BlockReplace");
		
		Class<?>[] classTest = new Class<?>[4];
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
						
						Log.debug("Replacing block - $0/$1",id,registryName);
						
						Block newBlock = blockClass.newInstance();
						FMLControlledNamespacedRegistry<Block> registryBlocks = GameData.getBlockRegistry();
						Field map1 = RegistrySimple.class.getDeclaredFields()[1];
						map1.setAccessible(true);
						((Map)map1.get(registryBlocks)).put(registryName,newBlock);
						
						Field map2 = RegistryNamespaced.class.getDeclaredFields()[0];
						map2.setAccessible(true);
						((ObjectIntIdentityMap)map2.get(registryBlocks)).func_148746_a(newBlock,id); // OBFUSCATED put object
						
						blockField.setAccessible(true);
						modifiersField.setInt(blockField,blockField.getModifiers() & ~Modifier.FINAL);
						blockField.set(null,newBlock);
						
						ItemBlock itemBlock = itemBlockClass.getConstructor(Block.class).newInstance(newBlock);
						FMLControlledNamespacedRegistry<Item> registryItems = GameData.getItemRegistry();
						((Map)map1.get(registryItems)).put(registryName,itemBlock);
						((ObjectIntIdentityMap)map2.get(registryItems)).func_148746_a(itemBlock,id); // OBFUSCATED put object
						
						classTest[0] = blockField.get(null).getClass();
						classTest[1] = Block.blockRegistry.getObjectById(id).getClass();
						classTest[2] = ((ItemBlock)Item.getItemFromBlock(newBlock)).field_150939_a.getClass();
						classTest[3] = Item.getItemFromBlock(newBlock).getClass();
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
		Log.debug("Check itemblock: $0",classTest[3]);
		
		if (classTest[0] != classTest[1] || classTest[0] != classTest[2] || classTest[0] == null || classTest[3] != itemBlockClass){
			throw new RuntimeException("HardcoreEnderExpansion was unable to replace block "+toReplace.getUnlocalizedName()+"! Debug info to report: "+classTest[0]+","+classTest[1]+","+classTest[2]+","+classTest[3],exception);
		}
	}
}
