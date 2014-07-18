package chylex.hee.mechanics.minions.handlers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.nbt.NBTTagCompound;
import chylex.hee.entity.mob.EntityMobMinion;
import chylex.hee.entity.mob.util.DamageSourceMobUnscaled;
import chylex.hee.mechanics.minions.properties.MinionAttributes;

public class AbilityAttackMonsters extends AbstractAbilityEntityTargeter{
	public AbilityAttackMonsters(EntityMobMinion minion){
		super(minion,EntityMob.class,1);
	}

	@Override
	protected void onEntityCollision(Entity e){
		e.attackEntityFrom(new DamageSourceMobUnscaled(minion),2F+3F*minionData.getAttributeLevel(MinionAttributes.STRENGTH));
	}

	@Override
	protected boolean canTargetEntity(Entity e){
		EntityMob mob = (EntityMob)e;
		Entity owner = null;//minion.getOwner();
		return (mob.getAttackTarget() == owner || mob.getAITarget() == owner || e instanceof EntitySlime);
	}

	@Override
	public void onDeath(){}

	@Override
	public void writeDataToNBT(NBTTagCompound nbt){}

	@Override
	public void readDataFromNBT(NBTTagCompound nbt){}

}
