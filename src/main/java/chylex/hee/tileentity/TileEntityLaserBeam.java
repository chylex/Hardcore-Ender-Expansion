package chylex.hee.tileentity;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import chylex.hee.entity.fx.FXType;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.proxy.ModCommonProxy;

public class TileEntityLaserBeam extends TileEntity implements IUpdatePlayerListBox{
	private float beamAngle;
	private int ticksLeft = 1;
	
	@Override
	public void update(){
		beamAngle += 1.5F;
		
		if (!worldObj.isRemote && --ticksLeft <= 0){
			worldObj.setBlockToAir(getPos());
			double x = getPos().getX()+0.5D, y = getPos().getY(), z = getPos().getZ()+0.5D;
			
			for(EntityPlayer player:(List<EntityPlayer>)worldObj.getEntitiesWithinAABB(EntityPlayer.class,AxisAlignedBB.fromBounds(x-1.5D,y,z-1.5D,x+1.5D,y+1D,z+1.5D))){
				player.hurtResistantTime = 0;
				player.attackEntityFrom(DamageSource.magic,ModCommonProxy.opMobs ? 5F : 3F);
				player.hurtResistantTime = 0;
				player.attackEntityFrom(DamageSource.generic,ModCommonProxy.opMobs ? 10F : 6F);
				player.setFire(40);
			}
			
			PacketPipeline.sendToAllAround(this,64D,new C20Effect(FXType.Basic.LASER_BEAM_DESTROY,this));
		}
	}
	
	public void setTicksLeft(int ticksLeft){
		this.ticksLeft = ticksLeft;
	}
	
	public float getBeamAngle(){
		return beamAngle;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		
		nbt.setFloat("beamAng",beamAngle);
		nbt.setInteger("ticksLeft",ticksLeft);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		
		beamAngle = nbt.getFloat("beamAng");
		ticksLeft = nbt.getInteger("ticksLeft");
	}
}
