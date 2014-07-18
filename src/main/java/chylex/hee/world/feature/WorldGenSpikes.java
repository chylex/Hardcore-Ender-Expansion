package chylex.hee.world.feature;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import chylex.hee.block.BlockList;
import chylex.hee.entity.block.EntityBlockEnderCrystal;
import chylex.hee.system.util.MathUtil;
import chylex.hee.world.biome.BiomeDecoratorHardcoreEnd;

public class WorldGenSpikes extends WorldGenerator{
	private static Block surfaceID = Blocks.end_stone;
	
	@Override
	public boolean generate(World world, Random rand, int x, int y, int z){
		if (world.isAirBlock(x,y,z) && world.getBlock(x,y-1,z) == surfaceID){
			int height = rand.nextInt(32)+6;
			int radius = rand.nextInt(4)+1;

			for(int xx = x-radius; xx <= x+radius; ++xx){
				for(int zz = z-radius; zz <= z+radius; ++zz){
					if (MathUtil.square(xx-x)+MathUtil.square(zz-z) <= radius*radius+1 && world.getBlock(xx,y-1,zz) != surfaceID){
						return false;
					}
				}
			}

			for(int yy = y; yy < y+height && yy < 128; ++yy){
				for(int xx = x-radius; xx <= x+radius; ++xx){
					for(int zz = z-radius; zz <= z+radius; ++zz){
						if (MathUtil.square(xx-x)+MathUtil.square(zz-z) <= radius*radius+1){
							world.setBlock(xx,yy,zz,BlockList.obsidian_end,0,2);
						}
					}
				}
			}
			
			boolean bars = height > 20 && rand.nextInt(5) <= 1;
			if (bars){
				for(int xx = x-radius; xx <= x+radius; ++xx){
					for(int zz = z-radius; zz <= z+radius; ++zz){
						if (!world.isAirBlock(xx,y+height-1,zz)){
							boolean hasMoreAir = false;
							
							for(int ax = -1; ax <= 1; ax++){
								for(int az = -1; az <= 1; az++){
									if (world.isAirBlock(xx+ax,y+height-1,zz+az)){
										hasMoreAir = true;
										ax = 2;
										break;
									}
								}
							}
							
							if (hasMoreAir)world.setBlock(xx,y+height,zz,Blocks.iron_bars,0,2);
						}
					}
				}
			}

			EntityBlockEnderCrystal crystal = new EntityBlockEnderCrystal(world);
			crystal.setLocationAndAngles(x+0.5D,y+height,z+0.5D,rand.nextFloat()*360F,0F);

			crystal.setCrystalType(
				bars?EntityBlockEnderCrystal.BARS:
				(rand.nextInt(4) != 0 && height*radius*radius*Math.PI<320D)?EntityBlockEnderCrystal.BLAST:
				EntityBlockEnderCrystal.TNT
			);
			
			world.spawnEntityInWorld(crystal);
			world.setBlock(x,y+height,z,Blocks.bedrock,0,2);
			
			crystal.setCrystalKey(BiomeDecoratorHardcoreEnd.getCache(world).addCrystal(x,y+height,z));
			
			return true;
		}

		return false;
	}
}
