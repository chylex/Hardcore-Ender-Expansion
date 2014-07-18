package chylex.hee.mechanics.knowledge.data.renderer;
import net.minecraft.client.Minecraft;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public interface IRegistrationRenderer{
	IRegistrationRenderer setTooltip(String tooltip);
	String getTooltip();
	@SideOnly(Side.CLIENT)
	void render(Minecraft mc, int x, int y);
}
