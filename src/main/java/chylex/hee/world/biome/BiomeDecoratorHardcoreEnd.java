package chylex.hee.world.biome;
import net.minecraft.world.biome.BiomeEndDecorator;
import net.minecraft.world.biome.BiomeGenBase;

public class BiomeDecoratorHardcoreEnd extends BiomeEndDecorator{
	public BiomeDecoratorHardcoreEnd(){
		this.spikeGen = null;
	}
	
	@Override
	protected void genDecorations(BiomeGenBase biome){
		if (currentWorld.provider.dimensionId != 1)super.genDecorations(biome);
	}
}
