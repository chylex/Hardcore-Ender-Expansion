package chylex.hee.mechanics.misc;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.render.entity.RenderBossDragon;
import chylex.hee.render.entity.RenderMobAngryEnderman;
import chylex.hee.render.entity.RenderMobBabyEnderman;
import chylex.hee.render.entity.RenderMobEnderman;
import chylex.hee.render.entity.RenderMobHomelandEnderman;
import chylex.hee.render.entity.RenderMobInfestedBat;
import chylex.hee.render.entity.RenderMobParalyzedEnderman;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class Baconizer{
	private static final Pattern lcword = Pattern.compile("\\b([a-z]+?)\\b",Pattern.MULTILINE),
								 fcword = Pattern.compile("\\b([A-Z][a-z]+?)\\b",Pattern.MULTILINE),
								 ucword = Pattern.compile("\\b([A-Z]+?)\\b",Pattern.MULTILINE);
	
	@SideOnly(Side.CLIENT)
	private static Map<Class<?>,ResourceLocation> renderers;

	@SideOnly(Side.CLIENT)
	public static void load(){
		if (!ModCommonProxy.hardcoreEnderbacon)return;
		
		Blocks.end_stone.setBlockName("baconStone");
		
		baconizeBlocks(new Block[]{
			BlockList.end_powder_ore,
			BlockList.endium_ore,
			BlockList.endium_block,
			BlockList.enderman_head,
			BlockList.death_flower,
			BlockList.transport_beacon
		});
		
		baconizeItems(new Item[]{
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
		
		renderers = new HashMap<>();
		
		baconizeMob(RenderMobEnderman.class,"enderman");
		baconizeMob(RenderMobAngryEnderman.class,"enderman");
		baconizeMob(RenderMobBabyEnderman.class,"enderman");
		baconizeMob(RenderMobHomelandEnderman.class,"enderman");
		baconizeMob(RenderMobParalyzedEnderman.class,"enderman");
		baconizeMob(RenderMobInfestedBat.class,"bat_infested");
		baconizeMob(RenderBossDragon.class,"dragon");
	}
	
	private static void baconizeBlocks(Block[] blocks){
		for(Block block:blocks)block.setBlockName(block.getUnlocalizedName().substring(5)+".bacon");
	}
	
	private static void baconizeItems(Item[] items){
		for(Item item:items)item.setUnlocalizedName(item.getUnlocalizedName().substring(5)+".bacon");
	}
	
	private static void baconizeMob(Class<?> cls, String texName){
		renderers.put(cls,new ResourceLocation("hardcoreenderexpansion:textures/entity/bacon/"+texName+".png"));
	}
	
	public static String mobName(String name){
		return ModCommonProxy.hardcoreEnderbacon ? name.substring(0,name.length()-4)+"bacon.name" : name;
	}
	
	public static ResourceLocation mobTexture(Render renderer, ResourceLocation defLoc){
		return ModCommonProxy.hardcoreEnderbacon ? renderers.get(renderer.getClass()) : defLoc;
	}
	
	public static String soundNormal(String defsound){
		return ModCommonProxy.hardcoreEnderbacon ? "mob.pig.say" : defsound;
	}
	
	public static String soundDeath(String defsound){
		return ModCommonProxy.hardcoreEnderbacon ? "mob.pig.death" : defsound;
	}
	
	public static String sentence(String text){
		if (!ModCommonProxy.hardcoreEnderbacon)return text;
		
		String lc = StatCollector.translateToLocal("baconizer.bacon"), fc = Character.toUpperCase(lc.charAt(0))+lc.substring(1), uc = lc.toUpperCase(Locale.ENGLISH);
		
		text = lcword.matcher(text).replaceAll(lc);
		text = fcword.matcher(text).replaceAll(fc);
		text = ucword.matcher(text).replaceAll(uc);
		return text;
	}
	
	@SideOnly(Side.CLIENT)
	public static void runBaconCommand(){
		ChatComponentStyle text = null;
		
		if (!ModCommonProxy.hardcoreEnderbacon)text = new ChatComponentTranslation("commands.generic.notFound");
		else text = new ChatComponentText(getBaconJoke(Minecraft.getMinecraft().theWorld.rand));
		
		Minecraft.getMinecraft().thePlayer.addChatMessage(text);
	}
	
	@SideOnly(Side.CLIENT)
	private static String getBaconJoke(Random rand){
		switch(rand.nextInt(11)){
			case 0: return "Why did the pig go into the kitchen? He felt like bacon.";
			case 1: return "What do you get when you cross a pig and a cactus? A Porkupine.";
			case 2: return "How do pigs write top secret messages? With invisible oink.";
			case 3: return "What do you call a pig that does karate? Porkchop!";
			case 4: return "What do you get when you cross a pig and a dinosaur? Jurassic pork.";
			case 5: return "What did the bacon say to a burger patty? Don't worry, I got you covered!";
			case 6: return "What did one slice of bacon say to the other slice? Nice fat!";
			case 7: return "How did the pig get to the hospital? In a hambulance.";
			case 8: return "What do pigs drive? Pigup trucks.";
			case 9: return "Where do pigs go for warm weather? The tropigs.";
			case 10: return "What do you call a pig that took a plane? Swine flu.";
			default: return "";
		}
	}
	
	private Baconizer(){}
}
