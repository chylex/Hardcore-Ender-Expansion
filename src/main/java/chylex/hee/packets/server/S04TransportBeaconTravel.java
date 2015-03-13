package chylex.hee.packets.server;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import chylex.hee.mechanics.misc.PlayerTransportBeacons;
import chylex.hee.packets.AbstractServerPacket;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityTransportBeacon;

public class S04TransportBeaconTravel extends AbstractServerPacket{
	private int tileX, tileY, tileZ, targetX, targetZ;
	
	public S04TransportBeaconTravel(){}
	
	public S04TransportBeaconTravel(int tileX, int tileY, int tileZ, int targetX, int targetZ){
		this.tileX = tileX;
		this.tileY = tileY;
		this.tileZ = tileZ;
		this.targetX = targetX;
		this.targetZ = targetZ;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeInt(tileX).writeShort(tileY).writeInt(tileZ).writeInt(targetX).writeInt(targetZ);
	}

	@Override
	public void read(ByteBuf buffer){
		tileX = buffer.readInt();
		tileY = buffer.readShort();
		tileZ = buffer.readInt();
		targetX = buffer.readInt();
		targetZ = buffer.readInt();
	}

	@Override
	protected void handle(EntityPlayerMP player){
		TileEntity tile = player.worldObj.getTileEntity(tileX,tileY,tileZ);
		
		if (tile instanceof TileEntityTransportBeacon && MathUtil.distance(tileX+0.5D-player.posX,tileY+0.5D-player.posY,tileZ+0.5D-player.posZ) <= 8D){
			PlayerTransportBeacons data = PlayerTransportBeacons.getInstance(player);
			if (data.checkBeacon(targetX,targetZ,tileX,tileZ))((TileEntityTransportBeacon)tile).teleportPlayer(player,targetX,targetZ,data);
		}
	}
}
