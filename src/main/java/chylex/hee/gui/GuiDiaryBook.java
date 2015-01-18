package chylex.hee.gui;
import java.io.IOException;
import java.util.Iterator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiDiaryBook extends GuiScreenBook{
	private static final ResourceLocation resource = new ResourceLocation("hardcoreenderexpansion:textures/gui/diary_book.png");

	private int bookImageWidth = 192;
	private int bookImageHeight = 192;
	private NBTTagList bookPages;
	private int bookTotalPages = 1;
	public int currPage;
	private GuiButtonPageArrow buttonNextPage;
	private GuiButtonPageArrow buttonPreviousPage;
	
	public GuiDiaryBook(EntityPlayer player, ItemStack is){
		super(player,is,false);
		bookImageHeight = 217; // +23
		
		if (is.hasTagCompound()){
			NBTTagCompound nbttagcompound = is.getTagCompound();
			bookPages = nbttagcompound.getTagList("pages",8);

			if (bookPages != null){
				bookPages = (NBTTagList)bookPages.copy();
				bookTotalPages = bookPages.tagCount();

				if (bookTotalPages < 1)bookTotalPages = 1;
			}
		}
	}
	
	@Override
	public void initGui(){
		super.initGui();
		
		for(Iterator<?> iter = buttonList.iterator(); iter.hasNext();){
			GuiButton btn = (GuiButton)iter.next();
			if (btn.id == 0)btn.yPosition += 23;
			else if (btn.id == 1 || btn.id == 2)iter.remove();
		}
		
		int centerX = (width-bookImageWidth)/2;
		buttonList.add(buttonNextPage = new GuiButtonPageArrow(1,centerX+120,2+154+23,true));
		buttonList.add(buttonPreviousPage = new GuiButtonPageArrow(2,centerX+38,2+154+23,false));
	}
	
	public void updateButtons(){
		buttonNextPage.visible = currPage < bookTotalPages-1;
		buttonPreviousPage.visible = currPage > 0;
	}
	
	@Override
	protected void actionPerformed(GuiButton button) throws IOException{
		if (button.enabled){
			if (button.id == 1){
				if (currPage < bookTotalPages-1)++currPage;
			}
			else if (button.id == 2){
				if (currPage > 0)--currPage;
			}
			else{
				super.actionPerformed(button);
				return;
			}
			
			updateButtons();
		}
	}

	@Override
	public void drawScreen(int x, int y, float renderPartialTicks){
		GL11.glColor4f(1F,1F,1F,1F);
		mc.getTextureManager().bindTexture(resource);
		int xCenter = (width-bookImageWidth)/2;
		byte yStart = 2;
		drawTexturedModalRect(xCenter,yStart,0,0,bookImageWidth,bookImageHeight);

		String s = I18n.format("book.pageIndicator",new Object[]{Integer.valueOf(currPage+1),Integer.valueOf(bookTotalPages)});

		fontRendererObj.drawString(s,xCenter-fontRendererObj.getStringWidth(s)+bookImageWidth-44,yStart+16,0);
		fontRendererObj.drawSplitString((bookPages != null && currPage >= 0 && currPage < bookPages.tagCount())?bookPages.getStringTagAt(currPage):"",xCenter+36,yStart+32,116,0);

		for(int a = 0; a < buttonList.size(); ++a)((GuiButton)buttonList.get(a)).drawButton(mc,x,y);
	}
}
