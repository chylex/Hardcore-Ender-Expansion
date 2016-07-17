package chylex.hee.system.test.list.mechanics;
import gnu.trove.map.hash.TIntIntHashMap;
import java.util.Map;
import java.util.Map.Entry;
import chylex.hee.mechanics.charms.CharmRecipe;
import chylex.hee.mechanics.charms.CharmType;
import chylex.hee.mechanics.charms.RuneType;
import chylex.hee.system.test.Assert;
import chylex.hee.system.test.data.MethodType;
import chylex.hee.system.test.data.RunTime;
import chylex.hee.system.test.data.UnitTest;

public class CharmTests{
	@UnitTest(type = MethodType.TEST, runTime = RunTime.LOADCOMPLETE)
	public void testRecipeConflicts(){
		TIntIntHashMap hashes = new TIntIntHashMap(80,0.75F,-1,-1);
		
		for(CharmType type:CharmType.values()){
			for(CharmRecipe recipe:type.recipes){
				Map<RuneType,Byte> data = recipe.getRunes();
				int hash = 0, prevHash;
				
				for(Entry<RuneType,Byte> entry:data.entrySet()){
					int key = entry.getKey().ordinal(); // 0-6
					int amount = entry.getValue(); // 0-5
					
					hash |= amount << (key*3);
				}
				
				if ((prevHash = hashes.put(hash,recipe.id)) != -1){
					Assert.fail("Found a duplicate Charm recipe: "+prevHash+" and "+recipe.id);
				}
			}
		}
	}
}
