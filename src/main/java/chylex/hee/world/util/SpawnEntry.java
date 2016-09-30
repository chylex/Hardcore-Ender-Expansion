package chylex.hee.world.util;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.IMob;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.logging.Log;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.util.IRangeGenerator.RangeGenerator;

public class SpawnEntry<T extends EntityLiving>{
	public static <T extends EntityLiving> Builder<T> create(Class<T> mobClass, Function<World, T> mobConstructor){
		return new Builder<>(mobClass, mobConstructor);
	}

	public static <T extends EntityLiving> Builder<T> create(Class<T> entityCls){
		try{
			final MethodHandle constructor = MethodHandles.lookup().findConstructor(entityCls, MethodType.methodType(entityCls, World.class));
			
			return new Builder<>(entityCls, world -> {
				try{ return (T)constructor.invokeExact(world); }
				catch(Throwable t){ return null; }
			});
		}catch(NoSuchMethodException|IllegalAccessException e){
			e.printStackTrace();
			Log.throwable(e, "Could not find constructor for entity $0.", entityCls.getName());
			return null;
		}
	}
	
	// Entry Builder
	
	public static final class Builder<T extends EntityLiving>{
		private final Class<T> mobClass;
		private final Function<World, T> mobConstructor;
		private Consumer<T> locationFinder;
		private Predicate<T> spawnCondition = entity -> entity.worldObj.getCollidingBoundingBoxes(entity, entity.boundingBox).isEmpty();
		
		private IRangeGenerator groupSize;
		private IGroupLocationFinder<T> groupLocationFinder;
		
		Builder(Class<T> mobClass, Function<World, T> mobConstructor){
			this.mobClass = mobClass;
			this.mobConstructor = mobConstructor;
		}
		
		public Builder<T> setLocationFinder(Consumer<T> locationFinder){
			this.locationFinder = locationFinder;
			return this;
		}
		
		public Builder<T> addSpawnCondition(Predicate<T> condition){
			this.spawnCondition = this.spawnCondition.and(condition);
			return this;
		}
		
		public Builder<T> addSpawnConditions(Predicate<T>...conditions){
			for(Predicate<T> condition:conditions)this.spawnCondition = this.spawnCondition.and(condition);
			return this;
		}
		
		public Builder<T> addVanillaSpawnConditions(){
			addSpawnCondition(noLiquid());
			addSpawnCondition(noCollidingEntities());
			return this;
		}
		
		public Builder<T> setSpawnPlayerDistance(double minDistance){
			addSpawnCondition(entity -> entity.worldObj.getClosestPlayerToEntity(entity, minDistance) == null);
			return this;
		}
		
		public Builder<T> setGroupSize(int min, int max){
			if (!(min == 1 && max == 1))this.groupSize = new RangeGenerator(min, max, RandomAmount.linear);
			return this;
		}
		
		public Builder<T> setGroupSize(int min, int max, RandomAmount distribution){
			if (!(min == 1 && max == 1))this.groupSize = new RangeGenerator(min, max, distribution);
			return this;
		}
		
		public Builder<T> setGroupSize(IRangeGenerator range){
			this.groupSize = range;
			return this;
		}
		
		public Builder<T> setGroupLocationFinder(IGroupLocationFinder<T> groupLocationFinder){
			this.groupLocationFinder = groupLocationFinder;
			return this;
		}
		
		public SpawnEntry build(){
			if (locationFinder == null)throw new IllegalStateException("Spawn Entry has no location finder!");
			if (groupSize != null && groupLocationFinder == null)throw new IllegalStateException("Group Spawn Entry has no group location finder!");
			
			return groupSize == null ? new SpawnEntry<>(mobClass, mobConstructor, locationFinder, spawnCondition) : new GroupSpawnEntry<>(mobClass, mobConstructor, locationFinder, spawnCondition, groupSize, groupLocationFinder);
		}
	}
	
	// Conditions
	
