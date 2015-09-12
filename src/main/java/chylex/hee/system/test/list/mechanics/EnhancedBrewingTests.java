package chylex.hee.system.test.list.mechanics;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import org.apache.commons.lang3.tuple.Pair;
import chylex.hee.init.ItemList;
import chylex.hee.mechanics.brewing.PotionTypes;
import chylex.hee.system.test.Assert;
import chylex.hee.system.test.UnitTest;

public class EnhancedBrewingTests{
	private final List<Pair<Item,PotionEffect>> stages = new ArrayList<>();
	private Potion[] potionList; // unused
	private Item[] specialIngredients;
	private ItemStack isWater;
	private ItemStack isAwkward;
	private ItemStack isVanillaHeal;
	private ItemStack isVanillaSpeed;
	private ItemStack isCustomHeal;
	private ItemStack isCustomSpeed;
	private ItemStack isCustomSpeedT2;
	private ItemStack isCustomSpeedL2;
	private ItemStack isCustomSpeedL3;
	
	{
		potionList = new Potion[]{
			Potion.heal,
			Potion.harm,
			Potion.moveSpeed,
			Potion.moveSlowdown,
			Potion.damageBoost,
			Potion.weakness,
			Potion.nightVision,
			Potion.invisibility,
			Potion.regeneration,
			Potion.poison,
			Potion.fireResistance,
			Potion.waterBreathing
		};
		
		specialIngredients = new Item[]{
			ItemList.instability_orb,
			ItemList.ectoplasm,
			ItemList.silverfish_blood
		};
		
		isWater = new ItemStack(Items.potionitem);
		isAwkward = new ItemStack(Items.potionitem,1,16);
		isVanillaHeal = new ItemStack(Items.potionitem,1,8197);
		isVanillaSpeed = new ItemStack(Items.potionitem,1,8194);
		isCustomHeal = PotionTypes.setCustomPotionEffect(isVanillaHeal.copy(),new PotionEffect(Potion.heal.id,1,0));
		isCustomSpeed = PotionTypes.setCustomPotionEffect(isVanillaSpeed.copy(),new PotionEffect(Potion.moveSpeed.id,1200,0)); // 1:00
		isCustomSpeedT2 = PotionTypes.setCustomPotionEffect(isVanillaSpeed.copy(),new PotionEffect(Potion.moveSpeed.id,1200,1)); // 1:00
		isCustomSpeedL2 = PotionTypes.setCustomPotionEffect(isVanillaSpeed.copy(),new PotionEffect(Potion.moveSpeed.id,2700,0)); // 2:15
		isCustomSpeedL3 = PotionTypes.setCustomPotionEffect(isVanillaSpeed.copy(),new PotionEffect(Potion.moveSpeed.id,4200,0)); // 3:30
	}
	
	@UnitTest
	public void testValidation(){
		for(Item item:specialIngredients){
			Assert.state(PotionTypes.isSpecialIngredient(item),"Unexpected state, invalid special ingredient: "+item.getUnlocalizedName());
		}

		Assert.state(PotionTypes.isPotionItem(Items.glass_bottle),"Unexpected state, invalid potion item: "+Items.glass_bottle.getUnlocalizedName());
		Assert.state(PotionTypes.isPotionItem(Items.potionitem),"Unexpected state, invalid potion item: "+Items.potionitem.getUnlocalizedName());
	}
	
	@UnitTest
	public void testPotionData(){
		Assert.equal(PotionTypes.getPotionData(isAwkward).getPotionType(),null,"Unexpected potion data, expected $2, got $1.");
		Assert.equal(PotionTypes.getPotionData(isVanillaHeal).getPotionType(),Potion.heal,"Unexpected potion data, expected $2, got $1.");
		Assert.equal(PotionTypes.getPotionData(isCustomHeal).getPotionType(),Potion.heal,"Unexpected potion data, expected $2, got $1.");
		Assert.equal(PotionTypes.getPotionData(isVanillaSpeed).getPotionType(),Potion.moveSpeed,"Unexpected potion data, expected $2, got $1.");
		Assert.equal(PotionTypes.getPotionData(isCustomSpeed).getPotionType(),Potion.moveSpeed,"Unexpected potion data, expected $2, got $1.");
	}
	
