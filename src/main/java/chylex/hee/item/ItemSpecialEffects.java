package chylex.hee.item;
import chylex.hee.system.util.MathUtil;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemSpecialEffects extends Item{
	public static final byte glyphIndex = 10;
	public static final byte questionMark = 18;
	public static final byte achievementLorePageIcon = 19;
	public static final byte biomePointStart = 20;
	public static final byte biomePointEnd = 22;
	public static final byte achievementCompendiumIcon = 23;
	public static final byte totalIcons = 24;

	@SideOnly(Side.CLIENT)
	private IIcon[] iconArray;
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage){
		return iconArray[MathUtil.clamp(damage,0,iconArray.length-1)];
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister){
		iconArray = new IIcon[totalIcons];
		
		for(int a = 0; a < 10; a++)iconArray[a] = iconRegister.registerIcon("hardcoreenderexpansion:"+a);
		char c = 'a';
		for(int a = 10; a < 18; a++)iconArray[a] = iconRegister.registerIcon("hardcoreenderexpansion:altar_glyph_"+(c++));
		iconArray[18] = iconRegister.registerIcon("hardcoreenderexpansion:question_mark");
		iconArray[19] = iconRegister.registerIcon("hardcoreenderexpansion:achievement_lore_pages");
		for(int a = 20; a <= biomePointEnd; a++)iconArray[a] = iconRegister.registerIcon("hardcoreenderexpansion:biome_"+(a-20));
		iconArray[23] = iconRegister.registerIcon("hardcoreenderexpansion:ender_compendium");
		itemIcon = iconArray[0];
	}
}
