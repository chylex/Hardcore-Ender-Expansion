package chylex.hee.item;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemReed;
import chylex.hee.block.BlockList;
import chylex.hee.system.creativetab.ModCreativeTab;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.util.GameRegistryUtil;

public final class ItemList{
	private static final Map<String,Item> items = new HashMap<>();
	
	private static void register(String itemIdentifier, Item item){
		items.put(itemIdentifier,item);
	}
	
	public static Item getItem(String identifier){
		return items.get(identifier);
	}
	
	public static Collection<Item> getAllItems(){
		return items.values();
	}
	
	// LORE
	
	public static Item adventurers_diary;
	public static Item knowledge_note;
	
	// BASIC MATERIALS
	
	public static Item end_powder;
	public static Item endium_ingot;
	public static Item stardust;
	public static Item igneous_rock;
	public static Item instability_orb;
	
	// CRAFTING MATERIALS
	
	public static Item altar_nexus;
	public static Item essence;
	public static Item silverfish_blood;
	public static Item dry_splinter;
	public static Item ectoplasm;
	public static Item spectral_tear;
	public static Item living_matter;
	public static Item rune;
	public static Item infernium;
	public static Item arcane_shard;
	public static Item blank_gem;
	public static Item obsidian_fragment;
	public static Item obsidian_rod;
	public static Item auricion;
	public static Item energy_wand_core;
	
	// FUNCTIONAL ITEMS
	
	public static Item enhanced_brewing_stand;
	public static Item enhanced_ender_pearl;
	public static Item potion_of_instability;
	public static Item biome_compass;
	public static Item spatial_dash_gem;
	public static Item transference_gem;
	public static Item temple_caller;
	public static Item infestation_remedy;
	public static Item ghost_amulet;
	public static Item curse;
	public static Item potion_of_purity;
	public static Item curse_amulet;
	public static Item charm_pouch;
	public static Item charm;
	public static Item scorching_pickaxe;
	public static Item energy_wand;
	
	// WORLD
	
	public static Item enderman_head;
	public static Item bucket_ender_goo;
	public static Item music_disk;
	
	// TECHNICAL
	
	public static Item exp_bottle;
	public static Item spawn_eggs;
	public static Item special_effects;
	
	// LOAD
	
