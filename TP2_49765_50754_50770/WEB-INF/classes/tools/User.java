package tools;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class User {
	
	// Nome do ficheiro com os utilizadores
    private static String file =  "src/main/webapp/" + "xml/lista.xml";
    private static String fileXSD = XMLDoc.getContexto() + "xml/lista.xsd";

    // Ficheiro dos utilizadores
    private static Document doc = null;
    
    // Valor do id para tornar perfil único
    private UUID id = null;
    
    // Data em que foi atualizado pela última vez
    private LocalDateTime time = LocalDateTime.now();
    private static String padraoTempo = "yyyy-MM-dd'T'HH:mm:ss";
    
    // Nome de utilizador
    private String username = null;
    
    // Palavra-passe encriptada e revelada
    private String password = null;
    private String passwordEncriptada = null;
    
    // Nacionalidade
    private String nacionalidade = null;
    
    // Idade
    private int idade = 0;
    
    // Cor favorita
    private String cor = null;
    
    // Foto encriptada
    private String foto = "teste";
    private static String pathFoto = "src/main/webapp/fotos/";

    // Scanner para aceitar entrada de dados
    private static Scanner sc = new Scanner(System.in);
    
    // Simbolo atribuido ao utilizador
    private String simbolo = null;
        
    public UUID getId() {
    	return this.id;
    }
    
    public void setId(UUID id) {
    	this.id = id;
    }
    
    public String getUsername() {
    	return this.username;
    }
    
    public void setUsername(String username) {
    	this.username = username;
    }
    
    public void setIdade(int idade) {
		this.idade = idade;
	}
    
    public void setCor(String cor) {
    	this.cor = cor;
    }

	/**
     * Carregar o documento no inicio da classe
     */
    private static void load() throws SAXException, IOException {
    	// Documento xml
    	Document d = XMLDoc.parseFile(file);

    	// Validar o xml com o xsd
    	XMLDoc.validDocXSD(d, fileXSD);
    	
    	// Guardar o documento na classe
    	doc = d;
    }
    
    public User() {
    	this.time = LocalDateTime.now();
    	
    	try {
			load();
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
    }
    
    public User(String pathXmlJSP, String pathXsdJSP, String pathFotosJSP) {
    	this.time = LocalDateTime.now();
    	
    	file = pathXmlJSP;
    	fileXSD = pathXsdJSP;
    	pathFoto = pathFotosJSP;
    	
    	try {
			load();
		} catch (SAXException | IOException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Metodo para armazenar o XML o registo de vitoria/empate/derrota
     * 
     * @param simbolo Simbolo do vencedor
     */
    public void armazenarRegistos(String simbolo) {
	    Registo reg = new Registo(this.id);
	
	    // caso do empate
	    if (simbolo.equals("E")) {
	    	reg.setEmpate(true);
	
	    	reg.setVitoria(false);
	    	reg.setDerrota(false);
	    }
	    	
	    // vitoria se o simbolo do utilizador for igual ao passado na funcao
	    if (this.simbolo.equals(simbolo)) {
	    	reg.setVitoria(true);
	    		
	    	reg.setEmpate(false);
	    	reg.setDerrota(false);
	    } else {
	    	reg.setDerrota(true);
	
	    	reg.setVitoria(false);
	    	reg.setEmpate(false);
	    }
	    reg.saveOrUpdateData();	
    }
    
    /**
     * Validar e definir o nome de utilizador
     * 
     * @param username
     * @return true, bem validadado e definido
     */
    public boolean validarDefinirUsername(String username) {
    	// username vazio, retorna logo false
    	if(username.length() == 0) {
    		return false;
    	}
    	
    	// tamanho minimo e maximo
    	int min = 3, max = 30;
    	// expressao regular para verificar se o username tem apenas caracteres  validos
    	// caracteres validos: letra, numeros e hifens.
    	String exp_regular = "^[a-zA-Z0-9_-]{"+min+","+max+"}$";
    	
    	// vamos utilizar uma funcao propria do objeto String, que utiliza expressoes regulares 
    	// para verificar se condiz com as condicoes especificadas
    	if(!username.matches(exp_regular)) {
    		return false;
    	}
    	
    	// definicao do username
    	this.username = username;
    	return true;
    }
    
    /**
     * Encriptar a palavra-passe e dar set na mesma
     * 
     * @param password
     * @return true, bem validado e definido
     * @throws NoSuchAlgorithmException 
     */
    public boolean setPassword(String password) throws NoSuchAlgorithmException {
    	this.password = password;
    	this.passwordEncriptada = XMLDoc.SHA256(password);
    	return true;
    }
    
    /**
     * Validar a idade e defini-la
     * 
     * @param idade
     * @return true, bem validado e definido
     */
    private boolean validarDefinirIdade(String idade) {
    	String exp_regular = "^[0-9]{"+1+","+3+"}$";

    	if(!idade.matches(exp_regular)) {
    		return false;
    	}
    	
    	this.idade = Integer.valueOf(idade);
    	return true;
    }
    
    /**
     * Obter o tempo configurado pronto para xml
     * 
     * @return String, tempo
     */
    private String getTempo() {
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern(padraoTempo);
		return time.format(formatter);
    }
    
    public boolean setNacionalidade(String nacionalidade) {
    	String exp_regular = "^[a-zA-Z]{"+1+","+30+"}$";
    	if(!nacionalidade.matches(exp_regular)) {
    		return false;
    	}
    	
    	this.nacionalidade = nacionalidade; 
    	return true;
	}
    
    private boolean setFoto() {
    	pathFoto += "cara.jpg";
    	MyImage img = new MyImage(pathFoto);
    	this.foto = img.getBase64();
    	return true;
    }
    
    private boolean setFoto(String path) {
    	pathFoto += path;
    	MyImage img = new MyImage(pathFoto);
    	this.foto = img.getBase64();
    	return true;
    }
    
    /**
     * Método para registar um novo utilizador
     * 
     * @return
     * @throws Exception
     */
    public static void registar(User user) throws Exception {
    	System.out.println("Insira os dados do registo: (username, password, nacionalidade, idade, foto)");    	
    	do {
    	    System.out.print("Nome do utilizador: ");
    	    String username = sc.nextLine();
    	    if (user.validarDefinirUsername(username))
    	    	break;
    	    System.out.println("Nome de utilizador inválido!");
    	} while (true);

    	do {
    	    System.out.print("Senha para autenticação: ");
    	    String password = sc.nextLine();
	    	if (user.setPassword(password))
	    		break;
    	    System.out.println("Senha inválida!");
    	} while (true);
    	
    	do {
    	    System.out.print("Nacionalidade (pt ou portugues, p.e.): ");
    	    String nacionalidade = sc.nextLine();
	    	if (user.setNacionalidade(nacionalidade))
	    		break;
    	    System.out.println("Nacionalidade inválida!");
    	} while (true);
    	
    	do {
    	    System.out.print("Idade: ");
    	    String idade = sc.nextLine();
    	    if(user.validarDefinirIdade(idade))
    	    	break;
    	    System.out.println("Idade inválida!");
    	} while(true);
    	
    	// adicionar foto generica
    	user.setFoto();
    	
    	user.print();
    	replace(user);
    }
    
    public static boolean registarJSP(User user) throws Exception {
    	user.setFoto();
    	
    	user.print();
    	replace(user);
    	return true;
    }

    /**
     * Login com um utilizador ja registado
     * 
     * @throws Exception 
     */
    public static void login(User user) throws Exception {    	
    	
    	do {
    		do {
        	    System.out.print("Nome do utilizador: ");
        	    String username = sc.nextLine();
        	    if (user.validarDefinirUsername(username))
        	    	break;
        	    System.out.println("Nome de utilizador inválido!");
        	} while (true);

        	do {
        	    System.out.print("Senha para autenticação: ");
        	    String password = sc.nextLine();
    	    	if (user.setPassword(password))
    	    		break;
        	    System.out.println("Senha inválida!");
        	} while (true);
    	} while(!findUser(user.username, user.password));
    	
		user.getDataAfterLogin();
		//user.print();
    }
    

	/**
     * Display dos dados
     * @throws Exception
     */
    public void print() throws Exception {
    	System.out.println("----- Dados do Utilizador -----");
    	System.out.println("Identificador (UUID): " + id);
    	System.out.println("Última atualização em: " + time);
    	System.out.println("Nome de utilizador: " + username);
    	System.out.println("Senha: " + password);
    	System.out.println("Senha encriptada: " + passwordEncriptada);
    	System.out.println("Idade: " + idade);
    	System.out.println("Nacionalidade: " + nacionalidade);
    	System.out.println("Foto em base64: " + foto);
    	System.out.println("Cor favorita do utilizador: " + cor);
    	System.out.println("----- ------------------- -----");
    }
    
    
    
    /**
     * Valida o documento XML, garante backup e grava as alterações.
     * 
     * @throws SAXException                         Se ocorrer um erro ao analisar o
     *                                              documento XML.
     * @throws IOException                          Se ocorrer um erro ao ler ou
     *                                              escrever arquivos.
     * @throws TransformerFactoryConfigurationError Se houver um problema na
     *                                              configuração da fábrica de
     *                                              transformação XML.
     * @throws TransformerException                 Se ocorrer um erro ao
     *                                              transformar o documento XML.
     * 
     * @author Eng. Porfírio Filipe
     */
    private static void save()
	    throws SAXException, IOException, TransformerFactoryConfigurationError, TransformerException {

		// 1. Valida o documento XML contra o esquema XSD para garantir sua conformidade
		// com a estrutura esperada
		// --> Prevenir a gravação de dados inválidos ou inconsistentes.
		XMLDoc.validDocXSD(doc, fileXSD);
	
		// 2. Cria o caminho completo para o ficheiro XML de utilizadores
		String docXML = file;
	
		// 3. Gera um nome de ficheiro para guardar um backup do ficheiro original
		String backup = XMLDoc.gerarNomeFBackupVersao(docXML);
	
		// 4. Grava as alterações feitas no ficheiro XML de utilizadores
		// --> Usando um mecanismo de bloqueio para evitar problemas de concorrência.
		// --> Renomeia o ficheiro original antes de o alterar.
		XMLDoc.gravarLock(doc, docXML, backup);
    }
    
    /**
     * Atualiza um utilizador existente ou cria um novo se não existir
     * 
     * Este método recebe um objeto da classe `User` como argumento e atualiza os
     * dados do utilizador indicado em parametro no DOM. Se o utilizador não existir
     * no documento, ele é criado.
     * 
     * @param user Instância da classe User com os dados do utilizador a ser
     *             atualizado/adicionado
     * @throws XPathExpressionException             Exceção lançada caso ocorra um
     *                                              erro na expressão XPath
     *                                              utilizada para localizar o
     *                                              utilizador
     * @throws DOMException                         Exceção lançada caso ocorra um
     *                                              erro ao manipular o documento
     *                                              XML
     * @throws ParserConfigurationException         Exceção lançada caso não seja
     *                                              possível criar um novo documento
     *                                              XML (necessário para a criação
     *                                              de um novo utilizador)
     * @throws TransformerException                 Exceção lançada caso ocorra um
     *                                              erro ao transformar o documento
     *                                              XML
     * @throws TransformerFactoryConfigurationError Exceção lançada caso não seja
     *                                              possível configurar a fábrica de
     *                                              transformadores XSLT
     * @throws IOException                          Exceção lançada caso ocorra um
     *                                              erro ao ler ou escrever o
     *                                              documento XML
     * @throws SAXException                         Exceção lançada caso ocorra um
     *                                              erro ao analisar o documento XML
     * 
     * @author Eng. Porfírio Filipe
     */
    private static void replace(User user) throws ParserConfigurationException, SAXException, IOException,
	    TransformerFactoryConfigurationError, TransformerException, XPathExpressionException {

		// Procura o elemento "user" com o mesmo ID do utilizador indicado em parametro
		NodeList us = XMLDoc.getXPath("/lista/user[id/text()='" + user.id + "']", doc);
	
		// Comentário:
		// - A expressão XPath acima procura por um elemento "user" dentro do elemento
		// "users"
		// - O filtro `[userid/text()=' + user.getUserId() + ']` garante que apenas o
		// utilizador com o ID especificado seja selecionado.
	
		// Procura o Nó principal
		NodeList nl = doc.getElementsByTagName("lista");
		if (nl.getLength() != 1) {
		    System.out.println("Não encontrou o elemento raiz!");
		    return; // erro, inconsistencia
		}
		Node principal = nl.item(0);
	
		// Comentário:
		// - Obtém o elemento "users" principal do documento XML.
		// - Verifica se existe apenas um elemento "users". Se não, há um erro na
		// estrutura do documento.
	
		// Verifica se o utilizador já existe (tamanho da NodeList us será 1)
		if (us.getLength() == 1) {
		    // Se o utilizador existir, remove o elemento "user" antigo do documento
		    principal.removeChild(us.item(0));
		} else
		    // Adiciona o utilizador atual ao DOM
		    user.toDocument();
		
		// Salva as alterações no disco.
		save();
    }
        
    /**
     * Atualiza o DOM com os dados do utilizador atual
     * 
     * @throws ParserConfigurationException em caso de erro
     */
    private void toDocument() throws ParserConfigurationException {
		Element userElement = doc.createElement("user");
		
		this.id = UUID.randomUUID();
		Element aux = doc.createElement("id");
		aux.setTextContent(getId().toString());
		userElement.appendChild(aux);
	
		aux = doc.createElement("lastupdated");
		aux.setTextContent(getTempo());
		userElement.appendChild(aux);
	
		aux = doc.createElement("username");
		aux.setTextContent(username);
		userElement.appendChild(aux);

		aux = doc.createElement("password");
		aux.setTextContent(passwordEncriptada);
		userElement.appendChild(aux);

		aux = doc.createElement("nacionalidade");
		aux.setTextContent(nacionalidade);
		userElement.appendChild(aux);
		
		aux = doc.createElement("idade");
		aux.setTextContent(Integer.toString(idade));
		userElement.appendChild(aux);
		
		// Fotografia (se disponível)
		aux = doc.createElement("foto");
		aux.setTextContent((foto == null) ? "nan" : foto);
		userElement.appendChild(aux);
		
		// Cor favorita (se disponível)
		aux = doc.createElement("cor");
		aux.setTextContent((cor == null) ? "nan" : cor);
		userElement.appendChild(aux);

		// Procura o Nó principal
		NodeList users = doc.getElementsByTagName("lista");
		if (users.getLength() != 1) {
		    System.out.println("Não encontrou o elemento raiz!");
		    return; 
		}
		Node principal = users.item(0);

		// Acrescenta o utilizaor atual
		principal.appendChild(userElement);
		
		try {
		    XMLDoc.validDocXSD(doc, fileXSD);
		} catch (SAXException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }

    
    /**
     * Metodo para encontrar o utilizador ja criado para proceder ao login
     * 
     * @param username
     * @param password
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static boolean findUser(String username, String password) throws NoSuchAlgorithmException {
    	NodeList users = doc.getElementsByTagName("user");

    	for(int i = 0; i < users.getLength(); i++) {
    		if (users.item(i).getNodeType() == Node.ELEMENT_NODE) {
    			
    			Element userElement = (Element) users.item(i);
                String currentUsername = userElement.getElementsByTagName("username").item(0).getTextContent();
                if (currentUsername.equals(username)) {
	    			
	    			String passEncrip = XMLDoc.SHA256(password);
	    			String currentPassword = userElement.getElementsByTagName("password").item(0).getTextContent();
	    			if(currentPassword.equals(passEncrip)) {
		                return true;
	    			}
                }
    		}
    	}
    	System.out.println("Utilizador especifico nao encontrado.");
    	return false;
    
    }
    
    private void getDataAfterLogin() throws NoSuchAlgorithmException {
    	NodeList users = doc.getElementsByTagName("user");
    	
    	for(int i = 0; i < users.getLength(); i++) {
    		if (users.item(i).getNodeType() == Node.ELEMENT_NODE) {
    			
    			Element userElement = (Element) users.item(i);
                String currentUsername = userElement.getElementsByTagName("username").item(0).getTextContent();
                
                if (currentUsername.equals(username)) {
	    			
	    			String passEncrip = XMLDoc.SHA256(password);
	    			String currentPassword = userElement.getElementsByTagName("password").item(0).getTextContent();
	    			if(currentPassword.equals(passEncrip)) {
	    				
	    				String idString = userElement.getElementsByTagName("id").item(0).getTextContent();
	    				setId(UUID.fromString(idString));
	    				validarDefinirIdade(userElement.getElementsByTagName("idade").item(0).getTextContent());
	    				setNacionalidade(userElement.getElementsByTagName("nacionalidade").item(0).getTextContent());
	    				setFoto();
	    				break;
	    			}
                }
    		}
    	}
    }
}
