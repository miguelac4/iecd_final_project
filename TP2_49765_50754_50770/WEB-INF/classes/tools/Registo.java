package tools;

import java.io.IOException;
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

public class Registo {

	// Nome do ficheiro com os utilizadores
    private static String file = "xml/registos";
    
    // Ficheiro dos utilizadores
    private static Document doc = null;
    
    private UUID id = null;
    private boolean utilizadorJaExiste = false;
    
    private boolean vitoria = false;
    public int vitorias = 0;

    private boolean empate = false;
    private int empates = 0;
    
    private boolean derrota = false;
    private int derrotas = 0;
    
    
    /**
     * Setters e Getters
     */
    public boolean isVitoria() {
		return vitoria;
	}

	public void setVitoria(boolean vitoria) {
		this.vitoria = vitoria;
	}

	public boolean isEmpate() {
		return empate;
	}

	public void setEmpate(boolean empate) {
		this.empate = empate;
	}

	public boolean isDerrota() {
		return derrota;
	}

	public void setDerrota(boolean derrota) {
		this.derrota = derrota;
	}

	// Bloco que é executado ao inicio, quando a classe é corrida
    static {
    	load();
    }
    
    /**
     * Carregar o documento no inicio da classe
     */
    public static void load() {
    	String caminho = XMLDoc.getContexto() + file;
    	// Documento xml
    	Document d = XMLDoc.parseFile(caminho + ".xml");
    	try {
    		// Validar o xml com o xsd
    		XMLDoc.validDocXSD(d, caminho + ".xsd");
    	} catch (SAXException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	}
    	// Guardar o documento na classe
    	doc = d;
    }
        
    public Registo(UUID id) {
    	this.id = id;
    	
        this.utilizadorJaExiste = getDataFromXML();
    	if(utilizadorJaExiste)
    		System.out.println("Dados recuperados com sucesso");
    	else
    		System.out.println("Dados nao encontrados. A criar novo registo");
    }
    
    /**
     * Classe para ir buscar os registos de vitorias/empates/derrotas ao xml
     * 
     * @return true dados encontrados, se nao false
     */
    public boolean getDataFromXML() {
        NodeList registrosList = doc.getElementsByTagName("registos");
        
        if (registrosList.getLength() > 0) {
        	
            Element registrosElement = (Element) registrosList.item(0);
            NodeList registoList = registrosElement.getElementsByTagName("registo");
            for (int i = 0; i < registoList.getLength(); i++) {
            	
                Element registoElement = (Element) registoList.item(i);
                String id = registoElement.getElementsByTagName("id").item(0).getTextContent();
                if (id.equals(this.id.toString())) {
                	this.vitorias = Integer.parseInt(registoElement.getElementsByTagName("vitorias").item(0).getTextContent());
                	this.empates = Integer.parseInt(registoElement.getElementsByTagName("empates").item(0).getTextContent());
                	this.derrotas = Integer.parseInt(registoElement.getElementsByTagName("derrotas").item(0).getTextContent());
                	return true;
                }
                
            }
        }
        return false;
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
		XMLDoc.validDocXSD(doc, XMLDoc.getContexto() + file + ".xsd");
	
		// 2. Cria o caminho completo para o ficheiro XML de utilizadores
		String docXML = XMLDoc.getContexto() + file + ".xml";
	
		// 3. Gera um nome de ficheiro para guardar um backup do ficheiro original
		String backup = XMLDoc.gerarNomeFBackupVersao(docXML);
	
		// 4. Grava as alterações feitas no ficheiro XML de utilizadores
		// --> Usando um mecanismo de bloqueio para evitar problemas de concorrência.
		// --> Renomeia o ficheiro original antes de o alterar.
		XMLDoc.gravarLock(doc, docXML, backup);
    }
    
