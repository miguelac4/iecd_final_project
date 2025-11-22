package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Servidor {
    
	private static final int DEFAULT_PORT = 5025;
    
	private static ArrayList<Socket> onlineSockets = new ArrayList<>();
    private static HashMap<String, PrintWriter> onlineNames = new HashMap<>();
    
    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(DEFAULT_PORT);
            System.out.println("Servidor TCP iniciado.");

            for (;;) {
                System.out.println("A aguardar ligação no porto " + DEFAULT_PORT + "...");

                Socket newClient = serverSocket.accept();
                System.out.println("Cliente conectado: " + newClient);

                Thread clientHandler = new Thread(() -> { 
                try {
					handleClient(newClient);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}});
                clientHandler.start();
                
            }
        } catch (Exception e) {
            System.err.println("Erro no servidor: " + e);
        }
    }

    private static void handleClient(Socket client) throws InterruptedException {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter out = new PrintWriter(client.getOutputStream(), true);
            
            String username = in.readLine();
            System.out.println("Utilizador a iniciar: "+username);
            int opcao;

            do {
            	out.println("Bem-vindo:");
                out.println("-------------------");
                out.println("Escolha o modo de jogo");
                out.println("1. Jogar com alguém aleatório.");
                out.println("2. Escolher alguém da lista online.");
                out.println("3. Ficar em espera.");
                out.println("-------------------");
                out.println("Escolha a opção desejada: ");

                opcao = Integer.parseInt(in.readLine());
                out.println();

                switch (opcao) {
                    case 1:
                    	escolha1(client, username, out);
                        break;
                        
                    case 2:
                    	synchronized (onlineSockets) {
                    		if (onlineSockets.isEmpty()) {
                    			opcao = 3;
                    			out.println("Não há jogadores na fila de espera. A encaminhar para a fila.");
                    			escolha3(client, username, out);
                    			break;
                        	}
                    	}
                    	
                    	escolha2(client, in, out);
                        break;
                    
                    case 3:
                    	escolha3(client, username, out);
                    	break;
                    default:
                        System.out.println("Opção inválida!");
                }

            } while (opcao < 1 || opcao > 3);
            
        } catch (IOException e) {
            System.err.println("Erro ao comunicar com o cliente: " + e.getLocalizedMessage());
        }
    }
    
    
    private static void escolha1(Socket client, String username, PrintWriter out) {
    	Object lock = new Object();
        PrintWriter clientOut = null;
        Socket opponent = null;
        
        synchronized (lock) {
    		if (onlineSockets.isEmpty()) {
            	
            	onlineSockets.add(client);
            	onlineNames.put(username, out);
            
            } else {
            	opponent = onlineSockets.get(0);
                onlineSockets.remove(0);
                
                clientOut = onlineNames.remove( onlineNames.keySet().iterator().next() );
                System.out.println("Partida a iniciar");
                
                clientOut.println("Oponente escolhido. A iniciar o jogo.");
                out.println("Oponente escolhido. A iniciar o jogo.");
                
                Thread gameThread = new HandleConnectionThread(client, opponent);
                gameThread.start();
            }
        }
    }
    
    private static void escolha2(Socket client, BufferedReader in, PrintWriter out) throws IOException {
    	Object lock = new Object();
    	PrintWriter clientOut;
    	
    	synchronized (lock) {
        	boolean validInput = false;
    	    int index = -1;

    		do {
    			out.println("\nLista de utilizadores: ");
	    		for (Map.Entry<String, PrintWriter> entry : onlineNames.entrySet()) {
	    		    out.println("Utilizador: " + entry.getKey());
	    		}
	    	    out.println("Introduza o nome do oponente:");
	    	    out.println("FIM_DA_LISTA");
	    	    
	    	    String input = in.readLine();
	    		out.println();
	    		System.out.println(input);
        	
        		Set<String> keySet = onlineNames.keySet();

        	    int currentIndex = 0;
        	    for (String key : keySet) {
        	        if (key.equals(input)) {
        	            index = currentIndex; 
        	            break;
        	        }
        	        currentIndex++;
        	    }
        	    
        	    if (index != -1) {
        	    	validInput = true;
        	    }
        		
	    		Socket opponent = onlineSockets.get(index);
	    		System.out.println(opponent);
	    		if (opponent != null) {
		            onlineSockets.remove(index);
		                
		            clientOut = onlineNames.remove( onlineNames.keySet().iterator().next() );
		               
		                
		            clientOut.println("Oponente escolhido. A iniciar o jogo.");
		            out.println("Oponente escolhido. A iniciar o jogo.");    
		            Thread gameThread = new HandleConnectionThread(client, opponent);
		            gameThread.start();
		    	} else {
		    		validInput = false;
		    	}
            	
    		} while(!validInput);
    	}
    }

    private static void escolha3(Socket client, String username, PrintWriter out) {
    	Object lock = new Object();
    	synchronized (lock) {
    		onlineNames.put(username, out);
    		onlineSockets.add(client);
            out.println("Aguardando um oponente...");
    	}
    }
}

