

Para poder determinar metodos e classes que ser�o deployados em  ambientes diferentes, 
o spring fornece a opcao de criar profiles: 

A classe Developerment somente ser� usada se dev estiver ativo
@Configuration
@Profile("dev")
public class DevelopmentProfileConfig { ... } 

A classe Production se prod estiver ativo. 
@Configuration
@Profile("prod")
public class ProductionProfileConfig { ... }

Assim nao precisamos recompilar a app para ambientes diferentes. 

 ** at� a v3.1 o @Profile er� somente a nivel de classe
  a partir da 3.2, o @Profile pode ser usado em metodos. 
  
 
 Dentro do mesmo config: (podemos escolher o metodo)
 
 	@Bean
  	@Profile("dev")
	public DataSource embeddedDataSource() { ... } 
	
	@Bean
	@Profile("prod")
	public DataSource jndiDataSource() {...}
	
** metodos nao anotados com o @Profile ser�o sempre criados, independente do profile ativo


PARA ATIVAR UM PROFILE

Dentro do xml, na declaracao do <beans>, colocar um "profile="dev"" ou qualquer outro profile. 
Esses arquivos de profile ser�o carregados no momento do deploy, por isso podemos muda-los. 

Podemos, para evitar criar muitos arquivos, criar dentro do mesmo <beans> outros <beans> 

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:jdbc="http://www.springframework.org/schema/jdbc"
	xmlns:jee="http://www.springframework.org/schema/jee"
	xmlns:p="http://www.springframework.org/schema/p"
	xsi:schemaLocation="
	http://www.springframework.org/schema/jee
	http://www.springframework.org/schema/jee/spring-jee.xsd
	http://www.springframework.org/schema/jdbc
	http://www.springframework.org/schema/jdbc/spring-jdbc.xsd
	http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans.xsd">

	<beans profile="dev">
	 ...
	</beans> 

	<beans profile="QA">
	 ...
	</beans> 

	<beans profile="prod">
	 ...
	</beans> 
</beans> 

Mas como o Spring vai saber qual profile usar? 
Existe o profile ativo:  spring.profiles.active
e o defualt:  spring.profiles.default

Se o ativo nao estiver setado, ser� usado o default. Se nao tiver nenhum, nao vai
criar os beans que estao dentro dos profiles, somente os que estao fora. 

Para setar o ativo e o default, existem varias formas: 

	As initialization parameters on DispatcherServlet
	 As context parameters of a web application
 	As JNDI entries
	 As environment variables
	As JVM system properties
	Using the @ActiveProfiles annotation on an integration test class
	
Exemplo de web.xml para setar o default profile (pag 71): 

setando por context: 

	<context-param>
		<param-name>spring.profiles.default</param-name>	
		<param-value>dev</param-value>
	</context-param>	

por servlet
	
	<servlet>
		<servlet-name>appServlet</servlet-name>
		<servlet-class>
			org.springframework.web.servlet.DispatcherServlet
		</servlet-class>
		<init-param>
			<param-name>spring.profiles.default</param-name>
			<param-value>dev</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>


** No entanto, se tiver um active profile setado, o default nao tera efeito, 
ja que o active tem preferencia. 

** sao default profiles e active profiles, ou seja, pode ter mais de um default
e mais de um sctive. Se fizer sentido...

Para usar o @AtiveProfile com testes integrados, o ex: (assim podemos simular prod, qa,...)

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={PersistenceTestConfig.class})
@ActiveProfiles("dev")
public class PersistenceTest {
...
}


CONDITIONAL

Pode ser aplicado a declaracao do bean no config e somente sera executado
se a condicao passada for verdadeira (por ex, uma classe exista), senao o bean 
sera ignorado: 

	@Bean
	@Conditional(MagicExistsCondition.class)
	public MagicBean magicBean() {
		return new MagicBean();
	}

Podemos uma interface Condition, que tem um metodo matches(). Se esse metodo retornar true, 
entao o bean será criado, se retornar false, será ignorado. 

Exemplo de classe checando se existe uma variavel de sistema magic: 
Nesse caso usamos o MagiExistsCondition no annotation

	public class MagicExistsCondition implements Condition {
		public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
			Environment env = context.getEnvironment();
			return env.containsProperty("magic");
			
		}
	}

No final da 73 e inicio da 74 tem uma lista dos metodos do context e para o que pode ser usado. 

Por exemplo, o AnnotatedTypeMetadata tem um metodo isAnnotated(string) para verificar se  um bean esta
anotado com alguma informacao que querremos. 


Resolvendo ambiguidades

Quando temos mais de um @Component que caiba no @Autowire. O livro deu o exemplo do 
dessert: 

	@Autowired
	public void setDessert(Dessert d)....
	
Onde dessert é uma interface, implementada por cake, cookie e icecream (todos com o @Compoent). 

O spring nao sabera o que fazer e fará throws de uma NonUniqueBeanDefinitionException

Solucoes: 

