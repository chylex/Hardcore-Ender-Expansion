package chylex.hee.api.message.element;
import net.minecraft.item.Item;
import chylex.hee.api.message.MessageRunner;
import chylex.hee.api.message.element.base.Optional;
import chylex.hee.api.message.element.base.PreconditionComposite;
import chylex.hee.system.util.ItemDamagePair;

public class ItemDamagePairValue extends PreconditionComposite<ItemDamagePair>{
	public static final ItemDamagePairValue any(){
		return new ItemDamagePairValue();
	}
	
	private ItemDamagePairValue(){
		addCondition("id",ItemStackValue.itemString);
		addCondition("damage",Optional.of(IntValue.any(),-1));
	}
	
	@Override
	public ItemDamagePair getValue(MessageRunner runner){
		Item item = ItemStackValue.getItemFromString(runner.getString("id"));
		if (item == null)throw new IllegalStateException("Failed constructing an ItemDamagePair from IMC, item is null.");
		
		int damage = runner.getInt("damage");
		return new ItemDamagePair(item,damage < 0 ? -1 : damage);
	}
}
