package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import chylex.hee.tileentity.TileEntityAbstractTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ContainerAbstractTable extends Container{
	private final TileEntityAbstractTable table;
	private int prevReqStardust, prevTime;
	
	public ContainerAbstractTable(InventoryPlayer inv, TileEntityAbstractTable table){
		this.table = table;
		
		registerSlots(table);
		
		for(int i = 0; i < 3; ++i){
			for(int j = 0; j < 9; ++j)addSlotToContainer(new Slot(inv,j+i*9+9,8+j*18,84+i*18));
		}

		for(int i = 0; i < 9; ++i)addSlotToContainer(new Slot(inv,i,8+i*18,142));
	}
	
	protected abstract void registerSlots(TileEntityAbstractTable table);
	
	@Override
	public void addCraftingToCrafters(ICrafting crafter){
		super.addCraftingToCrafters(crafter);
		crafter.sendProgressBarUpdate(this,0,table.getRequiredStardust());
		crafter.sendProgressBarUpdate(this,1,table.getTime());
	}

	@Override
	public void detectAndSendChanges(){
		super.detectAndSendChanges();

		for(int i = 0; i < crafters.size(); ++i){
			ICrafting crafter = (ICrafting)crafters.get(i);
			if (prevReqStardust != table.getRequiredStardust())crafter.sendProgressBarUpdate(this,0,table.getRequiredStardust());
			if (prevTime != table.getTime())crafter.sendProgressBarUpdate(this,1,table.getTime());
		}

		prevReqStardust = table.getRequiredStardust();
		prevTime = table.getTime();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value){
		if (id == 0)table.setRequiredStardust(value);
		else if (id == 1)table.setTime(value);
	}
	
	@Override
	public final boolean canInteractWith(EntityPlayer player){
		return table.isUseableByPlayer(player);
	}
}
