package chylex.hee.gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.system.ConfigHandler;

@SideOnly(Side.CLIENT)
public class GuiModConfig extends GuiConfig{
	public GuiModConfig(GuiScreen parent){
		super(parent,ConfigHandler.getGuiConfigElements(),"HardcoreEnderExpansion",false,false,GuiConfig.getAbridgedConfigPath(ConfigHandler.getConfigString()));
	}
}
