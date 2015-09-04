Odds e Ends

HashFunction

o guava tem duas funcoes para gerar hashing. Adler-32 e CRC-32

	HashFunction adler32 = Hashing.adler32(); 
	HashFunction crc32 = Hashing.crc32(); 
	
	
Tem outras funcoes nao criptografadas
	HashFunction gfh = Hashing.goodFastHash(128); 
	HashFunction murmur3_32 = Hashing.murmur3_32(); 
	HashFunction murmur3_128 = Hashing.murmur3_128(); 
	
	
Hashs criptografados

	HashFunction sha1 = Hashing.sha1();
	HashFunction sha256 = Hashing.sha256();
	HashFunction sha512 = Hashing.sha512();
	

	
BloomFilter

bloomfilter é interessante pois age sobre um set indicando que: 
	- o set absolutamente nao tem um elemento
	- o set pode ter o elemento.
	
Isso evita, pexemplo, fzer leituras desnecessarias em grandes sets. 




Optional

Usado para evitar retornos nulos de metodos. Isso indica ao chamador da funcao que o resultado
do retorno pode ou nao estar presente. 

Optional é uma classe abstrata que pode ser herdada. 
Outras formas de obter instancias: 

	- Optional.absent() - retorna um empty optional
	- Optional.of(T ref) - retorna um optional com uma instancia de T
	- Optional.fromNullable(T ref) - se ref for null, retorna um empty, senao com o valor. 
	- Optional.or(Supplier<T> supplier) 
	

Throwables
	
	Ajuda a trabalhar com os stacks dos throwables. 
	
	Um retorna do top pra baixo, o outro do root para cima. 
	Throwables.getCasualChain  
	Throwables.getRootCause
	
	} catch (Exception e) {
		cause = Throwables.getRootCause(e);
	}
	
	} catch (Exception e) {
		throwables = Throwables.getCausalChain(e);
	}


