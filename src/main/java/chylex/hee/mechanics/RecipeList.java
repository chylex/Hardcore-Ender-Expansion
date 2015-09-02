package chylex.hee.mechanics;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import chylex.hee.block.BlockGloomrock;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Meta.BlockColor;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.util.CollectionUtil;
import chylex.hee.system.util.GameRegistryUtil;
import cpw.mods.fml.common.registry.GameRegistry;

public final class RecipeList{
	public static void addRecipes(){
		Stopwatch.time("RecipeList - addRecipes");
		
		// Remove unwanted recipes
		
		Set<Item> toRemove = CollectionUtil.newSet(new Item[]{
			Items.ender_eye,
			Item.getItemFromBlock(Blocks.ender_chest)
		});
		
		for(Iterator<IRecipe> iter = ((List<IRecipe>)CraftingManager.getInstance().getRecipeList()).iterator(); iter.hasNext();){
			ItemStack is = iter.next().getRecipeOutput();
			if (is != null && toRemove.remove(is.getItem()) && toRemove.isEmpty())break;
		}
		
		// Ethereum
		
		GameRegistry.addShapelessRecipe(new ItemStack(Items.ender_eye),
			Items.ender_pearl,
			ItemList.ethereum,
			Items.gold_nugget
		);
		
		// Stone Bricks
		
		GameRegistry.addShapelessRecipe(new ItemStack(Blocks.stonebrick,1,Meta.stoneBrickMossy),
			new ItemStack(Blocks.stonebrick,1,Meta.stoneBrickPlain),
			Blocks.vine
		);
		
		GameRegistryUtil.addSmeltingRecipe(
			new ItemStack(Blocks.stonebrick,1,Meta.stoneBrickPlain),
			new ItemStack(Blocks.stonebrick,1,Meta.stoneBrickCracked),
			0.1F
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(Blocks.stonebrick,1,Meta.stoneBrickChiseled),
			"S", "S",
			'S', new ItemStack(Blocks.stone_slab,1,Meta.slabStoneBrickBottom)
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.stone_brick_wall,6),
			"BBB", "BBB",
			'B', Blocks.stonebrick
		);
		
		// Gloomrock
		
