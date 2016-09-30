package chylex.hee.api.message.handlers;
import chylex.hee.api.message.IMessageHandler;
import chylex.hee.api.message.element.StringValue;
import chylex.hee.api.message.utils.MessageLogger;
import chylex.hee.api.message.utils.RunEvent;
import chylex.hee.game.integration.ModIntegrationManager;

public class ImcSystemHandlers extends ImcHandler{
	private static final IMessageHandler disableIntegration = runner -> {
		if (ModIntegrationManager.blacklistMod(runner.getString("modid"))){
			MessageLogger.logOk("Mod integration was blacklisted.");
		}
		else{
			MessageLogger.logFail("The mod ID does not have any mod integration registered.");
		}
	};
	
	@Override
	public void register(){
		register("System:DisableIntegration", disableIntegration, RunEvent.POSTINIT)
		.addProp("modid", StringValue.any());
	}
}
