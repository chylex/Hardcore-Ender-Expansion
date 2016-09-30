package chylex.hee.tileentity;
import net.minecraft.nbt.NBTTagCompound;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityEndermanHead extends TileEntityAbstractSynchronized{
	private byte rotation;
	private byte meta;
	
	@Override
	public boolean canUpdate(){
		return false;
	}

	@Override
	public NBTTagCompound writeTileToNBT(NBTTagCompound nbt){
		nbt.setByte("rot", rotation);
		nbt.setByte("meta", meta);
		return nbt;
	}

	@Override
	public void readTileFromNBT(NBTTagCompound nbt){
		rotation = nbt.getByte("rot");
		meta = nbt.getByte("meta");
	}

	public void setRotation(int rotation){
		this.rotation = (byte)rotation;
		this.meta = 1;
		synchronize();
	}
	
	public void setMeta(int meta){
		this.meta = (byte)meta;
		synchronize();
	}

	@SideOnly(Side.CLIENT)
	public byte getRotationClient(){
		return rotation;
	}
	
	@SideOnly(Side.CLIENT)
	public byte getMetaClient(){
		return meta;
	}
}
