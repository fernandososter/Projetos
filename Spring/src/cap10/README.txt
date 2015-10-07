Spring e JDBC

- Defining Spring’s data-access support
- Configuring database resources
- Working with Spring’s JDBC template



	Acesso aos dados no hibernate é feito atraves do esquema de DAO, onde definimos as interfaces
	que ficam expostas. 
	
	As exception do jdbc sao muito genericas e geralmente nao oferecem opcao de tratamento. Quando sao 
	lançadas nao tem hipotese de recover. O hibernate até tenta resolver isso tratando erros de forma 
	mais especifica, mas essa é uma tecnica especifica do hibernate. 
	
	
	O spring oference varios tipos de exceptions (praticamente uma para cada possivel erro). 
	Na pagina 285 tem uma lista com alguns (nao todos). 
	
	O que todas essas exceptions tem em comum é o fato que sao subtipos de DataAccessException, que é 
	uma unchecked exception (nao precisa ser capturada?)
	
** No spring tem os checked e unchecked exception. A segunda nao precisa de catch. 

	No spring, o acesso aos dados é separado por interfaces e existem dois modulos: 
		- templates = gerencia a parte fixa do processo
		- callbacks = gerecia a parte do business (o que queremos fazer com os dados). 
		
		
		Templates: 
			Entre as partes do template, podemos destacar: 
				- controlar transactions
				- gerenciar recursos
				- gerenciar exception
				
		Callbacks:
				-criar statements
				-binding parameters
				- verificar resultados
		
	Alguns templates ja vem prontos, como se quisermos executar queries em sql, podemos usar 
	o JdbcTemplate. Se quiser usar um ORM, podemos usar o HibernateTemplate ou o JpaTemplate. 
	Alguns da lista: 
	
		jca.cci.core.CciTemplate - JCA CCI Connections
		jdbc.core.JdbcTemplate - jdbc connections
		jdbc.core.namedparam.NamedParameterJdbcTemplate - jdbc com suporte ao namedparameters
		jdbc.core.simple.SimpleJdbcTemplate - jdbc connection (deprecated no 3.1)
		orm.hibernate3.HibernateTemplate - hibernate 3.x
		orm.ibatis.SqlMapClientTemplate - Ibatis SqlMap
		orm.jdo.JdoTempalte - Java Data Object implementation
		orm.jpa.JpaTemplate - JPA
		
		
		
CONFIGURANDO UM DATASOURCE

	O spring suporta criar datasources por 
		- JDBC Driver
		- Lookup do JNDI
		- pool connections. 
		
	JNDI
		
	Com o spring tag <jee:jndi-lookup> é possivel fazer looup de qualquer elemento do container
	(jboss, tomcat,...) e adiciona-lo via wired em qualquer bean. 
	
	Exemplo:
		<jee:jndi-lookup id="dataSource" jndi-name="/jdbc/SpitterDS" resource-ref="true" />

