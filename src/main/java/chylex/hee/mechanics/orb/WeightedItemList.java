package chylex.hee.mechanics.orb;
import chylex.hee.system.collections.weight.WeightedList;

public class WeightedItemList extends WeightedList<WeightedItem>{
	@Override
	public void add(WeightedItem item){
		for(WeightedItem checkedItem:this){
			if (checkedItem.getItem() == item.getItem()){
				if (item.getWeight() > checkedItem.getWeight())checkedItem.setWeight(item.getWeight());
				checkedItem.combineDamageValues(item);
				setDirty();
				return;
			}
		}
		
		super.add(item);
	}
}
