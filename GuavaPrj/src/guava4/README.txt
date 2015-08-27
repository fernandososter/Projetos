
FluentIterable
Permite criar chain de comandos em um Iterable. 

-> FluentIterable.filter - aceita um Predicate como parametro e todo elemento que for true 
							para o predicate sera retornado. Se nenhum for encontrado, retorna
							um Iterable vazio. 
							
	Iterable<Person> personsFilteredByAge= FluentIterable.from(personList).filter(Predicate); 						
							
							
-> FluentIterable.transform - aplica uma function a todos os elementos de uma colecao. 
							 (como o map do scala). 
							 
	List<String> transformedPersonList =FluentIterable.from(personList).transform(Function );
	
	
-> FluentIterable.toList -> vai retornar os elementos em lista. Tem outros, como toMap, toSet, toSortedMap,...

	List<String> transformedPersonList =FluentIterable.from(personList).transform(Function ).toList();
	
	
	
List

	Auxilia na criacao e iteracao com listas. 
	
-> Lists.partition - retorna listas particionadas do tamanho do segundo parametro passado. 
					ex. partition(lista,3) , vai retornar sublistas com 3 elementos. 
	

Sets

-> Sets.difference - retorna a diferença entre duas listas. Retorna o que tem na primeira e nao na segunda. 
					O contrario nao é verdadeiro. 
					
-> Sets.cartesianProduct - faz produto cartesiano entre os sets. 		

-> Sets.symmetricDifference - é a diferenca entre os sets. (aqui considera os dois, nao apenas o da esquerda). 

-> Sets.union
-> Sets.intersection

				
Maps

-> Maps.uniqueIndex - transforma uma lista ou iterable em map. O valor é o elemento da lista, a chave pode 
					ser gerada com uma functions, que é o segundo parametro. 
					
-> Maps.asMap - tem como input um set e uma function. O retorno é um map onde o key é o objeto da lista 
				e o value o valor retornado da function. 					
					
-> Maps.toMap - mesma coisa mas o rtorno é immutable					
									
-> Maps.transformEntries - aplica transformacoes de uma function nos entries

-> Maps.transformValues - aplica transformacoes de uma function nos values. 									
									
				
Multimaps
	Sao estruturas de estruturas (guarda informacoes aninhadas). 
	
-> ArrayListMultimap	 - armazena uma chave e um arraylist como valor. 
	Metodos estaticos construtores: 
			crate(); 
			create(numerEsperadoDeKeys, numeroEsperadoDeValoresPorKey);
			create(listMultiMap);
			
	Ex: 
		ArrayListMulitmap<String,String> mulitMap = ArrayListMultimap.create();		 			
				
	
BiMap 
	Permite varias keys para um value. Se tentar repetir o value, uma IllegalArgumentException será lançada. 

-> BiMap.forcePut - vai colocar um elemento com o mesmo valor forçadamente. Ou seja, silenciosamente vai 
					remover o anterior e adicionar o novo elemento. 
					
					
-> BiMap.inverse - inverte a posicao de chave-valor, onde vira valor-chave. 					
	
	
	
	
Tables

Tables consiste de map de maps (Map<R,Map<C,V>>)

Para criar, existem 3 metodos: 
	
	HashBasedTable<Integer,Integer,String> hbt = HashBasedTable.create(); 	
	HashBasedTable<Integer,Integer,String> hbt = HashBasedTable.create(5,5); // 5X5 
	HashBasedTable<Integer,Integer,String> hbt = HashBasedTable.create(anotherTable); 	
	
	hbt.put(1,1,"fernando")
	hbt.put(1,2,"soster")
	
-> column(1) - retorna um map com o valores passados como parametro (no caso as colunas com valor 1)
-> row(2) - retorna um map com os valores passados no parametro 


Range

Define objetos em uma collection com um intervalo passado. Os elementos sao todos comparaveis entre si. 

Range.open(1,10) - valores definidos excluidos
Range.close(1,10) - valores definidos incluidos. 


ImmutableCollections 

 fornece thread-safe schema e segurança, ja que ninguem que pegue sua collection podera alterar as informacoes. 
 Par cada collection acima, existe uma parceira que é imutavel. 
 
 Para criar os elementos immutable, usar o elemento estatico Builder que cada classe collection tem. 
 	MapMultimap mmm = new ImmutableListMultimap.Builder<Integer,String>().put(1,"A").put(2,"B").build(); 
 	
 O .build() ao finai vai construir o final object. 
 


Ordening

Prove metodos que ajudar na ordenacao de valores. 
A classe Ordening tem metodo compare que é abtrado. 

Tem duas formas de obter uma classe Ordening
	* Criar uma instancia e fornecer o metodo compare(). 
	* Usar o metodo estatico from (Ordening.from() ) e passar um comparator como param. 
	
-> reverse() - pode ser chamado para reverter a ordem, apos o from()

-> nullsFirst() - coloca os valores com null no inicio da ordem
-> nullsLast() - coloca os valores com null no final da ordem. 

-> compound() - faz um secondary sort, ou seja, usa um parametro complementar para realizar a ordenacao. 
 
 Ex: Ordening.from(Comparator1).compound(Comparator2); 

Para obter os primeiros e ultimos: 
	Ordening<City> ordering = Ordering.from(comparator); 
	List<City> topFive = ordening.greatestOf(cityList,5); 
	List<City> bottomFive = ordening.leastOf(cityList,3); 
	
	
	

 
 
 	
 
	
	
	
	
								