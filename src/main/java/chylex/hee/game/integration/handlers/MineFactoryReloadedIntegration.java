package chylex.hee.game.integration.handlers;
import java.lang.reflect.Method;
import chylex.hee.entity.mob.EntityMobBabyEnderman;
import chylex.hee.game.integration.IIntegrationHandler;

public class MineFactoryReloadedIntegration implements IIntegrationHandler{
	@Override
	public void integrate() throws Throwable{
		Method blacklist = Class.forName("powercrystals.minefactoryreloaded.MFRRegistry").getMethod("registerSafariNetBlacklist",Class.class);
		blacklist.invoke(null,EntityMobBabyEnderman.class);
	}
}
