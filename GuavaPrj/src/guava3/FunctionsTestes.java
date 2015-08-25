package guava3;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.google.common.base.Function;
import com.google.common.base.Functions;



class FormatStringToDateFunction implements Function<String,Date> {
	
	public Date apply(String input) {
		SimpleDateFormat d = new SimpleDateFormat("dd/MM/yyyy"); 
		try {
			return d.parse(input);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null; 
	}
}



public class FunctionsTestes {

	
	public static void main(String...args) {
		
		teste1(); 
		teste2(); 
		
		
		
	}
	
	
	private static void teste1() {
		Map<String,Integer> testeMap = new HashMap<String,Integer>() ;
		testeMap.put("um", new Integer(1)); 
		testeMap.put("dois",new Integer(2)); 
		testeMap.put("dez", new Integer(10)); 
		
		Function<String,Integer> numeros = Functions.forMap(testeMap); 
		System.out.println(numeros.apply("um")); 
		System.out.println(numeros.apply("dez")); 
		System.out.println(numeros.apply("dois")); 
		
		
	}
	
	
	
	private static void teste2() {
		
		
		
		
	}
	
	
}
