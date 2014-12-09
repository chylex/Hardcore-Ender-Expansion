package chylex.hee.system.integration;
import chylex.hee.system.integration.handlers.ArsMagicaFixIntegration;
import chylex.hee.system.integration.handlers.MineFactoryReloadedIntegration;
import chylex.hee.system.integration.handlers.NotEnoughItemsIntegration;
import chylex.hee.system.integration.handlers.ThaumcraftIntegration;
import chylex.hee.system.logging.Log;
import chylex.hee.system.logging.Stopwatch;
import cpw.mods.fml.common.Loader;

public final class ModIntegrationManager{
	public static final void integrateMods(){
		Stopwatch.time("ModIntegrationManager - integrateMods");
		
		Class[] handlerClasses = new Class[]{
			NotEnoughItemsIntegration.class,
			ThaumcraftIntegration.class,
			MineFactoryReloadedIntegration.class,
			ArsMagicaFixIntegration.class
		};
		
		for(Class<? extends IIntegrationHandler> cls:handlerClasses){
			try{
				IIntegrationHandler handler = cls.newInstance();
				if (Loader.isModLoaded(handler.getModId()))handler.integrate();
			}catch(Throwable e){
				Log.throwable(e,"Unable to integrate with mod $0.",cls.getSimpleName());
			}
		}
		
		Stopwatch.finish("ModIntegrationManager - integrateMods");
	}
}
