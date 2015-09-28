package chylex.hee.system.test.list.old;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedSet;
import chylex.hee.system.collections.CustomArrayList;
import chylex.hee.system.test.Assert;
import chylex.hee.system.test.UnitTest;
import chylex.hee.system.util.CollectionUtil;

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
	
	/* TODO
	@UnitTest
	public void testWeightedList(){
		WeightedList<ObjectWeightPair<String>> list = new WeightedList<>();
		list.add(ObjectWeightPair.of("Herp",Integer.MAX_VALUE-1));
		list.add(ObjectWeightPair.of("Derp",1));
		
		Assert.equal(list.size(),2);
		Assert.equal(list.getRandomItem(new Random()).getObject(),"Herp");
	}
	
	@UnitTest
	public void testBadWeightedList(){
		WeightedList<ObjectWeightPair<String>> list = new WeightedList<>();
		
		Assert.isNull(list.getRandomItem(new Random()));
		Assert.isFalse(list.tryGetRandomItem(new Random()).isPresent());
		
		list.add(ObjectWeightPair.of("Bad",0));
		
		Assert.isNull(list.getRandomItem(new Random()));
		Assert.isFalse(list.tryGetRandomItem(new Random()).isPresent());
	}*/
	
	@UnitTest
	public void testCollectionUtil(){
		Assert.equal(CollectionUtil.newList("a","b","c","d","e").size(),5);
		Assert.equal(CollectionUtil.<String>newList(1,"a","b","c","d","e").size(),5);
		Assert.equal(CollectionUtil.newSet("a","b","b","c").size(),3);
		
		Map<String,Integer> map = new HashMap<>();
		map.put("a",3);
		map.put("b",1);
		map.put("c",2);
		
		SortedSet<Entry<String,Integer>> asc = CollectionUtil.sortMapByValueAsc(map);
		Iterator<Entry<String,Integer>> ascIter = asc.iterator();
		Assert.equal(ascIter.next(),new SimpleEntry<String,Integer>("b",1));
		Assert.equal(ascIter.next(),new SimpleEntry<String,Integer>("c",2));
		
		SortedSet<Entry<String,Integer>> desc = CollectionUtil.sortMapByValueDesc(map);
		Iterator<Entry<String,Integer>> descIter = desc.iterator();
		Assert.equal(descIter.next(),new SimpleEntry<String,Integer>("a",3));
		Assert.equal(descIter.next(),new SimpleEntry<String,Integer>("c",2));
	}
}
