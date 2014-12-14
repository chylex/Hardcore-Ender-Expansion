package chylex.hee.mechanics.curse;
import net.minecraft.entity.EntityLivingBase;

public interface ICurseHandler{
	/**
	 * Return true if the tick counted as a use.
	 */
	boolean tickEntity(EntityLivingBase entity, ICurseCaller caller);
}
