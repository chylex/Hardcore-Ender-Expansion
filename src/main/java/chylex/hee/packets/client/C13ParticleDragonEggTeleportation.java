package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C13ParticleDragonEggTeleportation extends AbstractClientPacket{
	private int startX,startY,startZ;
	private int endX,endY,endZ;
	
	public C13ParticleDragonEggTeleportation(){}
	
	public C13ParticleDragonEggTeleportation(int startX, int startY, int startZ, int endX, int endY, int endZ){
		this.startX = startX;
		this.startY = startY;
		this.startZ = startZ;
		this.endX = endX;
		this.endY = endY;
		this.endZ = endZ;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeInt(startX).writeInt(startY).writeInt(startZ);
		buffer.writeInt(endX).writeInt(endY).writeInt(endZ);
	}

	@Override
	public void read(ByteBuf buffer){
		startX = buffer.readInt();
		startY = buffer.readInt();
		startZ = buffer.readInt();
		endX = buffer.readInt();
		endY = buffer.readInt();
		endZ = buffer.readInt();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		float x1 = startX+0.5F, y1 = startY+0.5F, z1 = startZ+0.5F,
			  x2 = endX+0.5F, y2 = endY+0.5F, z2 = endZ+0.5F,
			  len = (float)Math.sqrt(MathUtil.square(x2-x1)+MathUtil.square(y2-y1)+MathUtil.square(z2-z1));
				
		double dir = Math.atan2((z2-z1),(x2-x1)),cos = Math.cos(dir),sin = Math.sin(dir),
			   cos2 = Math.cos(Math.atan2((z2-z1),(y2-y1)));
		
		for(float i = 0f; i < len; i += 0.5f){
			for(int a = 0; a < 8; a++){
				player.worldObj.spawnParticle("portal",x1+(cos*i),y1+(cos2*i),z1+(sin*i),(rand.nextFloat()-0.5F)*0.2F,(rand.nextFloat()-0.5F)*0.2F,(rand.nextFloat()-0.5F)*0.2F);
			}
		}
	}
}
