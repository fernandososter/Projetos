This chapter covers
- Enabling declarative caching
- Caching with Ehcache, Redis, and GemFire
- Annotation-oriented caching



	tem dois tipos de suporte ao cache com o spring
		- annotation-driven
		- xml-declared
		
	Geralmente colocamos cache em um metodo utilizando os annotations @Cacheable ou @CacheEvict
	
	Para habilitar o cache, basta colocar o @EnableCaching em um dos configurations do spring
	
		@Configuration
		@EnableCaching										<-- habilitando o cache
		public class CachingConfig {
			@Bean
			public CacheManager cacheManager() {
				return new ConcurrentMapCacheManager();			<- declarando um cachemanager
			}
		}

	
	em xml 
		<?xml version="1.0" encoding="UTF-8"?>
		<beans xmlns="http://www.springframework.org/schema/beans"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xmlns:cache="http://www.springframework.org/schema/cache"
			xsi:schemaLocation="
			http://www.springframework.org/schema/beans
			http://www.springframework.org/schema/beans/spring-beans.xsd
			http://www.springframework.org/schema/cache
			http://www.springframework.org/schema/cache/spring-cache.xsd">
			
			<cache:annotation-driven />
			<bean id="cacheManager" class="org.springframework.cache.concurrent.ConcurrentMapCacheManager" />
		</beans>
		
		
		
	Quando habilitamos o cache no spring, ele cria um mecanismo em AOP. O cachemanager é mais que um gerenciador
	ele tambem permite adaptar o spring a varios mecanismos diferentes de cache. 
	
	O CurrentMapCacheManager usa o java.util.concurrent.ConcurrentHashMap para cache. Nao é o ideal 
	para producao pois é muito simples. 
	
	Existem outros managers de cache: 
		- SimpleCacheManager
		- NoOpCacheManager
		- ConcurrentMapCacheManager
		- CompositeCacheManager
		- EhCacheCacheManager
		
	A partir da 3.2 ainda tem mais dois: 
		- RedisCacheManager
		- GemfireCacheManager
		
		
		
	EhCache
	
		@Configuration
		@EnableCaching
		public class CachingConfig {
		
			@Bean
			public EhCacheCacheManager cacheManager(CacheManager cm) {
				return new EhCacheCacheManager(cm);
			}
		
			@Bean
			public EhCacheManagerFactoryBean ehcache() {
				EhCacheManagerFactoryBean ehCacheFactoryBean = new EhCacheManagerFactoryBean();
				ehCacheFactoryBean.setConfigLocation(new ClassPathResource("com/habuma/spittr/cache/ehcache.xml"));
				return ehCacheFactoryBean;
			}
	  	}
		
		
** o nome do manager do ehcache é ehcachecachemanager por no ehcache ja existe um ehcachemanager e isso poderia
causar confusao no dependecy injecttion. (no metodo cacheManager()  o retorno é um EhCacheCache). 
		
		ehcache.xml: 
		
		<ehcache>
			<cache name="spittleCache" maxBytesLocalHeap="50m" timeToLiveSeconds="100">
			</cache>
		</ehcache>
		
