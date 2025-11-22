package server;

public class Jogo {
	
	public char[][] tabuleiro;

	public Jogo() {
		inicializarTabuleiro();
	}
	
	/**
	 * Inicializar o tabuleiro com uma grelha numerada e 4 peças no centro
	 */
	public void inicializarTabuleiro() {
		tabuleiro = new char[8][8];
		
		//Inicializar Tabuleiro com espaços vazios
		for (int l = 0; l < tabuleiro.length; l++) {
			for (int c = 0; c < tabuleiro[0].length; c++) {
				tabuleiro[l][c] = ' ';
			}
		}
		
		// Posicionar as pecas iniciais
		tabuleiro[3][3] = 'O';
		tabuleiro[3][4] = 'X';
		tabuleiro[4][3]	= 'X';
		tabuleiro[4][4] = 'O';
	}
	
	public String JogoToTXT() {
		String out = " |1|2|3|4|5|6|7|8\n +-+-+-+-+-+-+-+\n";
		for (int i = 0; i < 8; i++) {
			out = out + (i+1) + "|";
			for (int j = 0; j < 8; j++) {
				out = out + tabuleiro[i][j];
				if (j<7)
					out = out + "|";
			}
			out = out + "\n";
			if (i<7)
				out = out + " +-+-+-+-+-+-+-+\n";
		}
		return out;
	}
	
	public void mostrar() {
		System.out.println(JogoToTXT());
	}
	
	public static void main(String[] args) {
		System.out.println(32 > 32);
	}
	
	/**
	 * Verificar se o jogo está terminado ou não
	 * 
	 * @return true se o tabuleiro não tiver espaço livre, false se ainda der para jogar
	 */
	public boolean terminar() {
		for (int l = 0; l < tabuleiro.length; l++) {
			for (int c = 0; c < tabuleiro[0].length; c++) {
				if(tabuleiro[l][c] == ' ') {
					return false;
				}
			}
		}
		return true;
	}
	
	/**
	 * Verificar o vencedor da partida quando já não houver mais jogadas
	 * 
	 * @param simbolo Simbolo do jogador
	 * @return true se o jogador tiver mais de 32 pontos (Vitoria), false se não
	 */
	public boolean vencedor(char simbolo) {
		int pontos = 0;	
		if (terminar()==true) {
			for (int l = 0; l < tabuleiro.length; l++) {
				for (int c = 0; c < tabuleiro[0].length; c++) {
					if(tabuleiro[l][c] == simbolo) {
						pontos++;
		}}}}
		return pontos > 32;
	}
	
	/**
	 * Assinalar o simbolo na posicao certa, se for uma jogada valida
	 * 
	 * @param lin Linha da jogada, entre 1-8
	 * @param col Coluna da jogada, entre 1-8
	 * @param simbolo Simbolo da jogada pretendida
	 * @return true se a jogada for valida, false se não
	 */
	public boolean joga(int lin, int col, char simbolo) {
		// Converter as linhas e colunas para entradas da matriz
		lin = lin - 1;
		col = col - 1;
		
		// Se a jogada for valida coloca-se no tabuleiro e modifica-se o necessario
		if (validarMovimento(lin, col, simbolo)) {
			tabuleiro[lin][col] = simbolo;
			alterarPecas(lin, col, simbolo);
			return true;
		}
		return false;
	}
	
	private void alterarPecas(int lin, int col, char simbolo) {
		// determinar o simbolo do adversario
		char adv = (simbolo == 'X') ? 'O' : 'X';
				
		// Direcoes possiveis para verificar se a peça está adjacente a outra contraria
		// (-1,-1) (0,-1) (1,-1)
		// (-1, 0)   OX   (1, 0)
		// (-1, 1) (0, 1) (1, 1)
		int[][] direcoes = { {-1, -1}, {-1, 0}, 
					{-1,  1}, {0, -1}, 
					{0,   1}, {1, -1}, 
					{1,   0}, {1,  1} 
		};
		
		for (int[] direcao : direcoes) {
			// coordenadas novas
			int l = lin + direcao[0];
			int c = col + direcao[1];
			
			// igualmente ao metodo de validar o movimento, porem, quando encontramos 
			// as coordenadas de onde esta o simbolo da jogada, vamos virar as pecas todas
			// desde as coord. da peça colocada ate às novas
			if(l>=0 && l<8 && c>=0 && c<8 && tabuleiro[l][c] == adv) {
				while (l>=0 && l<8 && c>=0 && c<8 && tabuleiro[l][c] == adv) {
                    l += direcao[0];
                    c += direcao[1];
                }
				if (l>=0 && l<8 && c>=0 && c<8 && tabuleiro[l][c] == simbolo) {
					l -= direcao[0];
                    c -= direcao[1];
                    while (tabuleiro[l][c] == adv) {
                    	tabuleiro[l][c] = simbolo;
                        l -= direcao[0];
                        c -= direcao[1];
                    }
				}
			}
		}
	}
	
	public boolean validarMovimento(int lin, int col, char simbolo) {
		// determinar o simbolo do adversario
		char adv = (simbolo == 'X') ? 'O' : 'X';
		
		// Direcoes possiveis para verificar se a peça está adjacente a outra contraria
		// (-1,-1) (0,-1) (1,-1)
		// (-1, 0)   OX   (1, 0)
		// (-1, 1) (0, 1) (1, 1)
		int[][] direcoes = { {-1, -1}, {-1, 0}, 
				   {-1,  1}, {0, -1}, 
				   {0,   1}, {1, -1}, 
				   {1,   0}, {1,  1} 
		};
		
		for (int[] direcao : direcoes) {
			// posicoes em analise
			int l = lin + direcao[0];
			int c = col + direcao[1];
		
			// verificar se as novas coordenadas estao dentro do tabuleiro e se calham numa peca contraria
			if(l>=0 && l<8 && c>=0 && c<8 && tabuleiro[l][c] == adv) {
				while (l>=0 && l<8 && c>=0 && c<8 && tabuleiro[l][c] == adv) {
                    l += direcao[0];
                    c += direcao[1];
                }
				// depois de movermos as coordenadas ate nao haver um adversario na mesma
				// o fim do loop significa que algo esta nas coordenadas (simbolo ou nada)
				// se for o simbolo da jogada, a jogada é valida
                if (l>=0 && l<8 && c>=0 && c<8 && tabuleiro[l][c] == simbolo)
                    return true;
			}
		}
		return false;
	}
}
