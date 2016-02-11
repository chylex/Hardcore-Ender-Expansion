package chylex.hee.render.item;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import chylex.hee.system.abstractions.GL;
import chylex.hee.tileentity.TileEntityLootChest;

public class RenderItemLootChest implements IItemRenderer{
	private final TileEntityLootChest chestRenderer = new TileEntityLootChest();
	
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
		GL.rotate(90F,0F,1F,0F);
		GL.translate(-0.5F,-0.5F,-0.5F);
		
		if (type == ItemRenderType.EQUIPPED_FIRST_PERSON){
			GL.translate(-0.55F,0.7F,0.25F);
		}
		else if (type == ItemRenderType.EQUIPPED){
			GL.translate(-0.5F,0.7F,0.5F);
		}
		else if (type == ItemRenderType.INVENTORY){
			if (Minecraft.getMinecraft().currentScreen instanceof GuiAchievements)RenderHelper.enableGUIStandardItemLighting();
		}
		
		TileEntityRendererDispatcher.instance.renderTileEntityAt(chestRenderer,0D,0D,0D,0F);
		GL.enableRescaleNormal();
	}
}
