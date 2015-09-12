package chylex.hee.system.test.list.system;
import java.util.Random;
import chylex.hee.system.collections.CustomArrayList;
import chylex.hee.system.collections.WeightedList;
import chylex.hee.system.collections.weight.ObjectWeightPair;
import chylex.hee.system.test.Assert;
import chylex.hee.system.test.UnitTest;

public class CollectionTests{
	@UnitTest
	public void testCustomArrayList(){
		CustomArrayList<String> customArrayList = new CustomArrayList<>();
		customArrayList.add("Entry 1");
		customArrayList.add("Entry 2");
		customArrayList.addAll(new String[]{ "Entry 3", "Entry 4" });
		
		Assert.equal(customArrayList.size(),4);
		Assert.equal(customArrayList.get(2),"Entry 3");
	}
	
	@UnitTest
	public void testWeightedList(){
		WeightedList<ObjectWeightPair<String>> list = new WeightedList<>();
		
		Assert.isNull(list.getRandomItem(new Random()));
		Assert.isFalse(list.tryGetRandomItem(new Random()).isPresent());
		
		list.add(ObjectWeightPair.of("Herp",Integer.MAX_VALUE-1));
		list.add(ObjectWeightPair.of("Derp",1));
		
		Assert.equal(list.size(),2);
		Assert.equal(list.getRandomItem(new Random()).getObject(),"Herp");
	}
}
