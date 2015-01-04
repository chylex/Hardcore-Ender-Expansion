package chylex.hee.tileentity.spawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.tileentity.TileEntityCustomSpawner;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class CustomSpawnerLogic extends MobSpawnerBaseLogic{
	protected TileEntityCustomSpawner spawnerTile;
	protected Entity entityCache;
	
	protected int minSpawnDelay = 200;
	protected int maxSpawnDelay = 800;
	protected byte spawnRange;
	protected byte attemptCount = 4;
	protected byte spawnCount = 4;
	protected byte maxNearbyEntities = 6;
	protected byte activatingRangeFromPlayer = 16;
	
	public double renderRotation;
	public double renderRotationPrev;

	protected CustomSpawnerLogic(TileEntityCustomSpawner spawnerTile){
		this.spawnerTile = spawnerTile;
	}
	
	/*
	 * METHODS FOR CLASSES THAT EXTEND THIS
	 */

	public void onBlockBreak(){}
	
	@Override
	public boolean isActivated(){
		return getSpawnerWorld().getClosestPlayer(getSpawnerX()+0.5D,getSpawnerY()+0.5D,getSpawnerZ()+0.5D,activatingRangeFromPlayer) != null;
	}
	
	protected void resetTimer(){
		if (maxSpawnDelay <= minSpawnDelay)spawnDelay = minSpawnDelay;
		else spawnDelay = minSpawnDelay+getSpawnerWorld().rand.nextInt(maxSpawnDelay-minSpawnDelay);
		func_98267_a(1);
	}
	
	protected AxisAlignedBB getSpawnerCheckBB(){
		return AxisAlignedBB.getBoundingBox(getSpawnerX(),getSpawnerY(),getSpawnerZ(),getSpawnerX()+1,getSpawnerY()+1,getSpawnerZ()+1).expand(spawnRange*2D,4D,spawnRange*2D);
	}
	
	protected boolean checkSpawnerConditions(){
		return true;
	}
	
	protected boolean canMobSpawn(EntityLiving entity){
		return true;
	}
	
	protected void onMobSpawned(EntityLiving entity){}
	
	protected abstract EntityLiving createMob(World world);
	
	/*
	 * THESE ARE NOT OVERRIDABLE
	 */

	@Override
	public final void func_98267_a(int i){
		spawnerTile.getWorldObj().addBlockEvent(spawnerTile.xCoord,spawnerTile.yCoord,spawnerTile.zCoord,Blocks.mob_spawner,i,0);
	}

	@Override
	public final void updateSpawner(){
		if (!isActivated())return;

		World world = getSpawnerWorld();
		
		if (world.isRemote){
			double particleX = getSpawnerX()+world.rand.nextFloat();
			double particleY = getSpawnerY()+world.rand.nextFloat();
			double particleZ = getSpawnerZ()+world.rand.nextFloat();
			world.spawnParticle("smoke",particleX,particleY,particleZ,0D,0D,0D);
			world.spawnParticle("flame",particleX,particleY,particleZ,0D,0D,0D);

			if (spawnDelay > 0)--spawnDelay;

			renderRotationPrev = renderRotation;
			renderRotation = (renderRotation+(1000F/(spawnDelay+200F)))%360D;
		}
		else{
			if (spawnDelay == -1)resetTimer();

			if (spawnDelay > 0){
				--spawnDelay;
				return;
			}
			
			int spawned = 0;

			for(int attempt = 0; attempt < attemptCount && spawned < spawnCount; ++attempt){
				EntityLiving entity = createMob(getSpawnerWorld());
				if (entity == null)return;

				int nearby = getSpawnerWorld().getEntitiesWithinAABB(entity.getClass(),getSpawnerCheckBB()).size();

				if (nearby >= maxNearbyEntities || !checkSpawnerConditions()){
					resetTimer();
					return;
				}

				double posX = getSpawnerX()+(world.rand.nextDouble()-world.rand.nextDouble())*spawnRange;
				double posY = getSpawnerY()+getSpawnerWorld().rand.nextInt(3)-1;
				double posZ = getSpawnerZ()+(world.rand.nextDouble()-world.rand.nextDouble())*spawnRange;
				entity.setLocationAndAngles(posX,posY,posZ,getSpawnerWorld().rand.nextFloat()*360F,0F);

				if (canMobSpawn(entity)){
					func_98265_a(entity); // OBFUSCATED spawn entity
					getSpawnerWorld().playAuxSFX(2004,getSpawnerX(),getSpawnerY(),getSpawnerZ(),0);
					entity.spawnExplosionParticle();
					
					onMobSpawned(entity);
					++spawned;
				}
			}

			if (spawned != 0)resetTimer();
		}
	}

	@Override
	public final World getSpawnerWorld(){
		return spawnerTile.getWorldObj();
	}

	@Override
	public final int getSpawnerX(){
		return spawnerTile.xCoord;
	}

	@Override
	public final int getSpawnerY(){
		return spawnerTile.yCoord;
	}

	@Override
	public final int getSpawnerZ(){
		return spawnerTile.zCoord;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final Entity func_98281_h(){
		if (entityCache == null)entityCache = func_98265_a(createMob(null)); // OBFUSCATED spawn entity
		return entityCache;
	}
	
	public static final class BrokenSpawnerLogic extends CustomSpawnerLogic{
		public BrokenSpawnerLogic(TileEntityCustomSpawner spawnerTile){
			super(spawnerTile);
		}

		@Override
		protected EntityLiving createMob(World world){
			return new EntityEnderman(world);
		}
	}
}
