package chylex.hee.block.override;
import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockEndPortal;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import chylex.hee.game.save.SaveData;
import chylex.hee.game.save.types.player.StrongholdPortalFile;
import chylex.hee.mechanics.causatum.Causatum;
import chylex.hee.mechanics.causatum.Causatum.Progress;
import chylex.hee.system.abstractions.Meta;
import chylex.hee.system.abstractions.Pos;
import chylex.hee.system.abstractions.Pos.PosMutable;
import chylex.hee.system.abstractions.facing.Facing4;
import chylex.hee.tileentity.TileEntityEndPortalCustom;
import chylex.hee.world.TeleportHandler;
import chylex.hee.world.util.EntityPortalStatus;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockEndPortalCustom extends BlockEndPortal{
	private final EntityPortalStatus portalStatus = new EntityPortalStatus();
	
	public BlockEndPortalCustom(){
		super(Material.portal);
		setBlockUnbreakable().setResistance(6000000F);
		setTickRandomly(true);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta){
		return new TileEntityEndPortalCustom();
	}
	
	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
		Pos pos = Pos.at(x,y,z);
		int meta = pos.getMetadata(world);
		
		if (meta != Meta.endPortalActive && meta != Meta.endPortalDisabled)pos.setAir(world);
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity){
		if (entity.posY <= y+0.05D && entity instanceof EntityPlayerMP){
			Pos pos = Pos.at(x,y,z);
			int meta = pos.getMetadata(world);
			EntityPlayerMP player = (EntityPlayerMP)entity;
			
			if (meta == Meta.endPortalActive){
				if (portalStatus.onTouch(player)){
					if (world.provider.dimensionId == 0){
						SaveData.player(player,StrongholdPortalFile.class).setPortalPos(findCenterPortalBlock(world,pos));
						Causatum.progress(player,Progress.INTO_THE_END);
						TeleportHandler.toEnd(player);
					}
					else TeleportHandler.toOverworld(player);
				}
			}
			else if (meta != Meta.endPortalDisabled){
				pos.setAir(world);
				return;
			}
		}
	}
	
	@Override
	public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int meta){
		return Meta.endPortalActive;
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		world.scheduleBlockUpdate(x,y,z,this,1);
	}
	
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB checkAABB, List list, Entity entity){
		AxisAlignedBB collisionBox = AxisAlignedBB.getBoundingBox(x,y,z,x+1D,y+0.025D,z+1D);
		if (checkAABB.intersectsWith(collisionBox))list.add(collisionBox);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random rand){
		if (Pos.at(x,y,z).getMetadata(world) == Meta.endPortalActive && rand.nextInt(7) == 0){
	        world.spawnParticle("smoke",x+rand.nextDouble(),y+0.25D,z+rand.nextDouble(),0D,0D,0D);
		}
	}
	
	private Pos findCenterPortalBlock(World world, Pos anyPos){
		PosMutable pos1 = new PosMutable(anyPos), pos2 = new PosMutable(anyPos);
		
		while(pos1.move(Facing4.NORTH_NEGZ).getBlock(world) == this);
		pos1.move(Facing4.SOUTH_POSZ);
		while(pos1.move(Facing4.WEST_NEGX).getBlock(world) == this);
		pos1.move(Facing4.EAST_POSX);
		
		while(pos2.move(Facing4.SOUTH_POSZ).getBlock(world) == this);
		pos2.move(Facing4.NORTH_NEGZ);
		while(pos2.move(Facing4.EAST_POSX).getBlock(world) == this);
		pos2.move(Facing4.WEST_NEGX);
		
		return pos1.offset((pos2.getX()-pos1.getX())/2,0,(pos2.getZ()-pos1.getZ())/2);
	}
}
