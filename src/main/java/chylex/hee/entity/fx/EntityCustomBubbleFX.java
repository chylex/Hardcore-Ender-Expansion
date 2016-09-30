package chylex.hee.entity.fx;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.Pos;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityCustomBubbleFX extends EntityBubbleFX{
	private final int airLifeSpan;
	private int airLife;
	
	public EntityCustomBubbleFX(World world, double x, double y, double z, double motionX, double motionY, double motionZ){
		super(world, x, y, z, motionX, motionY, motionZ);
		airLifeSpan = rand.nextInt(40)+18;
		noClip = true;
	}

	@Override
	public void onUpdate(){
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		moveEntity(motionX, motionY += 0.0045D, motionZ);
		motionX *= 0.85D;
		motionY *= 0.875D;
		motionZ *= 0.85D;

		if (Pos.at(this).getMaterial(worldObj) != Material.water){
			if (++airLife > airLifeSpan)setDead();
		}
		else if (particleMaxAge-- <= 0)setDead();
	}
}
