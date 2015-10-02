

Até agora usamos o InternalResourceViewResolver para resolver o nome dos views 
(direciona para /WEB-INF/views/<nome>.jsp 

Para fazer essa conversão, existem duas interface: 

	ViewResolver { 
		View resolveViewName( String viewName, Locale loc); 
	} 
	
	View {
		String getContentType(); 
		void render(Map model, HttpServletRequest, HttpServletResponse); 
	}	
	
** a ViewResolver retorna uma instancia de View. A View faz o processo
de renderizar o model no request e no respose. 

Views Ja Implementados: 
	-BeanNameViewResolver : resolve views como beans, bean e view tem o mesmo nome
	-ContentNegotiatingViewResolver :  usa o contentType para chamar outro resolver e resolver
	-FreeMarkerViewResolver : resolve como FreeMarker templates. 
	-InternalResourceViewResolver : resolve os vies em JSP
	-JasperReportsViewResolver : resolve como jasper definition
	-ResourceBundleViewResolver : resolve como resource bundle (properties file)
	-TilesViewResolver : resolve com tiles. A classe que resolve para 2.0 e 3.0 sao diferentes
	-UrlBasedViewResolver : resolve pelo viewname (fisico)
	-VelocityLayoutViewResolver : resolve como velocity compose
	-VelocityViewResolver : resolve como velocity template
	-XmlViewResolver : resolve com configuracoes feitas em xml
	-XsltViewResolver : resolve uma transformacao de xslt
	

JSP

Spring suporta jps de duas formas: 
	-InternalResourceViewResolver 
	- JSP Tag Libraries: uma para form-to-model e outra de utilitarios features

	O InternalResourceViewResolver usar <prefix><nome><suffix>. Por default, 
	a configuração é prefix: '/WEB-INF/views/' e o suffix: '.jsp'. Isso pode ser mudado: 
	
		@Bean
		public ViewResolver viewResolver() {
			InternalResourceViewResolver resolver =new InternalResourceViewResolver();
			resolver.setPrefix("/WEB-INF/views/");
			resolver.setSuffix(".jsp");
			return resolver;
		}
	ou 
		
		<bean id="viewResolver" 
			class="org.springframework.web.servlet.view.InternalResourceViewResolver"
			p:prefix="/WEB-INF/views/"
			p:suffix=".jsp" />
		
	O processo normal do InternalResourceViewResolver vai criar View do tipo InternalResourceView. 
	Se quisermos usar Jstl, temos que criar outro tipo de View: JstlView
	
	
			@Bean
			public ViewResolver viewResolver() {
				InternalResourceViewResolver resolver = new InternalResourceViewResolver();
				resolver.setPrefix("/WEB-INF/views/");
				resolver.setSuffix(".jsp");
				resolver.setViewClass(org.springframework.web.servlet.view.JstlView.class);
				return resolver;
			}
		
		<bean id="viewResolver"
		   class="org.springframework.web.servlet.view.InternalResourceViewResolver"
			p:prefix="/WEB-INF/views/"
			p:suffix=".jsp"
			p:viewClass="org.springframework.web.servlet.view.JstlView" />	
		
** para mudar para jstl, configurar o ViewClass. 

Utilizando taglibraries


-> Forms-to-model taglib


<%@ taglib uri="http://www.springframework.org/tags/form" prefix="sf" %>

<sf:checkbox>
<sf:checkboxex>
<sf:errors>
<sf:form:
<sf:hidden>
<sf:input>
<sf:label>
<sf:option>
<sf:options>
<sf:password>
<sf:radiobutton>		
<sf:radiobuttons>
<sf:select>	
<sf:textarea>
	
	<sf:form method="POST" commandName="spitter">
		First Name: <sf:input path="firstName" /><br/>
		Last Name: <sf:input path="lastName" /><br/>
		Email: <sf:input path="email" /><br/>
		Username: <sf:input path="username" /><br/>
		Password: <sf:password path="password" /><br/>
		<input type="submit" value="Register" />
	</sf:form>

** como colocamos o commandName, dentro do model que sera encaminhado para esse form
deve ter um objeto chamado spitter: 
** a palavra reservada que faz o link com o atributo de spitter é o path

	@RequestMapping(value="/register", method=GET)
	public String showRegistrationForm(Model model) {
		model.addAttribute(new Spitter());
		return "registerForm";
	}
			
Para mostrar os erros, o controle é feito logo apos o campo passando path
como parametro no erro:
	
	<sf:form method="POST" commandName="spitter" >
		First Name: <sf:input path="firstName" />
		<sf:errors path="firstName" /><br/> // ainda pode ter  cssClass="error"
	...
	</sf:form>
	
** somente o erro do firstName será mostrado apos o  cmapo. 
Para mostrar todos os erros agrupados no final: 
	<sf:form method="POST" commandName="spitter" >
		<sf:errors path="*" element="div" cssClass="errors" />
		...
	</sf:form>

**por default os erros sao mostrados em <span>. colocar o elemento div quando 
for agrupado irá mostrar de forma melhor

Para configurar a mensagem que será apresentada, temos que fazer duas ações: 

	- Na classe bean onde a validacao irá ocorrer, devemos estipular a mensagem: 
	
		@NotNull
		@Size(min=5, max=16, message="{username.size}")
		private String username;
		@NotNull
		@Size(min=5, max=25, message="{password.size}")
		private String password;
	
	- Essa mensagem virá de um arquivo de templates de mensagens que precisamos definir: 
		O arquivo se chama ValidationMessages.properties e deve estar no classpath: 
		...
		firstName.size= First name must be between {min} and {max} characters long.
		lastName.size= Last name must be between {min} and {max} characters long.
		.. 
		
Podemos criar arquivos de mensagens em outras linguas, e se o browser do usuario estiver
configurado como espanhos, o spring vai pegar o arquivo: ValidationErrors_es.properties


-> General TagLibrary 

	
	Foi a primeira introduzida no spring e esta disponivel nas versoes mais antigas. (antes da form-to-model)
	
	<%@ taglib uri="http://www.springframework.org/tags" prefix="s" %>
	
	tags disponiveis:  ( a maioria foi marcada como obsoleta)
		<s:bind>
		<s:escapeBody>
		<s:hasBindErrors>
		<s:htmlEscape>
		<s:message:>
		<s:nestedPath>
		<s:theme>
		<s:transform>
		<s:url>
		<s:eval>


Para fazer internacionalizacao, usar o message: 
	<h1><s:message code="spittr.welcome" /></h1>
	
Para mostrar a mensagem, será necessarios usar um recurso de message-resource. Existem varios recursos 
ja implementados. O mais usado é o ResourceBundleMessageSource

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
		messageSource.setBasename("messages");
		return messageSource;
	}

	O Applicatino Context vai procurar no classpath os aquivos de properties messages e qualquer derivado dele. 
	
	Existe tambem o ReloadableResourceBundleMessageSource, que tem a habilidade de recarregar o properties sem 
	precisar reinicializar a app: 
	
		@Bean
		public MessageSource messageSource() {
			ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
			messageSource.setBasename("file:///etc/spittr/messages");
			messageSource.setCacheSeconds(10);
			return messageSource;
		}
		
		os protocolos podem ser 
			file: busca externo
			classpath: no classpath
			sem nada: busca no root do webapp

	Para criar o arquivo, criar um messages.properties e colocar dentro:
		spittr.welcome=Welcome to Spittr!
	
	Para criar em outra lingua, criar um arquivo messages_es.properties e colocar: 
		spittr.welcome=Bienvenidos a Spittr!
		
		
	


