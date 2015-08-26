
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
				
	
	
	
	
	
	
	
	
	
	
	
	
	
	
								