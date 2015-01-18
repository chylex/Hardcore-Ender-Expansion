package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.packets.AbstractClientPacket;
import chylex.hee.system.util.DragonUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class C01ParticleEndPortalCreation extends AbstractClientPacket{
	private int x,z;
	
	public C01ParticleEndPortalCreation(){}
	
	public C01ParticleEndPortalCreation(int x, int z){
		this.x = x;
		this.z = z;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeInt(x).writeInt(z);
	}

	@Override
	public void read(ByteBuf buffer){
		this.x = buffer.readInt();
		this.z = buffer.readInt();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		DragonUtil.portalEffectX = x;
		DragonUtil.portalEffectZ = z;
	}
}
