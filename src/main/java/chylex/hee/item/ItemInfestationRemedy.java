package chylex.hee.item;
import gnu.trove.iterator.TIntIterator;
import gnu.trove.set.hash.TIntHashSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import chylex.hee.mechanics.infestation.InfestationEvents;

public class ItemInfestationRemedy extends Item{
	@Override
	public int getMaxItemUseDuration(ItemStack is){
		return 32;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack is){
		return EnumAction.drink;
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack is, World world, EntityPlayer player){
		player.setItemInUse(is,getMaxItemUseDuration(is));
		return is;
	}
	
	@Override
	public ItemStack onEaten(ItemStack is, World world, EntityPlayer player){
		if (!player.capabilities.isCreativeMode)--is.stackSize;
		
		TIntHashSet toRemove = new TIntHashSet(5);
		Map<PotionEffect,Integer> newDurations = new HashMap<>();
		
		for(PotionEffect eff:(Collection<PotionEffect>)player.getActivePotionEffects()){
			if (eff.getIsAmbient() && InfestationEvents.isValidPotionEffect(eff.getPotionID())){
				int dur = eff.getDuration()-3000+itemRand.nextInt(600);
				
				if (dur <= 0)toRemove.add(eff.getPotionID());
				else newDurations.put(eff,dur);
			}
		}
		
		for(TIntIterator iter = toRemove.iterator(); iter.hasNext();)player.removePotionEffect(iter.next());
		for(Entry<PotionEffect,Integer> entry:newDurations.entrySet()){
			PotionEffect oldEff = entry.getKey();
			player.removePotionEffect(oldEff.getPotionID());
			player.addPotionEffect(new PotionEffect(oldEff.getPotionID(),entry.getValue(),oldEff.getAmplifier(),oldEff.getIsAmbient()));
		}
		
		if (is.stackSize <= 0)return new ItemStack(Items.glass_bottle);
		player.inventory.addItemStackToInventory(new ItemStack(Items.glass_bottle));
		return is;
	}
}
