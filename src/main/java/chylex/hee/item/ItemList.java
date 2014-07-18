package chylex.hee.item;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemReed;
import chylex.hee.block.BlockList;
import chylex.hee.system.creativetab.CreativeTabItemList;

public final class ItemList{
	public static final CreativeTabItemList tabOrderedList = new CreativeTabItemList();
	
	public static Item adventurers_diary;
	public static Item altar_nexus;
	public static Item essence;
	public static Item enhanced_brewing_stand;
	public static Item enhanced_ender_pearl;
	public static Item end_powder;
	public static Item stardust;
	public static Item igneous_rock;
	public static Item instability_orb;
	public static Item potion_of_instability;
	public static Item biome_compass;
	public static Item spatial_dash_gem;
	public static Item transference_gem;
	public static Item temple_caller;
	public static Item silverfish_blood;
	public static Item dry_splinter;
	public static Item infestation_remedy;
	public static Item ghost_amulet;
	public static Item ectoplasm;
	public static Item corporeal_mirage_orb;
	public static Item spectral_wand;
	public static Item scorching_pickaxe;
	public static Item enderman_relic;
	public static Item enderman_head;
	public static Item bucket_ender_goo;
	public static Item music_disk;
	public static Item ender_compendium;
	public static Item knowledge_fragment;
	public static Item spawn_eggs;
	public static Item special_effects;
	
	public static void loadItems(){
		adventurers_diary = new ItemAdventurersDiary()
		.setMaxStackSize(1)
		.setUnlocalizedName("lorePage").setTextureName("hardcoreenderexpansion:lore_page");
		
		altar_nexus = new Item()
		.setUnlocalizedName("altarNexus").setTextureName("hardcoreenderexpansion:altar_nexus");
		
		essence = new ItemEssence()
		.setUnlocalizedName("essence");
		
		enhanced_brewing_stand = new ItemReed(BlockList.enhanced_brewing_stand)
		.setUnlocalizedName("enhancedBrewingStand").setTextureName("hardcoreenderexpansion:enhanced_brewing_stand");
		
		enhanced_ender_pearl = new ItemEnhancedEnderPearl()
		.setCreativeTab(null)
		.setUnlocalizedName("enderPearl").setTextureName("ender_pearl");
		
		end_powder = new ItemEndPowder()
		.setUnlocalizedName("endPowder").setTextureName("hardcoreenderexpansion:end_powder");
		
		stardust = new Item()
		.setUnlocalizedName("stardust").setTextureName("hardcoreenderexpansion:stardust");
		
		igneous_rock = new ItemIgneousRock()
		.setUnlocalizedName("igneousRock").setTextureName("hardcoreenderexpansion:igneous_rock");
		
		instability_orb = new ItemInstabilityOrb()
		.setUnlocalizedName("instabilityOrb").setTextureName("hardcoreenderexpansion:instability_orb");
		
		potion_of_instability = new ItemPotionOfInstability()
		.setMaxStackSize(1)
		.setTextureName("hardcoreenderexpansion:potion_of_instability");
		
		spatial_dash_gem = new ItemSpatialDashGem()
		.setMaxStackSize(1).setMaxDamage(300).setNoRepair()
		.setUnlocalizedName("spatialDashGem").setTextureName("hardcoreenderexpansion:spatial_dash_gem");
		
		transference_gem = new ItemTransferenceGem()
		.setMaxStackSize(1).setMaxDamage(108).setNoRepair()
		.setUnlocalizedName("transferenceGem").setTextureName("hardcoreenderexpansion:transference_gem");
		
		temple_caller = new ItemTempleCaller()
		.setMaxStackSize(1).setMaxDamage(50).setNoRepair()
		.setUnlocalizedName("templeCaller").setTextureName("hardcoreenderexpansion:temple_caller");
		
		biome_compass = new ItemBiomeCompass()
		.setMaxStackSize(1)
		.setUnlocalizedName("biomeCompass").setTextureName("hardcoreenderexpansion:biome_compass");

		silverfish_blood = new Item()
		.setUnlocalizedName("silverfishBlood").setTextureName("hardcoreenderexpansion:silverfish_blood");
		
		dry_splinter = new Item()
		.setUnlocalizedName("drySplinter").setTextureName("hardcoreenderexpansion:dry_splinter");
		
		infestation_remedy = new ItemInfestationRemedy()
		.setUnlocalizedName("infestationRemedy").setTextureName("hardcoreenderexpansion:infestation_remedy");
		
		ghost_amulet = new Item()
		.setUnlocalizedName("ghostAmulet").setTextureName("hardcoreenderexpansion:ghost_amulet");
		
		ectoplasm = new Item()
		.setUnlocalizedName("endoplasm").setTextureName("hardcoreenderexpansion:endoplasm");
		
		corporeal_mirage_orb = new ItemCorporealMirageOrb()
		.setMaxStackSize(4)
		.setUnlocalizedName("corporealMirageOrb").setTextureName("hardcoreenderexpansion:corporeal_mirage_orb");
		
		spectral_wand = new ItemSpectralWand()
		.setMaxStackSize(1)
		.setUnlocalizedName("spectralWand").setTextureName("hardcoreenderexpansion:spectral_wand");
		
		scorching_pickaxe = new ItemScorchingPickaxe()
		.setMaxStackSize(1).setMaxDamage(399)
		.setUnlocalizedName("scorchingPickaxe").setTextureName("hardcoreenderexpanison:scorching_pickaxe");
		
		enderman_relic = new ItemEndermanRelic()
		.setMaxStackSize(1).setMaxDamage(222).setNoRepair()
		.setUnlocalizedName("endermanRelicRepaired").setTextureName("hardcoreenderexpansion:enderman_relic_repaired");
		
		enderman_head = new ItemEndermanHead()
		.setUnlocalizedName("endermanHead").setTextureName("hardcoreenderexpansion:enderman_head");
		
		bucket_ender_goo = new ItemBucket(BlockList.ender_goo)
		.setUnlocalizedName("bucketEnderGoo").setTextureName("hardcoreenderexpansion:bucket_ender_goo");
		
		music_disk = new ItemMusicDisk()
		.setUnlocalizedName("record").setTextureName("hardcoreenderexpansion:music_disk");
		
		ender_compendium = new ItemEnderCompendium()
		.setMaxStackSize(1)
		.setUnlocalizedName("enderCompendium").setTextureName("hardcoreenderexpansion:ender_compendium");
		
		knowledge_fragment = new ItemKnowledgeFragment()
		.setMaxStackSize(1)
		.setUnlocalizedName("knowledgeFragment").setTextureName("hardcoreenderexpansion:knowledge_fragment");
		
		spawn_eggs = new ItemSpawnEggs()
		.setUnlocalizedName("monsterPlacer").setTextureName("spawn_egg");
		
		special_effects = new ItemSpecialEffects()
		.setUnlocalizedName("itemNumber");
		
		tabOrderedList.addItems(
			adventurers_diary,altar_nexus,essence,enhanced_brewing_stand,
			end_powder,stardust,igneous_rock,instability_orb,potion_of_instability,
			biome_compass,spatial_dash_gem,transference_gem,temple_caller,
			silverfish_blood,dry_splinter,infestation_remedy,
			ghost_amulet,ectoplasm,corporeal_mirage_orb
		).addBlocks(
			BlockList.soul_charm
		).addItems(
			spectral_wand,scorching_pickaxe,enderman_relic,
			enderman_head,bucket_ender_goo,music_disk,
			ender_compendium,knowledge_fragment,
			spawn_eggs
		);
	}
	
	private ItemList(){} // static class
}
