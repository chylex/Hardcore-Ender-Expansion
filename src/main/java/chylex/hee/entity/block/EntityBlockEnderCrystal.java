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
import chylex.hee.game.savedata.WorldDataHandler;
import chylex.hee.game.savedata.types.DragonSavefile;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing6;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class EntityBlockEnderCrystal extends EntityEnderCrystal{
	public enum Type{
		TNT, BARS, BLAST
	}

	private Type crystalType = Type.TNT;
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

			worldObj.createExplosion(null,posX,posY,posZ,6F,true);
			if (worldObj.provider.dimensionId == 1)WorldDataHandler.<DragonSavefile>get(DragonSavefile.class).destroyCrystal(crystalKey);
			
			Entity tar = source.getEntity();
			
			if (crystalType == Type.TNT){
				if (tar instanceof EntityPlayer){
					int limiter = 4+worldObj.difficultySetting.getDifficultyId(), topblock = DragonUtil.getTopBlockY(worldObj,Blocks.end_stone,MathUtil.floor(posX),MathUtil.floor(posZ),MathUtil.floor(posY));
					
					for(EntityEnderman enderman:(List<EntityEnderman>)worldObj.getEntitiesWithinAABB(EntityEnderman.class,AxisAlignedBB.getBoundingBox(posX-10D,topblock-5D,posZ-10D,posX+10D,topblock+5D,posZ+10D))){
						if (enderman.getDistance(posX,topblock,posZ) < 20D){
							enderman.setTarget(tar);
							if (--limiter <= 0)break;
						}
					}
				}
				
				for(int a = 0; a < 8; a++){
					float v = 0.15F+(rand.nextFloat()/8F);
					float tx = 0F, tz = 0F;
					
					switch(a){
						case 0:case 1:case 2: tx = v; break;
						case 5:case 6:case 7: tx = -v; break;
					}
					switch(a){
						case 0:case 3:case 5: tz = -v; break;
						case 2:case 4:case 7: tz = v; break;
					}
					
					EntityTNTPrimed tnt = new EntityTNTPrimed(worldObj,posX+0.5F,posY+1F,posZ+0.5F,null);
					tnt.addVelocity(tx,1F,tz);
					tnt.fuse = (int)(58+(posY-DragonUtil.getTopBlockY(worldObj,Blocks.end_stone,MathUtil.floor(posX),MathUtil.floor(posZ),MathUtil.floor(posY)))/2);
					worldObj.spawnEntityInWorld(tnt);
					worldObj.playSoundAtEntity(tnt,"random.fuse",1F,1F);
				}
			}
			else if (crystalType == Type.BLAST){
				final Pos crystalPos = Pos.at(this).offset(Facing6.DOWN_NEGY);
				crystalPos.setAir(worldObj);
				
				int terY = 1+DragonUtil.getTopBlockY(worldObj,Blocks.end_stone,crystalPos.getX(),crystalPos.getZ(),crystalPos.getY()+1);
				
				Pos.forEachBlock(crystalPos.offset(-4,terY-crystalPos.getY(),-4),crystalPos.offset(4,0,4),pos -> {
					if (pos.getBlock(worldObj) == BlockList.obsidian_falling){
						pos.setAir(worldObj);
						double[] vec = DragonUtil.getNormalizedVector(crystalPos.getX()-pos.x,crystalPos.getZ()-pos.z);
						
						EntityBlockFallingObsidian obsidian = new EntityBlockFallingObsidian(worldObj,pos.x+0.5D,pos.y+0.1D,pos.z+0.5D);
						obsidian.motionX = (vec[0]+(rand.nextFloat()*0.5F-0.25F))*2.25F*rand.nextFloat();
						obsidian.motionZ = (vec[1]+(rand.nextFloat()*0.5F-0.25F))*2.25F*rand.nextFloat();
						obsidian.motionY = -0.25F-rand.nextFloat()*0.4F;
						worldObj.spawnEntityInWorld(obsidian);
					}
				});
			}
		}
		
		return true;
	}

	public void setCrystalType(Type type){
		this.crystalType = type;
	}
	
	public void setCrystalKey(String key){
		this.crystalKey = key;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setByte("crystalType",(byte)crystalType.ordinal());
		nbt.setString("crystalKey",crystalKey);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		crystalType = Type.values()[MathUtil.clamp(nbt.getByte("crystalType"),0,Type.values().length-1)];
		crystalKey = nbt.getString("crystalKey");
	}
}
