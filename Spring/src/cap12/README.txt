This chapter covers
- Writing repositories backed by MongoDB and Neo4j
- Persisting data across multiple data stores
- Working with Spring and Redis


	
MongoDB
		
		Quando um banco orientado a documentos é a melhor opção? Quando nao precisamos de relacionamento 
		entre os dados. 
		
		Ex: quando precisamos avaliar as notas de alunos. Nao precisamos cruzar as notas de varios alunos
		para verificar se o aluno vai passar. Nesse caso um banco que guarde as notas do aluno no aluno 
		é a melhor opcao.
		
		
		Para usar o mongodb, o spring tem um "plugin": Spring Data MongoDB. Ele oferece: 
			- Annotation para object-to-document mappgin
			- Template: MongoTemplate
			- Geracao do repository automatica
		
		Diferentemente do Spring Data Jpa, que usa os annotation do JPA, o Spring Data MongoDB usa
		os proprios annotation para fazer o ralecionamento objeto-documento. 
		
		Antes de usar precisamos de algumas configuracoes: 
			- MongoClient : para acessar o database
			- MongTemplate: para executar consultas,,..
			- Habilitar a geracao automatica do repository (se desejado)
			
		
			@Configuration
			@EnableMongoRepositories(basePackages="orders.db") 				<-- habilitando a geracao automatica do repo
			public class MongoConfig {
		
				@Bean
				public MongoFactoryBean mongo() {							<-- MongoClient bean
					MongoFactoryBean mongo = new MongoFactoryBean();
					mongo.setHost("localhost");
					return mongo;
				}
	
				@Bean
				public MongoOperations mongoTemplate(Mongo mongo) {			<-- MongoTemplate bean
					return new MongoTemplate(mongo, "OrdersDB");
				}
			}
						
** nos dois metodos acima, o mongo() cria o factory e pega uma instancia 						
** no segundo, cria o template. O nome que é passado no template é o nome do database. 
Mesmo se nao vamos usar o template, a geracao automatica do repositorio vai usar internamente. 


		Outro caminho é extender a classe AbstractMongoConfiguration. Nesse caso precisamos implementar
		os metodos fornecidos e instanciar a classe MongoClient. Nao precisamos lidar diretamente com 
		template pq ela é criada implicitamente. 
		
		@Configuration
		@EnableMongoRepositories("orders.db")
		public class MongoConfig extends AbstractMongoConfiguration {
			
			@Override
			protected String getDatabaseName() {
				return "OrdersDB";
			}
			
			@Override
			public Mongo mongo() throws Exception {
				return new MongoClient();
			}
		}
		

		Para acessar bancos que necessitam de credenciais, é um pouco mais complicado:	
			
		...	
			MongoCredential.createMongoCRCredential(env.getProperty("mongo.username"),"OrdersDB",
				env.getProperty("mongo.password").toCharArray());
			return new MongoClient(new ServerAddress("localhost", 37017),Arrays.asList(credential));
		...			
			
		
		
		Para criar a configuracao em xml: 
		
			<?xml version="1.0" encoding="UTF-8"?>
			<beans xmlns="http://www.springframework.org/schema/beans"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xmlns:mongo="http://www.springframework.org/schema/data/mongo"
				xsi:schemaLocation="
				http://www.springframework.org/schema/data/mongo
				http://www.springframework.org/schema/data/mongo/spring-mongo.xsd
				http://www.springframework.org/schema/beans
				http://www.springframework.org/schema/beans/spring-beans.xsd">
			
				<mongo:repositories base-package="orders.db" />
				<mongo:mongo />
				<bean id="mongoTemplate" class="org.springframework.data.mongodb.core.MongoTemplate">
					<constructor-arg ref="mongo" />
					<constructor-arg value="OrdersDB" />
				</bean>
			</beans>
		
		
		
		
	Annotation fornecidos pelo Spring Data MongoDB
		@Document : identifica um domain object para ser mapeado para documento
		@Id : indica que o elemento é um id
		@DbRef: identifica que um campo pretende referenciar outrou documento, possivelmente em outro database
		@Field: Define custom metadata para um documento
		@Version: identifica um propriedade para ser usada como version field
		
		
		
		Ex: 
				@Document									<-- identificacao do documento (similar ao @Entity)						
				public class Order {
					@Id										<-- id do documento
					private String id;
					
					@Field("client")						<-- troca o nome do campo
					private String customer;
					
					private String type;
					private Collection<Item> items = 
							new LinkedHashSet<Item>();
		
					...
				}
		
