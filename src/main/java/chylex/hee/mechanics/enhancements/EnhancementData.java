package chylex.hee.mechanics.enhancements;
import net.minecraft.item.Item;
import chylex.hee.mechanics.enhancements.SlotList.SlotType;

class EnhancementData{
	final Class<? extends Enum> clsEnum;
	final Enum[] valuesEnum;
	
	final Class<? extends IEnhancementEnum> clsInterface;
	final IEnhancementEnum[] valuesInterface;
	
	final Item newItem;
	final SlotList slots;
	
	EnhancementData(Class<? extends Enum> cls, Item newItem, SlotType...slotTypes){
		this.clsEnum = cls;
		this.valuesEnum = cls.getEnumConstants();
		
		this.clsInterface = (Class<? extends IEnhancementEnum>)cls;
		this.valuesInterface = new IEnhancementEnum[valuesEnum.length];
		for(int a = 0; a < valuesEnum.length; a++)valuesInterface[a] = (IEnhancementEnum)valuesEnum[a];
		
		this.newItem = newItem;
		this.slots = new SlotList(slotTypes);
	}
}