USANDO TILES

imagine que precisa criar um header e footer para as paginas. Nao precisa visitar todas as paginas e altera-las. 

Para usar o tiles, precisamos de duas classes:
	- TilesConfigurer : para carregas as configuracoes e gerenciar o tiles
	- TilesViewResolver: ViewResolver para resolver as paginas
	
	Existe diferentes classes para Tiles2 e Tiles3 (diferenciadas pelo pacote: 
			org.springframework.web.servlet.view.tiles3
			org.springframework.web.servlet.view.tiles2
			
			
	Para carregar o TilesConfigurer: 
	
		@Bean
		public TilesConfigurer tilesConfigurer() {
			TilesConfigurer tiles = new TilesConfigurer();
			tiles.setDefinitions(new String[] {"/WEB-INF/layout/tiles.xml"});
			tiles.setCheckRefresh(true);
			return tiles;
		}
** o setDefinitions() aceita um String[] para passar todos os arquivos do tiles que temos. 
Para procurar todos os tiles dentro de web-inf:
	tiles.setDefinitions(new String[] {"/WEB-INF/**/tiles.xml"});
	
O ** é um wildcard que o TilesViewResolver vai usar para procurar os arquivos. 

	Para configurar o TilesViewResolver: 
	
		@Bean
		public ViewResolver viewResolver() {
			return new TilesViewResolver();
		}


Ja a configuracao em XML para ambos: 
	
	<bean id="tilesConfigurer" class="org.springframework.web.servlet.view.tiles3.TilesConfigurer">
		<property name="definitions">
			<list>
				<value>/WEB-INF/layout/tiles.xml.xml</value>
				<value>/WEB-INF/views/**/tiles.xml</value>
			</list>
		</property>
	</bean>
	
	<bean id="viewResolver" class="org.springframework.web.servlet.view.tiles3.TilesViewResolver" />


Definindo um arquivo de tiles: 
	Composto por 1..* <definition>, que tem 1..* <put-attributes>
	
	<tiles-definitions>
		<definition name="base" template="/WEB-INF/layout/page.jsp">
			<put-attribute name="header" value="/WEB-INF/layout/header.jsp" />
			<put-attribute name="footer" value="/WEB-INF/layout/footer.jsp" />
		</definition>

... continua.... 


Working with Thymeleaf

	Thymeleaf roda em todos os lugares que o HTML roda mas resolve algumas pendencias que o JSP nao consegue, pois 
	thymeleaf não é amarrado na especificacao servlet como é o JSP. 
	
Para usar o thymeleaf, precisamos instanciar 3 beans: 
	- ThymeleafViewResolver: resolve o thlf template 
	- SpringTemplageEngine: processar os templates 
	- TemplateResolver: carrega os templates
	

Codigo para carregar: 
	- view
		@Bean
		public ViewResolver viewResolver(SpringTemplateEngine templateEngine) {
			ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
			viewResolver.setTemplateEngine(templateEngine);
			return viewResolver;
		}
	- engine	
		@Bean
		public TemplateEngine templateEngine(TemplateResolver templateResolver) {
			SpringTemplateEngine templateEngine = new SpringTemplateEngine();
			templateEngine.setTemplateResolver(templateResolver);
			return templateEngine;
		}
	- resolver	
		@Bean
		public TemplateResolver templateResolver() {
			TemplateResolver templateResolver =new ServletContextTemplateResolver();
			templateResolver.setPrefix("/WEB-INF/templates/");
			templateResolver.setSuffix(".html");
			templateResolver.setTemplateMode("HTML5");
			return templateResolver;
		}


XML

	<bean id="viewResolver"class="org.thymeleaf.spring3.view.ThymeleafViewResolver"
			p:templateEngine-ref="templateEngine" />
			
	<bean id="templateEngine" class="org.thymeleaf.spring3.SpringTemplateEngine"
			p:templateResolver-ref="templateResolver" />
			
	<bean id="templateResolver" class="org.thymeleaf.templateresolver.ServletContextTemplateResolver"
			p:prefix="/WEB-INF/templates/"
			p:suffix=".html"
			p:templateMode="HTML5" />


Definindo templates do Thymeleaf

	Ao inves de taglibraries, usa namespace: 
	
	HOME.HTML: 
		
	<html xmlns="http://www.w3.org/1999/xhtml"xmlns:th="http://www.thymeleaf.org">   <-- declarando namespace
		<head>
			<title>Spittr</title>
			<link rel="stylesheet" type="text/css" th:href="@{/resources/style.css}">   <- link pro css
			</link>
		</head>
		<body>
			<h1>Welcome to Spittr</h1>
			<a th:href="@{/spittles}">Spittles</a> |					<-- link para as paginas
			<a th:href="@{/spitter/register}">Register</a>
		</body>
	</html>



... Pesquisar mais sobre thymeleaf....




























			