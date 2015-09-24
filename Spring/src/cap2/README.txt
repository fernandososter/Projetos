Wiring 
	
	Tres tipos de mecanismos para wirgin: 
		- explicit xml 
		- explicit java
		- implicit discovery e automatic wiring
		
** preferencia sempre pelo autmatico. Se preicsar fazer manual, pode escolher um 
ou usar mais de um no mesmo projeto. 
No entanto, xml deve ser sempre a ultima opção (devido a complexidade). 

Nenhum esquema bate o autmatico. Ele é dividido em duas perspectivas: 

	- component scanning : automaticamente descobre os beans a ser criado no application context. 
	- autowiring  : automaticamente satisfaz as dependencias. 

O autoscan nao vem habilitado por default. Precisa ter uma classe de config, anotado com
 	@Configuration
 	@ComponentScan
 A classe config definida com os anotations acima vai procurar no pacote em que esta e nos abaixo
 as classes anotadas com o @Component. Quando encontrar, vai usa-la. 
 
 Ex de como fazer com xml em soundsystem.xml
 
 	
 Para todo @Component é dado um ID. Esse id pode ser implicito (nao fornecedio e vira o nome da 
 classe com a primeira letra minuscula) ou explicito: 
 
		@Component("lonelyHeartsClub")
		public class SgtPeppers implements CompactDisc {
			...
		}

tambem podemos usar o annotation da jsr 330 (Java Dependency Injection) para dar o nome: 

@Named("lonelyHeartsClub") * geralmente @Component e @Named sao intercambiaveis. 

Se quiser mudar o endereço do de scan do componente, basta passar o parametro no annotation: 
Podemos usar essa configuração para colocar os configurations em somente um package, por exemplo. 
@ComponentScan("soundsystem")
@ComponentScan(basePackages="soundsystem")
@ComponentScan(basePackages={"soundsystem", "video"})

ou entao, usando o basePackageClasses e indicando as classes, o spring vai buscar os pacotes onde estao
as classes, e usar os pacotes como basePackge. 
@ComponentScan(basePackageClasses={CDPlayer.class, DVDPlayer.class})

Em termos de estrategia, o melhor é colocar uma interface vazia que define o pacote e usar a busca 
pela classe. 


@Autowired

Quando essa anotacao for encontrada no codigo, o spring vai procurar o bean, instanciar 
e passar como parametro no metodo anotado: 

	@Component
	public class CDPlayer implements MediaPlayer {
		private CompactDisc cd;
		
		@Autowired
		public CDPlayer(CompactDisc cd) {
			this.cd = cd;
		}
		
		public void play() {
			cd.play();
		}
	}

