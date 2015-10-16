package chylex.hee.tileentity;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import chylex.hee.block.BlockCorruptedEnergy;
import chylex.hee.init.BlockList;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.system.abstractions.facing.Facing6;

public class TileEntityEndPortalFrame extends TileEntity{
	private static final byte targetUnits = 100;
	
	private Pos clusterPos;
	private byte storedUnits = Byte.MIN_VALUE;
	
	@Override
	public void updateEntity(){
		if (storedUnits >= 0 && storedUnits < targetUnits){
			if (getClusterPos().getBlock(worldObj) == BlockList.energy_cluster){
				TileEntityEnergyCluster cluster = getClusterPos().getTileEntity(worldObj);
				
				if (storedUnits > 0 || (storedUnits == 0 && cluster.getData().get().getEnergyLevel() >= targetUnits)){
					if (cluster.getData().get().drainUnit()){
						if (++storedUnits == targetUnits){
							// TODO animate the process
							
							cluster.shouldNotExplode = true;
							getClusterPos().breakBlock(worldObj,false);
							activatePortal();
						}
					}
					else{
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
				pos1 = pos1.offset(facing).offset(facing.rotateRight());
				Pos pos2 = Pos.at(pos1).offset(facing,2).offset(facing.rotateLeft(),2);
				
				Pos.forEachBlock(pos1,pos2,pos -> {
					if (pos.checkBlock(worldObj,Blocks.end_portal,Meta.endPortalDisabled)){
						pos.setMetadata(worldObj,Meta.endPortalActive);
						((TileEntityEndPortalCustom)pos.getTileEntity(worldObj)).startAnimation();
					}
				});
				
				break;
			}
		}
	}
	
	private Pos getClusterPos(){
		return clusterPos == null ? (clusterPos = Pos.at(this).offset(Facing6.UP_POSY)) : clusterPos;
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
