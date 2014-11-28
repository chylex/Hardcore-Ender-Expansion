package chylex.hee.system.integration.handlers;
import chylex.hee.entity.mob.EntityMobHomelandEnderman;
import chylex.hee.system.integration.IIntegrationHandler;

public class ArsMagicaFixIntegration implements IIntegrationHandler{
	@Override
	public String getModId(){
		return "arsmagica2";
	}

	@Override
	public void integrate(){
		EntityMobHomelandEnderman.ignoreUpdateEvent = true;
	}
}
