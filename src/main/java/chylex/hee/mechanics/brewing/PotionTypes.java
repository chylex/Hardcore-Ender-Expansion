package chylex.hee.mechanics.brewing;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFishFood.FishType;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.commons.lang3.ArrayUtils;
import chylex.hee.item.ItemAbstractPotion;
import chylex.hee.item.ItemList;
import chylex.hee.system.util.ItemDamagePair;
import chylex.hee.system.util.ItemUtil;

public class PotionTypes{
	public static final List<AbstractPotionData> potionData = Arrays.asList(
		/*  0 */ new EmptyPotion(null,0,16),
		/*  1 */ new InstantPotion(Potion.heal,16,8197,3,4),
		/*  2 */ new InstantPotion(Potion.harm,8197,8204,3,4),
		/*  3 */ new TimedPotion(Potion.moveSpeed,16,8194,3,4,60,720),
		/*  4 */ new TimedPotion(Potion.moveSlowdown,8194,8202,3,4,30,480,30),
		/*  5 */ new TimedPotion(Potion.damageBoost,16,8201,3,4,60,720),
		/*  6 */ new TimedPotion(Potion.weakness,8195,8200,3,4,30,480,30),
		/*  7 */ new TimedPotion(Potion.nightVision,16,8198,1,1,30,600),
		/*  8 */ new TimedPotion(Potion.invisibility,8198,8206,1,1,30,600),
		/*  9 */ new TimedPotion(Potion.regeneration,16,8193,3,4,15,120,15),
		/* 10 */ new TimedPotion(Potion.poison,16,8196,3,4,6,48,6),
		/* 11 */ new TimedPotion(Potion.fireResistance,16,8195,1,1,60,600),
		/* 12 */ new TimedPotion(Potion.waterBreathing,16,000,1,1,60,720,60),
		// NEW //
		/* 13 */ new TimedPotion(Potion.blindness,16,8197,1,1,6,48,6),
		/* 14 */ new TimedPotion(Potion.jump,16,8192,3,4,60,600),
		/* 15 */ new TimedPotion(Potion.confusion,8251,8253,1,1,6,48,6)
	);
	
	private static final Map<Item,Item> customPotions = new HashMap<>();
	private static final Map<ItemDamagePair,byte[]> itemToIndex = new HashMap<>();
	
	private static void mapItemToIndex(Item item, int...indexes){
		mapItemToIndex(item,(short)0,indexes);
	}
	
	private static void mapItemToIndex(Item item, Short damage, int...indexes){
		byte[] byteIndexes = new byte[indexes.length];
		for(int a = 0; a < indexes.length; a++)byteIndexes[a] = (byte)indexes[a];
		itemToIndex.put(new ItemDamagePair(item,damage),byteIndexes);
	}
	
	public static byte[] getItemIndexes(ItemStack is){
		for(Entry<ItemDamagePair,byte[]> entry:itemToIndex.entrySet()){
			ItemDamagePair item = entry.getKey();
			if (item.check(is))return entry.getValue();
		}
		
		return ArrayUtils.EMPTY_BYTE_ARRAY;
	}
	
	static{
		customPotions.put(ItemList.instability_orb,ItemList.potion_of_instability);
		customPotions.put(ItemList.ectoplasm,ItemList.potion_of_purity);
		customPotions.put(ItemList.silverfish_blood,ItemList.infestation_remedy);
		
		mapItemToIndex(Items.nether_wart,0);
		mapItemToIndex(Items.speckled_melon,1);
		mapItemToIndex(Items.sugar,3);
		mapItemToIndex(Items.blaze_powder,5);
		mapItemToIndex(Items.golden_carrot,7);
		mapItemToIndex(Items.ghast_tear,9);
		mapItemToIndex(Items.spider_eye,10);
		mapItemToIndex(Items.magma_cream,11);
		mapItemToIndex(Items.fish,(short)FishType.PUFFERFISH.func_150976_a(),new int[]{ 12 }); // OBFUSCATED get fish damage
		mapItemToIndex(Items.fermented_spider_eye,2,4,6,8/*,16*/);
		/*mapItemToIndex(Item.rottenFlesh,13);
		mapItemToIndex(Item.flint,14);
		mapItemToIndex(Item.feather,15);*/
	}
	
	public static boolean isSpecialIngredient(Item item){
		return customPotions.containsKey(item);
	}
	
	public static boolean isPotionItem(Item item){
		return item instanceof ItemPotion || item == Items.glass_bottle || customPotions.containsValue(item);
	}
	
	public static PotionEffect getEffectIfValid(ItemStack is){
		List list = Items.potionitem.getEffects(is);
		return list == null || list.size() != 1 ? null : (PotionEffect)list.get(0);
	}
	
	public static AbstractPotionData getPotionData(ItemStack is){
		for(AbstractPotionData data:potionData){
			if (data.damageValue == (is.getItemDamage()&~16384))return data;
		}
		
		return null;
	}
	
