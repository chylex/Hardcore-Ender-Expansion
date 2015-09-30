package chylex.hee.system.test.list;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;
import java.util.Map.Entry;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.system.collections.CustomArrayList;
import chylex.hee.system.collections.RandomList;
import chylex.hee.system.collections.weight.IWeightProvider;
import chylex.hee.system.collections.weight.WeightedList;
import chylex.hee.system.collections.weight.WeightedMap;
import chylex.hee.system.test.Assert;
import chylex.hee.system.test.UnitTest;

public class TestCollections{
	@UnitTest
	public void testCustomArrayList(){
		CustomArrayList<String> list = new CustomArrayList<>();
		list.add("Entry 1");
		list.add("Entry 2");
		list.addAll(new String[]{ "Entry 3", "Entry 4" });
		
		Assert.equal(list.size(),4);
		Assert.equal(list.get(2),"Entry 3");
	}
	
	@UnitTest
	public void testCollectionConstruction(){
		List<String> list = CollectionUtil.newList("A","B","C");
		Assert.equal(list.size(),3);
		Assert.contains(list,"A");
		Assert.contains(list,"B");
		Assert.contains(list,"C");
		
		List<String> similarList = CollectionUtil.newList(8,new String[]{ "A","B","C" });
		Assert.equal(similarList,list);
		
		Set<String> set = CollectionUtil.newSet("A","A","B","C","C","C");
		Assert.equal(set.size(),3);
		Assert.contains(set,"A");
		Assert.contains(set,"B");
		Assert.contains(set,"C");
	}
	
	@UnitTest
	public void testShuffledList(){
		List<String> originalList = new ArrayList<>();
		originalList.add("Item 1");
		originalList.add("Item 2");
		originalList.add("Item 3");
		originalList.add("Item 4");
		RandomList<String> randomList = CollectionUtil.shuffled(originalList,new Random());
		
		Assert.equal(originalList.size(),4);
		
		for(String item:randomList){
			Assert.isTrue(originalList.remove(item));
		}
		
		Assert.equal(originalList.size(),0);
	}
	
	@UnitTest
	public void testRandomGet(){
		Random rand = new Random();
		int value;
		
		List<Integer> emptyList = new ArrayList<>();
		Assert.isFalse(CollectionUtil.random(emptyList,rand).isPresent());
		Assert.isNull(CollectionUtil.randomOrNull(emptyList,rand));
		
		List<Integer> filledList = new ArrayList<>();
		filledList.add(1);
		filledList.add(7);
		filledList.add(33);
		
		value = CollectionUtil.random(filledList,rand).get().intValue();
		Assert.isTrue(value == 1 || value == 7 || value == 33);
		
		value = CollectionUtil.randomOrNull(filledList,rand).intValue();
		Assert.isTrue(value == 1 || value == 7 || value == 33);
	}
	
	@UnitTest
	public void testMapSorting(){
		Map<String,Integer> map = new HashMap<>();
		map.put("a",3);
		map.put("b",1);
		map.put("c",2);
		
		SortedSet<Entry<String,Integer>> set;
		Iterator<Entry<String,Integer>> iterator;
		
		set = CollectionUtil.sortMapByValueAsc(map);
		iterator = set.iterator();
		Assert.equal(iterator.next(),new SimpleEntry<>("b",1));
		Assert.equal(iterator.next(),new SimpleEntry<>("c",2));
		Assert.equal(iterator.next(),new SimpleEntry<>("a",3));
		
		set = CollectionUtil.sortMapByValueDesc(map);
		iterator = set.iterator();
		Assert.equal(iterator.next(),new SimpleEntry<>("a",3));
		Assert.equal(iterator.next(),new SimpleEntry<>("c",2));
		Assert.equal(iterator.next(),new SimpleEntry<>("b",1));
	}
	
