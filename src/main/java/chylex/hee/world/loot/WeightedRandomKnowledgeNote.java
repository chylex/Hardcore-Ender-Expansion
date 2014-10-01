package chylex.hee.world.loot;
import java.util.Random;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.WeightedRandomChestContent;
import chylex.hee.item.ItemKnowledgeNote;
import chylex.hee.item.ItemList;

public class WeightedRandomKnowledgeNote extends WeightedRandomChestContent{
	public WeightedRandomKnowledgeNote(int weight){
		super(new ItemStack(ItemList.knowledge_note),1,1,weight);
	}
	
	@Override
	protected ItemStack[] generateChestContent(Random rand, IInventory inv){
		return new ItemStack[]{ ItemKnowledgeNote.setRandomNote(new ItemStack(ItemList.knowledge_note),rand,4) };
	}
}
