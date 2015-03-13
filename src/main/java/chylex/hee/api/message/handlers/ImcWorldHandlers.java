package chylex.hee.api.message.handlers;
import java.util.HashMap;
import java.util.Map;
import chylex.hee.api.message.MessageHandler;
import chylex.hee.api.message.MessageRunner;
import chylex.hee.api.message.element.StringValue;
import chylex.hee.api.message.element.WeightedLootValue;
import chylex.hee.api.message.utils.MessageLogger;
import chylex.hee.api.message.utils.RunEvent;
import chylex.hee.entity.block.EntityBlockHomelandCache;
import chylex.hee.world.loot.LootItemStack;
import chylex.hee.world.loot.WeightedLootList;
import chylex.hee.world.structure.island.biome.feature.forest.ravageddungeon.RavagedDungeonLoot;
import chylex.hee.world.structure.island.biome.feature.island.StructureHiddenCellar;
import chylex.hee.world.structure.island.biome.feature.island.laboratory.LaboratoryContent;
import chylex.hee.world.structure.tower.ComponentTower;
import com.google.common.base.Function;

public final class ImcWorldHandlers extends ImcHandler{
	private static final Map<String,WeightedLootList> lootNames = new HashMap<>();
	
	static{
		lootNames.put("DungeonTowerChest",ComponentTower.lootTower);
		lootNames.put("DungeonTowerFurnaceFuel",ComponentTower.lootFuel);
		lootNames.put("RavagedDungeonGeneral",RavagedDungeonLoot.lootGeneral);
		lootNames.put("RavagedDungeonUncommon",RavagedDungeonLoot.lootUncommon);
		lootNames.put("RavagedDungeonRare",RavagedDungeonLoot.lootRare);
		lootNames.put("RavagedDungeonFinalRoom",RavagedDungeonLoot.lootEnd);
		lootNames.put("HiddenCellarNormalHomeland",StructureHiddenCellar.normalChestVariation[0]);
		lootNames.put("HiddenCellarRareHomeland",StructureHiddenCellar.rareChestVariation[0]);
		lootNames.put("HiddenCellarNormalLaboratory",StructureHiddenCellar.normalChestVariation[1]);
		lootNames.put("HiddenCellarRareLaboratory",StructureHiddenCellar.rareChestVariation[1]);
		lootNames.put("HomelandCache",EntityBlockHomelandCache.loot);
		lootNames.put("LaboratorySmallChest",LaboratoryContent.smallChestLoot);
		lootNames.put("LaboratoryLargeChest",LaboratoryContent.largeChestLoot);
	}
	
	private static final StringValue lootName = StringValue.function(new Function<String,Boolean>(){
		@Override
		public Boolean apply(String input){
			return Boolean.valueOf(lootNames.containsKey(input));
		}
	});
	
	private static final MessageHandler lootAdd = new MessageHandler(){
		@Override
		public void call(MessageRunner runner){
			WeightedLootList list = lootNames.get(runner.getString("list"));
			LootItemStack toAdd = runner.<LootItemStack>getValue("item");
			
			for(LootItemStack item:list){
				if (item.getItem() == toAdd.getItem()){
					MessageLogger.logFail("The item was already in the list.");
					return;
				}
			}
			
			list.add(toAdd);
			MessageLogger.logOk("Added 1 item to the list.");
		}
	};
	
	@Override
	public void register(){
		register("HEE:World:LootAdd",lootAdd,RunEvent.LOADCOMPLETE)
		.addProp("list",lootName)
		.addProp("item",WeightedLootValue.any());
	}
}
