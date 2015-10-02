WEB

Spring MVC

As informacoes da requisicao do cliente sao acumulados no objeto request. 
O objeto é enviado para components, que resolvem e/ou adicionam novas informacoes. 

O primeiro ponto que o request passa é o front controller (DispatcherServlet), 
que é o servlet que serve de funil. 

O dispatcherServlet envia o request para o component do spring que controla para 
onde vao os requests. Esse componente é o controller. No entanto, uma app pode ter
varios controlers, entao o dispatcher precisa primeiro para qual controller enviar 
o request. Para descobrir, o dispatcher vai consultar um HandlerMapping. 

Quando encontra o controller, o payload (informacoes) do request sao descarregadas 
para processamento. (na verdade um bom controller faz apenas o delegate para outro
objeto - serve apenas como delegate).

O resultado do processamento é entao remetido de volta para o cliente (geralmente em 
jsp). Essa informacao de retorno é chamada de model. 

O controller empacota tudo e envia devolta para o Dispatcher, o model e o nome da view para poder
renderizar. O nome da view é um metadado (nao é o nome concreto de um jsp por exemplo) e isso deixa
bem generico. 
Para poder saber qual é o destino final, o Dispatcher pega esse nome e consulta view resolver (que 
contem o nome concreto do arquivo que ira resolver o conteudo). 

O conteudo do model entao é descarregado no view e a pagina (se for jsp) apresentada ao client. 

** fig da pag 132 mostra o fluxo. 


Configurando o Dispatcher

 Até a versao 3.1 o dispatcher era configurado somente no web.xml. A partir da 3.1, podemos usar o java
 para configurar o dispatcher. 
 
** Na especificacao do ejb3.0, quando o container sobe procura uma classe que faz as configuracoes do container 
(ServletContainerInitializer - ou que extende ela). O Spring fornece a classe  AbstractAnnotationConfigDispatcherServletInitializer
que extende essa classe e onde podemos jogar nossas configuracoes. 

** essa configuracao somente ira funcionar com container que implementem serlvet3.0. Para versoes anteriores, precisamos usar o web.xml
 
public class SpittrWebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {
	
	// foence ao container as informacoes dos servlets mapping (nesse caso, a configuracao será aplicado a todas as urls). 
	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}
	
	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { RootConfig.class };
	}
	
	// aqui estamos pedindo para o container carregar os beans do spring (WebConfig.class)
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { WebConfig.class };
	}
 
}


Agora para habilitar o mvn, precisamos definir o basico do  @Configuration (WebConfig)
	
	@Configuration
	@EnableWebMvc
	public class WebConfig {
	}

No entanto no basico falta: 
	- um resolver configurado (apenas de usar o default, BeanNameViewResolver, que tenta resolver pelo nome dos beans)
	- Component-scanning nao esta habilitado, precisamos declarar os controllers manualmente
	- um default dispatcher que vai controlar tudo (conteudo dinamico e estatico)
	
Ex mais completo
	
	@Configuration
	@EnableWebMvc
	@ComponentScan("spitter.web") 								// adicionando o componentscan (no pacote splliter.web)
	public class WebConfig extends WebMvcConfigurerAdapter { 	// extends, para pode implements o ultimo metodo. 
		
		@Bean													// adicionando um ViewResolver. 
		public ViewResolver viewResolver() {	
			InternalResourceViewResolver resolver = new InternalResourceViewResolver();
			resolver.setPrefix("/WEB-INF/views/");
			resolver.setSuffix(".jsp");
			resolver.setExposeContextBeansAsAttributes(true);
			return resolver;
		}
		
		@Override
		public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
			configurer.enable(); 								// chamando enable() falamos ao mvc para nao tentar controlar cnoteudo estatico. 
		} 
	}	

** quanto usamos o ComponentScan no contexto web, o mvc vai procurar por beans anotados com @Controller. 	

	
e a implementacao do RootConfig

		@Configuration
		@ComponentScan(basePackages={"spitter"},
			excludeFilters={
				@Filter(type=FilterType.ANNOTATION, value=EnableWebMvc.class)
			})
		public class RootConfig {
		}



Simple Controler. 

Para tratar o resquest da pagina home, um simples controler: 

	@Controller
	public class HomeController {
		@RequestMapping(value="/", method=GET)
		public String home() {
			return "home";
		}
	}

** O controller serve para que o component scanner pegue e classe e carregue no application context. 
é o @Component (poderiamos até anotar com ele e teria o mesmo efeito), mas seria menos detalhado 
sobre o que o controller realmente é. 

