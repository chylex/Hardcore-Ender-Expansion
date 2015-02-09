package chylex.hee.item;
import net.minecraft.item.Item;

public class ItemSpecialEffects extends Item implements IMultiModel{
	public static final byte glyphIndex = 10;
	public static final byte questionMark = 18;
	public static final byte achievementLorePageIcon = 19;
	public static final byte biomePointStart = 20;
	public static final byte biomePointEnd = 22;
	public static final byte achievementCompendiumIcon = 23;
	public static final byte totalIcons = 24;
	
	@Override
	public String[] getModels(){
		return new String[]{
			"^0", "^1", "^2", "^3", "^4", "^5", "^6", "^7", "^8", "^9",
			"^altar_glyph_a", "^altar_glyph_b", "^altar_glyph_c", "^altar_glyph_d", "^altar_glyph_e", "^altar_glyph_f", "^altar_glyph_g", "^altar_glyph_h",
			"^question_mark", "^achievement_lore_pages", "^biome_0", "^biome_1", "^biome_2", "^ender_compendium"
		};
	}
}
