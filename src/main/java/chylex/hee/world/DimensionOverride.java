package chylex.hee.world;
import java.lang.reflect.Field;
import java.util.Hashtable;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import chylex.hee.world.biome.BiomeGenHardcoreEnd;
import chylex.hee.world.providers.ChunkProviderHardcoreEndServer;
import chylex.hee.world.providers.WorldProviderHardcoreEnd;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public final class DimensionOverride{
	public static void setup(){
		BiomeGenBase.sky = new BiomeGenHardcoreEnd(9).setColor(8421631).setBiomeName("Sky").setDisableRain();
		BiomeGenBase.getBiomeGenArray()[9] = BiomeGenBase.sky;
		
		MinecraftForge.EVENT_BUS.register(new DimensionOverride());
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
}
