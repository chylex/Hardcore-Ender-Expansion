package chylex.hee.block;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCompressed;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.item.block.ItemBlockEndFlower;
import chylex.hee.item.block.ItemBlockEnhancedTNT;
import chylex.hee.item.block.ItemBlockEssenceAltar;
import chylex.hee.item.block.ItemBlockSlab;
import chylex.hee.item.block.ItemBlockSlab.IBlockSlab;
import chylex.hee.item.block.ItemBlockWithSubtypes;
import chylex.hee.system.creativetab.ModCreativeTab;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.util.GameRegistryUtil;

public final class BlockList{
	private static final Map<String,BlockData> blocks = new HashMap<>();
	static final Random blockRandom = new Random();
	
	private static void register(String blockIdentifier, Block block){
		blocks.put(blockIdentifier,new BlockData(block));
	}
	
	private static void setItemClass(String blockIdentifier, Class<? extends ItemBlock> itemBlockClass){
		blocks.get(blockIdentifier).itemBlockClass = itemBlockClass;
	}
	
	public static Block getBlock(String identifier){
		return blocks.get(identifier).block;
	}
	
	public static Collection<BlockData> getAllBlocks(){
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
	public static Block soul_charm;
	public static Block laboratory_glass;
	
	// TECHNICAL
	
	public static Block death_flower_pot;
	public static Block laser_beam;
	public static Block custom_spawner;
	public static Block temple_end_portal;
	public static Block biome_core;
	public static Block special_effects;
	
	// LOAD
	
	public static void loadBlocks(){
		FluidRegistry.registerFluid(BlockEnderGoo.fluid);
		
		register("obsidian_end", obsidian_falling = new BlockObsidianEnd().setHardness(50F).setResistance(2000F).setStepSound(Block.soundTypeStone).setUnlocalizedName("obsidianEnd"));
		register("obsidian_stairs", obsidian_stairs = new BlockBasicStairs(Blocks.obsidian.getDefaultState()).setHardness(25F).setResistance(1000F).setUnlocalizedName("obsidianStairs"));
		register("obsidian_special", obsidian_special = new BlockObsidianSpecial(false).setHardness(28F).setResistance(2000F).setStepSound(Block.soundTypeStone).setUnlocalizedName("obsidianSpecial"));
		register("obsidian_special_glow", obsidian_special_glow = new BlockObsidianSpecial(true).setHardness(28F).setResistance(2000F).setLightLevel(1F).setStepSound(Block.soundTypeStone).setUnlocalizedName("obsidianSpecial"));
		register("end_stone_terrain", end_terrain = new BlockEndstoneTerrain().setHardness(2.5F).setResistance(15F).setStepSound(Block.soundTypeStone).setUnlocalizedName("endStoneTerrain"));
		register("ravaged_brick", ravaged_brick = new BlockRavagedBrick().setHardness(3.5F).setResistance(28F).setStepSound(Block.soundTypePiston).setUnlocalizedName("ravagedBrick"));
		register("ravaged_brick_smooth", ravaged_brick_smooth = new BlockRavagedBrickSmooth().setHardness(1F).setResistance(6F).setStepSound(Block.soundTypePiston).setUnlocalizedName("ravagedBrickSmooth"));
		register("ravaged_brick_glow", ravaged_brick_glow = new BlockBasic(Material.rock).setHardness(1F).setResistance(6F).setLightLevel(1F).setStepSound(Block.soundTypePiston).setUnlocalizedName("ravagedBrickGlow"));
		register("ravaged_brick_slab", ravaged_brick_slab = new BlockBasicSlab(ravaged_brick).setHardness(1.75F).setResistance(14F).setStepSound(Block.soundTypePiston).setUnlocalizedName("ravagedBrickSlab"));
		register("ravaged_brick_stairs", ravaged_brick_stairs = new BlockBasicStairs(ravaged_brick.getDefaultState()).setHardness(2.25F).setResistance(22F).setStepSound(Block.soundTypePiston).setUnlocalizedName("ravagedBrickStairs"));
		register("ravaged_brick_fence", ravaged_brick_fence = new BlockFence(Material.rock).setHardness(1.5F).setResistance(6F).setStepSound(Block.soundTypePiston).setUnlocalizedName("ravagedBrickFence"));
		register("dungeon_puzzle", dungeon_puzzle = new BlockDungeonPuzzle().setBlockUnbreakable().setResistance(6000000F).setStepSound(Block.soundTypeMetal).setUnlocalizedName("dungeonPuzzle"));
		register("cinder", cinder = new BlockBasic(Material.rock).setHardness(1F).setResistance(10F).setStepSound(Block.soundTypeStone).setUnlocalizedName("cinder"));
		register("persegrit", persegrit = new BlockPersegrit().setHardness(4F).setResistance(0.2F).setStepSound(Block.soundTypeGravel).setUnlocalizedName("persegrit"));
		register("laboratory_obsidian", laboratory_obsidian = new BlockBasic(Material.rock).setHardness(40F).setResistance(500F).setStepSound(Block.soundTypeStone).setUnlocalizedName("laboratoryObsidian"));
		register("laboratory_floor", laboratory_floor = new BlockBasic(Material.rock).setHardness(30F).setResistance(100F).setStepSound(Block.soundTypeStone).setUnlocalizedName("laboratoryFloor"));
		register("laboratory_stairs", laboratory_stairs = new BlockBasicStairs(laboratory_floor.getDefaultState()).setHardness(20F).setResistance(80F).setStepSound(Block.soundTypeStone).setUnlocalizedName("laboratoryStairs"));
		
		register("end_powder_ore", end_powder_ore = new BlockEndPowderOre().setHardness(3F).setResistance(12F).setStepSound(Block.soundTypeStone).setUnlocalizedName("endPowderOre"));
		register("endium_ore", endium_ore = new BlockEndiumOre().setHardness(18F).setResistance(100F).setStepSound(Block.soundTypeStone).setUnlocalizedName("endiumOre"));
		register("stardust_ore", stardust_ore = new BlockStardustOre().setHardness(7F).setResistance(4F).setStepSound(Block.soundTypeStone).setUnlocalizedName("stardustOre"));
		register("igneous_rock_ore", igneous_rock_ore = new BlockIgneousRockOre().setHardness(2F).setResistance(5F).setStepSound(Block.soundTypeStone).setUnlocalizedName("igneousRockOre"));
		register("instability_orb_ore", instability_orb_ore = new BlockInstabilityOrbOre().setHardness(5.5F).setResistance(3F).setStepSound(Block.soundTypeStone).setUnlocalizedName("instabilityOrbOre"));
		register("sphalerite", sphalerite = new BlockSphalerite().setHardness(1.8F).setResistance(40F).setUnlocalizedName("sphalerite"));
		
		register("essence_altar", essence_altar = new BlockEssenceAltar().setHardness(8F).setResistance(20F).setLightOpacity(0).setLightLevel(0.4F).setStepSound(Block.soundTypeMetal).setUnlocalizedName("essenceAltar"));
		register("enhanced_brewing_stand_block", enhanced_brewing_stand = new BlockEnhancedBrewingStand().setHardness(0.65F).setLightLevel(0.125F).setUnlocalizedName("brewingStand"));
		register("enhanced_tnt", enhanced_tnt = new BlockEnhancedTNT().setHardness(0F).setStepSound(Block.soundTypeGrass).setUnlocalizedName("tnt"));
		register("void_chest", void_chest = new BlockVoidChest().setHardness(5F).setResistance(140F).setStepSound(Block.soundTypePiston).setUnlocalizedName("voidChest"));
		register("decomposition_table", decomposition_table = new BlockDecompositionTable().setUnlocalizedName("decompositionTable"));
		register("experience_table", experience_table = new BlockExperienceTable().setUnlocalizedName("experienceTable"));
		register("accumulation_table", accumulation_table = new BlockAccumulationTable().setUnlocalizedName("accumulationTable"));
		register("energy_extraction_table", extraction_table = new BlockExtractionTable().setUnlocalizedName("extractionTable"));
		register("transport_beacon", transport_beacon = new BlockTransportBeacon().setBlockUnbreakable().setResistance(6000000F).setLightLevel(1F).setUnlocalizedName("transportBeacon"));
		
		register("endium_block", endium_block = new BlockCompressed(MapColor.pinkColor).setHardness(14F).setResistance(800F).setStepSound(Block.soundTypeMetal).setUnlocalizedName("endiumBlock"));
		register("spooky_log", spooky_log = new BlockSpookyLog().setHardness(0.7F).setStepSound(Block.soundTypeWood).setUnlocalizedName("spookyLog"));
		register("spooky_leaves", spooky_leaves = new BlockSpookyLeaves().setHardness(0.1F).setStepSound(Block.soundTypeGrass).setUnlocalizedName("spookyLeaves"));
		
		register("ender_goo", ender_goo = new BlockEnderGoo().setHardness(150F).setLightOpacity(2).setUnlocalizedName("enderGoo"));
		register("crossed_decoration", crossed_decoration = new BlockCrossedDecoration().setHardness(0F).setStepSound(Block.soundTypeGrass).setCreativeTab(null).setUnlocalizedName("crossedDecoration"));
		register("enderman_head_block", enderman_head = new BlockEndermanHead().setHardness(1F).setStepSound(Block.soundTypeStone).setUnlocalizedName("endermanHead"));
		register("death_flower", death_flower = new BlockDeathFlower().setHardness(0F).setResistance(4F).setStepSound(Block.soundTypeGrass).setCreativeTab(null).setUnlocalizedName("endFlower"));
		register("energy_cluster", energy_cluster = new BlockEnergyCluster().setHardness(0.7F).setResistance(0.2F).setStepSound(BlockEnergyCluster.soundTypeEnergyCluster).setUnlocalizedName("energyCluster"));
		register("corrupted_energy_high", corrupted_energy_high = new BlockCorruptedEnergy(true).setBlockUnbreakable().setResistance(6000000F));
		register("corrupted_energy_low", corrupted_energy_low = new BlockCorruptedEnergy(false).setBlockUnbreakable().setResistance(6000000F));
		register("laboratory_glass", laboratory_glass = new BlockLaboratoryGlass().setHardness(5F).setResistance(50F).setLightOpacity(5).setUnlocalizedName("laboratoryGlass"));
		
		register("death_flower_pot", death_flower_pot = new BlockDeathFlowerPot().setHardness(0F).setStepSound(Block.soundTypeStone).setUnlocalizedName("flowerPot"));
		register("laser_beam", laser_beam = new BlockLaserBeam().setBlockUnbreakable().setLightLevel(1F).setResistance(6000000F).setUnlocalizedName("laserBeam"));
		register("custom_spawner", custom_spawner = new BlockCustomSpawner().setHardness(5F).setStepSound(Block.soundTypeMetal).setUnlocalizedName("mobSpawner"));
		register("temple_end_portal", temple_end_portal = new BlockTempleEndPortal().setHardness(-1F).setResistance(6000000F).setUnlocalizedName("templeEndPortal"));
		register("biome_core", biome_core = new BlockBiomeIslandCore().setBlockUnbreakable().setStepSound(Block.soundTypeStone).setUnlocalizedName("biomeIslandCore"));
		register("block_special_effects", special_effects = new BlockSpecialEffects());
		
		setItemClass("obsidian_special", ItemBlockWithSubtypes.class);
		setItemClass("obsidian_special_glow", ItemBlockWithSubtypes.class);
		setItemClass("essence_altar", ItemBlockEssenceAltar.class);
		setItemClass("enhanced_tnt", ItemBlockEnhancedTNT.class);
		setItemClass("end_stone_terrain", ItemBlockWithSubtypes.class);
		setItemClass("crossed_decoration", ItemBlockWithSubtypes.class);
		setItemClass("death_flower", ItemBlockEndFlower.class);
		setItemClass("sphalerite", ItemBlockWithSubtypes.class);
		setItemClass("ravaged_brick", ItemBlockWithSubtypes.class);
		setItemClass("ravaged_brick_slab", ItemBlockSlab.class);
		setItemClass("dungeon_puzzle", ItemBlockWithSubtypes.class);
		setItemClass("block_special_effects", ItemBlockWithSubtypes.class);
		setItemClass("persegrit", ItemBlockWithSubtypes.class);
	}
	
	public static void registerBlocks(){
		Stopwatch.time("BlockList - register");
		
		for(Entry<String,BlockData> entry:BlockList.blocks.entrySet()){
			GameRegistryUtil.registerBlock(entry.getValue().block,entry.getKey(),entry.getValue().itemBlockClass);
		}
		
		Stopwatch.finish("BlockList - register");
		
		ModCreativeTab.tabMain.list.addBlocks(
			Blocks.dragon_egg,obsidian_falling,obsidian_special,obsidian_special_glow,obsidian_stairs,
			essence_altar,transport_beacon,void_chest,decomposition_table,experience_table,accumulation_table,extraction_table,
			end_powder_ore,endium_ore,stardust_ore,igneous_rock_ore,instability_orb_ore,energy_cluster,
			endium_block,
			sphalerite,end_terrain,spooky_log,spooky_leaves,
			ravaged_brick,ravaged_brick_smooth,ravaged_brick_glow,ravaged_brick_slab,ravaged_brick_stairs,ravaged_brick_fence,
			dungeon_puzzle,cinder,persegrit,laboratory_obsidian,laboratory_floor,laboratory_stairs,laboratory_glass,
			crossed_decoration,death_flower
		);
		
		if (Log.isDeobfEnvironment)ModCreativeTab.tabMain.list.addBlocks(special_effects);
	}
	
	private BlockList(){} // static class
	
	public static final class BlockData{
		public final Block block;
		public Class<? extends ItemBlock> itemBlockClass = ItemBlock.class;
		
		public BlockData(Block block){
			this.block = block;
		}
	}
	
	public static class BlockBasic extends Block{
		public BlockBasic(Material material){
			super(material);
		}
	}
	
	public static class BlockBasicStairs extends BlockStairs{
		public BlockBasicStairs(IBlockState modelState){
			super(modelState);
		}
	}
	
	public static class BlockBasicSlab extends BlockSlab implements IBlockSlab{
		private final Block fullBlock;
		
		public BlockBasicSlab(Block fullBlock){
			super(fullBlock.getMaterial());
			this.fullBlock = fullBlock;
		}

		@Override
		public String func_150002_b(int meta){
			return getUnlocalizedName();
		}

		@Override
		public Block getFullBlock(){
			return fullBlock;
		}
		
		@Override
		public Item getItemDropped(IBlockState state, Random rand, int fortune){
			return Item.getItemFromBlock(this);
		}
		
		@Override
		protected ItemStack createStackedBlock(IBlockState state){
			return new ItemStack(Item.getItemFromBlock(this),1,0);
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public Item getItem(World world, BlockPos pos){
			return Item.getItemFromBlock(this);
		}
	}
}
