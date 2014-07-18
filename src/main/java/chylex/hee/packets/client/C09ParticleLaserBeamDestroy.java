package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.tileentity.TileEntityLaserBeam;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C09ParticleLaserBeamDestroy extends AbstractClientPacket{
	private int x,y,z;
	
	public C09ParticleLaserBeamDestroy(){}
	
	public C09ParticleLaserBeamDestroy(TileEntityLaserBeam beam){
		this.x = beam.xCoord;
		this.y = beam.yCoord;
		this.z = beam.zCoord;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeInt(x).writeInt(y).writeInt(z);
	}

	@Override
	public void read(ByteBuf buffer){
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		String[] particles = new String[]{ "largesmoke", "portal", "flame" };
		for(int a = 0; a < 10; a++){
			for(String pt:particles)player.worldObj.spawnParticle(pt,x+rand.nextDouble(),y+rand.nextDouble(),z+rand.nextDouble(),0D,0D,0D);
		}
	}
}
