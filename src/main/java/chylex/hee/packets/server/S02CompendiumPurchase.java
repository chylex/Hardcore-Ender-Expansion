package chylex.hee.packets.server;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.packets.AbstractServerPacket;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C09SimpleEvent;
import chylex.hee.packets.client.C09SimpleEvent.EventType;

public class S02CompendiumPurchase extends AbstractServerPacket{
	private int id;
	
	public S02CompendiumPurchase(){}
	
	public S02CompendiumPurchase(KnowledgeFragment fragment){
		this.id = fragment.globalID;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeShort(id);
	}

	@Override
	public void read(ByteBuf buffer){
		id = buffer.readShort();
	}

	@Override
	protected void handle(EntityPlayerMP player){
		KnowledgeFragment fragment = KnowledgeFragment.fromID(id);
		if (fragment != null && CompendiumEvents.getPlayerData(player).tryPurchaseFragment(player,fragment))return; // C19CompendiumData also restores the state
		
		if (!MinecraftServer.getServer().isDedicatedServer())PacketPipeline.sendToPlayer(player,new C09SimpleEvent(EventType.RESTORE_COMPENDIUM_PAUSE));
	}
}
