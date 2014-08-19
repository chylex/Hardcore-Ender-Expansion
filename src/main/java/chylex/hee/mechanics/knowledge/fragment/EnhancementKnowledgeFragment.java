package chylex.hee.mechanics.knowledge.fragment;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.item.ItemList;
import chylex.hee.item.ItemSpecialEffects;
import chylex.hee.mechanics.enhancements.IEnhancementEnum;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class EnhancementKnowledgeFragment extends TextKnowledgeFragment{
	@SideOnly(Side.CLIENT)
	private static ResourceLocation texEnhancement;

	@SideOnly(Side.CLIENT)
	public static void initClient(){
		texEnhancement = new ResourceLocation("hardcoreenderexpansion:textures/gui/fragment_enhancement.png");
	}
	
	private static final Pattern formattingCodePattern = Pattern.compile("(?i)"+String.valueOf('\u00a7')+"[0-9A-FK-OR]");
	private static final ItemStack lockedItem = new ItemStack(ItemList.special_effects,1,ItemSpecialEffects.questionMark);
	
	private ItemStack shownIS;
	
	public EnhancementKnowledgeFragment(int id){
		super(id);
	}
	
	public EnhancementKnowledgeFragment setEnhancement(IEnhancementEnum enhancement){
		shownIS = enhancement.getItemSelector().getRepresentativeItem();
		setLocalizedText(stripColorCodes(enhancement.getName()));
		return this;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getHeight(Minecraft mc, boolean isUnlocked){
		return 18;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(int x, int y, Minecraft mc, boolean isUnlocked){
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(texEnhancement);
		
		Tessellator tessellator = Tessellator.instance;
		tessellator.startDrawingQuads();
		tessellator.addVertexWithUV(x,y+18,0,0,0.5625F);
		tessellator.addVertexWithUV(x+18,y+18,0,0.5625F,0.5625F);
		tessellator.addVertexWithUV(x+18,y,0,0.5625F,0);
		tessellator.addVertexWithUV(x,y,0,0,0);
		tessellator.draw();

		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		RenderHelper.enableGUIStandardItemLighting();
		GuiItemRenderHelper.renderItemIntoGUI(mc.getTextureManager(),isUnlocked ? shownIS : lockedItem,x+1,y+1);
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);

		boolean origFont = mc.fontRenderer.getUnicodeFlag();
		mc.fontRenderer.setUnicodeFlag(true);
		mc.fontRenderer.drawString(getString(true),x+20,y+4,255<<24|130<<16|255,false);
		mc.fontRenderer.setUnicodeFlag(origFont);
	}
	
	public static String stripColorCodes(String s){
		return formattingCodePattern.matcher(s).replaceAll("");
	}
}
