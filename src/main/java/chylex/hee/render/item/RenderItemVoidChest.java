package chylex.hee.render.item;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.tileentity.TileEntityVoidChest;

public class RenderItemVoidChest implements IItemRenderer{
	private final TileEntityVoidChest chestRenderer = new TileEntityVoidChest();
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type){
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper){
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object...data){
		GL11.glRotatef(90F,0F,1F,0F);
		GL11.glTranslatef(-0.5F,-0.5F,-0.5F);
		
		if (type == ItemRenderType.EQUIPPED_FIRST_PERSON){
			GL11.glTranslatef(-0.55F,0.7F,0.25F);
		}
		else if (type == ItemRenderType.EQUIPPED){
			GL11.glTranslatef(-0.5F,0.7F,0.5F);
		}
		else if (type == ItemRenderType.INVENTORY){
			if (Minecraft.getMinecraft().currentScreen instanceof GuiAchievements)RenderHelper.enableGUIStandardItemLighting();
		}
		
		TileEntityRendererDispatcher.instance.renderTileEntityAt(chestRenderer,0D,0D,0D,0F);
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
	}
}
