package chylex.hee.mechanics.knowledge.data.renderer;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public final class DummyRenderer implements IRegistrationRenderer{
	private String customTooltip;
	
	@Override
	public IRegistrationRenderer setTooltip(String tooltip){
		customTooltip = tooltip;
		return this;
	}
	
	@Override
	public String getTooltip(){
		return customTooltip == null?"DummyRenderer":customTooltip;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void render(Minecraft mc, int x, int y){}
}
