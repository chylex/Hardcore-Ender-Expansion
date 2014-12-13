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
	public static Item fire_shard;
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
	public static Item corporeal_mirage_orb;
	public static Item charm_pouch;
	public static Item charm;
	public static Item scorching_pickaxe;
	public static Item energy_wand;
	
	// WORLD
	
	public static Item enderman_head;
	public static Item bucket_ender_goo;
	public static Item music_disk;
	
	// TECHNICAL
	
	public static Item spawn_eggs;
	public static Item special_effects;
	
	// LOAD
	
	public static void loadItems(){
		register("adventurers_diary", adventurers_diary = new ItemAdventurersDiary().setMaxStackSize(1).setUnlocalizedName("lorePage").setTextureName("hardcoreenderexpansion:lore_page"));
		register("knowledge_note", knowledge_note = new ItemKnowledgeNote().setMaxStackSize(1).setUnlocalizedName("knowledgeNote").setTextureName("hardcoreenderexpansion:knowledge_fragment"));
		
		register("end_powder", end_powder = new ItemEndPowder().setUnlocalizedName("endPowder").setTextureName("hardcoreenderexpansion:end_powder"));
		register("endium_ingot", endium_ingot = new Item().setUnlocalizedName("endiumIngot").setTextureName("hardcoreenderexpansion:endium_ingot"));
		register("stardust", stardust = new Item().setUnlocalizedName("stardust").setTextureName("hardcoreenderexpansion:stardust"));
		register("igneous_rock", igneous_rock = new ItemIgneousRock().setUnlocalizedName("igneousRock").setTextureName("hardcoreenderexpansion:igneous_rock"));
		register("instability_orb", instability_orb = new ItemInstabilityOrb().setUnlocalizedName("instabilityOrb").setTextureName("hardcoreenderexpansion:instability_orb"));
		
		register("altar_nexus", altar_nexus = new Item().setUnlocalizedName("altarNexus").setTextureName("hardcoreenderexpansion:altar_nexus"));
		register("essence", essence = new ItemEssence().setUnlocalizedName("essence"));
		register("silverfish_blood", silverfish_blood = new Item().setUnlocalizedName("silverfishBlood").setTextureName("hardcoreenderexpansion:silverfish_blood"));
		register("dry_splinter", dry_splinter = new Item().setUnlocalizedName("drySplinter").setTextureName("hardcoreenderexpansion:dry_splinter"));
		register("endoplasm", ectoplasm = new Item().setUnlocalizedName("ectoplasm").setTextureName("hardcoreenderexpansion:ectoplasm"));
		register("spectral_tear", spectral_tear = new Item().setUnlocalizedName("spectralTear").setTextureName("hardcoreenderexpansion:spectral_tear"));
		register("living_matter", living_matter = new Item().setUnlocalizedName("livingMatter").setTextureName("hardcoreenderexpansion:living_matter"));
		register("rune", rune = new ItemRune().setMaxStackSize(16).setUnlocalizedName("rune").setTextureName("hardcoreenderexpansion:rune"));
		register("fire_shard", fire_shard = new Item().setUnlocalizedName("fireShard").setTextureName("hardcoreenderexpansion:fire_shard"));
		register("obsidian_fragment", obsidian_fragment = new Item().setUnlocalizedName("obsidianFragment").setTextureName("hardcoreenderexpansion:obsidian_fragment"));
		register("obsidian_rod", obsidian_rod = new Item().setUnlocalizedName("obsidianRod").setTextureName("hardcoreenderexpansion:obsidian_rod"));
		register("auricion", auricion = new Item().setUnlocalizedName("auricion").setTextureName("hardcoreenderexpansion:auricion"));
		register("energy_wand_core", energy_wand_core = new Item().setUnlocalizedName("energyWandCore").setTextureName("hardcoreenderexpansion:energy_wand_core"));
		
		register("enhanced_brewing_stand", enhanced_brewing_stand = new ItemReed(BlockList.enhanced_brewing_stand).setUnlocalizedName("enhancedBrewingStand").setTextureName("hardcoreenderexpansion:enhanced_brewing_stand"));
		register("enhanced_ender_pearl", enhanced_ender_pearl = new ItemEnhancedEnderPearl().setCreativeTab(null).setUnlocalizedName("enderPearl").setTextureName("ender_pearl"));
		register("potion_of_instability", potion_of_instability = new ItemPotionOfInstability().setMaxStackSize(1).setTextureName("hardcoreenderexpansion:potion_of_instability"));
		register("biome_compass", biome_compass = new ItemBiomeCompass().setMaxStackSize(1).setUnlocalizedName("biomeCompass"));
		register("spatial_dash_gem", spatial_dash_gem = new ItemSpatialDashGem().setMaxStackSize(1).setMaxDamage(300).setNoRepair().setUnlocalizedName("spatialDashGem").setTextureName("hardcoreenderexpansion:spatial_dash_gem"));
		register("transference_gem", transference_gem = new ItemTransferenceGem().setMaxStackSize(1).setMaxDamage(204).setNoRepair().setUnlocalizedName("transferenceGem").setTextureName("hardcoreenderexpansion:transference_gem"));
		register("temple_caller", temple_caller = new ItemTempleCaller().setMaxStackSize(1).setMaxDamage(50).setNoRepair().setUnlocalizedName("templeCaller").setTextureName("hardcoreenderexpansion:temple_caller"));
		register("infestation_remedy", infestation_remedy = new ItemInfestationRemedy().setMaxStackSize(1).setUnlocalizedName("infestationRemedy").setTextureName("hardcoreenderexpansion:infestation_remedy"));
		register("ghost_amulet", ghost_amulet = new ItemGhostAmulet().setUnlocalizedName("ghostAmulet").setTextureName("hardcoreenderexpansion:ghost_amulet"));
		register("corporeal_mirage_orb", corporeal_mirage_orb = new ItemCorporealMirageOrb().setMaxStackSize(4).setUnlocalizedName("corporealMirageOrb").setTextureName("hardcoreenderexpansion:corporeal_mirage_orb"));
		register("charm_pouch", charm_pouch = new ItemCharmPouch().setMaxStackSize(1).setUnlocalizedName("charmPouch").setTextureName("hardcoreenderexpansion:charm_pouch"));
		register("charm", charm = new ItemCharm().setMaxStackSize(1).setUnlocalizedName("charm").setTextureName("hardcoreenderexpansion:charm"));
		register("schorching_pickaxe", scorching_pickaxe = new ItemScorchingPickaxe().setMaxStackSize(1).setMaxDamage(399).setUnlocalizedName("scorchingPickaxe").setTextureName("hardcoreenderexpansion:scorching_pickaxe"));
		register("energy_wand", energy_wand = new ItemEnergyWand().setMaxStackSize(1).setUnlocalizedName("energyWand").setTextureName("hardcoreenderexpansion:energy_wand"));
		
		register("enderman_head", enderman_head = new ItemEndermanHead().setUnlocalizedName("endermanHead").setTextureName("hardcoreenderexpansion:enderman_head"));
		register("bucket_ender_goo", bucket_ender_goo = new ItemBucket(BlockList.ender_goo).setUnlocalizedName("bucketEnderGoo").setTextureName("hardcoreenderexpansion:bucket_ender_goo"));
		register("music_disk", music_disk = new ItemMusicDisk().setUnlocalizedName("record").setTextureName("hardcoreenderexpansion:music_disk"));
		
		register("spawn_eggs", spawn_eggs = new ItemSpawnEggs().setUnlocalizedName("monsterPlacer").setTextureName("spawn_egg"));
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
			biome_compass,spatial_dash_gem,transference_gem,temple_caller,
			silverfish_blood,dry_splinter,infestation_remedy,charm_pouch,rune,
			ghost_amulet,ectoplasm,spectral_tear,corporeal_mirage_orb
		).addBlocks(
			BlockList.soul_charm
		).addItems(
			fire_shard,scorching_pickaxe,obsidian_fragment,obsidian_rod,auricion,energy_wand_core,energy_wand,
			enderman_head,bucket_ender_goo,music_disk,
			knowledge_note,
			spawn_eggs
		);
		
		ModCreativeTab.tabCharms.list.addItems(
			charm
		);
	}
	
	private ItemList(){} // static class
}
