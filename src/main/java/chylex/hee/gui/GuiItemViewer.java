package chylex.hee.gui;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.init.BlockList;
import chylex.hee.init.ItemList;
import chylex.hee.system.abstractions.GL;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiItemViewer extends GuiScreen{
	private final List<ItemStack> toRender = new ArrayList<>();
	private final int prevGuiScale;
	private int yStart;
	
	public GuiItemViewer(){
		mc = Minecraft.getMinecraft();
		prevGuiScale = mc.gameSettings.guiScale;
		mc.gameSettings.guiScale = 1;
	}
	
	@Override
	public void initGui(){
		toRender.clear();

		for(Block block:BlockList.getAllBlocks()){
			Item item = Item.getItemFromBlock(block);
			if (item == null)continue;
			
			List<ItemStack> is = new ArrayList<>();
			item.getSubItems(item,null,is);
			toRender.addAll(is);
		}

		for(Item item:ItemList.getAllItems()){
			List<ItemStack> is = new ArrayList<>();
			item.getSubItems(item,null,is);
			toRender.addAll(is);
		}
	}
	
	@Override
	public void onGuiClosed(){
		Minecraft.getMinecraft().gameSettings.guiScale = prevGuiScale;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime){
		super.drawBackground(0);
		
		int wheel = Mouse.getDWheel();
		if (wheel < 0)yStart += 12;
		else if (wheel > 0)yStart = Math.max(0,yStart-12);
		
		GL.enableRescaleNormal();
		GL.scale(8F,8F,8F);
		
		for(int a = yStart, xx = 0, yy = 0; a < toRender.size(); a++){
			drawRect(2+xx*17,2+yy*17,1+(xx+1)*17,1+(yy+1)*17,(255<<24)|(180<<16)|(180<<8)|180);
			GuiItemRenderHelper.renderItemIntoGUI(mc.getTextureManager(),toRender.get(a),2+xx*17,2+yy*17);
			
			if (++xx == 12){
				++yy;
				xx = 0;
			}
		}
		
		GL.disableRescaleNormal();
	}
}