	public static int getRequiredPowder(Item ingredient, ItemStack is){
		if (is.getItemDamage() <= 16){
			if (is.getItemDamage() == 16 && customPotions.containsKey(ingredient))return 8;
			else if (ingredient == Items.gunpowder)return 3;
			else return 0;
		}
		
		PotionEffect eff = getEffectIfValid(is);
		if (eff == null)return 0;

		if (ingredient == Items.redstone){
			AbstractPotionData potionData = getPotionData(is);
			if (potionData instanceof TimedPotion)return ((TimedPotion)potionData).getDurationLevel(eff.getDuration())/2+1;
		}
		else if (ingredient == Items.glowstone_dust)return 2*(eff.getAmplifier()+1);
		else if (ingredient == Items.gunpowder)return 3;
		return 0;
	}
	
	public static boolean canBeApplied(ItemStack ingredient, ItemStack is, boolean enhanced){
		byte[] indexes = getItemIndexes(ingredient);
		
		if (indexes.length == 0){
			Item ingredientItem = ingredient.getItem();
			
			if (is.getItemDamage() <= 16){
				if (isSpecialIngredient(ingredientItem))return is.getItemDamage() == 16;
				else if (ingredientItem == Items.gunpowder && is.getItem() instanceof ItemAbstractPotion)return is.getItemDamage() == 0;
				else return false;
			}
			
			if (ingredientItem == Items.gunpowder){
				return !ItemPotion.isSplash(is.getItemDamage());
			}
			else if (ingredientItem == Items.glowstone_dust){
				AbstractPotionData data = getPotionData(is);
				return data != null && data.canIncreaseLevel(is,enhanced);
			}
			else if (ingredientItem == Items.redstone){
				AbstractPotionData data = getPotionData(is);
				return data instanceof TimedPotion && ((TimedPotion)data).canIncreaseDuration(is);
			}
			return false;
		}
		
		for(Byte b:indexes){
			AbstractPotionData data = potionData.get(b);
			if (data.requiredDamageValue == (is.getItemDamage()&~16384))return true;
		}
		
		return false;
	}
	
	public static ItemStack applyIngredientUnsafe(ItemStack ingredient, ItemStack is){
		Item ingredientItem = ingredient.getItem();
		
		if (isSpecialIngredient(ingredientItem) || (ingredientItem == Items.gunpowder && is.getItem() instanceof ItemAbstractPotion)){
			if (ingredientItem == Items.gunpowder)return new ItemStack(is.getItem(),1,1);
			else return new ItemStack(customPotions.get(ingredientItem));
		}
		
		byte[] indexes = getItemIndexes(ingredient);
		
		if (indexes.length == 0){
			PotionEffect eff = getEffectIfValid(is);
			if (eff == null)return is;
			
			if (ingredientItem == Items.gunpowder){
				setCustomPotionEffect(is,eff); // make sure splash doesn't change duration
				is.setItemDamage(is.getItemDamage()|16384);
				return is;
			}
			
			PotionEffect newEffect = null;
			
			if (ingredientItem == Items.glowstone_dust){
				newEffect = new PotionEffect(eff.getPotionID(),eff.getDuration(),eff.getAmplifier()+1,eff.getIsAmbient());
			}
			else if (ingredientItem == Items.redstone){
				TimedPotion data = (TimedPotion)getPotionData(is);
				newEffect = new PotionEffect(eff.getPotionID(),Math.min(eff.getDuration()+data.getDurationStep(),data.maxDuration),eff.getAmplifier(),eff.getIsAmbient());
			}
			
			if (newEffect != null)setCustomPotionEffect(is,newEffect);
			return is;
		}
		
		for(Byte b:indexes){
			AbstractPotionData data = potionData.get(b);
			if (data != null && data.requiredDamageValue == (is.getItemDamage()&~16384)){
				PotionEffect prevEffect = getEffectIfValid(is);
				
				ItemUtil.getTagRoot(is,false).removeTag("CustomPotionEffects");
				data.onFirstBrewingFinished(is);
				
				if (prevEffect != null){
					PotionEffect curEffect = getEffectIfValid(is);
					if (curEffect != null)setCustomPotionEffect(is,new PotionEffect(curEffect.getPotionID(),prevEffect.getDuration(),prevEffect.getAmplifier(),prevEffect.getIsAmbient()));
				}
				
				break;
			}
		}
		
		return is;
	}
	
	public static ItemStack setCustomPotionEffect(ItemStack is, PotionEffect effect){
		NBTTagList potionList = new NBTTagList();
		potionList.appendTag(effect.writeCustomPotionEffectToNBT(new NBTTagCompound()));
		ItemUtil.getTagRoot(is,true).setTag("CustomPotionEffects",potionList);
		return is;
	}
}
