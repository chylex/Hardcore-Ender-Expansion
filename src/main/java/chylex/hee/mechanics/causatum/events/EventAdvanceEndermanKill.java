package chylex.hee.mechanics.causatum.events;
import net.minecraft.entity.player.EntityPlayerMP;

public class EventAdvanceEndermanKill extends CausatumEventInstance{
	EventAdvanceEndermanKill(EventTypes eventType, EntityPlayerMP player){
		super(eventType,player);
	}

	@Override
	protected void onUpdate(){}

	@Override
	protected void onPlayerDisconnected(){}

	@Override
	protected void onPlayerDied(){}

	@Override
	protected void onPlayerChangedDimension(){}
}
