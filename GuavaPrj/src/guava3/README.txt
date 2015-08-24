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
pela chave passada. 


Functions.compose() faz um compose dos functios passados como param. Ou seja, chamando um, 
o retorno sera aplicado como param de chamada para  o outro. 






