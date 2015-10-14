package chylex.hee.gui;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiLootChest extends GuiContainer{
	private static final ResourceLocation texChest = new ResourceLocation("textures/gui/container/generic_54.png");
	
	private final InventoryPlayer invPlayer;
	private final InventoryLootChest invLootChest;
	private final byte inventoryHeight;
	
	public GuiLootChest(InventoryPlayer inventory, InventoryLootChest chest){
		super(new ContainerLootChest(inventory,chest));
		this.invPlayer = inventory;
		this.invLootChest = chest;
		inventoryHeight = (byte)(chest.getSizeInventory()*2);
		ySize = 114+inventoryHeight;
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY){
		fontRendererObj.drawString(invLootChest.hasCustomInventoryName() ? invLootChest.getInventoryName() : I18n.format(invPlayer.player.capabilities.isCreativeMode ? "container.lootChest.editing" : "container.lootChest"),8,6,4210752);
		fontRendererObj.drawString(invPlayer.hasCustomInventoryName() ? invPlayer.getInventoryName() : I18n.format(invPlayer.getInventoryName()),8,ySize-94,4210752);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTickTime, int mouseX, int mouseY){
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(texChest);
		
		int x = (width-xSize)>>1, y = (height-ySize)>>1;
		drawTexturedModalRect(x,y,0,0,xSize,inventoryHeight+17);
		drawTexturedModalRect(x,y+inventoryHeight+17,0,126,xSize,96);
	}
}
