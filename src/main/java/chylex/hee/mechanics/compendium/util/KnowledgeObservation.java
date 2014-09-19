package chylex.hee.mechanics.compendium.util;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.mechanics.compendium.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium.objects.ObjectBlock;
import chylex.hee.mechanics.compendium.objects.ObjectItem;
import chylex.hee.mechanics.compendium.objects.ObjectMob;

public final class KnowledgeObservation{
	private KnowledgeObject<? extends IKnowledgeObjectInstance<?>> object;
	private byte type;
	
	public void setEmpty(){
		this.object = null;
		this.type = 0;
	}
	
	public void setBlock(KnowledgeObject<ObjectBlock> obj){
		this.object = obj;
		this.type = 1;
	}
	
	public void setItem(KnowledgeObject<ObjectItem> obj){
		this.object = obj;
		this.type = 2;
	}
	
	public void setMob(KnowledgeObject<ObjectMob> obj){
		this.object = obj;
		this.type = 3;
	}
	
	public KnowledgeObject<? extends IKnowledgeObjectInstance<?>> getObject(){
		return object;
	}
	
	public void discover(EntityPlayer player){
		switch(type){
			case 1: CompendiumEvents.getPlayerData(player).tryDiscoverBlock((KnowledgeObject<ObjectBlock>)object,true); break;
			case 2: CompendiumEvents.getPlayerData(player).tryDiscoverItem((KnowledgeObject<ObjectItem>)object,true); break;
			case 3: CompendiumEvents.getPlayerData(player).tryDiscoverMob((KnowledgeObject<ObjectMob>)object,true); break;
			default:
		}
	}
}