** o metodo home() ira tratar a chamada do '/' e retornar "home", que é o metadado para o ViewResolver tratar. 
Como o viewresolver instanciado la em cima foi o InternalResourceViewResolver, ele irá procurar em 
	/WEB-INF/views/home.jsp
	

Como o HomeController é um POJO, podemos usar o @Test para testar. No entanto isso nao vai simular um ambiente web. 
Para poder simular um ambiente web, o MockMvc faz isso. 
codigo na pag 142

Se puxarmos o @RequestMapping para cima da classe, estamos dizendo que o classe ira controlar todas as solicitadoes de '/':
	@Controller
	@RequestMapping("/")
	public class HomeController {
		@RequestMapping(method=GET)
		public String home() {
			return "home";
		}
	}
A partir de agora, qualquer metodo anotado com @RequestMapping  será complementar ao '/'. Se nao tiver nenhum metodo anotado, 
entao o unico (no caso o home), ira tratar '/'

O @RequestMapping tambem aceita mais de uma url base. Nesse caso para tratar mais de uma no mesmo controller: 
@RequestMapping({"/", "/homepage"})


Setando o Model. 

Para poder setar o model o livro faz um codigo fazendo mock para testes de um repositorio de dados. pag 145. 
Mas o importante aqui é como tratar o model. 
	
	@Controller
	@RequestMapping("/spittles")
	public class SpittleController {
		private SpittleRepository spittleRepository;
		
		@Autowired
		public SpittleController(SpittleRepository spittleRepository) {
			this.spittleRepository = spittleRepository;
		}
		
		@RequestMapping(method=RequestMethod.GET)
		public String spittles(Model model) {
			model.addAttribute(spittleRepository.findSpittles(Long.MAX_VALUE, 20));
			return "spittles";
		}
	}

** o model é um objeto key-value que será passado para a tela com as informacoes que queremos mostrar. 
Podemos passar ou nao a key (se nao passar, será setada automaticamente como o nome do tipo do objeto). 

** o retorno é a chave do ViewResolver

** podemos substituir o Model por um map, o funcionamento é exatamente o mesmo. 

OU entao reescrever o metodo para retornar uma list: 

	@RequestMapping(method=RequestMethod.GET)
	public List<Spittle> spittles() {
		return spittleRepository.findSpittles(Long.MAX_VALUE, 20));
	}

Nesse caso o retorno é listSplittle e o tipo será inferido pelo ViewResolver: 
Because this method handles GET requests for /spittles, the view name is spittles (chopping off
the leading slash).


Para enviar dados do cliente para o servidos, existem tres alteranativas: 

	- Query Parameters
	- Form parameters
	- Path variables
	
Query Parameters

	Obtem os valores do get? Precisa sobrecarregar o metodo? Assim quando os parametros forem enviados, 
	o metodo ja esta preparado para receber. 
	
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Spittle> spittles(
		@RequestParam("max") long max,
		@RequestParam("count") int count) {
			return spittleRepository.findSpittles(max, count);
	}

	Nao, o metodo é o mesmo, nao precisa de sobrecarga. 
 	Para tratar quando os valores nao estiverem presentes, podemos colocar valores default: 
 	
 	@RequestMapping(method=RequestMethod.GET)
	public List<Spittle> spittles(
		@RequestParam(value="max",
		defaultValue=MAX_LONG_AS_STRING) long max,
		@RequestParam(value="count", defaultValue="20") int count) {
		
			return spittleRepository.findSpittles(max, count);
	}
	
	
Path VAriables
	
	Enquanto no primeiro Query passamos os valores da seguinte forma: /spittles/show?spittle_id=12345
	No path passamos: /spittles/12345
** nao ocorre a identificacao do elemento

	No path variables é implementado o conceito de placeholders (variaveis com {}): 
	
	@RequestMapping(value="/{spittleId}", method=RequestMethod.GET)
	public String spittle(
		@PathVariable("spittleId") long spittleId, Model model) {
			model.addAttribute(spittleRepository.findOne(spittleId));
			return "spittle";
	}

** o parametro agora é anotado com @PathVariable. Ele quer dizer que, qualquer informacao passada apos o /splittes
será usado como variavel "spittletId"
** Podemos omitir o value do @PathVariable se o nome da variavel do metodo bater com o requestmapping. 


No entanto Query e Path variables sao limitadas na quantidade de parametros que consegue passar. Para passar
mais informacoes, processing forms

Processing Forms

