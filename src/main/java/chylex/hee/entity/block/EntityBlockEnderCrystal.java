package chylex.hee.entity.block;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import chylex.hee.block.BlockList;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.DragonSavefile;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class EntityBlockEnderCrystal extends EntityEnderCrystal{
	public static final byte TNT = 0, BARS = 1, BLAST = 2;

	private byte crystalType = 0;
	private String crystalKey = "";

	public EntityBlockEnderCrystal(World world){
		super(world);
	}

	@Override
	public boolean attackEntityFrom(DamageSource source, float damage){
		if (isEntityInvulnerable())return false;
		else if (!isDead && !worldObj.isRemote){
			health = 0;
			setDead();

			worldObj.createExplosion((Entity)null,posX,posY,posZ,6F,true);
			if (worldObj.provider.dimensionId == 1)WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).destroyCrystal(crystalKey);

			/*
			 * TNT
			 */
			Entity tar = source.getEntity();
			
			if (crystalType == TNT){
				if (tar instanceof EntityPlayer){
					int limiter = 4+worldObj.difficultySetting.getDifficultyId(),topblock = DragonUtil.getTopBlock(worldObj,Blocks.end_stone,MathUtil.floor(posX),MathUtil.floor(posZ));
					
					for(EntityEnderman enderman:(List<EntityEnderman>)worldObj.getEntitiesWithinAABB(EntityEnderman.class,AxisAlignedBB.getBoundingBox(posX-10D,topblock-5D,posZ-10D,posX+10D,topblock+5D,posZ+10D))){
						if (enderman.getDistance(posX,topblock,posZ) < 20D){
							enderman.setTarget(tar);
							if (--limiter <= 0)break;
						}
					}
				}
				
				float tx, tz, v;
				EntityTNTPrimed tnt;
				for(int a = 0; a < 8; a++){
					v = 0.15F+(rand.nextFloat()/8F);
					tx = tz = 0F;
					
					switch(a){
						case 0:case 1:case 2: tx = v; break;
						case 5:case 6:case 7: tx = -v; break;
					}
					switch(a){
						case 0:case 3:case 5: tz = -v; break;
						case 2:case 4:case 7: tz = v; break;
					}
					
					tnt = new EntityTNTPrimed(worldObj,posX+0.5F,posY+1F,posZ+0.5F,null);
					tnt.addVelocity(tx,1F,tz);
					tnt.fuse = (int)(58+(posY-DragonUtil.getTopBlock(worldObj,Blocks.end_stone,MathUtil.floor(posX),MathUtil.floor(posZ)))/2);
					worldObj.spawnEntityInWorld(tnt);
					worldObj.playSoundAtEntity(tnt,"random.fuse",1F,1F);
				}
			}
			/*
			 * BLAST
			 */
			else if (crystalType == BLAST){
				int maxRad = 4,
					ix = MathUtil.floor(posX),
					iy = MathUtil.floor(posY),
					iz = MathUtil.floor(posZ),
					terY = DragonUtil.getTopBlock(worldObj,Blocks.end_stone,ix,iz);
				
				worldObj.setBlockToAir(ix,iy-1,iz);
				
				for(int xx = ix-maxRad; xx <= ix+maxRad; xx++){
					for(int zz = iz-maxRad; zz <= iz+maxRad; zz++){
						for(int yy = terY; yy <= iy; yy++){
							if (worldObj.getBlock(xx,yy,zz) == BlockList.obsidian_falling){
								worldObj.setBlockToAir(xx,yy,zz);
								double[] vec = DragonUtil.getNormalizedVector(xx-ix,zz-iz);
								EntityBlockFallingObsidian obsidian = new EntityBlockFallingObsidian(worldObj,xx+0.5D,yy+0.1D,zz+0.5D);
								obsidian.motionX = (vec[0]+(rand.nextFloat()*0.5F-0.25F))*2.25F*rand.nextFloat();
								obsidian.motionZ = (vec[1]+(rand.nextFloat()*0.5F-0.25F))*2.25F*rand.nextFloat();
								obsidian.motionY = -0.25F-rand.nextFloat()*0.4F;
								worldObj.spawnEntityInWorld(obsidian);
							}
						}
					}
				}
			}
		}
		
		return true;
	}

	public void setCrystalType(byte type){
		this.crystalType = type;
	}
	
	public void setCrystalKey(String key){
		this.crystalKey = key;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setByte("crystalType",crystalType);
		nbt.setString("crystalKey",crystalKey);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		crystalType = nbt.getByte("crystalType");
		crystalKey = nbt.getString("crystalKey");
	}
}
