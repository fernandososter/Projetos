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


Pag 148 item 5.3









