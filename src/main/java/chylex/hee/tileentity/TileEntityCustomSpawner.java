package chylex.hee.tileentity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import chylex.hee.system.logging.Log;
import chylex.hee.tileentity.spawner.BlobEndermanSpawnerLogic;
import chylex.hee.tileentity.spawner.CustomSpawnerLogic;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic;
import chylex.hee.tileentity.spawner.SilverfishDungeonSpawnerLogic;
import chylex.hee.tileentity.spawner.SilverfishRavagedSpawnerLogic;
import chylex.hee.tileentity.spawner.TowerEndermanSpawnerLogic;

public class TileEntityCustomSpawner extends TileEntity implements IUpdatePlayerListBox{
	private byte logicId;
	private CustomSpawnerLogic logic;
	private long cachedPos = Long.MIN_VALUE;

	public TileEntityCustomSpawner setLogicId(int id){
		createLogic((byte)id);
		return this;
	}
	
	private void createLogic(byte id){
		switch(id){
			case 0: logic = new TowerEndermanSpawnerLogic(this); break;
			case 1: logic = new SilverfishDungeonSpawnerLogic(this); break;
			case 2: logic = new LouseRavagedSpawnerLogic(this); break;
			case 3: logic = new SilverfishRavagedSpawnerLogic(this); break;
			case 4: logic = new BlobEndermanSpawnerLogic(this); break;
			default:
				Log.error("Unable to find spawner logic $0, this is not supposed to happen! Substituting empty logic to prevent crashes.",id);
				logic = new CustomSpawnerLogic.BrokenSpawnerLogic(this);
		}
		
		this.logicId = id;
	}
	
	@Override
	public void update(){
		if (cachedPos == Long.MIN_VALUE)cachedPos = getPos().toLong();
		else if (cachedPos != getPos().toLong())logic.updateSpawner();
	}

	@Override
	public Packet getDescriptionPacket(){
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		tag.removeTag("SpawnPotentials");
		tag.removeTag("actualPos");
		return new S35PacketUpdateTileEntity(getPos(),1,tag);
	}

	@Override
	public boolean receiveClientEvent(int eventNb, int arg){
		return logic.setDelayToMin(eventNb) || super.receiveClientEvent(eventNb,arg);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setByte("logicId",logicId);
		nbt.setLong("actualPosL",cachedPos);
		logic.writeToNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		createLogic(nbt.getByte("logicId"));
		
		if (nbt.hasKey("actualPosL"))cachedPos = nbt.getLong("actualPosL");
		else{
			int[] cached = nbt.getIntArray("actualPos");
			if (cached.length == 3)cachedPos = new BlockPos(cached[0],cached[1],cached[2]).toLong();
		}
		
		logic.readFromNBT(nbt);
	}

	public CustomSpawnerLogic getSpawnerLogic(){
		return logic;
	}
}