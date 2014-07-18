package chylex.hee.entity.weather;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.init.Blocks;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityWeatherLightningBoltSafe extends EntityLightningBolt{
	private int lightningState;
	private int boltLivingTime;
	
	public EntityWeatherLightningBoltSafe(World world, double x, double y, double z){
		super(world,x,y,z);
		lightningState = 2;
		boltVertex = rand.nextLong();
		boltLivingTime = rand.nextInt(3)+1;

		if (!world.isRemote && world.difficultySetting.getDifficultyId() >= 2 && world.doChunksNearChunkExist(MathHelper.floor_double(x),MathHelper.floor_double(y),MathHelper.floor_double(z),10)){
			for(int testX = -2; testX <= 2; ++testX){
				for(int testY = -2; testY <= 2; ++testY){
					for(int testZ = -2; testZ <= 2; ++testZ){
						int xx = MathHelper.floor_double(x)+testX,yy = MathHelper.floor_double(y)+testY,zz = MathHelper.floor_double(z)+testZ;
						if (world.getBlock(xx,yy,zz) == Blocks.fire)world.setBlockToAir(xx,yy,zz);
					}
				}
			}
		}
	}

	@Override
	public void onUpdate(){
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

		if (lightningState >= 0){
			if (worldObj.isRemote){
				worldObj.lastLightningBolt = 2;
			}
		}
	}
}