		GameRegistryUtil.addSmeltingRecipe(
			new ItemStack(BlockList.gloomrock,1,BlockGloomrock.Meta.PLAIN.value),
			new ItemStack(BlockList.gloomrock,1,BlockGloomrock.Meta.SMOOTH.value),
			0.05F
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.gloomrock,4,BlockGloomrock.Meta.BRICK.value),
			"GG", "GG",
			'G', new ItemStack(BlockList.gloomrock,1,BlockGloomrock.Meta.SMOOTH.value)
		);
		
		Meta.BlockColor[] gloomrockColors = new Meta.BlockColor[]{
			BlockColor.RED, BlockColor.ORANGE, BlockColor.YELLOW, BlockColor.LIME, BlockColor.CYAN,
			BlockColor.LIGHT_BLUE, BlockColor.BLUE, BlockColor.PURPLE, BlockColor.MAGENTA,
			BlockColor.PINK, BlockColor.WHITE, BlockColor.GRAY, BlockColor.BLACK
		};
		
		for(int color = 0; color < gloomrockColors.length; color++){
			GameRegistry.addShapedRecipe(new ItemStack(BlockList.gloomrock,8,BlockGloomrock.Meta.firstColor+color),
				"GGG", "GDG", "GGG",
				'G', new ItemStack(BlockList.gloomrock,1,BlockGloomrock.Meta.SMOOTH.value),
				'D', new ItemStack(Items.dye,1,Meta.getDye(gloomrockColors[color]))
			);
		}
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.gloomrock_smooth_slab,6),
			"GGG",
			'G', new ItemStack(BlockList.gloomrock,1,BlockGloomrock.Meta.SMOOTH.value)
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.gloomrock_smooth_stairs,4),
			"  G", " GG", "GGG",
			'G', new ItemStack(BlockList.gloomrock,1,BlockGloomrock.Meta.SMOOTH.value)
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.gloomrock_brick_slab,6),
			"GGG",
			'G', new ItemStack(BlockList.gloomrock,1,BlockGloomrock.Meta.BRICK.value)
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.gloomrock_brick_stairs,4),
			"  G", " GG", "GGG",
			'G', new ItemStack(BlockList.gloomrock,1,BlockGloomrock.Meta.BRICK.value)
		);
		
		/*GameRegistry.addShapedRecipe(new ItemStack(ItemList.altar_nexus),
			"DED",
			'D', Items.diamond,
			'E', Items.ender_eye
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.essence_altar),
			"LLL", "BNB", "OOO",
			'B', Blocks.bookshelf,
			'L', Items.leather,
			'O', Blocks.obsidian,
			'N', ItemList.altar_nexus
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.void_chest),
			"PPP", "ECE", "PPP",
			'C', Blocks.ender_chest,
			'E', BlockList.endium_block,
			'P', ItemList.end_powder
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.decomposition_table),
			"PBP", "SCS", "INI",
			'P', ItemList.end_powder,
			'B', Blocks.iron_bars,
			'S', Blocks.stone,
			'C', ItemList.igneous_rock,
			'N', BlockList.endium_block,
			'I', Blocks.iron_block
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.experience_table),
			"PBP", "SCS", "INI",
			'P', ItemList.end_powder,
			'B', Blocks.iron_bars,
			'S', Blocks.stone,
			'C', Items.glass_bottle,
			'N', BlockList.endium_block,
			'I', Blocks.iron_block
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.accumulation_table),
			"PBP", "SCS", "INI",
			'P', ItemList.end_powder,
			'B', Blocks.iron_bars,
			'S', Blocks.stone,
			'C', ItemList.energy_wand_core,
			'N', BlockList.endium_block,
			'I', Blocks.iron_block
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.extraction_table),
			"PBP", "SCS", "INI",
			'P', ItemList.end_powder,
			'B', Blocks.iron_bars,
			'S', Blocks.stone,
			'C', ItemList.living_matter,
			'N', BlockList.endium_block,
			'I', Blocks.iron_block
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(ItemList.biome_compass),
			" N ", "NSN", " N ",
			'N', ItemList.endium_ingot,
			'S', ItemList.end_powder
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(ItemList.spectral_tear),
			" E ", "ETE", " E ",
			'E', ItemList.ectoplasm,
			'T', Items.ghast_tear
		);
		
		GameRegistry.addShapelessRecipe(new ItemStack(ItemList.living_matter),
			ItemList.silverfish_blood,
			Items.bone,
			Items.spider_eye,
			ItemList.spectral_tear,
			ItemList.instability_orb
		);
		
		for(CurseType curse:CurseType.values()){
			GameRegistry.addShapedRecipe(new ItemStack(ItemList.curse,8,curse.damage),
				"PAP", "DLB", "PCP",
				'P', ItemList.end_powder,
				'L', ItemList.living_matter,
				'A', curse.getRecipeItem(0),
				'B', curse.getRecipeItem(1),
				'C', curse.getRecipeItem(2),
				'D', curse.getRecipeItem(3)
			);
			
			GameRegistry.addShapelessRecipe(
				new ItemStack(ItemList.curse,1,curse.damage|0b100000000),
				new ItemStack(ItemList.curse,1,curse.damage),
				ItemList.stardust,
				ItemList.arcane_shard
			);
		}
		
		GameRegistry.addShapedRecipe(new ItemStack(ItemList.charm_pouch),
			"PLP", "LRL", "PLP",
			'P', ItemList.end_powder,
			'L', Items.leather,
			'R', new ItemStack(ItemList.rune,1,OreDictionary.WILDCARD_VALUE)
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(ItemList.obsidian_rod),
			" F", "F ",
			'F', ItemList.obsidian_fragment
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(Blocks.obsidian),
			"FFF", "FFF", "FFF",
			'F', ItemList.obsidian_fragment
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.obsidian_stairs),
			"  F", " FF", "FFF",
			'F', ItemList.obsidian_fragment
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.obsidian_special,1,0),
			"FFF", "FQF", "FFF",
			'F', ItemList.obsidian_fragment,
			'Q', new ItemStack(Blocks.quartz_block,1,0)
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.obsidian_special,1,1),
			"FFF", "FQF", "FFF",
			'F', ItemList.obsidian_fragment,
			'Q', new ItemStack(Blocks.quartz_block,1,1)
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.obsidian_special,1,2),
			"FFF", "FQF", "FFF",
			'F', ItemList.obsidian_fragment,
			'Q', new ItemStack(Blocks.quartz_block,1,2)
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(ItemList.energy_wand_core),
			"AAA", "AEA", "AAA",
			'A', ItemList.auricion,
			'E', BlockList.endium_block
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(ItemList.energy_wand),
			"  C", " R ", "R  ",
			'C', ItemList.energy_wand_core,
			'R', ItemList.obsidian_rod
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.ravaged_brick_slab,6),
			"XXX",
			'X', new ItemStack(BlockList.ravaged_brick,1,OreDictionary.WILDCARD_VALUE)
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.ravaged_brick_stairs,4),
			"  X", " XX", "XXX",
			'X', new ItemStack(BlockList.ravaged_brick,1,OreDictionary.WILDCARD_VALUE)
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.ravaged_brick_fence,6),
			"XXX", "XXX",
			'X', new ItemStack(BlockList.ravaged_brick,1,OreDictionary.WILDCARD_VALUE)
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.spooky_log),
			"XXX", "XXX", "XXX",
			'X', ItemList.dry_splinter
		);

		GameRegistry.addShapelessRecipe(new ItemStack(BlockList.spooky_leaves),
			ItemList.dry_splinter, Blocks.deadbush, Blocks.sand
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(ItemList.scorching_pickaxe),
			"FFF", "FPF", "FFF",
			'F', ItemList.infernium,
			'P', Items.golden_pickaxe
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(ItemList.blank_gem),
			"AAA", "AEA", "AAA",
			'A', ItemList.arcane_shard,
			'E', BlockList.endium_block
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(ItemList.spatial_dash_gem),
			"APA", "PGP", "APA",
			'A', ItemList.arcane_shard,
			'P', Items.ender_pearl,
			'G', ItemList.blank_gem
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(ItemList.transference_gem),
			"AEA", "EGE", "AEA",
			'A', ItemList.arcane_shard,
			'E', ItemList.ectoplasm,
			'G', ItemList.spatial_dash_gem
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.endium_block),
			"XXX", "XXX", "XXX",
			'X', ItemList.endium_ingot
		);
		
		GameRegistry.addShapelessRecipe(new ItemStack(ItemList.endium_ingot,9),
			BlockList.endium_block
		);
		
		for(int a = 0; a < 14; a++){
			GameRegistry.addShapelessRecipe(
				new ItemStack(Items.dye,2,13),
				new ItemStack(BlockList.death_flower,1,a)
			);
		}
		
		GameRegistry.addShapelessRecipe(
			new ItemStack(Items.dye,2,8),
			new ItemStack(BlockList.death_flower,1,15)
		);
		
		GameRegistry.addShapelessRecipe(
			new ItemStack(Items.dye,2,14),
			new ItemStack(BlockList.crossed_decoration,1,BlockCrossedDecoration.dataLilyFire)
		);
		
		GameRegistry.addShapelessRecipe(
			new ItemStack(Items.ender_eye,1),
			ItemList.enhanced_ender_pearl,Items.blaze_powder
		);
		
		FurnaceRecipes.smelting().func_151393_a(BlockList.endium_ore,new ItemStack(ItemList.endium_ingot),0.9F);*/
		
		Stopwatch.finish("RecipeList - addRecipes");
	}
	
	private RecipeList(){}
}
