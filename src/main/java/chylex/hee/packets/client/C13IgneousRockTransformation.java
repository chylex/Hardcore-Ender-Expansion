package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C13IgneousRockTransformation extends AbstractClientPacket{
	private int x,y,z;
	
	public C13IgneousRockTransformation(){}
	
	public C13IgneousRockTransformation(int x, int y, int z){
		this.x = x;
		this.y = y;
		this.z = z;
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
		for(int a = 0; a < 40; a++){
			HardcoreEnderExpansion.fx.flame(player.worldObj,x-0.25D+rand.nextDouble()*1.5D,y-0.25D+rand.nextDouble()*1.5D,z-0.25D+rand.nextDouble()*1.5D,12+rand.nextInt(9));
		}
	}
}
