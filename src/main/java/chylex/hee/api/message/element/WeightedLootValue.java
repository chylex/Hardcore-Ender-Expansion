package chylex.hee.api.message.element;
import chylex.hee.api.message.MessageRunner;
import chylex.hee.api.message.element.base.Optional;
import chylex.hee.api.message.element.base.PreconditionComposite;
import chylex.hee.world.loot.LootItemStack;

public class WeightedLootValue extends PreconditionComposite<LootItemStack>{
	public static final WeightedLootValue any(){
		return new WeightedLootValue();
	}
	
	private WeightedLootValue(){
		addCondition("id",ItemStackValue.itemString);
		addCondition("damage",Optional.of(IntArrayValue.condition(IntValue.range(0,Short.MAX_VALUE),IntValue.range(1,2)),new int[]{ 0 }));
		addCondition("count",Optional.of(IntArrayValue.condition(IntValue.range(1,64),IntValue.range(1,2)),new int[]{ 1 }));
		addCondition("weight",IntValue.positive());
	}
	
	@Override
	public LootItemStack getValue(MessageRunner runner){
		LootItemStack item = new LootItemStack(ItemStackValue.getItemFromString(runner.getString("id")));
		if (item.getItem() == null)throw new IllegalStateException("Failed constructing a LootItemStack (WeightedItem) from IMC, item is null.");
		
		int[] damage = runner.getIntArray("damage");
		
		if (damage.length == 2)item.setDamage(damage[0],damage[1]);
		else item.setDamage(damage[0]);
		
		int[] count = runner.getIntArray("count");
		
		if (count.length == 2)item.setAmount(count[0],count[1]);
		else item.setAmount(count[0]);
		
		item.setWeight(runner.getInt("weight"));
		return item;
	}
}
