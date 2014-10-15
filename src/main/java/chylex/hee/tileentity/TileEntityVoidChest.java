package chylex.hee.tileentity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;

public class TileEntityVoidChest extends TileEntity{
	public float lidAnim;
	public float prevLidAnim;
	public int openedAmount;
	private int ticksExisted;
	
	@Override
	public void updateEntity(){
		super.updateEntity();

		if (++ticksExisted%80 == 0)worldObj.addBlockEvent(xCoord,yCoord,zCoord,Blocks.ender_chest,1,openedAmount);

		prevLidAnim = lidAnim;

		if (openedAmount > 0 && lidAnim == 0F){
			worldObj.playSoundEffect(xCoord+0.5D,yCoord+0.5D,zCoord+0.5D,"random.chestopen",0.5F,worldObj.rand.nextFloat()*0.1F+0.9F);
		}

		if (openedAmount == 0 && lidAnim > 0F || openedAmount > 0 && lidAnim < 1F){
			float oldAnim = lidAnim;
			lidAnim = Math.max(0F,Math.min(1F,openedAmount > 0 ? lidAnim+0.1F : lidAnim-0.1F));

			if (lidAnim < 0.5F && oldAnim >= 0.5F){
				worldObj.playSoundEffect(xCoord+0.5D,yCoord+0.5D,zCoord+0.5D,"random.chestclosed",0.5F,worldObj.rand.nextFloat()*0.1F+0.9F);
			}
		}
	}

	@Override
	public boolean receiveClientEvent(int eventId, int eventData){
		if (eventId == 1){
			openedAmount = eventData;
			return true;
		}
		else return super.receiveClientEvent(eventId,eventData);
	}

	@Override
	public void invalidate(){
		updateContainingBlockInfo();
		super.invalidate();
	}

	public void addPlayerToOpenList(){
		++openedAmount;
		worldObj.addBlockEvent(xCoord,yCoord,zCoord,Blocks.ender_chest,1,openedAmount);
	}

	public void removePlayerFromOpenList(){
		--openedAmount;
		worldObj.addBlockEvent(xCoord,yCoord,zCoord,Blocks.ender_chest,1,openedAmount);
	}

	public boolean canPlayerUse(EntityPlayer player){
		return worldObj.getTileEntity(xCoord,yCoord,zCoord) != this ? false : player.getDistanceSq(xCoord+0.5D,yCoord+0.5D,zCoord+0.5D) <= 64D;
	}
}
