package chylex.hee.world.end.tick;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.entity.effect.EntityWeatherEffect;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C05CustomWeather;
import chylex.hee.packets.client.C05CustomWeather.IWeatherEntityConstructor;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.world.end.EndTerritory;
import chylex.hee.world.util.IRangeGenerator;
import chylex.hee.world.util.IRangeGenerator.RangeGenerator;
import chylex.hee.world.util.RandomAmount;

public class TerritoryBehaviorWeather implements ITerritoryBehavior{
	public static final TerritoryBehaviorWeather forLightning(final EndTerritory territory, final Pos centerPos, IWeatherEntityConstructor<?> weatherConstructor){
		final int chunkRad = territory.createBoundingBox().x2;
		final PosMutable mpos = new PosMutable();
		
		final Function<World,Pos> locationSelector = world -> {
			mpos.set(centerPos).move(world.rand.nextInt(chunkRad*2+1)-chunkRad,0,world.rand.nextInt(chunkRad*2+1)-chunkRad);
			mpos.y = world.getPrecipitationHeight(mpos.getX(),mpos.getZ());
			return mpos;
		};
		
		TerritoryBehaviorWeather behavior = new TerritoryBehaviorWeather(territory,centerPos,locationSelector,weatherConstructor);
		behavior.addCondition(pos -> pos.getY() > 0);
		return behavior;
	}
	
	private final Pos centerPos;
	private final Function<World,Pos> locationSelector;
	private final IWeatherEntityConstructor<?> weatherConstructor;
	private final int packetDistance;
	
	private float chancePerTick;
	private int effectSpawnAttempts = 1;
	private IRangeGenerator effectAmountGenerator = rand -> 1;
	private IRangeGenerator delayGenerator = rand -> 0;
	private Predicate<Pos> effectSpawnCondition = pos -> true;
	
	private int delayLeft;
	
	public TerritoryBehaviorWeather(EndTerritory territory, Pos centerPos, Function<World,Pos> locationSelector, IWeatherEntityConstructor<?> weatherConstructor){
		this.centerPos = centerPos;
		this.locationSelector = locationSelector;
		this.weatherConstructor = weatherConstructor;
		this.packetDistance = territory.createBoundingBox().x2+EndTerritory.chunksBetween*8;
	}
	
	// Setup
	
	public void setChancePerTick(float chancePerTick){
		this.chancePerTick = chancePerTick;
	}
	
	public void setEffectSpawnAttempts(int effectSpawnAttempts){
		this.effectSpawnAttempts = effectSpawnAttempts;
	}
	
	public void setEffectAmount(IRangeGenerator effectAmountGenerator){
		this.effectAmountGenerator = effectAmountGenerator;
	}
	
	public void setDelayAfterTrigger(IRangeGenerator delayGenerator){
		this.delayGenerator = delayGenerator;
	}
	
	public void setDelayAfterTrigger(int minDelay, int maxDelay){
		this.delayGenerator = new RangeGenerator(minDelay,maxDelay,RandomAmount.linear);
	}
	
	public void addCondition(Predicate<Pos> condition){
		this.effectSpawnCondition = this.effectSpawnCondition.and(condition);
	}
	
	// Ticking
	
	@Override
	public void tick(World world, NBTTagCompound nbt){
		if (delayLeft > 0){
			--delayLeft;
			return;
		}
		
		if (world.rand.nextFloat() < chancePerTick){
			HardcoreEnderExpansion.notifications.report("tick");
			boolean spawned = false;
			
			for(int amount = effectAmountGenerator.next(world.rand); amount > 0; amount--){
				for(int attempt = 0; attempt < effectSpawnAttempts; attempt++){
					Pos pos = locationSelector.apply(world);
					
					if (effectSpawnCondition.test(pos)){
						EntityWeatherEffect eff = weatherConstructor.construct(world,pos.getX()+world.rand.nextDouble(),pos.getY()+world.rand.nextDouble(),pos.getZ()+world.rand.nextDouble());
						
						world.addWeatherEffect(eff);
						PacketPipeline.sendToAllAround(world.provider.dimensionId,centerPos,packetDistance,new C05CustomWeather(eff));
						
						spawned = true;
						break;
					}
				}
			}
			
			if (spawned)delayLeft = delayGenerator.next(world.rand);
		}
	}
}
