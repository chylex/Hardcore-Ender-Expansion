package chylex.hee.system.integration.handlers;
import chylex.hee.system.integration.IIntegrationHandler;

public class NotEnoughItemsIntegration implements IIntegrationHandler{
	@Override
	public String getModId(){
		return "NotEnoughItems";
	}

	@Override
	public void integrate(){ // TODO do stuff
		//API.registerRecipeHandler(new PearlEnhancementsHandler());
		/*for(PearlEnhancements pearlType:PearlEnhancements.values()){
			GameRegistry.addRecipe(new FakeShapelessRecipe(pearlType.applyTo(new ItemStack(ItemList.enhanced_ender_pearl)),new ItemStack(Items.ender_pearl),new ItemStack(ItemList.end_powder),new ItemStack(pearlType.getCraftItem())));
		}
		
		for(GemEnhancements gemEnhancement:GemEnhancements.values()){
			GameRegistry.addRecipe(new FakeShapedRecipe(3,3,gemEnhancement.applyTo(new ItemStack(ItemList.transference_gem)),new ItemStack[]{
				new ItemStack(ItemList.end_powder), new ItemStack(ItemList.end_powder), new ItemStack(ItemList.end_powder),
				new ItemStack(ItemList.end_powder), new ItemStack(ItemList.transference_gem), new ItemStack(ItemList.end_powder),
				new ItemStack(ItemList.end_powder), gemEnhancement.itemSelector.getRepresentativeItem(), new ItemStack(ItemList.end_powder)
			}));
		}
		
		for(SoulCharmEnhancements soulCharmEnhancement:SoulCharmEnhancements.values()){
			ItemStack soulCharm = new ItemStack(BlockList.soul_charm),itemToShow = soulCharmEnhancement.itemSelector.getRepresentativeItem();
			soulCharmEnhancement.setLevel(soulCharm,(byte)-1);
			
			GameRegistry.addRecipe(new FakeShapedRecipe(3,3,soulCharm,new ItemStack[]{
				new ItemStack(ItemList.end_powder), itemToShow, new ItemStack(ItemList.end_powder),
				itemToShow, new ItemStack(BlockList.soul_charm), itemToShow,
				new ItemStack(ItemList.end_powder), itemToShow, new ItemStack(ItemList.end_powder)
			}));
		}*/
	}
}
