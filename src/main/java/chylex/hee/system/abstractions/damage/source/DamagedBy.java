package chylex.hee.system.abstractions.damage.source;
import net.minecraft.util.DamageSource;

public class DamagedBy extends DamageSource{
	public DamagedBy(String sourceName){
		super(sourceName);
	}

	@Override
	public boolean isDamageAbsolute(){
		return true;
	}
	
	@Override
	public boolean isUnblockable(){
		return true;
	}
	
	@Override
	public boolean isDifficultyScaled(){
		return false;
	}
}
