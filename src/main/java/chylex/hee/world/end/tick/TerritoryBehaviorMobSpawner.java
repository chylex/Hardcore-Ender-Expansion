package chylex.hee.world.end.tick;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.BlockInfo;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Vec;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.collections.weight.WeightedMap;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.end.EndTerritory;
import chylex.hee.world.util.BoundingBox;
import chylex.hee.world.util.SpawnEntry;
import chylex.hee.world.util.SpawnEntry.IGroupLocationFinder;

public class TerritoryBehaviorMobSpawner implements ITerritoryBehavior{
	private final WeightedMap<SpawnEntry> spawnEntries = new WeightedMap<>();
	private final List<Consumer<SpawnEntry.Builder<?>>> entryBuilderModifiers = new ArrayList<>(2);
	private final BoundingBox area;
	
	private int tickRate = 10;
	private int attemptsPerTick = 4;
	private int attemptsPerMob = 15;
	
	private ToIntFunction<World> mobLimit = world -> 60;
	
	private int tickLimiter;
	
	public TerritoryBehaviorMobSpawner(EndTerritory territory, Pos centerPos){
		this.area = territory.createBoundingBox().offset(centerPos);
	}
	
	public void addBuilderModifier(Consumer<SpawnEntry.Builder<?>> entryCallback){
		this.entryBuilderModifiers.add(entryCallback);
	}
	
	public void addEntry(SpawnEntry.Builder<?> entryBuilder, int weight){
		for(Consumer<SpawnEntry.Builder<?>> callback:entryBuilderModifiers)callback.accept(entryBuilder);
		this.spawnEntries.add(entryBuilder.build(),weight);
	}
	
	public void setTickRate(int tickRate){
		this.tickRate = tickRate;
	}
	
	public void setAttemptsPerTick(int attemptsPerTick){
		this.attemptsPerTick = attemptsPerTick;
	}
	
	public void setAttemptsPerMob(int attemptsPerMob){
		this.attemptsPerMob = attemptsPerMob;
	}
	
	public void setMobLimit(final int maxMobs){
		this.mobLimit = world -> maxMobs;
	}
	
	public void setMobLimit(ToIntFunction<World> mobLimitFunc){
		this.mobLimit = mobLimitFunc;
	}
	
	public void setMobLimitScaled(final int initialValue, final int addPerPlayer, final int maxValue){
		this.mobLimit = world -> Math.min(maxValue,initialValue+EntitySelector.players(world,area.toAABB()).size()*addPerPlayer);
	}
	
	@Override
	public void tick(World world, NBTTagCompound nbt){
		if (++tickLimiter > tickRate){
			tickLimiter = 0;
			
			if (EntitySelector.living(world,area.toAABB()).size() > mobLimit.applyAsInt(world))return;
			
			for(int attempt = 0; attempt < attemptsPerTick; attempt++){
				spawnEntries.getRandomItem(world.rand).trySpawn(world,attemptsPerMob);
			}
		}
	}
	
	public <T extends EntityLiving> Consumer<T> spawnOnFloor(final Predicate<BlockInfo> blockFinder){
		return entity -> {
			final Random rand = entity.worldObj.rand;
			final double posX = area.x1+rand.nextDouble()*(area.x2-area.x1);
			final double posZ = area.z1+rand.nextDouble()*(area.z2-area.z1);
			final int posY = Pos.getTopBlock(entity.worldObj,MathUtil.floor(posX),MathUtil.floor(posZ),area.y1+rand.nextInt(1+area.y2-area.y1),blockFinder).getY();
			
			entity.setPosition(posX,posY,posZ);
		};
	}
	
	public <T extends EntityLiving> Consumer<T> spawnOnSolidFloor(){
		return spawnOnFloor(info -> info.block.getMaterial().blocksMovement());
	}
	
	public <T extends EntityLiving> Consumer<T> spawnInAir(){
		return entity -> {
			final Random rand = entity.worldObj.rand;
			entity.setPosition(area.x1+rand.nextDouble()*(area.x2-area.x1),area.y1+rand.nextDouble()*(area.y2-area.y1),area.z1+rand.nextDouble()*(area.z2-area.z1));
		};
	}
	
	public <T extends EntityLiving> IGroupLocationFinder<T> groupOnFloor(final double minDistance, final double maxDistance, final Predicate<BlockInfo> blockFinder){
		return (parentEntity, groupedEntity) -> {
			final Random rand = parentEntity.worldObj.rand;
			final Vec offset = Vec.xzRandom(rand).multiplied(minDistance+rand.nextDouble()*(maxDistance-minDistance));
			final double posX = parentEntity.posX+offset.x;
			final double posZ = parentEntity.posZ+offset.z;
			final int posY = Pos.getTopBlock(parentEntity.worldObj,MathUtil.floor(posX),MathUtil.floor(posZ),MathUtil.floor(parentEntity.posY+maxDistance),blockFinder).getY();
			
			groupedEntity.setPosition(posX,posY,posZ);
		};
	}
	
	public <T extends EntityLiving> IGroupLocationFinder<T> groupOnFloor(final double minDistance, final double maxDistance){
		return groupOnFloor(minDistance,maxDistance,info -> info.block.getMaterial().blocksMovement());
	}
	
	public <T extends EntityLiving> IGroupLocationFinder<T> groupInAir(final double minDistance, final double maxDistance){
		return (parentEntity, groupedEntity) -> {
			final Random rand = parentEntity.worldObj.rand;
			final Vec offset = Vec.xyzRandom(rand).multiplied(minDistance+rand.nextDouble()*(maxDistance-minDistance));
			
			groupedEntity.setPosition(parentEntity.posX+offset.x,parentEntity.posY+offset.y,parentEntity.posZ+offset.z);
		};
	}
}
