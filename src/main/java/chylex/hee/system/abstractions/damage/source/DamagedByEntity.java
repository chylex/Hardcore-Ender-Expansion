package chylex.hee.system.abstractions.damage.source;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

public class DamagedByEntity extends EntityDamageSource{
	public static String getDamageFromEntity(Entity source){
		if (source instanceof EntityPlayer)return "player";
		else if (source instanceof EntityLiving)return "mob";
		else if (source instanceof EntityFireball)return "fireball";
		else if (source instanceof EntityArrow)return "arrow";
		else return "generic";
	}
	
	public DamagedByEntity(Entity entity){
		super(getDamageFromEntity(entity), entity);
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
	
	public static class Indirect extends EntityDamageSourceIndirect{
		public Indirect(Entity source, Entity indirectSource){
			super(getDamageFromEntity(source), source, indirectSource);
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
}
