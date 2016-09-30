package chylex.hee.api.message.element;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.api.message.MessageRunner;
import chylex.hee.api.message.element.base.Optional;
import chylex.hee.api.message.element.base.PreconditionComposite;
import chylex.hee.system.util.ItemPattern;

public class ItemPatternValue extends PreconditionComposite<ItemPattern>{
	public static final ItemPatternValue any(){
		return new ItemPatternValue();
	}
	
	private ItemPatternValue(){
		addCondition("id", StringValue.any());
		addCondition("damage", Optional.of(IntArrayValue.condition(IntValue.range(0, Short.MAX_VALUE)), ArrayUtils.EMPTY_INT_ARRAY));
		addCondition("tag", Optional.of(NbtValue.any(), new NBTTagCompound()));
	}
	
	@Override
	public ItemPattern getValue(MessageRunner runner){
		Pair<String, String> name = ItemStackValue.parseItemName(runner.getString("id"));
		return new ItemPattern().setItemName(name.getLeft(), name.getRight()).setDamageValues(runner.getIntArray("damage")).setNBT(runner.<NBTTagCompound>getValue("tag"));
	}
}
