package chylex.hee.game.save.types.player;
import java.util.Optional;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.item.ItemPortalToken;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.nbt.NBTCompound;

public class PortalFile extends PlayerFile{
	public static final int slots = 27;
	
	private Pos strongholdPos;
	private InventoryBasic inventory = new InventoryBasic("",false,slots);
	private ItemStack[] prevInventory = new ItemStack[slots];
	
	public PortalFile(String filename){
		super("portal",filename);
	}
	
	public void setStrongholdPos(Pos pos){
		this.strongholdPos = pos.immutable();
		setModified();
	}
	
	public Optional<Pos> getStrongholdPos(){
		return Optional.ofNullable(strongholdPos);
	}
	
	@Override
	public boolean wasModified(){
		boolean wasModified = false;
		
		for(int slot = 0; slot < inventory.getSizeInventory(); slot++){
			ItemStack is1 = inventory.getStackInSlot(slot);
			
			if (is1 != null && ItemPortalToken.isExpired(is1)){
				inventory.setInventorySlotContents(slot,is1 = null);
			}
			
			if (!ItemStack.areItemStacksEqual(is1,prevInventory[slot])){
				wasModified = true;
				prevInventory[slot] = is1 == null ? null : is1.copy();
			}
		}
		
		return wasModified || super.wasModified();
	}
	
	public InventoryBasic getTokenInventory(){
		return inventory;
	}

	@Override
	protected void onSave(NBTCompound nbt){
		if (strongholdPos != null)nbt.setLong("stronghold",strongholdPos.toLong());
		nbt.writeInventory("inv",inventory);
	}

	@Override
	protected void onLoad(NBTCompound nbt){
		strongholdPos = nbt.hasKey("stronghold") ? Pos.at(nbt.getLong("stronghold")) : null;
		nbt.readInventory("inv",inventory);
		wasModified();
	}
}
