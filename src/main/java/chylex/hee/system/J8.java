package chylex.hee.system;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public final class J8{
	public static <F,T> List<T> mapArray(F[] array, Function<F,T> mapper){
		return Lists.transform(Arrays.asList(array),mapper);
	}
	
	public static <F,T> List<T> mapList(List<F> list, Function<F,T> mapper){
		return Lists.transform(list,mapper);
	}
	
	public static <T> List<T> filterList(List<T> list, Predicate<T> predicate){
		return Lists.newArrayList(Iterables.filter(list,predicate));
	}
	
	public static boolean allMatch(int limit, Predicate<Integer> predicate){
		for(int a = 0; a < limit; a++){
			if (!predicate.apply(a))return false;
		}
		
		return true;
	}
	
	public static <T> T[] toArray(List<T> list, Class<T> componentCls){
		T[] arr = (T[])Array.newInstance(componentCls,list.size());
		for(int ind = 0; ind < arr.length; ind++)arr[ind] = list.get(ind);
		return arr;
	}
	
	public static Predicate nonNull = new Predicate(){
		@Override
		public boolean apply(Object input){
			return input != null;
		}
	};
	
	private J8(){}
}
