package chylex.hee.init;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCompressed;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.oredict.OreDictionary;
import chylex.hee.block.*;
import chylex.hee.block.override.BlockDragonEggCustom;
import chylex.hee.block.override.BlockEndPortalCustom;
import chylex.hee.block.vanilla.BlockBasic;
import chylex.hee.block.vanilla.BlockBasicSlab;
import chylex.hee.block.vanilla.BlockBasicStairs;
import chylex.hee.item.block.ItemBlockEndFlower;
import chylex.hee.item.block.ItemBlockEnhanceableTile;
import chylex.hee.item.block.ItemBlockEssenceAltar;
import chylex.hee.item.block.ItemBlockSlab;
import chylex.hee.item.block.ItemBlockWithSubtypes;
import chylex.hee.system.collections.DefaultingHashMap;
import chylex.hee.system.creativetab.ModCreativeTab;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.GameRegistryUtil;
import chylex.hee.tileentity.*;

public final class BlockList{
	private static final Map<String,Block> blocks = new HashMap<>();
	private static final Map<String,Class<? extends ItemBlock>> itemBlocks = new DefaultingHashMap<String,Class<? extends ItemBlock>>(ItemBlock.class);
	
	public static final Random blockRandom = new Random();
	
	private static void register(String blockIdentifier, Block block){
		blocks.put(blockIdentifier,block);
	}
	
	private static void setItemClass(String blockIdentifier, Class<? extends ItemBlock> itemBlockClass){
		itemBlocks.put(blockIdentifier,itemBlockClass);
	}
	
	public static Block getBlock(String identifier){
		return blocks.get(identifier);
	}
	
	public static Collection<Block> getAllBlocks(){
		return blocks.values();
	}
	
	// BUILDING BLOCKS
	
	public static Block obsidian_falling;
	public static Block obsidian_stairs;
	public static Block obsidian_special;
	public static Block obsidian_special_glow;
	public static Block end_terrain;
	public static Block ravaged_brick;
	public static Block ravaged_brick_smooth;
	public static Block ravaged_brick_glow;
	public static Block ravaged_brick_slab;
	public static Block ravaged_brick_stairs;
	public static Block ravaged_brick_fence;
	public static Block dungeon_puzzle;
	public static Block cinder;
	public static Block persegrit;
	public static Block laboratory_obsidian;
	public static Block laboratory_floor;
	public static Block laboratory_stairs;
	// TODO SANCTUARY public static Block sacred_stone;
	
	// ORES
	
	public static Block end_powder_ore;
	public static Block endium_ore;
	public static Block stardust_ore;
	public static Block igneous_rock_ore;
	public static Block instability_orb_ore;
	public static Block sphalerite;
	
	// FUNCTIONAL BLOCKS
	
	public static Block essence_altar;
	public static Block enhanced_brewing_stand;
	public static Block enhanced_tnt;
	public static Block void_chest;
	public static Block decomposition_table;
	public static Block experience_table;
	public static Block accumulation_table;
	public static Block extraction_table;
	public static Block transport_beacon;
	
	// OPAQUE BLOCKS
	
	public static Block endium_block;
	public static Block spooky_log;
	public static Block spooky_leaves;
	
	// TRANSPARENT BLOCKS
	
	public static Block ender_goo;
	public static Block crossed_decoration;
	public static Block enderman_head;
	public static Block death_flower;
	public static Block energy_cluster;
	public static Block corrupted_energy_high;
	public static Block corrupted_energy_low;
	public static Block laboratory_glass;
	
	// TECHNICAL
	
	public static Block death_flower_pot;
	public static Block laser_beam;
	public static Block custom_spawner;
	public static Block biome_core;
	// TODO SANCTUARY public static Block sanctuary_brain;
	// TODO SANCTUARY public static Block sanctuary_barrier;
	public static Block special_effects;
	
	// LOAD
	
