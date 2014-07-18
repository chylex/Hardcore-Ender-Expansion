package chylex.hee.mechanics.enhancements;
import java.util.Iterator;
import com.google.common.collect.Iterators;

public final class SlotList implements Iterable{
	public enum SlotType{
		POWDER, INGREDIENT
	}
	
	public final SlotType[] slotTypes;
	public final int amountPowder;
	public final int amountIngredient;
	
	public SlotList(SlotType...slotTypes){
		this.slotTypes = slotTypes;
		
		int amtPowder = 0, amtIngredient = 0;
		for(SlotType type:slotTypes){
			if (type == SlotType.POWDER)++amtPowder;
			else if (type == SlotType.INGREDIENT)++amtIngredient;
		}
		
		this.amountPowder = amtPowder;
		this.amountIngredient = amtIngredient;
	}
	
	@Override
	public Iterator<SlotType> iterator(){
		return Iterators.forArray(slotTypes);
	}
}
