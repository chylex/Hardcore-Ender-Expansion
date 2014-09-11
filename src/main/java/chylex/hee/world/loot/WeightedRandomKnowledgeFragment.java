package chylex.hee.world.loot;
import java.util.Random;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import chylex.hee.item.ItemList;

public class WeightedRandomKnowledgeFragment extends WeightedRandomChestContent{
	public WeightedRandomKnowledgeFragment(int weight){
		super(new ItemStack(ItemList.knowledge_note),1,1,weight);
	}
	
	@Override
	protected ItemStack[] generateChestContent(Random rand, IInventory inv){
		ItemStack is = new ItemStack(ItemList.knowledge_note);
		// TODO random thingy for fragment item
		return new ItemStack[]{ is };
	}
}
