package chylex.hee.entity.block;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.mechanics.enhancements.list.EnhancementList;
import chylex.hee.mechanics.enhancements.types.TNTEnhancements;
import chylex.hee.system.abstractions.Explosion;
import chylex.hee.system.abstractions.Pos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

// TODO add a global madness indicator that will control how many packets, particles, and such are going to be handled to reduce lag
public class EntityBlockEnhancedTNTPrimed extends EntityTNTPrimed{
	private final EnhancementList<TNTEnhancements> enhancements;
	private boolean wentIntoWall = false;
	
	public EntityBlockEnhancedTNTPrimed(World world){
		super(world);
		fuse = 80;
		yOffset = 0;
		
		if (world.isRemote){
			int count = world.loadedEntityList.size();
			if (count > 500)setDead();
		}
		
		this.enhancements = new EnhancementList<>(TNTEnhancements.class);
	}

	public EntityBlockEnhancedTNTPrimed(World world, double x, double y, double z, EntityLivingBase igniter, EnhancementList<TNTEnhancements> enhancements){
		super(world,x,y,z,igniter);
		this.enhancements = enhancements;
		
		/* TODO if (tntEnhancements.contains(TNTEnhancements.NOCLIP)){
			noClip = true;
			fuse = 40;
		}
		
		if (tntEnhancements.contains(TNTEnhancements.NO_FUSE))fuse = 1;*/
	}

	@Override
	public void onUpdate(){
		lastTickPosX = posX;
		lastTickPosY = posY;
		lastTickPosZ = posZ;
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		
		motionY -= 0.04D;
		moveEntity(motionX,motionY,motionZ);
		motionX *= 0.98D;
		motionY *= 0.98D;
		motionZ *= 0.98D;
		
		if (!worldObj.isRemote && noClip){
			Block block = Pos.at(this).getBlock(worldObj);
			
			if (!wentIntoWall && block.isOpaqueCube())wentIntoWall = true;
			else if (wentIntoWall && block.getMaterial() == Material.air){
				fuse = 1;
			}
		}

		if (onGround && !noClip){
			motionX *= 0.7D;
			motionZ *= 0.7D;
			motionY *= -0.5D;
		}
		
		if (--fuse <= 0 && !worldObj.isRemote){
			setDead();
			Explosion explosion = new Explosion(worldObj,posX,posY,posZ,4F,this);
			explosion.trigger();
		}
		else{
			HardcoreEnderExpansion.fx.setLimiter();
			HardcoreEnderExpansion.fx.global("smoke",posX,posY+0.5D,posZ,0D,0D,0D);
			HardcoreEnderExpansion.fx.reset();
		}
		
		setPosition(posX,posY,posZ);
	}

	private void explode(){
		/*EnhancedTNTExplosion explosion = new EnhancedTNTExplosion(worldObj,this,posX,posY,posZ,tntEnhancements.contains(TNTEnhancements.EXTRA_POWER) ? 5.2F : 4F);
		explosion.isFlaming = tntEnhancements.contains(TNTEnhancements.FIRE);
		explosion.isSmoking = !tntEnhancements.contains(TNTEnhancements.NO_BLOCK_DAMAGE);
		explosion.damageEntities = !tntEnhancements.contains(TNTEnhancements.NO_ENTITY_DAMAGE);
		explosion.doExplosionA();
		explosion.doExplosionB(true);
		explosion.doExplosionB(true);*/
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void setPositionAndRotation2(double x, double y, double z, float yaw, float pitch, int par9){
		if (worldObj.loadedEntityList.size() > 500)return; // let client handle motion if there's too much TNT, looks a bit better
		setPosition(x,y,z);
		setRotation(yaw,pitch);
	}
	
	@Override
	public void writeEntityToNBT(NBTTagCompound nbt){
		super.writeEntityToNBT(nbt);
		nbt.setString("enhancements2",enhancements.serialize());
		nbt.setBoolean("wentIntoWall",wentIntoWall);
	}

	@Override
	public void readEntityFromNBT(NBTTagCompound nbt){
		super.readEntityFromNBT(nbt);
		enhancements.deserialize(nbt.getString("enhancements2"));
		wentIntoWall = nbt.getBoolean("wentIntoWall");
	}
}
