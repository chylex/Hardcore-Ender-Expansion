package chylex.hee.game.save.types.player;
import java.util.Optional;
import net.minecraft.inventory.InventoryBasic;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.nbt.NBTCompound;

public class PortalFile extends PlayerFile{
	private Pos strongholdPos;
	private InventoryBasic inventory = new InventoryBasic("",false,27);
	
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
	
	public void onTokenInventoryUpdated(){
		setModified();
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
	}
}