	public static void loadItems(){
		register("adventurers_diary", adventurers_diary = new ItemAdventurersDiary().setMaxStackSize(1).setUnlocalizedName("lorePage"));
		register("knowledge_note", knowledge_note = new ItemKnowledgeNote().setMaxStackSize(1).setUnlocalizedName("knowledgeNote"));
		
		register("end_powder", end_powder = new ItemEndPowder().setUnlocalizedName("endPowder"));
		register("endium_ingot", endium_ingot = new Item().setUnlocalizedName("endiumIngot"));
		register("stardust", stardust = new Item().setUnlocalizedName("stardust"));
		register("igneous_rock", igneous_rock = new ItemIgneousRock().setUnlocalizedName("igneousRock"));
		register("instability_orb", instability_orb = new ItemInstabilityOrb().setUnlocalizedName("instabilityOrb"));
		
		register("altar_nexus", altar_nexus = new Item().setUnlocalizedName("altarNexus"));
		register("essence", essence = new ItemEssence().setUnlocalizedName("essence"));
		register("silverfish_blood", silverfish_blood = new Item().setUnlocalizedName("silverfishBlood"));
		register("dry_splinter", dry_splinter = new Item().setUnlocalizedName("drySplinter"));
		register("endoplasm", ectoplasm = new Item().setUnlocalizedName("ectoplasm"));
		register("spectral_tear", spectral_tear = new Item().setUnlocalizedName("spectralTear"));
		register("living_matter", living_matter = new Item().setUnlocalizedName("livingMatter"));
		register("rune", rune = new ItemRune().setMaxStackSize(16).setUnlocalizedName("rune"));
		register("fire_shard", infernium = new Item().setUnlocalizedName("infernium"));
		register("arcane_shard", arcane_shard = new Item().setUnlocalizedName("arcaneShard"));
		register("blank_gem", blank_gem = new Item().setUnlocalizedName("blankGem"));
		register("obsidian_fragment", obsidian_fragment = new Item().setUnlocalizedName("obsidianFragment"));
		register("obsidian_rod", obsidian_rod = new Item().setUnlocalizedName("obsidianRod"));
		register("auricion", auricion = new Item().setUnlocalizedName("auricion"));
		register("energy_wand_core", energy_wand_core = new Item().setUnlocalizedName("energyWandCore"));
		
		register("enhanced_brewing_stand", enhanced_brewing_stand = new ItemReed(BlockList.enhanced_brewing_stand).setUnlocalizedName("enhancedBrewingStand"));
		register("enhanced_ender_pearl", enhanced_ender_pearl = new ItemEnhancedEnderPearl().setCreativeTab(null).setUnlocalizedName("enderPearl"));
		register("potion_of_instability", potion_of_instability = new ItemPotionOfInstability().setUnlocalizedName("potionOfInstability"));
		register("biome_compass", biome_compass = new ItemBiomeCompass().setMaxStackSize(1).setUnlocalizedName("biomeCompass"));
		register("spatial_dash_gem", spatial_dash_gem = new ItemSpatialDashGem().setMaxStackSize(1).setMaxDamage(300).setNoRepair().setUnlocalizedName("spatialDashGem"));
		register("transference_gem", transference_gem = new ItemTransferenceGem().setMaxStackSize(1).setMaxDamage(204).setNoRepair().setUnlocalizedName("transferenceGem"));
		register("temple_caller", temple_caller = new ItemTempleCaller().setMaxStackSize(1).setMaxDamage(50).setNoRepair().setUnlocalizedName("templeCaller"));
		register("infestation_remedy", infestation_remedy = new ItemInfestationRemedy().setMaxStackSize(1).setUnlocalizedName("infestationRemedy"));
		register("ghost_amulet", ghost_amulet = new ItemGhostAmulet().setMaxStackSize(1).setUnlocalizedName("ghostAmulet"));
		register("curse", curse = new ItemCurse().setMaxStackSize(32).setUnlocalizedName("curse"));
		register("potion_of_purity", potion_of_purity = new ItemPotionOfPurity().setUnlocalizedName("potionOfPurity"));
		register("curse_amulet", curse_amulet = new Item().setMaxStackSize(1).setUnlocalizedName("curseAmulet"));
		register("charm_pouch", charm_pouch = new ItemCharmPouch().setMaxStackSize(1).setUnlocalizedName("charmPouch"));
		register("charm", charm = new ItemCharm().setMaxStackSize(1).setUnlocalizedName("charm"));
		register("schorching_pickaxe", scorching_pickaxe = new ItemScorchingPickaxe().setMaxStackSize(1).setMaxDamage(399).setUnlocalizedName("scorchingPickaxe"));
		register("energy_wand", energy_wand = new ItemEnergyWand().setMaxStackSize(1).setUnlocalizedName("energyWand"));
		
		register("enderman_head", enderman_head = new ItemEndermanHead().setUnlocalizedName("endermanHead"));
		register("bucket_ender_goo", bucket_ender_goo = new ItemBucket(BlockList.ender_goo).setUnlocalizedName("bucketEnderGoo"));
		register("music_disk", music_disk = new ItemMusicDisk().setUnlocalizedName("record"));
		
		register("exp_bottle_consistent", exp_bottle = new ItemExpBottleConsistent().setUnlocalizedName("expBottle"));
		register("spawn_eggs", spawn_eggs = new ItemSpawnEggs().setUnlocalizedName("monsterPlacer"));
		register("item_special_effects", special_effects = new ItemSpecialEffects().setUnlocalizedName("itemNumber"));
	}
	
	public static void registerItems(){
		Stopwatch.time("ItemList - register");
		
		for(Entry<String,Item> entry:ItemList.items.entrySet()){
			GameRegistryUtil.registerItem(entry.getValue(),entry.getKey());
		}
		
		Stopwatch.finish("ItemList - register");
		
		ModCreativeTab.tabMain.list.addItems(
			adventurers_diary,altar_nexus,essence,enhanced_brewing_stand,
			end_powder,endium_ingot,stardust,igneous_rock,instability_orb,potion_of_instability,
			biome_compass,blank_gem,spatial_dash_gem,transference_gem,temple_caller,
			silverfish_blood,dry_splinter,infestation_remedy,charm_pouch,rune,
			ghost_amulet,ectoplasm,spectral_tear,living_matter,curse,potion_of_purity,curse_amulet,
			infernium,scorching_pickaxe,arcane_shard,obsidian_fragment,obsidian_rod,auricion,energy_wand_core,energy_wand,
			enderman_head,bucket_ender_goo,music_disk,
			knowledge_note,exp_bottle,
			spawn_eggs
		);
		
		ModCreativeTab.tabCharms.list.addItems(
			charm
		);
	}
	
	private ItemList(){} // static class
}
