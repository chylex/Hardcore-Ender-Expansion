package chylex.hee.system.util;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import java.util.TreeSet;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import chylex.hee.entity.boss.EntityBossDragon;

public final class DragonUtil{
	public static int portalEffectX, portalEffectZ;
	
	public static <K,V extends Comparable<? super V>> SortedSet<Entry<K,V>> sortMapByValueAscending(Map<K,V> map){
		SortedSet<Entry<K,V>> sorted = new TreeSet<>(
			new Comparator<Entry<K,V>>(){
				@Override public int compare(Entry<K,V> e1, Entry<K,V> e2){
					int r = e1.getValue().compareTo(e2.getValue());
					return r == 0 ? 1 : r;
				}
			}
		);
		sorted.addAll(map.entrySet());
		return sorted;
	}

	public static <K,V extends Comparable<? super V>> SortedSet<Entry<K,V>> sortMapByValueDescending(Map<K,V> map){
		SortedSet<Entry<K,V>> sorted = new TreeSet<>(
			new Comparator<Entry<K,V>>(){
				@Override public int compare(Entry<K,V> e1, Entry<K,V> e2){
					int r = e2.getValue().compareTo(e1.getValue());
					return r == 0 ? 1 : r;
				}
			}
		);
		sorted.addAll(map.entrySet());
		return sorted;
	}
	
	private static final double[] rayX = new double[]{ -1D, -1D, 1D, 1D },
								  rayZ = new double[]{ -1D, 1D, -1D, 1D };
	
	public static boolean canEntitySeePoint(EntityLivingBase entity, double x, double y, double z, double pointScale){
		return canEntitySeePoint(entity,x,y,z,pointScale,false);
	}
	
	public static boolean canEntitySeePoint(EntityLivingBase entity, double x, double y, double z, double pointScale, boolean fromCenter){
		double px = entity.posX,
			   py = entity.posY,
			   pz = entity.posZ,
			   ry = -entity.rotationYaw,
			   rp = -entity.rotationPitch;
		int t1x,t1z,t2x,t2z,t3y,t4y;
		
		if (!fromCenter){
			px -= MathUtil.lendirx(1.5D,ry);
			pz -= MathUtil.lendiry(1.5D,ry);
		}
		t1x = (int)(px+MathUtil.lendirx(600,ry-66));
		t1z = (int)(pz+MathUtil.lendiry(600,ry-66));
		t2x = (int)(px+MathUtil.lendirx(600,ry+66));
		t2z = (int)(pz+MathUtil.lendiry(600,ry+66));
		
		boolean isXZInYaw = MathUtil.triangle((int)x,(int)z,(int)px,(int)pz,t1x,t1z,t2x,t2z);
		boolean isYInPitch;
		
		if ((Math.abs(rp) < 70 && Math.abs(y - py) <= 2) || Math.abs(y - py) > 2)isYInPitch = true;
		else{
			t3y = (int)(py+MathUtil.lendirx(28,rp-60));
			t4y = (int)(py+MathUtil.lendirx(28,rp+60));
			isYInPitch = (y >= t3y && y <= t4y);
		}
		
		boolean[] isBlockedArr = new boolean[8];

		for(int a = 0; a < 4; a++){
			for(int b = 0; b < 2; b++){
				MovingObjectPosition mop = entity.worldObj.rayTraceBlocks(Vec3.createVectorHelper(px,py+0.12F,pz),Vec3.createVectorHelper(x+rayX[a]*pointScale,y+(b == 0?-1D:1D)*pointScale,z+rayZ[a]*pointScale));
				
				if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK){
					isBlockedArr[a*2+b] = MathUtil.distance(mop.blockX+0.5D-x,mop.blockY+0.5D-y,mop.blockZ+0.5D-z) > 0.1D &&
										  MathUtil.distance(mop.blockX+0.5D-px,mop.blockY+0.62D-py,mop.blockZ-pz) > 2D;

					if (isBlockedArr[a*2+b]){
						if (!entity.worldObj.getBlock(mop.blockX,mop.blockY,mop.blockZ).isOpaqueCube())isBlockedArr[a*2+b] = false;
					}
				}
			}
		}
		
		boolean isBlocked = isBlockedArr[0] && isBlockedArr[1] && isBlockedArr[2] && isBlockedArr[3] && isBlockedArr[4] && isBlockedArr[5] && isBlockedArr[6] && isBlockedArr[7];

		if (isBlocked)return false;
		else return (isXZInYaw && isYInPitch);
	}
	
	public static float rotateSmoothly(float sourceAngle, float targetAngle, float amount){
		while(sourceAngle < 0F)sourceAngle += 360F;
		while(sourceAngle >= 360F)sourceAngle -= 360F;
		
		float angleDifference = 180F-Math.abs(Math.abs(sourceAngle-targetAngle)-180F);
		if (angleDifference <= amount)return targetAngle;
		
		float d = sourceAngle < 180F?sourceAngle+180F:sourceAngle-180F;
		sourceAngle += amount*((targetAngle > d && targetAngle < sourceAngle) || (sourceAngle < 180F && (targetAngle > d || targetAngle < sourceAngle))?-1:1);
		
		return sourceAngle;
	}
	
	public static double[] getNormalizedVector(double vecX, double vecZ){
		double len = Math.sqrt(vecX*vecX+vecZ*vecZ);
		return len == 0 ? new double[]{ 0, 0 } : new double[]{ vecX/len, vecZ/len };
	}

	public static int getTopBlock(World worldObj, Block block, int x, int z, int starty){
		int y = starty+1;
		while(y-- >= 0){
			if (worldObj.getBlock(x,y,z) == block)return y+1;
		}
		return -1;
	}
	
	public static int getTopBlock(World worldObj, Block block, int x, int z){
		return getTopBlock(worldObj,block,x,z,255);
	}
	
	public static void spawnXP(EntityBossDragon dragon, int amount){
		for(int a = amount; a > 0;){
			int split = EntityXPOrb.getXPSplit(a);
			a -= split;
			dragon.worldObj.spawnEntityInWorld(new EntityXPOrb(dragon.worldObj,dragon.posX,dragon.posY,dragon.posZ,a));
		}
	}
	
	public static void teleportToOverworld(EntityPlayerMP player){
		player.mcServer.getConfigurationManager().transferPlayerToDimension(player,0);
		player.playerNetServerHandler.playerEntity = player.mcServer.getConfigurationManager().respawnPlayer(player,0,true);
	}
	
	public static void createExplosion(Entity entity, double x, double y, double z, float strength, boolean fire){
		entity.worldObj.newExplosion(entity,x,y,z,strength,fire,entity.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing"));
	}
	
	public static void createExplosion(World world, double x, double y, double z, float strength, boolean fire){
		world.newExplosion(null,x,y,z,strength,fire,world.getGameRules().getGameRuleBooleanValue("mobGriefing"));
	}
	
	private DragonUtil(){}
}
