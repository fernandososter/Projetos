package guava2;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;


class Person implements Comparable<Person>  {
	
	class Endereco implements Comparable<Endereco> {
		String rua; 
		String cidade; 
		Endereco() {
			this.rua = "Rua A"; 
			this.cidade = "cidade A"; 
		}
		
		@Override
		public String toString() {
			return Objects.toStringHelper(this).add("rua", rua).add("cidade",cidade).toString(); 
		
		}
		@Override
		public int hashCode() {
			return Objects.hashCode(rua,cidade); 
		}
		@Override
		public int compareTo(Endereco o) {
			return ComparisonChain.start()
				.compare(rua, o.rua)
				.compare(cidade,o.cidade)
				.result(); 
			
		}
		
		
	}
	
	String nome; 
	int idade; 
	String rg;
	Endereco end; 
	
	Person() {
		this.nome = "nome a";
		this.idade = 20; 
		this.rg = "rgA"; 
		this.end = new Endereco(); 
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(nome,idade,end,rg); 
	}
	
	
	
	public String toString() {
		return Objects.toStringHelper(this)
			.omitNullValues() // usado para omitir valores nulos. 
			.add("nome", nome)
			.add("idade", idade)
			.add("endereco", end)  //funciona tambem para objetos complexo. 
			.add("rg", rg).toString() ;
	}

	@Override
	public int compareTo(Person a) {
	 return	ComparisonChain.start()
			.compare(this.nome, a.nome)
			.compare(this.idade, a.idade)
			.compare(this.rg, a.rg)
			.compare(this.end, a.end)
			.result(); 
			
	}
}

public class ObjectUtilitiesTeste {

	/*
	 * Auxiliam na implementacao de metodos das classes que objetos precisam, 
	 * como toString, hashValue(), compareTo()...
	 * 
	 * -> Objects.toStringHelper(). -  cria o metodo toString() da classe. 
	 * -> Objects.firstNonNull() - para ver se o objeto é null, senao um default. Se ambos nulos, Nullpointerexception
	 * -> Objects.hashCode(...) - gera o hashCode(). 
	 * -> ComparisionChain.start() - gera o compareTo(). Para no primeiro non zeroed value. 
	 * 
	 * 
	 */
	
	public static void main(String...args) {
		teste1(); 
		
		
	}
	
	private static void teste1() {
	
		System.out.println(new Person());
		
		System.out.println(Objects.firstNonNull(null, "se null entao esse!"));
		
		System.out.println(new Person().hashCode());
		
		System.out.println(new Person().compareTo(new Person()));
		
	}
	
	
	
	
	
}
