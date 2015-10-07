Spring Web Flow

This chapter covers
- Creating conversational web applications
- Defining flow states and actions
- Securing web flows


Nao existe configuracao de do web flow atraves de classes java. Por enquanto sometne atraves de xml. 
Para poder usar no xml, tem que declarar o namespace. 


	<?xml version="1.0" encoding="UTF-8"?>
	<beans xmlns="http://www.springframework.org/schema/beans"
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			xmlns:flow="http://www.springframework.org/schema/webflow-config"
			xsi:schemaLocation=
			"http://www.springframework.org/schema/webflow-config
			http://www.springframework.org/schema/webflow-config/[CA]
			spring-webflow-config-2.3.xsd
			http://www.springframework.org/schema/beans
			http://www.springframework.org/schema/beans/spring-beans.xsd">
			

Todos os requests são interceptados pelo DispatcherServlet, que processa as requisicoes.

Flow Executors

	Faz o controle dos flows. Quando a execucao chega nele, uma instancia é criada e ele ira controlar
	o flow do usuario. 
	
	Para criar um executor: 
		
		<flow:flow-executor id="flowExecutor" />
		
	Apesar dos executir criar e controlar o flow, não é ele que carrega os flows definitios. 
	
	
Flow Registry
	
	O flow registry é responsavel por carregar o flow definition e disponibiliza-lo ao flow executor: 
	
	<flow:flow-registry id="flowRegistry" base-path="/WEB-INF/flows">
		<flow:flow-location-pattern value="*-flow.xml" />
	</flow:flow-registry>
	
	Pela definiciao acima, o flow-registry vai buscar na pasta BASE-PATH (/WEB-INF/flows), todos os arquivos
	xml que terminam com -flow.
	
	Podemos tambem indicar diretamente o xml do flow: 
	Nesse caso, é flow-location e nao flow-location-pattern
	<flow:flow-registry id="flowRegistry">
		<flow:flow-location id="pizza" path="/WEB-INF/flows/springpizza.xml" />
	</flow:flow-registry>	
	
	
Flow Resquests

	Para usar flow, precisamos cadastrar um FlowHandlerMapping que possa ajudar ao DispatcherServlet a controlar
	os requests. 
	
		<bean class="org.springframework.webflow.mvc.servlet.FlowHandlerMapping">
			<property name="flowRegistry" ref="flowRegistry" />
		</bean>
	
	
