package chylex.hee.api.message.element;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import chylex.hee.api.message.MessageRunner;
import chylex.hee.api.message.element.base.PreconditionComposite;
import chylex.hee.world.util.SpawnEntry;

public class SpawnEntryValue extends PreconditionComposite<SpawnEntry>{
	public static SpawnEntryValue any(){
		return new SpawnEntryValue();
	}
	
	public static final StringValue livingMobString = StringValue.function(input -> {
		Class<?> cls = (Class<?>)EntityList.stringToClassMapping.get(input);
		return cls != null && EntityLiving.class.isAssignableFrom(cls);
	});
	
	private SpawnEntryValue(){
		addCondition("id", livingMobString);
		addCondition("limit", IntValue.range(1, 127));
		addCondition("weight", IntValue.range(1, 127));
	}

	@Override
	public SpawnEntry getValue(MessageRunner runner){
		return null; // TODO new SpawnEntry((Class<? extends EntityLiving>)EntityList.stringToClassMapping.get(runner.getString("id")), runner.getInt("limit"), runner.getInt("weight"));
	}
}
