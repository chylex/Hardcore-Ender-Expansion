package chylex.hee.game.integration;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import chylex.hee.game.integration.handlers.MineFactoryReloadedIntegration;
import chylex.hee.game.integration.handlers.ThaumcraftIntegration;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;

public final class ModIntegrationManager{
	private static Map<String, Supplier<IIntegrationHandler>> handlers = new HashMap<>();
	
	static{
		handlers.put("Thaumcraft", ThaumcraftIntegration::new);
		handlers.put("MineFactoryReloaded", MineFactoryReloadedIntegration::new);
	}
	
	public static boolean blacklistMod(String modId){
		return handlers.remove(modId) != null;
	}
	
	public static void integrateMods(){
		Stopwatch.time("ModIntegrationManager - integrateMods");
		
		for(Entry<String, Supplier<IIntegrationHandler>> entry:handlers.entrySet()){
			if (!Loader.isModLoaded(entry.getKey()))continue;
			
			try{
				entry.getValue().get().integrate();
			}catch(Throwable e){
				Log.throwable(e, "Unable to integrate with mod $0.", entry.getKey());
			}
		}
		
		handlers = null; // collect
		
		Stopwatch.finish("ModIntegrationManager - integrateMods");
	}
	
	public static void sendIMCs(){
		FMLInterModComms.sendMessage("rftools", "dimlet_blacklist", "Material.tile.endiumBlock"); // TODO ffs unlocalized names
		FMLInterModComms.sendMessage("rftools", "dimlet_blacklist", "Material.tile.endiumOre");
		FMLInterModComms.sendMessage("rftools", "dimlet_blacklist", "Material.tile.igneousRockOre");
		FMLInterModComms.sendMessage("rftools", "dimlet_blacklist", "Material.tile.stardustOre");
		FMLInterModComms.sendMessage("rftools", "dimlet_blacklist", "Material.tile.instabilityOrbOre");
	}
}
