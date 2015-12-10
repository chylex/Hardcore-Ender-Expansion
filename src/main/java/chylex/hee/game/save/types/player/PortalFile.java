package chylex.hee.game.save.types.player;
import java.util.Optional;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.game.save.types.PlayerFile;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.util.NBTUtil;

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
	protected void onSave(NBTTagCompound nbt){
		nbt.setLong("stronghold",strongholdPos.toLong());
		nbt.setTag("inv",NBTUtil.writeInventory(inventory));
	}

	@Override
	protected void onLoad(NBTTagCompound nbt){
		strongholdPos = nbt.hasKey("stronghold") ? Pos.at(nbt.getLong("stronghold")) : null;
		NBTUtil.readInventory(nbt.getTagList("inv",NBT.TAG_COMPOUND),inventory);
	}
}
