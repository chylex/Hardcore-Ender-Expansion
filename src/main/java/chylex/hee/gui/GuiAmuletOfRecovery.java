package chylex.hee.gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.gui.helpers.IContainerEventHandler;
import chylex.hee.item.ItemAmuletOfRecovery;
import chylex.hee.system.abstractions.GL;
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
	public void initGui(){
		super.initGui();
		buttonList.add(new GuiButtonArrow(1,width/2-6,height/2+6));
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		IContainerEventHandler.sendEvent(0);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRendererObj.drawString(I18n.format("container.amuletOfRecovery"),8,6,(64<<16)|(64<<8)|64);
		fontRendererObj.drawString(I18n.format(player.inventory.getInventoryName()),8,ySize-96+2,(64<<16)|(64<<8)|64);
		
		GuiButtonArrow arrow = (GuiButtonArrow)buttonList.get(0);
		
		if (arrow.func_146115_a() && mouseX >= arrow.xPosition && mouseY >= arrow.yPosition && mouseX < arrow.xPosition+arrow.width && mouseY < arrow.yPosition+arrow.height){
			GuiItemRenderHelper.setupTooltip(mouseX-guiLeft,mouseY-guiTop,I18n.format("gui.moveAndEquip"));
			GuiItemRenderHelper.drawTooltip(this,fontRendererObj);
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY){
		GL.color(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(texture);
		
		int x = (width-xSize)/2, y = (height-ySize)/2;
		drawTexturedModalRect(x,y,0,0,xSize,rowCount*18+17);
		drawTexturedModalRect(x,y+rowCount*18+17,0,126,xSize,96);
	}
}
