package chylex.hee.system.test.list;
import net.minecraft.init.Items;
import chylex.hee.api.HeeIMC;
import chylex.hee.mechanics.essence.handler.DragonEssenceHandler;
import chylex.hee.system.test.Assert;
import chylex.hee.system.test.data.MethodType;
import chylex.hee.system.test.data.RunTime;
import chylex.hee.system.test.data.UnitTest;

public class ImcTests{
	@UnitTest(type = MethodType.PREPARATION, runTime = RunTime.PREINIT)
	public void prepareImcs(){
		for(String msgs:new String[]{
			"HEE:DragonEssence:AddRecipe { 'input': { 'id': 'ghast_tear' }, 'output': { 'id': '~hee:spectral_tear' }, 'cost': 15 }",
			"HEE:DragonEssence:RemoveRecipe { 'type': 'input', 'search': { 'id': '~hee:*' }, 'limit': 1 }"
			// TODO
		})HeeIMC.acceptString("UnitTester",msgs.replace('\'','"'));
	}
	
	@UnitTest(type = MethodType.TEST, runTime = RunTime.LOADCOMPLETE)
	public void testImcsDragonEssence(){
		Assert.equal(DragonEssenceHandler.recipes.size(),3,"Unexpected list size, expected $2, got $1.");
		Assert.equal(DragonEssenceHandler.recipes.get(1).input.getItem(),Items.ender_eye,"Unexpected second entry, expected $2, got $1. Full list: "+DragonEssenceHandler.recipes);
		Assert.equal(DragonEssenceHandler.recipes.get(2).cost,15,"Unexpected recipe cost, expected $2, got $1.");
	}
}
