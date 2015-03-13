package chylex.hee.system.achievements;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Achievement;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;

public class HeeAchievement extends Achievement{
	private KnowledgeObject<? extends IKnowledgeObjectInstance<?>> knowledgeObj;
	
	public HeeAchievement(String statId, String achievementId, int x, int y, ItemStack is, Achievement parentAchievement){
		super(statId,achievementId,x,y,is,parentAchievement);
	}
	
	@Override
	public HeeAchievement setSpecial(){
		super.setSpecial();
		return this;
	}
	
	public HeeAchievement setKnowledgeObj(KnowledgeObject<? extends IKnowledgeObjectInstance<?>> obj){
		this.knowledgeObj = obj;
		return this;
	}
	
	public KnowledgeObject<? extends IKnowledgeObjectInstance<?>> getKnowledgeObj(){
		return knowledgeObj;
	}
}
