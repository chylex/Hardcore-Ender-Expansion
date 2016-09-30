package chylex.hee.mechanics.charms;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.item.ItemCharmPouch;

public final class CharmPouchInfo{
	public final long pouchID;
	public final List<Pair<CharmType, CharmRecipe>> charms = new ArrayList<>(5);
	private long prevUpdateTime;
	
	public CharmPouchInfo(ItemStack is){
		pouchID = ItemCharmPouch.getPouchID(is);
		
		for(ItemStack charmStack:ItemCharmPouch.getPouchCharms(is)){
			if (charmStack != null)charms.add(CharmType.getFromDamage(charmStack.getItemDamage()));
		}
	}
	
	public void update(World world){
		this.prevUpdateTime = world.getTotalWorldTime();
	}
	
	public boolean isIdle(World world){
		return world.getTotalWorldTime()-prevUpdateTime > 4;
	}
	
	@Override
	public boolean equals(Object o){
		if (o != null && o.getClass() == CharmPouchInfo.class)return ((CharmPouchInfo)o).pouchID == pouchID;
		else return false;
	}
	
	@Override
	public int hashCode(){
		return (int)(pouchID^(pouchID>>>32));
	}
}
