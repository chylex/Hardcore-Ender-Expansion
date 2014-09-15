package chylex.hee.gui;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import chylex.hee.block.BlockList;
import chylex.hee.gui.helpers.GuiItemRenderHelper;
import chylex.hee.item.ItemList;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiItemViewer extends GuiScreen{
	private final GuiItemRenderHelper itemRenderer = new GuiItemRenderHelper();
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
		
		// Blocks
		
		// TODO use the list stuff
		Field[] blockList = BlockList.class.getFields();
		
		for(int a = 0; a < blockList.length; a++){
			if (!Block.class.isAssignableFrom(blockList[a].getType()))continue;
			
			try{
				Block block = (Block)blockList[a].get(null);
				Item item = Item.getItemFromBlock(block);
				if (item == null)continue;
				
				List is = new ArrayList();
				item.getSubItems(item,null,is);
				for(Object o:is)toRender.add((ItemStack)o);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		
		// Items
		
		Field[] itemList = ItemList.class.getFields();
		
		for(int a = 0; a < itemList.length; a++){
			if (!Item.class.isAssignableFrom(itemList[a].getType()))continue;
			
			try{
				Item item = (Item)itemList[a].get(null);
				List is = new ArrayList();
				item.getSubItems(item,null,is);
				for(Object o:is)toRender.add((ItemStack)o);
			}catch(Exception e){
				e.printStackTrace();
			}
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
		
		GL11.glEnable(GL12.GL_RESCALE_NORMAL);
		GL11.glScalef(8F,8F,8F);
		
		for(int a = yStart, xx = 0, yy = 0; a < toRender.size(); a++){
			drawRect(2+xx*17,2+yy*17,1+(xx+1)*17,1+(yy+1)*17,(255<<24)|(180<<16)|(180<<8)|180);
			GuiItemRenderHelper.renderItemIntoGUI(mc.getTextureManager(),toRender.get(a),2+xx*17,2+yy*17);
			if (++xx == 12){
				++yy;
				xx = 0;
			}
		}
		
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
	}
}