	@UnitTest
	public void testPotionEffects(){
		Assert.equal(PotionTypes.getEffectIfValid(isAwkward),null,"Unexpected potion effect, expected $2, got $1.");
		Assert.equal(PotionTypes.getEffectIfValid(isVanillaHeal),new PotionEffect(Potion.heal.id,1,0),"Unexpected potion effect, expected $2, got $1.");
		Assert.equal(PotionTypes.getEffectIfValid(isCustomHeal),new PotionEffect(Potion.heal.id,1,0),"Unexpected potion effect, expected $2, got $1.");
		Assert.equal(PotionTypes.getEffectIfValid(isCustomSpeed),new PotionEffect(Potion.moveSpeed.id,1200,0),"Unexpected potion effect, expected $2, got $1.");
		Assert.equal(PotionTypes.getEffectIfValid(isCustomSpeedT2),new PotionEffect(Potion.moveSpeed.id,1200,1),"Unexpected potion effect, expected $2, got $1.");
		Assert.equal(PotionTypes.getEffectIfValid(isCustomSpeedL2),new PotionEffect(Potion.moveSpeed.id,2700,0),"Unexpected potion effect, expected $2, got $1.");
	}
	
	@UnitTest
	public void testRequiredPowder(){
		Assert.equal(PotionTypes.getRequiredPowder(Items.nether_wart,isWater),0,"Unexpected powder requirement, expected $2, got $1.");
		Assert.equal(PotionTypes.getRequiredPowder(ItemList.instability_orb,isAwkward),8,"Unexpected powder requirement, expected $2, got $1.");
		Assert.equal(PotionTypes.getRequiredPowder(Items.glowstone_dust,isCustomHeal),2,"Unexpected powder requirement, expected $2, got $1.");
		Assert.equal(PotionTypes.getRequiredPowder(Items.glowstone_dust,isCustomSpeed),2,"Unexpected powder requirement, expected $2, got $1.");
		Assert.equal(PotionTypes.getRequiredPowder(Items.glowstone_dust,isCustomSpeedT2),4,"Unexpected powder requirement, expected $2, got $1.");
		Assert.equal(PotionTypes.getRequiredPowder(Items.redstone,isCustomSpeed),1,"Unexpected powder requirement, expected $2, got $1.");
		Assert.equal(PotionTypes.getRequiredPowder(Items.redstone,isCustomSpeedL2),1,"Unexpected powder requirement, expected $2, got $1.");
		Assert.equal(PotionTypes.getRequiredPowder(Items.redstone,isCustomSpeedL3),2,"Unexpected powder requirement, expected $2, got $1.");
		Assert.equal(PotionTypes.getRequiredPowder(Items.gunpowder,isCustomSpeed),3,"Unexpected powder requirement, expected $2, got $1.");
	}
	
	@UnitTest
	public void testBrewingHeal2(){
		stages.clear();
		stages.add(Pair.of(Items.nether_wart,(PotionEffect)null));
		stages.add(Pair.of(Items.speckled_melon,new PotionEffect(Potion.heal.id,1,0)));
		stages.add(Pair.of(Items.glowstone_dust,new PotionEffect(Potion.heal.id,1,1)));
		runBrewingTest(isWater.copy(),false,false);
	}
	
	@UnitTest
	public void testBrewingHeal4Fail(){
		stages.clear();
		stages.add(Pair.of(Items.nether_wart,(PotionEffect)null));
		stages.add(Pair.of(Items.speckled_melon,new PotionEffect(Potion.heal.id,1,0)));
		stages.add(Pair.of(Items.glowstone_dust,new PotionEffect(Potion.heal.id,1,1)));
		stages.add(Pair.of(Items.glowstone_dust,new PotionEffect(Potion.heal.id,1,2)));
		stages.add(Pair.of(Items.glowstone_dust,new PotionEffect(Potion.heal.id,1,3)));
		runBrewingTest(isWater.copy(),false,true);
	}
	
