package chylex.hee.entity.item;
/*import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import chylex.hee.block.BlockInfestationRemedyCauldron.CauldronState;
import chylex.hee.block.BlockList;
import chylex.hee.item.ItemList;
import chylex.hee.mechanics.knowledge.KnowledgeRegistrations;
import chylex.hee.mechanics.knowledge.util.ObservationUtil;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C24InfestationCauldronBubbleEffect;

public class EntityItemInfestationCauldron extends EntityItem{
	public EntityItemInfestationCauldron(World world){
		super(world);
	}
	
	public EntityItemInfestationCauldron(World world, double x, double y, double z, ItemStack is){
		super(world,x,y,z,is);
	}
	
	@Override
	public void onUpdate(){
		super.onUpdate();
		
		if (worldObj.isRemote)return;
		
		int ix = (int)Math.floor(posX),iy = (int)Math.floor(posY),iz = (int)Math.floor(posZ);
		Block block = worldObj.getBlock(ix,iy,iz);
		
		if (block == Blocks.cauldron){
			if (worldObj.getBlockMetadata(ix,iy,iz) != 3 || worldObj.getBlock(ix,iy-1,iz) != Blocks.fire)return;
			
			Item item = getEntityItem().getItem();
			if (item == ItemList.silverfish_blood)worldObj.setBlock(ix,iy,iz,BlockList.infestation_cauldron,CauldronState.HAS_SILVERFISH_BLOOD.metadata,3);
			else if (item == ItemList.infested_bat_wing)worldObj.setBlock(ix,iy,iz,BlockList.infestation_cauldron,CauldronState.HAS_BAT_WING.metadata,3);
			else if (item == ItemList.dry_splinter)worldObj.setBlock(ix,iy,iz,BlockList.infestation_cauldron,CauldronState.HAS_DRY_SPLINTER.metadata,3);
			else return;
			
			bubble(ix,iy,iz);
			setDead();
		}
		else if (block == BlockList.infestation_cauldron){
			CauldronState newState = CauldronState.getByMeta(worldObj.getBlockMetadata(ix,iy,iz)).tryAddItem(getEntityItem().getItem());
			if (newState != null){
				worldObj.setBlockMetadataWithNotify(ix,iy,iz,newState.metadata,3);
				bubble(ix,iy,iz);
				setDead();
			}
		}
	}
	
	private void bubble(int ix, int iy, int iz){
		PacketPipeline.sendToAllAround(this,64D,new C24InfestationCauldronBubbleEffect(ix,iy,iz));
		
		for(EntityPlayer observer:ObservationUtil.getAllObservers(worldObj,ix,iy,iz,6D)){
			KnowledgeRegistrations.INFESTATION_REMEDY.tryUnlockFragment(observer,0.43F);
		}
	}
}
*/