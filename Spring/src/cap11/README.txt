
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
					

			
	Repositorios automaticos com SPRING DATA.
	
		Mesmo fazendo uma implementacao mais alto nivel com hibernate, jpa,... ainte temos implementacoes
		especificas nos metodos. Por exemplo, usando o session ou entitymanager (persist, merge,..)
		
pag 318			
			
			
			
			
			
			
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		