1- declarar um bean favorito: 

	juntamente com o @Component podemos colocar um @Primary, assim o anotado terá precedencia sobre 
	os outros em caso de ambiguidade: 
	
	@Component
	@Primary
	public class Cake implements Dessert{ 
	
	Ou entao na declaracao do bean (dentro do config): 
	
	@Bean
	@Primary
	public Dessert iceCream() {
	
	ou em xml:
	<bean id="iceCream" class="com.desserteater.IceCream" primary="true" />
	
	*** isso resolve até termos mais de um anotado com o @Primary (nesse caso voltamos ao velho problema)
	

2 - usando qualificador (@Qualifier)
	Para especificar qual bean queremos injetar. Pode ser usado com autowired e inject
	
	@Autowired
	@Qualifier("iceCream")
	public void setDessert(Dessert d){ 	
	
	onde iceCream á o beanID.
	
	** e o que acontece se fizermos um refactoring e alterar de de iceCream para gelato? 
	R: vai parar de funcionar. 
	
	Podemos atribuir o qualifier do outro lado tambem, para o spring pode se encontrar por mais
	que o nome da classe seja alterado, colocando um qualifier do lado do component: 
	
	@Component
	@Qualifier("cold")
	public class IceCream implements Dessert....
	
	agora podemos chamar o qualifier cold em qualquer lugar. Inclusive na declaracao do bean: 
	
	@Bean
	@Qualifier("cold")
	public Dessert iceCream()...
	
	Se precisar, podem ser usados mais de um qualifier para resolver ponto que ocorrer
	ambiguidade novamente: 
	
	@Bean
	@Qualifier("cold")
	@Qualifier("hot"),...
	
	No entao o compilador java nao permiter annotar algo com duas annotatin iguais e com 
	valores diferentes. (Até o java8, apatir do 8, podemos com o @Repeatable na declaracao do 
	annotatin. Mas nao é o caso do Qualifier). 
	
	Para resolver, podemos criar as anotations e usa-las como qualificadores: 
	
	@Target({ElementType.CONSTRUCTOR, ElementType.FIELD,
			ElementType.METHOD, ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	@Qualifier
	public @interface Cold { }
	
	@Target({ElementType.CONSTRUCTOR, ElementType.FIELD,
			ElementType.METHOD, ElementType.TYPE})
	@Retention(RetentionPolicy.RUNTIME)
	@Qualifier
	public @interface Hot { }
	
	

SCOPING BEANS

Beans sao singleton, ou seja, nao importa quantas vezes sao injetados, é sempre 
a mesma instancia usada. No entanto as vezes precisamos criar classes mutaveis
e nao seguras para serem injetadas em outros lugadores.

O spring define os seguintes escopos: 
	
	- Singleton: uma instancia para toda a aplicacao
	- Prototype: Uma instancia para cada vez que ocorre uma injecao ou 
				 é obtido do contexto. 
	- Session: em uma web app, um bean para toda a session
	- Request: em uma web app, um bean por request. 
	
Para mudar o escopo, usar o @Scope junto com o @Component ou @Bean: 

	@Component
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // ou @Scope("prototype")
	public class Notepad{...}
	
e no config: 
	
	@Bean
	@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public Notepad notepad()...

XML:
	<bean id="notepad" class="com.myapp.Notepad" scope="prototype" />
	

Para criar um bean de sessao (web app): 
	
	@Component
	@Scope( value = WebApplicationContext.SCOPE_SESSION, 
			proxyMode=ScopedProxyMode.INTERFACES)
	public ShoppingCart cart() {...} 
	
** o scope usa uma constante do WebApplicationContext. Ele garante que 
cada cliente vai ter uma sessao e somente um objeto ShoppingCart por sessao. 

Mas como estamos lidadno com bean de sessao, se for injetado em um bean 
singleton, podemos ter problemas. O bean de sessao pode ainda nem existir  quando injetado
ou, como saber qual bean usar, pois cada usuario tem o seu session bean. 

Para resolver isso o spring cria um proxy. Esse proxy vai chamar o bean desejado
em lazy style. 
A segunda parte do Scope serve para isso.
Se o shoppingcart for uma classe concreta, nao podemos indicar uma interface. Temos que indicar
ele como sendo uma classe concreta. Nesse caso, usar: 

ScopedProxyMode.TARGET_CLASS e nao ScopedProxyMode.INTERFACES

**figura final da pagina 83. 



RUNTIME VALUE INJECTION

até agora a passagem de informacoes para os bean via injection foram atraves de hardcode. 

	
	@Bean
	public CompactDisc sgtPeppers() {
		return new BlankDisc(
		"Sgt. Pepper's Lonely Hearts Club Band",
			"The Beatles");
	}
ou em xml: 

	<bean id="sgtPeppers" class="soundsystem.BlankDisc"
			c:_title="Sgt. Pepper's Lonely Hearts Club Band"
			c:_artist="The Beatles" />
 
Para evitar a passagem via hardcode, existem duas formas: 

	- Property Placeholders
	- Spring Expression Language (SpEL)
	

O jeito mais simples é com um arquivo de properties e usando  o Environment

@Configuration
@PropertySource("classpath:/com/soundsystem/app.properties")
public class ExpressiveConfig{ 
	
	@Autowires
	Environment env; 
	
	... {
		env.getProperty("disc.title");
	}

O getProperty() ainda tem sobrescrita para ter um default value: 
	getProperty(String var, String default); 
	
e existe o: 
	T getProperty(String) ou getProperty(String,default), onde T pode ser, por exmeplo 
	integer. 
	
o environment tambem tem o getRequiredProperty(), onde se nao existir a informacao, ira 
ocorrer uma IllegalArgument

	Podemos usar no xml a passagem com o cname e ${}
	
		<bean id="sgtPeppers" class="soundsystem.BlankDisc"
				c:_title="${disc.title}"
				c:_artist="${disc.artist}" />



		
Quando estivermos usando um autowired e nao tem configuration file, podemos usar o 
@Value para injetar diretametente a propriedade: 

Na passagem de parametro dos metodos: 

	public BlankDisc( @Value("${disc.title}") String title, 
						@Value("${disc.artist}") String artist) { 
						

No entanto, é necessaria a configuracao do PropertyPlaceholderConfigurer ou do 
PropertySourcesPlacedholderConfigurer. 
O preferencial é o sources: 

@Bean
public static PropertySourcesPlaceholderConfigurer placeHolderConfigurer() {
	return new PropertySourcesPlaceholderConfigurer(); 
} 

ou no XML a tag: <context:property-placeholder /> dentro do beans. 


	

Spring Expression Language (SpEL)

nos permite alguns truques como: 

	- ability to reference beasn by their IDs
	- Inveking methods and accessing properties on objects
	- Mathematical, relational ad logical operations on values
	- regular expression matching
	- Collection manipulation
	
Para usar o SpEL, a sintaxe é #{}. Por exemplo #{1}.
Ou de uma forma mais complexa, 

#{T(System).currentTimeMillis()}

Pode usar para acessar outros bean. Considerando que exista um
bean com id sgtPeppers: 

	#{sgtPeppers.artist}

ou para acessar o systemProperties: 
	#{systemProperties['disc.title']}
	
Usando o @Value tambem é possivel: 


	public BlankDisc(
		@Value("#{systemProperties['disc.title']}") String title,
		@Value("#{systemProperties['disc.artist']}") String artist) {
			this.title = title;
			this.artist = artist;
	}	
	
E tambem com p-name ou c-name:
	
	<bean id="sgtPeppers"
		class="soundsystem.BlankDisc"
		c:_title="#{systemProperties['disc.title']}"
		c:_artist="#{systemProperties['disc.artist']}" />

Pode ser tanto em property ou constructor. 

suporta pontos flutuantes: #{9.86565}
suporta valores matematicos: #{4.545E98}
suporta booleanos: #{false}


Podemos usar para chmar funcoes: #{artistSelector.selectArtist()} 
Vai chamar a funcao selectArtist no bean

Para valores onde queremos verificar o retorno e entao chamar outra funcao, 
podemos usar um operador '?.'
Ex: #{artistSelector.selectArtist()?.toUpperCase()} 

O spring vai verificar o retorno de selectArtist() e se nao for null, 
vai aplicar o toUpperCase. Senao ira retornar null ao chamador. 


T() - vai retornar T. Por exemplo, T(java.lang.Math) ser para obter uma referencia para Math. 
Assim, o T de retorno fica valendo Math e podemos usa-lo ou passar como parametro para algum
lugar. 

#{T(java.lang.Math).PI} no da acesso ao PI. 

** somente para valores estaticos. 

Pag 92 tem as operacoes possiveis dentro do SpEL. Ex: 

#{2 * T(java.lang.Math).PI * circle.radius}

#{disc.title + ' by ' + disc.artist} para concatenar Strings

#{counter.total eq 100} ou #{counter.total == 100} para equals

#{scoreboard.score > 1000 ? "Winner!" : "Loser"} operacao ternarias, vai cuspis winner ou loser

#{disc.title ?: 'Rattle and Hum'} para verificar se é null (elvis pois parece o cabelo do elvis haha)

#{admin.email matches '[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.com'} regular expression para ver se é email valido

#{jukebox.songs[4].title} acessando lista (zero based)

#{'This is a test'[3]} acessando o quarto caracter da string 'this is a test' (s no caso)

#{jukebox.songs.?[artist eq 'Aerosmith']} procurando 'aerosmith' nos artistas em sons. 

#{jukebox.songs.^[artist eq 'Aerosmith']} ^ tras o primeiro encontrado

#{jukebox.songs.$[artist eq 'Aerosmith']} $ tras o ultimo encontrado

#{jukebox.songs.![title]} ! cria uma lista de uma lista (por exemplo, cria uma lista com todos os titles de songs) 









  
	
	

	
	

	
					
	
	
	
	
	
	
	
	 
	
	
		
	
	
	



	





