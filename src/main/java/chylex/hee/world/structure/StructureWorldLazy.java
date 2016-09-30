package chylex.hee.world.structure;
import java.util.Random;
import net.minecraft.world.World;
import chylex.hee.system.abstractions.Pos.PosMutable;

/**
 * Special structure world that uses chunk methods directly, which makes the generation very fast but only suitable for generating in remote, unloaded locations.
 */
public class StructureWorldLazy extends StructureWorld{
	private boolean sendToWatchers;
	
	public StructureWorldLazy(World world, int radX, int sizeY, int radZ){
		super(world, radX, sizeY, radZ);
	}
	
	public StructureWorldLazy(int radX, int sizeY, int radZ){
		super(null, radX, sizeY, radZ);
	}
	
	/**
	 * Updates the chunks for players close to them at the expense of performance.
	 */
	public void setSendToWatchers(){
		this.sendToWatchers = true;
	}
	
	@Override
	protected void generateBlocksInWorld(World world, Random rand, int centerX, int bottomY, int centerZ){
		PosMutable pos = new PosMutable();
		int x, y, z, index = -1;
		
		for(z = -radZ; z <= radZ; z++){
			for(x = -radX; x <= radX; x++){
				for(y = 0; y < sizeY; y++){
					if (blocks[++index] != null){
						pos.set(centerX+x, bottomY+y, centerZ+z);
						
						if (world.getChunkFromBlockCoords(pos.x, pos.z).func_150807_a(pos.x&15, pos.y, pos.z&15, blocks[index], metadata[index]) && sendToWatchers){
							world.markBlockForUpdate(pos.x, pos.y, pos.z);
						}
					}
				}
			}
		}
	}
}
