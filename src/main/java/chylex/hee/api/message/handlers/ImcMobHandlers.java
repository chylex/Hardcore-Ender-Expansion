package chylex.hee.api.message.handlers;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import chylex.hee.api.message.MessageHandler;
import chylex.hee.api.message.MessageRunner;
import chylex.hee.api.message.element.DecimalValue;
import chylex.hee.api.message.element.SpawnEntryValue;
import chylex.hee.api.message.utils.MessageLogger;
import chylex.hee.api.message.utils.RunEvent;
import chylex.hee.entity.GlobalMobData;
import chylex.hee.mechanics.energy.EnergyValues;

public final class ImcMobHandlers extends ImcHandler{
	private static final MessageHandler enderGooTolerantAdd = new MessageHandler(){
		@Override
		public void call(MessageRunner runner){
			if (GlobalMobData.setEnderGooTolerant(runner.getString("id")))MessageLogger.logOk("Added 1 mob to the list.");
			else MessageLogger.logFail("The mob was already in the list.");
		}
	};
	
	private static final MessageHandler energySet = new MessageHandler(){
		@Override
		public void call(MessageRunner runner){
			if (EnergyValues.setMobEnergy((Class<? extends EntityLivingBase>)EntityList.stringToClassMapping.get(runner.getString("id")),(float)runner.getDouble("units")))MessageLogger.logOk("Added 1 mob to the list.");
			else MessageLogger.logFail("The mob was already in the list.");
		}
	};
	
	@Override
	public void register(){
		register("HEE:Mobs:SetGooImmune",enderGooTolerantAdd,RunEvent.LOADCOMPLETE)
		.addProp("id",SpawnEntryValue.livingMobString);
		
		register("HEE:Mobs:SetEnergy",energySet,RunEvent.LOADCOMPLETE)
		.addProp("id",SpawnEntryValue.livingMobString)
		.addProp("units",DecimalValue.positiveOrZero());
	}
}
