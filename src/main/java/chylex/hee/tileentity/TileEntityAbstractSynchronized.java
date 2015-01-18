package chylex.hee.tileentity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public abstract class TileEntityAbstractSynchronized extends TileEntity{
	public abstract NBTTagCompound writeTileToNBT(NBTTagCompound nbt);
	public abstract void readTileFromNBT(NBTTagCompound nbt);
	
	public final void synchronize(){
		if (!worldObj.isRemote)worldObj.markBlockForUpdate(pos);
	}
	
	@Override
	public Packet getDescriptionPacket(){
		return new S35PacketUpdateTileEntity(pos,0,writeTileToNBT(new NBTTagCompound()));
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet){
		readTileFromNBT(packet.getNbtCompound()); // OBFUSCATED get tag data
		worldObj.markBlockRangeForRenderUpdate(pos,pos);
	}
	
	@Override
	public final void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		writeTileToNBT(nbt);
	}
	
	@Override
	public final void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		readTileFromNBT(nbt);
	}
}
