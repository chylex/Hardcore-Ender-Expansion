package chylex.hee.mechanics.compendium.content;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import chylex.hee.block.BlockList;
import chylex.hee.mechanics.knowledge.util.IGuiItemStackRenderer;

public class KnowledgeCategories implements IGuiItemStackRenderer{
	private static final byte iconSize = 44;
	
	public static KnowledgeCategories OVERWORLD, DRAGON_LAIR, ENDSTONE_BLOBS, DUNGEON_TOWER, METEOROIDS, BIOME_ISLAND_FOREST, BIOME_ISLAND_MOUNTAINS, BIOME_ISLAND_ENCHISLAND;
	
	public static final KnowledgeCategories[] categoryList = new KnowledgeCategories[]{
		OVERWORLD = new KnowledgeCategories(0, -iconSize*2, 0, "Overworld", new ItemStack(Blocks.stonebrick)),
		DRAGON_LAIR = new KnowledgeCategories(1, -iconSize, 0, "Dragon Lair", new ItemStack(Blocks.dragon_egg)),
		ENDSTONE_BLOBS = new KnowledgeCategories(2, 0, 0, "Endstone Blobs", new ItemStack(Blocks.end_stone)),
		DUNGEON_TOWER = new KnowledgeCategories(3, iconSize, 0, "Dungeon Tower", new ItemStack(BlockList.obsidian_special, 1, 1)),
		METEOROIDS = new KnowledgeCategories(4, iconSize*2, 0, "Meteoroids", new ItemStack(BlockList.sphalerite)),
		BIOME_ISLAND_FOREST = new KnowledgeCategories(5, iconSize*3, -iconSize, "Biome Island - Infested Forest", new ItemStack(BlockList.end_terrain, 1, 0)),
		BIOME_ISLAND_MOUNTAINS = new KnowledgeCategories(6, iconSize*3, 0, "Biome Island - Burning Mountains", new ItemStack(BlockList.end_terrain, 1, 1)),
		BIOME_ISLAND_ENCHISLAND = new KnowledgeCategories(7, iconSize*3, iconSize, "Biome Island - Enchanted Island", new ItemStack(BlockList.end_terrain, 1, 2))
	};
	
	public final byte id;
	private final int x, y;
	private final String tooltip;
	private final ItemStack showcaseItem;
	
	private KnowledgeCategories(int id, int x, int y, String tooltip, ItemStack showcaseItem){
		this.id = (byte)id;
		this.x = x-iconSize;
		this.y = y-(iconSize>>1);
		this.tooltip = tooltip;
		this.showcaseItem = showcaseItem;
	}

	@Override
	public int getX(){
		return x;
	}

	@Override
	public int getY(){
		return y;
	}

	@Override
	public ItemStack getItemStack(){
		return showcaseItem;
	}

	@Override
	public String getTooltip(){
		return tooltip;
	}
}
