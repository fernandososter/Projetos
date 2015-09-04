


Cache no Guava

MapMaker

Encontrado no com.google.common.collect. 
Permite uma rapida construcao do ConcurrentHasMap.

CurrentMap<String,Book> books = new MapMaker().concurrencyLevel(2) // quantidade de modificacoes simultaneas permitidas
								.softValues() // os dados serao wrapped em um obj SoftReference para rapida garbage collection
											  // pode ser weak. 	
								.makeMap(); 

O guava tem dois mecanismos de cache: 

	Cache
	LoadingCache (interface que extends Cache)
	

Cache é o mesmo funcionamento de um Map
tem um metodo put(chave,valor)

Se tentarmos obter um valor que nao existe ira retorna null

No entanto o get() possue um funcioanmento especial: 

	V value = cache.get(key, Callable<? Extends V> value); 

Podemos organizar o retorno do valor com callable de for sincrona ou assincrona. 
(pesquisar mais)

Tem tambem metodos para invalidar os conteudo do cache: 

	invalidate(key);
	invalidateAll();
	invalidate(Iterable<?> keys); 
	
	
LoadingCache

Codigo: Book book = loadingCache.get(id); 

Se o book nao estiver no cache, o loadingcache vai saber como obter o book e armazena-lo no cache. 

Se ocorrer uma chamada ao get() e o loadingcache estiver armazenando o valor, o fluxo ficara 
blocked até terminar a carga. 
Caso ocorra mais de uma chamada ao mesmo tempo, com ids diferentes, o loadingcache ira executar de 
forma concorrente. 

Se quisermos obter uma lista de valores para uma lista de keys: 

	ImmutableMap<key,value> map = cache.getAll(Iterable< ? Extends key>); 
	
O retorno acima pode ser todos novos, todos cacheados ou mix. 

Para fazer refresh do valor: 
	refresh(key); 

O valor presente nao sera descartado até o refresh terminar. 
Se ocorrer exception, o valor anterior será mantido. 
Se for a atualizacao for assincrona, poderemos obter uma valor staled.

	

CacheBuilder

Prove formas de obter o cache e o loadingcache atraves do Builder pattern. 


	LoadingCache<String,TraceAccount> tradeAccountCache = // Trade acount = pojo
		CacheBuilder.newBuilder()
			.expireAfterWrite(5L, TimeUnit.Minutes) // vai remover o elemento do cache automaticamente apos 5 minutos
			.maximumSize(5000L) // tamanho maximo do elemento
			.removalListener(new TradeAccountRemovalListener()) //listener de quando o elemento for removido
			.ticker(Ticker.systemTicker())
			.build( // metodo de carregar o valor para quanto tentar obter. 
				new CacheLoader<String,TradeAccount() {
					@Override
					public TradeAccount load(String key) throws Exception {
						return tradeAccountService.getTradeAccountById(key); 
					}
			}); 

Para quando quisermos colocar um tempo por acesso: 

	.expireAfterAccess(20L, TimeUnit.MINUTES)
	
Outros parametros: 
	.concurrencyLevel(10) - o default é 4
	.refreshAfterWrite(5L, TimeUnit.SECONDS) - para expirar 5 segs apos escrito. 
	
	

CacheBuilderSpec

Podemos usado uma especificacao para montar um CacheBuilder: 

	String configString = "concurrencyLevel=10,refreshAfterWrite=5s"
		
no refreshAfterWrite, as medidas: 

	s - seconds
	m - minutes
	h - hours
	d - days
** nao tem para milliseconds ou nanoseconds

Para criar: 
CacheBuilderSpec spec = CacheBuilderSpec.parse(configString);
CacheBuilder.from(spec); 



CacheLoader

O cacheloader tem dois metodos from (um para Functions e outro para Suppliers). 
Pesquisa rfora. 


CacheStats

Retorna estatiscas de um mecanismo de cache criado (como quantidade, performance,...)

Quando criamos o cache, é necessario habilitar as estatisticas com um metodo: 
	
	CacheBuilder.newBuilder()
				.recordStats() // para habilitar. 
				
Para obter uma instancia das estatisticas de um Cache/LoadingCache: 

	CacheStats stst = cache.stats(); 
	
	
RemovalListener 
RemovalListeners
RemovalNotification






	
	
								