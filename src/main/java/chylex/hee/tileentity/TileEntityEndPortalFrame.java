package chylex.hee.tileentity;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import chylex.hee.HardcoreEnderExpansion;
import chylex.hee.block.BlockCorruptedEnergy;
import chylex.hee.init.BlockList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C04EndPortalAnimation;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.abstractions.facing.Facing6;

public class TileEntityEndPortalFrame extends TileEntity{
	private static final byte targetUnits = 100;
	
	private Pos clusterPos;
	private byte storedUnits = Byte.MIN_VALUE;
	private boolean isAnimating;
	
	@Override
	public void updateEntity(){
		if (worldObj.isRemote){
			if (isAnimating){
				if (getClusterPos().getBlock(worldObj) == BlockList.energy_cluster){
					Random rand = worldObj.rand;
					TileEntityEnergyCluster cluster = getClusterPos().getTileEntity(worldObj);
					
					double x = cluster.xCoord+0.5D+(rand.nextDouble()-0.5D)*0.1D;
					double y = cluster.yCoord+0.5D+(rand.nextDouble()-0.5D)*0.1D;
					double z = cluster.zCoord+0.5D+(rand.nextDouble()-0.5D)*0.1D;
					HardcoreEnderExpansion.fx.energy(x,y,z,xCoord+0.5D,yCoord+0.5D,zCoord+0.5D,cluster.getColor(0),cluster.getColor(1),cluster.getColor(2),0.1F+rand.nextFloat()*0.1F,0.1F);
				}
				else isAnimating = false;
			}
			
			return;
		}
		
		if (storedUnits >= 0 && storedUnits < targetUnits){
			if (getClusterPos().getBlock(worldObj) == BlockList.energy_cluster){
				TileEntityEnergyCluster cluster = getClusterPos().getTileEntity(worldObj);
				
				if (storedUnits > 0 || (storedUnits == 0 && cluster.getData().get().getEnergyUnits() >= targetUnits)){
					if (cluster.getData().get().drainUnit()){
						startAnimating();
						
						if (++storedUnits == targetUnits){
							isAnimating = false;
							cluster.shouldNotExplode = true;
							getClusterPos().breakBlock(worldObj,false);
							activatePortal();
						}
					}
					else{
						isAnimating = false;
						cluster.shouldNotExplode = true;
						getClusterPos().breakBlock(worldObj,false);
					}
				}
			}
			else{
				Pos testPos = getClusterPos();
				int attempts = 10;
				
				do{
					if (testPos.isAir(worldObj)){
						testPos.setBlock(worldObj,BlockCorruptedEnergy.getCorruptedEnergy(3+storedUnits/15));
						break;
					}
					else testPos = Pos.at(this).offset(worldObj.rand.nextInt(3)-1,worldObj.rand.nextInt(3)-1,worldObj.rand.nextInt(3)-1);
				}while(--attempts > 0);
				
				storedUnits = Byte.MIN_VALUE;
			}
		}
	}
	
	public void onNeighborClusterUpdate(){
		if (getClusterPos().getBlock(worldObj) == BlockList.energy_cluster && storedUnits == Byte.MIN_VALUE){
			storedUnits = 0;
		}
	}
	
	private void activatePortal(){
		Pos pos1 = Pos.at(this);
		
		for(Facing4 facing:Facing4.list){
			if (pos1.offset(facing).checkBlock(worldObj,Blocks.end_portal,Meta.endPortalDisabled)){
				PacketPipeline.sendToAllAround(this,64D,new C04EndPortalAnimation(pos1.offset(facing,2)));
				
				for(Pos framePos:new Pos[]{
					pos1.offset(facing,4), pos1.offset(facing,2).offset(facing.rotateLeft(),2), pos1.offset(facing,2).offset(facing.rotateRight(),2)
				}){
					framePos.castTileEntity(worldObj,TileEntityEndPortalFrame.class).ifPresent(frame -> frame.storedUnits = targetUnits);
				}
				
				pos1 = pos1.offset(facing).offset(facing.rotateRight());
				Pos pos2 = Pos.at(pos1).offset(facing,2).offset(facing.rotateLeft(),2);
				
				Pos.forEachBlock(pos1,pos2,pos -> {
					if (pos.checkBlock(worldObj,Blocks.end_portal,Meta.endPortalDisabled)){
						((TileEntityEndPortalCustom)pos.getTileEntity(worldObj)).startActivating();
					}
				});
				
				break;
			}
		}
	}
	
	private Pos getClusterPos(){
		return clusterPos == null ? (clusterPos = Pos.at(this).offset(Facing6.UP_POSY)) : clusterPos;
	}
	
	private void startAnimating(){
		if (!isAnimating){
			isAnimating = true;
			worldObj.markBlockForUpdate(xCoord,yCoord,zCoord); // setting to false is done on client side
		}
	}
	
	@Override
	public Packet getDescriptionPacket(){
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setBoolean("anim",isAnimating);
		return new S35PacketUpdateTileEntity(xCoord,yCoord,zCoord,0,nbt);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet){
		isAnimating = packet.func_148857_g().getBoolean("anim"); // OBFUSCATED get tag data
		worldObj.markBlockRangeForRenderUpdate(xCoord,yCoord,zCoord,xCoord,yCoord,zCoord);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if (storedUnits != Byte.MIN_VALUE)nbt.setByte("energy",storedUnits);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		if (nbt.hasKey("energy"))storedUnits = nbt.getByte("energy");
	}
}
