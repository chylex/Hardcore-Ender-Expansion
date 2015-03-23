package chylex.hee.tileentity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.block.BlockList;
import chylex.hee.entity.mob.EntityMobSanctuaryOverseer;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.util.BlockLocation;

public class TileEntitySanctuaryBrain extends TileEntity{
	private BlockLocation point1, point2;
	private List<ConquerPointHandler> conquerPts = new ArrayList<>();
	
	private byte checkTimer = 5, runTimer = 0;
	private boolean isIdle;
	
	@Override
	public void updateEntity(){
		if (worldObj.isRemote || point1 == null || point2 == null)return;
		
		if (--checkTimer < 0){
			checkTimer = 20;
			isIdle = worldObj.getEntitiesWithinAABB(EntityPlayer.class,BlockLocation.getBoundingBox(point1,point2).expand(8D,8D,8D)).isEmpty();
		}
		
		if (!isIdle){
			if (--runTimer < 0){
				runTimer = 5;
				List<EntityPlayer> list = worldObj.getEntitiesWithinAABB(EntityPlayer.class,BlockLocation.getBoundingBox(point1,point2).expand(0.9D,0.9D,0.9D));
				
				for(EntityPlayer player:list){
					PotionEffect effJump = player.getActivePotionEffect(Potion.jump);
					player.addPotionEffect(new PotionEffect(Potion.jump.id,8,4,true));
					player.addPotionEffect(new PotionEffect(Potion.nightVision.id,215,0,true));
				}
			}
				
			for(ConquerPointHandler point:conquerPts)point.update();
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if (point1 != null)nbt.setIntArray("p1",new int[]{ point1.x, point1.y, point1.z });
		if (point2 != null)nbt.setIntArray("p2",new int[]{ point2.x, point2.y, point2.z });
		
		NBTTagList ptsTag = new NBTTagList();
		for(ConquerPointHandler point:conquerPts)ptsTag.appendTag(point.writeToNBT());
		nbt.setTag("conquer",ptsTag);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		readCustomData(nbt);
	}
	
	public void readCustomData(NBTTagCompound nbt){
		int[] p1 = nbt.getIntArray("p1"), p2 = nbt.getIntArray("p2");
		if (p1.length == 3)point1 = new BlockLocation(p1[0],p1[1],p1[2]);
		if (p2.length == 3)point2 = new BlockLocation(p2[0],p2[1],p2[2]);
		
		NBTTagList ptsTag = nbt.getTagList("conquer",NBT.TAG_COMPOUND);
		
		for(int a = 0; a < ptsTag.tagCount(); a++){
			ConquerPointHandler point = new ConquerPointHandler();
			point.readFromNBT(ptsTag.getCompoundTagAt(a));
			conquerPts.add(point);
		}
	}
	
	private final class ConquerPointHandler{ // TODO test everything
		BlockLocation point1, point2;
		byte enemiesLeft = Byte.MIN_VALUE;
		byte runTimer = -1, startTimer = 0;
		boolean isConquered;
		
		void update(){
			if (point1 == null || point2 == null || isConquered)return;
			
			worldObj.setBlock(point1.x,point2.y+8,point1.z,Blocks.bedrock); // TODO
			
			AxisAlignedBB full = BlockLocation.getBoundingBox(point1,point2);
			boolean hasPlayers = !worldObj.getEntitiesWithinAABB(EntityPlayer.class,full.expand(0.9D,0.9D,0.9D)).isEmpty();
			
			if (hasPlayers){ // TODO
				System.out.println("run "+runTimer+", start "+startTimer+", enemies "+enemiesLeft);
				System.out.println("tick "+point1.toString()+", "+point2.toString());
			}
			
			if (runTimer == -1){
				if (!hasPlayers)startTimer = 0;
				else if (++startTimer >= 12+worldObj.rand.nextInt(20)){
					runTimer = startTimer = 0;
					if (enemiesLeft == Byte.MIN_VALUE)enemiesLeft = (byte)(8+worldObj.difficultySetting.getDifficultyId()*2+worldObj.rand.nextInt(4));
					updateBarriers(true);
				}
			}
			else if (runTimer >= 0){
				if (!hasPlayers){
					runTimer = -1;
					startTimer = 0;
					updateBarriers(false);
					for(EntityMobSanctuaryOverseer foe:(List<EntityMobSanctuaryOverseer>)worldObj.getEntitiesWithinAABB(EntityMobSanctuaryOverseer.class,full.expand(0.9D,0.9D,0.9D)))foe.setDead();
				}
				else{
					List<EntityMobSanctuaryOverseer> foes = worldObj.getEntitiesWithinAABB(EntityMobSanctuaryOverseer.class,full.expand(0.9D,0.9D,0.9D));
					
					if (++runTimer > 8){
						runTimer = 0;
						
						if (foes.isEmpty()){
							Random rand = worldObj.rand;
							
							for(int enemies = Math.min(enemiesLeft,2+rand.nextInt(2)+Math.min(2,rand.nextInt(1+worldObj.difficultySetting.getDifficultyId()))), attempt = 0; enemies > 0 && attempt < 10; attempt++){
								EntityMobSanctuaryOverseer mob = new EntityMobSanctuaryOverseer(worldObj);
								mob.setPosition(MathUtil.floor(full.minX+rand.nextFloat()*(full.maxX-full.minX))+0.5D,full.minY+1+rand.nextFloat()*(full.maxY-full.minY-2),MathUtil.floor(full.minZ+rand.nextFloat()*(full.maxZ-full.minZ))+0.5D);
								
								if (DragonUtil.getClosestEntity(mob,(List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class,mob.boundingBox.expand(1.5D,1.5D,1.5D))) == null){
									worldObj.spawnEntityInWorld(mob);
									--enemies;
								}
							}
						}
					}
					
					for(EntityMobSanctuaryOverseer overseer:foes){
						if (overseer.deathTime == 1)--enemiesLeft;
					}
					
					if (enemiesLeft <= 0){
						isConquered = true;
						updateBarriers(false);
					}
				}
			}
		}
		
		private void updateBarriers(boolean block){
			for(int y = point1.y; y <= point2.y; y++){
				for(int side = 0; side < 2; side++){
					for(int x = Math.min(point1.x,point2.x), z = side == 0 ? Math.min(point1.z,point2.z)-1 : Math.max(point1.z,point2.z)+1; x <= Math.max(point1.x,point2.x); x++){
						if (block && worldObj.isAirBlock(x,y,z))worldObj.setBlock(x,y,z,BlockList.sanctuary_barrier);
						else if (!block && worldObj.getBlock(x,y,z) == BlockList.sanctuary_barrier)worldObj.setBlockToAir(x,y,z);
						else continue;
						
						worldObj.playAuxSFX(x,y,z,2001,Block.getIdFromBlock(BlockList.sanctuary_barrier));
					}
					
					for(int z = Math.min(point1.z,point2.z), x = side == 0 ? Math.min(point1.x,point2.x)-1 : Math.max(point1.x,point2.x)+1; z <= Math.max(point1.z,point2.z); z++){
						if (block && worldObj.isAirBlock(x,y,z))worldObj.setBlock(x,y,z,BlockList.sanctuary_barrier);
						else if (!block && worldObj.getBlock(x,y,z) == BlockList.sanctuary_barrier)worldObj.setBlockToAir(x,y,z);
						else continue;
						
						worldObj.playAuxSFX(x,y,z,2001,Block.getIdFromBlock(BlockList.sanctuary_barrier));
					}
				}
			}
		}
		
		NBTTagCompound writeToNBT(){
			NBTTagCompound tag = new NBTTagCompound();
			if (point1 != null)tag.setIntArray("p1",new int[]{ point1.x, point1.y, point1.z });
			if (point2 != null)tag.setIntArray("p2",new int[]{ point2.x, point2.y, point2.z });
			tag.setByte("run",runTimer);
			tag.setByte("enm",enemiesLeft);
			tag.setBoolean("done",isConquered);
			return tag;
		}
		
		void readFromNBT(NBTTagCompound tag){
			int[] p1 = tag.getIntArray("p1"), p2 = tag.getIntArray("p2");
			if (p1.length == 3)point1 = new BlockLocation(p1[0],p1[1],p1[2]);
			if (p2.length == 3)point2 = new BlockLocation(p2[0],p2[1],p2[2]);
			runTimer = tag.hasKey("run") ? tag.getByte("run") : runTimer;
			enemiesLeft = tag.hasKey("enm") ? tag.getByte("enm") : enemiesLeft;
			isConquered = tag.hasKey("done") ? tag.getBoolean("done") : isConquered;
		}
	}
}
