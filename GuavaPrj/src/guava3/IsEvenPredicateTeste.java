package guava3;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;



public class IsEvenPredicateTeste {

	public static void main(String...args) {
		List<Integer> numbers = Arrays.asList(1,2,3,4,5,6,7,8,9); 
		Predicate<Integer> even = new Predicate<Integer>() {
			public boolean apply(Integer input) {return (input % 2) == 0;}
		}; 
		System.out.println(Joiner.on("#").join(Iterables.filter(numbers, even))); 
		System.out.println(Joiner.on("#").join(Iterables.filter(numbers, Predicates.not(even)))); 
	}
}
