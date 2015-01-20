package chylex.hee.tileentity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityEndermanHead extends TileEntityAbstractSynchronized{
	private byte rotation;
	private byte meta;

	@Override
	public NBTTagCompound writeTileToNBT(NBTTagCompound nbt){
		nbt.setByte("rot",rotation);
		nbt.setByte("meta",meta);
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
	public byte getRotation(){
		return rotation;
	}
	
	@SideOnly(Side.CLIENT)
	public byte getMeta(){
		return meta;
	}
}
