package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C10ParticleTransferenceGemLink extends AbstractClientPacket{
	private boolean isLinking;
	private int x,y,z;
	
	public C10ParticleTransferenceGemLink(){}
	
	public C10ParticleTransferenceGemLink(boolean isLinking, int x, int y, int z){
		this.isLinking = isLinking;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeBoolean(isLinking).writeInt(x).writeInt(y).writeInt(z);
	}

	@Override
	public void read(ByteBuf buffer){
		isLinking = buffer.readBoolean();
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		for(int a = 0; a < 25; a++)HardcoreEnderExpansion.fx.portalOrbiting(player.worldObj,x+0.5D,y+0.38D+rand.nextDouble()*0.6D,z+0.5D,rand.nextDouble()*0.045D+0.015D);
		
		if (isLinking)player.worldObj.playSound(x+0.5D,y+1D,z+0.5D,"hardcoreenderexpansion:environment.gem.link",1F,rand.nextFloat()*0.02F+0.64F,false);
		else player.worldObj.playSound(x+0.5D,y+1D,z+0.5D,"mob.endermen.portal",1.2F,player.worldObj.rand.nextFloat()*0.05F+0.85F,false);
	}
}
