package chylex.hee.game.integration;
import java.util.HashSet;
import java.util.Set;
import chylex.hee.game.integration.handlers.ArsMagicaFixIntegration;
import chylex.hee.game.integration.handlers.MineFactoryReloadedIntegration;
import chylex.hee.game.integration.handlers.ThaumcraftIntegration;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;

public final class ModIntegrationManager{
	public static final Set<String> blacklistedMods = new HashSet<>();
	
	public static final void integrateMods(){
		Stopwatch.time("ModIntegrationManager - integrateMods");
		
		Class[] handlerClasses = new Class[]{
			ThaumcraftIntegration.class,
			MineFactoryReloadedIntegration.class,
			ArsMagicaFixIntegration.class
		};
		
		for(Class<? extends IIntegrationHandler> cls:handlerClasses){
			try{
				IIntegrationHandler handler = cls.newInstance();
				String modId = handler.getModId();
				if (Loader.isModLoaded(modId) && !blacklistedMods.contains(modId))handler.integrate();
			}catch(Throwable e){
				Log.throwable(e,"Unable to integrate with mod $0.",cls.getSimpleName());
			}
		}
		
		Stopwatch.finish("ModIntegrationManager - integrateMods");
	}
	
	public static final void sendIMCs(){
		FMLInterModComms.sendMessage("rftools","dimlet_blacklist","Material.tile.endiumBlock"); // TODO ffs unlocalized names
		FMLInterModComms.sendMessage("rftools","dimlet_blacklist","Material.tile.endiumOre");
		FMLInterModComms.sendMessage("rftools","dimlet_blacklist","Material.tile.igneousRockOre");
		FMLInterModComms.sendMessage("rftools","dimlet_blacklist","Material.tile.stardustOre");
		FMLInterModComms.sendMessage("rftools","dimlet_blacklist","Material.tile.instabilityOrbOre");
	}
}
