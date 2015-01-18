package chylex.hee.world;
import java.lang.reflect.Field;
import java.util.Hashtable;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import chylex.hee.system.logging.Stopwatch;
import chylex.hee.world.biome.BiomeGenHardcoreEnd;
import chylex.hee.world.structure.island.ComponentIsland;
import chylex.hee.world.structure.island.StructureIsland;
import chylex.hee.world.structure.tower.ComponentTower;
import chylex.hee.world.structure.tower.StructureTower;

public final class DimensionOverride{
	public static void setup(){
		Stopwatch.time("DimensionOverride");
		
		overrideBiome();
		overrideWorldGen();

		MapGenStructureIO.registerStructure(StructureTower.class,"hardcoreenderdragon_EndTower");
		MapGenStructureIO.registerStructureComponent(ComponentTower.class,"hardcoreenderdragon_EndTowerC");
		MapGenStructureIO.registerStructure(StructureIsland.class,"hardcoreenderdragon_EndIsland");
		MapGenStructureIO.registerStructureComponent(ComponentIsland.class,"hardcoreenderdragon_EndIslandC");
		
		if (BiomeGenHardcoreEnd.overrideWorldGen)MinecraftForge.EVENT_BUS.register(new DimensionOverride());
		
		Stopwatch.finish("DimensionOverride");
	}
	
	public static void postInit(){
		Stopwatch.time("DimensionOverride - PostInit");
		
		if (!(BiomeGenBase.sky instanceof BiomeGenHardcoreEnd))throw new RuntimeException("End biome class mismatch, Hardcore Ender Expansion cannot proceed! Biome class: "+BiomeGenBase.sky.getClass().getName());
		
		try{
			Field f = DimensionManager.class.getDeclaredField("providers");
			f.setAccessible(true); // let it throw NPE if the field isn't found
			
			Class<?> cls = ((Hashtable<Integer,Class<? extends WorldProvider>>)f.get(null)).get(1);
			if (cls != WorldProviderHardcoreEnd.class)throw new RuntimeException("End world provider class mismatch, Hardcore Ender Expansion cannot proceed! Provider class: "+(cls == null ? "<null>" : cls.getName()));
		}catch(NullPointerException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e){
			throw new RuntimeException("End world provider check failed!",e);
		}
		
		((BiomeGenHardcoreEnd)BiomeGenBase.getBiome(9)).overrideMobLists();
		
		Stopwatch.finish("DimensionOverride - PostInit");
	}
	
	private static void overrideBiome(){
		Stopwatch.time("DimensionOverride - Biome");
		
		BiomeGenBase.sky = new BiomeGenHardcoreEnd(9).setColor(8421631).setBiomeName("Sky").setDisableRain();
		BiomeGenBase.getBiomeGenArray()[9] = BiomeGenBase.sky;
		
		Stopwatch.finish("DimensionOverride - Biome");
	}
	
	private static void overrideWorldGen(){
		Stopwatch.time("DimensionOverride - WorldProvider");
		
		DimensionManager.unregisterProviderType(1);
		DimensionManager.registerProviderType(1,WorldProviderHardcoreEnd.class,false);

		Stopwatch.finish("DimensionOverride - WorldProvider");
	}
	
	private DimensionOverride(){}
	
	@SubscribeEvent
	public void onWorldLoad(WorldEvent.Load e){
		if (e.world.provider.getDimensionId() == 1 && e.world instanceof WorldServer){
			WorldServer world = (WorldServer)e.world;
			world.chunkProvider = world.theChunkProviderServer = new ChunkProviderServerOverride(world);
		}
	}
	
	public static final class ChunkProviderServerOverride extends ChunkProviderServer{
		public ChunkProviderServerOverride(WorldServer world){
			super(world,world.theChunkProviderServer.currentChunkLoader,world.theChunkProviderServer.currentChunkProvider);
		}

		@Override
		public void populate(IChunkProvider provider, int x, int z){
			Chunk chunk = provideChunk(x,z);
			
			if (!chunk.isTerrainPopulated()){
				chunk.func_150809_p();
				
				if (currentChunkProvider != null){
					currentChunkProvider.populate(provider,x,z);
					chunk.setChunkModified();
				}
			}
		}
	}
}
