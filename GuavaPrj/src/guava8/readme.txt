

Working with files. 

Para ajudar a manipular arquivos, tem a classe Files. 

	File a = new File("A"); 
	File b = new File("B"); 

Copying a file
	Files.copy(a,b); 
	
Moving a file
	Files.move(a,b); 
	
Ler arquivos em String: 
	List<String> readLines = Files.readLines(file,Chasets.UTF_8); 
	
** ainda tem uma sobrescrita desse metodo readLines() que recebe tambem um metodo 
LineProcessor como parametro. 

O lineprocessor é um processador de linha em que podemos obter uma informação especifica 
do arquivo por linha e retorna-la. 

	public class ToListLineProcessor implements LineProcessor<List<String>>{
		private static final Splitter splitter = Splitter.on(",");
		private List<String> bookTitles = Lists.newArrayList();
		private static final int TITLE_INDEX = 1;

		// metodo para fazer o parser do arquivo e acumular na lista de bookTitles
		
		// metodo para retornar o bookTitles: T getResult()
	}
	
	
Para gerar o codigo hash de um arquivo:

	HashCode hashCode = Files.hash(file, Hashing.md5());
	System.out.println(hashCode);


Escrevendo no arquivo.

File file = new File("src/test/resources/quote.txt");
Files.write(texto,file, Charsets.UTF_8);
Files.append(hamletQuoteEnd,file,Charsets.UTF_8);

** o File.write vai sobrescrever o conteudo
** o File.append vai adicionar ao conteudo.

Nao precisa abrir arquivo, flush ou close. 


Interfaces InputSupplier e OutputSupplier

O guava faz o conceito de source e sink para ler e gravar arquivos. 

Tem dois tipo de sources
	- ByteSource
	- CharSource
	
e dois tipos de sinks
	- ByteSink
	- CharSink
	
Podemos criar as instancias do Byte (sink e source) e Char(sink e souce) atraves da classe
Files, ByteStreams / CharStreams. 

Para criar um ByteSource: 

	File f1 = new File("a.pdf"); 
	byteSource = Files.asByteSource(f1); 
	byte[] readBytes = byteSoruce.read(); 
	
	
para criar um ByteSink

	File dest = new File("src/test/resources/byteSink.pdf");
	dest.deleteOnExit();
	byteSink = Files.asByteSink(dest);
	File file = new File("src/main/resources/sample.pdf");
	byteSink.write(Files.toByteArray(file));


Copiando de um bytesink para um bytesource

	File dest = new File("src/test/resources/sampleCompany.pdf");
	File source = new File("src/main/resources/sample.pdf");
	
	ByteSource byteSource = Files.asByteSource(source); 
	ByteSink byteSink = Files.asByteSink(dest); 
	
	byteSource.copyTo(byteSink); 
	
	
A classe ByteStreams tem utilidades para trabalhar com InputStream e OutputStream.
A classe CharStreams para trabalhar com Reader e Writer


Closer

	para fechar os recursos: 
	
	Closer closer = Closer.create();
	try{
		...
		closer.register(reader); // pode ser um reader qualquer
		closer.register(writer); 
	catch()
		...
	finally{
		closer.close()
	}


BaseEncoding

Usado para converter tipos: 

	BaseEncoding baseEncoding = BaseEncoding.base64(); 
	String encoded = baseEncoding.encode(byte[]) // para str
	baseEncoding.decode(encoded) // para byte[]

	
Pode usar para converter para binario arquivo text?




	
	
	