	@UnitTest
	public void testBrewingHeal4Enhanced(){
		stages.clear();
		stages.add(Pair.of(Items.nether_wart,(PotionEffect)null));
		stages.add(Pair.of(Items.speckled_melon,new PotionEffect(Potion.heal.id,1,0)));
		stages.add(Pair.of(Items.glowstone_dust,new PotionEffect(Potion.heal.id,1,1)));
		stages.add(Pair.of(Items.glowstone_dust,new PotionEffect(Potion.heal.id,1,2)));
		stages.add(Pair.of(Items.glowstone_dust,new PotionEffect(Potion.heal.id,1,3)));
		runBrewingTest(isWater.copy(),true,false);
	}
	
	@UnitTest
	public void testBrewingSpeedLong(){
		stages.clear();
		stages.add(Pair.of(Items.nether_wart,(PotionEffect)null));
		stages.add(Pair.of(Items.sugar,new PotionEffect(Potion.moveSpeed.id,1200,0)));
		stages.add(Pair.of(Items.redstone,new PotionEffect(Potion.moveSpeed.id,2700,0)));
		stages.add(Pair.of(Items.redstone,new PotionEffect(Potion.moveSpeed.id,4200,0)));
		stages.add(Pair.of(Items.redstone,new PotionEffect(Potion.moveSpeed.id,5700,0)));
		stages.add(Pair.of(Items.redstone,new PotionEffect(Potion.moveSpeed.id,7200,0)));
		stages.add(Pair.of(Items.redstone,new PotionEffect(Potion.moveSpeed.id,8700,0)));
		stages.add(Pair.of(Items.redstone,new PotionEffect(Potion.moveSpeed.id,10200,0)));
		stages.add(Pair.of(Items.redstone,new PotionEffect(Potion.moveSpeed.id,11700,0)));
		stages.add(Pair.of(Items.redstone,new PotionEffect(Potion.moveSpeed.id,13200,0)));
		stages.add(Pair.of(Items.redstone,new PotionEffect(Potion.moveSpeed.id,14400,0))); // limit
		runBrewingTest(isWater.copy(),false,false);
	}
	
	@UnitTest
	public void testBrewingSpeedLongOverFail(){
		ItemStack startIS = PotionTypes.setCustomPotionEffect(isCustomSpeed.copy(),new PotionEffect(Potion.moveSpeed.id,11700,0));
		stages.clear();
		stages.add(Pair.of(Items.redstone,new PotionEffect(Potion.moveSpeed.id,13200,0)));
		stages.add(Pair.of(Items.redstone,new PotionEffect(Potion.moveSpeed.id,14400,0))); // limit
		stages.add(Pair.of(Items.redstone,new PotionEffect(Potion.moveSpeed.id,14401,0)));
		runBrewingTest(startIS,false,true);
	}
	
	private void runBrewingTest(ItemStack is, boolean enhanced, boolean shouldFail){
		for(Pair<Item,PotionEffect> stage:stages){
			ItemStack ingredient = new ItemStack(stage.getLeft());
			
			if (!PotionTypes.canBeApplied(ingredient,is,enhanced)){
				if (shouldFail)return;
				else Assert.fail("Unexpected brewing state, cannot apply ingredient "+stage.getLeft().getUnlocalizedName()+" to "+is+".");
			}
			
			is = PotionTypes.applyIngredientUnsafe(ingredient,is);
			Assert.equal(PotionTypes.getEffectIfValid(is),stage.getRight(),"Unexpected brewing state effect, expected $2, got $1.");
		}
		
		if (shouldFail)Assert.fail("Brewing was supposed to fail, but proceeded to get "+is+".");
	}
}
