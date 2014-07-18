package chylex.hee.system.integration;
import chylex.hee.system.integration.handlers.DartCraftIntegration;
import chylex.hee.system.integration.handlers.NotEnoughItemsIntegration;
import chylex.hee.system.integration.handlers.ThaumcraftIntegration;
import chylex.hee.system.util.DragonUtil;
import cpw.mods.fml.common.Loader;

public final class ModIntegrationManager{
	public static final void integrateMods(){
		Class[] handlerClasses = new Class[]{
			NotEnoughItemsIntegration.class,
			ThaumcraftIntegration.class,
			DartCraftIntegration.class
		};
		
		for(Class<? extends IIntegrationHandler> cls:handlerClasses){
			try{
				IIntegrationHandler handler = cls.newInstance();
				if (Loader.isModLoaded(handler.getModId()))handler.integrate();
			}catch(Throwable e){
				DragonUtil.warning("Error with mod integration: %0%",cls.getSimpleName());
			}
		}
		
		/*try{
			for(ClassInfo clsInfo:ClassPath.from(ModIntegrationManager.class.getClassLoader()).getTopLevelClasses("chylex.hee.system.integration.handlers")){
				Class cls = clsInfo.load();
				
				try{
					IIntegrationHandler handler = (IIntegrationHandler)cls.newInstance();
					if (Loader.isModLoaded(handler.getModId()))handler.integrate();
				}catch(Throwable e){
					DragonUtil.warning("Error with mod integration: %0%",cls.getSimpleName());
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			DragonUtil.warning("Error setting up mod integration.");
		}*/
	}
}
