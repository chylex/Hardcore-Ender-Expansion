package chylex.hee.api.message.handlers;
import java.util.Iterator;
import net.minecraft.item.ItemStack;
import chylex.hee.api.message.MessageHandler;
import chylex.hee.api.message.MessageRunner;
import chylex.hee.api.message.element.IntValue;
import chylex.hee.api.message.element.ItemPatternValue;
import chylex.hee.api.message.element.ItemStackValue;
import chylex.hee.api.message.element.StringValue;
import chylex.hee.api.message.utils.MessageLogger;
import chylex.hee.api.message.utils.RunEvent;
import chylex.hee.mechanics.essence.handler.DragonEssenceHandler;
import chylex.hee.mechanics.essence.handler.dragon.AltarItemRecipe;
import chylex.hee.system.util.ItemPattern;

public final class ImcEssenceHandlers extends ImcHandler{
	private static final MessageHandler dragonEssenceAdd = new MessageHandler(){
		@Override
		public void call(MessageRunner runner){
			ItemStack input = runner.<ItemStack>getValue("input");
			
			for(AltarItemRecipe recipe:DragonEssenceHandler.recipes){
				if (ItemStack.areItemStacksEqual(recipe.input,input) && ItemStack.areItemStackTagsEqual(recipe.input,input)){
					MessageLogger.logFail("Duplicate input item, ignoring.");
					return;
				}
			}
			
			DragonEssenceHandler.recipes.add(new AltarItemRecipe(input,runner.<ItemStack>getValue("output"),runner.getInt("cost")));
			MessageLogger.logOk("Added 1 recipe.");
		}
	};

	private static final MessageHandler dragonEssenceRemove = new MessageHandler(){
		@Override
		public void call(MessageRunner runner){
			boolean input = runner.getString("type").equals("input");
			ItemPattern pattern = runner.<ItemPattern>getValue("search");
			int limit = runner.getInt("limit");
			
			int size = DragonEssenceHandler.recipes.size();
			
			for(Iterator<AltarItemRecipe> iter = DragonEssenceHandler.recipes.iterator(); iter.hasNext();){
				AltarItemRecipe recipe = iter.next();
				
				if (pattern.matches(input ? recipe.input : recipe.output)){
					iter.remove();
					if (limit > 0 && --limit == 0)break;
				}
			}
			
			size = size-DragonEssenceHandler.recipes.size();
			
			if (size == 0)MessageLogger.logWarn("Did not find any items to remove.");
			else MessageLogger.logOk("Removed $0 item(s).",size);
		}
	};
	
	@Override
	public void register(){
		register("HEE:DragonEssence:AddRecipe",dragonEssenceAdd,RunEvent.POSTINIT)
		.addProp("input",ItemStackValue.any())
		.addProp("output",ItemStackValue.any())
		.addProp("cost",IntValue.positive());
		
		register("HEE:DragonEssence:RemoveRecipe",dragonEssenceRemove,RunEvent.POSTINIT)
		.addProp("type",StringValue.one("input","output"))
		.addProp("search",ItemPatternValue.any())
		.addProp("limit",IntValue.positiveOrZero());
	}
}
