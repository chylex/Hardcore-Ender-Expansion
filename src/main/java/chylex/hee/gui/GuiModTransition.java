package chylex.hee.gui;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Set;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import chylex.hee.game.ModTransition;
import chylex.hee.system.logging.Log;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiModTransition extends GuiScreen{
	private static String generateRenderString(int worlds){
		StringBuilder build = new StringBuilder(128);
		
		build.append("Hardcore Ender Expansion 2 needs to convert ").append(worlds).append(worlds == 1 ? " world" : " worlds").append(".\n\n");
		
		build.append("If you proceed, End Dimension and Ender Compendium data\n");
		build.append("will be ").append(EnumChatFormatting.BOLD).append("deleted").append(EnumChatFormatting.RESET).append(" from all saved worlds. If you logged out\n");
		build.append("in the End, you will be moved to your bed or spawn point.\n\n");
		
		build.append("It is highly suggested to make a ").append(EnumChatFormatting.BOLD).append("backup").append(EnumChatFormatting.RESET).append(" of\n");
		build.append("your saves folder before proceeding.\n\n");
		
		build.append("If you do not want to convert your worlds, please downgrade\n");
		build.append("to the last version of Hardcore Ender Expansion 1.");
		
		return build.toString();
	}
	
	private final GuiScreen mainMenu;
	private final GuiScreen worldMenu;
	private final Set<File> worldsToUpdate;
	private String[] renderString;
	private byte runUpdates;
	
	public GuiModTransition(GuiScreen mainMenu, GuiScreen worldMenu, Set<File> worldsToupdate){
		this.mainMenu = mainMenu;
		this.worldMenu = worldMenu;
		this.worldsToUpdate = worldsToupdate;
		
		renderString = generateRenderString(worldsToUpdate.size()).split("\\n");
	}
	
	@Override
	public void initGui(){
		buttonList.add(new GuiButton(1,width/2-154,height-38,150,20,I18n.format("gui.yes")));
		buttonList.add(new GuiButton(2,width/2+4,height-38,150,20,I18n.format("gui.no")));
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		switch(button.id){
			case 1:
				runUpdates = 1;
				renderString = new String[]{ "Please wait..." };
				buttonList.clear();
				break;
				
			case 2: mc.displayGuiScreen(mainMenu); break;
			
			case 3:
				try{
					URI uri = URI.create("https://github.com/chylex/Hardcore-Ender-Expansion/");
					if (java.awt.Desktop.isDesktopSupported())java.awt.Desktop.getDesktop().browse(uri);
				}catch(Throwable t){
					Log.throwable(t,"Could not open website.");
				}
				
				break;
		}
	}
	
	@Override
	public void updateScreen(){
		if (runUpdates == 2){
			int failed = 0;
			
			for(File file:worldsToUpdate){
				if (!file.exists())continue; // might have been deleted while in the GUI
				
				try{
					ModTransition.doConvertWorld(file);
				}catch(IOException e){
					Log.throwable(e,"Could not convert world - $0",file.getName());
					++failed;
				}
			}
			
			if (failed > 0){
				StringBuilder build = new StringBuilder();
				build.append("Could not convert ").append(failed).append(" out of ").append(worldsToUpdate.size()).append(worldsToUpdate.size() == 1 ? " world" : " worlds").append(".\n\n");
				
				build.append("Please, check the console or log for more details, and\n");
				build.append("report it to the Hardcore Ender Expansion issue tracker.");
				
				renderString = build.toString().split("\\n");
				buttonList.add(new GuiButton(3,width/2-154,height-38,150,20,I18n.format("gui.openHeeTracker")));
				buttonList.add(new GuiButton(2,width/2+4,height-38,150,20,I18n.format("gui.back")));
			}
			else mc.displayGuiScreen(worldMenu);
			
			runUpdates = 0;
		}
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime){
		drawDefaultBackground();
		
		int y = (height/2-(10*renderString.length)/2)-25;
		for(String line:renderString)drawCenteredString(fontRendererObj,line,width/2,y += 10,0xFFFFFF);
		
		if (runUpdates == 1)runUpdates = 2;
		
		super.drawScreen(mouseX,mouseY,partialTickTime);
	}
}