	public static void loadBlocks(){
		FluidRegistry.registerFluid(BlockEnderGoo.fluid);
		
		register("obsidian_end", obsidian_falling = new BlockObsidianEnd().setHardness(50F).setResistance(2000F).setStepSound(Block.soundTypeStone).setBlockName("obsidianEnd").setBlockTextureName("obsidian"));
		register("obsidian_stairs", obsidian_stairs = new BlockBasicStairs(Blocks.obsidian,0).setHardness(25F).setResistance(1000F).setBlockName("obsidianStairs"));
		register("obsidian_special", obsidian_special = new BlockObsidianSpecial(false).setHardness(28F).setResistance(2000F).setStepSound(Block.soundTypeStone).setBlockName("obsidianSpecial").setBlockTextureName("hardcoreenderexpansion:obsidian_smooth"));
		register("obsidian_special_glow", obsidian_special_glow = new BlockObsidianSpecial(true).setHardness(28F).setResistance(2000F).setLightLevel(1F).setStepSound(Block.soundTypeStone).setBlockName("obsidianSpecial").setBlockTextureName("hardcoreenderexpansion:obsidian_smooth"));
		register("end_stone_terrain", end_terrain = new BlockEndstoneTerrain().setHardness(2.5F).setResistance(15F).setStepSound(Block.soundTypeStone).setBlockName("endStoneTerrain"));
		register("ravaged_brick", ravaged_brick = new BlockRavagedBrick().setHardness(3.5F).setResistance(28F).setStepSound(Block.soundTypePiston).setBlockName("ravagedBrick"));
		register("ravaged_brick_smooth", ravaged_brick_smooth = new BlockRavagedBrickSmooth().setHardness(1F).setResistance(6F).setStepSound(Block.soundTypePiston).setBlockName("ravagedBrickSmooth").setBlockTextureName("hardcoreenderexpansion:ravaged_brick_smooth"));
		register("ravaged_brick_glow", ravaged_brick_glow = new BlockBasic(Material.rock).setHardness(1F).setResistance(6F).setLightLevel(1F).setStepSound(Block.soundTypePiston).setBlockName("ravagedBrickGlow").setBlockTextureName("hardcoreenderexpansion:ravaged_brick_glow"));
		register("ravaged_brick_slab", ravaged_brick_slab = new BlockBasicSlab(ravaged_brick).setHardness(1.75F).setResistance(14F).setStepSound(Block.soundTypePiston).setBlockName("ravagedBrickSlab").setBlockTextureName("hardcoreenderexpansion:ravaged_brick"));
		register("ravaged_brick_stairs", ravaged_brick_stairs = new BlockBasicStairs(ravaged_brick,0).setHardness(2.25F).setResistance(22F).setStepSound(Block.soundTypePiston).setBlockName("ravagedBrickStairs"));
		register("ravaged_brick_fence", ravaged_brick_fence = new BlockFence("hardcoreenderexpansion:ravaged_brick",Material.rock).setHardness(1.5F).setResistance(6F).setStepSound(Block.soundTypePiston).setBlockName("ravagedBrickFence"));
		register("dungeon_puzzle", dungeon_puzzle = new BlockDungeonPuzzle().setBlockUnbreakable().setResistance(6000000F).setStepSound(Block.soundTypeMetal).setBlockName("dungeonPuzzle"));
		register("cinder", cinder = new BlockBasic(Material.rock).setHardness(1F).setResistance(10F).setStepSound(Block.soundTypeStone).setBlockName("cinder").setBlockTextureName("hardcoreenderexpansion:cinder"));
		register("persegrit", persegrit = new BlockPersegrit().setHardness(4F).setResistance(0.2F).setStepSound(Block.soundTypeGravel).setBlockName("persegrit").setBlockTextureName("hardcoreenderexpansion:persegrit"));
		register("laboratory_obsidian", laboratory_obsidian = new BlockBasic(Material.rock).setHardness(40F).setResistance(500F).setStepSound(Block.soundTypeStone).setBlockName("laboratoryObsidian").setBlockTextureName("hardcoreenderexpansion:laboratory_obsidian"));
		register("laboratory_floor", laboratory_floor = new BlockBasic(Material.rock).setHardness(30F).setResistance(100F).setStepSound(Block.soundTypeStone).setBlockName("laboratoryFloor").setBlockTextureName("hardcoreenderexpansion:laboratory_floor"));
		register("laboratory_stairs", laboratory_stairs = new BlockBasicStairs(laboratory_floor,0).setHardness(20F).setResistance(80F).setStepSound(Block.soundTypeStone).setBlockName("laboratoryStairs"));
		// TODO SANCTUARY register("sacred_stone", sacred_stone = new BlockSacredStone().setBlockUnbreakable().setResistance(6000000F).setStepSound(Block.soundTypeMetal).setBlockName("sacredStone").setBlockTextureName("hardcoreenderexpansion:sacred_stone"));
		
		register("end_powder_ore", end_powder_ore = new BlockEndPowderOre().setHardness(3F).setResistance(12F).setStepSound(Block.soundTypeStone).setBlockName("endPowderOre").setBlockTextureName("hardcoreenderexpansion:end_powder_ore"));
		register("endium_ore", endium_ore = new BlockEndiumOre().setHardness(18F).setResistance(100F).setStepSound(Block.soundTypeStone).setBlockName("endiumOre").setBlockTextureName("hardcoreenderexpansion:endium_ore"));
		register("stardust_ore", stardust_ore = new BlockStardustOre().setHardness(7F).setResistance(4F).setStepSound(Block.soundTypeStone).setBlockName("stardustOre").setBlockTextureName("hardcoreenderexpansion:stardust_ore"));
		register("igneous_rock_ore", igneous_rock_ore = new BlockIgneousRockOre().setHardness(2F).setResistance(5F).setStepSound(Block.soundTypeStone).setBlockName("igneousRockOre").setBlockTextureName("hardcoreenderexpansion:igneous_rock_ore"));
		register("instability_orb_ore", instability_orb_ore = new BlockInstabilityOrbOre().setHardness(5.5F).setResistance(3F).setStepSound(Block.soundTypeStone).setBlockName("instabilityOrbOre").setBlockTextureName("hardcoreenderexpansion:instability_orb_ore"));
		register("sphalerite", sphalerite = new BlockSphalerite().setHardness(1.8F).setResistance(40F).setBlockName("sphalerite").setBlockTextureName("hardcoreenderexpansion:sphalerite"));
		
		register("essence_altar", essence_altar = new BlockEssenceAltar().setHardness(8F).setResistance(20F).setLightOpacity(0).setLightLevel(0.4F).setStepSound(Block.soundTypeMetal).setBlockName("essenceAltar").setBlockTextureName("hardcoreenderexpansion:essence_altar"));
		register("enhanced_brewing_stand_block", enhanced_brewing_stand = new BlockEnhancedBrewingStand().setHardness(0.65F).setLightLevel(0.125F).setBlockName("brewingStand").setBlockTextureName("hardcoreenderexpansion:enhanced_brewing_stand"));
		register("enhanced_tnt", enhanced_tnt = new BlockEnhancedTNT().setHardness(0F).setStepSound(Block.soundTypeGrass).setBlockName("tnt").setBlockTextureName("tnt"));
		register("void_chest", void_chest = new BlockVoidChest().setHardness(5F).setResistance(140F).setStepSound(Block.soundTypePiston).setBlockName("voidChest"));
		register("decomposition_table", decomposition_table = new BlockDecompositionTable().setBlockName("decompositionTable").setBlockTextureName("decomposition_table"));
		register("experience_table", experience_table = new BlockExperienceTable().setBlockName("experienceTable").setBlockTextureName("experience_table"));
		register("accumulation_table", accumulation_table = new BlockAccumulationTable().setBlockName("accumulationTable").setBlockTextureName("accumulation_table"));
		register("energy_extraction_table", extraction_table = new BlockExtractionTable().setBlockName("extractionTable").setBlockTextureName("extraction_table"));
		register("transport_beacon", transport_beacon = new BlockTransportBeacon().setBlockUnbreakable().setResistance(6000000F).setLightLevel(1F).setBlockName("transportBeacon").setBlockTextureName("hardcoreenderexpansion:transport_beacon"));
		
		register("endium_block", endium_block = new BlockCompressed(MapColor.pinkColor).setHardness(14F).setResistance(800F).setStepSound(Block.soundTypeMetal).setBlockName("endiumBlock").setBlockTextureName("hardcoreenderexpansion:endium_block"));
		register("spooky_log", spooky_log = new BlockSpookyLog().setHardness(0.7F).setStepSound(Block.soundTypeWood).setBlockName("spookyLog"));
		register("spooky_leaves", spooky_leaves = new BlockSpookyLeaves().setHardness(0.1F).setStepSound(Block.soundTypeGrass).setBlockName("spookyLeaves").setBlockTextureName("hardcoreenderexpansion:spooky_leaves"));
		
		register("ender_goo", ender_goo = new BlockEnderGoo().setHardness(150F).setLightOpacity(2).setBlockName("enderGoo").setBlockTextureName("hardcoreenderexpansion:endergoo_flow"));
		register("crossed_decoration", crossed_decoration = new BlockCrossedDecoration().setHardness(0F).setStepSound(Block.soundTypeGrass).setCreativeTab(null).setBlockName("crossedDecoration"));
		register("enderman_head_block", enderman_head = new BlockEndermanHead().setHardness(1F).setStepSound(Block.soundTypeStone).setBlockName("endermanHead").setBlockTextureName("hardcoreenderexpansion:enderman_head"));
		register("death_flower", death_flower = new BlockDeathFlower().setHardness(0F).setResistance(4F).setStepSound(Block.soundTypeGrass).setCreativeTab(null).setBlockName("endFlower").setBlockTextureName("hardcoreenderexpansion:end_flower"));
		register("energy_cluster", energy_cluster = new BlockEnergyCluster().setHardness(0.7F).setResistance(0.2F).setStepSound(BlockEnergyCluster.soundTypeEnergyCluster).setBlockName("energyCluster").setBlockTextureName("hardcoreenderexpansion:energy_cluster"));
		register("corrupted_energy_high", corrupted_energy_high = new BlockCorruptedEnergy(true).setBlockUnbreakable().setResistance(6000000F));
		register("corrupted_energy_low", corrupted_energy_low = new BlockCorruptedEnergy(false).setBlockUnbreakable().setResistance(6000000F));
		register("laboratory_glass", laboratory_glass = new BlockLaboratoryGlass().setHardness(5F).setResistance(50F).setLightOpacity(5).setBlockName("laboratoryGlass").setBlockTextureName("hardcoreenderexpansion:laboratory_glass"));
		
		register("death_flower_pot", death_flower_pot = new BlockDeathFlowerPot().setHardness(0F).setStepSound(Block.soundTypeStone).setBlockName("flowerPot").setBlockTextureName("flower_pot"));
		register("laser_beam", laser_beam = new BlockLaserBeam().setBlockUnbreakable().setLightLevel(1F).setResistance(6000000F).setBlockName("laserBeam").setBlockTextureName("hardcoreenderexpansion:laser_beam"));
		register("custom_spawner", custom_spawner = new BlockCustomSpawner().setHardness(5F).setStepSound(Block.soundTypeMetal).setBlockName("mobSpawner").setBlockTextureName("mob_spawner"));
		register("biome_core", biome_core = new BlockBiomeIslandCore().setBlockUnbreakable().setResistance(6000000F).setStepSound(Block.soundTypeStone).setBlockName("biomeIslandCore").setBlockTextureName("bedrock"));
		// TODO SANCTUARY register("sanctuary_brain", sanctuary_brain = new BlockSanctuaryBrain().setBlockUnbreakable().setResistance(6000000F).setStepSound(Block.soundTypeStone).setBlockName("sanctuaryBrain").setBlockTextureName("bedrock"));
		// TODO SANCTUARY register("sanctuary_barrier", sanctuary_barrier = new BlockBasic(Material.rock).setBlockUnbreakable().setResistance(6000000F).setBlockName("sanctuaryBarrier").setBlockTextureName("hardcoreenderexpansion:sacred_stone_1"));
		register("block_special_effects", special_effects = new BlockSpecialEffects());
		
		setItemClass("obsidian_special", ItemBlockWithSubtypes.class);
		setItemClass("obsidian_special_glow", ItemBlockWithSubtypes.class);
		setItemClass("essence_altar", ItemBlockEssenceAltar.class);
		setItemClass("enhanced_brewing_stand_block", ItemBlockEnhanceableTile.class);
		setItemClass("enhanced_tnt", ItemBlockEnhanceableTile.class);
		setItemClass("end_stone_terrain", ItemBlockWithSubtypes.class);
		setItemClass("crossed_decoration", ItemBlockWithSubtypes.class);
		setItemClass("death_flower", ItemBlockEndFlower.class);
		setItemClass("sphalerite", ItemBlockWithSubtypes.class);
		setItemClass("ravaged_brick", ItemBlockWithSubtypes.class);
		setItemClass("ravaged_brick_slab", ItemBlockSlab.class);
		setItemClass("dungeon_puzzle", ItemBlockWithSubtypes.class);
		setItemClass("block_special_effects", ItemBlockWithSubtypes.class);
		setItemClass("persegrit", ItemBlockWithSubtypes.class);
		setItemClass("sacred_stone", ItemBlockWithSubtypes.class);
	}
	
