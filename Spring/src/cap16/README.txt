This chapter covers
- Writing controllers that serve REST resources
- Representing resources in XML, JSON, and other formats
- Consuming REST resources



Sobre o REST

	Rest foca em coisas e nao em acoes. Ou seja, as operacoes devem ser controladas
	por substantivos e nao por verbos. A parte das acoes, quem controla, 
	sao os comandos do HTTP:
		put, post, get, patch, delete
		
		
	O REST é representacional do estado do objeto. Por isso, nao usar o rest
	para fazer chamada de funcao remota, mas para obter e setar estado do obj. 
	
	
Como Spring suporta REST

	- O spring suporta GET, PUT, DELETE, POST e apartir da 3.2 PATCH
	- O @PathVariable habilita aos controllers controlar as urls parametrizadas
	- Os recursos podem ser representados por Spring Views ou View Resolvers
	



		@Controller
		@RequestMapping("/spittles")
		public class SpittleController {
			private static final String MAX_LONG_AS_STRING="9223372036854775807";
			private SpittleRepository spittleRepository;
	
			@Autowired
			public SpittleController(SpittleRepository spittleRepository) {
				this.spittleRepository = spittleRepository;
			}

			@RequestMapping(method=RequestMethod.GET)
			public List<Spittle> spittles(@RequestParam(value="max",defaultValue=MAX_LONG_AS_STRING) long max,	
											@RequestParam(value="count", defaultValue="20") int count) {
											
				return spittleRepository.findSpittles(max, count);
			}
		}
	
	
	Quando o spittles() é chamado, o retorno será emcapsulado em um model de retornado para a tela (provavelmente html). Mas para restful nao 
	pode ser um html. 
	Podemos mudar para qualquer formado. Se o consumidor quiser um json, xml, pdf, excel, csv,... o metodo é o mesmo, nao muda a logica. 
	
	Nao precisamos implementar todos, geralmente json e xml são suficientes para a maioria dos casos. 
	
	Json é javascript e extremamente apropriado para lidar com telas. Basta fazer o matcher/desmatcher das informacoes. 
	
	O spring tem dois passos para transformar as informacoes de objeto java para Json (por exemplo, mas serve para outros protocolos)
	
		- Content Negotiation : selecionar uma view que possa fazer a transformacao de java para o client
		- Message Conversion : faz a conversao do objeto fornecido pelo controller para o cliente. 
		
	
		
		Content Negotiation 
			
			Alem do nome para estipular que view será apresentada ao cliente, o ViewResolver tem que levar em consideracao
			que tipo de conteudo o cliente esta esperando. Para isso, o ContentNegotiatingViewResolver é especialista. 
			Ele considera o contenttype da informação esperada pelo client. 
			
			para configurar: 

				@Bean
				public ViewResolver cnViewResolver() {
					return new ContentNegotiatingViewResolver();
				}
	
			Quando instanciado ele faz: 
				- Determinar o media type requisitado
				- encontrar o melhor view para o tipo determinado 
				
			Para identificar o tipo esperado o ContentNegotiatingViewResolver nao pode confiar cegamente na 
			propriedade accept do header. Ela pode estar errada. 
			Por isso, ele olha a url de solicitacao. Se for uma solicitacao de arquivo conhecio 
				jsp,html,json, xls,... ele irá se adaptar ao formato do arquivo e responder no formado. 
				
			Se nao conseguir identificar pelo arquivo da url, entao irá usar o accept (mimetype). 
			Se mesmo assim nao conseguir, vai enviar como / e o browser decide o formato. 
			
			Apos identificado o formato, o ContentNegotiatingViewResolver envia o fluxo para um ViewResolver 
			cadastrado para resolver o tipo determinado. 
			
			O beneficio de usar o ContentNegotiating é que nao é preciso mudar nosso controler, 
			o spring vai decidir qual formado deve ser retornado. (html, xml, json,..)
			
	MESSAGE CONVERTERS
	
		aqui terminamos qual implementacao ira fazer o parser do java objetc para o que será retornado 
		(Object-to-representation)
		
		Implementacoes: 
			- AtomFeedHttpMessageConverter
			- BufferedImageHttpMessageConverter
			- ByteArrayHttpMessageConverter
			- FormHttpMessageConverter
			- Jaxb2RootElementMessageConverter
			- MappingJacksonHttpMessageConverter
			- MappingJackson2HttpMessageConverter
			- MarshallingHttpMessageConverter
			- ResourceHttpMessageConverter
			- RssChannelHttpMessageConverter
			- SoruceHttpMessageConverter
			- StringHttpMessageConverter
			- XmlAwareFormHttpMessageConverter
	
	Pag 427 tem a descricao de todos. 
	
	temos que usar o @RequestBody e o @ResponseBodhy para habilitar os converters. 
	
		Por exemplo, se o cliente estiver esperando via accept um json, o Jackson será usado para 
		fazer a conversao. 
		No entanto ele deve estar no classpath da aplicacao. 
		
		Para appliction/text, usa o Jaxb2RootElementHttpMessageConverter. Para poder usar o JAXB deve estar no classpath. 
		
		
		Para poder usar os converters, precisamos desabilitar o funcionamento do View/Model de 
		fazer a conversao automaticamente. Para desabilitar, usar o @ResponseBody
		
			@RequestMapping(method=RequestMethod.GET,produces="application/json")
			public @ResponseBody List<Spittle> spittles(
				@RequestParam(value="max",defaultValue=MAX_LONG_AS_STRING) long max,
				@RequestParam(value="count", defaultValue="20") int count) 
			{
					
					return spittleRepository.findSpittles(max, count);
			}	
					
		Para saber que tipo de retorno o converter deve dar, ele irá procurar o accept. 
		Quando identificar que é json, vai procurar as labraries do jackson para fazer a transformacao e, 
		apos transformar, irá postar o conteudo na resposta. 
		
		Ex de postagem: 
			[
				{
					"id": 42,
					"latitude": 28.419489,
					"longitude": -81.581184,
					"message": "Hello World!",
					"time": 1400389200000
				},
				{
					"id": 43,
					"latitude": 28.419136,
					"longitude": -81.577225,
					"message": "Blast off!",
					"time": 1400475600000
				}
			]	
			
			
	Converter como Input
		
			O converter tambem pode ser usado para input. Imagina receber um xml e ter converter para um objeto para, 
			para depois converter em json. Usar o @RequestBody no input do metodo. 
					
			@RequestMapping(method=RequestMethod.POST,consumes="application/json")
			public @ResponseBody Spittle saveSpittle(@RequestBody Spittle spittle) {
				return spittleRepository.save(spittle);
			}		
	
			o converter tenta inferir pelo content-type do request qual é o tipo de dados que esta sendo 
			submetido para fazer a transformacao. quem identifica o tipo é o dispatcher e ele mesmo 
			ira encaminhar a mensagem para o converter correto. O retorno é entao passado como input do metodo 
	
	
	Apartir do Spring 4.0, foi introduzido uma configuracao que faz o processo automaticamente e nao precisa mais 
	do @ResponseBody e @RequestBody. é o @RestController, onde anotamos o configuration usando essa tag ao inves de @Controller
	
	
			@RestController
			@RequestMapping("/spittles")
			public class SpittleController {
				private static final String MAX_LONG_AS_STRING="9223372036854775807";
				private SpittleRepository spittleRepository;
			
				@Autowired
				public SpittleController(SpittleRepository spittleRepository) {
					this.spittleRepository = spittleRepository;
				}
			
				@RequestMapping(method=RequestMethod.GET)
				public List<Spittle> spittles(
					@RequestParam(value="max",defaultValue=MAX_LONG_AS_STRING) long max,
					@RequestParam(value="count", defaultValue="20") int count) {
			
					return spittleRepository.findSpittles(max, count);
				}
		
				@RequestMapping(
					method=RequestMethod.POST
					consumes="application/json")
				public Spittle saveSpittle(@RequestBody Spittle spittle) {
					return spittleRepository.save(spittle);
				}
			}
				
