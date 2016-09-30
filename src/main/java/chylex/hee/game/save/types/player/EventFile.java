package chylex.hee.game.save.types.player;
import net.minecraft.entity.player.EntityPlayer;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.mechanics.compendium.content.KnowledgeFragment;
import chylex.hee.mechanics.compendium.events.CompendiumEvents;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C09SimpleEvent;
import chylex.hee.packets.client.C09SimpleEvent.EventType;
import chylex.hee.system.abstractions.nbt.NBTCompound;

public class EventFile extends PlayerFile{
	private int compendiumEndermanHint;
	
	public EventFile(String filename){
		super("events", filename);
	}
	
	public void onEndermanAvoid(EntityPlayer target){
		if (compendiumEndermanHint == -1)return;
		
		if (++compendiumEndermanHint == 3+target.getRNG().nextInt(2)){
			compendiumEndermanHint = -1;
			CompendiumEvents.getPlayerData(target).tryUnlockHintFragment(target, KnowledgeFragment.fromID(100));
			PacketPipeline.sendToPlayer(target, new C09SimpleEvent(EventType.TRIGGER_COMPENDIUM_HINT));
		}
		
		setModified();
	}

	@Override
	protected void onSave(NBTCompound nbt){
		nbt.setByte("compendiumHint1", (byte)compendiumEndermanHint);
	}

	@Override
	protected void onLoad(NBTCompound nbt){
		compendiumEndermanHint = nbt.getByte("compendiumHint1");
	}
}
