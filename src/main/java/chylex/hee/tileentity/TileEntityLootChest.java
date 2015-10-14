package chylex.hee.tileentity;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.Constants.NBT;
import chylex.hee.game.save.handlers.PlayerDataHandler;
import chylex.hee.gui.InventoryLootChest;
import chylex.hee.init.BlockList;
import chylex.hee.system.util.MathUtil;
import chylex.hee.system.util.NBTUtil;

public class TileEntityLootChest extends TileEntity{
	private final InventoryLootChest sourceInventory;
	private final Map<String,InventoryLootChest> inventories;
	
	public float lidAnim;
	public float prevLidAnim;
	public int openedAmount;
	private int ticksExisted;
	private String customName;
	
	public TileEntityLootChest(){
		sourceInventory = new InventoryLootChest(this);
		inventories = new HashMap<>();
	}
	
	@Override
	public void updateEntity(){
		super.updateEntity();

		if (++ticksExisted%80 == 0)worldObj.addBlockEvent(xCoord,yCoord,zCoord,BlockList.loot_chest,1,openedAmount);

		prevLidAnim = lidAnim;

		if (openedAmount > 0 && lidAnim == 0F){
			worldObj.playSoundEffect(xCoord+0.5D,yCoord+0.5D,zCoord+0.5D,"random.chestopen",0.5F,worldObj.rand.nextFloat()*0.1F+0.9F);
		}

		if (openedAmount == 0 && lidAnim > 0F || openedAmount > 0 && lidAnim < 1F){
			float oldAnim = lidAnim;
			lidAnim = MathUtil.clamp(openedAmount > 0 ? lidAnim+0.1F : lidAnim-0.1F,0F,1F);

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
	
	public InventoryLootChest getInventoryFor(EntityPlayer player){
		return player.capabilities.isCreativeMode ? sourceInventory : inventories.computeIfAbsent(PlayerDataHandler.getID(player),id -> new InventoryLootChest(id,this,sourceInventory));
	}
	
	@Override
	public Packet getDescriptionPacket(){
		NBTTagCompound packetTag = new NBTTagCompound();
		if (customName != null)packetTag.setString("customName",customName);
		return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,0,packetTag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet){
		NBTTagCompound packetTag = packet.func_148857_g(); // OBFUSCATED get tag data
		customName = packetTag.hasKey("customName") ? packetTag.getString("customName") : null;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setTag("sourceInv",NBTUtil.writeInventory(sourceInventory));
		
		NBTTagCompound playerTag = new NBTTagCompound();
		for(Entry<String,InventoryLootChest> entry:inventories.entrySet())playerTag.setTag(entry.getKey(),NBTUtil.writeInventory(entry.getValue()));
		nbt.setTag("playerInv",playerTag);
		
		if (customName != null)nbt.setString("customName",customName);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		NBTUtil.readInventory(nbt.getTagList("sourceInv",NBT.TAG_COMPOUND),sourceInventory);
		
		NBTTagCompound playerTag = nbt.getCompoundTag("playerInv");
		
		for(String id:(Set<String>)playerTag.func_150296_c()){
			InventoryLootChest inv = new InventoryLootChest(id,this);
			
			NBTUtil.readInventory(playerTag.getTagList(id,NBT.TAG_COMPOUND),inv);
			inventories.put(id,inv);
		}
		
		if (nbt.hasKey("customName"))customName = nbt.getString("customName");
	}

	public void addPlayerToOpenList(){
		++openedAmount;
		worldObj.addBlockEvent(xCoord,yCoord,zCoord,BlockList.loot_chest,1,openedAmount);
	}

	public void removePlayerFromOpenList(){
		--openedAmount;
		worldObj.addBlockEvent(xCoord,yCoord,zCoord,BlockList.loot_chest,1,openedAmount);
	}

	public boolean canPlayerUse(EntityPlayer player){
		return worldObj.getTileEntity(xCoord,yCoord,zCoord) != this ? false : player.getDistanceSq(xCoord+0.5D,yCoord+0.5D,zCoord+0.5D) <= 64D;
	}
	
	public void setCustomInventoryName(String customName){
		this.customName = customName;
	}
	
	public String getInventoryNameIfPresent(){ // TODO test
		return customName;
	}
}
