package chylex.hee.mechanics.energy;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.util.Direction;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.system.savedata.WorldDataHandler;
import chylex.hee.system.savedata.types.EnergySavefile;
import chylex.hee.system.util.MathUtil;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;

public final class EnergyEvents{
	private static final EnergyEvents instance = new EnergyEvents();
	
	public static void register(){
		MinecraftForge.EVENT_BUS.register(instance);
		FMLCommonHandler.instance().bus().register(instance);
	}
	
	@SubscribeEvent
	public void onWorldTick(WorldTickEvent e){
		if (e.phase != Phase.START || e.world.provider.dimensionId != 1 || e.world.isRemote || ++updateTimer <= 4)return;
		
		Stopwatch.timeAverage("EnergyEvents - WorldTick",80);
		
		List<Chunk> chunks = new ArrayList<>(((ChunkProviderServer)e.world.getChunkProvider()).loadedChunks);
		Set<EnergyChunkData> usedData = new HashSet<>();
		EnergySavefile file = WorldDataHandler.get(EnergySavefile.class);
		
		for(Chunk chunk:chunks){
			EnergyChunkData data = file.getFromChunkCoords(e.world,chunk.xPosition,chunk.zPosition,true);
			if (usedData.contains(data))continue;
			
			data.onUpdate(e.world,e.world.rand);
			
			for(int a = 0; a < 4; a++){
				data.onAdjacentInteract(e.world.rand,file.getFromChunkCoords(e.world,chunk.xPosition+Direction.offsetX[a]*EnergySavefile.sectionSize,chunk.zPosition+Direction.offsetZ[a]*EnergySavefile.sectionSize,true));
			}
			
			usedData.add(data);
		}

		updateTimer = 0;
		Stopwatch.finish("EnergyEvents - WorldTick");
	}
	
	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onLivingDeath(LivingDeathEvent e){
		if (e.entity.worldObj.isRemote || e.entity.dimension != 1)return;
		
		float energy = EnergyValues.getMobEnergy(e.entityLiving);
		if (MathUtil.floatEquals(energy,0F))return;
		
		WorldDataHandler.<EnergySavefile>get(EnergySavefile.class).getFromBlockCoords(e.entity.worldObj,MathUtil.floor(e.entity.posX),MathUtil.floor(e.entity.posZ),true).addEnergy(energy);
	}
	
	private byte updateTimer;
	
	private EnergyEvents(){}
}
