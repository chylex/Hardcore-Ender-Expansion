package chylex.hee.mechanics.compendium.content.fragments;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.item.ItemSpawnEggs;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.misc.Baconizer;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.DragonUtil;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KnowledgeFragmentText extends KnowledgeFragment{
	/* === LANGUAGE FILE LINKS ===
	 * $<type>:<name>[:<display>][$]
	 * Type: [a-z]
	 * Name: [a-zA-Z0-9_/~]
	 * Display: [^$]
	 * 
	 * $b:Block --> block registry name
	 * $b:Block/0 --> block registry name with metadata
	 * $i:Item --> item registry name
	 * $i:Item/0 --> item registry name with damage
	 * $e:Entity --> entity list name
	 * $d:Dummy --> dummy identifier
	 * 
	 * ~ before block/item/entity names is a substitute for HEE
	 * trailing $ is only required if there is a <display>
	 */
	
	public static byte smoothRenderingMode = 0;
	
	public KnowledgeFragmentText(int globalID){
		super(globalID);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getHeight(GuiEnderCompendium gui, boolean isUnlocked){
		boolean origFont = gui.mc.fontRenderer.getUnicodeFlag();
		gui.mc.fontRenderer.setUnicodeFlag(true);
		int h = gui.mc.fontRenderer.listFormattedStringToWidth(getString(true),GuiEnderCompendium.guiPageWidth-10).size()*gui.mc.fontRenderer.FONT_HEIGHT;
		gui.mc.fontRenderer.setUnicodeFlag(origFont);
		return h;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean onClick(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, int buttonId, boolean isUnlocked){
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onRender(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, boolean isUnlocked){
		renderString(getString(isUnlocked),x+1,y,GuiEnderCompendium.guiPageWidth-10,gui);
	}
	
	@SideOnly(Side.CLIENT)
	protected String getString(boolean isUnlocked){
		String content = I18n.format("ec.reg."+globalID);
		return isUnlocked ? Baconizer.sentence(convertString(content)) : StringUtils.repeat('?',content.length());
	}
	
	public static void renderString(String str, int x, int y, GuiEnderCompendium gui){
		renderString(str,x,y,9999,gui);
	}
	
	public static void renderString(String str, int x, int y, int maxWidth, GuiEnderCompendium gui){
		renderString(str,x,y,maxWidth,255<<24,240<<24,gui);
	}
	
	public static void renderString(String str, int x, int y, int normalColor, int smoothColor, GuiEnderCompendium gui){
		renderString(str,x,y,9999,normalColor,smoothColor,gui);
	}
	
	public static void renderString(String str, int x, int y, int maxWidth, int normalColor, int smoothColor, GuiEnderCompendium gui){
		boolean origFont = gui.mc.fontRenderer.getUnicodeFlag();
		gui.mc.fontRenderer.setUnicodeFlag(true);
		
		if (smoothRenderingMode > 0){
			gui.mc.fontRenderer.drawSplitString(str,x,y,maxWidth,smoothColor);
			
			GL11.glTranslatef(-0.2F,0F,0F);
			gui.mc.fontRenderer.drawSplitString(str,x,y,maxWidth,smoothColor);
			GL11.glTranslatef(0.2F,0F,0F);
			
			GL11.glTranslatef(0F,smoothRenderingMode == 1 ? -0.2F : 0.2F,0F);
			gui.mc.fontRenderer.drawSplitString(str,x,y,maxWidth,smoothColor);
			GL11.glTranslatef(0F,smoothRenderingMode == 1 ? 0.2F : -0.2F,0F);
		}
		else gui.mc.fontRenderer.drawSplitString(str,x,y,maxWidth,normalColor);
		
		gui.mc.fontRenderer.setUnicodeFlag(origFont);
	}
	
	@SideOnly(Side.CLIENT)
	private static String convertString(String str){
		if (str.indexOf('$') == -1)return str;
		
		int index = 0, lastIndex = 0;
		char type = 0, tmp;
		StringBuilder build = new StringBuilder(str.length()), tmpBuild = new StringBuilder(16);
		
		while(true){
			if ((index = str.indexOf('$',lastIndex)) == -1)break;
			build.append(str.substring(lastIndex,index));
			
			if (index >= str.length()-2){
				Log.warn("Invalid text formatting, incorrect link start location: $0",str);
				break;
			}
			
			type = str.charAt(++index);
			
			if (str.charAt(++index) != ':'){
				Log.warn("Invalid text formatting, expected a colon: $0",str);
				break;
			}
			
			tmpBuild.setLength(0);
			
			while(index < str.length()-1){
				tmp = Character.toLowerCase(str.charAt(++index));
				
				if ((tmp >= 'a' && tmp <= 'z') || (tmp >= '0' && tmp <= '9') || tmp == '_' || tmp == '/' || tmp == '~')tmpBuild.append(str.charAt(index));
				else break;
			}
			
			build.append(EnumChatFormatting.DARK_PURPLE);
			
			if (str.charAt(index) == ':'){
				lastIndex = ++index;
				index = str.indexOf('$',lastIndex);
				
				if (index == -1){
					Log.warn("Invalid text formatting, display text not terminated: $0",str);
					break;
				}
				
				build.append(str.substring(lastIndex,index++)); // TODO getObjectName to test
			}
			else if (tmpBuild.length() > 0){
				build.append(getObjectName(type,tmpBuild.toString()));
			}
			else{
				Log.warn("Invalid text formatting, identifier empty: $0",str);
				break;
			}
			
			lastIndex = index;
			build.append(EnumChatFormatting.BLACK);
		}
		
		build.append(str.substring(lastIndex));
		return build.toString();
	}
	
	@SideOnly(Side.CLIENT)
	private static String getObjectName(char type, String identifier){
		String text = null;
		
		boolean isHEE = identifier.charAt(0) == '~' && identifier.length() > 1;
		if (isHEE)identifier = identifier.substring(1);
		
		switch(type){
			case 'b':
			case 'i':
				int metaIndex = identifier.indexOf('/'), meta = 0;
				
				if (metaIndex != -1 && metaIndex+1 < identifier.length()){
					meta = DragonUtil.tryParse(identifier.substring(metaIndex+1),0);
					identifier = identifier.substring(0,metaIndex);
				}
				
				Item item = GameRegistry.findItem(isHEE ? "HardcoreEnderExpansion" : "minecraft",identifier);
				if (item != null)text = StatCollector.translateToLocal(item.getUnlocalizedName(new ItemStack(item,1,meta))+".name");
				break;
				
			case 'e':
				if (isHEE)text = ItemSpawnEggs.getMobName((Class<?>)EntityList.stringToClassMapping.get("HardcoreEnderExpansion."+identifier));
				else text = StatCollector.translateToLocal("entity."+identifier+".name");
				break;
				
			case 'd':
				KnowledgeObject obj = KnowledgeObject.getObject(identifier);
				if (obj != null)text = obj.getTooltip();
				break;
		}
		
		if (text == null){
			Log.warn("Invalid object type or identifier: $0:$1",type,identifier);
			return identifier;
		}
		else return text;
	}
}
