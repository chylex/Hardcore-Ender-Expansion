package chylex.hee.api.message.handlers;
import java.util.Iterator;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.boss.IBossDisplayData;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.api.message.IMessageHandler;
import chylex.hee.api.message.element.ItemPatternValue;
import chylex.hee.api.message.element.SpawnEntryValue;
import chylex.hee.api.message.utils.MessageLogger;
import chylex.hee.api.message.utils.RunEvent;
import chylex.hee.mechanics.orb.OrbAcquirableItems;
import chylex.hee.mechanics.orb.OrbSpawnableMobs;
import chylex.hee.mechanics.orb.WeightedItem;
import chylex.hee.mechanics.orb.WeightedItemList;
import chylex.hee.system.util.ItemPattern;

public class ImcOrbHandlers extends ImcHandler{
	private static final IMessageHandler itemBlacklist = runner -> {
		ItemPattern pattern = runner.getValue("pattern");
		
		WeightedItemList list = OrbAcquirableItems.idList;
		int size = list.size(), damageRemoved = 0;
		
		for(Iterator<WeightedItem> iter = list.iterator(); iter.hasNext();){
			Pair<Integer,Boolean> info = iter.next().runBlacklistPattern(pattern);
			
			if (info.getRight())iter.remove();
			damageRemoved += info.getLeft();
		}
		
		size = size-list.size();
		
		if (size == 0 && damageRemoved == 0)MessageLogger.logWarn("Did not find any items to remove.");
		else if (size == 0)MessageLogger.logOk("Removed $0 damage value(s) from items.",damageRemoved);
		else MessageLogger.logOk("Removed $0 item(s) and $1 damage value(s) in total.",size,damageRemoved);
		
		if (list.size() == 0)MessageLogger.logWarn("No items left in the list, falling back to generic chest loot.");
	};
	
	private static final IMessageHandler mobAdd = runner -> {
		Class<?> cls = (Class<?>)EntityList.stringToClassMapping.get(runner.getString("id"));
		
		if (IBossDisplayData.class.isAssignableFrom(cls))MessageLogger.logFail("Cannot add boss mobs to the list.");
		else if (OrbSpawnableMobs.classList.add(cls))MessageLogger.logOk("Added 1 mob to the list.");
		else MessageLogger.logFail("The mob was already in the list.");
	};
	
	private static final IMessageHandler mobRemove = runner -> {
		if (OrbSpawnableMobs.classList.remove(EntityList.stringToClassMapping.get(runner.getString("id"))))MessageLogger.logOk("Removed 1 mob from the list.");
		else MessageLogger.logFail("The mob was not present in the list.");
	};
	
	@Override
	public void register(){
		register("HEE:Orb:ItemBlacklist",itemBlacklist,RunEvent.LOADCOMPLETE)
		.addProp("pattern",ItemPatternValue.any());
		
		register("HEE:Orb:MobAdd",mobAdd,RunEvent.LOADCOMPLETE)
		.addProp("id",SpawnEntryValue.livingMobString);
		
		register("HEE:Orb:MobRemove",mobRemove,RunEvent.LOADCOMPLETE)
		.addProp("id",SpawnEntryValue.livingMobString);
	}
}
