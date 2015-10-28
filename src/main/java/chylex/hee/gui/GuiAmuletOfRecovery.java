package chylex.hee.gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import chylex.hee.item.ItemAmuletOfRecovery;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAmuletOfRecovery extends GuiContainer{
	private static final ResourceLocation texture = new ResourceLocation("textures/gui/container/generic_54.png");
	
	private final EntityPlayer player;
	private final IInventory amuletInv;
	private final int rowCount;
	
	public GuiAmuletOfRecovery(EntityPlayer player){
		super(new ContainerAmuletOfRecovery(player));
		this.player = player;
		this.amuletInv = ItemAmuletOfRecovery.getAmuletInventory(player.getHeldItem());
		this.rowCount = amuletInv.getSizeInventory()/9;
		this.ySize = 114+rowCount*18;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRendererObj.drawString(I18n.format("container.amuletOfRecovery"),8,6,4210752);
		fontRendererObj.drawString(I18n.format(player.inventory.getInventoryName()),8,ySize-96+2,4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY){
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(texture);
		
		int x = (width-xSize)/2, y = (height-ySize)/2;
		drawTexturedModalRect(x,y,0,0,xSize,rowCount*18+17);
		drawTexturedModalRect(x,y+rowCount*18+17,0,126,xSize,96);
	}
}