Além do conteudo normal.

		Se chamar do cliente esse metodo passado o id, e nao achar, vai retornal null para o client mas vai 
		retornar um HTTP 200 (OK). 
		
		@RequestMapping(value="/{id}", method=RequestMethod.GET)
		public @ResponseBody Spittle spittleById(@PathVariable long id) {
			return spittleRepository.findOne(id);
		}
		
		Isso não esta certo. 
		O spring deve retornar um HTTP 404 (Not Found). 
		
		Exitem 3 formas de fazer isso: 
		
			- atravez do @ResponseStatus
			- Carregar informacoes no ResponseEntity para retornar
			- Exception hadlers. 
			
		* ResponseEntity
			
			é um objeto que pode retornar metadados (como o status code de retorno). 
			
			Nova implementacao do metodo: 
			
				@RequestMapping(value="/{id}", method=RequestMethod.GET)
				public ResponseEntity<Spittle> spittleById(@PathVariable long id) {
					Spittle spittle = spittleRepository.findOne(id);
					HttpStatus status = spittle != null ?
					HttpStatus.OK : HttpStatus.NOT_FOUND;
					return new ResponseEntity<Spittle>(spittle, status);
				}
			
			** nesse caso, o metodo nao esta anotado com @ResponseBody. Se o metodo retornar 
			um reponseentity, nao tem necessidade, ja é implicito. 
			
			No entanto, o retorno continua sendo empty (spittle), pois o metodo find() nao encontrou nada. 
			
			Para resornar a mensagem de erro alem do codigo HTTP 404, temos que criar um objeto 
			de erro para encapsular a msg: 
				
				public class Error {
					private int code;
					private String message;
								...
				} 
	
			e modificar o metodo find: 
			
				@RequestMapping(value="/{id}", method=RequestMethod.GET)
				public ResponseEntity<?> spittleById(@PathVariable long id) {
					Spittle spittle = spittleRepository.findOne(id);
					if (spittle == null) {
						Error error = new Error(4, "Spittle [" + id + "] not found");
						return new ResponseEntity<Error>(error, HttpStatus.NOT_FOUND);
					}
					return new ResponseEntity<Spittle>(spittle, HttpStatus.OK);
				}
	
	
		Esse metodo ajuda a resolver o problema, mas causa outro: 
			-colocamos um tipo de retorno que é especifico d+ (ResponseEntity) 
		
		
			
		* Handling Erros
			@ExceptionHandler
			
			Definindo um metodo com essa annotation, qualquer exception do tipo definido que ocorrer no 
			@Controller, será gerenciado pelo metodo. 
			
			ex: 
			
				@ExceptionHandler(SpittleNotFoundException.class)
				public ResponseEntity<Error> spittleNotFound(SpittleNotFoundException e) {
					long spittleId = e.getSpittleId();
					Error error = new Error(4, "Spittle [" + spittleId + "] not found");
					return new ResponseEntity<Error>(error, HttpStatus.NOT_FOUND);
				}
			Se ocorrer uma SpittleNotFoundException no @Controller, então esse metodo será ativado. 
			
			Entao, podemos evoluir o metodo iniciar para :
			
				@RequestMapping(value="/{id}", method=RequestMethod.GET)
				public @ResponseBody Spittle spittleById(@PathVariable long id) {
					Spittle spittle = spittleRepository.findOne(id);
					if (spittle == null) { throw new SpittleNotFoundException(id); }
					return spittle;
				}			
			
			e se o controller estiver anotado com @RestController, 
			
				@RequestMapping(value="/{id}", method=RequestMethod.GET)
				public Spittle spittleById(@PathVariable long id) {
					Spittle spittle = spittleRepository.findOne(id);
					if (spittle == null) { throw new SpittleNotFoundException(id); }
					return spittle;
				}
						
	
			e o metodo de erro, ja que ele sempre vai retornar 404, podemos remover o 
			ResponseEntity e adicionar direto o erro com o codigo 404 no annotation: 
							
				@ExceptionHandler(SpittleNotFoundException.class)
				@ResponseStatus(HttpStatus.NOT_FOUND)
				public Error spittleNotFound(SpittleNotFoundException e) {
					long spittleId = e.getSpittleId();
					return new Error(4, "Spittle [" + spittleId + "] not found");
				}	
	
		
			Na criacao de um objeto, podemos colocar o status de CREATED (201) com o @ResponseStatus: 
				@RequestMapping(
					method=RequestMethod.POST
					consumes="application/json")
				@ResponseStatus(HttpStatus.CREATED)
				public Spittle saveSpittle(@RequestBody Spittle spittle) {
					return spittleRepository.save(spittle);
				}
			Mas uma boa pratica diz que, sempre que criar algo, retornar o endereco de onde foi criado o elmento
			na property Location do header de retorno. 
			Para poder fazer isso, vamos precisar do ResponseEntity. 
			
				@RequestMapping(
					method=RequestMethod.POST
					consumes="application/json")
				public ResponseEntity<Spittle> saveSpittle(@RequestBody Spittle spittle) {
					Spittle spittle = spittleRepository.save(spittle);
				
					HttpHeaders headers = new HttpHeaders();
					URI locationUri = URI.create("http://localhost:8080/spittr/spittles/" + spittle.getId());
					headers.setLocation(locationUri);
				
					ResponseEntity<Spittle> responseEntity = new ResponseEntity<Spittle>(
								spittle, headers, HttpStatus.CREATED)
					return responseEntity;
				}
							
		No entanto tivemos que colocar url (localhost:8080/...) hardcode e isso tem que ser evitado. 
		Utilizar o objeto UriComponentsBuilder: 
		
				@RequestMapping(
					method=RequestMethod.POST
					consumes="application/json")
				public ResponseEntity<Spittle> saveSpittle(@RequestBody Spittle spittle,
					UriComponentsBuilder ucb) {
					
					Spittle spittle = spittleRepository.save(spittle);
					HttpHeaders headers = new HttpHeaders();
					
					URI locationUri =
						ucb.path("/spittles/")
							.path(String.valueOf(spittle.getId()))
							.build()
							.toUri();
					headers.setLocation(locationUri);
					ResponseEntity<Spittle> responseEntity =new ResponseEntity<Spittle>(
						spittle, headers, HttpStatus.CREATED)
					return responseEntity;
				}
		
		
		
