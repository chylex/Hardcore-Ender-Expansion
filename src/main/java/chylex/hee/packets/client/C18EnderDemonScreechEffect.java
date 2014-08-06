package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C18EnderDemonScreechEffect extends AbstractClientPacket{
	public C18EnderDemonScreechEffect(){}
	
	@Override
	public void write(ByteBuf buffer){}

	@Override
	public void read(ByteBuf buffer){}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		player.worldObj.playSound(player.posX,player.posY+16D,player.posZ,"hardcoreenderexpansion:enderdemon.scream",1.8F,1F,false);
	}
}
