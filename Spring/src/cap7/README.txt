Cap 7 advanced MVC

This chapter covers
- Alternate Spring MVC configuration options
- Handling file uploads
- Handling exceptions in controllers
- Working with flash attributes


Alteranativa ao MVC configuration

As vezes precisamos mudar como o spring é configurado (DispatcherServlet). 
Nomalmente instanciamos o AbstractAnnotationConfigDispatcherServletInitializer

No entanto para rodar em container que nao suportam servlet 3.0, precisamos usar
o web.xml. 


Customisando o DispatcherServlet


Existem mais do que os 3 metodos do AbstractAnnotationConfigDispatcherServletInitializer que precisamos 
iniciar no Initializer. 

Por ex. Depois que o AbstractAnnotationConfigDispatcherServletInitializer initializa o dispatcher, 
o metodo customizedRegistration() é chamado. 
Sobrescrevendo esse metodo, podemos colocar configuracoes adicionais no dispatcher
é passado como parametro um ServletRegistration.Dynamic. 

Ex: suportar multipart uploads: 
	
	@Override
	protected void customizeRegistration(Dynamic registration) {
		registration.setMultipartConfig(new MultipartConfigElement("/tmp/spittr/uploads"));
	}

** configurando para armezenar arquivos na pasta acima. 

** o spring permite criar varios WebApplicationInitializer

Registrando um Servlet

	public class MyServletInitializer implements WebApplicationInitializer {
		@Override
		public void onStartup(ServletContext servletContext)throws ServletException {
			Dynamic myServlet =servletContext.addServlet("myServlet", MyServlet.class);
			myServlet.addMapping("/custom/**");
		}
	}


Registrando filter e mapping
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		javax.servlet.FilterRegistration.Dynamic filter = servletContext.addFilter("myFilter", MyFilter.class);
		filter.addMappingForUrlPatterns(null, false, "/custom/*");
	}



