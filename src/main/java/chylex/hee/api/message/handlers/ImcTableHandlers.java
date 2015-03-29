package chylex.hee.api.message.handlers;
import chylex.hee.api.message.MessageHandler;
import chylex.hee.api.message.MessageRunner;
import chylex.hee.api.message.element.DecimalValue;
import chylex.hee.api.message.element.IntValue;
import chylex.hee.api.message.element.ItemDamagePairValue;
import chylex.hee.api.message.element.ItemPatternValue;
import chylex.hee.api.message.utils.MessageLogger;
import chylex.hee.api.message.utils.RunEvent;
import chylex.hee.mechanics.energy.EnergyValues;
import chylex.hee.mechanics.misc.StardustDecomposition;
import chylex.hee.system.util.ItemDamagePair;
import chylex.hee.system.util.ItemPattern;
import chylex.hee.tileentity.TileEntityExperienceTable;

public final class ImcTableHandlers extends ImcHandler{
	private static final MessageHandler decompositionBlacklist = new MessageHandler(){
		@Override
		public void call(MessageRunner runner){
			StardustDecomposition.addToBlacklist(runner.<ItemPattern>getValue("pattern"));
			MessageLogger.logOk("Added 1 pattern to the list.");
		}
	};
	
	private static final MessageHandler energySet = new MessageHandler(){
		@Override
		public void call(MessageRunner runner){
			if (EnergyValues.setItemEnergy(runner.<ItemDamagePair>getValue("item"),(float)runner.getDouble("units")))MessageLogger.logOk("Added 1 item to the list.");
			else MessageLogger.logFail("The item was already in the list.");
		}
	};
	
	private static final MessageHandler expTableAdd = new MessageHandler(){
		@Override
		public void call(MessageRunner runner){
			if (TileEntityExperienceTable.addDirectConversion(runner.<ItemDamagePair>getValue("item"),(byte)runner.getInt("bottles")))MessageLogger.logOk("Added 1 item to the list.");
			else MessageLogger.logFail("The item was already in the list.");
		}
	};
	
	@Override
	public void register(){
		register("HEE:DecompositionTable:Blacklist",decompositionBlacklist,RunEvent.LOADCOMPLETE)
		.addProp("pattern",ItemPatternValue.any());
		
		register("HEE:ExtractionTable:SetEnergy",energySet,RunEvent.LOADCOMPLETE)
		.addProp("item",ItemDamagePairValue.any())
		.addProp("units",DecimalValue.positiveOrZero());
		
		register("HEE:ExperienceTable:AddItem",expTableAdd,RunEvent.LOADCOMPLETE)
		.addProp("item",ItemDamagePairValue.any())
		.addProp("bottles",IntValue.range(1,64));
	}
}
