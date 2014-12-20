package chylex.hee.mechanics.misc;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemList;
import chylex.hee.proxy.ModCommonProxy;

public final class Baconizer{
	private static final Pattern lcword = Pattern.compile("\\b([a-z]+?)\\b",Pattern.MULTILINE),
								 fcword = Pattern.compile("\\b([A-Z][a-z]+?)\\b",Pattern.MULTILINE),
								 ucword = Pattern.compile("\\b([A-Z]+?)\\b",Pattern.MULTILINE);
	
	public static void load(){
		if (!ModCommonProxy.hardcoreEnderbacon)return;
		
		Blocks.end_stone.setBlockName("baconStone");
		
		baconize(new Block[]{
			BlockList.end_powder_ore,
			BlockList.endium_ore,
			BlockList.endium_block,
			BlockList.enderman_head,
			BlockList.death_flower
		});
		
		baconize(new Item[]{
			ItemList.end_powder,
			ItemList.endium_ingot,
			ItemList.adventurers_diary,
			ItemList.knowledge_note,
			ItemList.altar_nexus,
			ItemList.temple_caller,
			ItemList.dry_splinter,
			ItemList.spectral_tear,
			ItemList.living_matter,
			ItemList.scorching_pickaxe
		});
	}
	
	private static void baconize(Block[] blocks){
		for(Block block:blocks)block.setBlockName(block.getUnlocalizedName().substring(5)+".bacon");
	}
	
	private static void baconize(Item[] items){
		for(Item item:items)item.setUnlocalizedName(item.getUnlocalizedName().substring(5)+".bacon");
	}
	
	public static String mobName(String name){
		return ModCommonProxy.hardcoreEnderbacon ? name.substring(0,name.length()-4)+"bacon.name" : name;
	}
	
	public static String soundNormal(String defsound){
		return ModCommonProxy.hardcoreEnderbacon ? "mob.pig.say" : defsound;
	}
	
	public static String soundDeath(String defsound){
		return ModCommonProxy.hardcoreEnderbacon ? "mob.pig.death" : defsound;
	}
	
	public static String sentence(String text){
		if (!ModCommonProxy.hardcoreEnderbacon)return text;
		
		String lc = StatCollector.translateToLocal("baconizer.bacon"), fc = Character.toUpperCase(lc.charAt(0))+lc.substring(1), uc = lc.toUpperCase();
		
		text = lcword.matcher(text).replaceAll(lc);
		text = fcword.matcher(text).replaceAll(fc);
		text = ucword.matcher(text).replaceAll(uc);
		return text;
	}
	
	private Baconizer(){}
}
