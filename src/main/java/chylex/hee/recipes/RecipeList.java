package chylex.hee.recipes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import chylex.hee.block.BlockCrossedDecoration;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.enhancements.types.SoulCharmEnhancements;
import cpw.mods.fml.common.registry.GameRegistry;

public final class RecipeList{
	public static void addRecipes(){
		GameRegistry.addShapedRecipe(new ItemStack(ItemList.altar_nexus),
			"DED",
			'D',Items.diamond, 'E',Items.ender_eye
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.essence_altar),
			"LLL", "BNB", "OOO",
			'B',Blocks.bookshelf, 'L',Items.leather,
			'O',Blocks.obsidian, 'N',ItemList.altar_nexus
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.soul_charm),
			"EEE", "EXE", "EEE",
			'E',ItemList.ectoplasm,
			'X',ItemList.enderman_head
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(ItemList.ender_compendium),
			"PPP", "PEP", "PPP",
			'P',Items.paper,
			'E',Items.ender_eye
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.decomposition_table),
			"PBP", "SRS", "III",
			'P', ItemList.end_powder,
			'B', Blocks.iron_bars,
			'S', Blocks.stone,
			'R', ItemList.igneous_rock,
			'I', Blocks.iron_block
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.energy_extraction_table),
			"PBP", "SES", "III",
			'P', ItemList.end_powder,
			'B', Blocks.iron_bars,
			'S', Blocks.stone,
			'E', ItemList.ectoplasm,
			'I', Blocks.iron_block
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(ItemList.biome_compass), // TODO review later
			"PSP", "SCS", "PSP",
			'P', ItemList.end_powder,
			'S', ItemList.stardust,
			'C', Items.compass
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(ItemList.spectral_wand),
			"  E", " S ", "E  ",
			'E', ItemList.ectoplasm,
			'S', Items.stick
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(ItemList.charm_pouch),
			"PLP", "LRL", "PLP",
			'P', ItemList.end_powder,
			'L', Items.leather,
			'R', new ItemStack(ItemList.rune,1,OreDictionary.WILDCARD_VALUE)
		);
		
		GameRegistry.addShapedRecipe(new ItemStack(BlockList.spooky_log),
			"XXX", "XXX", "XXX",
			'X', ItemList.dry_splinter
		);
		
		GameRegistry.addShapelessRecipe(new ItemStack(BlockList.spooky_leaves),
			ItemList.dry_splinter, Blocks.deadbush, Blocks.sand
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
		
		GameRegistry.addShapelessRecipe(
			new ItemStack(ItemList.corporeal_mirage_orb),
			ItemList.instability_orb,ItemList.ectoplasm
		);

		RecipeSorter.register("hee:soul_charm_enhancements",SoulCharmEnhancementRecipe.class,RecipeSorter.Category.SHAPED,"after:minecraft:shapeless");
		for(SoulCharmEnhancements enhancement:SoulCharmEnhancements.values())GameRegistry.addRecipe(new SoulCharmEnhancementRecipe(enhancement));
	}
	
	private RecipeList(){}
}
