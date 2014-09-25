package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.mechanics.compendium.content.KnowledgeObject;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.render.OverlayManager;
import chylex.hee.system.achievements.AchievementManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

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
		objectID = (byte)(buffer.readByte()+128);
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		OverlayManager.addNotification("Unlocked new Knowledge Object: "+KnowledgeObject.getObjectById(objectID).getTooltip());
		player.worldObj.playSound(player.posX,player.posY,player.posZ,"hardcoreenderexpansion:player.random.pageflip",0.25F,0.5F*((player.getRNG().nextFloat()-player.getRNG().nextFloat())*0.7F+1.6F),false);
		if (!player.getStatFileWriter().hasAchievementUnlocked(AchievementManager.THE_MORE_YOU_KNOW))Minecraft.getMinecraft().guiAchievement.func_146255_b(AchievementManager.THE_MORE_YOU_KNOW);
	}
}
