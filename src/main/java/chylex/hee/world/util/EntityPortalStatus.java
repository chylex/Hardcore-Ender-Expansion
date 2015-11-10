package chylex.hee.world.util;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.entity.EntityLivingBase;

public final class EntityPortalStatus{
	private Set<UUID> waitingForContact = new HashSet<>();
	
	/**
	 * Updates the entity status and returns true if it should be teleported.
	 */
	public boolean onTouch(EntityLivingBase entity){
		UUID id = entity.getUniqueID();
		
		if (waitingForContact.remove(id)){
			entity.timeUntilPortal = 10;
			return false;
		}
		else if (entity.timeUntilPortal == 0){
			entity.timeUntilPortal = 10;
			waitingForContact.add(id);
			return true;
		}
		else{
			entity.timeUntilPortal = 10;
			return false;
		}
	}
}
