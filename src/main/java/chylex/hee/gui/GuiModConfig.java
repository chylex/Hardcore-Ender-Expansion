package chylex.hee.gui;
import net.minecraft.client.gui.GuiScreen;
import chylex.hee.game.ConfigHandler;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiModConfig extends GuiConfig{
	public GuiModConfig(GuiScreen parent){
		super(parent,ConfigHandler.getGuiConfigElements(),"HardcoreEnderExpansion",false,false,GuiConfig.getAbridgedConfigPath(ConfigHandler.getConfigString()));
	}
}
