package chylex.hee.tileentity;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import chylex.hee.world.util.BlockLocation;

public class TileEntitySanctuaryBrain extends TileEntity{
	public BlockLocation point1, point2;
	
	private byte checkTimer = 5, runTimer = 0;
	private boolean isIdle;
	
	@Override
	public void updateEntity(){
		if (worldObj.isRemote || point1 == null || point2 == null)return;
		
		if (--checkTimer < 0){
			checkTimer = 20;
			isIdle = worldObj.getEntitiesWithinAABB(EntityPlayer.class,getBoundingBox().expand(8D,8D,8D)).isEmpty();
		}
		
		if (!isIdle){
			if (--runTimer < 0){
				runTimer = 5;
				List<EntityPlayer> list = worldObj.getEntitiesWithinAABB(EntityPlayer.class,getBoundingBox());
				
				for(EntityPlayer player:list){
					PotionEffect effJump = player.getActivePotionEffect(Potion.jump);
					player.addPotionEffect(new PotionEffect(Potion.jump.id,8,4,true));
					player.addPotionEffect(new PotionEffect(Potion.nightVision.id,10,0,true));
				}
			}
		}
	}
	
	private AxisAlignedBB getBoundingBox(){
		return AxisAlignedBB.getBoundingBox(Math.min(point1.x,point2.x),point1.y,Math.min(point1.z,point2.z),Math.max(point1.x,point2.x),point2.y,Math.max(point1.z,point2.z));
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if (point1 != null)nbt.setIntArray("point1",new int[]{ point1.x, point1.y, point1.z });
		if (point2 != null)nbt.setIntArray("point2",new int[]{ point2.x, point2.y, point2.z });
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		readCustomData(nbt);
	}
	
	public void readCustomData(NBTTagCompound nbt){
		int[] p1 = nbt.getIntArray("point1"), p2 = nbt.getIntArray("point2");
		if (p1.length == 3)point1 = new BlockLocation(p1[0],p1[1],p1[2]);
		if (p2.length == 3)point2 = new BlockLocation(p2[0],p2[1],p2[2]);
	}
}
