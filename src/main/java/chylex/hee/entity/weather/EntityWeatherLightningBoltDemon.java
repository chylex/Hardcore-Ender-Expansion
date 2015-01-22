package chylex.hee.entity.weather;
import net.minecraft.block.material.Material;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import chylex.hee.entity.boss.EntityBossEnderDemon;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.BlockPosM;

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
			BlockPosM pos = new BlockPosM(this), testPos = pos.copy();
			
			if (caster != null){
				for(int testX = pos.x-1; testX <= pos.x+1; testX++){
					for(int testZ = pos.z-1; testZ <= pos.z+1; testZ++){
						for(int testY = pos.y; testY > pos.y-1; testY--){
							if (testPos.getBlockMaterial(worldObj) == Material.water){
								caster.attackEntityFrom(DamageSource.drown,ModCommonProxy.opMobs ? 50F : 70F);
								return;
							}
						}
					}
				}
			}
			
			if (!shouldMakeFire && world.isAreaLoaded(testPos.moveTo(this),10)){
				for(int testX = -2; testX <= 2; ++testX){
					for(int testY = -2; testY <= 2; ++testY){
						for(int testZ = -2; testZ <= 2; ++testZ){
							if (testPos.moveTo(pos).moveBy(testX,testY,testZ).getBlock(worldObj) == Blocks.fire)testPos.setToAir(worldObj);
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
				worldObj.setLastLightningBolt(2);
			}
		}
		else super.onUpdate();
	}
}
