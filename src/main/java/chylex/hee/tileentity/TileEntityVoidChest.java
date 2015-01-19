package chylex.hee.tileentity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import chylex.hee.block.BlockList;
import chylex.hee.system.util.MathUtil;

public class TileEntityVoidChest extends TileEntity implements IUpdatePlayerListBox{
	public float lidAnim;
	public float prevLidAnim;
	public int openedAmount;
	private int ticksExisted;
	
	@Override
	public void update(){
		if (++ticksExisted%80 == 0)worldObj.addBlockEvent(getPos(),BlockList.void_chest,1,openedAmount);

		prevLidAnim = lidAnim;

		if (openedAmount > 0 && lidAnim == 0F){
			worldObj.playSoundEffect(getPos().getX()+0.5D,getPos().getY()+0.5D,getPos().getZ()+0.5D,"random.chestopen",0.5F,worldObj.rand.nextFloat()*0.1F+0.9F);
		}

		if (openedAmount == 0 && lidAnim > 0F || openedAmount > 0 && lidAnim < 1F){
			float oldAnim = lidAnim;
			lidAnim = MathUtil.clamp(openedAmount > 0 ? lidAnim+0.1F : lidAnim-0.1F,0F,1F);

			if (lidAnim < 0.5F && oldAnim >= 0.5F){
				worldObj.playSoundEffect(getPos().getX()+0.5D,getPos().getY()+0.5D,getPos().getZ()+0.5D,"random.chestclosed",0.5F,worldObj.rand.nextFloat()*0.1F+0.9F);
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
		worldObj.addBlockEvent(getPos(),BlockList.void_chest,1,openedAmount);
	}

	public void removePlayerFromOpenList(){
		--openedAmount;
		worldObj.addBlockEvent(getPos(),BlockList.void_chest,1,openedAmount);
	}

	public boolean canPlayerUse(EntityPlayer player){
		return worldObj.getTileEntity(getPos()) != this ? false : player.getDistanceSq(getPos().getX()+0.5D,getPos().getY()+0.5D,getPos().getZ()+0.5D) <= 64D;
	}
}
