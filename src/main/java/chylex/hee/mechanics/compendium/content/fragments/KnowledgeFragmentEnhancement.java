package chylex.hee.mechanics.compendium.content.fragments;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import chylex.hee.gui.GuiEnderCompendium;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.enhancements.IEnhancementEnum;
import chylex.hee.system.util.DragonUtil;
import com.google.common.base.Joiner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class KnowledgeFragmentEnhancement extends KnowledgeFragment{
	private static final Set<KnowledgeFragmentEnhancement> enhancementFragments = new HashSet<>();
	
	public static final KnowledgeFragmentEnhancement getEnhancementFragment(IEnhancementEnum enhancement){
		for(KnowledgeFragmentEnhancement fragment:enhancementFragments){
			if (fragment.enhancement == enhancement)return fragment;
		}
		
		return null;
	}
	
	private IEnhancementEnum enhancement;
	private String name;
	
	public KnowledgeFragmentEnhancement(int globalID){
		super(globalID);
		enhancementFragments.add(this);
	}
	
	public KnowledgeFragmentEnhancement setEnhancement(IEnhancementEnum enhancement){
		this.enhancement = enhancement;
		this.name = DragonUtil.stripChatFormatting(enhancement.getName());
		return this;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getHeight(GuiEnderCompendium gui, boolean isUnlocked){
		return 20;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean onClick(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, int buttonId){
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void onRender(GuiEnderCompendium gui, int x, int y, int mouseX, int mouseY, boolean isUnlocked){
		GL11.glColor4f(1F,1F,1F,1F);
		
		ItemStack is = isUnlocked ? enhancement.getItemSelector().getRepresentativeItem() : KnowledgeFragmentCrafting.lockedItem;
		
		GuiItemRenderHelper.renderItemIntoGUI(gui.mc.getTextureManager(),is,x+1,y+1);
		
		RenderHelper.disableStandardItemLighting();
		KnowledgeFragmentText.renderString(name,x+22,y+5,130<<16|255,130<<16|255,gui);
		
		if (isUnlocked && mouseX >= x && mouseX <= x+17 && mouseY >= y && mouseY <= y+17){
			GuiItemRenderHelper.drawTooltip(gui,gui.mc.fontRenderer,mouseX,mouseY,Joiner.on('\n').join(is.getTooltip(gui.mc.thePlayer,false)));
		}
	}
}