Componentes do Flow: 

	Sao divididos em States, Transitions e flow data. 
	
	States sao pontos. um paralelo com uma estrada, os states sao cidades, postos ,... 
	Transitions sao as estradas que ligam um ponto a outros (passa de um ponto a outro). 
	
	
	STATES
		Action : Onde tem logicas
		DEcision: quebra o fluxo em duas direcoes baseado no dados 
		End: terminacao 
		Subflow: inicia um novo flow 
		View: coloca o flow em pausa e chama a participacao do ussuario
	
		View State: 
			Apresenta uma tela para iteracao com o usuario. Pode ser qualquer elemento do SpringMVC
			mas geralmente é feito em jsp 
			Para definir um view state:	
				
				<view-state id="welcome" />
			Ele especifica nao somente o id como o nome do view tambem. 
			Se quiser fazer separado: 
			
				<view-state id="welcome" view="greeting" />
				
			Se estivermos usando um form, 
				<view-state id="takePayment" model="flowScope.paymentDetails"/>	
			Aqui estavamos falando que o submit do takePayment vai umar um obj paymentDetails
			
			
		Action State:
			
			É quando o web flow chama um managed bean para fazer algo. 
			Apesar de nao ser obrigatorio, chama um evaluate. 
			
			<action-state id="saveOrder">
				<evaluate expression="pizzaFlowActions.saveOrder(order)" />
				<transition to="thankYou" />
			</action-state>
	
		Decision States: 
		
			Faz a quebra de um fluxo em 2 ou mais alternativas
			
			<decision-state id="checkDeliveryArea">
				<if test="pizzaFlowActions.checkDeliveryArea(customer.zipCode)"
						then="addCustomer"
						else="deliveryWarning" />
			</decision-state>	
			
			o decision nunca vem sozinho, sempre vem acompanhado de <if> 	
				
		
		Subflow State: 
			Possibilita desenvolver como se fosse em metodos... reapreveitar pontos...
			
			<subflow-state id="order" subflow="pizza/order">
				<input name="order" value="order"/>
				<transition on="orderCreated" to="payment" />
			</subflow-state>
			
		End state: 
			
			Um flow entra em transition para um end-state. Quando isso acnotece, o flow acaba. 
		
			<end-state id="customerReady" />
				
			O que acontece depois depende: 
			
				- Se a chamada for de um subflow: será usado para sair do subflow
				- Se o end-state tiver um view attribute especificado, a tela sera renderisada. 
				- se nao for subflow e nao tiver tela, o fluxo termina. 
			
			Um fluxo pode ter mais de um endflow, e cada um pode ser configurado com uma caracteristica 
			que precisamos. 
			
			
			
	TRANSITIONS	
				
		Cada state acima ja sabe para onde vai quando terminar. Todos, com excecao do end, deve ter 
		um transtion indicando o caminho. Se tiver somente um to="", ele é o caminho default. 
		
		<transition on="phoneEntered" to="lookupCustomer"/>
		
		Acima estamos dizendo que, se ocorrer um phoheEntered, o transitino ira para lookupCustomer. 
		
		E o abaixo, se um customer nao for encontrado, destinar para o registration: 
		
		<transition on-exception="com.springinaction.pizza.service.CustomerNotFoundException"
				to="registrationForm" />
		
		A diferença é que o on-exception trata uma exception, e nao um evento. 
		
		Para nao precisar ficar repetindo transtions na app inteira, pode definir glboais e ira 
		valer para todos...
		
		<global-transitions>
			<transition on="cancel" to="endState" />
		</global-transitions>
		
		
	FLOW DATA
		Sao os componentes que nos possibilitam acumular valores durante o flow. 
		
		é feito com o <var>
			<var name="customer" class="com.springinaction.pizza.domain.Customer"/>	
		
		A variavel acima do tipo Customer estará disponivel em todos os states com o nome customer
		
		Existem os seguintes escopos de variaveis: 
			Conversation: criado entre o toplevel start e toplevel end. Todos os subflows tem acesso
			Flow : Entre o começo e fim do flow. Nao acessivel a outros
			Request : entre a solicitacao e o final do flow.
			Flash: 
			View : somente na view e durante a view
		
		
		O default do var é o Flow.
		
		
		
	Pizza Order
	
		Essa é o webflow de um programa de solicitar pizza. 
	
			<?xml version="1.0" encoding="UTF-8"?>
			<flow xmlns="http://www.springframework.org/schema/webflow"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xsi:schemaLocation="http://www.springframework.org/schema/webflow
				http://www.springframework.org/schema/webflow/spring-webflow-2.3.xsd">
				
				//1 passo é instanciar o objeto que vai armazenar as informacoes do fluxo. 
				// toda vez que um cliente entra, um objeto é instanciado. 
				// o objeto esta abaixo. 
				<var name="order" class="com.springinaction.pizza.domain.Order"/>
				
				<subflow-state id="identifyCustomer" subflow="pizza/customer">
					<output name="customer" value="order.customer"/>
					<transition on="customerReady" to="buildOrder" />
				</subflow-state>
				
				<subflow-state id="buildOrder" subflow="pizza/order">
					<input name="order" value="order"/>
					<transition on="orderCreated" to="takePayment" />
				</subflow-state>
				
				<subflow-state id="takePayment" subflow="pizza/payment">
					<input name="order" value="order"/>
					<transition on="paymentTaken" to="saveOrder"/>
				</subflow-state>
				
				// o saveOrder usa o evaluate para chamar a funcao saveOrder e salvar o order 
				<action-state id="saveOrder">
					<evaluate expression="pizzaFlowActions.saveOrder(order)" />
					<transition to="thankCustomer" />
				</action-state>
			
				<view-state id="thankCustomer">
					<transition to="endState" />
					</view-state>
				<end-state id="endState" />
				
				<global-transitions>
					<transition on="cancel" to="endState" />
				</global-transitions>
		
			</flow>		
			
		Objecto order 
			
			public class Order implements Serializable {
				private static final long serialVersionUID = 1L;
				private Customer customer;
				private List<Pizza> pizzas;
				private Payment payment;
				
				... getter e setters...
			} 	
				
				
				
		Por default, o primeiro declarado é o primeiro state a ser visitado. 
		Podemos mudar a sequencia setando no <flow> o start-state
		
			<flow xmlns="http://www.springframework.org/schema/webflow"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xsi:schemaLocation="http://www.springframework.org/schema/webflow
				http://www.springframework.org/schema/webflow/spring-webflow-2.3.xsd"
				start-state="identifyCustomer">
						
		
		
		Codigo do thankCustormer.jsp, localizado em WEB-INF/flows/pizza/thankCustomer.jsp
		
			<html xmlns:jsp="http://java.sun.com/JSP/Page">
				<jsp:output omit-xml-declaration="yes"/>
				<jsp:directive.page contentType="text/html;charset=UTF-8" />
				<head><title>Spizza</title></head>
				<body>
					<h2>Thank you for your order!</h2>
					<![CDATA[
					<a href='${flowExecutionUrl}&_eventId=finished'>Finish</a>
					]]>
				</body>
			</html>
		
		
		Novo exemplo, agora com um <decision-state> para verificar se o customer ja esta cadastrado 
		na base de dados. Se nao estiver, redireciona para  o form de cadastramento: 
		
			
			<?xml version="1.0" encoding="UTF-8"?>
			<flow xmlns="http://www.springframework.org/schema/webflow"
				xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
				xsi:schemaLocation="http://www.springframework.org/schema/webflow
				http://www.springframework.org/schema/webflow/spring-webflow-2.3.xsd">
		
				<var name="customer" class="com.springinaction.pizza.domain.Customer"/>
			
				<view-state id="welcome">
					<transition on="phoneEntered" to="lookupCustomer"/>
				</view-state>
				
				//verificando se o customer existe atraves de exception. 
				// se nao existir (throws na exception), destinar para o form de cadastramento
				<actionstate id="lookupCustomer">
					<evaluate result="customer" expression="pizzaFlowActions.lookupCustomer(requestParameters.phoneNumber)" />
					<transition to="registrationForm" on-exception="com.springinaction.pizza.service.CustomerNotFoundException" />
					<transition to="customerReady" />
				</action-state>

				<view-state id="registrationForm" model="customer">
					<on-entry>
						<evaluate expression="customer.phoneNumber = requestParameters.phoneNumber" />
					</on-entry>
					<transition on="submit" to="checkDeliveryArea" />
				</view-state>

				<decision-state id="checkDeliveryArea">
					<if test="pizzaFlowActions.checkDeliveryArea(customer.zipCode)"
						then="addCustomer"
						else="deliveryWarning"/>
				</decision-state>

				<view-state id="deliveryWarning">
					<transition on="accept" to="addCustomer" />
				</view-state>
			
				<action-state id="addCustomer">
					<evaluate expression="pizzaFlowActions.addCustomer(customer)" />
					<transition to="customerReady" />
				</action-state>
				
				<end-state id="cancel" />
				
				<end-state id="customerReady">
					<output name="customer" />
				</end-state>
		
				<global-transitions>
					<transition on="cancel" to="cancel" />
				</global-transitions>
			
			</flow>
		
		
		
		
		
		
		
			