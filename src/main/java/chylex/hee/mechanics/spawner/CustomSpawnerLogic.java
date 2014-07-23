package chylex.hee.mechanics.spawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.MobSpawnerBaseLogic;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.tileentity.TileEntityCustomSpawner;

public abstract class CustomSpawnerLogic extends MobSpawnerBaseLogic{
	protected TileEntityCustomSpawner spawnerTile;
	protected int minSpawnDelay = 200;
	protected int maxSpawnDelay = 800;
	protected byte spawnRange;
	protected byte spawnCount = 4;
	protected byte maxNearbyEntities = 6;
	protected byte activatingRangeFromPlayer = 16;
	protected Entity entityCache;

	protected CustomSpawnerLogic(TileEntityCustomSpawner spawnerTile){
		this.spawnerTile = spawnerTile;
	}

	public void onBlockBreak(){}

	@Override
	public void func_98267_a(int i){
		spawnerTile.getWorldObj().addBlockEvent(spawnerTile.xCoord,spawnerTile.yCoord,spawnerTile.zCoord,Blocks.mob_spawner,i,0);
	}

	@Override
	public World getSpawnerWorld(){
		return spawnerTile.getWorldObj();
	}

	@Override
	public int getSpawnerX(){
		return spawnerTile.xCoord;
	}

	@Override
	public int getSpawnerY(){
		return spawnerTile.yCoord;
	}

	@Override
	public int getSpawnerZ(){
		return spawnerTile.zCoord;
	}

	@Override
	public boolean isActivated(){
		return getSpawnerWorld().getClosestPlayer(getSpawnerX()+0.5D,getSpawnerY()+0.5D,getSpawnerZ()+0.5D,activatingRangeFromPlayer) != null;
	}

	@Override
	public void updateSpawner(){
		if (isActivated()){
			if (getSpawnerWorld().isRemote){
				double x = (getSpawnerX()+getSpawnerWorld().rand.nextFloat());
				double y = (getSpawnerY()+getSpawnerWorld().rand.nextFloat());
				double z = (getSpawnerZ()+getSpawnerWorld().rand.nextFloat());
				getSpawnerWorld().spawnParticle("smoke",x,y,z,0D,0D,0D);
				getSpawnerWorld().spawnParticle("flame",x,y,z,0D,0D,0D);

				if (spawnDelay > 0){
					--spawnDelay;
				}

				field_98284_d = field_98287_c;
				field_98287_c = (field_98287_c+(1000.0F/(spawnDelay+200.0F)))%360.0D;
			}
			else{
				if (spawnDelay == -1){
					resetTimer();
				}

				if (spawnDelay > 0){
					--spawnDelay;
					return;
				}

				boolean flag = false;

				for(int i = 0; i < spawnCount; ++i){
					Entity entity = EntityList.createEntityByName(getEntityNameToSpawn(),getSpawnerWorld());
					if (entity == null)return;

					int nearby = getSpawnerWorld().getEntitiesWithinAABB(entity.getClass(),AxisAlignedBB.getBoundingBox(getSpawnerX(),getSpawnerY(),getSpawnerZ(),(getSpawnerX()+1),(getSpawnerY()+1),(getSpawnerZ()+1)).expand((spawnRange*2),4.0D,(spawnRange*2))).size();

					if (nearby >= maxNearbyEntities){
						resetTimer();
						return;
					}

					double posX = getSpawnerX()+(getSpawnerWorld().rand.nextDouble()-getSpawnerWorld().rand.nextDouble())*spawnRange;
					double posY = (getSpawnerY()+getSpawnerWorld().rand.nextInt(3)-1);
					double posZ = getSpawnerZ()+(getSpawnerWorld().rand.nextDouble()-getSpawnerWorld().rand.nextDouble())*spawnRange;
					EntityLiving entityliving = entity instanceof EntityLiving?(EntityLiving)entity:null;
					entity.setLocationAndAngles(posX,posY,posZ,getSpawnerWorld().rand.nextFloat()*360F,0F);

					if (entityliving == null || entityliving.getCanSpawnHere()){
						func_98265_a(entity); // OBFUSCATED spawn entity
						getSpawnerWorld().playAuxSFX(2004,getSpawnerX(),getSpawnerY(),getSpawnerZ(),0);

						if (entityliving != null){
							entityliving.spawnExplosionParticle();
						}

						flag = true;
					}
				}

				if (flag){
					resetTimer();
				}
			}
		}
	}

	protected void resetTimer(){
		if (maxSpawnDelay <= minSpawnDelay)spawnDelay = minSpawnDelay;
		else{
			int i = maxSpawnDelay-minSpawnDelay;
			spawnDelay = minSpawnDelay+getSpawnerWorld().rand.nextInt(i);
		}
		func_98267_a(1);
	}
}
