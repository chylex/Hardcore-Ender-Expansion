package chylex.hee.gui;
import java.util.Iterator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.StatFileWriter;
import net.minecraftforge.common.AchievementPage;
import chylex.hee.system.achievements.AchievementManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAchievementsCustom extends GuiAchievements{
	private GuiButton nextPageButton;
	private AchievementPage achievements;
	
	public GuiAchievementsCustom(GuiScreen parent, StatFileWriter statWriter){
		super(parent,statWriter);
	}
	
	@Override
	public void initGui(){
		super.initGui();
		
		for(Iterator<GuiButton> iter = buttonList.iterator(); iter.hasNext();){
			GuiButton btn = iter.next();
			
			if (btn.id == 1){
				btn.width = 140;
				btn.xPosition = (width>>1)-(btn.width>>1);
			}
			else if (btn.id == 2){
				nextPageButton = btn;
				iter.remove();
			}
		}
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int buttonId){
		if (buttonId == 0 && achievements != null){
			for(Achievement achievement:achievements.getAchievements()){
				// TODO detect click and return to Compendium
			}
		}
		
		super.mouseClicked(mouseX,mouseY,buttonId);
	}
	
	@Override
	public void func_146509_g(){
		super.func_146509_g();
		
		for(int a = 0; a < AchievementPage.getAchievementPages().size(); a++){
			actionPerformed(nextPageButton);
			
			if (nextPageButton.displayString.equals(AchievementManager.achievementScreenName)){
				achievements = AchievementPage.getAchievementPage(AchievementManager.achievementScreenName);
				break;
			}
		}
	}
}
