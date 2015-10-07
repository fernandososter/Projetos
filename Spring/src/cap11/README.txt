
- Working with Spring and Hibernate
- Writing Spring-free repositories with contextual sessions
- Using JPA with Spring
- Automatic JPA repositories with Spring Data



	Spring fornece suporte para varios persistence apis. 
		- hibernate
		- iBatis
		- Java Data Object (JDO)
		- Java persistence api (jpa)
		
		
	
	Spring com Hibernate
		
		O principal elemento do Hibernate é o Session. Com o session, podemos fazer 
		update, insert, delete,... 
		
		Para controlar o session existe o SessionFactory, ele abre e fecha os hibernates
		sessions. 
		
		No spring a forma de obter o session é atraves do Hibernate session factory bean. 
		Existem 3 diferentes: 
		
			- org.springframework.orm.hibernate3.LocalSessionFactoryBean
			- org.springframework.orm.hibernate3.annotation.AnnotationSession-FactoryBean
			- org.springframework.orm.hibernate4.LocalSessionFactoryBean
		
		E escolha do bean depende da versao do hibernate e de que forma estamos usando: 
		
			Se for hibernate 3.2 ou maior (até 4.0), e estamos usando o mapeamento em xml, 
			devemos usar o LocalSessionFactoryBean. 
			
			@Bean
			public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
				LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
				sfb.setDataSource(dataSource);											<-- indicando o datasource
				sfb.setMappingResources(new String[] { "Spitter.hbm.xml" });			<-- indicando o mapeamento
				Properties props = new Properties();
				props.setProperty("dialect", "org.hibernate.dialect.H2Dialect");		<-- configuracoes do hibernate
				sfb.setHibernateProperties(props);
				return sfb;
			}

			
			
			Se o hibernate for anterior ao v 4.0, e estivermos usando annotations, devemos 
			usar o AnnotationSessionFactoryBean
			
				@Bean
				public AnnotationSessionFactoryBean sessionFactory(DataSource ds) {
					AnnotationSessionFactoryBean sfb = new AnnotationSessionFactoryBean();
					sfb.setDataSource(ds);
					sfb.setPackagesToScan(new String[] { "com.habuma.spittr.domain" });
					Properties props = new Properties();
					props.setProperty("dialect", "org.hibernate.dialect.H2Dialect");
					sfb.setHibernateProperties(props);
					return sfb;
				}

			
			Se for o hibernate4 ou acima, devemos usar o LocalSessionFactoryBean, mas do pacote
			hibernate4. Ele serve tanto para Annotation quanto para xml
			
				@Bean
				public LocalSessionFactoryBean sessionFactory(DataSource dataSource) {
					LocalSessionFactoryBean sfb = new LocalSessionFactoryBean();
					sfb.setDataSource(dataSource);
					sfb.setPackagesToScan(new String[] { "com.habuma.spittr.domain" });
					Properties props = new Properties();
					props.setProperty("dialect", "org.hibernate.dialect.H2Dialect");
					sfb.setHibernateProperties(props);
					return sfb;
				}
						
			
** Podemos usar a propriedade packagesToScan para procurar as classes que usam as annotations do jpa ou podemos
declarar uma-a-uma com o annotatedClasses
	
	sfb.setAnnotatedClasses(
		new Class<?>[] { Spitter.class, Spittle.class }
	);

Se o dominio de classes for pequeno, é uma boa opcao usar o annotatedClasses, senao usaro o packagesToScan. 


	Para usar o hibernate nas versoes anteriores era usado o HibernateTemplate. No entanto ele deixava 
	o spring muito amarrado com o hibernate. Nas novas versoes, o modelo ideal é injetar o 
	SessionFactory no nosso repositorio e obter o session: 
	
	---- apesar de nao aparecer no livro, esta escrito que a classe esta anotada com um @Repository.
	
	
		// injetando o sessionFactory
		@Inject 
		public HibernateSpitterRepository(SessionFactory sessionFactory) {
			this.sessionFactory = sessionFactory;
		}
		
		//obtendo o currentSession do sessionFactory. 
		private Session currentSession() {
			return sessionFactory.getCurrentSession();
		}
		public long count() {
			return findAll().size();
		}
		
		public Spitter save(Spitter spitter) {
			Serializable id = currentSession().save(spitter);   <-- acessando o session e usando. 
			return new Spitter((Long) id,
			spitter.getUsername(),
			spitter.getPassword(),
			spitter.getFullName(),
			spitter.getEmail(),
			spitter.isUpdateByEmail());
		}
		
		public Spitter findOne(long id) {
			return (Spitter) currentSession().get(Spitter.class, id);     <-- acessando o session e usando. 
		}
		
		public Spitter findByUsername(String username) {
			return (Spitter) currentSession() 					<-- acessando o session e usando. 
				.createCriteria(Spitter.class)
				.add(Restrictions.eq("username", username))
				.list().get(0);
		}

		public List<Spitter> findAll() {
			return (List<Spitter>) currentSession()					<-- acessando o session e usando. 
			.createCriteria(Spitter.class).list();
		}
	}
				

