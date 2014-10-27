package chylex.hee.entity.mob;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import chylex.hee.proxy.ModCommonProxy;
import chylex.hee.system.util.MathUtil;

public class EntityMobEnderGuardian extends EntityMob{
	public EntityMobEnderGuardian(World world){
		super(world);
		setSize(1.5F,3.2F);
	}
	
	@Override
	protected Entity findPlayerToAttack(){
		EntityPlayer player = worldObj.getClosestVulnerablePlayerToEntity(this,3D);
		return player != null && canEntityBeSeen(player) ? player : null;
	}
	
	@Override
	protected void applyEntityAttributes(){
		super.applyEntityAttributes();
		getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(ModCommonProxy.opMobs ? 1.2D : 1.1D);
		getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(ModCommonProxy.opMobs ? 100D : 80D);
		getEntityAttribute(SharedMonsterAttributes.attackDamage).setBaseValue(ModCommonProxy.opMobs ? 13D : 8D);
	}
	
	@Override
	public boolean attackEntityAsMob(Entity entity){
		float damage = (float)this.getEntityAttribute(SharedMonsterAttributes.attackDamage).getAttributeValue();

        if (entity.attackEntityFrom(DamageSource.causeMobDamage(this),damage)){
			entity.addVelocity(-MathHelper.sin(MathUtil.toRad(rotationYaw))*3D,0.3D,MathHelper.cos(MathUtil.toRad(rotationYaw))*3D);
			motionX *= 0.8D;
			motionZ *= 0.8D;

            EnchantmentHelper.func_151385_b(this,entity);
        	return true;
        }
        else return false;
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource source, float damage){
		if (source == DamageSource.fallingBlock || source == DamageSource.anvil)damage *= 0.8F;
		else if (source.isProjectile())damage *= 0.5F;
		else if (source.isFireDamage() || source == DamageSource.drown)damage *= 0.2F;
		else if (source == DamageSource.cactus)damage = 0F;
		
		return damage < 0.1F ? false : super.attackEntityFrom(source,damage);
	}
	
	@Override
	protected void dropFewItems(boolean recentlyHit, int looting){
		int amount = rand.nextInt(2+looting)-rand.nextInt(2);
		for(int a = 0; a < amount; a++)dropItem(Items.ender_pearl,1);
		
		amount = 1+rand.nextInt(3+(looting>>1));
		for(int a = 0; a < amount; a++)dropItem(Item.getItemFromBlock(Blocks.obsidian),1);
	}
	
	@Override
	public void addVelocity(double xVelocity, double yVelocity, double zVelocity){
		double mp = rand.nextInt(5) == 0 ? 0D : 0.3D+rand.nextDouble()*0.2D;
		super.addVelocity(xVelocity*mp,yVelocity*mp,zVelocity*mp);
	}
	
	@Override
	public String getCommandSenderName(){
		return StatCollector.translateToLocal("entity.enderGuardian.name");
	}
}
