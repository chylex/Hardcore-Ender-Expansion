package chylex.hee.mechanics.compendium.content.fragments;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityList;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.item.ItemSpawnEggs;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.objects.ObjectBlock.BlockMetaWrapper;
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
	private static final String linkColor = EnumChatFormatting.DARK_PURPLE.toString();
	
	private String parsed;
	private List<KnowledgeObject<?>> parsedObjects = new ArrayList<>();
	
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
		if (isUnlocked){
			KnowledgeObject<?> obj = getHoveredObject(gui.mc.fontRenderer,mouseX,mouseY,x,y);
			
			if (obj != null){
				gui.showObject(obj);
				gui.moveToCurrentObject(true);
				return true;
			}
		}
		
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onRender(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, boolean isUnlocked){
		String str = getString(isUnlocked);
		renderString(str,x+1,y,GuiEnderCompendium.guiPageWidth-10,gui);
		
		if (isUnlocked){
			KnowledgeObject<?> obj = getHoveredObject(gui.mc.fontRenderer,mouseX,mouseY,x,y);
			if (obj != null)GuiItemRenderHelper.setupTooltip(mouseX,mouseY,obj.getTooltip()+"\n"+EnumChatFormatting.DARK_PURPLE+I18n.format("compendium.viewObject"));
		}
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
	private String convertString(String str){
		if (parsed != null)return parsed;
		
		if (str.indexOf('$') == -1)return parsed = str;
		
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
			
			build.append(linkColor);
			
			if (str.charAt(index) == ':'){
				lastIndex = ++index;
				index = str.indexOf('$',lastIndex);
				
				if (index == -1){
					Log.warn("Invalid text formatting, display text not terminated: $0",str);
					break;
				}
				
				parsedObjects.add(getObject(type,tmpBuild.toString()).getRight());
				build.append(str.substring(lastIndex,index++));
			}
			else if (tmpBuild.length() > 0){
				Pair<String,KnowledgeObject<?>> pair = getObject(type,tmpBuild.toString());
				parsedObjects.add(pair.getRight());
				build.append(pair.getLeft());
			}
			else{
				Log.warn("Invalid text formatting, identifier empty: $0",str);
				break;
			}
			
			lastIndex = index;
			build.append(EnumChatFormatting.BLACK);
		}
		
		if (parsedObjects.contains(null))Log.warn("Invalid text formatting, unknown object found: $0 $1",str,parsedObjects);
		
		build.append(str.substring(lastIndex));
		return parsed = build.toString();
	}
	
	@SideOnly(Side.CLIENT)
	private KnowledgeObject<?> getHoveredObject(FontRenderer fontRenderer, int mouseX, int mouseY, int x, int y){
		if (!(mouseX >= x && mouseX <= x+GuiEnderCompendium.guiPageWidth-10 && mouseY >= y && parsed.contains(linkColor)))return null;

		boolean origFont = fontRenderer.getUnicodeFlag();
		fontRenderer.setUnicodeFlag(true);
		
		List<String> list = fontRenderer.listFormattedStringToWidth(parsed,GuiEnderCompendium.guiPageWidth-10);
		
		if (mouseY <= y+list.size()*fontRenderer.FONT_HEIGHT){
			boolean multiLine = false;
			int count = -1, index, prevIndex;
			String lineStr;
			
			for(int line = 0; line < list.size(); line++){
				lineStr = list.get(line);
				prevIndex = -1;
				
				while((index = lineStr.indexOf(linkColor,++prevIndex)) != -1){
					prevIndex = index;
					
					if (multiLine)multiLine = false;
					else ++count;
					
					int startX = x+fontRenderer.getStringWidth(lineStr.substring(0,prevIndex = index));
					
					if ((index = lineStr.indexOf(EnumChatFormatting.BLACK.toString(),index)) == -1){
						index = lineStr.length();
						multiLine = true;
					}

					if (mouseY >= y+line*fontRenderer.FONT_HEIGHT && mouseY <= y+(line+1)*fontRenderer.FONT_HEIGHT){
						if (mouseX >= startX && mouseX <= startX+fontRenderer.getStringWidth(lineStr.substring(prevIndex,index))){
							fontRenderer.setUnicodeFlag(origFont);
							return count < parsedObjects.size() ? parsedObjects.get(count) : null;
						}
					}
				}
			}
		}
		
		fontRenderer.setUnicodeFlag(origFont);
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	private static Pair<String,KnowledgeObject<?>> getObject(char type, String identifier){
		KnowledgeObject<?> obj = null;
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
				
				if (item != null){
					text = StatCollector.translateToLocal(item.getUnlocalizedName(new ItemStack(item,1,meta))+".name");
					obj = KnowledgeObject.getObject(type == 'b' ? (item instanceof ItemBlock ? new BlockMetaWrapper(((ItemBlock)item).field_150939_a,meta) : null) : item);
				}
				
				break;
				
			case 'e':
				if (isHEE)text = ItemSpawnEggs.getMobName((Class<?>)EntityList.stringToClassMapping.get(identifier = ("HardcoreEnderExpansion."+identifier)));
				else text = StatCollector.translateToLocal("entity."+identifier+".name");
				
				Class<?> cls = (Class<?>)EntityList.stringToClassMapping.get(identifier);
				if (cls != null)obj = KnowledgeObject.getObject(cls);
				break;
				
			case 'd':
				obj = KnowledgeObject.getObject(identifier);
				if (obj != null)text = obj.getTooltip();
				break;
		}
		
		if (text == null || obj == null){
			Log.warn("Invalid object type or identifier: $0:$1",type,identifier);
			return Pair.<String,KnowledgeObject<?>>of(text == null ? identifier : text,obj);
		}
		else return Pair.<String,KnowledgeObject<?>>of(text,obj);
	}
}