O form tem duas funcionalidades importantes: 
	- display form
	- processing data that the user submits. 
	

Para essa funcionalidade, o livro cria um novo Controller
	
	@Controller
	@RequestMapping("/spitter")
	public class SpitterController {
		@RequestMapping(value="/register", method=GET)
		public String showRegistrationForm() {
			return "registerForm";
		}
	}
** esse metodo @RequestMapping nao recebe input, apenas retorna o fluxo para a pagina registerForm (se for o 
InternalResourceViewResolver, direcinara para /WEB-INF/views/registerForm.jsp

Essa pagina (registerForm) vai precisar ter um form declarado: 

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<html>
<head>
<title>Spittr</title>
<link rel="stylesheet" type="text/css"
href="<c:url value="/resources/style.css" />" >
</head>
<body>
<h1>Register</h1>
	<form method="POST">
	First Name: <input type="text" name="firstName" /><br/>
	Last Name: <input type="text" name="lastName" /><br/>
	Username: <input type="text" name="username" /><br/>
	Password: <input type="password" name="password" /><br/>
	<input type="submit" value="Register" />
	</form>
</body>
</html>
	
** O form declarado acima nao tem um action declarado. Isso significa que quando for submetido, será para o 
mesmo endereço que destinou para ele (/spitters/register)

Para criar um metodo que vai processar o submit do form: 

	@Controller
	@RequestMapping("/spitter")
	public class SpitterController {
		private SpitterRepository spitterRepository;

		@Autowired
		public SpitterController(SpitterRepository spitterRepository) {
			this.spitterRepository = spitterRepository;
		}
		
		@RequestMapping(value="/register", method=GET)
		public String showRegistrationForm() {
			return "registerForm";
		}
		
		@RequestMapping(value="/register", method=POST)
		public String processRegistration(Spitter spitter) {
			spitterRepository.save(spitter);
			return "redirect:/spitter/" + spitter.getUsername();
		}
		
		@RequestMapping(value="/{username}", method=GET)
		public String showSpitterProfile(@PathVariable String username, Model model) {
			Spitter spitter = spitterRepository.findByUsername(username);
			model.addAttribute(spitter);
			return "profile";
		}
		
	}

** O proprio spring ja cria a instancia do bean Splitter e popula as informacoes. 
A primeira linha é um save no repositorio. A segunda e o return.
O return tem um "redirect:". Esse cara diz so InternalResourceViewResolver para apenas direcionar a pagina. 

** o InternalResourceViewResolver tem tambem o forward:

** o metodo showSplitterProfile() é o chamado no redirect: que vai tratar a informacao (por path variable). 

e para mostrar a informacao no html

<h1>Your Profile</h1>
<c:out value="${spitter.username}" /><br/>
<c:out value="${spitter.firstName}" />
<c:out value="${spitter.lastName}" />


VALIDATING FORMS 

Ao inves de colocar um monte de if no codigo para verificar se os parametros passados no form estao corretos, 
podemos usar a Java Validation API (JSR303). Isso é possivel apartir do String 3.0. 

Apenas precisamos garantir que uma implementacao da JSR303 esteja no classpath (Hibernate Validator por exemplo). 

Existem varias annotation no JVA que podemos usar para validar os properties
 (todos no pacote javax.validation.constraints)

@AssertFalse @AssertTrue @DecimalMax @DecimalMin @Digits @Future @Max @Min @NotNull 
@Null @Past @Pattern @Size ( e mais)

Exemplo de como ficaria o bean:

	public class Spitter {
		private Long id;
		@NotNull
		@Size(min=5, max=16)
		private String username;
		
		@NotNull
		@Size(min=5, max=25)
		private String password;
		
		@NotNull
		@Size(min=2, max=30)
		private String firstName;
		
		@NotNull
		@Size(min=2, max=30)
		private String lastName;
		...
	}

e para fazer a validacao, precisamos mudar o metodo que recebe o form:

	@RequestMapping(value="/register", method=POST)
	public String processRegistration( @Valid Spitter spitter, Errors errors) {
		if (errors.hasErrors()) {
			return "registerForm";
		}
		
		spitterRepository.save(spitter);
		return "redirect:/spitter/" + spitter.getUsername();
	}

** o @Valid faz com que o spring valide as informacoes do Spitter. 
** as validacoes nao impedem que o bean seja submetido ao servidor. 
** se ocorrer erros, eles serao jogados em Errors (importante que a primeira coisa a ser feita seja uma 
consulta se existem erros)





