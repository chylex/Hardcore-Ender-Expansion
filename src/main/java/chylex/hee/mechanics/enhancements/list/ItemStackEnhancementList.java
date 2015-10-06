package chylex.hee.mechanics.enhancements.list;
import net.minecraft.item.ItemStack;
import chylex.hee.mechanics.enhancements.EnhancementRegistry;
import chylex.hee.system.util.ItemUtil;

public class ItemStackEnhancementList<T extends Enum<T>> extends EnhancementList<T>{
	private final ItemStack linkedIS;
	
	public ItemStackEnhancementList(Class<T> enumCls, ItemStack linkedIS){
		super(enumCls);
		this.linkedIS = linkedIS;
		
		String enhancementData = ItemUtil.getTagRoot(linkedIS,false).getString("enhancements2");
		if (!enhancementData.isEmpty())deserialize(enhancementData);
	}
	
	@Override
	public void set(T enhancement, int level){
		super.set(enhancement,level);
		ItemUtil.getTagRoot(linkedIS,true).setString("enhancements2",serialize());
		linkedIS.func_150996_a(EnhancementRegistry.getItemTransformation(linkedIS.getItem()));
	}
	
	@Override
	public void upgrade(T enhancement){
		super.upgrade(enhancement);
		ItemUtil.getTagRoot(linkedIS,true).setString("enhancements2",serialize());
		linkedIS.func_150996_a(EnhancementRegistry.getItemTransformation(linkedIS.getItem()));
	}
}