** todos os outros atributos que nao tem @Field serao persistidos com o mesmo nome. 
** para nao persistir um elemento, @Transient
	
			A classe Item é um pojo que nao precisa ser anotada com @Document ou @Id, pois ela será 
			persistida juntamente com o Order, e nao será criado relacionamento. 
			
			
		
	Usando o MongoDB
	
		Depois de configurar o mongo (com o javaconfig ou extendendo AbstractMongoConfiguration, basta 
		injetar o MongoOperations onde queremos usar: 
		
			@Autowired
			MongoOperations mongo;
				
		O MongoOperations é uma interface que o MongoTemplate implemta. Assim nao trabalhos diretametne 
		com a classe concreta, mas com um proxy dela. 
		
		Essa interface nos oferece diversos metodos para lidar com os dados: 
		
			//efetuando um count dos objetos order
			long orderCount = mongo.getCollection("order").count();	
			
			//salvando um objeto order
			// o primeiro elemento é o objeto e o segundo é onde salvar
			mongo.save(order, "order");
	
			//find
			Order order = mongo.findById(orderId, Order.class);
		
		Se precisar passar uma query customizada, 
		
			// buscar as orders que o client é 'chuck wagon'
			List<Order> chucksOrders = mongo.find(Query.query(Criteria.where("client").is("Chuck Wagon")), Order.class);	
		
			// mais complexa. 
			List<Order> chucksWebOrders = mongo.find(Query.query(Criteria.where("customer").is("Chuck Wagon")
				.and("type").is("WEB")), Order.class);
		
			//remover
			mongo.remove(order);
		
		
		
	Criando o repositorio automatico
	
		Assim como com o Spring Data JPA, podemos criar um repository automaticamente para mongodb tambem. 
		
		Como o suporte para mongodb ja esta habilitado (@EnableMongoRepositories), só temos que criar uma interface
		que extenda a interface do mongodb que irá definir os metodos: MongoRepository. 
		
		Aqui criamos uma interface para lidar com os objetos de Order: 
		
			public interface OrderRepository extends MongoRepository<Order, String> {
			}
		
		A internface MongoRepository extende diretamente a classe Repository (e toda a classe que extende 
		repository tera os metodos de controle gerados automaticamente)
		
		No generics, o primeiro parametro é a classe que tem o @Document e o segundo é o tipo
		do dado que tem o @Id. 
		
		A lista dos metodos que será implementado por default esta na pagina 338
		
		Para adicionar metodos custormizados na nossa interface, o principio é o mesmo usado no Spring Data JPA
		
		** adicionando os metodos customizados na interface. 
		
			public interface OrderRepository extends MongoRepository<Order, String> {
				List<Order> findByCustomer(String c);
				List<Order> findByCustomerLike(String c);
				List<Order> findByCustomerAndType(String c, String t);
				List<Order> findByCustomerLikeAndType(String c, String t);
			}
			
		
		** o verbo é flexivel, pode ser find ou get ou read. 
		
		
		Tambem podemos customizar queries com o @Query. No entanto, o parametro do @Query não é uma query, 
		mas um json:
		
			@Query("{'customer': 'Chuck Wagon', 'type' : ?0}")
			List<Order> findChucksOrders(String t);
		
		** o ?0 indica o pametro que a variavel t será substituida. Se tiverem mais, ?1, ?2 ,...
		
		
		
		
Grafos com NEO4j	
		
	A  chave na configuracao do neo4j é a criacao de um bean GraphDatabaseService para criar o 
	repositorio automaticamente. 
	
		
		@Configuration
		@EnableNeo4jRepositories(basePackages="orders.db")		<-- habilitando repositorio automatico
		public class Neo4jConfig extends Neo4jConfiguration {
		
			public Neo4jConfig() {
				setBasePackage("orders");						<-- setando o base package do modelo
			}
			
			@Bean(destroyMethod="shutdown")
			public GraphDatabaseService graphDatabaseService() {
				return new GraphDatabaseFactory()
				.newEmbeddedDatabase("/tmp/graphdb");			<-- configuracao do embedded db. 
			}
			
		}
		
		
	 Habilitar o Spring para criar os repositorios automaticamente (@EnableNeo4jRepositories) e 
	 passar o pacote base onde ele deve procurar por interfaces que extendam direta ou indiretamente
	 a interface Repository. 
	 
	 Quando extendemos a classe Neo4jConfiguration, ele nos fornece o metodo setBasePackage(), que
	 chamamos no contrutor e falamos em qual pacote procurar as classes entity. 
	 
	 ** quando falamos de embedded db no neo4j, estamos falando que o database irá ser executado
	 na mesma vm, e nao em um servidor separado. 
	 
	 Se tiver um servidor executando um neo4j e quisermos conectar nele, devemos alterar para 
	 acessar via REST usando a classe SpringRestGraphDatabase: 
	 
	 	@Bean(destroyMethod="shutdown")
		public GraphDatabaseService graphDatabaseService() {
			return new SpringRestGraphDatabase("http://graphdbserver:7474/db/data/");
		}
	Se precisar fornecer credenciais: 
		...
			return new SpringRestGraphDatabase("http://graphdbserver:7474/db/data/",
				env.getProperty("db.username"), env.getProperty("db.password"));		 
		..
		
	Para usar xml na configuracao no neo4j, pag 344
	
	Annotations 
		
		Annotations que o Spring Data Neo4j fornece Pag 345
		
	Ex de como ficaria a classe Order para o Neo4j
		
		@NodeEntity								<-- indicando que Order é nó
		public class Order {
		
			@GraphId							<-- o id do ghraph
			private Long id;
			
			private String customer;
			
			private String type;
			
			@RelatedTo(type="HAS_ITEMS")							<-- relacao com outro grafo
			private Set<Item> items = new LinkedHashSet<Item>();
			
			...
		} 
		
** todo @NodeEntity deve ter um @GraphId e ele deve ser long. 
** itens sem anotacoes e nao marcados como @Transient seráo atributos do nó automaticamente. 

		Nó Item: 
			@NodeEntity
			public class Item {
				@GraphId
				private Long id;
				private String product;
				private double price;
				private int quantity;
			...
			}

					
		
		Para criar relationships que tenham valores, é necessário criar uma classe que represente
		o relation: 
		
			@RelationshipEntity(type="HAS_LINE_ITEM_FOR")		<-- indica a relacao
			public class LineItem {
				@GraphId										<-- id do grafo
				private Long id;
				
				@StartNode
				private Order order;							<-- no de inicio
				
				@EndNode
				private Product product;						<-- no de fim
				
				private int quantity;
				...
			}
	
		
		Para usar o Neo4j, existe o neo4jtemplate que podemos injetar em qualquer ponto 
		do codigo que quisermos usar: 
		
			@Autowired
			private Neo4jOperations neo4j;
		
		Ele oference diversos metodo: 
			
			//save, desde que o objeto esteja anotado com @NodeEntity 
			Order order = ...;
			Order savedOrder = neo4j.save(order);
		
		Se souber o entityid a ser buscado: 
		
			Order order = neo4j.findOne(42, Order.class);
		** se nao existir irá ocorrer uma notfoundexception
		
		
		Para buscar uma lista por tipo de nó
		
			EndResult<Order> allOrders = neo4j.findAll(Order.class);
		** EndResult é um Iterable. Se nao tiver retorn, será um empty iterable
		
		Fazer o count por um determinado tipo
			long orderCount = count(Order.class);
			
		Delete
			neo4j.delete(order);
			
		Para criar um relacionamento, que faz isso é o createRelationshipBetween()
		
						Order order = ...;
						Product prod = ...;
					
						LineItem lineItem = neo4j.createRelationshipBetween(
								order, prod, LineItem.class, "HAS_LINE_ITEM_FOR", false);
					
						lineItem.setQuantity(5);
						neo4j.save(lineItem);
	
			Os argumentos do createRelationshipBetween sao: 
					no de inicio
					no de fim
					classe do relacionamento (anotado com @RelationshipEntity
					nome do relacionamento (strnig)
					se é bidirecional
			
			O retorno é uma instancia do relationship que podemos setar qualquer valor que quisermos. 
			Chamar o save no final. 
			
			
			
			
	Repositorios automaticos para o Neo4j
			
			
			Se ja tem uma classe de config anotada com @EnableNeo4jRepositories, podemos implementar 
			nossa interface: 
			
				public interface OrderRepository extends GraphRepository<Order> {}
			
			** aqui nao precisa passar o tipo do dado do id, pois será sempre long. 
			** lista dos metodos que serão disponibilizados na pag 350
			
			** um dos metodos que será disponibilizado é o query(), que permite executar 
			queries cypher no banco de dados. 
			
			** tambem podemos declarar metodos na interface, com as mesmas regras de nomenclatura 
			para realizar buscar nos grafos. 
			
				public interface OrderRepository extends GraphRepository<Order> {
					List<Order> findByCustomer(String customer);
					List<Order> findByCustomerAndType(String customer, String type);
				}
				
			Para usar o @Query com o cypher
				
					@Query("match (o:Order)-[:HAS_ITEMS]->(i:Item) " +
						"where i.product='Spring in Action' return o")
					List<Order> findSiAOrders();
				
			
			
			
			
			
		
	
					
				
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