    /**
     * Cria um novo se não existir
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
    private static void replace(Registo registo) throws ParserConfigurationException, SAXException, IOException,
	    TransformerFactoryConfigurationError, TransformerException, XPathExpressionException {

		// Procura o elemento "user" com o mesmo ID do utilizador indicado em parametro
		NodeList us = XMLDoc.getXPath("/registos/registo[id/text()='" + registo.id + "']", doc);
	
		// Comentário:
		// - A expressão XPath acima procura por um elemento "user" dentro do elemento
		// "users"
		// - O filtro `[userid/text()=' + user.getUserId() + ']` garante que apenas o
		// utilizador com o ID especificado seja selecionado.
	
		// Procura o Nó principal
		NodeList nl = doc.getElementsByTagName("registos");
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
			registo.newDocument();
		
		// Salva as alterações no disco.
		save();
    }

    /**
     * Atualiza o DOM com os dados do utilizador atual
     * 
     * @throws ParserConfigurationException em caso de erro
     */
    private void newDocument() throws ParserConfigurationException {
		Element registo = doc.createElement("registo");
	
		Element aux = doc.createElement("id");
		aux.setTextContent(id.toString());
		registo.appendChild(aux);
		
		if(vitoria) {
			vitorias += 1;
			createElement(registo, "vitorias", vitorias);
			createElement(registo, "empates", empates);
			createElement(registo, "derrotas", derrotas);
		}
		
		if(derrota) {
			derrotas += 1;
			createElement(registo, "vitorias", vitorias);
			createElement(registo, "empates", empates);
			createElement(registo, "derrotas", derrotas);
		}
		
		if(empate) {
			empates += 1;
			createElement(registo, "vitorias", vitorias);
			createElement(registo, "empates", empates);
			createElement(registo, "derrotas", derrotas);
		}

		// Procura o Nó principal
		NodeList registos = doc.getElementsByTagName("registos");
		if (registos.getLength() != 1) {
		    System.out.println("Não encontrou o elemento raiz!");
		    return; 
		}
		Node principal = registos.item(0);

		// Acrescenta o utilizaor atual
		principal.appendChild(registo);
		
		try {
		    XMLDoc.validDocXSD(doc, XMLDoc.getContexto() + file + ".xsd");
		} catch (SAXException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }
    
    /**
     * Metodo para atualizar a arvore DOM com os dados mais recentes do utilizador 
     */
    private void updateRecentData() {
    	NodeList registos = doc.getElementsByTagName("registo");
    	System.out.println("Numero de registos > " + registos.getLength());
    	
    	for(int i = 0; i < registos.getLength(); i++) {
    		if (registos.item(i).getNodeType() == Node.ELEMENT_NODE) {
    			
    			Element registo = (Element) registos.item(i);
                String valorId = registo.getElementsByTagName("id").item(0).getTextContent();
                
                if (valorId.equals(id.toString())) {
                	int V = Integer.parseInt(registo.getElementsByTagName("vitorias").item(0).getTextContent());
                	int E = Integer.parseInt(registo.getElementsByTagName("empates").item(0).getTextContent());
                	int D = Integer.parseInt(registo.getElementsByTagName("derrotas").item(0).getTextContent());
                	
                	if(vitoria) {
                		V++;
                    	registo.getElementsByTagName("vitorias").item(0).setTextContent(String.valueOf(V));
                	} else if(derrota) {
                		D++;
                    	registo.getElementsByTagName("derrotas").item(0).setTextContent(String.valueOf(D));
                	} else if(empate) {
                		E++;
                    	registo.getElementsByTagName("empates").item(0).setTextContent(String.valueOf(E));
                	}
                	
                	
                }
    		}
    	}
    	// Salva as alterações no disco.
    	try {
			save();
		} catch (SAXException | IOException | TransformerFactoryConfigurationError | TransformerException e) {
			e.printStackTrace();
		}
    }


    public void saveOrUpdateData() {
    	if(utilizadorJaExiste) {
    		updateRecentData();
    	} else {
    		try {
				replace(this);
			} catch (XPathExpressionException | ParserConfigurationException | SAXException | IOException
					| TransformerFactoryConfigurationError | TransformerException e) {
				e.printStackTrace();
			}
    	}
    }
    
    private synchronized void createElement(Element element, String name, int value) {
    	Element aux = doc.createElement(name);
		aux.setTextContent(String.valueOf(value));
		element.appendChild(aux);
    }
    
    public static void main(String[] args) {
    	
    }
    
}
