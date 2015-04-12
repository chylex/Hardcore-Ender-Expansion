package chylex.hee.api.message.handlers;
import java.util.Iterator;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.api.message.MessageHandler;
import chylex.hee.api.message.MessageRunner;
import chylex.hee.api.message.element.ItemPatternValue;
import chylex.hee.api.message.utils.MessageLogger;
import chylex.hee.api.message.utils.RunEvent;
import chylex.hee.mechanics.orb.OrbAcquirableItems;
import chylex.hee.mechanics.orb.WeightedItem;
import chylex.hee.mechanics.orb.WeightedItemList;
import chylex.hee.system.util.ItemPattern;

public class ImcOrbHandlers extends ImcHandler{
	private static final MessageHandler itemBlacklist = new MessageHandler(){
		@Override
		public void call(MessageRunner runner){
			ItemPattern pattern = runner.<ItemPattern>getValue("pattern");
			
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
		}
	};
	
	@Override
	public void register(){
		register("HEE:Orb:ItemBlacklist",itemBlacklist,RunEvent.LOADCOMPLETE)
		.addProp("pattern",ItemPatternValue.any());
	}
}