-- procurar mais detalhes na internet sobre o @Repository e para que ele serve jutnamente com o 
PersistenceExceptionTranslationPostProcessor que ele usa 
	@Bean
	public BeanPostProcessor persistenceTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}


	
	
	SPRING E JPA
	
		O jpa usa o EntityManagerFactory para ter acesso ao EntityManager. 
		O entitymanager pode ser de dois tipos: 
		
			- application-managed: o entitymanager pe obtido pela aplicacao que o controla. A app
					abre transacao, abre em e fecha em. 
			
			- container-managed: o entitymanager fica a cargo de um container (jee) e a app acessa
					via jndi. 
					
		
		Em ambos os casos, a interface entitymanager é a mesma, o que muda é a forma como é criada. 
		O applicationmanaged é atraves do EntityManagerFactory obtido do metodo createEntityManager() 
		da classe PersistenceProvider. 
		Ja o containermanaged é obtido do EntityManagerFactory retornado do metodo 
		createContainerEntityManagerFactory do PersistenceProvider: 
		
			Ambos vem do PersistenceProvider: 
				createEntityManagerFactory()
				createContainerEntityManagerFactory() 
				
		
		O spring vai controlar ambos
			- LocalEntityManagerFactoryBean produces an application-managed Entity-ManagerFactory.
			- LocalContainerEntityManagerFactoryBean produces a container-managed EntityManagerFactory.		
		
		
		APPLICATION MANAGED
			No application managed, é obrigatorio um persistence.xml aparecer na pasta 
			META-INF  da aplicacao. 
			
			O arquivo deve ter pelo menos um persistence-unit declarado. 
			
			para criar o bean: 
			
				@Bean
				public LocalEntityManagerFactoryBean entityManagerFactoryBean() {
					LocalEntityManagerFactoryBean emfb = new LocalEntityManagerFactoryBean();
					emfb.setPersistenceUnitName("spitterPU");
					return emfb;
				}
			
		CONTAINER MANAGED
			Quando usamos o container managed, as informacoes nao precisam ser fornecidas 
			pelo persistence.xml, mas pelo container (spring no caso). 
			
			Exmplo de como criar o bean 
			
			@Bean
			public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource,
														 JpaVendorAdapter jpaVendorAdapter) {
				
				LocalContainerEntityManagerFactoryBean emfb = new LocalContainerEntityManagerFactoryBean();
				emfb.setDataSource(dataSource);
				emfb.setJpaVendorAdapter(jpaVendorAdapter);
				return emfb;
			}
						
			Podemos estipular um vendoAdapter para a configuracao. Os vendors adapters disponvieis: 
			
			- EclipseLinkJpaVendorAdapter
			- HibernateJpaVendorAdapter
			- OpenJpaVendorAdapter
			- TopLinkJpaVendorAdapter (deprecated in Spring 3.1)
			
			
			EX:
			
				@Bean
				public JpaVendorAdapter jpaVendorAdapter() {
					HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
					adapter.setDatabase("HSQL");
					adapter.setShowSql(true);
					adapter.setGenerateDdl(false);
					adapter.setDatabasePlatform("org.hibernate.dialect.HSQLDialect");
					return adapter;
				}
							
