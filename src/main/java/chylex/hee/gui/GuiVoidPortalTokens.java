package chylex.hee.gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiVoidPortalTokens extends GuiContainer{
	public GuiVoidPortalTokens(EntityPlayer player){
		super(new ContainerVoidPortalTokens(player));
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY){}
}
