package chylex.hee.gui.helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.stats.Achievement;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiAchievementOverlay{
	private final FakeAchievement achievement = new FakeAchievement();
	
	private ChatComponentText title;
	private String description;
	private long displayEnd;
	
	public void display(String title, String description, long millis){
		this.title = new ChatComponentText(I18n.format(title));
		this.description = I18n.format(description);
		this.displayEnd = Minecraft.getSystemTime()+millis;
		Minecraft.getMinecraft().guiAchievement.func_146255_b(achievement); // OBFUSCATED showAchievement
	}
	
	public void hide(){
		Minecraft.getMinecraft().guiAchievement.func_146257_b(); // OBFUSCATED hideAchievement
		displayEnd = 0L;
	}
	
	public void update(){
		if (displayEnd != 0L && Minecraft.getSystemTime() >= displayEnd)hide();
	}
	
	private final class FakeAchievement extends Achievement{
		FakeAchievement(){
			super("HEE2 Fake Achievement","",0,0,new ItemStack(Blocks.air),null);
			IScoreObjectiveCriteria.field_96643_a.remove(func_150952_k().func_96636_a()); // OBFUSCATED dummyObjectivesMap getCriteria getStringID
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public IChatComponent func_150951_e(){ // OBFUSCATED get name
			return title;
		}
		
		@Override
		@SideOnly(Side.CLIENT)
		public String getDescription(){
			return description;
		}
	}
}
