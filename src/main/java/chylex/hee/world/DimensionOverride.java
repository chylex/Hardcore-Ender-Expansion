package chylex.hee.world;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.terraingen.InitMapGenEvent;
import net.minecraftforge.event.terraingen.InitMapGenEvent.EventType;
import net.minecraftforge.event.world.WorldEvent;
import chylex.hee.world.biome.BiomeGenHardcoreEnd;
import chylex.hee.world.feature.WorldGenDispersedCluster;
import chylex.hee.world.feature.WorldGenEnergyShrine;
import chylex.hee.world.feature.WorldGenStronghold;
import chylex.hee.world.providers.ChunkProviderHardcoreEndServer;
import chylex.hee.world.providers.WorldProviderHardcoreEnd;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;

public final class DimensionOverride{
	public static void setup(){
		BiomeGenBase.getBiomeGenArray()[9] = null;
		BiomeGenBase.sky = new BiomeGenHardcoreEnd(9).setColor(8421631).setBiomeName("Sky").setDisableRain();
		BiomeGenBase.getBiomeGenArray()[9] = BiomeGenBase.sky;
		
		DimensionOverride instance = new DimensionOverride();
		MinecraftForge.EVENT_BUS.register(instance);
		MinecraftForge.TERRAIN_GEN_BUS.register(instance);
		
		GameRegistry.registerWorldGenerator(new WorldGenDispersedCluster(), Integer.MAX_VALUE);
		GameRegistry.registerWorldGenerator(new WorldGenStronghold(), Integer.MAX_VALUE-1);
		GameRegistry.registerWorldGenerator(new WorldGenEnergyShrine(), Integer.MAX_VALUE-2);
	}
	
	public static void postInit(){
		DimensionManager.unregisterProviderType(1);
		DimensionManager.registerProviderType(1, WorldProviderHardcoreEnd.class, false);
		
		BiomeGenBase endBiome = BiomeGenBase.getBiome(9);
		if (endBiome instanceof BiomeGenHardcoreEnd)((BiomeGenHardcoreEnd)endBiome).overrideMobLists(); // if false, integrity verification fails
	}
	
	private DimensionOverride(){}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e){
		if (e.world.provider.dimensionId == 1 && e.world instanceof WorldServer){
			WorldServer world = (WorldServer)e.world;
			world.chunkProvider = world.theChunkProviderServer = new ChunkProviderHardcoreEndServer(world);
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void onInitMapGen(InitMapGenEvent e){
		if (e.type == EventType.STRONGHOLD){
			e.newGen = new MapGenStronghold(){
				@Override
				protected boolean canSpawnStructureAtCoords(int x, int z){ return false; }
				
				@Override
				public ChunkPosition func_151545_a(World world, int x, int y, int z){ return new ChunkPosition(x, y+2, z); }
			};
		}
	}
}
