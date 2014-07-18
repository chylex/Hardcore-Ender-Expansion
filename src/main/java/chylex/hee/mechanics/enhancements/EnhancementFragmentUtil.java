package chylex.hee.mechanics.enhancements;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.knowledge.data.KnowledgeRegistration;
import chylex.hee.mechanics.knowledge.data.UnlockResult;
import chylex.hee.mechanics.knowledge.fragment.EnhancementKnowledgeFragment;
import chylex.hee.mechanics.knowledge.fragment.KnowledgeFragment;

public class EnhancementFragmentUtil{
	public static KnowledgeFragment[] getEnhancementFragments(Class<? extends Enum> enhancementEnum){
		Enum[] values = enhancementEnum.getEnumConstants();
		KnowledgeFragment[] fragments = new KnowledgeFragment[values.length];
		for(int a = 0; a < values.length; a++)fragments[a] = new EnhancementKnowledgeFragment(a).setEnhancement((IEnhancementEnum)values[a]);
		return fragments;
	}
	
	public static Set<Short> getUnlockedEnhancements(KnowledgeRegistration registration, EntityPlayer player){
		Set<Short> unlockedEnhancements = new HashSet<Short>();
		
		InventoryPlayer inv = player.inventory;
		
		for(int a = 0; a < inv.mainInventory.length; a++){
			ItemStack is = inv.mainInventory[a];
			if (is == null || is.getItem() != ItemList.ender_compendium || is.stackTagCompound == null)continue;
			
			for(int id:registration.fragmentSet.getUnlockedFragments(is))unlockedEnhancements.add((short)id);
			break;
		}
		
		return unlockedEnhancements;
	}
	
	public static boolean unlockEnhancement(KnowledgeRegistration registration, Enum enhancement, EntityPlayer player){
		InventoryPlayer inv = player.inventory;
		
		for(int a = 0; a < inv.mainInventory.length; a++){
			ItemStack is = inv.mainInventory[a];
			if (is == null || is.getItem() != ItemList.ender_compendium || is.stackTagCompound == null)continue;
			
			return registration.tryUnlockFragment(player,1F,new short[]{ (short)enhancement.ordinal() }) == UnlockResult.SUCCESSFUL;
		}
		
		return false;
	}
}
