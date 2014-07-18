package chylex.hee.packets.client;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.entity.EntityClientPlayerMP;
import chylex.hee.block.BlockList;
import chylex.hee.block.BlockSpookyLeaves;
import chylex.hee.block.BlockSpookyLog;
import chylex.hee.packets.AbstractClientPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class C14ParticleSpookyTreeDecay extends AbstractClientPacket{
	private boolean isLog;
	private int x,y,z;
	
	public C14ParticleSpookyTreeDecay(){}
	
	public C14ParticleSpookyTreeDecay(boolean isLog, int x, int y, int z){
		this.isLog = isLog;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeBoolean(isLog).writeInt(x).writeInt(y).writeInt(z);
	}

	@Override
	public void read(ByteBuf buffer){
		isLog = buffer.readBoolean();
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
	}

	@Override
	@SideOnly(Side.CLIENT)
	protected void handle(EntityClientPlayerMP player){
		if (isLog)((BlockSpookyLog)BlockList.spooky_log).addDestroyEffectsCustom(player.worldObj,x,y,z);
		else ((BlockSpookyLeaves)BlockList.spooky_leaves).addDestroyEffectsCustom(player.worldObj,x,y,z);
	}
}