	public static void registerBlocks(){
		BlockReplaceHelper.replaceBlock(Blocks.dragon_egg, new BlockDragonEggCustom());
		BlockReplaceHelper.replaceBlock(Blocks.end_portal,new BlockEndPortalCustom());
		
		for(Entry<String,Block> entry:BlockList.blocks.entrySet()){
			GameRegistryUtil.registerBlock(entry.getValue(),entry.getKey(),itemBlocks.get(entry.getKey()));
		}
		
		ModCreativeTab.tabMain.list.addBlocks(
			Blocks.dragon_egg,obsidian_falling,obsidian_special,obsidian_special_glow,obsidian_stairs,
			essence_altar,transport_beacon,void_chest,decomposition_table,experience_table,accumulation_table,extraction_table,
			end_powder_ore,endium_ore,stardust_ore,igneous_rock_ore,instability_orb_ore,energy_cluster,
			endium_block,
			sphalerite,end_terrain,spooky_log,spooky_leaves,
			ravaged_brick,ravaged_brick_smooth,ravaged_brick_glow,ravaged_brick_slab,ravaged_brick_stairs,ravaged_brick_fence,
			dungeon_puzzle,cinder,persegrit,laboratory_obsidian,laboratory_floor,laboratory_stairs,laboratory_glass,
			/* TODO SANCTUARY sacred_stone,*/crossed_decoration,death_flower
		);
		
		if (Log.isDeobfEnvironment)ModCreativeTab.tabMain.list.addBlocks(special_effects);
	}
	
