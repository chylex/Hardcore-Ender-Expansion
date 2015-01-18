package chylex.hee.system.achievements;
import net.minecraft.event.HoverEvent;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class HeeChallenge extends HeeAchievement{
	private IChatComponent statName;
	private final String achievementId;

	public HeeChallenge(String statId, String achievementId, int x, int y, ItemStack is){
		super(statId,achievementId,x,y,is,null);
		this.achievementId = achievementId;
		setSpecial();
	}
	
	@Override
	public IChatComponent getStatName(){
		if (statName == null)this.statName = new ChatComponentText(new StringBuilder().append(StatCollector.translateToLocal("challenge.title")).append(" ").append(StatCollector.translateToLocal("challenge."+achievementId)).toString());
		
		IChatComponent component = statName.createCopy();
		component.getChatStyle().setColor(EnumChatFormatting.GRAY);
		component.getChatStyle().setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_ACHIEVEMENT,new ChatComponentText(this.statId)));
		component.getChatStyle().setColor(getSpecial() ? EnumChatFormatting.DARK_PURPLE : EnumChatFormatting.GREEN);
		return component;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getDescription(){
		return new StringBuilder().append(StatCollector.translateToLocal("challenge."+achievementId+".desc")).append("\n").append(EnumChatFormatting.RED).append(StatCollector.translateToLocal("challenge.difficulty."+AchievementManager.challengeStrings.get(achievementId))).toString();
	}
}
