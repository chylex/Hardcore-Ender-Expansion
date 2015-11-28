package chylex.hee.api.message.handlers;
import chylex.hee.api.message.IMessageHandler;
import chylex.hee.api.message.element.StringValue;
import chylex.hee.api.message.utils.MessageLogger;
import chylex.hee.api.message.utils.RunEvent;
import chylex.hee.game.integration.ModIntegrationManager;

public class ImcSystemHandlers extends ImcHandler{
	private static final IMessageHandler disableIntegration = runner -> {
		if (ModIntegrationManager.blacklistedMods.add(runner.getString("modid")))MessageLogger.logOk("Added 1 mod ID to the list.");
		else MessageLogger.logFail("The mod ID was already in the list.");
	};
	
	@Override
	public void register(){
		register("HEE:System:DisableIntegration",disableIntegration,RunEvent.POSTINIT)
		.addProp("modid",StringValue.any());
	}
}