** o parametro mais importante é o setDatabase(). Existe uma lista na pag 314 com alguns 
disponiveis. 

		
		A necessidade ainda do persistence.xml é de listar as classes anotadas com @Entity 
		que fazem parte do persistence-unit. 
		Podemos mudar isso com o codigo abaixo: 
		
			@Bean
			public LocalContainerEntityManagerFactoryBean entityManagerFactory(
							DataSource dataSource, JpaVendorAdapter jpaVendorAdapter) {
			
				LocalContainerEntityManagerFactoryBean emfb =
				new LocalContainerEntityManagerFactoryBean();
				emfb.setDataSource(dataSource);
				emfb.setJpaVendorAdapter(jpaVendorAdapter);
				emfb.setPackagesToScan("com.habuma.spittr.domain");
				return emfb;
			}			
	
		O setPackagesToScan() vai procurar no pacote especificado todas as classes anotadas 
		com @Entity. 
		
		Podemos apagar o persistence.xml
		
		
	
	CRIANDO O REPOSITORIO
	
		Para criar o repositorio, nao precisamos usar o JpaTemplate
		
		Usando o EntityManagerFactory: 
		
			@Repository
			@Transactional
			public class JpaSpitterRepository implements SpitterRepository {
				
				@PersistenceUnit
				private EntityManagerFactory emf;
			
				public void addSpitter(Spitter spitter) {
					emf.createEntityManager().persist(spitter);
				}
				public Spitter getSpitterById(long id) {
					return emf.createEntityManager().find(Spitter.class, id);
				}
				public void saveSpitter(Spitter spitter) {
					emf.createEntityManager().merge(spitter);
				}
				...
			}
			
** cada metodo chama o createEntityManager() pq ele não é thread-safe, ou seja, nao podemos reter um 
entitymanager em uma instancia singleton. Por isso deve ser criado toda chamada um diferente. 
Para ajuda nisso: 

			@Repository
			@Transactional
			public class JpaSpitterRepository implements SpitterRepository {
			
				@PersistenceContext
				private EntityManager em;					<-- diretor o entitymanager
			
				public void addSpitter(Spitter spitter) {
					em.persist(spitter);
				}
			
				public Spitter getSpitterById(long id) {
					return em.find(Spitter.class, id);
				}
			
				public void saveSpitter(Spitter spitter) {
					em.merge(spitter);
				}
				...
			}			

** no entanto continua nao sendo thread-safe. Mas o persistencecontext cria um proxy, que nos 
da o comportamente de thread-safe, mas nao é. A instancia do entitymanager por tras é sempre diferente. 

Essas annotations nao sao do Spring. Para que o spring possa entender as annotation e adicionar 
o entitymanager ou entitymanagerfactory, devemos criar o bean do PersistenceAnnotationBeanPostProcessor
	
	@Bean
	public PersistenceAnnotationBeanPostProcessor paPostProcessor() {
		return new PersistenceAnnotationBeanPostProcessor();
	}
						
** a classe esta anotada tambem com o @Repository e o @Transation. o transational diz que 
o fluxo que ira passar na classe sera transacional. 
O @Repository é para fazer translate de exception????

PersistenceExceptionTranslationPostProcessor????

tambem precisa do bean abaixo
	@Bean
	public BeanPostProcessor persistenceTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
					

			
	Repositorios automaticos com SPRING DATA JPA.
	
		Mesmo fazendo uma implementacao mais alto nivel com hibernate, jpa,... ainte temos implementacoes
		especificas nos metodos. Por exemplo, usando o session ou entitymanager (persist, merge,..)
		
		Com o spring data esse problema é resolvido e nao precisamos implementar nada. 
		
		1) declarar interface JpaRepository para o spring saber que queremos cnotrola um objeto 
			Spitter, que tem uma chave do tipo Long
		
			public interface SpitterRepository extends JpaRepository<Spitter, Long> {}
			
			Essa interface ja vai nos fornecer implicitamente 18 metodos para interagir com os 
			objetos Spitter.
			
			Mas ainda assim precisamos sobrescrever esses 18 metodos criando um SpitterRepository? Nao: 
			 
			O spring cria o repository automaticamente com as implementacoes: 
			
		2) para criar o repository: <jpa:repositories base-package="com.habuma.spittr.db" />
		
			<?xml version="1.0" encoding="UTF-8"?>
			<beans xmlns="http://www.springframework.org/schema/beans"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xmlns:jpa="http://www.springframework.org/schema/data/jpa"
				xsi:schemaLocation="http://www.springframework.org/schema/data/jpa
				http://www.springframework.org/schema/data/jpa/spring-jpa-1.0.xsd">
			
				<jpa:repositories base-package="com.habuma.spittr.db" />
				...
			</beans>
			 
			 Isso faz com que o Spring saia procurando no base-package as interfaces marcadas com o 
			 @Repository e gera uma implementacao para elas. 
			 
			** Com o java configuration: 
			 	
			 	@Configuration
				@EnableJpaRepositories(basePackages="com.habuma.spittr.db")
				public class JpaConfiguration {
					...
				}
							
