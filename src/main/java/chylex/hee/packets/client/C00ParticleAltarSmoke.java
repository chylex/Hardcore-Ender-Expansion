package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.tileentity.TileEntityEssenceAltar;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C00ParticleAltarSmoke extends AbstractClientPacket{
	private int x,y,z;
	
	public C00ParticleAltarSmoke(){}
	
	public C00ParticleAltarSmoke(TileEntityEssenceAltar altar){
		this.x = altar.xCoord;
		this.y = altar.yCoord;
		this.z = altar.zCoord;
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
		for(int a = 0; a < 15; a++){
			player.worldObj.spawnParticle("largesmoke",x+rand.nextDouble(),y+1.1D+rand.nextDouble()*0.25D,z+rand.nextDouble(),0D,0.15D,0D);
		}
	}
}
