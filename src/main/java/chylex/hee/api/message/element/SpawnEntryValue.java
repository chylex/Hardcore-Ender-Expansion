package chylex.hee.api.message.element;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import chylex.hee.api.message.MessageRunner;
import chylex.hee.api.message.element.base.PreconditionComposite;
import chylex.hee.world.util.SpawnEntry;
import com.google.common.base.Function;

public class SpawnEntryValue extends PreconditionComposite<SpawnEntry>{
	public static SpawnEntryValue any(){
		return new SpawnEntryValue();
	}
	
	public static final StringValue livingMobString = StringValue.function(new Function<String,Boolean>(){
		@Override
		public Boolean apply(String input){
			Class<?> cls = (Class<?>)EntityList.stringToClassMapping.get(input);
			return Boolean.valueOf(cls != null && EntityLiving.class.isAssignableFrom(cls));
		}
	});
	
	private SpawnEntryValue(){
		addCondition("id",livingMobString);
		addCondition("limit",IntValue.range(1,127));
		addCondition("weight",IntValue.range(1,127));
	}

	@Override
	public SpawnEntry getValue(MessageRunner runner){
		return new SpawnEntry((Class<? extends EntityLiving>)EntityList.stringToClassMapping.get(runner.getString("id")),runner.getInt("limit"),runner.getInt("weight"));
	}
}
