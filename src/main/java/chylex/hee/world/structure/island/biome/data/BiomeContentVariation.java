package chylex.hee.world.structure.island.biome.data;
import gnu.trove.set.hash.TByteHashSet;
import chylex.hee.system.weight.IWeightProvider;
import chylex.hee.system.weight.WeightedList;
import chylex.hee.world.util.SpawnEntry;

public final class BiomeContentVariation implements IWeightProvider{
	private static final TByteHashSet usedIDs = new TByteHashSet(16);
	
	public final byte id;
	private final byte weight;
	public final WeightedList<SpawnEntry> spawnEntries = new WeightedList<SpawnEntry>();
	
	public BiomeContentVariation(int id, int weight){
		this.id = (byte)id;
		this.weight = (byte)weight;
		
		if (usedIDs.contains(this.id))throw new IllegalArgumentException("Biome Content Variation with ID "+id+" has been already taken!");
		else usedIDs.add(this.id);
	}
	
	@Override
	public int getWeight(){
		return weight;
	}
	
	@Override
	public boolean equals(Object o){
		return o == this;
	}
	
	@Override
	public int hashCode(){
		return System.identityHashCode(this);
	}
}
