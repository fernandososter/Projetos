This chapter covers
- Accessing and exposing RMI services
- Using Hessian and Burlap services
- Working with Spring’s HTTP invoker
- Using Spring with web services



	Spring suporta: 
		- RMI 
		- Hessian/Burlap
		- Http Invoker
		- JAX-RPC e JAX-WS
		
	
	Em todos esses modelos, os serviços podem ser configurados no 
	spring como beans gerenciados. 
	
	As chamadas podem ser feitas como se fossem locais pois o spring
	gera um proxy que cuida de toda a parte de comunicacoes. 
	
	O cliente entao se comunica com o proxy, que realiza as chamadas remotas
	em nome do cliente no Remote Service. 
	
	Se ocorrer uma RemoteException durante a comunicacao (problemas de rede)
	o proxy vai encapsular essa exception como uma RemoteAccessException. 
	
	A implementacao da regra de negocio nao precisa saber que tipo de comunicacao
	esta sendo feita, nem se preocupar com detalhes de rede. Apenas algumas 
	classes que sao trafegadas na rede precisa implementar Serializable. 
	
	
RMI
	Para expor um serviço em RMI normalmente, temos que criar uma 
	interface, uma implementacao e ambas devem implementar a Remote Interface	
	
	No Spring, temos que usar uma classe chamada RmiServiceExporter, que
	vai fazer a exposicao do servico de uma interface que criamos e 
	ja associa-la ao RMI Server. 
	
	A RmiServiceExporter vai criar uma especie de package adapter no bean
	que esta sendo criado para a interface exposta como servico. 
	
	fig final pagina 398
	
	Para criar no java: 
		SpitterService é a interface com a declaracao dos metodos. 
		
		@Bean
		public RmiServiceExporter rmiExporter(SpitterService spitterService) {
			RmiServiceExporter rmiExporter = new RmiServiceExporter();
			rmiExporter.setService(spitterService);
			rmiExporter.setServiceName("SpitterService");
			rmiExporter.setServiceInterface(SpitterService.class);
			return rmiExporter;
		}
	
	O RMIServiceExporter por default vai tentar registrar o servico em 
	localhost:1099. Se nao encontrar, vai subir um RMI Server automaticamente. 
	Podemos ainda configurar outro local com o registryHost e registryPort
		
		rmiExporter.setRegistryHost("rmi.spitter.com");
		rmiExporter.setRegistryPort(1199);
	
	
	Para fazer o lado do cliente (nao precisar fazer naming e tratar as exception)
	usamos o RmiProxyFactoryBean: 
	
		@Bean
		public RmiProxyFactoryBean spitterService() {
			RmiProxyFactoryBean rmiProxy = new RmiProxyFactoryBean();
			rmiProxy.setServiceUrl("rmi://localhost/SpitterService");
			rmiProxy.setServiceInterface(SpitterService.class);
			return rmiProxy;
		}
				
		e na classe do cliente
		
			@Autowired
			SpitterService spitterService;
			...
			public List<Spittle> getSpittles(String userName) {
				Spitter spitter = spitterService.getSpitter(userName);
				return spitterService.getSpittlesForSpitter(spitter);
			}
		
		
	Para poder usar o RMI ambos os lados da app deve ter a mesma versao 
	do java, ja que os objetos serão serializados e trocados via rede. 
	
	No RMI, a porta de troca de mensagens é aleratória, o que pode causar
	problemas de redes (cair em portas bloquadas pelo firewall). 
	
	A troca de informação é feita somente em java. 
	
	Para resolver esses pontos, Hessian e Burlap
	
Hessian e Burlap
		
	Sao duas tecnologias desenvolvidadas pela Caucho Tech que visa a 
	troca de informacoes via RMI e XML. 
	
	O Hessian assume a parte do RMI, onde a troca é feita com informacoes
	binarias e tem modulos para varias linguagens diferentes (PHP, Python, C++...)
	
	A segunda é o Burlap onde a informacao é trafegada por xml. 
	
	
	
	HESSIAN
		Assim como no RMI, temos que usar uma implementacao do Spring para 
		exportar o servico: HessianServiceExporter.
		
		A diferença entre RMI e Hessian é  que o hessian é exposto como 
		um MVC-Controller. (fig pag 403)
		
			@Bean
			public HessianServiceExporter
			hessianExportedSpitterService(SpitterService service) {
				HessianServiceExporter exporter = new HessianServiceExporter();
				exporter.setService(service);
				exporter.setServiceInterface(SpitterService.class);
				return exporter;
			}	
		
		Ser um MVCController significa que precisamos: 
			- trata-lo como um web application e configurar o DispatcherServlet
				no web.xml. 
				
			- Configurar um URL handler para desviar o fluxo para o service bean. 
			
		Para configurar no web.xml
		
			<servlet-mapping>
				<servlet-name>spitter</servlet-name>
				<url-pattern>*.service</url-pattern>
			</servlet-mapping>	
		Ou no java: 
		
			ServletRegistration.Dynamic dispatcher = container.addServlet(
				"appServlet", new DispatcherServlet(dispatcherServletContext));
				dispatcher.setLoadOnStartup(1);
				dispatcher.addMapping("/");
				dispatcher.addMapping("*.service");
							
		Assim todas as requisicoes .service vai cair no service que queremos. 
		
		
	BURLAP	
			o funcionamento é o mesmo mas a classe é a BurlapServiceExporter: 
			
			@Bean
			public BurlapServiceExporter
			burlapExportedSpitterService(SpitterService service) {
				BurlapServiceExporter exporter = new BurlapServiceExporter();
				exporter.setService(service);
				exporter.setServiceInterface(SpitterService.class);
				return exporter;
			}
			
	
		do lado do client o burlap segue o mesmo template do RMI: 	
		Mas a injecao é feita da seguinte forma: 
		
			@Bean
			public HessianProxyFactoryBean spitterService() {
				HessianProxyFactoryBean proxy = new HessianProxyFactoryBean();
				proxy.setServiceUrl("http://localhost:8080/Spitter/spitter.service");
				proxy.setServiceInterface(SpitterService.class);
				return proxy;
			}
		
			@Bean
			public BurlapProxyFactoryBean spitterService() {
				BurlapProxyFactoryBean proxy = new BurlapProxyFactoryBean();
				proxy.setServiceUrl("http://localhost:8080/Spitter/spitter.service");
				proxy.setServiceInterface(SpitterService.class);
				return proxy;
			}
		
		Assim podemos mudar a implementação facilmenete entre os diferentes services. 
		Nao precisa alterar a logica do negocio, somente a classe de configuração do
		bean. 
		
		
		