** o resource-ref no final adiciona o java:comp/env/

	Tambem é possivel adicionar o jndi com java configurations usando JndiObjectFactoryBean: 
	
		@Bean
		public JndiObjectFactoryBean dataSource() {
			JndiObjectFactoryBean jndiObjectFB = new JndiObjectFactoryBean(); 
			jndiObjectFB.setJndiName("jdbc/SpittrDS");
			jndiObjectFB.setResourceRef(true);
			jndiObjectFB.setProxyInterface(javax.sql.DataSource.class);
			return jndiObjectFB;
		}	
		
	
	POOLED DATASOURCE
	
		O spring nao fornece um pool datasource. No entanto, podemos usar as seguintes tecnologias
		juntamente com o spring: 
		
			- Apache Commons DBCP (http://jakarta.apache.org/commons/dbcp)
			- c3p0 (http://sourceforge.net/projects/c3p0/)
			- BoneCP (http://jolbox.com/) 	
		
		
			para o DBCP, no xml
				<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource"
					p:driverClassName="org.h2.Driver"
					p:url="jdbc:h2:tcp://localhost/~/spitter"
					p:username="sa"
					p:password=""
					p:initialSize="5"
					p:maxActive="10" />
							
			ou no javaconfiguration: 
			
				@Bean
				public BasicDataSource dataSource() {
					BasicDataSource ds = new BasicDataSource();
					ds.setDriverClassName("org.h2.Driver");
					ds.setUrl("jdbc:h2:tcp://localhost/~/spitter");
					ds.setUsername("sa");
					ds.setPassword("");
					ds.setInitialSize(5);
					ds.setMaxActive(10);
					return ds;
				}
		
	
	JDBC Based Datasoruce
		
		podemos escolher 3 opcoes de conexao, que estao no pacote org.springframework.jdbc.datasource
		
			- DriverManagerDataSource: cada vez que é solicitado cria uma conexao e retorna
			- SimpleDriverDataSource:  
			- SingleConnectionDataSource: uma conexao só, que é retornada cada vez que alguem chama
		
		Ex: no java configuration para o DriverManagerDataSource
		
				@Bean
				public DataSource dataSource() {
					DriverManagerDataSource ds = new DriverManagerDataSource();
					ds.setDriverClassName("org.h2.Driver");
					ds.setUrl("jdbc:h2:tcp://localhost/~/spitter");
					ds.setUsername("sa");
					ds.setPassword("");
					return ds;
				}
		
			e no xml: 
				<bean id="dataSource"
					class="org.springframework.jdbc.datasource.DriverManagerDataSource"
					p:driverClassName="org.h2.Driver"
					p:url="jdbc:h2:tcp://localhost/~/spitter"
					p:username="sa"
					p:password="" />
		
	
	
** cuidado com JDBC Datasource em producao, não é o mais indicado. 

	EMBEDDED DATASOURCE	
		Faz parte da aplicacao e é uma boa opcao para fazer testes. Roda como parte da aplicacao 
		e nao como parte separada. Permite que seja zerada a cada reinicializacao .
		
		Quem faz a configuracao é o <jdbc:embedded-database> 
		Esse é um exemplo para H2 database. Tambem podemos usar o DERBY 
		
			<?xml version="1.0" encoding="UTF-8"?> 
			<beans xmlns="http://www.springframework.org/schema/beans"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xmlns:jdbc="http://www.springframework.org/schema/jdbc"
				xmlns:c="http://www.springframework.org/schema/c"
				xsi:schemaLocation="http://www.springframework.org/schema/jdbc
				http://www.springframework.org/schema/jdbc/spring-jdbc-3.1.xsd
				http://www.springframework.org/schema/beans
				http://www.springframework.org/schema/beans/spring-beans.xsd">
					...
				<jdbc:embedded-database id="dataSource" type="H2"> 
					<jdbc:script location="com/habuma/spitter/db/jdbc/schema.sql"/> 
					<jdbc:script location="com/habuma/spitter/db/jdbc/test-data.sql"/> 
				</jdbc:embedded-database>
					...
			</beans>	
	
** um embedded-database pode ter um ou mais jdbc:script. No caso acima, um é o schema de criacao
e o segund sao os dados. 

** o id do bean que será criado é 'dataSource'. Quando precisar, basta injetar esse bean. 

		Para usar java configurations, é a classe EmbeddedDatabaseBuilder
		
			@Bean
			public DataSource dataSource() {
				return new EmbeddedDatabaseBuilder()
					.setType(EmbeddedDatabaseType.H2)
					.addScript("classpath:schema.sql")
					.addScript("classpath:test-data.sql")
					.build();
			}
	
		
		

	Com essas caracteristicas, podemos criar varios profiles e diferentes tipo de conexoes
	para diferentes ambientes: 
	
	ex: 
		@Configuration
		public class DataSourceConfiguration {
			
			@Profile("development")
			@Bean
			public DataSource embeddedDataSource() {
				return new EmbeddedDatabaseBuilder()
					.setType(EmbeddedDatabaseType.H2)
					.addScript("classpath:schema.sql")
					.addScript("classpath:test-data.sql")
					.build();
			}
			
			@Profile("qa")
			@Bean
			public DataSource Data() {
				BasicDataSource ds = new BasicDataSource();
				ds.setDriverClassName("org.h2.Driver");
				ds.setUrl("jdbc:h2:tcp://localhost/~/spitter");
				ds.setUsername("sa");
				ds.setPassword("");
				ds.setInitialSize(5);
				ds.setMaxActive(10);
				return ds;
			}
			
			@Profile("production")
			@Bean
			public DataSource dataSource() {
				JndiObjectFactoryBean jndiObjectFactoryBean = new JndiObjectFactoryBean();
				jndiObjectFactoryBean.setJndiName("jdbc/SpittrDS");
				jndiObjectFactoryBean.setResourceRef(true);
				jndiObjectFactoryBean.setProxyInterface(javax.sql.DataSource.class);
				return (DataSource) jndiObjectFactoryBean.getObject();
			}
		}
	
	
		
JDBC 
	
	O spring vai reduzir a quantidade de codigo que precisamos digitar usando 
	o template e os callbacks. 
		Existem tres tipos de templates que podemos escolher:
			- JdbcTemplate : mais simples, fornece acesso ao banco por jdbc e queries com parametro posicionais
			- NamedParameterJdbcTemplate : permite queries com parametros com nomes
			- SimpleJdbcTempalte: permite as vantagens do java 5, generics, lists,...
		
		A partir do Spring 3.1 o simpleJdbc foi marcado como deprecated, e as funcionalidades
		agregadas ao JdbcTemplate. Por isso, apartir da 3.1, só precisamos decidir se vamos
		usar o named ou nao. 
		
		
		Para criar um JdbcTemplate, a unica coisa que precisamos é de um DataSource, que 
		sera injetado: 
		
			@Bean
			public JdbcTemplate jdbcTemplate(DataSource dataSource) {
				return new JdbcTemplate(dataSource);
			}
	
		O spring vai injetar qualquer dataSource criado (nos exemplos anteriores) e podemos
		usar no nosso codigo:
		
			@Repository
			public class JdbcSpitterRepository implements SpitterRepository {
				private JdbcOperations jdbcOperations;
				
				@Inject
				public JdbcSpitterRepository(JdbcOperations jdbcOperations) {
					this.jdbcOperations = jdbcOperations;
				}
				...
			}
			
		
		
		Para utilizar o jdbc que foi injetado: 
		
			public void addSpitter(Spitter spitter) {
				jdbcOperations.update(INSERT_SPITTER,
					spitter.getUsername(),
					spitter.getPassword(),
					spitter.getFullName(),
					spitter.getEmail(),
					spitter.isUpdateByEmail());
			}		
	
	
		Para ler dados :	
			
			public Spitter findOne(long id) {
				return jdbcOperations.queryForObject(
					SELECT_SPITTER_BY_ID,                 <- string query que sera usada 
					new SpitterRowMapper()					<- RowMapper é onde sera colocado o resultado do resultset
					,id);									<- elementos que serao os ids da query
			}
	
			e para cada resultado do select, sera chamado o metodo mapRow abaixo, que irá fazer o 
			parser da informacao automaticamente. 
		
			private static final class SpitterRowMapper implements RowMapper<Spitter> {
				public Spitter mapRow(ResultSet rs, int rowNum) throws SQLException {
					return new Spitter(
						rs.getLong("id"),
						rs.getString("username"),
						rs.getString("password"),
						rs.getString("fullName"),
						rs.getString("email"),
						rs.getBoolean("updateByEmail"));
				}
			}
	

Exemplo de uso de namedjdbc na pag 303



	
	
	
	