	public static void configureBlocks(){
		BlockList.obsidian_falling.setHarvestLevel("pickaxe", 3);
		BlockList.obsidian_stairs.setHarvestLevel("pickaxe", 3);
		BlockList.obsidian_special.setHarvestLevel("pickaxe", 3);
		BlockList.obsidian_special_glow.setHarvestLevel("pickaxe", 3);
		BlockList.stardust_ore.setHarvestLevel("pickaxe", 3);
		BlockList.igneous_rock_ore.setHarvestLevel("pickaxe", 2);
		BlockList.instability_orb_ore.setHarvestLevel("pickaxe", 3);
		BlockList.sphalerite.setHarvestLevel("pickaxe", 1);
		BlockList.end_terrain.setHarvestLevel("pickaxe", 1);
		BlockList.cinder.setHarvestLevel("pickaxe", 2);
		BlockList.spooky_log.setHarvestLevel("axe", 0);
		BlockList.persegrit.setHarvestLevel("shovel", 0);
		BlockList.laboratory_obsidian.setHarvestLevel("pickaxe", 2);
		BlockList.laboratory_floor.setHarvestLevel("pickaxe", 2);
		Blocks.fire.setFireInfo(BlockList.spooky_log, 10, 10);
		Blocks.fire.setFireInfo(BlockList.spooky_leaves, 40, 30);
		
		OreDictionary.registerOre("blockHeeEndium", BlockList.endium_block);
		OreDictionary.registerOre("oreHeeEndium", BlockList.endium_ore);
		OreDictionary.registerOre("oreHeeEndPowder", BlockList.end_powder_ore);
		OreDictionary.registerOre("oreHeeStardust", BlockList.stardust_ore);
		OreDictionary.registerOre("oreHeeIgneousRock", BlockList.igneous_rock_ore);
		OreDictionary.registerOre("oreHeeInstabilityOrb", BlockList.instability_orb_ore);
		
		MinecraftForge.EVENT_BUS.register(BlockList.essence_altar);
		MinecraftForge.EVENT_BUS.register(BlockList.ender_goo);
	}
	
