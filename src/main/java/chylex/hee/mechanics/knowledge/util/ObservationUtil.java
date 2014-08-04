package chylex.hee.mechanics.knowledge.util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class ObservationUtil{
	public static EntityPlayer getRandomObserver(Entity target, double maxDistance){
		List<EntityPlayer> closePlayers = target.worldObj.getEntitiesWithinAABB(EntityPlayer.class,target.boundingBox.expand(maxDistance,maxDistance/2,maxDistance));
		Collections.shuffle(closePlayers);
		
		for(EntityPlayer player:closePlayers){
			if (MathUtil.distance(player.posX-target.posX,player.posZ-target.posZ) <= maxDistance && (DragonUtil.canEntitySeePoint(player,target.posX,target.posY,target.posZ,1D) || player == target)){
				return player;
			}
		}
		
		return null;
	}
	
	public static ArrayList<EntityPlayer> getAllObservers(Entity target, double maxDistance){
		ArrayList<EntityPlayer> observers = new ArrayList<>();
		
		List<EntityPlayer> closePlayers = target.worldObj.getEntitiesWithinAABB(EntityPlayer.class,target.boundingBox.expand(maxDistance,maxDistance/2,maxDistance));
		
		for(EntityPlayer player:closePlayers){
			if (MathUtil.distance(player.posX-target.posX,player.posZ-target.posZ) <= maxDistance && canPlayerSeePoint(player,target.posX,target.posY+target.getEyeHeight(),target.posZ) && (DragonUtil.canEntitySeePoint(player,target.posX,target.posY,target.posZ,1D) || player == target)){
				observers.add(player);
			}
		}
		
		return observers;
	}
	
	public static ArrayList<EntityPlayer> getAllObservers(World world, double x, double y, double z, double maxDistance){
		ArrayList<EntityPlayer> observers = new ArrayList<>();
		
		List<EntityPlayer> closePlayers = world.getEntitiesWithinAABB(EntityPlayer.class,AxisAlignedBB.getBoundingBox(x-maxDistance,y-maxDistance/2,z-maxDistance,x+maxDistance,y+maxDistance/2,z+maxDistance));
		
		for(EntityPlayer player:closePlayers){
			if (MathUtil.distance(player.posX-x,player.posZ-z) <= maxDistance && canPlayerSeePoint(player,x,y,z) && DragonUtil.canEntitySeePoint(player,x,y,z,1D)){
				observers.add(player);
			}
		}
		
		return observers;
	}
	
	private static boolean canPlayerSeePoint(EntityPlayer player, double x, double y, double z){
		MovingObjectPosition mop = player.worldObj.rayTraceBlocks(Vec3.createVectorHelper(player.posX,player.posY+player.getEyeHeight(),player.posZ),Vec3.createVectorHelper(x,y,z));
		return mop == null || (Math.abs(x-mop.blockX) < 1 && Math.abs(y-mop.blockY) < 1 && Math.abs(z-mop.blockZ) < 1);
	}
}