class HandleConnectionThread extends Thread {
	
	private Socket connection1 = null;
	private Socket connection2 = null;

    public HandleConnectionThread(Socket connection1, Socket connection2) {
		this.connection1 = connection1;
		this.connection2 = connection2;
	}
	
	/**
	 * Metodo de execucao
	 */
	public void run() {
		BufferedReader scX = null;
		PrintWriter osX = null;
		
		BufferedReader scO = null;
		PrintWriter osO = null;
		
		try {
			// INPUTS
			scX = new BufferedReader(new InputStreamReader(connection1.getInputStream()));
			scO = new BufferedReader(new InputStreamReader(connection2.getInputStream()));
			
			// OUTPUTS
			osX = new PrintWriter(connection1.getOutputStream(), true);
			osO = new PrintWriter(connection2.getOutputStream(), true);

			System.out.println("Thread " + this.getId() + ":");
			System.out.println("Jogador X: " + connection1);
			System.out.println("Jogador O: " + connection2);
			
			Jogo jogo = new Jogo();

			// CICLO DO JOGO
			for(;;) {
				
				jogo.mostrar();
				osX.println((jogo.JogoToTXT()+"\nJoga X:").replaceAll("\n", "\7"));
				
				String inputX1 = scX.readLine();
				String inputX2 = scX.readLine();
				if(inputX1==null || inputX2==null)
					break;
				

				System.out.println(connection1.getRemoteSocketAddress()+" -> Recebido do X: " + inputX1 + ", " + inputX2);

				
				jogo.joga(Integer.parseInt(inputX1), Integer.parseInt(inputX2), 'X');
				
				if (jogo.vencedor('X')) {
					String resp=(jogo.JogoToTXT()+"\nVitória do X!").replaceAll("\n", "\7");
					osX.println(resp); 
					osO.println(resp);
					System.out.println(resp.replaceAll("\7", "\n"));
					break;
				} 
				
				
				
				jogo.mostrar();
				osO.println((jogo.JogoToTXT()+"\nJoga O:").replaceAll("\n", "\7"));
				
				String inputO1 = scO.readLine();
				String inputO2 = scO.readLine();
				if(inputO1==null || inputO2==null)
					break;
				

				System.out.println(connection2.getRemoteSocketAddress()+" -> Recebido do O: " + inputO1 + ", " + inputO2);

				
				jogo.joga(Integer.parseInt(inputO1), Integer.parseInt(inputO2), 'O');
				
				if (jogo.vencedor('O')) {
					String resp=(jogo.JogoToTXT()+"\nVitória do O!").replaceAll("\n", "\7");
					osX.println(resp); 
					osO.println(resp);
					System.out.println(resp.replaceAll("\7", "\n"));
					break;
				} 

			}
			

		} catch(IOException e) {
			System.out.println("Ligacao encerrada.");
		} finally {
			// garantir que os sockets são fechados
			try {
				if (scX != null)
					scX.close();
				if (osX != null)
					osX.close();
				if (connection1 != null)
					connection1.close();
				if (scO != null)
					scO.close();
				if (osO != null)
					osO.close();
				if (connection2 != null)
					connection2.close();
				} catch (IOException e) {
			}
		}
		
		System.out.println("Thread terminada (servidor dedicado). " + this.getId());
	}
}