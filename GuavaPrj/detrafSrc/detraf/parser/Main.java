package detraf.parser;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;

public class Main {

	
	public static Collection<Detraf> carregarDetrafs() throws IOException {
	
		Collection<Detraf> result = null; 
		
		File dir = new File("/tmp/detraf/") ;
		for(String filename :  dir.list()) {
			File f = new File("/tmp/DETRAF_FINAL_201307_HOME_TIM_C_20130603065631_ITX.csv"); 
			List<String> linhas = Files.readLines(f, Charsets.UTF_8); 
			Collection<Detraf> listaDetraf = Collections2.transform(linhas, new ToDetrafFunction()) ;
			Collection<Detraf> listaDetrafValoresValidos = Collections2.filter(listaDetraf, new Predicate<Detraf>() {
				@Override
				public boolean apply(Detraf input) {
					if(input.valor5 == null) {
						return false; 
					} 
					return true;
				}
			}); 
			
			if( result == null) {
				result = listaDetrafValoresValidos; 
			} else {
				result = (Collection<Detraf>) Iterables.concat(result,listaDetrafValoresValidos); 
			}
			 
		}
		
		
		
		return result; 
	}
	
	
	
	public static void main(String...args) throws IOException {
		
		
				
	}
	
}
