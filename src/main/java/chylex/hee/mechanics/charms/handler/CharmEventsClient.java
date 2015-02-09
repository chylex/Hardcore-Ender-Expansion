package chylex.hee.mechanics.charms.handler;
import gnu.trove.list.array.TFloatArrayList;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.mechanics.charms.CharmPouchInfo;
import chylex.hee.mechanics.charms.CharmRecipe;
import chylex.hee.mechanics.charms.CharmType;

public final class CharmEventsClient{
	public static float[] getProp(String prop){
		CharmPouchInfo info = CharmPouchHandlerClient.getActivePouch();
		if (info == null)return ArrayUtils.EMPTY_FLOAT_ARRAY;
		
		TFloatArrayList values = new TFloatArrayList(5);
		for(Pair<CharmType,CharmRecipe> entry:info.charms){
			float value = entry.getRight().getProp(prop);
			if (value != -1)values.add(value);
		}
		
		return values.toArray();
	}
	
	public static float getPropPercentIncrease(String prop, float baseValue){
		float finalValue = 0;
		for(float val:getProp(prop))finalValue += (val*baseValue)-baseValue;
		return finalValue;
	}
	
	CharmEventsClient(){}
	
	/**
	 * HASTE (CLIENT)
	 */
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onBreakSpeed(BreakSpeed e){
		// HASTE
		if (e.entity.worldObj.isRemote)e.newSpeed += getPropPercentIncrease("breakspd",e.originalSpeed);
	}
}
