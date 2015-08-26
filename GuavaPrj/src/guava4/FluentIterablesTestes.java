package guava4;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class FluentIterablesTestes {

	
	public static void main(String...agrs) {
		
		
		
		FluentIterablesTestes.filter(); 
		
		transform(); 
		toList(); 
		
		
	}
	
	private static void filter() {
		List<Integer> l = Arrays.asList(1,2,3,4,5,6,7,8,9); 
		
		Iterable<Integer> i = FluentIterable.from(l).filter(new Predicate<Integer>() {
			@Override
			public boolean apply(Integer input) {
				return (input%2)==0; 
			}
		}); 
		
		System.out.println(Joiner.on("|").join(i)); 
		
	}
	
	
	private static void transform() {
		List<Integer> l = Arrays.asList(1,2,3,4,5,6,7,8,9); 
		
		System.out.println(FluentIterable.from(l).transform( new Function<Integer,Integer>() {
			@Override
			public Integer apply(Integer input) {
				return (int) Math.pow(input, 2);
			}
		})); 
		
		
		FluentIterable.from(l).transform(new Function<Integer,Integer>() {
			@Override
			public Integer apply(Integer input) {
				System.out.println(input);
				return input;
			}
		}); 
	}
	
	private static void toList() {
		List<Integer> l = Arrays.asList(1,2,3,4,5,6,7,8,9); 
		
		ImmutableList<Integer> il = FluentIterable.from(l).transform(new Function<Integer,Integer>() {
			@Override
			public Integer apply(Integer input) {
				return (int) Math.pow(input, 2);
			}
		}).toList();
		
		System.out.println(Joiner.on("|").join(il)); 
		
		
		ImmutableSet<Integer> is = FluentIterable.from(l).transform(new Function<Integer,Integer>() {
			@Override
			public Integer apply(Integer input) {
				return (int) Math.pow(input, 2);
			}
		}).toSet();
		
		System.out.println(is);
	}
	
	
}
