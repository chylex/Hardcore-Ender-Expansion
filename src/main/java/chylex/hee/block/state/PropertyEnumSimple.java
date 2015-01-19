package chylex.hee.block.state;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.properties.PropertyHelper;
import com.google.common.collect.ImmutableSet;

public class PropertyEnumSimple extends PropertyHelper{
	public static PropertyEnumSimple create(String name, Class valueClass){
		return new PropertyEnumSimple(name,valueClass);
	}
	
	private final ImmutableSet values;
	
	private PropertyEnumSimple(String name, Class valueClass){
		super(name,String.class);
		
		Set<String> valuesSetup = new HashSet<>();
		
		for(Object o:valueClass.getEnumConstants()){
			Enum e = (Enum)o;
			if (!valuesSetup.add(e.name()))throw new IllegalArgumentException("Multiple values have the same name \'"+e+"\'");
		}
		
		values = ImmutableSet.copyOf(valuesSetup);
	}

	@Override
	public Collection getAllowedValues(){
		return values;
	}

	@Override
	public String getName(Comparable value){
		return ((Enum)value).name();
	}

}
