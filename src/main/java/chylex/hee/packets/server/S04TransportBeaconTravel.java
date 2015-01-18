package chylex.hee.packets.server;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import chylex.hee.mechanics.misc.PlayerTransportBeacons;
import chylex.hee.packets.AbstractServerPacket;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityTransportBeacon;

public class S04TransportBeaconTravel extends AbstractServerPacket{
	private BlockPos tilePos;
	private int targetX, targetZ;
	
	public S04TransportBeaconTravel(){}
	
	public S04TransportBeaconTravel(BlockPos tilePos, int targetX, int targetZ){
		this.tilePos = tilePos;
		this.targetX = targetX;
		this.targetZ = targetZ;
	}
	
	@Override
	public void write(ByteBuf buffer){
		buffer.writeLong(tilePos.toLong()).writeInt(targetX).writeInt(targetZ);
	}

	@Override
	public void read(ByteBuf buffer){
		tilePos = BlockPos.fromLong(buffer.readLong());
		targetX = buffer.readInt();
		targetZ = buffer.readInt();
	}

	@Override
	protected void handle(EntityPlayerMP player){
		TileEntity tile = player.worldObj.getTileEntity(tilePos);
		
		if (tile instanceof TileEntityTransportBeacon && MathUtil.distance(tilePos.getX()+0.5D-player.posX,tilePos.getY()+0.5D-player.posY,tilePos.getZ()+0.5D-player.posZ) <= 8D){
			PlayerTransportBeacons data = PlayerTransportBeacons.getInstance(player);
			if (data.checkBeacon(targetX,targetZ,tilePos.getX(),tilePos.getZ()))((TileEntityTransportBeacon)tile).teleportPlayer(player,targetX,targetZ,data);
		}
	}
}
