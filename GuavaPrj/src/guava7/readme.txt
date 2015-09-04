EventBuf

Faz o esquema do publish/subscrive eventos. 

A principal classe é a com.google.common.eventbus.EventBus. Nela sao inscritos e notificados
os elementos. 

	EventBus eventBus = new EventBus();
	
Podemos atribuir um nome ao EventBus (para propsito de log)

	EventBus eventBus = new EventBus(TradeAccountEvent.class.getName());
	
Para que um objeto possa receber notificacoes, deve: 
	- ter um metodo publico que receba um argumento do tipo do evento esperado. 
	- anotado com um @Subscribe
	- deve se registrar, passando ele mesmo, em EventBus.register()
	

Para postar, um metodo deve postar um evento em EventBus.post(). O EventBus vai enviar o 
objeto para os subscribers registrados. 

Os handlers serao chamados serialmente, por isso, é importante que sejam rapidos. Se um 
handler demorar, é bom abrir uma nova thread para tratar a requisicao dentro do handler.

O EventBus nao vai chamar os handlers com multithread. A menos que nos indiquemos que o 
metodo é thread-safe. Para indicar, anotar com @AllowConcurrentEvent. 

Ex de como criar um subscribe: 

	public class SimpleTradeAuditor {
		private List<TradeAccountEvent> tradeEvents =
			Lists.newArrayList();

		public SimpleTradeAuditor(EventBus eventBus){ // o construtor recebe um EventBus e já se registra. 
			eventBus.register(this);
		}

		@Subscribe // o metodo auditTrade marcado como subscribe
		public void auditTrade(TradeAccountEvent tradeAccountEvent){ // recebe um argumento unico (event)
			tradeEvents.add(tradeAccountEvent);
			System.out.println("Received trade "+tradeAccountEvent);
		}
	}
 
** o TradeAcountEvent é um pojo com os atributos:

	private double amount;
	private Date tradeExecutionTime;
	private TradeType tradeType;
	private TradeAccount tradeAccount;


Event Publishing

Para postar um evento: 

	public class SimpleTradeExecutor {
		//o construtor recebe um EventBus e armazena para usar depois
		public SimpleTradeExecutor(EventBus eventBus) {
			this.eventBus = eventBus;
		}
		
		//chama um eventBus.post() em outro metodo
	}
	
	
	
*** Podemos criar mais de um subscribe por classe. Desde que se registrem para eventos diferentes. 
*** Apros criar um evneto, podemos extends na classe do evento e criar novos. Por exemplo :

class TradeEvent;  
class BuytTradeEvent extends TradeEvent; 
class SellTradeEvent extends TradeEvent; 

e ouvir ambos na mesma classe, colocando eventos diferentes nos metodos. 

** Se colocarmos um Object como metodo, vamos ouvir qualquer evento do EventBus 

Nada impede de ter mais de um eventbus instance. Nesse caso, eles trabalharao totalmente independentes um do outro. 


o eventBus tem um metodo unregister() que vai tirar o subscribe da instancia (nao da classe). 

	eventBus.unregister(this); //vai tirar o registro da instancia. 
	
	
AsyncEventBus

Faz o processamento de um evento de forma assincrona. 
	
	AsyncEventBus asyncEventBus = new AsyncEventBus(executorService);
	
Ideal para quando sustepeitarmos que os subscribers estao executando processamentos pesados. 

Eventos postados e nao capturados por subscribers sao armazenados em um DeadEvent. 
O DeadEvent tem um metodo get() que retorna o evento original

	@Subscribe
	public void handleUnsubscribedEvent(DeadEvent deadEvent){
		logger.warn("No subscribers for "+deadEvent.getEvent());
	}













	
	