package chylex.hee.mechanics.compendium_old.util;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.mechanics.compendium_old.content.KnowledgeObject;
import chylex.hee.mechanics.compendium_old.events.CompendiumEvents;
import chylex.hee.mechanics.compendium_old.objects.IKnowledgeObjectInstance;
import chylex.hee.mechanics.compendium_old.objects.ObjectBlock;
import chylex.hee.mechanics.compendium_old.objects.ObjectItem;
import chylex.hee.mechanics.compendium_old.objects.ObjectMob;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C03KnowledgeNotification;
import chylex.hee.packets.client.C19CompendiumData;

public final class KnowledgeObservation{
	private KnowledgeObject<? extends IKnowledgeObjectInstance<?>> object;
	private byte type;
	
	public void setEmpty(){
		this.object = null;
		this.type = 0;
	}
	
	public KnowledgeObservation setBlock(KnowledgeObject<ObjectBlock> obj){
		if (obj == null)return this;
		this.object = obj;
		this.type = 1;
		return this;
	}
	
	public KnowledgeObservation setItem(KnowledgeObject<ObjectItem> obj){
		if (obj == null)return this;
		this.object = obj;
		this.type = 2;
		return this;
	}
	
	public KnowledgeObservation setMob(KnowledgeObject<ObjectMob> obj){
		if (obj == null)return this;
		this.object = obj;
		this.type = 3;
		return this;
	}
	
	public KnowledgeObject<? extends IKnowledgeObjectInstance<?>> getObject(){
		return object;
	}
	
	public void discover(EntityPlayer player){
		boolean result = false;
		
		switch(type){
			case 1: result = CompendiumEvents.getPlayerData(player).tryDiscoverBlock((KnowledgeObject<ObjectBlock>)object,true); break;
			case 2: result = CompendiumEvents.getPlayerData(player).tryDiscoverItem((KnowledgeObject<ObjectItem>)object,true); break;
			case 3: result = CompendiumEvents.getPlayerData(player).tryDiscoverMob((KnowledgeObject<ObjectMob>)object,true); break;
			default: return;
		}
		
		if (result){
			PacketPipeline.sendToPlayer(player,new C03KnowledgeNotification(object));
			PacketPipeline.sendToPlayer(player,new C19CompendiumData(player));
		}
	}
}