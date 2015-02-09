package chylex.hee.gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiButtonState extends GuiButton{
	public boolean forcedHover;
	
	public GuiButtonState(int id, int x, int y, int width, int height, String text){
		super(id,x,y,width,height,text);
	}
	
	@Override
	public int getHoverState(boolean hover){
		return forcedHover ? 2 : super.getHoverState(hover);
	}
}
