package chylex.hee.game.achievements;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.content.objects.IObjectHolder;

public class HeeAchievement extends Achievement{
	private KnowledgeObject<? extends IObjectHolder<?>> knowledgeObj;
	
	public HeeAchievement(String statId, String achievementId, int x, int y, ItemStack is, Achievement parentAchievement){
		super(statId,achievementId,x,y,is,parentAchievement);
	}
	
	@Override
	public HeeAchievement setSpecial(){
		super.setSpecial();
		return this;
	}
	
	public HeeAchievement setKnowledgeObj(KnowledgeObject<? extends IObjectHolder<?>> obj){
		this.knowledgeObj = obj;
		return this;
	}
	
	public KnowledgeObject<? extends IObjectHolder<?>> getKnowledgeObj(){
		return knowledgeObj;
	}
}
