package cliente;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import tools.User;

public class Jogador {
	
	private final static String DEFAULT_HOST = "localhost";
	private final static int DEFAULT_PORT = 5025;
	
	private static Socket socket = null;
	public static BufferedReader is = null;
	public static PrintWriter os = null;
	private static Scanner leitor = null;
	
    private User user = null;
    
    // CONSTRUTOR PARA O JSP
    public Jogador(User user) throws UnknownHostException, IOException, NoSuchAlgorithmException {
   		// Cria o socket para acesso ao servidor
   		socket = new Socket(DEFAULT_HOST, DEFAULT_PORT);

   		// Mostrar os parametros da ligação
   		System.out.println("Ligação: " + socket);

   		// Stream para escrita no socket
   		os = new PrintWriter(socket.getOutputStream(), true);

   		// Stream para leitura do socket
   		is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    		
   		leitor = new Scanner(System.in);
   		System.out.println("\nJava-> Ligação estabelecida: " + socket + '\n');
   		
   		colocarEsperaJSP(user);
    }
    
    // CONSTRUTOR PARA A CONSOLA
    public Jogador() throws UnknownHostException, IOException {
    	boolean conectar = correrMenuInicial();
    	if (conectar) {

    		// Cria o socket para acesso ao servidor
    		socket = new Socket(DEFAULT_HOST, DEFAULT_PORT);

    		// Mostrar os parametros da ligação
    		System.out.println("Ligação: " + socket);

    		// Stream para escrita no socket
    		os = new PrintWriter(socket.getOutputStream(), true);

    		// Stream para leitura do socket
    		is = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    		
    		leitor = new Scanner(System.in);
    		System.out.println("\nJava-> Ligação estabelecida: " + socket + '\n');    		
    	}
    }
    
    private void colocarEsperaJSP(User user) {
		AtomicBoolean jspPronto = new AtomicBoolean(false);
		
		os.println(user.getUsername());
		
		Thread threadLeitura = new Thread(() -> {
			try {
				String line;
                while ((line = is.readLine()) != null) {
                	
                	if (line.startsWith("Oponente escolhido. A iniciar o jogo.")) {
                		System.out.println(line);
                		jspPronto.set(true);
                		break;
                	}
                	
                	if (line.startsWith("FIM_DA_LISTA")) {
                		continue;
                	}
                    	
                    if (line.equals("")) {
                    	continue;
                    }
                    
                    else {
                    	System.out.println(line);
                    }
                        
                }} catch (IOException e) {
                    System.err.println("Ligação cancelada remotamente pelo servidor!" + e.getLocalizedMessage());
        }});
		
		threadLeitura.start();
		
        int aux = 1;
        for (;;) {
    		if (aux==0) {break;}
    		String op = "1";
    		os.println(op);
    		aux--;
    	}

		// FICA PRESO ENQ A THREAD DE SELECAO DE MODO DE JOGO ACABE
        while(!jspPronto.get()) {}
        threadLeitura.interrupt();
    }
    
    // Fecha o socket
 	public void fechar() {
 		// No fim, fechar os streams e o socket
 		try {
 			if (os != null)
 				os.close();
 			if (is != null)
 				is.close();
 			if (socket != null)
 				socket.close();
 		} catch (IOException e) {
 			// if an I/O error occurs when closing this socket
 		}
 	}
 	
 	// Envia a jogada para o servidor
 	public void jogar(int l, int c) {
 		os.println(l);
 		os.println(c);
 	}
 		
 	// Recebe o tabuleiro do servidor
 	public String tabuleiro() throws IOException {
 		return tabuleiro("\n");
 	}
 	
 	public String tabuleiro(String newline) throws IOException {
 		String tab=is.readLine();
 		if(tab!=null)
 			return tab.replaceAll("\7", newline);
 		return "";
 	}
 	
	public static void main(String[] args) throws UnknownHostException, IOException {
		Jogador JW = new Jogador();
		AtomicBoolean prontoJogar = new AtomicBoolean(false);

		os.println(JW.user.getUsername());
		
    	Thread threadLeitura = new Thread(() -> {
			try {
				String line;
                while ((line = is.readLine()) != null) {
                	
                	if (line.startsWith("Oponente escolhido. A iniciar o jogo.")) {
                		prontoJogar.set(true);
                		break;
                	}
                	
                	if (line.startsWith("FIM_DA_LISTA")) {
                		continue;
                	}
                    	
                    if (line.equals("")) {
                    	continue;
                    }
                    
                    else {
                    	System.out.println(line);
                    }
                        
                }} catch (IOException e) {
                    System.err.println("Ligação cancelada remotamente pelo servidor!" + e.getLocalizedMessage());
        }});

	
    	threadLeitura.start();
				
        int aux = 1;
        for (;;) {
    		if (aux==0) {break;}
    		String op = leitor.nextLine();
    		os.println(op);
    		if (op.equals("2"))
    			aux = 1;
    		else
    			aux--;
    	}
        
		// FICA PRESO ENQ A THREAD DE SELECAO DE MODO DE JOGO ACABE
		while(!prontoJogar.get()) {}
        threadLeitura.interrupt();

		String simbolo = null;
		
        try (Scanner in = new Scanner(System.in)) {
			for (;;) {
				String tabuleiro = JW.tabuleiro();
		        System.out.println(tabuleiro);
		        
				if (tabuleiro.contains("X:")) {
		            simbolo = "X";
		        } else if (tabuleiro.contains("O:")) {
		            simbolo = "O";
		        }
				
				int l = Integer.parseInt(in.nextLine());
				int c = Integer.parseInt(in.nextLine());
				JW.jogar(l,c);
				
				if (tabuleiro.contains("Vitória do ")) {
					break;
				}
			}
		}
        
        JW.user.armazenarRegistos(simbolo);
        JW.fechar();
    }
    
    
    private int menuLoginRegisto() {
		Scanner scanner = new Scanner(System.in);

        int opcao;

        do {
            System.out.println("Menu Inicial");
            System.out.println("-------------------");
            System.out.println("1. Registo");
            System.out.println("2. Login");
            System.out.println("3. Sair");
            System.out.println("-------------------");
            System.out.print("Digite a opção desejada: ");

            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    return 1;
                case 2:
                    return 2;
                case 3:
                    System.out.println("Obrigado por usar o programa!");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida!");
            }

        } while (opcao < 1 || opcao > 3);

        scanner.close();
        return 0;
	}
    
    private boolean correrMenuInicial() {
        User user = new User();
        int opcao = menuLoginRegisto();
        
        if(opcao==1) {
            try {
                User.registar(user);
            } catch (Exception e) {
                System.out.println("Erro ao registar... ");
                e.printStackTrace();
            }
        }
        
        if(opcao==2) {
            try {
                User.login(user);
            } catch (Exception e) {
                System.out.println("Erro ao dar login... ");
                e.printStackTrace();
            }
        }
        
        this.user = user;
        return true;
    }

}