Consumidores REST

		Para reduzir a quantidade de codigo desnecessario na regra de negocio, assim como 
		no jdbc, que tem o template (JdbcTemplate), no rest tambem existe um template que 
		empacota todo codigo repetido nas operacoes. RestTemplate
		
		O RestTemplate tem 36 metodos ja implementados, com 11 operacoes diferentes; 
		Os 11 unique operations sao:
			- delete()
			- exchange()
			- execute()
			- getForEntity()
			- getForObject()
			- headForHeaders()
			- optionsForAllow()
			- postForEntity()
			- postForLocation()
			- postForObject() 
			- put()
			
		
		pag 440 a descricao de todos
		
		Com excecao do TRACE, todos os comandos HTTP sao cobertos pelos metodos acima. 
		
		Exemplo com GET,PUT,DELETE e POST
		
		GET 
			para o GET existem dois metodos (cada um com 3 sobrescritas)
			
			getForObject()
			getForEntity()
			
			Ambos segue o padrao: 
			 	(URI url, Class<T> responseType)
				(String url, Class<T> responseType, Object...uriVariables)
				(String url, Class<T> responseType, Map<String,?> uriVariables) 
				
			A diferença  no return type é que o getForObject() retorna um objeto T e o getForEntity() retorna
			o ResponseEntity<T>. 
				-> T getForObject()
				-> ResponseEntity<T> getForEnntity()
		
			
			O getForObject() retorna um objeto especifico que passamos como pametro: 
			
				public Profile fetchFacebookProfile(String id) {
					RestTemplate rest = new RestTemplate();
					return rest.getForObject("http://graph.facebook.com/{spitter}",Profile.class, id);
				}			
			O id é colocado no {spitter}. 
			
			Tambem podemos passar um Map quando precisar de mais de um id: 
			
				public Spittle[] fetchFacebookProfile(String id) {
					Map<String, String> urlVariables = new HashMap<String, String();
					urlVariables.put("id", id);
					RestTemplate rest = new RestTemplate();
					return rest.getForObject("http://graph.facebook.com/{spitter}",Profile.class, urlVariables);
				}
			
			Exite uma unchecked exceptino (RestClientException) que o metodo pode reportar. 
			
			O getForEntity() retorna o ResponseEntity com a classe que queremos dentro dele. 
			
				public Spittle fetchSpittle(long id) {
					RestTemplate rest = new RestTemplate();
					ResponseEntity<Spittle> response = rest.getForEntity("http://localhost:8080/spittr-api/spittles/{id}",
							Spittle.class, id);
							
					if(response.getStatusCode() == HttpStatus.NOT_MODIFIED) {
						throw new NotModifiedException();
					}
					return response.getBody();
				}			
				
		PUTing 
			
			Para put, o RestTemplate oferece 3 metodos: 
				void (URI url, Object request) 
				void (String url, Object request, Object...uriVariables)
				void (String url, Object request, Map<String,?> uriVariables) 
				
			ex: 
				public void updateSpittle(Spittle spittle) throws SpitterException {
					RestTemplate rest = new RestTemplate();
					String url = "http://localhost:8080/spittr-api/spittles/"+ spittle.getId();
					rest.put(URI.create(url), spittle);
				}
							
		 	Ex com string url: (para criar dinamico)
		 		public void updateSpittle(Spittle spittle) throws SpitterException {
					RestTemplate rest = new RestTemplate();
					rest.put("http://localhost:8080/spittr-api/spittles/{id}",spittle, spittle.getId());
				}
						
						
		DELETE
			3 metodos tambem
			
				void delete(String url, Object...uriVariables)
				void delete(String url, Map<String,?> uriVariables)
				void delete(URI url)
				
				public void deleteSpittle(long id) {
					RestTemplate rest = new RestTemplate();
					rest.delete("http://localhost:8080/spittr-api/spittles/{id}", id));
				}
		
		POST (para salvar novos objetos)
		
			o post tem 3 metodos, com 3 sobrescrita cada: 
				postForEntity()
				postForObject()
				postForLocation()
				 
				 postForObject(): 
				 	
				 	T (URI, Object request, Class<T> responseType)
				 	T (String url, Object request, Class<T> responseType, Object...uriVariables)
				 	T (String url, Object request, class<T> responseType, Map<String,?> uriVariables)
				 	
				 Ex: 
				 	public Spitter postSpitterForObject(Spitter spitter) {
						RestTemplate rest = new RestTemplate();
						return rest.postForObject("http://localhost:8080/spittr-api/spitters",spitter, Spitter.class);
					}					
											
				postForEntity(): 
					o mesmo que o get, o retorno do postForEntity() é um ResponseEntity
					com as propriedades do HTTP.
					
					
				portForLocation(): 
					ao inves de responder com o objeto criado ou com o ResponseEntity, responde com o Location
					(HTTP Location) de onde foi criado o object: 
					
					public String postSpitter(Spitter spitter) {
						RestTemplate rest = new RestTemplate();
						return rest.postForLocation("http://localhost:8080/spittr-api/spitters",spitter).toString();
					}
		
	Ainda tem o metodo exchange() que possibilita a mudança das informacoes dos cabeçalhos. 
	
	
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
	
	
	
	
	
	
	
	
	
	
	
	
	