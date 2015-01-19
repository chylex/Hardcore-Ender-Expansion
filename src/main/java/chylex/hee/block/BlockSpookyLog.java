package chylex.hee.block;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import chylex.hee.entity.fx.FXType;
import chylex.hee.entity.mob.EntityMobForestGhost;
import chylex.hee.item.ItemList;
import chylex.hee.packets.PacketPipeline;
import chylex.hee.packets.client.C08PlaySound;
import chylex.hee.packets.client.C20Effect;
import chylex.hee.system.util.BlockPosM;
import chylex.hee.system.util.DragonUtil;
import chylex.hee.system.util.MathUtil;

public class BlockSpookyLog extends Block{
	public BlockSpookyLog(){
		super(Material.wood);
		setTickRandomly(true);
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z){
		world.scheduleUpdate(pos,this,tickRate(world));
	}

	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float chance, int fortune){
		super.dropBlockAsItemWithChance(world,x,y,z,meta,chance,fortune);
		
		if (meta > 0 && !world.isRemote && world.rand.nextInt(4) == 0){
			EntityPlayer closest = null;
			double curDist = 8D;
			
			for(EntityPlayer player:(List<EntityPlayer>)world.playerEntities){
				double dist = MathUtil.distance(player.posX-x,player.posZ-z);
				
				if (dist < curDist){
					dist = curDist;
					closest = player;
				}
			}
			
			if (closest != null){
				boolean foundAmulet = false;
				ItemStack is;
				
				for(int a = 0; a < closest.inventory.getSizeInventory(); a++){
					if ((is = closest.inventory.getStackInSlot(a)) == null)continue;
					
					if (is.getItem() == ItemList.ghost_amulet && is.getItemDamage() == 1){
						if (world.rand.nextInt(5) <= 1)dropBlockAsItem(world,x,y,z,new ItemStack(ItemList.ectoplasm));
						
						PacketPipeline.sendToPlayer(closest,new C08PlaySound(C08PlaySound.GHOST_DEATH,closest.posX,closest.posY,closest.posZ,1.8F,0.9F+world.rand.nextFloat()*0.3F));
						
						foundAmulet = true;
						break;
					}
				}
				
				if (!foundAmulet){
					double ang = Math.toRadians(closest.rotationYaw+270D);
					EntityMobForestGhost ghost = new EntityMobForestGhost(world,closest);
					ghost.setLocationAndAngles(closest.posX+Math.cos(ang)*3D,closest.posY,closest.posZ+Math.sin(ang)*3D,0F,0F);
					world.spawnEntityInWorld(ghost);
					
					PacketPipeline.sendToPlayer(closest,new C08PlaySound(C08PlaySound.GHOST_SPAWN,closest.posX,closest.posY,closest.posZ,1.4F,1F));
				}
			}
		}
		
		if (!world.isRemote && world.rand.nextInt(8) == 0)dropBlockAsItem(world,x,y,z,new ItemStack(ItemList.dry_splinter));

		if (world.getBlock(x,y+1,z) == this){
			dropBlockAsItemWithChance(world,x,y+1,z,world.getBlockMetadata(x,y+1,z),chance,fortune);
			PacketPipeline.sendToAllAround(world.provider.getDimensionId(),x+0.5D,y+0.5D,z+0.5D,64D,new C20Effect(FXType.Basic.SPOOKY_LOG_DECAY,x,y,z));
			world.setBlockToAir(x,y+1,z);
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random rand){
		if (world.isRemote || world.getBlockMetadata(x,y,z) == 0)return;
		
		if (rand.nextInt(4) == 0 && !isBlockSeen(world,x,y,z)){
			int w = rand.nextInt(5); // 0,1 = rotate, 2 = move up or down, 3,4 = move tree
			boolean moved = false;
			
			if (w == 0 || w == 1){
				world.setBlockMetadataWithNotify(x,y,z,rand.nextInt(4)+1,3);
				world.scheduleUpdate(pos,this,tickRate(world));
				moved = true;
			}
			else if (w == 2){
				int dir = rand.nextInt(2)*2-1;
				
				if (world.getBlock(x,y+dir,z) == this){
					world.setBlockMetadataWithNotify(x,y+dir,z,world.getBlockMetadata(x,y,z),3);
					world.setBlockMetadataWithNotify(x,y,z,0,3);
					world.scheduleBlockUpdate(x,y+dir,z,this,tickRate(world));
					moved = true;
				}
			}
			else{
				for(int attempt = 0, xx, zz; attempt < 50; attempt++){
					xx = x+rand.nextInt(31)-15;
					zz = z+rand.nextInt(31)-15;
					
					if (world.getBlock(xx,y,zz) == this){
						boolean hasFace = false;
						
						for(int testY = y; true; testY++){
							if (world.getBlock(xx,testY,zz) != this)break;
							if (world.getBlockMetadata(xx,testY,zz) > 0){
								hasFace = true;
								break;
							}
						}
						
						for(int testY = y; true; testY--){
							if (world.getBlock(xx,testY,zz) != this)break;
							if (world.getBlockMetadata(xx,testY,zz) > 0){
								hasFace = true;
								break;
							}
						}
						
						if (!hasFace && !isBlockSeen(world,xx,y,zz)){
							world.setBlockMetadataWithNotify(xx,y,zz,world.getBlockMetadata(x,y,z),3);
							world.setBlockMetadataWithNotify(x,y,z,0,3);
							world.scheduleBlockUpdate(xx,y,zz,this,tickRate(world));
							moved = true;
						}
					}
				}
			}
			
			if (moved){
				if (rand.nextInt(32) == 0)PacketPipeline.sendToAllAround(world.provider.getDimensionId(),x+0.5D,y+0.5D,z+0.5D,64D,new C08PlaySound(C08PlaySound.GHOST_MOVE,x+0.5D,y+0.5D,z+0.5D,1.2F+rand.nextFloat()*0.4F,0.6F+rand.nextFloat()*0.5F));
				return;
			}
			
			world.scheduleUpdate(pos,this,tickRate(world));
		}
		else world.scheduleUpdate(pos,this,8);
	}
	
	private boolean isBlockSeen(World world, int x, int y, int z){
		for(EntityPlayer entity:(List<EntityPlayer>)world.playerEntities){
			if (Math.abs(entity.posX-x) > 250 || Math.abs(entity.posZ-z) > 250)continue;
			if (DragonUtil.canEntitySeePoint(entity,x,y,z,0.5D))return true;
		}
		
		return false;
	}
	
	@Override
	public int tickRate(World world){
		return 60;
	}

	@Override
	public Item getItemDropped(int meta, Random rand, int fortune){
		return Items.stick;
	}
	
	@Override
	public int quantityDropped(Random rand){
		return rand.nextInt(4);
	}
	
	@Override
	public boolean canSustainLeaves(IBlockAccess world, int x, int y, int z){
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean addHitEffects(World world, MovingObjectPosition target, EffectRenderer effectRenderer){
		BlockPosM pos = new BlockPosM(target.getBlockPos());
		
		for(int a = 0; a < 30; a++){	
			effectRenderer.addEffect(new EntityDiggingFX(world,pos.x+world.rand.nextFloat(),pos.y+world.rand.nextFloat(),pos.z+world.rand.nextFloat(),world.rand.nextFloat()-0.5F,0D,world.rand.nextFloat()-0.5F,this,0).applyColourMultiplier(x,y,z).multiplyVelocity(0.3F+world.rand.nextFloat()*0.6F).multipleParticleScaleBy(0.2F+world.rand.nextFloat()*2F));
		}
		
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer){
		for(int a = 0; a < 90; a++){
			effectRenderer.addEffect(new EntityDiggingFX(world,x+world.rand.nextFloat(),y+world.rand.nextFloat()*1.5F,z+world.rand.nextFloat(),world.rand.nextFloat()-0.5F,0D,world.rand.nextFloat()-0.5F,this,0).applyColourMultiplier(x,y,z).multiplyVelocity(0.1F+world.rand.nextFloat()*0.2F).multipleParticleScaleBy(world.rand.nextFloat()*2.2F));
		}
		
		return false;
	}
	
	@SideOnly(Side.CLIENT)
	public boolean addDestroyEffectsCustom(World world, int x, int y, int z){
		EffectRenderer eff = Minecraft.getMinecraft().effectRenderer;
		for(int a = 0; a < 30; a++){
			eff.addEffect(new EntityDiggingFX(world,x+world.rand.nextFloat(),y+world.rand.nextFloat()*1.5F,z+world.rand.nextFloat(),world.rand.nextFloat()-0.5F,0D,world.rand.nextFloat()-0.5F,this,0).applyColourMultiplier(x,y,z).multiplyVelocity(0.1F+world.rand.nextFloat()*0.2F).multipleParticleScaleBy(world.rand.nextFloat()*2.2F));
		}
		
		return false;
	}
}
