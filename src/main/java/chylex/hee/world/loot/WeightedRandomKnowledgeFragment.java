package chylex.hee.world.loot;
import java.util.Random;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import chylex.hee.item.ItemKnowledgeFragment;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.knowledge.util.FragmentWeightLists.FragmentWeightList;

public class WeightedRandomKnowledgeFragment extends WeightedRandomChestContent{
	private final FragmentWeightList fragmentWeightList;
	
	public WeightedRandomKnowledgeFragment(int weight, FragmentWeightList fragmentWeightList){
		super(new ItemStack(ItemList.knowledge_fragment),1,1,weight);
		this.fragmentWeightList = fragmentWeightList;
	}
	
	@Override
	protected ItemStack[] generateChestContent(Random rand, IInventory inv){
		ItemStack is = new ItemStack(ItemList.knowledge_fragment);
		ItemKnowledgeFragment.setRandomRegistration(is,fragmentWeightList,rand);
		return new ItemStack[]{ is };
	}
}
