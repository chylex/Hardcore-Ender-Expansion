package chylex.hee.tileentity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import chylex.hee.tileentity.spawner.CustomSpawnerLogic;
import chylex.hee.tileentity.spawner.LouseRavagedSpawnerLogic;
import chylex.hee.tileentity.spawner.SilverfishDungeonSpawnerLogic;
import chylex.hee.tileentity.spawner.SilverfishRavagedSpawnerLogic;
import chylex.hee.tileentity.spawner.TowerEndermanSpawnerLogic;

public class TileEntityCustomSpawner extends TileEntity{
	private byte logicId;
	private CustomSpawnerLogic logic;

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
			default: throw new IllegalArgumentException("Unable to find spawner logic "+id+", this is not supposed to happen! Shutting down Minecraft to prevent more errors...");
		}
		this.logicId = id;
	}
	
	@Override
	public void updateEntity(){
		logic.updateSpawner();
		super.updateEntity();
	}

	@Override
	public Packet getDescriptionPacket(){
		NBTTagCompound tag = new NBTTagCompound();
		writeToNBT(tag);
		tag.removeTag("SpawnPotentials");
		return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,1,tag);
	}

	@Override
	public boolean receiveClientEvent(int eventNb, int arg){
		return logic.setDelayToMin(eventNb)?true:super.receiveClientEvent(eventNb,arg);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setByte("logicId",logicId);
		logic.writeToNBT(nbt);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		createLogic(nbt.getByte("logicId"));
		logic.readFromNBT(nbt);
	}

	public CustomSpawnerLogic getSpawnerLogic(){
		return logic;
	}
}