package chylex.hee.api.message.handlers;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import chylex.hee.api.message.MessageHandler;
import chylex.hee.api.message.MessageRunner;
import chylex.hee.api.message.element.DecimalValue;
import chylex.hee.api.message.element.StringValue;
import chylex.hee.api.message.utils.MessageLogger;
import chylex.hee.api.message.utils.RunEvent;
import chylex.hee.entity.GlobalMobData;
import chylex.hee.mechanics.energy.EnergyValues;
import com.google.common.base.Function;

public final class ImcMobHandlers extends ImcHandler{
	private static final StringValue livingMobString = StringValue.function(new Function<String,Boolean>(){
		@Override
		public Boolean apply(String input){
			Class<?> cls = (Class<?>)EntityList.stringToClassMapping.get(input);
			return Boolean.valueOf(cls != null && EntityLivingBase.class.isAssignableFrom(cls));
		}
	});
	
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
		.addProp("id",livingMobString);
		
		register("HEE:Mobs:SetEnergy",energySet,RunEvent.LOADCOMPLETE)
		.addProp("id",livingMobString)
		.addProp("units",DecimalValue.positiveOrZero());
	}
}
