package detraf.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Splitter;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Iterables;
import com.google.common.primitives.Doubles;



class ToDetrafFunction implements Function<String,Detraf> {
	 
	private final  SimpleDateFormat sdf = new SimpleDateFormat("yyyymm"); 
	@Override
	public Detraf apply(String input) {
		Detraf d = new Detraf(); 
		try {
			Iterable<String> i = Splitter.on(";").split(input); 
			d.eotOrigem = Iterables.get(i,1); 
			d.eotDestino = Iterables.get(i,2); 
			d.periodoTrafego = toDate(Iterables.get(i, 3));
			d.periodoReferencia = toDate(Iterables.get(i, 4));
			d.valor5 = toDouble(Iterables.get(i, 14)); 
			
		} catch (ParseException e) {
			d = null; 
		}
		return d;
	}
	
	private Double toDouble(String input) {
		return Doubles.tryParse(input.replaceAll(",",".")); 
	}
	
	private Date toDate(String input) throws ParseException {
		return sdf.parse(input); 
	}
}

public class Detraf implements Comparable<Detraf> {


	
	
	 String eotOrigem;
	 String eotDestino; 
	  Date periodoTrafego; 
	  Date periodoReferencia; 
	  String param1; 
	  String param2;
	  String param3; 
	  String param4; 
	  String param5;
	  Double valor1;
	  Double valor2;
	  Double valor3;
	  Double valor4;
	  Integer valorInt1;
	  Double valor5;
	  
	  
	  
	  
	  @Override
	public String toString() {
	return 
		 Objects.toStringHelper(this)
		 		.omitNullValues()
		 		.add("eotOrigem", eotOrigem)
		 		.add("eotDestino", eotDestino)
		 		.add("periodoTrafego", periodoTrafego)
		 		.add("periodoReferencia", periodoReferencia)
		 		.addValue(param1)
		 		.addValue(param2)
		 		.addValue(param3)
		 		.addValue(param4)
		 		.addValue(param5)
		 		.add("valor1", valor1)
		 		.add("valor2", valor2)
		 		.add("valor3", valor3)
		 		.add("valor4", valor4)
		 		.add("valor5", valor5)
		 		.toString(); 
		 		
		  
	}
	  
	  @Override
	public int hashCode() {
		return Objects.hashCode(  eotOrigem, eotDestino,periodoTrafego,
					periodoReferencia); 
	}
	  
	 @Override
	public boolean equals(Object obj) {
		return Objects.equal(this,obj); 
	} 
	  
	@Override
	public int compareTo(Detraf detraf) {
		
		return ComparisonChain.start()
				.compare(eotOrigem, detraf.eotOrigem)
				.compare(eotDestino, detraf.eotDestino)
				.compare(periodoTrafego, detraf.periodoTrafego)
				.compare(periodoReferencia, detraf.periodoReferencia)
				.compare(param1, detraf.param1)
				.compare(param2, detraf.param2)
				.compare(param3, detraf.param3)
				.compare(param4, detraf.param4)
				.compare(param5, detraf.param5)
				.compare(valor1, detraf.valor1)
				.compare(valor2, detraf.valor2)
				.compare(valor3, detraf.valor3)
				.compare(valor4, detraf.valor4)
				.compare(valorInt1, detraf.valorInt1)
				.compare(valor5, detraf.valor5)
				.result(); 
	}

	  

}
