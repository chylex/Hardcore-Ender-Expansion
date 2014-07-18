package chylex.hee.mechanics.minions.properties;
import net.minecraft.item.ItemStack;
import chylex.hee.entity.mob.EntityMobMinion;
import chylex.hee.mechanics.minions.MinionData;
import chylex.hee.mechanics.minions.handlers.AbilityAttackMonsters;
import chylex.hee.mechanics.minions.handlers.AbilityMineMinerals;
import chylex.hee.mechanics.minions.handlers.AbilityPickupItems;
import chylex.hee.mechanics.minions.handlers.AbilityPickupXp;
import chylex.hee.mechanics.minions.handlers.AbstractAbilityHandler;

public enum MinionAbilities implements IMinionInfusionHandler{
	 MINE_MINERALS(AbilityMineMinerals.class, 0, new ItemStack[]{
		 
	 }),
	 
	 PICKUP_ITEMS(AbilityPickupItems.class, 0, new ItemStack[]{
		 
	 }),
	 
	 PICKUP_XP(AbilityPickupXp.class, 0, new ItemStack[]{
		 
	 }),
	 
	 ATTACK_MONSTERS(AbilityAttackMonsters.class, 0, new ItemStack[]{
		 
	 });
	
	private final Class<? extends AbstractAbilityHandler> abilityHandler;
	private final byte cost;
	private final ItemStack[] recipe;
	
	MinionAbilities(Class<? extends AbstractAbilityHandler> abilityHandler, int cost, ItemStack[] recipe){
		this.abilityHandler = abilityHandler;
		this.cost = (byte)cost;
		this.recipe = recipe;
	}
	
	public AbstractAbilityHandler createHandler(EntityMobMinion minion){
		if (abilityHandler == null || minion == null)return null;
		
		try{
			return abilityHandler.getConstructor(EntityMobMinion.class).newInstance(minion);
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public boolean canApply(MinionData data){
		return data.getAbilitiesLeft() > 0 && !data.hasAbility(this);
	}
	
	@Override
	public void apply(MinionData data){
		data.addAbility(this);
	}
	
	@Override
	public int getEssenceCost(){
		return cost;
	}

	@Override
	public ItemStack[] getRecipe(){
		return recipe;
	}
}
