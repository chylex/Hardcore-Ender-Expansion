package chylex.hee.tileentity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import chylex.hee.world.util.BlockLocation;

public class TileEntitySanctuaryBrain extends TileEntity{
	private BlockLocation point1, point2;
	
	private byte checkTimer = 5;
	private boolean isIdle;
	
	@Override
	public void updateEntity(){
		if (worldObj.isRemote || point1 == null || point2 == null)return;
		
		if (--checkTimer < 0){
			checkTimer = 40;
			
			isIdle = worldObj.getEntitiesWithinAABB(EntityPlayer.class,AxisAlignedBB.getBoundingBox(point1.x,point1.y,point1.z,point2.x,point2.y,point2.z)).isEmpty();
			System.out.println("update: idle "+isIdle);
		}
		
		if (!isIdle){
			
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setIntArray("point1",new int[]{ point1.x, point1.y, point1.z });
		nbt.setIntArray("point2",new int[]{ point2.x, point2.y, point2.z });
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		int[] p1 = nbt.getIntArray("point1"), p2 = nbt.getIntArray("point1");
		if (p1.length == 3)point1 = new BlockLocation(p1[0],p1[1],p1[2]);
		if (p2.length == 3)point2 = new BlockLocation(p2[0],p2[1],p2[2]);
	}
}
