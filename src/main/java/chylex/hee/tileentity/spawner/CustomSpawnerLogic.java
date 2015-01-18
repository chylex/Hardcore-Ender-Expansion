package chylex.hee.tileentity.spawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.tileentity.TileEntityCustomSpawner;

public abstract class CustomSpawnerLogic extends MobSpawnerBaseLogic{
	protected TileEntityCustomSpawner spawnerTile;
	protected Entity entityCache;
	
	protected int spawnDelay = 20;
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
	
	public boolean isActivated(){
		return getSpawnerWorld().getClosestPlayer(spawnerTile.getPos().getX()+0.5D,spawnerTile.getPos().getY()+0.5D,spawnerTile.getPos().getZ()+0.5D,activatingRangeFromPlayer) != null;
	}
	
	protected void resetTimer(){
		if (maxSpawnDelay <= minSpawnDelay)spawnDelay = minSpawnDelay;
		else spawnDelay = minSpawnDelay+getSpawnerWorld().rand.nextInt(maxSpawnDelay-minSpawnDelay);
		func_98267_a(1);
	}
	
	protected AxisAlignedBB getSpawnerCheckBB(){
		return new AxisAlignedBB(spawnerTile.getPos(),spawnerTile.getPos().add(1,1,1)).expand(spawnRange*2D,4D,spawnRange*2D);
	}
	
	protected boolean checkSpawnerConditions(){
		return true;
	}
	
	protected abstract boolean canMobSpawn(EntityLiving entity);
	
	protected void onMobSpawned(EntityLiving entity){}
	
	protected abstract EntityLiving createMob(World world);
	
	/*
	 * THESE ARE NOT OVERRIDABLE
	 */

	@Override
	public final void func_98267_a(int i){
		spawnerTile.getWorld().addBlockEvent(spawnerTile.getPos(),Blocks.mob_spawner,i,0);
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
		return spawnerTile.getWorld();
	}

	@Override
	public BlockPos func_177221_b(){ // OBFUSCATED getPos
		return spawnerTile.getPos();
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public final Entity func_180612_a(World world){
		if (entityCache == null)entityCache = func_98265_a(createMob(world)); // OBFUSCATED spawn entity
		return entityCache;
	}
	
	public static final class BrokenSpawnerLogic extends CustomSpawnerLogic{
		public BrokenSpawnerLogic(TileEntityCustomSpawner spawnerTile){
			super(spawnerTile);
		}
		
		@Override
		protected boolean canMobSpawn(EntityLiving entity){
			return false;
		}

		@Override
		protected EntityLiving createMob(World world){
			return new EntityEnderman(world);
		}
	}
}