nao precisa ser somente no construtor, pode ser tambem em set ou outro metodo qualquer: 

		@Autowired
		public void setCompactDisc(CompactDisc cd) {
		
		@Autowired
		public void insertDisc(CompactDisc cd) {

Se o spring nao encontrar o bean correspondente, irá reportar uma exception durante a subida 
do applicatin context. Para evitar a exception: 
		@Autowired(required=false)
** no entanto o elemento pode ficar null!

** autowired é especifico do Spring. Se quiser usar o da jsr 330 é: 
	@Inject
Ambos podem ser usados, fazem a mesma coisa e são intercambiaveis. 



Explicit Wiring

As vezes precisamos fazer wiring de forma explicita. Por exemplo quando precisamos 
lidar com classes de terceiros que nao podemos alterar para colocar @Component e @Autowired. 

Para isso podemos usar XML ou JAVA: 

* Preferencialmente usar java e nao xml, pois java é mais poderoso. 

Para usar java, precisamos declara os JavaConfig. O ideal é deixa-lo aparte do business logic, 
pois nao sao CLASSES NORMAIS, SAO CONFIGURACOES. 

Uma classe se torna JavaConf com o annotation @Configuration. 
Sem o ComponentScan, temos que declarar os beans: 

	@Bean
	public CompactDisc sgtPeppers() {
	
** vai dar o nome compactDisc, para alterar o nome do bean para o Spring: 

	@Bean(name="lonelyHeartsClubBand")

para fazer um injection: 

	@Bean
	public CDPlayer cdPlayer() {
		return new CDPlayer(sgtPeppers());
	}


**********************************
By default, all beans in Spring are singletons, and there’s no reason 
you need to create a duplicate instance for the second CDPlayer bean.

So Spring intercepts the call to sgtPeppers() and makes sure
that what is returned is the Spring bean that was created when Spring 
itself called sgtPeppers() to create the CompactDisc bean.
**********************************

XML

Para usar o xml: 

<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
		xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
		xsi:schemaLocation="http://www.springframework.org/schema/beans
		http://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/context">
		
		<!-- configuration details go here -->
</beans>

Já a declaracao do bean é feita da seguinte forma:

<bean class="soundsystem.SgtPeppers" />, onde o class é o fullpath. 

tambem é possivel atribuir um id ao bean.

<bean id="compactDisc" class="soundsystem.SgtPeppers" /> 

Se nao for atribuido, o nome do bean será: soundsystem.SgtPeppers#0, onde é zero
é o contador de quantas vezes o bean se repete na declaracao. 


Injetando o bean no construtor:

Existem duas formas de injetar o bean no construtor : 

-> <construtor-arg> 	
	como ja temos o bean compactDisc declarado, podemos passa-lo no contrutor de outro bean: 
	
	<bean id="cdPlayer" class="soundsystem.CDPlayer">	
		<constructor-arg ref="compactDisc" />
	</bean>

** passar null para uma variavel: <constructor-arg><null/></constructor-arg>

-> c-name
	para injetar com c-name, precisamos primeiro declarar no nome no namespace: 
	
	xmlns:c="http://www.springframework.org/schema/c"

	<bean id="cdPlayer" class="soundsystem.CDPlayer"
			c:cd-ref="compactDisc" />
			

-> valores literais

	Podemos iniciar os parametro de uma classe: 
	
	class BlankDisc...
	public BlankDisc(String title, String artist) {
		this.title = title;
		this.artist = artist;
	}
	...			

	a declaracao do bean ficaria assim: 
	
	<bean id="compactDisc" class="soundsystem.BlankDisc">
		<constructor-arg value="Sgt. Pepper's Lonely Hearts Club Band" />
		<constructor-arg value="The Beatles" />
	</bean>
	
	ou entao com o c-name
	<bean id="compactDisc" class="soundsystem.BlankDisc"
			c:_title="Sgt. Pepper's Lonely Hearts Club Band"
			c:_artist="The Beatles" />
	
	evoluindo para 
	<bean id="compactDisc" class="soundsystem.BlankDisc"
			c:_0="Sgt. Pepper's Lonely Hearts Club Band"
			c:_1="The Beatles" />
	

Tem uma coisa que o <constructor-args> faz e os outros nao: passar collections: 

Para um construtor do tipo:

	public BlankDisc(String title, String artist, List<String> tracks) {

podemos passar: 

	<constructor-arg>
		<list>
		<value>Sgt. Pepper's Lonely Hearts Club Band</value>
		<value>With a Little Help from My Friends</value>
		<value>Lucy in the Sky with Diamonds</value>
		<value>Getting Better</value>
		<value>Fixing a Hole</value>
		<!-- ...other tracks omitted for brevity... -->
		</list>
	</constructor-arg>
	
Alem do value, podemos passar uma referencia para beans ja criados. Nesse caso,
usamos o ref. Porem o construtor deve receber um tipo de variavel compativel 
com o bean: 


	public Discography(String artist, List<CompactDisc> cds) { ... }
	** os beans devem estar definidos. 
	<constructor-arg>
		<list>
			<ref bean="sgtPeppers" />
			<ref bean="whiteAlbum" />
			<ref bean="hardDaysNight" />
			<ref bean="revolver" />
			...
		</list>
	</constructor-arg>

*** Tambem é possivel passar SET. Basta mudar o <list> pelo <set>.

METODOS 

Se nao quisermos usar o construtor para iniciar os parametros, mas o metodo set, 
pode deixar o default constructor e chamar o <property> para setar em um metodo: 

	<bean id="cdPlayer" class="soundsystem.CDPlayer">
		<property name="compactDisc" ref="compactDisc" />
	</bean>

* o Metodo que será chamado é o especificado no name (setCompactDisc) e o ref é a
referencia a outro bean. 

aqui ao inves do c namespace, podemos usar o p namespace: 

para configurar: 
	xmlns:p="http://www.springframework.org/schema/p"
	
e o corresponde do <property> 
	
	<bean id="cdPlayer" class="soundsystem.CDPlayer"
			p:compactDisc-ref="compactDisc" />

Para setar os argumentos dos metodos de forma literal: 
	* metodos : setTitle, setArtist e setTracks
	
	<property name="title" value="Sgt. Pepper's Lonely Hearts Club Band" />
	<property name="artist" value="The Beatles" />
	<property name="tracks">
		<list>
			<value>Sgt. Pepper's Lonely Hearts Club Band</value>
			<value>With a Little Help from My Friends</value>
			<value>Lucy in the Sky with Diamonds</value>
			<value>Getting Better</value>
			<value>Fixing a Hole</value>
			<!-- ...other tracks omitted for brevity... -->
		</list>
	</property>

Da mesma forma aqui nao podemos usar o c-name para passar list ou set. Mas podemos 
misturar os elements: 

<bean id="compactDisc" class="soundsystem.BlankDisc"
			p:title="Sgt. Pepper's Lonely Hearts Club Band"
			p:artist="The Beatles">
	
	<property name="tracks">
		<list>
			<value>Sgt. Pepper's Lonely Hearts Club Band</value>
			<value>With a Little Help from My Friends</value>
			<value>Lucy in the Sky with Diamonds</value>
			<value>Getting Better</value>
			<value>Fixing a Hole</value>
			<!-- ...other tracks omitted for brevity... -->
		</list>
	</property>
</bean>


No entanto podemos usar o util-namespace. Ele nos possibilita adicionar lista como 
parametros:

	<util:list id="trackList">
		<value>Sgt. Pepper's Lonely Hearts Club Band</value>
		<value>With a Little Help from My Friends</value>
		<value>Lucy in the Sky with Diamonds</value>
		<value>Getting Better</value>
		<value>Fixing a Hole</value>
		<!-- ...other tracks omitted for brevity... -->
	</util:list>

Para adiciona-lo, precisamos adicionar o xml namespace: 

	xmlns:util="http://www.springframework.org/schema/util"
	
agora podemos adicionar a lista no bean: (nome trackList). 

	<bean id="compactDisc" class="soundsystem.BlankDisc"
			p:title="Sgt. Pepper's Lonely Hearts Club Band"
			p:artist="The Beatles"
			p:tracks-ref="trackList" />
			
** alem do <util:list>, temos: 

	<util:constant> <util:list> <util:map> 
	<util:properties>
	<util:property-path> <util:set> 
	

SEPARANDO CONFIGS

	Se os configs estiverem muito grandes e precisarmos separa-los, podemos fazer. 
	No entanto, precisamos que um referencie o outro. Para isso, usar o @Import: 
	@Import(CDConfig.class)
	
	Podemos até declarar um config superior aos dois e usa-lo para unificar tudo, 
	importanto os dois dentro do superior: 
	
	@Import({CDPlayerConfig.class, CDConfig.class})
	
	
	
	
	
	
	
				
	
