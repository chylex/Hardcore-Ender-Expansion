package chylex.hee.mechanics.causatum.events;
import java.util.Random;
import java.util.function.BiFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayerMP;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.handlers.PlayerDataHandler;
import chylex.hee.game.save.types.player.CausatumFile;
import chylex.hee.mechanics.causatum.Causatum.Progress;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.world.util.Range;

public abstract class CausatumEventInstance{
	public enum EventTypes{
		STAGE_ADVANCE_TO_ENDERMAN_KILLED(EventAdvanceEndermanKill::new);
		
		public final short requiredLevel;
		private final Progress minProgress, maxProgress;
		private final BiFunction<EventTypes, EntityPlayerMP, CausatumEventInstance> eventConstructor;
		
		private EventTypes(BiFunction<EventTypes, EntityPlayerMP, CausatumEventInstance> eventConstructor){
			this.requiredLevel = -1;
			this.minProgress = this.maxProgress = null;
			this.eventConstructor = eventConstructor;
		}
		
		private EventTypes(int requiredLevel, Progress minProgress, Progress maxProgress, BiFunction<EventTypes, EntityPlayerMP, CausatumEventInstance> eventConstructor){
			this.requiredLevel = (short)requiredLevel;
			this.minProgress = minProgress;
			this.maxProgress = maxProgress;
			this.eventConstructor = eventConstructor;
		}
		
		public boolean isRandomEvent(){
			return requiredLevel >= 0;
		}
		
		public boolean canTrigger(Progress currentProgress, int level){
			return (minProgress == null || maxProgress == null || new Range(minProgress.ordinal(), maxProgress.ordinal()).in(currentProgress.ordinal())) && (requiredLevel == -1 || level >= requiredLevel);
		}
		
		public CausatumEventInstance createEvent(EntityPlayerMP player){
			return eventConstructor.apply(this, player);
		}
		
		public CausatumEventInstance createEvent(EntityPlayerMP player, Object[] params){
			CausatumEventInstance inst = createEvent(player);
			inst.onParams(params);
			return inst;
		}
	}
	
	public enum EventState{
		WAITING, STARTED, SATISFIED, FINISHED
	}
	
	private final EventTypes eventType;
	private final String playerID;
	private @Nullable EntityPlayerMP player;
	private @Nonnull EventState state = EventState.WAITING;
	
	protected Random rand;
	
	CausatumEventInstance(EventTypes eventType, @Nonnull EntityPlayerMP player){
		this.eventType = eventType;
		this.playerID = PlayerDataHandler.getID(player);
		this.player = player;
		this.rand = player.getRNG();
	}
	
	protected final boolean hasPlayer(){
		return player != null;
	}
	
	protected final @Nullable EntityPlayerMP getPlayer(){
		return player;
	}
	
	protected final void updateState(@Nonnull EventState state){
		if (state.ordinal() <= this.state.ordinal())return;
		
		this.state = state;
		if (state == EventState.SATISFIED)SaveData.player(playerID, CausatumFile.class).finishEvent(eventType);
	}
	
	public void updateEvent(){
		if (player != null){ // TODO test all
			if (!player.playerNetServerHandler.netManager.isChannelOpen()){
				onPlayerDisconnected();
				player = null;
			}
			else if (player.isDead){
				onPlayerDied();
				player = null;
			}
			else if (!EntitySelector.players(player.worldObj).contains(player)){
				onPlayerChangedDimension();
				player = null;
			}
		}
		
		if (player == null)player = EntitySelector.players().stream().filter(entity -> playerID.equals(PlayerDataHandler.getID(entity))).findAny().orElse(null);
		
		onUpdate();
	}
	
	public final EventState getState(){
		return state;
	}
	
	protected void onParams(Object...params){}
	
	protected abstract void onUpdate();
	protected abstract void onPlayerDisconnected();
	protected abstract void onPlayerDied();
	protected abstract void onPlayerChangedDimension();
}
