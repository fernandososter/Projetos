Functional Programming. 

Trata de 3 Interfaces e suas classes com metodos estaticos que auxiliam na sua implementacao. 


Function  - Interface 
Functions - Class

Predicate  - Interface 
Precicates - Class
		 
Supplier   - Interface
Suppliers  - Class


FUNCTION 
Interface que permite tonar o java como Programacao Funcional. 
A interface tem dois metodos, 

public interface Function<F,T> {
	T apply(F input);
	boolean equals(Object object);
}


Functions.forMap() transforma um map em uma function que, chamando apply, procura o valor
pela chave passada. Caso nao encontre a chave passada, IllegalArgumentExcpetion


Functions.compose() faz um compose dos functios passados como param. Ou seja, chamando um, 
o retorno sera aplicado como param de chamada para  o outro. 


O objetivo da programacao funcional é que o objeto permaneça inalterado (final). Qualquer iteração irá 
gerar outro objeto. 


PREDICATE
O predicate tem os mesmos metodos do function (apply e equals). No entanto é usado para realizar filtragens. 

public class PopulationPredicate implements Predicate<City> {
	@Override
	public boolean apply(City input) {
		return input.getPopulation() <= 500000;
	}
}

Predicates.and cria uma associacao entre duas (OU MAIS?) precidate com o operador and (somente se forem verdadeiras). 

Predicate smallAndDry =
	Predicates.and(smallPopulationPredicate,lowRainFallPredicate);
	
	
Predicates.or faz a mesma coisa mas com operador or (assim que encontrar o primeiro true para). 

Predicate smallTemperate =
	Predicates.or(smallPopulationPredicate,temperateClimatePredicate);	


Predicates.not faz a negacao do predicate que passamos como param. (par nao precisar escrever outro predicate). 

Predicate largeCityPredicate =
	Predicate.not(smallPopulationPredicate);
	
	
Predicate.compose tem como parametros um Predicate e uma Function. O compose aplica o predicate no retorno da function. 

Predicate<String> predicate =
	Predicates.compose(southwestOrMidwestRegionPredicate,lookupFunction);
	


SUPPLIER

Tem apenas um metodo get() que retorna uma instancia T. 
Bom para criar factories, singletons e esconder a implementacao e complexidade de criacao de objetos, bem como realizar
lazy instantion de objetos.

public interface Supplier<T> {
	T get();
}

O T, pode ser function ou predicates. Podemos aninhar para ganhar agilidade e usar o supplier como construtor. 
pag 35/36


Suppliers.memoize() - cria um tipo de cache do objeto passado. Quando chama a primeira vez, a instancia é criada. Apos isso
toda a chamada ao wrapper vai usar a mesma instancia. 

Supplier<Predicate<String>> wrapped =
	Suppliers.memoize(composedPredicateSupplier);


Suppliers.memoizeWithExpiration() - faz a memorizacao mas tem um tempo de timeout. 
Supplier<Predicate<String>> wrapped =
	Suppliers.memoize(composedPredicateSupplier,10L,TimeUnit.MINUTES);







