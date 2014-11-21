package chylex.hee.mechanics.compendium;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import chylex.hee.block.BlockList;
import chylex.hee.mechanics.compendium.content.KnowledgeCategory;

public class KnowledgeCategories{
	public static final KnowledgeCategory OVERWORLD, DRAGON_LAIR, ENDSTONE_BLOBS, DUNGEON_TOWER, METEOROIDS, BIOME_ISLANDS, BIOME_ISLAND_FOREST, BIOME_ISLAND_MOUNTAINS, BIOME_ISLAND_ENCHISLAND;
	
	public static final KnowledgeCategory[] categoryList = new KnowledgeCategory[]{
		OVERWORLD = new KnowledgeCategory(0, "Overworld", new ItemStack(Blocks.stonebrick)),
		DRAGON_LAIR = new KnowledgeCategory(1, "Dragon Lair", new ItemStack(Blocks.dragon_egg)),
		ENDSTONE_BLOBS = new KnowledgeCategory(2, "Endstone Blobs", new ItemStack(Blocks.end_stone)),
		DUNGEON_TOWER = new KnowledgeCategory(3, "Dungeon Tower", new ItemStack(BlockList.obsidian_special, 1, 1)),
		METEOROIDS = new KnowledgeCategory(4, "Meteoroids", new ItemStack(BlockList.sphalerite)),
		BIOME_ISLANDS = new KnowledgeCategory(5, "Biome Islands", new ItemStack(BlockList.special_effects, 1, 1)),
		BIOME_ISLAND_FOREST = new KnowledgeCategory(5, "Biome Island - Infested Forest", new ItemStack(BlockList.end_terrain, 1, 0)),
		BIOME_ISLAND_MOUNTAINS = new KnowledgeCategory(6, "Biome Island - Burning Mountains", new ItemStack(BlockList.end_terrain, 1, 1)),
		BIOME_ISLAND_ENCHISLAND = new KnowledgeCategory(7, "Biome Island - Enchanted Island", new ItemStack(BlockList.end_terrain, 1, 2))
	};
}
