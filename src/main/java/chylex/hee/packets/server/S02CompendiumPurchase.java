package chylex.hee.packets.server;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.mechanics.compendium.player.PlayerCompendiumData;
import chylex.hee.packets.AbstractServerPacket;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C19CompendiumData;

public class S02CompendiumPurchase extends AbstractServerPacket{
	private boolean isFragment;
	private int id;
	
	public S02CompendiumPurchase(){}
	
	public S02CompendiumPurchase(KnowledgeObject<?> object){
		this.isFragment = false;
		this.id = object.globalID;
	}
	
	public S02CompendiumPurchase(KnowledgeFragment fragment){
		this.isFragment = true;
		this.id = fragment.globalID;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeBoolean(isFragment).writeInt(id);
	}

	@Override
	public void read(ByteBuf buffer){
		isFragment = buffer.readBoolean();
		id = buffer.readInt();
	}

	@Override
	protected void handle(EntityPlayerMP player){
		PlayerCompendiumData compendiumData = CompendiumEvents.getPlayerData(player);
		int points = 0;
		
		if (isFragment){
			KnowledgeFragment fragment = KnowledgeFragment.getById(id);
			if (fragment != null && compendiumData.getPoints() >= fragment.getPrice() && compendiumData.tryUnlockFragment(fragment))points = fragment.getPrice();
		}
		else{
			KnowledgeObject<?> object = KnowledgeObject.getObjectById(id);
			if (object != null && compendiumData.getPoints() >= object.getUnlockPrice() && compendiumData.tryDiscoverObject(object,false))points = object.getUnlockPrice();
		}
		
		if (points > 0)compendiumData.payPoints(points);
		
		PacketPipeline.sendToPlayer(player,new C19CompendiumData(compendiumData));
	}
}
