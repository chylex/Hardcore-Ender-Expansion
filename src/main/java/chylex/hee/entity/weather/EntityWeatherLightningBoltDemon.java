package chylex.hee.entity.weather;
import net.minecraft.block.material.Material;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import chylex.hee.entity.boss.EntityBossEnderDemon;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.MathUtil;

public class EntityWeatherLightningBoltDemon extends EntityLightningBolt{
	private int lightningState;
	private int boltLivingTime;
	private boolean isSafe;
	
	public EntityWeatherLightningBoltDemon(World world, double x, double y, double z){
		super(world,x,y,z);
		lightningState = 2;
		boltLivingTime = rand.nextInt(3)+1;
		isSafe = true;
	}
	
	public EntityWeatherLightningBoltDemon(World world, double x, double y, double z, EntityBossEnderDemon caster, boolean shouldMakeFire){
		this(world,x,y,z);
		
		isSafe = shouldMakeFire^true;

		if (!world.isRemote){
			int ix = MathUtil.floor(x),iy = MathUtil.floor(y),iz = MathUtil.floor(z);
			
			if (caster != null){
				for(int testX = ix-1; testX <= ix+1; testX++){
					for(int testZ = iz-1; testZ <= iz+1; testZ++){
						for(int testY = iy; testY > iy-1; testY--){
							if (world.getBlock(testX,testY,testZ).getMaterial() == Material.water){
								caster.attackEntityFrom(DamageSource.drown,ModCommonProxy.opMobs ? 50F : 70F);
								return;
							}
						}
					}
				}
			}
			
			if (!shouldMakeFire && world.doChunksNearChunkExist(MathHelper.floor_double(x),MathHelper.floor_double(y),MathHelper.floor_double(z),10)){
				for(int testX = -2; testX <= 2; ++testX){
					for(int testY = -2; testY <= 2; ++testY){
						for(int testZ = -2; testZ <= 2; ++testZ){
							int xx = ix+testX,yy = iy+testY,zz = iz+testZ;
							if (world.getBlock(xx,yy,zz) == Blocks.fire)world.setBlockToAir(xx,yy,zz);
						}
					}
				}
			}
		}
	}
	
	@Override
	public void onUpdate(){
		if (isSafe){
			onEntityUpdate();
			
			if (lightningState == 2){
				worldObj.playSoundEffect(posX,posY,posZ,"ambient.weather.thunder",10000F,0.8F+rand.nextFloat()*0.2F);
				worldObj.playSoundEffect(posX,posY,posZ,"random.explode",2F,0.5F+rand.nextFloat()*0.2F);
			}
	
			--lightningState;
	
			if (lightningState < 0){
				if (boltLivingTime == 0)setDead();
				else if (lightningState<-rand.nextInt(10)){
					--boltLivingTime;
					lightningState = 1;
					boltVertex = rand.nextLong();
				}
			}
	
			if (lightningState >= 0 && worldObj.isRemote){
				worldObj.lastLightningBolt = 2;
			}
		}
		else super.onUpdate();
	}
}
