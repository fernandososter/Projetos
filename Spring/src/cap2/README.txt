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

pag 46 iniciando beans with xml








