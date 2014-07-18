package chylex.hee.mechanics.enhancements;
import net.minecraft.item.Item;
import chylex.hee.mechanics.enhancements.SlotList.SlotType;
import chylex.hee.mechanics.knowledge.data.KnowledgeRegistration;

class EnhancementData{
	final Class<? extends Enum> clsEnum;
	final Enum[] valuesEnum;
	
	final Class<? extends IEnhancementEnum> clsInterface;
	final IEnhancementEnum[] valuesInterface;
	
	final KnowledgeRegistration knowledgeRegistration;
	final Item newItem;
	final SlotList slots;
	
	EnhancementData(Class<? extends Enum> cls, KnowledgeRegistration knowledgeRegistration, Item newItem, SlotType...slotTypes){
		this.clsEnum = cls;
		this.valuesEnum = cls.getEnumConstants();
		
		this.clsInterface = (Class<? extends IEnhancementEnum>)cls;
		this.valuesInterface = new IEnhancementEnum[valuesEnum.length];
		for(int a = 0; a < valuesEnum.length; a++)valuesInterface[a] = (IEnhancementEnum)valuesEnum[a];
		
		this.knowledgeRegistration = knowledgeRegistration;
		this.newItem = newItem;
		this.slots = new SlotList(slotTypes);
	}
}
