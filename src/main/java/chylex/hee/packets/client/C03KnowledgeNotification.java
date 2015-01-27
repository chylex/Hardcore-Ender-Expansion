package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.mechanics.compendium.events.CompendiumEventsClient;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.render.OverlayManager;
import chylex.hee.system.achievements.AchievementManager;

public class C03KnowledgeNotification extends AbstractClientPacket{
	private byte objectID;
	
	public C03KnowledgeNotification(){}
	
	public C03KnowledgeNotification(KnowledgeObject<?> object){
		this.objectID = (byte)(object.globalID-128);
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeByte(objectID);
	}

	@Override
	public void read(ByteBuf buffer){
		objectID = buffer.readByte();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(AbstractClientPlayer player){
		OverlayManager.addNotification("Unlocked new Knowledge Object: "+KnowledgeObject.getObjectById(objectID+128).getTooltip());
		CompendiumEventsClient.onObjectDiscovered(objectID+128);
		
		player.worldObj.playSound(player.posX,player.posY,player.posZ,"hardcoreenderexpansion:player.random.pageflip",0.25F,0.5F*((player.getRNG().nextFloat()-player.getRNG().nextFloat())*0.7F+1.6F),false);
		if (!((EntityPlayerSP)player).getStatFileWriter().hasAchievementUnlocked(AchievementManager.THE_MORE_YOU_KNOW))CompendiumEventsClient.showCompendiumAchievement();
	}
}
