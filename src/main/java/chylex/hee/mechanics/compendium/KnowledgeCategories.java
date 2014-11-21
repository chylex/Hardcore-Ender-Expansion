package chylex.hee.mechanics.compendium;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockSpecialEffects;
import chylex.hee.mechanics.compendium.content.KnowledgeCategory;

public class KnowledgeCategories{
	public static final KnowledgeCategory OVERWORLD, DRAGON_LAIR, ENDSTONE_BLOBS, DUNGEON_TOWER, METEOROIDS, BIOME_ISLANDS, BIOME_ISLAND_FOREST, BIOME_ISLAND_MOUNTAINS, BIOME_ISLAND_ENCHISLAND;
	
	public static final KnowledgeCategory[] categoryList = new KnowledgeCategory[]{
		OVERWORLD = new KnowledgeCategory("Overworld", new ItemStack(Blocks.stonebrick)),
		DRAGON_LAIR = new KnowledgeCategory("Dragon Lair", new ItemStack(Blocks.dragon_egg)),
		ENDSTONE_BLOBS = new KnowledgeCategory("Endstone Blob", new ItemStack(Blocks.end_stone)),
		DUNGEON_TOWER = new KnowledgeCategory("Dungeon Tower", new ItemStack(BlockList.obsidian_special, 1, 1)),
		METEOROIDS = new KnowledgeCategory("Meteoroid", new ItemStack(BlockList.sphalerite)),
		BIOME_ISLANDS = new KnowledgeCategory("Biome Islands", new ItemStack(BlockList.special_effects, 1, BlockSpecialEffects.metaBiomeIslandIcon)),
		BIOME_ISLAND_FOREST = new KnowledgeCategory("Biome Island - Infested Forest", new ItemStack(BlockList.end_terrain, 1, 0)),
		BIOME_ISLAND_MOUNTAINS = new KnowledgeCategory("Biome Island - Burning Mountains", new ItemStack(BlockList.end_terrain, 1, 1)),
		BIOME_ISLAND_ENCHISLAND = new KnowledgeCategory("Biome Island - Enchanted Island", new ItemStack(BlockList.end_terrain, 1, 2))
	};
}
