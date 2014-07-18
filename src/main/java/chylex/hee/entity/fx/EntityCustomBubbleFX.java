package chylex.hee.entity.fx;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityCustomBubbleFX extends EntityBubbleFX{
	private byte airLife = 0, airLifeSpan;
	
	public EntityCustomBubbleFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ){
		super(world,x,y,z,motionX,motionY,motionZ);
		airLifeSpan = (byte)(rand.nextInt(40)+18);
		noClip = true;
	}

	@Override
	public void onUpdate(){
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		moveEntity(motionX,motionY += 0.0045D,motionZ);
		motionX *= 0.85D;
		motionY *= 0.875D;
		motionZ *= 0.85D;

		if (worldObj.getBlock((int)Math.floor(posX),(int)Math.floor(posY),(int)Math.floor(posZ)).getMaterial() != Material.water){
			if (++airLife > airLifeSpan)setDead();
		}
		else if (particleMaxAge-- <= 0)setDead();
	}
}
