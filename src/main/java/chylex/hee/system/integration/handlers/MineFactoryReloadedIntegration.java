package chylex.hee.system.integration.handlers;
import java.lang.reflect.Method;
import chylex.hee.entity.mob.EntityMobBabyEnderman;
import chylex.hee.system.integration.IIntegrationHandler;
import chylex.hee.system.logging.Log;

public class MineFactoryReloadedIntegration implements IIntegrationHandler{
	@Override
	public String getModId(){
		return "MineFactoryReloaded";
	}

	@Override
	public void integrate(){ // TODO test
		try{
			Method blacklist = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry").getMethod("registerSafariNetBlacklist",Class.class);
			blacklist.invoke(null,EntityMobBabyEnderman.class);
		}catch(Throwable t){
			Log.throwable(t,"Unable to integrate with MineFactoryReloaded!");
		}
	}
}