	public static final <T extends EntityLiving> Predicate<T> noLiquid(){
		return entity -> !entity.worldObj.isAnyLiquid(entity.boundingBox);
	}
	
	public static final <T extends EntityLiving> Predicate<T> noCollidingEntities(){
		return entity -> entity.worldObj.checkNoEntityCollision(entity.boundingBox);
	}
	
	public static final <T extends EntityLiving> Predicate<T> withDifficulty(final EnumDifficulty minDifficulty){
		return entity -> entity.worldObj.difficultySetting.getDifficultyId() >= minDifficulty.getDifficultyId();
	}
	
	public static final <T extends EntityLiving> Predicate<T> withBlocksNearby(final int maxDistance){
		return withBlocksNearby(maxDistance, MathUtil.square(maxDistance*2+1));
	}
	
	public static final <T extends EntityLiving> Predicate<T> withBlocksNearby(final int maxDistance, final int attempts){
		PosMutable mpos = new PosMutable();
		
		return entity -> {
			final Random rand = entity.getRNG();
			
			for(int attempt = 0; attempt < attempts; attempt++){
				mpos.set(entity).move(rand.nextInt(maxDistance*2+1)-maxDistance, rand.nextInt(maxDistance*2+1)-maxDistance, rand.nextInt(maxDistance*2+1)-maxDistance);
				if (!mpos.isAir(entity.worldObj))return true;
			}
			
			return false;
		};
	}
	
	// Spawn Entry
	
	protected final Class<T> mobClass;
	protected final Function<World, T> mobConstructor;
	protected final Consumer<T> locationFinder;
	protected final Predicate<T> spawnCondition;
	
	public final boolean isHostile;
	
	public SpawnEntry(Class<T> mobClass, Function<World, T> mobConstructor, Consumer<T> locationFinder, Predicate<T> spawnCondition){
		this.mobClass = mobClass;
		this.mobConstructor = mobConstructor;
		this.locationFinder = locationFinder;
		this.spawnCondition = spawnCondition;
		this.isHostile = IMob.class.isAssignableFrom(mobClass);
	}
	
	public Class<T> getMobClass(){
		return mobClass;
	}
	
	public T trySpawn(World world, int attempts){
		T entity = mobConstructor.apply(world);
		
		for(int attempt = 0; attempt < attempts; attempt++){
			locationFinder.accept(entity);
			
			if (spawnCondition.test(entity)){
				world.spawnEntityInWorld(entity);
				return entity;
			}
		}
		
		return null;
	}
	
	// Group Spawn Entry
	
	public static class GroupSpawnEntry<T extends EntityLiving> extends SpawnEntry<T>{
		private final IRangeGenerator groupSize;
		private final IGroupLocationFinder<T> groupLocationFinder;
		
		public GroupSpawnEntry(Class<T> mobClass, Function<World, T> mobConstructor, Consumer<T> locationFinder, Predicate<T> spawnCondition, IRangeGenerator groupSize, IGroupLocationFinder<T> groupLocationFinder){
			super(mobClass, mobConstructor, locationFinder, spawnCondition);
			this.groupSize = groupSize;
			this.groupLocationFinder = groupLocationFinder;
		}
		
		@Override
		public T trySpawn(World world, int attempts){
			int amount = groupSize.next(world.rand);
			if (amount == 0)return null;
			
			T first = super.trySpawn(world, attempts);
			if (first == null)return null;
			
			while(--amount > 0){
				T entity = mobConstructor.apply(world);
				
				for(int attempt = 0; attempt < attempts; attempt++){
					groupLocationFinder.accept(first, entity);
					
					if (spawnCondition.test(entity)){
						world.spawnEntityInWorld(entity);
						break;
					}
				}
			}
			
			return first;
		}
	}
	
	public static interface IGroupLocationFinder<T extends EntityLiving> extends BiConsumer<T, T>{
		@Override
		public void accept(T parentEntity, T groupedEntity); // provide specific parameter names for convenience
	}
}
