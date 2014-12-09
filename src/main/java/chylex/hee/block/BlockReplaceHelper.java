package chylex.hee.block;
import java.lang.reflect.Field;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.util.Unfinalizer;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;

public class BlockReplaceHelper{
	public static void replaceBlock(Block toReplace, Block replacement, Class<? extends ItemBlock> itemBlockClass){
		Stopwatch.time("BlockReplace");
		
		Class<?>[] classTest = new Class<?>[4];
		Exception exception = null;
		
		try{
			for(Field blockField:Blocks.class.getDeclaredFields()){
				if (Block.class.isAssignableFrom(blockField.getType())){
					Block block = (Block)blockField.get(null);
					
					if (block == toReplace){
						String registryName = Block.blockRegistry.getNameForObject(block);
						int id = Block.getIdFromBlock(block);
						
						Log.debug("Replacing block - $0/$1",id,registryName);
						
						FMLControlledNamespacedRegistry<Block> registryBlocks = GameData.getBlockRegistry();
						registryBlocks.registryObjects.put(registryName,replacement);
						registryBlocks.underlyingIntegerMap.func_148746_a(replacement,id); // OBFUSCATED put object
						
						blockField.setAccessible(true);
						Unfinalizer.unfinalizeField(blockField);
						blockField.set(null,replacement);
						
						ItemBlock itemBlock = itemBlockClass.getConstructor(Block.class).newInstance(replacement);
						FMLControlledNamespacedRegistry<Item> registryItems = GameData.getItemRegistry();
						registryItems.registryObjects.put(registryName,itemBlock);
						registryItems.underlyingIntegerMap.func_148746_a(itemBlock,id); // OBFUSCATED put object
						
						classTest[0] = blockField.get(null).getClass();
						classTest[1] = Block.blockRegistry.getObjectById(id).getClass();
						classTest[2] = ((ItemBlock)Item.getItemFromBlock(replacement)).field_150939_a.getClass();
						classTest[3] = Item.getItemFromBlock(replacement).getClass();
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
