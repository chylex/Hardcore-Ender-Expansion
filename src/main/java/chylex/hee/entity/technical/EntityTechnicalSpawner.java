package chylex.hee.entity.technical;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.logging.Log;
import chylex.hee.world.util.BoundingBox;
import com.google.common.collect.ImmutableList;

public class EntityTechnicalSpawner<T extends EntityLiving> extends EntityTechnicalBase{
	private IVirtualSpawner<T> spawner;
	private int timer = -1;
	
	public EntityTechnicalSpawner(World world){
		super(world);
	}
	
	public EntityTechnicalSpawner(World world, double x, double y, double z, IVirtualSpawner<T> spawner){
		super(world);
		setPosition(x,y,z);
		this.spawner = spawner;
	}

	@Override
	protected void entityInit(){}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		if (worldObj.isRemote)return;
		
		if (spawner == null)setDead();
		else if (timer == -1){
			spawner.init(this);
			timer = spawner.getCheckTimer(rand);
		}
		else if (--timer == 0){
			timer = spawner.getCheckTimer(rand);
			
			ImmutableList<EntityPlayer> players = ImmutableList.copyOf(spawner.getPlayersInRange(worldObj));
			
			if (!players.isEmpty()){
				for(EntityPlayer player:players){
					int mobsLeft = spawner.getSpawnLimit(rand);
					if (mobsLeft == 0)continue;
					
					for(int attemptsLeft = spawner.getSpawnAttempts(rand); attemptsLeft > 0 && mobsLeft > 0; attemptsLeft--){
						T entity = spawner.createEntity(worldObj);
						spawner.findSpawnPosition(worldObj,rand,player,entity,spawner.getSpawnRange(rand));
						
						if (spawner.checkSpawnConditions(worldObj,rand,players,player,entity)){
							worldObj.spawnEntityInWorld(entity);
							--mobsLeft;
						}
					}
				}
			}
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound nbt){
		if (spawner != null)nbt.setString("spawnerCls",spawner.getClass().getName());
	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound nbt){
		try{
			this.spawner = ((Class<? extends IVirtualSpawner>)Class.forName(nbt.getString("spawnerCls"))).newInstance();
		}catch(Throwable t){
			Log.throwable(t,"Unable to load a spawner entity: $0",nbt.getString("spawnerCls"));
			setDead();
		}
	}
	
	public static interface IVirtualSpawner<T extends EntityLiving>{
		void init(EntityTechnicalSpawner owner);
		T createEntity(World world);
		
		int getCheckTimer(Random rand);
		int getSpawnAttempts(Random rand);
		int getSpawnLimit(Random rand);
		double getSpawnRange(Random rand);
		BoundingBox getCheckBox();
		
		default List<EntityPlayer> getPlayersInRange(World world){
			return EntitySelector.players(world,getCheckBox().toAABB());
		}
		
		default void findSpawnPosition(World world, Random rand, EntityPlayer target, T entity, double range){
			Vec vec = Vec.xzRandom(rand);
			entity.setPositionAndRotation(target.posX+vec.x*range,target.posY+(rand.nextDouble()-0.5D)*range,target.posZ+vec.z*range,rand.nextFloat()*360F-180F,0F);
		}
		
		default boolean checkSpawnConditions(World world, Random rand, ImmutableList<EntityPlayer> playersInRange, EntityPlayer target, T entity){
			return entity.getCanSpawnHere();
		}
	}
}
