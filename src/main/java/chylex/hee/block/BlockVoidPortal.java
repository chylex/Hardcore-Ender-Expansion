package chylex.hee.block;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.entity.technical.EntityTechnicalVoidPortal;
import chylex.hee.item.ItemPortalToken;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.entity.EntitySelector;
import chylex.hee.system.collections.CollectionUtil;
import chylex.hee.tileentity.TileEntityVoidPortal;
import chylex.hee.world.end.EndTerritory;
import chylex.hee.world.util.EntityPortalStatus;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockVoidPortal extends BlockEndPortal{
	public static Optional<EntityTechnicalVoidPortal> getData(World world, int x, int y, int z){
		return CollectionUtil.get(EntitySelector.type(world,EntityTechnicalVoidPortal.class,AxisAlignedBB.getBoundingBox(x-4.5D,y-1D,z-4.5D,x+5.5D,y+1D,z+5.5D)),0);
	}
	
	private final EntityPortalStatus portalStatus = new EntityPortalStatus();
	
	public BlockVoidPortal(){
		super(Material.portal);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityVoidPortal();
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if (entity.posY <= y+0.05D && entity instanceof EntityPlayerMP){
			Pos pos = Pos.at(x,y,z);
			int meta = pos.getMetadata(world);
			EntityPlayerMP player = (EntityPlayerMP)entity;
			
			if (portalStatus.onTouch(player)){
				if (pos.getMetadata(world) == Meta.voidPortalReturn){
					// TODO
				}
				else{
					ItemStack tokenIS = getData(world,x,y,z).map(data -> data.getActiveToken()).orElse(null);
					if (tokenIS == null)return;
					
					EndTerritory territory = ItemPortalToken.getTerritory(tokenIS);
					if (territory == null || !territory.canGenerate())return;
					
					// TODO
				}
			}
		}
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB checkAABB, List list, Entity entity){
		AxisAlignedBB collisionBox = AxisAlignedBB.getBoundingBox(x,y,z,x+1D,y+0.025D,z+1D);
		if (checkAABB.intersectsWith(collisionBox))list.add(collisionBox);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z){}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){}
}