Http Invoker

		Segue o mesmo funcionamento do Hessian e Burlap, mas com a interface
		HttpInvokerServiceExporter. 
		
		O HttpInvoker tambem usa o protocolo http para transporte, ou seja, 
		precisa ser tratado como uma app web e ter um dispatcher configurado: 
		
			@Bean
			public HttpInvokerServiceExporter
			httpExportedSpitterService(SpitterService service) {
				HttpInvokerServiceExporter exporter =
				new HttpInvokerServiceExporter();
				exporter.setService(service);
				exporter.setServiceInterface(SpitterService.class);
				return exporter;
			}
		
		E do lado do cliente, usado o HttpInvokerProxyFactoryBean: 
		
			@Bean
			public HttpInvokerProxyFactoryBean spitterService() {
				HttpInvokerProxyFactoryBean proxy = new HttpInvokerProxyFactoryBean();
				proxy.setServiceUrl("http://localhost:8080/Spitter/spitter.service");
				proxy.setServiceInterface(SpitterService.class);
				return proxy;
			}
		
		
		O httpinvoker tem limitacoes. Esse é um componente baseado 
		no spring, ou seja, para poder ser usado tem que ter spring em 
		ambos os lados. 
		Como usar serializacao java, ambos os lados devem ser compilados
		com a mesma versao. 
		

WebServices
	JAX-WS - Java Api XML Web Service. 
	
	Assim como RMI, Hessian, Burlap e HttpInvoker, web services tambem 
	tem um service exporter, mas que confia no JaxWs. 
		
		SimpleJaxWsServiceExporter
	
	Para export um servico a classe pojo deve ser anotada com @WebService 
	e o motodo com @WebMethod. 
	No entanto, pode ocorrer de o jaxws controlar o fluxo de vida do servico, 
	isso acontece principalmente com implementacoes de referencias antigas. 
	
	Para podermos usar o DI do spring juntamente com o jaxws, temos que 
	fazer nossa classe EndPoint extends SpringBeanAutowiringSupport. 
	Assim garantimos a integracao entre Spring e Jax-ws
	
	Dessa forma podemos usar o @Autowiring junto: 
	
		@WebService(serviceName="SpitterService")
		public class SpitterServiceEndpoint extends SpringBeanAutowiringSupport {
			@Autowired
			SpitterService spitterService;
			
			@WebMethod
			public void addSpittle(Spittle spittle) {
				spitterService.saveSpittle(spittle);
			}
			
			@WebMethod
			public void deleteSpittle(long spittleId) {
				spitterService.deleteSpittle(spittleId);
			}
			@WebMethod
			public List<Spittle> getRecentSpittles(int spittleCount) {
				return spitterService.getRecentSpittles(spittleCount);
			}
			@WebMethod
			public List<Spittle> getSpittlesForSpitter(Spitter spitter) {
				return spitterService.getSpittlesForSpitter(spitter);
			}
		}	

** Extender SpringBeanAutowiringSupport só é necessario quando o jaxws gerencia o 
ciclo de vida. 

	Para configurar o ServiceExporter, na configuracao: 
	
		@Bean
		public SimpleJaxWsServiceExporter jaxWsExporter() {
			return new SimpleJaxWsServiceExporter();
		}	
	Quando o ServiceExporter subir, vai sair varrendo toda a aplicacao atraz dos
	@WebSerice e @WebMethods e exporta-los como JAX-WS services no end localhost:8080
	
	Tambem é possivel mudar o endreco base do servico :	
		@Bean
		public SimpleJaxWsServiceExporter jaxWsExporter() {
			SimpleJaxWsServiceExporter exporter = new SimpleJaxWsServiceExporter();
			exporter.setBaseAddress("http://localhost:8888/services/");
		}
	
	A configuração do SimpleJaxWsServiceExport nao funciona com o JAX-WS 2.1
	
	Do lado do cliente, usamos o JaxWsPortProxyFactoryBean para fazer a comucacao 
	do proxy com o server via soap. 
	
	Fig pag 414		
	
	Cod: 
		@Bean
		public JaxWsPortProxyFactoryBean spitterService() {
			JaxWsPortProxyFactoryBean proxy = new JaxWsPortProxyFactoryBean();
			proxy.setWsdlDocument("http://localhost:8080/services/SpitterService?wsdl");
			proxy.setServiceName("spitterService");
			proxy.setPortName("spitterServiceHttpPort");
			proxy.setServiceInterface(SpitterService.class);
			proxy.setNamespaceUri("http://spitter.com");
			return proxy;
		}

	
	
	
		
				
		
			
		