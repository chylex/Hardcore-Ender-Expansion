package chylex.hee.gui;
import java.util.Iterator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.resources.I18n;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraftforge.common.AchievementPage;
import org.lwjgl.input.Mouse;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.events.CompendiumEventsClient;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.util.KnowledgeUtils;
import chylex.hee.system.achievements.AchievementManager;
import chylex.hee.system.achievements.HeeAchievement;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAchievementsCustom extends GuiAchievements{
	private GuiButton nextPageButton;
	private AchievementPage achievements;
	private boolean isLoaded;
	private byte screenType; // 0 = achievements, 1 = challenges
	
	public GuiAchievementsCustom(GuiScreen parent, StatFileWriter statWriter){
		super(parent,new ShowAllReadStatFile(statWriter));
	}
	
	@Override
	public void initGui(){
		super.initGui();
		
		for(Iterator<GuiButton> iter = buttonList.iterator(); iter.hasNext();){
			GuiButton btn = iter.next();
			
			if (btn.id == 2){
				nextPageButton = btn;
				iter.remove();
				break;
			}
		}
		
		buttonList.add(new GuiButton(10,(width-field_146555_f)/2+24,height/2+74,125,20,I18n.format("achievement.viewChallenges")));
	}
	
	private void loadPage(){
		String str = screenType == 0 ? AchievementManager.achievementScreenName : AchievementManager.challengeScreenName;
		
		for(int a = 0; a < AchievementPage.getAchievementPages().size(); a++){
			actionPerformed(nextPageButton);
			
			if (nextPageButton.displayString.equals(str)){
				achievements = AchievementPage.getAchievementPage(str);
				break;
			}
		}
		
		if (screenType == 0){
			field_146565_w = field_146569_s = field_146567_u = AchievementList.openInventory.displayColumn*24-82;
			field_146573_x = field_146568_t = field_146566_v = AchievementList.openInventory.displayRow*24-82;
		}
		else if (screenType == 1){
			field_146565_w = field_146569_s = field_146567_u = AchievementList.openInventory.displayColumn*24-118;
			field_146573_x = field_146568_t = field_146566_v = AchievementList.openInventory.displayRow*24-94;
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button){
		if (button.id == 10 && isLoaded){
			if (++screenType == 2)screenType = 0;
			button.displayString = I18n.format(screenType == 0 ? "achievement.viewChallenges" : "achievement.viewAchievements");
			loadPage();
		}
		else super.actionPerformed(button);
	}
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int buttonId){
		if (buttonId == 0 && achievements != null && screenType == 0){
			if (mouseX < width/2-113 || mouseX > width/2+113 || mouseY < height/2-84 || mouseY > height/2+68){
				super.mouseClicked(mouseX,mouseY,buttonId);
				return;
			}
			
			int offsetX = MathUtil.clamp(MathUtil.floor(field_146567_u),AchievementList.minDisplayColumn*24-112,AchievementList.maxDisplayColumn*24-78); // OBFUSCATED viewportX
			int offsetY = MathUtil.clamp(MathUtil.floor(field_146566_v),AchievementList.minDisplayRow*24-112,AchievementList.maxDisplayRow*24-78); // OBFUSCATED viewportY
			
			int centerX = (width-field_146555_f)/2+16; // OBFUSCATED viewportWidth, 256
			int centerY = (height-field_146557_g)/2+17; // OBFUSCATED viewportHeight, 202

			float realMouseX = (mouseX-centerX)*field_146570_r; // OBFUSCATED viewportScale
			float realMouseY = (mouseY-centerY)*field_146570_r;
			
			for(Achievement achievement:achievements.getAchievements()){
				int x = achievement.displayColumn*24-offsetX;
				int y = achievement.displayRow*24-offsetY;

				if (x >= -24 && y >= -24 && x <= 224F*field_146570_r && y <= 155F*field_146570_r && realMouseX >= x && realMouseX <= x+22 && realMouseY >= y && realMouseY <= y+22){
					KnowledgeObject<? extends IKnowledgeObjectInstance<?>> obj = ((HeeAchievement)achievement).getKnowledgeObj();
					if (obj == null)obj = KnowledgeUtils.tryGetFromItemStack(achievement.theItemStack);
					if (obj != null)CompendiumEventsClient.openCompendium(obj);
				}
			}
		}
		
		super.mouseClicked(mouseX,mouseY,buttonId);
	}
	
	@Override
	public void func_146509_g(){
		super.func_146509_g();
		loadPage();
		isLoaded = true;
	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTickTime){
		if (isLoaded){
			int wheel = Mouse.getDWheel();
			float prevScale = field_146570_r;
			
			if (wheel < 0)field_146570_r += 0.5F;
			else if (wheel > 0)field_146570_r -= 0.5F;
			
			field_146570_r = MathUtil.clamp(field_146570_r,1F,2F);
	
			if (field_146570_r != prevScale){
				field_146567_u -= ((field_146570_r*field_146555_f)-(prevScale*field_146555_f))*0.5F;
				field_146566_v -= ((field_146570_r*field_146557_g)-(prevScale*field_146557_g))*0.5F;
				field_146565_w = field_146569_s = field_146567_u;
				field_146573_x = field_146568_t = field_146566_v;
			}
		}
		
		super.drawScreen(mouseX,mouseY,partialTickTime);
	}
	
	static final class ShowAllReadStatFile extends StatFileWriter{
		private final StatFileWriter wrapped;
		
		public ShowAllReadStatFile(StatFileWriter wrapped){
			this.wrapped = wrapped;
		}
		
		@Override
		public boolean canUnlockAchievement(Achievement achievement){
			return wrapped.canUnlockAchievement(achievement);
		}
		
		@Override
		public boolean hasAchievementUnlocked(Achievement achievement){
			return wrapped.hasAchievementUnlocked(achievement);
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public int func_150874_c(Achievement achievement){ // OBFUSCATED getUnlockDepth
			return hasAchievementUnlocked(achievement) ? 0 : 1;
		}
	}
}
