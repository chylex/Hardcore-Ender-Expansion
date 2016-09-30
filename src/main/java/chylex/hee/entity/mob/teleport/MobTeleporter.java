package chylex.hee.entity.mob.teleport;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.Entity;
import chylex.hee.entity.mob.teleport.TeleportLocation.ITeleportXZ;
import chylex.hee.entity.mob.teleport.TeleportLocation.ITeleportY;
import chylex.hee.system.abstractions.Vec;

public class MobTeleporter<T extends Entity>{
	private int attempts;
	private ITeleportLocation<T> locationSelector;
	private List<ITeleportPredicate<T>> locationPredicates = new ArrayList<>(2);
	private List<ITeleportListener<T>> onTeleport = new ArrayList<>(2);
	
	public void setAttempts(int attempts){
		this.attempts = attempts;
	}
	
	public void setLocationSelector(ITeleportLocation<T> locationSelector){
		this.locationSelector = locationSelector;
	}
	
	public void setLocationSelector(ITeleportXZ<T> xzSelector, ITeleportY<T> ySelector){
		this.locationSelector = new TeleportLocation<>(xzSelector, ySelector);
	}
	
	public void addLocationPredicate(ITeleportPredicate<T> locationPredicate){
		this.locationPredicates.add(locationPredicate);
	}
	
	public void onTeleport(ITeleportListener<T> listener){
		this.onTeleport.add(listener);
	}
	
	public boolean teleport(T entity, Random rand){
		if (entity.worldObj.isRemote)return false;
		
		Vec oldPos = Vec.pos(entity), oldPosCopy = oldPos.copy();
		
		for(int attempt = 0; attempt < attempts; attempt++){
			Vec newPos = locationSelector.findPosition(entity, oldPosCopy, rand);
			entity.setPosition(newPos.x, newPos.y, newPos.z);
			
			if (locationPredicates.stream().allMatch(predicate -> predicate.isValid(entity, oldPos, rand))){
				if (entity.ridingEntity != null){
					entity.mountEntity(null);
					entity.setPosition(newPos.x, newPos.y, newPos.z);
				}
				
				for(ITeleportListener<T> listener:onTeleport)listener.onTeleport(entity, oldPos, rand);
				return true;
			}
		}
		
		entity.setPosition(oldPos.x, oldPos.y, oldPos.z);
		return false;
	}
}
