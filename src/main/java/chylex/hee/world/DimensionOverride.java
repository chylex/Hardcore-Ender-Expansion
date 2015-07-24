package chylex.hee.world;
import java.lang.reflect.Field;
import java.util.Hashtable;
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
import chylex.hee.world.providers.ChunkProviderHardcoreEndServer;
import chylex.hee.world.providers.WorldProviderHardcoreEnd;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class DimensionOverride{
	public static void setup(){
		BiomeGenBase.sky = new BiomeGenHardcoreEnd(9).setColor(8421631).setBiomeName("Sky").setDisableRain();
		BiomeGenBase.getBiomeGenArray()[9] = BiomeGenBase.sky;
		
		DimensionOverride instance = new DimensionOverride();
		MinecraftForge.EVENT_BUS.register(instance);
		MinecraftForge.TERRAIN_GEN_BUS.register(instance);
	}
	
	public static void postInit(){
		DimensionManager.unregisterProviderType(1);
		DimensionManager.registerProviderType(1,WorldProviderHardcoreEnd.class,false);
	}
	
	public static void verifyIntegrity(){
		if (!(BiomeGenBase.sky instanceof BiomeGenHardcoreEnd))throw new RuntimeException("End biome class mismatch, Hardcore Ender Expansion cannot proceed! Biome class: "+BiomeGenBase.sky.getClass().getName());
		((BiomeGenHardcoreEnd)BiomeGenBase.getBiome(9)).overrideMobLists();
		
		try{
			Field f = DimensionManager.class.getDeclaredField("providers");
			f.setAccessible(true); // let it throw NPE if the field isn't found
			
			Class cls = ((Hashtable<Integer,Class>)f.get(null)).get(1);
			if (cls != WorldProviderHardcoreEnd.class)throw new RuntimeException("End world provider class mismatch, Hardcore Ender Expansion cannot proceed! Provider class: "+(cls == null ? "<null>" : cls.getName()));
		}catch(NullPointerException | NoSuchFieldException | IllegalArgumentException | IllegalAccessException e){
			throw new RuntimeException("End world provider check failed!",e);
		}
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
				public ChunkPosition func_151545_a(World world, int x, int y, int z){ return new ChunkPosition(x,y+2,z); }
			};
		}
	}
}