	@UnitTest
	public void testWeightedList(){
		Random rand = new Random();
		String item;
		
		WeightedList<StringWeight> emptyList = new WeightedList<>();
		Assert.isTrue(emptyList.isEmpty());
		Assert.equal(emptyList.size(),0);
		Assert.equal(emptyList.getTotalWeight(),0);
		Assert.isNull(emptyList.getRandomItem(rand));
		Assert.isFalse(emptyList.tryGetRandomItem(rand).isPresent());
		Assert.isNull(emptyList.removeRandomItem(rand));
		Assert.isFalse(emptyList.tryRemoveRandomItem(rand).isPresent());
		
		WeightedList<StringWeight> filledList = new WeightedList<>(
			new StringWeight("Item 1",10),
			new StringWeight("Item 2",50),
			new StringWeight("Item 3",25)
		);
		
		Assert.isFalse(filledList.isEmpty());
		Assert.equal(filledList.size(),3);
		Assert.equal(filledList.getTotalWeight(),85);
		Assert.equal(filledList.indexOf(new StringWeight("Item 1",10)),0);
		Assert.equal(filledList.indexOf(new StringWeight("Item 3",25)),2);
		Assert.equal(filledList.indexOf(new StringWeight("Item Nope",0)),-1);
		
		item = filledList.getRandomItem(rand).str;
		Assert.isTrue(item.equals("Item 1") || item.equals("Item 2") || item.equals("Item 3"));
		item = filledList.tryGetRandomItem(rand).get().str;
		Assert.isTrue(item.equals("Item 1") || item.equals("Item 2") || item.equals("Item 3"));
		
		filledList.remove(new StringWeight("Item 1",10));
		
		Assert.isFalse(filledList.isEmpty());
		Assert.equal(filledList.size(),2);
		Assert.equal(filledList.getTotalWeight(),75);
		
		item = filledList.removeRandomItem(rand).str;
		Assert.isTrue(item.equals("Item 1") || item.equals("Item 2") || item.equals("Item 3"));
		item = filledList.tryRemoveRandomItem(rand).get().str;
		Assert.isTrue(item.equals("Item 1") || item.equals("Item 2") || item.equals("Item 3"));
		
		Assert.isTrue(filledList.isEmpty());
		Assert.equal(filledList.size(),0);
		
		WeightedList<StringWeight> extremeList = new WeightedList<>(2);
		extremeList.add(new StringWeight("Yes Please",Integer.MAX_VALUE-1));
		extremeList.add(new StringWeight("No Chance",1));
		
		Assert.equal(extremeList.size(),2);
		Assert.equal(extremeList.getTotalWeight(),Integer.MAX_VALUE);
		Assert.equal(extremeList.getRandomItem(rand).str,"Yes Please");
		Assert.equal(extremeList.removeRandomItem(rand).str,"Yes Please");
		
		WeightedList<StringWeight> invalidList = new WeightedList<>();
		invalidList.add(new StringWeight("Item 1",0));
		invalidList.add(new StringWeight("Item 2",0));
		invalidList.add(new StringWeight[]{ new StringWeight("Item 3",0), new StringWeight("Item 4",0) });
		
		Assert.isFalse(invalidList.isEmpty());
		Assert.equal(invalidList.size(),4);
		Assert.isNull(invalidList.getRandomItem(rand));
		Assert.isNull(invalidList.removeRandomItem(rand));
		
		WeightedList<StringWeight> lessInvalidList = new WeightedList<>(invalidList);
		lessInvalidList.add(new StringWeight("Okay",1));

		Assert.equal(lessInvalidList.size(),5);
		Assert.equal(lessInvalidList.getRandomItem(rand).str,"Okay");
		Assert.equal(lessInvalidList.removeRandomItem(rand).str,"Okay");
	}
	
	@UnitTest
	public void testWeightedMap(){
		Random rand = new Random();
		String item;
		
		WeightedMap<String> emptyMap = new WeightedMap<>();
		Assert.isTrue(emptyMap.isEmpty());
		Assert.equal(emptyMap.size(),0);
		Assert.equal(emptyMap.getTotalWeight(),0);
		Assert.isNull(emptyMap.getRandomItem(rand));
		Assert.isFalse(emptyMap.tryGetRandomItem(rand).isPresent());
		Assert.isNull(emptyMap.removeRandomItem(rand));
		Assert.isFalse(emptyMap.tryRemoveRandomItem(rand).isPresent());
		
		WeightedMap<String> filledMap = new WeightedMap<>();
		filledMap.add("Item 1",50);
		filledMap.add("Item 2",25);
		filledMap.add("Item 3",25);
		
		Assert.isFalse(filledMap.isEmpty());
		Assert.equal(filledMap.size(),3);
		Assert.equal(filledMap.getTotalWeight(),100);
		
		item = filledMap.getRandomItem(rand);
		Assert.isTrue(item.equals("Item 1") || item.equals("Item 2") || item.equals("Item 3"));
		item = filledMap.tryGetRandomItem(rand).get();
		Assert.isTrue(item.equals("Item 1") || item.equals("Item 2") || item.equals("Item 3"));
		
		item = filledMap.removeRandomItem(rand);
		Assert.isTrue(item.equals("Item 1") || item.equals("Item 2") || item.equals("Item 3"));
		item = filledMap.tryRemoveRandomItem(rand).get();
		Assert.isTrue(item.equals("Item 1") || item.equals("Item 2") || item.equals("Item 3"));
		
		Assert.equal(filledMap.size(),1);
		Assert.isTrue(filledMap.getTotalWeight() <= 50);
		
		WeightedMap<String> extremeMap = new WeightedMap<>(map -> {
			map.add("Yes Please",Integer.MAX_VALUE-1);
			map.add("No Chance",1);
		});
		
		Assert.equal(extremeMap.size(),2);
		Assert.equal(extremeMap.getTotalWeight(),Integer.MAX_VALUE);
		Assert.equal(extremeMap.getRandomItem(rand),"Yes Please");
		Assert.equal(extremeMap.removeRandomItem(rand),"Yes Please");
		
		WeightedMap<String> invalidMap = new WeightedMap<>(4);
		invalidMap.add("Oh :(",0);
		
		for(int run = 0; run < 2; run++){
			Assert.isFalse(invalidMap.isEmpty());
			Assert.equal(invalidMap.size(),1);
			Assert.isNull(invalidMap.getRandomItem(rand));
			Assert.isNull(invalidMap.removeRandomItem(rand));
			
			if (run == 0)invalidMap.add("Oh :(",0); // duplicate test
		}
		
		WeightedMap<String> lessInvalidMap = new WeightedMap<>(4,map -> {
			map.add("Meh",0);
			map.add("Okay",1);
		});
		
		Assert.equal(lessInvalidMap.size(),2);
		Assert.equal(lessInvalidMap.getRandomItem(rand),"Okay");
		Assert.equal(lessInvalidMap.removeRandomItem(rand),"Okay");
	}
	
	private static class StringWeight implements IWeightProvider{
		final String str;
		final int weight;
		
		StringWeight(String str, int weight){
			this.str = str;
			this.weight = weight;
		}
		
		@Override
		public int getWeight(){
			return weight;
		}
		
		@Override
		public int hashCode(){
			return str.hashCode();
		}
		
		@Override
		public boolean equals(Object obj){
			return obj instanceof StringWeight && ((StringWeight)obj).str.equals(str);
		}
	}
}