	public static void registerTileEntities(){
		GameRegistryUtil.registerTileEntity(TileEntityEssenceAltar.class, "EssenceAltar");
		GameRegistryUtil.registerTileEntity(TileEntityEnhancedBrewingStand.class, "EnhancedBrewingStand");
		GameRegistryUtil.registerTileEntity(TileEntityEndermanHead.class, "EndermanHead");
		GameRegistryUtil.registerTileEntity(TileEntityLaserBeam.class, "LaserBeam");
		GameRegistryUtil.registerTileEntity(TileEntityCustomSpawner.class, "EndermanSpawner");
		GameRegistryUtil.registerTileEntity(TileEntityDecompositionTable.class, "DecompositionTable");
		GameRegistryUtil.registerTileEntity(TileEntityExperienceTable.class, "ExperienceTable");
		GameRegistryUtil.registerTileEntity(TileEntityAccumulationTable.class, "AccumulationTable");
		GameRegistryUtil.registerTileEntity(TileEntityExtractionTable.class, "EnergyExtractionTable");
		GameRegistryUtil.registerTileEntity(TileEntityEnergyCluster.class, "EnergyCluster");
		GameRegistryUtil.registerTileEntity(TileEntityEnhancedTNT.class, "EnhancedTNT");
		GameRegistryUtil.registerTileEntity(TileEntityVoidChest.class, "VoidChest");
		GameRegistryUtil.registerTileEntity(TileEntityTransportBeacon.class, "TransportBeacon");
		GameRegistryUtil.registerTileEntity(TileEntityEndPortalCustom.class, "EndPortal");
		// TODO SANCTUARY GameRegistryUtil.registerTileEntity(TileEntitySanctuaryBrain.class, "SanctuaryBrain");
	}
	
	private BlockList(){} // static class
}