** os 18 metodos implementados sao herança de JpaRepository, PagingAndSortingRepository, and CrudRepository.
** Essa implementacao é gerada quando a aplicacao sobe, nao quando é compilada. 

			Mas e se precisarmos de algum metodo alem desses 18? 
			Podemos implemntar: 
			
		
		Definindo Query Methods
		
			Se precisa definir um metodo customizado de procura por um nome, por exemplo, 
			podemos alterar a interface e definir um metodo para isso:
			
				public interface SpitterRepository extends JpaRepository<Spitter, Long> {
					Spitter findByUsername(String username);
				}
		
			O spring vai inferir pelo nome dometodo, pelo parametro passado e pelo retorno, 
			que queremos buscar um elemento pela string username (1, nao uma lista) e vai 
			criar a implementacao do metodo juntamente com os outros 18. 
			
			O spring busca o padrao no nome do metodo: 
				verbo+sujeito+predicado
			Ex: readSpitterByFirstnameOrLasnameOrderByLastname()
				
				verbo + Sujeito +  Predicado
				read 	Spitter 	By....
			
				
				Podem ser usado 4 predicados: (get read find sao sinonimos)
					get - retorna objetos
					read - retorna objetos
					find - retorna objetos
					count - retorna um numero
			
		
				O sujeito pode ou nao ser passado. Se nao for é implicito (O spring sabe pelo 
				generic que foi passado no JpaRespository interface). 
				
				** uma excecao. Se no sujeito estiver a palava Distinct, entao o Spring vai 
				se organizar para retornar um valor distinto. 
				
				O predicado tem varias comparacoes que podem ser feitas. 
				Tem uma lista na pag 322 com alguns predicados
				
				Existem varias outras caracteristicas que vale a pena olhar o manual....
				
			Custom Queries com o SD JPA
			
				Para implementar um metodo em que ficaria dificil de organizar por nome, ou
				um nome muito grande, podemos estipular a query com o @Query: 
				
				para buscar todos os spitter que tem email gmail. 
				
					@Query("select s from Spitter s where s.email like '%gmail.com'")
					List<Spitter> findAllGmailSpitters();
		
			Mair poderoso
				
				se o @Query ainda nao for suficiente para o que precisamos, ainda podemos implementar um 
				recurso mais baixo nivel mas mesmo assim usando o Spring data jpa. 
				
				Quanto tem uma interface tipo o SpitterRepository, o spring mesmo assim vai procurar 
				por uma classe com o mesmo nome da interface, seguida por um Impl.
					SpitterRepositoryImpl
					
				O spring entao vai pegar esse implementacao e fazer o merge com a classe que ele irá implementar. 
				
				Exemplo de uma implementacao: 
				
					public class SpitterRepositoryImpl implements SpitterSweeper {
						@PersistenceContext
						private EntityManager em;
					
						public int eliteSweep() {
							String update =
								"UPDATE Spitter spitter " +
								"SET spitter.status = 'Elite' " +
								"WHERE spitter.status = 'Newbie' " +
								"AND spitter.id IN (" +
								"SELECT s FROM Spitter s WHERE (" +
								" SELECT COUNT(spittles) FROM s.spittles spittles) > 10000" +
								")";
							return em.createQuery(update).executeUpdate();
						}
					}
		
		** a interface que a classe implementa é a SpitterSweeper. Na classe SpitterRepository, temos que 
		adicionar essa interface tambem: 
				public interface SpitterRepository extends JpaRepository<Spitter, Long>,SpitterSweeper {
					....
				}
						
		e entao no final: 
		
			@EnableJpaRepositories(basePackages="com.habuma.spittr.db",repositoryImplementationPostfix="Helper")
		ou no xml
			<jpa:repositories base-package="com.habuma.spittr.db" repository-impl-postfix="Helper" />
		
		
		
		
		
		
		
		
		