** é possivel usar mais de um mecanismo de cache. Podemos mistura-los (pag 368)

	
	ANOTANDO ELEMENTOS
		
		Quando habilitamos o cache o spring cria um aspecto para gerenciar os componentes. 
			
			@Cacheable: indica que o spring deve verificar se o retorno do metodo anotado ja tem o retorno no cache. 
						Se nao tiver chama o metodo. 
			@CachePut: 	indica que o spring deve colocar o return value no cache
			@CacheEvict: indica que o spring deve expulsar uma ou mais entidades do cache
			@Caching:  
		
		
		As tags podem ser colocadas tanto em classe quanto em metodos. Quando declaradas nos 
		metodos sobrepoem o da classe, para o metodo. 
		
		
		@Cacheable e @CachePut fazem a mesma coisa (colocar a informacao no cache) mas de maneira
		diferente. 
		
		O @Cacheable primeiro verifica se o valor esta em cache e se estiver retorna. Se nao estiver, 
		chama o metodo e então coloca o valor no cache. 
		
		O @CachePut nao verifica se o valor ja esta em cache antes. Chama o metodo e coloca o valor 
		no cache. 
		
		Tanto @Cacheable quanto @CachePut dividem os mesmos atributos: 
			- value
			- condition
			- key
			-unless 
			
		Quando buscamos um findOne() do repository por exemplo é improvavel que ele mude. Por isso 
		se colocar no cache evitamos o tempo de busca do database. 
		
		Ex: 
			@Cacheable("spittleCache")
			public Spittle findOne(long id) {
				try {
					return jdbcTemplate.queryForObject(SELECT_SPITTLE_BY_ID,
						new SpittleRowMapper(),id);
				} catch (EmptyResultDataAccessException e) {
					return null;
				}
			}
		
		Quando ocorrer a chamada ao findOne() o aop vai interceptar e verificar se 
		existe no cache algo com o nome spittleCache. Se tiver vai retornar. Se nao 
		tiver, vai continuar a chamada do metodo. 
		
		Se colocarmos o @Cachable na implementacao do metodo, somente o metodo anotado 
		tera o cache. Se colocarmos na interface, todos os metodos que implementam o metodo
		da interface irao herdar automaticamente. Por exemlo, colocar nas classes repository: 
		
			@Cacheable("spittleCache")
			Spittle findOne(long id);		
			
		O @CachePut é ideal para quando usamos o save() por exemplo, que a informação que foi 
		salva provavelmente será usada logo. Assim a informação ficará disponivel no cache e se
		for chamada com o findOne() sera retornada. 
		
			@CachePut("spittleCache")
			Spittle save(Spittle spittle);
		
		No entanto nesse caso o key será o proprio objeto, e isso nao será muito util, pois teriamos
		que procurar o proprio objeto. Nesse caso temos que especificar a key que quermos usar para 
		esse objeto. 
		
		Podemos especificar a key atraves de SpEL. Existem varios parametros para poder especificar a key: 
		
			#root.args	
			#root.caches
			#root.target
			#root.targetClass
			#root.method
			#root.methodName
			#result
			#Argument
		
		Como vamos fazer cache do retorno do metodo save(), que é o proprio objeto, podemos chamar
		uma propriedade do objeto que será salvo. 
		
			@CachePut(value="spittleCache", key="#result.id")
			Spittle save(Spittle spittle);
			
	
		O mecanismo de cache pode ter condicoes.
			- unless	se a condicao SpEL for true nao faz cache
			- condition 	se a condicao SpEL for false nao faz cache
		
		A diferenca entre os dois é  que o unless a busca no cache será feita. Mas se a condicao
		nao for alcançada o elemento nao será adicionado. 
		
		No condition, o cache é desabilitado para o metodo se a condicao for false. Ou seja, nao 
		tera busca. 
		
			@Cacheable(value="spittleCache" unless="#result.message.contains('NoCache')")
			Spittle findOne(long id);
		
		
			@Cacheable(value="spittleCache"
				unless="#result.message.contains('NoCache')"
				condition="#id >= 10")
			Spittle findOne(long id);
		
		
		Para remover um elemento do cache, usar o @CacheEvict. Ex quando removemos um elemento
		do banco e precisamos removelo tambem do cache. 
		
			@CacheEvict("spittleCache")
			void remove(long spittleId);
		
		Uma diferença da utilizacao do @CacheEvict é que ele pode ser usado com metodo void, 
		diferente do @Cachable e @CachePut
		
		O @CacheEvict tem os seguintes atributos: 
			- value 
			- key
			- condition
			- allEntries
			- beforeInvocation
			
		
	XML
	
		Para quando nao puder alterar o codigo ou nao quisermos as anotacoes do spring em nosso codigo
		olhar os exemplo no livro. 
		
		
		
		
		
		
			
		
		