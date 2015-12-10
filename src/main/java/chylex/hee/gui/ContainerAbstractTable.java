package chylex.hee.gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;
import chylex.hee.gui.helpers.ContainerHelper;
import chylex.hee.system.util.MathUtil;
import chylex.hee.tileentity.TileEntityAbstractTable;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class ContainerAbstractTable extends Container{
	protected final TileEntityAbstractTable table;
	private int prevReqStardust, prevTime;
	private float prevStoredEnergy;
	
	public ContainerAbstractTable(InventoryPlayer inv, TileEntityAbstractTable table){
		this.table = table;
		registerSlots();
		ContainerHelper.addPlayerInventorySlots(this,inv,0,0);
	}
	
	protected abstract void registerSlots();
	
	@Override
	public void addCraftingToCrafters(ICrafting crafter){
		super.addCraftingToCrafters(crafter);
		crafter.sendProgressBarUpdate(this,0,table.getRequiredStardust());
		crafter.sendProgressBarUpdate(this,1,table.getTime());
		crafter.sendProgressBarUpdate(this,2,Float.floatToIntBits(table.getStoredEnergy()));
	}

	@Override
	public void detectAndSendChanges(){
		super.detectAndSendChanges();

		for(int i = 0; i < crafters.size(); i++){
			ICrafting crafter = (ICrafting)crafters.get(i);
			if (prevReqStardust != table.getRequiredStardust())crafter.sendProgressBarUpdate(this,0,table.getRequiredStardust());
			if (prevTime != table.getTime())crafter.sendProgressBarUpdate(this,1,table.getTime());
			if (!MathUtil.floatEquals(prevStoredEnergy,table.getStoredEnergy()))crafter.sendProgressBarUpdate(this,2,Float.floatToIntBits(table.getStoredEnergy()));
		}

		prevReqStardust = table.getRequiredStardust();
		prevTime = table.getTime();
		prevStoredEnergy = table.getStoredEnergy();
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotId){
		return ContainerHelper.transferStack(this,this::mergeItemStack,table.getSizeInventory(),slotId); // TODO test
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int value){
		if (id == 0)table.setRequiredStardustClient(value);
		else if (id == 1)table.setTimeClient(value);
		else if (id == 2)table.setStoredEnergyClient(Float.intBitsToFloat(value));
	}
	
	@Override
	public final boolean canInteractWith(EntityPlayer player){
		return table.isUseableByPlayer(player);
	}
}
