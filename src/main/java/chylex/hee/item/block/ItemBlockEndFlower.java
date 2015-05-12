package chylex.hee.item.block;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import chylex.hee.init.BlockList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.server.S00DeathFlowerPot;
import chylex.hee.system.util.BlockPosM;

public class ItemBlockEndFlower extends ItemBlock{
	public ItemBlockEndFlower(Block block){
		super(block);
		setHasSubtypes(true);
	}
	
	@Override
	public IIcon getIconFromDamage(int damage){
		return BlockList.death_flower.getIcon(2,damage);
	}
	
	@Override
	public int getMetadata(int damage){
		return damage;
	}

	@Override
	public boolean onItemUseFirst(ItemStack is, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ){
		if (world.isRemote && BlockPosM.tmp(x,y,z).checkBlock(world,Blocks.flower_pot,0)){
			PacketPipeline.sendToServer(new S00DeathFlowerPot(BlockPosM.tmp(x,y,z)));
			return true;
		}
		
		return super.onItemUseFirst(is,player,world,x,y,z,side,hitX,hitY,hitZ);
	}
}
