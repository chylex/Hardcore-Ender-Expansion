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
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.TimeMeasurement;
import cpw.mods.fml.common.registry.FMLControlledNamespacedRegistry;
import cpw.mods.fml.common.registry.GameData;

public class BlockReplaceHelper{
	public static void replaceBlock(Block toReplace, Class<? extends Block> blockClass){
		TimeMeasurement.start("BlockReplace");
		
		Field modifiersField = null;
		Class<?>[] classTest = new Class<?>[3];
		
		try{
			modifiersField = Field.class.getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			
			for(Field blockField:Blocks.class.getDeclaredFields()){
				if (Block.class.isAssignableFrom(blockField.getType())){
					Block block = (Block)blockField.get(null);
					
					if (block == toReplace){
						String registryName = Block.blockRegistry.getNameForObject(block);
						int id = Block.getIdFromBlock(block);
						ItemBlock item = (ItemBlock)Item.getItemFromBlock(block);
						DragonUtil.info("Replacing block - "+id+"/"+registryName);
						
						Block newBlock = blockClass.newInstance();
						FMLControlledNamespacedRegistry<Block> registry = GameData.getBlockRegistry();
						Field map = RegistrySimple.class.getDeclaredFields()[1];
						map.setAccessible(true);
						((Map)map.get(registry)).put(registryName,newBlock);
						
						map = RegistryNamespaced.class.getDeclaredFields()[0];
						map.setAccessible(true);
						((ObjectIntIdentityMap)map.get(registry)).func_148746_a(newBlock,id); // OBFUSCATED put object
						
						blockField.setAccessible(true);
						modifiersField.setInt(blockField,modifiersField.getInt(blockField) & ~Modifier.FINAL);
						blockField.set(null,newBlock);
						
						Field itemBlockField = ItemBlock.class.getDeclaredFields()[0];
						itemBlockField.setAccessible(true);
						modifiersField.setInt(itemBlockField,modifiersField.getInt(itemBlockField) & ~Modifier.FINAL);
						itemBlockField.set(item,newBlock);
						
						classTest[0] = blockField.get(null).getClass();
						classTest[1] = Block.blockRegistry.getObjectById(id).getClass();
						classTest[2] = ((ItemBlock)Item.getItemFromBlock(newBlock)).field_150939_a.getClass();
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		TimeMeasurement.finish("BlockReplace");
		
		DragonUtil.info("Check field: "+classTest[0]);
		DragonUtil.info("Check registry: "+classTest[1]);
		DragonUtil.info("Check item: "+classTest[2]);
		
		if (classTest[0] != classTest[1] || classTest[0] != classTest[2] || classTest[0] == null){
			throw new RuntimeException("Failed class test, replacing "+toReplace.getUnlocalizedName()+" failed!");
		}
	}
}
