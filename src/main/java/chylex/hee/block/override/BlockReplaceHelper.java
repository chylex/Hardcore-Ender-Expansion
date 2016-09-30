package chylex.hee.block.override;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
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
	public static void replaceBlock(Block toReplace, Block replacement){
		Stopwatch.time("BlockReplace");
		
		Class<?>[] classTest = new Class<?>[3];
		Exception exception = null;
		
		try{
			for(Field blockField:Blocks.class.getFields()){
				if (Block.class.isAssignableFrom(blockField.getType())){
					Block block = (Block)blockField.get(null);
					
					if (block == toReplace){
						String registryName = Block.blockRegistry.getNameForObject(block);
						int id = Block.getIdFromBlock(block);
						
						Log.debug("Replacing block - $0/$1", id, registryName);
						
						((ItemBlock)Item.getItemFromBlock(block)).field_150939_a = replacement;
						
						FMLControlledNamespacedRegistry<Block> registryBlocks = GameData.getBlockRegistry();
						registryBlocks.registryObjects.put(registryName, replacement);
						registryBlocks.underlyingIntegerMap.func_148746_a(replacement, id); // OBFUSCATED put object
						
						blockField.setAccessible(true);
						Unfinalizer.unfinalizeField(blockField);
						blockField.set(null, replacement);
						
						Method delegateNameMethod = replacement.delegate.getClass().getDeclaredMethod("setName", String.class);
						delegateNameMethod.setAccessible(true);
						delegateNameMethod.invoke(replacement.delegate, toReplace.delegate.name());
						
						classTest[0] = blockField.get(null).getClass();
						classTest[1] = Block.blockRegistry.getObjectById(id).getClass();
						classTest[2] = ((ItemBlock)Item.getItemFromBlock(replacement)).field_150939_a.getClass();
						break;
					}
				}
			}
		}catch(Exception e){
			exception = e;
		}
		
		Stopwatch.finish("BlockReplace");
		
		Log.debug("Check field: $0", classTest[0]);
		Log.debug("Check block registry: $0", classTest[1]);
		Log.debug("Check item: $0", classTest[2]);
		
		if (classTest[0] != classTest[1] || classTest[0] != classTest[2] || classTest[0] == null){
			throw new RuntimeException("HardcoreEnderExpansion was unable to replace block "+toReplace.getUnlocalizedName()+"! Debug info to report: "+classTest[0]+", "+classTest[1]+", "+classTest[2], exception);
		}
	}
}
