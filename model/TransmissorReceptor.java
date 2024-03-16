/* ***************************************************************
* Autor............: Carlos Gil Martins da Silva
* Matricula........: 202110261
* Inicio...........: 28/11/2023
* Ultima alteracao.: 04/12/2023
* Nome.............: TransmissorReceptor
* Funcao...........: Tem todas os metodos envolvidos de transmissao
e de recepcao, faz tudo, de uma ponta outra, incluindo os metodos
de Aloha e cmsa estao presentes Nessa classe, realiza tudo 
que eh necessario
****************************************************************/

package model;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import control.controllerPrincipal;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class TransmissorReceptor extends Thread {

    private final int id; // Id do Filósofo
	public final ImageView transmitindo;
    public final String msg;

    public TransmissorReceptor(int id, ImageView transmissor, String mensagem) { // Construtor da Classe com Identificador
      this.id = id;
	this.transmitindo = transmissor;
      this.msg = mensagem;
    }

	public long getId() {
		return id;
	}
	int i = 0;
  
    controllerPrincipal cT = new controllerPrincipal(); // Instanciando e Criando o Controller
    // Metodo Utilizado para Setar um Controlador em Comum em Todas Thread
    public void setControlador(controllerPrincipal controle) {
      this.cT = controle;
    }


    @Override
    public void run() {
      AplicacaoTransmissora(msg);

    }

		///////////////////////////////////////////////////////////////////
		// METODOS DE TRANSMISSAO ABAIXO, TODOS INCLUINDO TODAS CAMADAS  //
		///////////////////////////////////////////////////////////////////
    //Camadas de Aplicacao
    public void AplicacaoTransmissora(String mensagem) {
		CamadaDeAplicacaoTransmissora(mensagem);
	}// fim do metodo AplicacaoTransmissora

	void CamadaDeAplicacaoTransmissora(String mensagem) {
		char[] msg = mensagem.toCharArray(); // transforma a mensagem em um Array de Char
		setNumCaracteres(msg.length); //seta o numero de caracteres no controller
		setTamanhoMsgTotal(msg.length);
		setInicialCaracteres(msg.length);
		int indexQuadro = 0; // Index do Array
		int deslocBit = 0; // Variavel define qual bit vai deslocar para manipulacao (Inicia do 7 pois eh o ultimo Bit de um Caractere)
		// Estrutura Para Definir o Tamanho do Array
		int[] quadro = new int[setTamanhoArray(cT.getCodificacao())]; // Criacao do Array de Quadros
		// For ate o tamanho da Mensagem
		for (int i = 0; i < getNumCaracteres(); i++) {
			deslocBit = 7 *(i+1) + i; // Posicao do bit comparativo (Inicia do 7 pois eh o ultimo Bit de um Caractere) para os bits nao inverterem
			if(i % 4 == 0 && i != 0){ // Aumenta o indice do Array quando tiver mais de 4 letras
				indexQuadro++;
			}
			String aux = charParaBinario(msg[i]); // String auxiliar para transformar em binario
			if(id == 1){
				String DadosTerminal = "Caractere: " + msg[i] + " || Em Binario: " + aux;
				cT.exibeDados(DadosTerminal);
			}
			for (int j = 0; j < 8; j++) { // For para cada Bit
				// Estrutura de IF que manipula bit por Bit
				if(aux.charAt(j) =='1'){
					// define o bit na posicaoo deslocBit do quadro[indexQuadro] como 1, mantendo os outros bits inalterados.
					quadro[indexQuadro] = quadro[indexQuadro] | (1 << deslocBit);
				}
			deslocBit--;
			}
		}
		CamadaEnlaceDadosTransmissora(quadro); // Chama a Primeira Camada;
		qtdVezesAparece++;
	}// fim do metodo CamadaDeAplicacaoTransmissora
    // Fim Cmadas de Aplicacao
    // Inicio Camadas Enlace

       public void CamadaEnlaceDadosTransmissora(int quadro[]) {
        int quadroEnquadrado[] = CamadaEnlaceDadosTransmissoraEnquadramento(quadro);
        int quadroControleERRO[][] = CamadaEnlaceDadosTransmissoraControleDeErro(quadroEnquadrado);
        CamadaEnlaceDadosTransmissoraControleDeFluxo(quadroControleERRO);
       }// fim do metodo CamadaEnlaceDadosTransmissora
     
       public int[] CamadaEnlaceDadosTransmissoraEnquadramento(int quadro[]) {
         int tipoDeEnquadramento = cT.getEnquadramento(); // alterar de acordo com o teste
         caracteresAnterior = getNumCaracteres();
     
         setNumCaracteresEnquadramento(cT.getEnquadramento(), quadro); // Novo Numero de Caracteres
         int quadroEnquadrado[] = new int[setTamanhoArray(cT.getCodificacao())];
     
         switch (tipoDeEnquadramento) {
           case 0: // contagem de caracteres
             quadroEnquadrado = CamadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres(quadro);
             break;
           case 1: // insercao de bytes
             //quadroEnquadrado = CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBytes(quadro);
             break;
           case 2: // insercao de bits
             //quadroEnquadrado = CamadaEnlaceDadosTransmissoraEnquadramentoInsercaoDeBits(quadro);
             break;
           case 3: // violacao da camada fisica
             //quadroEnquadrado = CamadaEnlaceDadosTransmissoraEnquadramentoViolacaoDaCamadaFisica(quadro);
             break;
         }// fim do switch/case
         return quadroEnquadrado;
       }// fim do metodo CamadaEnlaceTransmissoraEnquadramento

			 public int[] CamadaEnlaceDadosTransmissoraEnquadramentoContagemDeCaracteres(int quadro[]) {

				// Criando Novo quadro com o novo tamanho
				int quadroEnquadrado[] = new int[setTamanhoArray(cT.getCodificacao())]; // Novo Array Quadro
		
				int controllerBits = 0; // Controlador Deslocamento de Bits Array
				int controllerBitsENQUADRAMENTO = 0; // Controlador Deslocamento de Bits Array Enquadramento
				int deslocBit = 0; // Variavel define qual bit vai deslocar para manipulacaoo (Inicia do 7 pois eh o ultimo Bit de um Caractere)
				int deslocBitENQUAD = 0; // Variavel define qual bit vai deslocar para manipulacaoo (Inicia do 7 pois eh o ultimo Bit de um Caractere)
				int indexQuadro = 0; // Index do Array ENQUADRADO
				
				// For até o tamanho da Mensagem
				for (int i = 0; i < caracteresAnterior; i++) {
					if (i % 3 == 0 && i != 0) { // Aumenta o indice do Array Enquadrado quando tiver mais de 3 carac (pois com o controle fica 4 carac)
						indexQuadro++;
					}
					deslocBitENQUAD = setDeslocamentoBIT(controllerBitsENQUADRAMENTO, deslocBitENQUAD);
					deslocBit = setDeslocamentoBIT(controllerBits, deslocBit);
		
					if (i % 3 == 0) { // Insere o byte de Controle (Contagem de Caractere)
						int enquad = Math.min(3, caracteresAnterior - i); // Pega o Tamanho do Quadro
						char intChar = Integer.toString(enquad).charAt(0); // Int para Char
						String aux = charParaBinario(intChar); // String com os Binarios
		
						for (int j = 0; j < 8; j++) { // For para cada Bit
							// Estrutura de IF que manipula bit por Bit
							if (aux.charAt(j) == '1') {
								// define o bit na posicaoo deslocBit do quadro[indexQuadro] como 1, mantendo os outros bits inalterados.
								quadroEnquadrado[indexQuadro] = quadroEnquadrado[indexQuadro] | (1 << deslocBitENQUAD);
							}
							deslocBitENQUAD--;
						}
						controllerBitsENQUADRAMENTO++;
						deslocBitENQUAD = setDeslocamentoBIT(controllerBitsENQUADRAMENTO, deslocBitENQUAD);
		
					} // Fim If Controle do Enquadramento (Contagem de Caracteres)
		
					for (int j = 0; j < 8; j++) { // For para cada caractere
						int mascara = 1 << deslocBit;
						int Bit = (quadro[i / 4] & mascara) >> deslocBit; // Pega o Bit na posicao da Mascara&Quadro na posicao deslocBit
						// Estrutura de IF que manipula bit por Bit
						if (Bit == 1 || Bit == -1) {
							quadroEnquadrado[indexQuadro] = quadroEnquadrado[indexQuadro] | (1 << deslocBitENQUAD);
						}
						deslocBit--;
						deslocBitENQUAD--;
					} // Fim For Caractere
					controllerBits++;
					controllerBitsENQUADRAMENTO++;
		
				} // Fim For Mensagem Completa
				return quadroEnquadrado;
			}// fim do metodo CamadaEnlaceDadosTransmissoraContagemDeCaracteres


			public int[][] CamadaEnlaceDadosTransmissoraControleDeErro(int quadro[]) {
				int tipoDeControleDeErro = cT.getControleErro(); // alterar de acordo com o teste
				TamanhoQuadro = setNumBITScontroleErro(tipoDeControleDeErro);
				int[][] quadroControleErro;
				switch (tipoDeControleDeErro) {
					case 0: // bit de paridade par
						quadroControleErro = CamadaEnlaceDadosTransmissoraControleDeErroBitParidadePar(quadro);
						break;
					case 1: // bit de paridade impar
						quadroControleErro = CamadaEnlaceDadosTransmissoraControleDeErroBitParidadeImpar(quadro);
						break;
					case 2: // CRC
						quadroControleErro = CamadaEnlaceDadosTransmissoraControleDeErroCRC(quadro);
						break;
					case 3: // codigo de Hamming
						quadroControleErro = CamadaEnlaceDadosTransmissoraControleDeErroCodigoDeHamming(quadro);
						// codigo
						break;
					default:
						quadroControleErro = null;
						break;
				}// fim do switch/case
				setBooleanACK(quadroControleErro.length);
				return quadroControleErro;
			}// fim do metodo CamadaEnlaceDadosTransmissoraControleDeErro

			int[][] CamadaEnlaceDadosTransmissoraControleDeErroBitParidadePar(int quadro[]) {
				StringBuilder Temporaria = new StringBuilder();
				// quadro com cada enquadramento separado manipulado por bits
				int[][] enquadrosSeparados = new int[getQtdEnquadros()][];
		
				// quadro q armazena a qnt de bits de cada enquadramento
				int[] qtdBitsControleFluxo = new int[getQtdEnquadros()];
				
				int[] quadroParcial = new int[2]; // quadro parcial
				StringBuilder str = new StringBuilder(ExibirBinarioControleErro(quadro)); // mensagem completa
				int forInterno = 0;
				int positionBit = 0;
				int indexQuadro = 0;
				int countsBits1 = 0;
				// Percorre toda mensagem separando cada enquadramento e inserindo a informacao de controle
				// em cada enquadramento separadamente para enviar para proxima camada
				for (int i = str.length() - 1; i >= 0; i--) {
					positionBit = i;
					if(i >=31){
						forInterno = 31;
					}else{
						forInterno = i;
					}
		
					for(int j = 0; j <= forInterno; j++){
						if (str.toString().charAt(positionBit) == '1') {
						countsBits1++;
						quadroParcial[0] = quadroParcial[0] | (1 << j);
						}
					positionBit--;
					}
					int bitQuadro = (forInterno+1) % 32;
						if (countsBits1 % 2 != 0) { // Se nao tiver Quantidade Par de Bits, Insere um Bit 1 no ultimo Bit da
							quadroParcial[quadroParcial.length-1] = quadroParcial[quadroParcial.length-1]| (1 << bitQuadro);
						}
						enquadrosSeparados[indexQuadro] = quadroParcial;
						countsBits1 = 0;
						qtdBitsControleFluxo[indexQuadro] = forInterno+2;
						System.out.println(("Quadro[" + indexQuadro + "]: " + ExibirBinarioControleFluxo(enquadrosSeparados[indexQuadro], qtdBitsControleFluxo[indexQuadro])));
						String aux = ("Quadro[" + indexQuadro + "]: " + ExibirBinarioControleFluxo(enquadrosSeparados[indexQuadro], qtdBitsControleFluxo[indexQuadro]));
						Temporaria.append(aux).append("\n");
						indexQuadro++;
						i = i - forInterno;
						if (i < 32){
							quadroParcial = new int[1];
						}else{
							quadroParcial = new int[2];
						}
					}
					setQtdBitsControleFluxo(qtdBitsControleFluxo);
					System.out.println("<----------------------------------------------------------------------->");
				return enquadrosSeparados;
			}// fim do metodo CamadaEnlaceDadosTransmissoraControledeErroBitParidadePar

			int[][] CamadaEnlaceDadosTransmissoraControleDeErroBitParidadeImpar(int quadro[]) {
				StringBuilder Temporaria = new StringBuilder();
						// quadro com cada enquadramento separado manipulado por bits
				int[][] enquadrosSeparados = new int[getQtdEnquadros()][];
		
				// quadro q armazena a qnt de bits de cada enquadramento
				int[] qtdBitsControleFluxo = new int[getQtdEnquadros()];
				
				int[] quadroParcial = new int[2]; // quadro parcial
				StringBuilder str = new StringBuilder(ExibirBinarioControleErro(quadro)); // mensagem completa
				int forInterno = 0;
				int positionBit = 0;
				int indexQuadro = 0;
				int countsBits1 = 0;
				// Percorre toda mensagem separando cada enquadramento e inserindo a informacao de controle
				// em cada enquadramento separadamente para enviar para proxima camada
				for (int i = str.length() - 1; i >= 0; i--) {
					positionBit = i;
					if(i >=31){
						forInterno = 31;
					}else{
						forInterno = i;
					}
		
					for(int j = 0; j <= forInterno; j++){
						if (str.toString().charAt(positionBit) == '1') {
						countsBits1++;
						quadroParcial[0] = quadroParcial[0] | (1 << j);
						}
					positionBit--;
					}
					int bitQuadro = (forInterno+1) % 32;
						if (countsBits1 % 2 == 0) { // Se nao tiver Quantidade Par de Bits, Insere um Bit 1 no ultimo Bit da
							quadroParcial[quadroParcial.length-1] = quadroParcial[quadroParcial.length-1]| (1 << bitQuadro);
						}
						enquadrosSeparados[indexQuadro] = quadroParcial;
						countsBits1 = 0;
						qtdBitsControleFluxo[indexQuadro] = forInterno+2;
						System.out.println("Quadro[" + indexQuadro + "]: " + ExibirBinarioControleFluxo(enquadrosSeparados[indexQuadro], qtdBitsControleFluxo[indexQuadro]));
						String aux = ("Quadro[" + indexQuadro + "]: " + ExibirBinarioControleFluxo(enquadrosSeparados[indexQuadro], qtdBitsControleFluxo[indexQuadro]));
						Temporaria.append(aux).append("\n");
						indexQuadro++;
						i = i - forInterno;
						if (i < 32){
							quadroParcial = new int[1];
						}else{
							quadroParcial = new int[2];
						}
					}
					setQtdBitsControleFluxo(qtdBitsControleFluxo);
					//cG.setBinaryArea(Temporaria.toString());
								System.out.println("<----------------------------------------------------------------------->");
				return enquadrosSeparados;
			}// fim do metodo CamadaEnlaceDadosTransmissoraControledeErroBitParidadeImpar

			int[][] CamadaEnlaceDadosTransmissoraControleDeErroCRC(int quadro[]) {
				StringBuilder Temporaria = new StringBuilder();
				/*
				 * EXEMPLO DE FUNCIONAMENTO DO CRC, COM UM DE 4 BITS
				 * 11010011101100 000 <--- entrada deslocada para a direita com 3 zeros
				 * 1011 <--- divisor (4 bits) = x³ + x + 1
				 * ------------------
				 * 01100011101100 000 <--- resultado
				 */
		
				// Polinomio CRC-32 = x32, x26, x23, x22, x16, x12, x11, x10, x8, x7, x5, x4,
				// x2, x1 + 1
				// Logo um Polinomio de 33 bits com 32 bits de resto, assim deslocamos a
				// mensagem
				// 32 Bits 0 A direita e dividos o crc e inserimos nesse lugar
		
				// quadro com cada enquadramento separado manipulado por bits
				int[][] enquadrosSeparados = new int[getQtdEnquadros()][];
		
				// quadro q armazena a qnt de bits de cada enquadramento
				int[] qtdBitsControleFluxo = new int[getQtdEnquadros()];
				
				int[] quadroParcial = new int[2]; // quadro parcial
				StringBuilder str = new StringBuilder(ExibirBinarioControleErro(quadro)); // mensagem completa
				String PolinomioCRC32 = "100000100110000010001110110110111";
				// 32 Bits 0 a serem inseridos no final da mensagem para calcular o crc
				String zerosInseridos = "00000000000000000000000000000000";
		
				int forInterno = 0;
				int positionBit = 0;
				int indexQuadro = 0;
		
				// Percorre toda mensagem separando cada enquadramento e inserindo a informacao de controle
				// em cada enquadramento separadamente para enviar para proxima camada
				for (int i = str.length() - 1; i >= 0; i--) {
					StringBuilder stringQuadro = new StringBuilder();
					positionBit = i;
					if(i >=31){
						forInterno = 31;
					}else{
						forInterno = i;
					}
					// Percorre apenas o tamanho definiddo do quadro
					//Concatenando os 0 e 1 na string
					for(int j = 0; j <= forInterno; j++){
						if (str.toString().charAt(positionBit) == '1') {
							stringQuadro.insert(0,'1');
						}
						else{
							stringQuadro.insert(0,'0');
						}
					positionBit--;
					}
					// Insere os 32 bits 0 e calcula o crc e insere na string
					String aux = stringQuadro.toString();
					aux = aux + zerosInseridos;
					String Resto = divisaoBinariaResto(aux, PolinomioCRC32);
					stringQuadro.append(Resto);
		
					//Passa a string para o array de int manipulando os bits
					positionBit = stringQuadro.length()-1;
					for(int j = 0; j <= stringQuadro.length()-1; j++){
						if (stringQuadro.toString().charAt(positionBit) == '1') {
						quadroParcial[j/32] = quadroParcial[j/32] | (1 << j);
						}
					positionBit--;
					}
					// o quadro de quadros recebe o quadro na posicao
						enquadrosSeparados[indexQuadro] = quadroParcial;
						qtdBitsControleFluxo[indexQuadro] = stringQuadro.length();
						if(id==1){
							String DadosTerminal = "Quadro[" + indexQuadro + "]: " + ExibirBinarioControleFluxo(enquadrosSeparados[indexQuadro], qtdBitsControleFluxo[indexQuadro]);
							cT.exibeDados(DadosTerminal);
						}
						String aux2 = ("Quadro[" + indexQuadro + "]: " + ExibirBinarioControleFluxo(enquadrosSeparados[indexQuadro], qtdBitsControleFluxo[indexQuadro]));
						Temporaria.append(aux2).append("\n");
						indexQuadro++;
						i = i - forInterno;
						quadroParcial = new int[2]; // tamanho do novo quadro
					}
					setQtdBitsControleFluxo(qtdBitsControleFluxo);
					if(id == 1){
						String DadosTerminal2 = "<----------------------------------------------------------------------->";
						cT.exibeDados(DadosTerminal2);
					}
					//cG.setBinaryArea(Temporaria.toString());
				qtdVezesAparece2++;
				return enquadrosSeparados;
			}// fim do metodo CamadaEnlaceDadosTransmissoraControledeErroCRC

			int[][] CamadaEnlaceDadosTransmissoraControleDeErroCodigoDeHamming(int quadro[]) {
				StringBuilder Temporaria = new StringBuilder();
						// quadro com cada enquadramento separado manipulado por bits
				int[][] enquadrosSeparados = new int[getQtdEnquadros()][];
				// quadro q armazena a qnt de bits de cada enquadramento
				int[] qtdBitsControleFluxo = new int[getQtdEnquadros()];
				StringBuilder str = new StringBuilder(ExibirBinarioControleErro(quadro)); // mensagem completa
		
				int forInterno = 0;
				int positionBit = 0;
				int indexQuadro = 0;
		
				// Percorre toda mensagem separando cada enquadramento e inserindo a informacao de controle
				// em cada enquadramento separadamente para enviar para proxima camada
				for (int i = str.length() - 1; i >= 0; i--) {
					StringBuilder stringQuadro = new StringBuilder();
					positionBit = i;
					if(i >=31){
						forInterno = 31;
					}else{
						forInterno = i;
					}
					// Percorre apenas o tamanho definiddo do quadro
					//Concatenando os 0 e 1 na string
					for(int j = 0; j <= forInterno; j++){
						if (str.toString().charAt(positionBit) == '1') {
							stringQuadro.insert(0,'1');
						}
						else{
							stringQuadro.insert(0,'0');
						}
					positionBit--;
					}
					// Insere os 32 bits 0 e calcula o crc e insere na string
					StringBuilder mensagemCodificada = new StringBuilder(codificarHamming(stringQuadro.toString()));
					int[] quadroParcial;
					if(mensagemCodificada.length() % 32 == 0){
						quadroParcial = new int[(mensagemCodificada.length()/32)];
					}
					else{
						quadroParcial = new int[((mensagemCodificada.length()/32)+1)];
					}
					//Passa a string para o array de int manipulando os bits
					positionBit = mensagemCodificada.length()-1;
					for(int j = 0; j <= mensagemCodificada.length()-1; j++){
						if (mensagemCodificada.toString().charAt(positionBit) == '1') {
						quadroParcial[j/32] = quadroParcial[j/32] | (1 << j);
						}
					positionBit--;
					}
					// o quadro de quadros recebe o quadro na posicao
						enquadrosSeparados[indexQuadro] = quadroParcial;
						qtdBitsControleFluxo[indexQuadro] = mensagemCodificada.length();
						if(id == 1){
							String DadosTerminal = "Quadro[" + indexQuadro + "]: " + ExibirBinarioControleFluxo(enquadrosSeparados[indexQuadro], qtdBitsControleFluxo[indexQuadro]);
							cT.exibeDados(DadosTerminal);
						}
						 String aux2 = ("Quadro[" + indexQuadro + "]: " + ExibirBinarioControleFluxo(enquadrosSeparados[indexQuadro], qtdBitsControleFluxo[indexQuadro]));
						Temporaria.append(aux2).append("\n");
						indexQuadro++; //aumenta o index do enquadrosSeparados
						i = i - forInterno; // diminui o valor de iteracao do I do valor do for interno que eh a (qtd de bits do quadro)
					}
					setQtdBitsControleFluxo(qtdBitsControleFluxo);
					if(id == 1){
						String DadosTerminal2 = "<----------------------------------------------------------------------->";
						cT.exibeDados(DadosTerminal2);
					}
				qtdVezesAparece2++;
				return enquadrosSeparados;
			}// fim do metodo CamadaEnlaceDadosTransmissoraControleDeErroCodigoDehamming

			public void CamadaEnlaceDadosTransmissoraControleDeFluxo(int quadro[][]) {
        int tipoDeControleDeFluxo = cT.getControleFluxo(); // alterar de acordo com o teste
        switch (tipoDeControleDeFluxo) {
            case 0: // protocolo de janela deslizante de 1 bit
                CamadaEnlaceDadosTransmissoraJanelaDeslizanteUmBit(quadro);
                break;
            case 1: // protocolo de janela deslizante go-back-n
                //CamadaEnlaceDadosTransmissoraJanelaDeslizanteGoBackN(quadro);
                break;
            case 2: // protocolo de janela deslizante com retransmissão seletiva
                CamadaEnlaceDadosTransmissoraJanelaDeslizanteComRetransmissaoSeletiva(quadro);
                break;
        }// fim do switch/case
    }// fim do metodo CamadaEnlaceDadosTransmissoraControleDeFluxo

		  public void CamadaEnlaceDadosTransmissoraJanelaDeslizanteUmBit(int quadro[][]) {
      ContAcks = 0;
      // Setando o Timer Com base no Tipo de Codificacao (Manchester Dobro da
      // mensagem)
      // Temporizador com Maior Folga
      if (cT.getCodificacao() != 0) {
        setMiliTemporizador(2500);
      } else {
        setMiliTemporizador(2500);
      }

      new Thread(() -> { // Abrindo as Threads
        int[] qtdBits = getQtdBitsControleFluxo();
        // Recebendo o Quadro e Colocando a Informacao de Controle
        for (int i = 0; i < quadro.length; i++) {
          int[] arrayQuadro = new int[(quadro[i].length + 1)];
          for (int j = 0; j < quadro[i].length; j++) {
            arrayQuadro[j] = quadro[i][j];
          }

          arrayQuadro[quadro[i].length] = alternaACK(i);
          setQtdBitsInsercaoBits(qtdBits[i]);

          Platform.runLater(() -> {
            CamadaAcessoAoMeioTransmissora(arrayQuadro);
          });

          try {
            Thread.sleep(getMiliTemporizador()); // TEMPO DE ESPERA
          } catch (InterruptedException e) {
          }
          while (confirmacaoEnvioProximoQuadro) {
            try {
              sleep(300);
            } catch (InterruptedException e) {
            }
          }
          confirmacaoEnvioProximoQuadro = true;
          // Caso o ACK nao tenha Chegado, entra aqui infinitamente, ate chegar
          while (getBooleanACK(i) != arrayQuadro[quadro[i].length]) {
            setQtdBitsInsercaoBits(qtdBits[i]);
            CamadaAcessoAoMeioTransmissora(arrayQuadro);
            try {
              Thread.sleep(getMiliTemporizador()); // TEMPO DE ESPERA
            } catch (InterruptedException e) {
            }
          }
          if (i != quadro.length - 1) {
            // System.out.println("\n||----------------> Quadro Enviado com Sucesso,
            // partindo para o proximo! <----------------||");
          }

        }
        // cG.disableButtons();
        ContAcks = 0;
      }).start(); // Fim thread
    }// fim do metodo CamadaEnlaceDadosTransmissoraJanelaDeslizanteUmBit

    public void CamadaEnlaceDadosTransmissoraJanelaDeslizanteComRetransmissaoSeletiva(int quadro[][]) {
      ContAcks = 0;
      // Setando o Timer Com base no Tipo de Codificacao (Manchester Dobro da
      // mensagem)
      // Temporizador com Maior Folga
      if (cT.getCodificacao() != 0) {
        setMiliTemporizador(2500);
      } else {
        setMiliTemporizador(2500);
      }

      int[] qtdBits = getQtdBitsControleFluxo();
      Thread[] threads = new Thread[quadro.length];

      setTamanhoEnquadros(quadro.length);
      setTamanhoFluxoRetransmissaoSeletiva(quadro.length);
      // Recebendo o Quadro e Colocando a Informacao de Controle
      for (int i = 0; i < quadro.length; i++) {
        final int indexExterno = i;
        int[] arrayQuadro = new int[(quadro[i].length + 1)];
        for (int j = 0; j < quadro[i].length; j++) {
          arrayQuadro[j] = quadro[i][j];
        }
        threads[i] = new Thread(() -> { // Criando as Threads Todas Juntas

          setTamanhoJanela(getTamanhoJanela() + 1); // Setando a Janela Deslizante
          // System.out.println("Quadro ["+ indexExterno+"] Entrou na Janela");

          arrayQuadro[quadro[indexExterno].length] = indexExterno;
          setQtdBitsInsercaoBits(qtdBits[indexExterno]);

          Platform.runLater(() -> {
            CamadaAcessoAoMeioTransmissora(arrayQuadro);
          });
          try {
            Thread.sleep(getMiliTemporizador()); // TEMPO DE ESPERA
          } catch (InterruptedException e) {
          }

          // Caso o ACK nao tenha Chegado, entra aqui infinitamente, ate chegar
          while (getBooleanACK(indexExterno) != arrayQuadro[quadro[indexExterno].length]) {
            setQtdBitsInsercaoBits(qtdBits[indexExterno]);
            Platform.runLater(() -> {
              CamadaAcessoAoMeioTransmissora(arrayQuadro);
            });
            try {
              Thread.sleep(getMiliTemporizador()); // TEMPO DE ESPERA
            } catch (InterruptedException e) {
            }
          }
          // System.out.println("||----------------> Quadro ["+ indexExterno+"] Recebido
          // com Sucesso <----------------||\n");
        });
      }
      new Thread(() -> {
        for (int i = 0; i < threads.length; i++) {
          while (getTamanhoJanela() == 4) {
            // Esperando para entrar no espaco da janela Deslizante de 4 quados por Vez
            try {
              Thread.sleep(200);
            } catch (InterruptedException e) {
            }
          }
          threads[i].start();
          try {
            while (confirmacaoEnvioProximoQuadro) {
              Thread.sleep(200);
            }
            confirmacaoEnvioProximoQuadro = true;
          } catch (InterruptedException e) {
          }
        }
      }).start();
    }// fim do CamadaEnlaceDadosTransmissoraJanelaDeslizanteComRetransmissaoSeletiva

	void CamadaAcessoAoMeioTransmissora(int quadro[]) {
		int tipoDeAcessoAoMeio = cT.getControleDeAcessoAoMeio(); // alterar de acordo com o teste
		switch (tipoDeAcessoAoMeio) {
			case 0:
				CamadaAcessoAoMeioTransmissoraAlohaPuro(quadro);
				break;
			case 1:
				CamadaAcessoAoMeioTransmissoraSlottedAloha(quadro);
				break;
			case 2:
				CamadaAcessoAoMeioTransmissoraCsmaNaoPersistente(quadro);
				break;
			case 3: 
				CamadaAcessoAoMeioTransmissoraCsmaPersistente(quadro);
				break;
			case 4: 
				CamadaDeAcessoAoMeioTransmissoraCsmaPPersistente(quadro);
				break;
			case 5:
				CamadaDeAcessoAoMeioTransmissoraCsmaCD(quadro);
				break;
		}// fim do switch/case
	}// fim do metodo CamadaAcessoAoMeioTransmisora

    void CamadaAcessoAoMeioTransmissoraAlohaPuro(int quadro[]) {
      new Thread(() -> {
        System.out.println("Transmissor [" + getId() + "] esta Transmitindo");
		setTransmitindoTrue(); // Seta imagem de Transmitindo True
        CamadaFisicaTransmissora(quadro); // Envia o Quadro
        try {
          sleep(1000); // Aguarda Confirmacao
        } catch (InterruptedException e) {
        }

        while (!confirmationAloha) { // Entrar aqui se n chegar
          try {
            sleep(timeAleatorioALOHA()); // Espera um tempo aleatorio
          } catch (InterruptedException e) {
          }
          System.out.println("Confirmacao nao chegou DEVIDO A COLISOES, Transmissor [" + getId() + "] esta reenviando");
          CamadaFisicaTransmissora(quadro); // Envia o Quadro
		  setTransmitindoTrue(); // Seta imagem de Transmitindo True
          try {
            sleep(1000); // Aguarda Confirmacao
          } catch (InterruptedException e) {
          }
        }
        confirmationAloha = false;
      }).start();
    }// fim do metodo CamadaAcessoAoMeioTransmisoraAlohaPuro

	void CamadaAcessoAoMeioTransmissoraSlottedAloha(int quadro[]) {
    new Thread(() -> {
      long Slot = ((System.currentTimeMillis()%1000)%1000);
      if(Slot == 0){ //Se entrar no Slot de tempo correto
        System.out.println("Transmissor [" + getId() + "] esta Transmitindo");
		    setTransmitindoTrue(); // Seta imagem de Transmitindo True
        CamadaFisicaTransmissora(quadro); // Envia o Quadro

        try {
          sleep(1000); // Aguarda Confirmacao
        } catch (InterruptedException e) {
        }

        while (!confirmationAloha) { // Entrar aqui
          try {
          sleep(timeSinc()); // Aguarda Confirmacao
          } catch (InterruptedException e) {
          }
          long Slot2 = ((System.currentTimeMillis()%1000)%1000); // Esperando Proximo Slot de tempo
          if(Slot2 == 0){
            System.out.println("Confirmacao nao chegou DEVIDO A COLISOES, Transmissor [" + getId() + "] esta reenviando");
            setTransmitindoTrue(); // Seta imagem de Transmitindo True
            CamadaFisicaTransmissora(quadro); // Envia o Quadro
            try {
              sleep(1000); // Aguarda Confirmacao
            } catch (InterruptedException e) {
            }
          }
        }
        confirmationAloha = false;
      } // Fim slot Correto
      else{ // Nao conseguiu entrar pois nao pegou a entrada do slot
        long Slot2 = ((System.currentTimeMillis()%1000)%1000); // Esperando Proximo Slot de tempo
          while(Slot2 != 0){ // Aguardando ate a proximo slot de transmissao
            Slot2 = ((System.currentTimeMillis()%1000)%1000);
          }
        System.out.println("Transmissor [" + getId() + "] esta Transmitindo");
		    setTransmitindoTrue(); // Seta imagem de Transmitindo True
        CamadaFisicaTransmissora(quadro); // Envia o Quadro

        try {
          sleep(1000); // Aguarda Confirmacao
        } catch (InterruptedException e) {
        }

          while (!confirmationAloha) { // Entrar aqui
          try {
            sleep(timeSinc()); // Aguarda Confirmacao
          } catch (InterruptedException e) {
          }
          long SlotAux = ((System.currentTimeMillis()%1000)%1000); // Esperando Proximo Slot de tempo
          if(SlotAux == 0){
            System.out.println("Confirmacao nao chegou DEVIDO A COLISOES, Transmissor [" + getId() + "] esta reenviando");
            setTransmitindoTrue(); // Seta imagem de Transmitindo True
            CamadaFisicaTransmissora(quadro); // Envia o Quadro
            try {
              sleep(1000); // Aguarda Confirmacao
            } catch (InterruptedException e) {
            }
          }
        }
        confirmationAloha = false;
      }
      }).start();
	}// fim do metodo CamadaAcessoAoMeioTransmisoraSlottedAloha

	/*CSMA NAO PERSISTENTE Antes de transmitir,
uma estacao escuta o canal. Se ninguem mais estiver transmitindo, a estacao iniciara a
transmissao. No entanto, se o canal ja estiver sendo utilizado, a estação nao permanecera
escutando continuamente a fim de se apoderar de imediato do canal apos detectar o fim da
transmissao anterior. Em vez disso, a estacao aguardara durante um intervalo de tempo aleatorio
e, em seguida, repetira o algoritmo. */

	void CamadaAcessoAoMeioTransmissoraCsmaNaoPersistente(int quadro[]) {
		new Thread(() -> {
			if (cT.getEscutaMeioTransmissao()) { // Meio de transmissao liberado para transmitir
				cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
				System.out.println("Transmissor [" + getId() + "] esta Transmitindo");
				setTransmitindoTrue(); // Seta imagem de Transmitindo True
				CamadaFisicaTransmissora(quadro); // Envia o Quadro

				try {
					sleep(1000); // Aguarda Confirmacao
				} catch (InterruptedException e) {
				}

				while (!confirmationPersistente) { // Entrar aqui se n chegar confirmacao
					try {
						sleep(timeAleatorioNaoPersist()); // Aguarda tempo aleatorio para verificar o Meio de Transmissao
					} catch (InterruptedException e) {
					}
					if (cT.getEscutaMeioTransmissao()) { // Verifica se esta livre para transmitir
						cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
						System.out.println("Confirmacao nao chegou DEVIDO A COLISOES, Transmissor [" + getId() + "] esta reenviando");
						setTransmitindoTrue(); // Seta imagem de Transmitindo True
						CamadaFisicaTransmissora(quadro); // Envia o Quadro
						try {
							sleep(1000); // Aguarda Confirmacao, se nao chegar, repete o processo
						} catch (InterruptedException e) {
						}
					} // Caso n esteja livre, repete o processo
				}
				confirmationPersistente = false;
			} else { // Meio BLOQUEADO INICIALMENTE
				try {
					sleep(timeAleatorioNaoPersist()); // Aguarda tempo aleatorio para verificar o Meio de Transmissao
				} catch (InterruptedException e) {
				}
				while (!cT.getEscutaMeioTransmissao()) { // Entra se o meio estiver bloqueado ainda
					try {
						sleep(timeAleatorioNaoPersist()); // Aguarda tempo aleatorio para verificar o Meio de Transmissao
					} catch (InterruptedException e) {
					}
				}
				// Chega aqui quando o meio Liberar
				cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
				System.out.println("Transmissor [" + getId() + "] esta Transmitindo");
				setTransmitindoTrue(); // Seta imagem de Transmitindo True
				CamadaFisicaTransmissora(quadro); // Envia o Quadro

				try {
					sleep(1000); // Aguarda Confirmacao
				} catch (InterruptedException e) {
				}

				while (!confirmationPersistente) { // Entrar aqui se n chegar confirmacao
					try {
						sleep(timeAleatorioNaoPersist()); // Aguarda tempo aleatorio para verificar o Meio de Transmissao
					} catch (InterruptedException e) {
					}
					if (cT.getEscutaMeioTransmissao()) { // Verifica se esta livre para transmitir
						cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
						System.out.println("Confirmacao nao chegou DEVIDO A COLISOES, Transmissor [" + getId() + "] esta reenviando");
						setTransmitindoTrue(); // Seta imagem de Transmitindo True
						CamadaFisicaTransmissora(quadro); // Envia o Quadro
						try {
							sleep(1000); // Aguarda Confirmacao, se nao chegar, repete o processo
						} catch (InterruptedException e) {
						}
					} //Se o Meio tiver Ocupado repete o processo todo
				}
				confirmationPersistente = false;
			}
		}).start();
	}// fim do metodo CamadaAcessoAoMeioTransmisoraCsmaNaoPersistente

/*. CSMA PERSISTENTE Quando uma estação tem dados a transmitir, ela primeiro
escuta o canal para ver se mais alguem esta transmitindo no momento. Se o canal estiver ocupado,
a estacao esperara ate que ele fique ocioso. Quando detectar um canal desocupado, a estação
transmitira um quadro. Se ocorrer uma colisao, a estação esperara um intervalo de tempo aleatório
e comecara tudo de novo */

	void CamadaAcessoAoMeioTransmissoraCsmaPersistente(int quadro[]) {
		new Thread(() -> {
			if (cT.getEscutaMeioTransmissao()) { // Meio de transmissao liberado para transmitir
				cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
				System.out.println("Transmissor [" + getId() + "] esta Transmitindo");
				setTransmitindoTrue(); // Seta imagem de Transmitindo True
				CamadaFisicaTransmissora(quadro); // Envia o Quadro

				try {
					sleep(1000); // Aguarda Confirmacao
				} catch (InterruptedException e) {
				}

				while (!confirmationPersistente) { // Entrar aqui se n chegar confirmacao(houve colisao)
					try {
						sleep(timeAleatorioNaoPersist()); // Aguarda tempo aleatorio pois ocorreu Colisao
					} catch (InterruptedException e) {
					}
					while(!cT.getEscutaMeioTransmissao()){ // Fica escutando o meio interruptamente
						try {
						sleep(20);
					} catch (InterruptedException e) {}
					}
					//Chega aqui quando tiver livre
						cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
						System.out.println("Confirmacao nao chegou DEVIDO A COLISOES, Transmissor [" + getId() + "] esta reenviando");
						setTransmitindoTrue(); // Seta imagem de Transmitindo True
						CamadaFisicaTransmissora(quadro); // Envia o Quadro
						try {
							sleep(1000); // Aguarda Confirmacao, se nao chegar, repete o processo
						} catch (InterruptedException e) {
						}// Volta no While e confirma se chegou a confirmacao ou se houve colisao
						//Se houver colisao, repete todo o processo
				} 
				confirmationPersistente = false;


			} // Fim if
			else { // Meio BLOQUEADO INICIALMENTE
				while (!cT.getEscutaMeioTransmissao()) { // Entra se o meio estiver bloqueado ainda
					try {
						sleep(20);
					} catch (InterruptedException e) {}
					//Escutando Meio de transmissao ate ele ficar Livre e Liberar a Transmissao
				}
				//Chega aqui quando liberar o Meio de Transmissao
				cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
				System.out.println("Transmissor [" + getId() + "] esta Transmitindo");
				setTransmitindoTrue(); // Seta imagem de Transmitindo True
				CamadaFisicaTransmissora(quadro); // Envia o Quadro

				try {
					sleep(1000); // Aguarda Confirmacao
				} catch (InterruptedException e) {
				}

				while (!confirmationPersistente) { // Entrar aqui se n chegar confirmacao(houve colisao)
					try {
						sleep(timeAleatorioNaoPersist()); // Aguarda tempo aleatorio pois ocorreu Colisao
					} catch (InterruptedException e) {
					}

					while(!cT.getEscutaMeioTransmissao()){ // Fica escutando o meio interruptamente
						try {
						sleep(20);
					} catch (InterruptedException e) {}
						//Escutando Meio de Transmissao ate ficar livre novamente
					}

						cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
						System.out.println("Confirmacao nao chegou DEVIDO A COLISOES, Transmissor [" + getId() + "] esta reenviando");
						setTransmitindoTrue(); // Seta imagem de Transmitindo True
						CamadaFisicaTransmissora(quadro); // Envia o Quadro
						try {
							sleep(1000); // Aguarda Confirmacao, se nao chegar, repete o processo
						} catch (InterruptedException e) {
						}// Volta no While e confirma se chegou a confirmacao ou se houve colisao
						//Se houver colisao, repete todo o processo
				} 
				confirmationPersistente = false;
			} // Fim Else
		}).start();
	}// fim do metodo CamadaAcessoAoMeioTransmisoraCsmaPersistente

	/*. CSMA P PERSISTENTE o CSMA p-persistente. Ele se aplica a canais segmentados (slotted channels) 
	Quando esta pronta para transmitir, a estacao escuta o
canal. Se ele estiver desocupado, a estacao transmitira com uma probabilidade p. Esse
processo se repete ate o quadro ser transmitido ou ate que outra estacao tenha iniciado uma
transmissoo. Nesse ultimo caso, ela age como se tivesse ocorrido uma colisao (ou seja, aguarda
durante um intervalo aleatorio e reinicia a transmissao). */
	void CamadaDeAcessoAoMeioTransmissoraCsmaPPersistente(int quadro[]) {
		new Thread(() -> {
			long Slot = ((System.currentTimeMillis() % 1000) % 1000);
			// IF que verifica se o slot de tempo esta aberto, se o meio de transmissao
			// livre e se a probabilidade de 50porcento (0 e 1) foi acertada
			if (Slot == 0 && cT.getEscutaMeioTransmissao() && Probabilidade50p() == 1) {
				cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
				System.out.println("Transmissor [" + getId() + "] esta Transmitindo");
				setTransmitindoTrue(); // Seta imagem de Transmitindo True
				CamadaFisicaTransmissora(quadro); // Envia o Quadro

				try {
					sleep(1000); // Aguarda Confirmacao
				} catch (InterruptedException e) {
				}

				while (!confirmationPersistente) { // Confirmacao nao chegou (houve colisao)

					if (getControleColisaoPpersit()) { // Ocorreu Colisao e Aguarda Tempo Necessario
						try {
							sleep(timeAleatorioNaoPersist()); // Aguarda tempo aleatorio pois ocorreu Colisao
						} catch (InterruptedException e) {
						}
						setControleColisaoPpersist(false);
					}

					long Slot2 = ((System.currentTimeMillis() % 1000) % 1000); // Esperando Proximo Slot de tempo
					// IF que verifica se o slot de tempo esta aberto, se o meio de transmissao
					// livre e se a probabilidade de 50porcento (0 e 1) foi acertada
					if (Slot2 == 0 && cT.getEscutaMeioTransmissao() && Probabilidade50p() == 1) {
						cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
						System.out.println("Confirmacao nao chegou DEVIDO A COLISOES, Transmissor [" + getId() + "] esta reenviando");
						setTransmitindoTrue(); // Seta imagem de Transmitindo True
						CamadaFisicaTransmissora(quadro); // Envia o Quadro

						try {
							sleep(1000); // Aguarda Confirmacao
						} catch (InterruptedException e) {
						}
						setControleColisaoPpersist(true); // caso Haja Colisao, Espera o Tempo aleatorio Novamente encima
					} // Fim if transmissao
				} // Fim while
				confirmationPersistente = false;
			} // Fim Conseguiu Transmitir

			else { // Nao conseguiu entrar pois nao pegou a entrada do slot // ou o Meio estava
							// ocupado ou a prob n permitiu

				long Slot2 = ((System.currentTimeMillis() % 1000) % 1000); // Esperando Proximo Slot de tempo
				// Aguardando Slot de tempo, Meio de transmissao Livre e Probabilidade Correta
				// para transmitir
				while (Slot2 != 0 || !cT.getEscutaMeioTransmissao() || Probabilidade50p() == 0) {
					Slot2 = ((System.currentTimeMillis() % 1000) % 1000); // Fica buscando Novos slots de tempo
				}
				// Sai do While quando Todas condicoes forem Falsas e permitir transmitir

				cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
				System.out.println("Transmissor [" + getId() + "] esta Transmitindo");
				setTransmitindoTrue(); // Seta imagem de Transmitindo True
				CamadaFisicaTransmissora(quadro); // Envia o Quadro

				try {
					sleep(1000); // Aguarda Confirmacao
				} catch (InterruptedException e) {
				}

				while (!confirmationPersistente) { // Confirmacao nao chegou (houve colisao)

					if (getControleColisaoPpersit()) { // Ocorreu Colisao e Aguarda Tempo Necessario
						try {
							sleep(timeAleatorioNaoPersist()); // Aguarda tempo aleatorio pois ocorreu Colisao
						} catch (InterruptedException e) {
						}
						setControleColisaoPpersist(false);
					}

					long SlotAux = ((System.currentTimeMillis() % 1000) % 1000); // Esperando Proximo Slot de tempo
					// IF que verifica se o slot de tempo esta aberto, se o meio de transmissao
					// livre e se a probabilidade de 50porcento (0 e 1) foi acertada
					if (SlotAux == 0 && cT.getEscutaMeioTransmissao() && Probabilidade50p() == 1) {
						cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
						System.out.println("Confirmacao nao chegou DEVIDO A COLISOES, Transmissor [" + getId() + "] esta reenviando");
						setTransmitindoTrue(); // Seta imagem de Transmitindo True
						CamadaFisicaTransmissora(quadro); // Envia o Quadro

						try {
							sleep(1000); // Aguarda Confirmacao
						} catch (InterruptedException e) {
						}
						setControleColisaoPpersist(true); // caso Haja Colisao, Espera o Tempo aleatorio Novamente encima
					} // Fim if transmissao
				} // Fim while
				confirmationPersistente = false;
			}
		}).start();
	}// fim do metodo CamadaAcessoAoMeioTransmisoraCsmaPPersistente

	void CamadaDeAcessoAoMeioTransmissoraCsmaCD(int quadro[]) {
		new Thread(() -> {
			if (cT.getEscutaMeioTransmissao()) { // Meio de transmissao liberado para transmitir
				cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
				System.out.println("Transmissor [" + getId() + "] esta Transmitindo");
				setTransmitindoTrue(); // Seta imagem de Transmitindo True
				CamadaFisicaTransmissora(quadro); // Envia o Quadro

				while (!getAguardoCSMACD()) { // Aguardando Ver se Colidiu Instantaneamente
					try {
						sleep(20);
					} catch (InterruptedException e) {}
				} 
				setAguardoCSMA(false); // // Valor Original da Variavel

				if(getreenvioCSMACD()){
					while(getreenvioCSMACD()){
						setREENVIOCSMA(false);// Valor Original da Variavel
						try {
						sleep(timeAleatorioNaoPersist()); // Aguarda tempo aleatorio para verificar o Meio de Transmissao
					} catch (InterruptedException e) {
					}

					if (cT.getEscutaMeioTransmissao()) {
					cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
					System.out.println("Transmissor [" + getId() + "] identificou Colisao Inicial e esta Retransmitindo");
					setTransmitindoTrue(); // Seta imagem de Transmitindo True
					CamadaFisicaTransmissora(quadro); // Envia o Quadro

					while (!getAguardoCSMACD()) { // Aguardando Ver se Colidiu Instantaneamente
					try {
						sleep(20);
					} catch (InterruptedException e) {
					}
					} // Fim if escuta
				} // Caso n esteja livre, repete o processo
				setAguardoCSMA(false); // Valor Original da Variavel
					} // Fim while
				} // Fim if
				
				else{
					// Quadro Enviado Corretamente
				}
			}
			else { // Meio BLOQUEADO INICIALMENTE

				while (!cT.getEscutaMeioTransmissao()) { // Entra se o meio estiver bloqueado ainda
					try {
						sleep(timeAleatorioNaoPersist()); // Aguarda tempo aleatorio para verificar o Meio de Transmissao
					} catch (InterruptedException e) {
					}
				}
				// Chega aqui quando o meio Liberar
				cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
				System.out.println("Transmissor [" + getId() + "] esta Transmitindo");
				setTransmitindoTrue(); // Seta imagem de Transmitindo True
				CamadaFisicaTransmissora(quadro); // Envia o Quadro

				while (!getAguardoCSMACD()) { // Aguardando Ver se Colidiu Instantaneamente
					try {
						sleep(20);
					} catch (InterruptedException e) {
					}
				} 
				setAguardoCSMA(false); // Valor Original da Variavel

				if(getreenvioCSMACD()){
					while(getreenvioCSMACD()){
						setREENVIOCSMA(false);// Valor Original da Variavel
						try {
						sleep(timeAleatorioNaoPersist()); // Aguarda tempo aleatorio para verificar o Meio de Transmissao
					} catch (InterruptedException e) {
					}

					if (cT.getEscutaMeioTransmissao()) {
					cT.setEscutaMeioTransmissao(false); // bloqueia meio de transmissao(transmitindo)
					System.out.println("Transmissor [" + getId() + "] identificou Colisao Inicial e esta Retransmitindo");
					setTransmitindoTrue(); // Seta imagem de Transmitindo True
					CamadaFisicaTransmissora(quadro); // Envia o Quadro

					while (!getAguardoCSMACD()) { // Aguardando Ver se Colidiu Instantaneamente
					try {
						sleep(20);
					} catch (InterruptedException e) {
					}
					} // Fim if escuta
				} // Caso n esteja livre, repete o processo
				setAguardoCSMA(false); // Valor Original da Variavel
					} // Fim while
				} // Fim if
				
				else{
					// Quadro Enviado Corretamente
				}
			}
		}).start();
	}// fim do metodo CamadaAcessoAoMeioTransmisoraCsmaCD

	void CamadaFisicaTransmissora(int quadro[]) {
		int tipoDeCodificacao = cT.getCodificacao(); // alterar de acordo o teste
		int[] fluxoBrutoDeBits;
		switch (tipoDeCodificacao) {
			case 0: // codificao binaria
				fluxoBrutoDeBits = CamadaFisicaTransmissoraCodificacaoBinaria(quadro);
				break;
			case 1: // codificacao manchester
				fluxoBrutoDeBits = CamadaFisicaTransmissoraCodificacaoManchester(quadro);
				break;
			case 2: // codificacao manchester diferencial
				fluxoBrutoDeBits = CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(quadro);
				break;
			default:
				fluxoBrutoDeBits = quadro;
			break;
		}// fim do switch/case
		cT.getCm().MeioDeComunicacao(fluxoBrutoDeBits, this); //CONFERIR ISSO AQUI
	}// fim do metodo CamadaFisicaTransmissora

	public int[] CamadaFisicaTransmissoraCodificacaoBinaria(int quadro[]) {
		// Como o Binário não precisa Alterar os valores, retorna o mesmo Quadro
		return quadro;
	}// fim do metodo CamadaFisicaTransmissoraCodificacaoBinaria


	public int[] CamadaFisicaTransmissoraCodificacaoManchester(int quadro[]) {
		setQtdBitsInsercaoBits(getQtdBitsInsercaoBits()*2);
		int qtdCarac;
		int tamanhoArray;
		if(getQtdBitsInsercaoBits()%32 == 0){
			tamanhoArray = getQtdBitsInsercaoBits()/32;
		}
		else{
			tamanhoArray = (getQtdBitsInsercaoBits()/32)+1;

		}
		if(getQtdBitsInsercaoBits()%16 == 0){
			qtdCarac = (getQtdBitsInsercaoBits()/16);
		}
		else{
			qtdCarac = (getQtdBitsInsercaoBits()/16)+1;
		}
		int [] CodManchester = new int[tamanhoArray+1];

		int controllerBits = 0;
		int controllerBitsManchester = 0;	
		int indexManch = 0; // Index do Array Manchester
		
		int deslocBit = 0; // Variavel define qual bit vai deslocar para manipulacaoo (Inicia do 7 pois eh o ultimo Bit de um Caractere)
		int deslocBitManchester = 0; // Variavel define qual bit vai deslocar para manipulacao (Inicia do 7 pois eh o ultimo Bit de um Caractere)

		// For até o tamanho da Mensagem
		for (int i = 0; i < qtdCarac; i++) {
			if(i % 2 == 0 && i != 0){ // Aumenta o indice do Array quando tiver mais de 2 letras (Cada index guarda 2 letras (Metade do Binario))
				indexManch++;
			}
			// Estruturas de If/else que faz o controle coordenado dos bits de cada array
			// Sendo eles o binario (7, 15, 23, 31) e o Manchester (15, 31)
			// assim, garante que serao pegados os bits corretamente salvando 2 caracteres no array manchester
			if (controllerBitsManchester % 2 == 0) {
				deslocBitManchester = 15; // Primeiro Bit da Primeira Letra
				controllerBitsManchester = 0;
			} else {
				deslocBitManchester = 31; // Primeiro Bit da Segunda lETRA
			}
			if (controllerBits % 4 == 0) {
				deslocBit = 7;
				controllerBits = 0;
			} else {
				deslocBit += 16;
			}
	
			for (int j = 0; j < 8; j++) { // For para cada caractere
 				int mascara = 1 << deslocBit;
				int Bit = (quadro[i/4] & mascara) >> deslocBit; // Pega o Bit na posicao da Mascara&Quadro na posicao deslocBit
				// Estrutura de IF que manipula bit por Bit
				if(Bit == 1 || Bit == -1){
					// define o bit na posicaoo deslocBit do quadro[indexQuadro] como 10, mantendo os outros bits inalterados.
					CodManchester[indexManch] = CodManchester[indexManch] | (1 << (deslocBitManchester));
					CodManchester[indexManch] = CodManchester[indexManch] | (0 << (deslocBitManchester-1));}
				else{
					// define o bit na posicaoo deslocBit do quadro[indexQuadro] como 01, mantendo os outros bits inalterados.
					CodManchester[indexManch] = CodManchester[indexManch] | (0 << (deslocBitManchester));
					CodManchester[indexManch] = CodManchester[indexManch] | (1 << (deslocBitManchester-1));}
			deslocBit--;
			deslocBitManchester = deslocBitManchester - 2;
			}
			controllerBits++;
			controllerBitsManchester++;
		}
    //Verifica Se o enquadramento selecionado foi o 
    //Violacao da camada fisica, caso tenha sido realiza o enquadramento
    // antes de enviar para o meio de comunicacao
    int quadroEnquadrado[];
    quadroEnquadrado = CodManchester;
		quadroEnquadrado[quadroEnquadrado.length-1] = quadro[quadro.length-1];
		//System.out.println("Testando: "+ cG.ExibirBinarioControleErro(quadroEnquadrado));
		return quadroEnquadrado;
	}// fim do metodo CamadaFisicaTransmissoraCodificacaoManchester


	public int[] CamadaFisicaTransmissoraCodificacaoManchesterDiferencial(int quadro[]) {
		setQtdBitsInsercaoBits(getQtdBitsInsercaoBits()*2); 
		int controllerBits = 0;
		int controllerBitsManchester = 0;
		int qtdCarac;
		int tamanhoArray;
		if(getQtdBitsInsercaoBits()%32 == 0){
			tamanhoArray = getQtdBitsInsercaoBits()/32;
		}
		else{
			tamanhoArray = (getQtdBitsInsercaoBits()/32)+1;

		}
		if(getQtdBitsInsercaoBits()%16 == 0){
			qtdCarac = (getQtdBitsInsercaoBits()/16);
		}
		else{
			qtdCarac = (getQtdBitsInsercaoBits()/16)+1;
		}
		int [] CodManchesterDiff = new int[tamanhoArray+1];

		int indexManch = 0; // Index do Array Manchester Differencial
		int deslocBit = 0; // Variavel define qual bit vai deslocar para manipulação (Inicia do 7 pois é o ultimo Bit de um Caractere)
		int deslocBitManchester = 0; // Variavel define qual bit vai deslocar para manipulação (Inicia do 7 pois é o ultimo Bit de um Caractere)
		
		// For até o tamanho da Mensagem
		for (int i = 0; i < qtdCarac; i++) {
			boolean InversionSignal = false;
			if(i % 2 == 0 && i != 0){ // Aumenta o indice do Array quando tiver mais de 2 letras (Cada index guarda 2 letras (Metade do Binario))
				indexManch++;
			}

			// Estruturas de If/else que faz o controle coordenado dos bits de cada array
			// Sendo eles o binario (7, 15, 23, 31) e o Manchester (15, 31)
			// assim, garante que serao pegados os bits corretamente salvando 2 caracteres no array manchester
		if (controllerBitsManchester % 2 == 0) {
			deslocBitManchester = 15; // Primeiro Bit da Primeira Letra
			controllerBitsManchester = 0;
		} else {
			deslocBitManchester = 31; // Primeiro Bit da Segunda lETRA
		}
		if (controllerBits % 4 == 0) {
			deslocBit = 7;
			controllerBits = 0;
		} else {
			deslocBit += 16;
		}

			for (int j = 0; j < 8; j++) { // For para cada caractere
				int mascara = 1 << deslocBit;
				int Bit = (quadro[i/4] & mascara) >> deslocBit; // Pega o Bit na posicao da Mascara&Quadro na posicao deslocBit
				// Estrutura de IF que manipula bit por Bit
				if (Bit == 1 || Bit == -1) {
					// define o bit na posicao deslocBit do quadro[indexQuadro] como 1, mantendo os outros bits inalterados.
					if (InversionSignal == true) {
						CodManchesterDiff[indexManch] = CodManchesterDiff[indexManch] | (0 << (deslocBitManchester));
						CodManchesterDiff[indexManch] = CodManchesterDiff[indexManch] | (1 << (deslocBitManchester - 1));
						InversionSignal = false;
					} else {
						CodManchesterDiff[indexManch] = CodManchesterDiff[indexManch] | (1 << (deslocBitManchester));
						CodManchesterDiff[indexManch] = CodManchesterDiff[indexManch] | (0 << (deslocBitManchester - 1));
						InversionSignal = true;
					}
				}
				else {
					// define o bit na posicao deslocBit do quadro[indexQuadro] como 1, mantendo os outros bits inalterados.
					if (InversionSignal == true) {
						CodManchesterDiff[indexManch] = CodManchesterDiff[indexManch] | (1 << (deslocBitManchester));
						CodManchesterDiff[indexManch] = CodManchesterDiff[indexManch] | (0 << (deslocBitManchester - 1));

					} else {
						CodManchesterDiff[indexManch] = CodManchesterDiff[indexManch] | (0 << (deslocBitManchester));
						CodManchesterDiff[indexManch] = CodManchesterDiff[indexManch] | (1 << (deslocBitManchester - 1));
					}
				}
				deslocBit--;
				deslocBitManchester = deslocBitManchester - 2;
			}
			controllerBits++;
			controllerBitsManchester++;
		}
    //Verifica Se o enquadramento selecionado foi o 
    //Violacao da camada fisica, caso tenha sido realiza o enquadramento
    // antes de enviar para o meio de comunicacao
    int quadroEnquadrado[];
      quadroEnquadrado = CodManchesterDiff;
			quadroEnquadrado[quadroEnquadrado.length-1] = quadro[quadro.length-1];
		return quadroEnquadrado;
	}// fim do CamadaFisicaTransmissoraCodificacaoManchesterDiferencial
  
		//////////////////////////////////////////////////////////////////
		//
		//
		//
		///////////////////////////////////////////////////////////////////
		// FINALIZADOS A TRANSMISSAO AGORA OS METODOS DE RECEPCAO ABAIXO //
		///////////////////////////////////////////////////////////////////
		//
		//
		//
		///////////////////////////////////////////////////////////////////

		void CamadaFisicaReceptora(int quadro[]) {
			int tipoDeDecodificacao = cT.getCodificacao(); // alterar de acordo o teste
			int[] fluxoBrutoDeBits = new int[setTamanhoArray(cT.getCodificacao())]; // ATENcaO: trabalhar com BITS!!!
			switch (tipoDeDecodificacao) {
				case 0: // codificao binaria
					fluxoBrutoDeBits = CamadaFisicaReceptoraDecodificacaoBinaria(quadro);
					break;
				case 1: // codificacao manchester
					fluxoBrutoDeBits = CamadaFisicaReceptoraDecodificacaoManchester(quadro);
					break;
				case 2: // codificacao manchester diferencial
					fluxoBrutoDeBits = CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(quadro);
					break;
			}// fim do switch/case
			CamadaAcessoAoMeioReceptora(fluxoBrutoDeBits);
		}// fim do metodo CamadaFisicaTransmissora

		public int[] CamadaFisicaReceptoraDecodificacaoBinaria(int quadro[]) {
			// Ja está em binario, não havendo necessidade de decodificar o Arrray
			return quadro;
		}// fim do metodo CamadaFisicaReceptoraDecodificacaoBinaria

		public int[] CamadaFisicaReceptoraDecodificacaoManchester(int quadro[]) {
			//Criando Novo Tamanho do Array
			int quadroDescrip[];
			int tamanhoFOR;
			int qtdCarac=0;
			int tamanhoArray2;
			if (cT.getEnquadramento() == 3) {
				//quadro = DecodificacaoViolacaoCamadaFisica(quadro);
				int tamanhoArray;
				int ContagemBits = getQtdBitsInsercaoBits();
				if (ContagemBits % 32 == 0)
				tamanhoArray = (ContagemBits / 32);
				else
				tamanhoArray = ((ContagemBits / 32) + 1);
	
				if (ContagemBits % 8 == 0)
				tamanhoFOR = (ContagemBits / 8);
				else
				tamanhoFOR = ((ContagemBits / 8) + 1);
				System.out.println(tamanhoFOR);
				quadroDescrip = new int[tamanhoArray];
			}
			else{
				if(getQtdBitsInsercaoBits()%32 == 0){
					tamanhoArray2 = getQtdBitsInsercaoBits()/32;
				}
				else{
					tamanhoArray2 = (getQtdBitsInsercaoBits()/32)+1;
		
				}
				if(getQtdBitsInsercaoBits()%16 == 0){
					qtdCarac = (getQtdBitsInsercaoBits()/16);
				}
				else{
					qtdCarac = (getQtdBitsInsercaoBits()/16)+1;
				}
				quadroDescrip = new int[tamanhoArray2+1];
			}
	
			int deslocBit = 0; // Variavel define qual bit vai deslocar para manipulação
			int deslocBitDescript = 0; // Variavel define qual bit vai deslocar para manipulação 
	
			for (int i = 0; i < qtdCarac; i++) { // For ate o tamanho da Mensagem
				if (deslocBit == 32) { // verifica o ultimo bit de cada posicao do array
					deslocBit = 0;
				}
				for (int t = 0; t < 8; t++) { // for para cada Caractere
					StringBuilder parBit = new StringBuilder();
					for (int j = 0; j < 2; j++) { // For para cada Par de Bit
						int mascara = 1 << deslocBit; // Mascara com bit 1 na Posicao deslocBit
						int Bit = (quadro[i / 2] & mascara) >> deslocBit; // Pega o Bit na posicao da Mascara&Quadro na posicao	desloc bit
						if (Bit == -1) {
							Bit = Bit * -1;
						}
						parBit.insert(0, Bit); // Insere o Bit no parBit que será um par de Bits
						deslocBit++;
					}
					if (parBit.toString().equals("10")) { // Verifica se o par de Bits eh 10
						quadroDescrip[i / 4] = quadroDescrip[i / 4] | (1 << (deslocBitDescript)); // Insere o Bit 1 na posicao deslocBit 																																							
					}
					deslocBitDescript++;
				}
			} // Fim for msg
			if(cT.getEnquadramento()!= 3)
				setQtdBitsInsercaoBits(getQtdBitsInsercaoBits()/2);
				quadroDescrip[quadroDescrip.length-1] = quadro[quadro.length-1];
			return quadroDescrip;
		}// fim do metodo CamadaFisicaReceptoraDecodificacaoManchester

		public int[] CamadaFisicaReceptoraDecodificacaoManchesterDiferencial(int quadro[]) {
			int quadroDescrip[];
			int tamanhoFOR;
			int qtdCarac=0;
			int tamanhoArray2;
			if (cT.getEnquadramento() == 3) {
				//quadro = DecodificacaoViolacaoCamadaFisica(quadro);
				int tamanhoArray;
				int ContagemBits = getQtdBitsInsercaoBits();
				if (ContagemBits % 32 == 0)
				tamanhoArray = (ContagemBits / 32);
				else
				tamanhoArray = ((ContagemBits / 32) + 1);
	
				if (ContagemBits % 8 == 0)
				tamanhoFOR = (ContagemBits / 8);
				else
				tamanhoFOR = ((ContagemBits / 8) + 1);
	
				quadroDescrip = new int[tamanhoArray];
				System.out.println(tamanhoFOR);
			}
			else{
				if(getQtdBitsInsercaoBits()%32 == 0){
					tamanhoArray2 = getQtdBitsInsercaoBits()/32;
				}
				else{
					tamanhoArray2 = (getQtdBitsInsercaoBits()/32)+1;
		
				}
				if(getQtdBitsInsercaoBits()%16 == 0){
					qtdCarac = (getQtdBitsInsercaoBits()/16);
				}
				else{
					qtdCarac = (getQtdBitsInsercaoBits()/16)+1;
				}
				quadroDescrip = new int[tamanhoArray2+1];
			}
			//System.out.println("Quadro Desenquadradado: " + cG.ExibirManchester(quadro));
			int deslocBit =0; // Variavel define qual bit vai deslocar para manipulação
			StringBuilder fluxoFinal = new StringBuilder();
	
			for (int i = 0; i < qtdCarac; i++) { // For ate o tamanho da Mensagem
				String bitComparation = "10"; // Bit de comparacao inicial
				boolean verifyComparation = true; // varival de controle para comparacao
	
				if(i % 2== 0){ // alterna o valor do deslocamento entre 15 ( ultimo bit da primeiro caractere da posicao)
					deslocBit = 15;
				}
				else{ // alterna o valor do deslocamento entre 31 ( ultimo bit da segunda caractere da posicao)
					deslocBit = 31;
				}
				StringBuilder fluxoAux = new StringBuilder();
				for (int t = 0; t < 8; t++) { // for para cada caractere
					StringBuilder parBit = new StringBuilder();
					for (int j = 0; j < 2; j++) { // For para cada par de Bits
						int mascara = 1 << deslocBit; // Mascara com bit 1 na Posicao deslocBit
						int Bit = (quadro[i / 2] & mascara) >> deslocBit; // Pega o Bit na posicao da Mascara&Quadro na posicao eslocBit																
						if (Bit == -1) {
							Bit = Bit * -1;
						}
						parBit.append(Bit); // Insere o Bit no parBit que sera um par de Bits
						deslocBit--;
					}
	
					if(parBit.toString().equals(bitComparation)){ // Verifica se deve ser inserido ou nao o Bit 1
						if(verifyComparation == true){
							fluxoAux.append(1);
							verifyComparation = false;
							bitComparation = parBit.toString();	
						}
						else{
							fluxoAux.append(0);
						}
					}
					else{
						if(verifyComparation == false){
							fluxoAux.append(1);
							bitComparation = parBit.toString();	
						}
						else{
							fluxoAux.append(0);
						}
					}
				}
				fluxoAux.reverse();
				fluxoFinal.append(fluxoAux.toString());	
			} // Fim for msg
	
			for (int i = 0; i < fluxoFinal.length(); i++) {
				// Estrutura de IF que manipula bit por Bit
				int deslocamento = i%32;
				if (fluxoFinal.charAt(i) == '1') {
					// define o bit na posicaoo deslocBit do quadro[indexQuadro] como 1
					quadroDescrip[i/32] = quadroDescrip[i/32] | (1 << deslocamento);
				}
			}
			if(cT.getEnquadramento()!= 3)
				setQtdBitsInsercaoBits(getQtdBitsInsercaoBits()/2); 
				quadroDescrip[quadroDescrip.length-1] = quadro[quadro.length-1];
			return quadroDescrip;
		}// fim do CamadaFisicaReceptoraDecodificacaoManchesterDiferencial

		void CamadaAcessoAoMeioReceptora(int quadro[]) {
			int tipoDeAcessoAoMeio = cT.getControleDeAcessoAoMeio();
			switch (tipoDeAcessoAoMeio) {
			case 0:
				CamadaAcessoAoMeioReceptoraAlohaPuro(quadro);
				break;
			case 1:
				CamadaAcessoAoMeioReceptoraSlottedAloha(quadro);
				break;
			case 2:
				CamadaAcessoAoMeioReceptoraCsmaNaoPersistente(quadro);
				break;
			case 3: 
				CamadaAcessoAoMeioReceptoraCsmaPersistente(quadro);
				break;
			case 4: 
				CamadaAcessoAoMeioReceptoraCsmaPPersistente(quadro);
				break;
			case 5:
				CamadaAcessoAoMeioReceptoraCsmaCD(quadro);
				break;
		}// fim do switch/case
		}// fim do metodo CamadaAcessoAoMeioDadosReceptora

        void CamadaAcessoAoMeioReceptoraAlohaPuro(int quadro[]) {
          setTransmitindoFalse(); // Terminou o uso do meio de transmissao
          try {
            // semaforo para proteger as variaveis compartilhadas que sao usadas apenas para interface
            // e animacao correta do codigo
            cT.getSemaforoVerificadorColisao().acquire();
            if (cT.getTransmissorColidiu()[(int) getId() - 1] == true) {
              // Essa Mensagem Colidiu e Chegou com erros, nao faz nada e aguarda o reenvio
            } else { 
              confirmationAloha = true; // COnfirmacao da mensagem chegada
              CamadaEnlaceDadosReceptora(quadro); // chama proxima camada
              confirmacaoEnvioProximoQuadro = false;
              System.out.println("Transmissor [" + getId() + "] Encerrou Sua Transmissao");
              System.out.println("<------------------------------------------------->");
            }
            cT.getTransmissorColidiu()[(int) getId() - 1] = false; //seta como false sua possivel colisao
            // semaforo para proteger as variaveis compartilhadas que sao usadas apenas para interface
            // e animacao correta do codigo
            cT.getSemaforoVerificadorColisao().release();
          } catch (InterruptedException e) {
          }
        }// fim do metodo CamadaAcessoAoMeioReceptoraAlohaPuro

		void CamadaAcessoAoMeioReceptoraSlottedAloha(int quadro[]) {
          setTransmitindoFalse(); // Terminou o uso do meio de transmissao
          try {
            // semaforo para proteger as variaveis compartilhadas que sao usadas apenas para interface
            // e animacao correta do codigo
            cT.getSemaforoVerificadorColisao().acquire();
            if (cT.getTransmissorColidiu()[(int) getId() - 1] == true) {
              // Essa Mensagem Colidiu e Chegou com erros, nao faz nada e aguarda o reenvio
            } else { 
              confirmationAloha = true; // COnfirmacao da mensagem chegada
              CamadaEnlaceDadosReceptora(quadro); // chama proxima camada
              confirmacaoEnvioProximoQuadro = false;
              System.out.println("Transmissor [" + getId() + "] Encerrou Sua Transmissao");
              System.out.println("<------------------------------------------------->");
            }
            cT.getTransmissorColidiu()[(int) getId() - 1] = false; //seta como false sua possivel colisao
            // semaforo para proteger as variaveis compartilhadas que sao usadas apenas para interface
            // e animacao correta do codigo
            cT.getSemaforoVerificadorColisao().release();
          } catch (InterruptedException e) {
          }
		}// fim do metodo CamadaAcessoAoMeioReceptoraSlottedAloha

		void CamadaAcessoAoMeioReceptoraCsmaNaoPersistente(int quadro[]) {
	          setTransmitindoFalse(); // Terminou o uso do meio de transmissao
          try {
            // semaforo para proteger as variaveis compartilhadas que sao usadas apenas para interface
            // e animacao correta do codigo e identificar colisoes
            cT.getSemaforoVerificadorColisao().acquire();
            if (cT.getTransmissorColidiu()[(int) getId() - 1] == true) {
							cT.setEscutaMeioTransmissao(true); // Libera meio de transmissao(finalizou a transmissao)
              // Essa Mensagem Colidiu e Chegou com erros, nao faz nada e aguarda o reenvio
            } else {
							confirmationPersistente = true; // COnfirmacao da mensagem chegada
							cT.setEscutaMeioTransmissao(true); // Libera meio de transmissao(finalizou a transmissao)
              confirmacaoEnvioProximoQuadro = false;
              CamadaEnlaceDadosReceptora(quadro); // chama proxima camada
              System.out.println("Transmissor [" + getId() + "] Encerrou Sua Transmissao");
              System.out.println("<------------------------------------------------->");
            }
            cT.getTransmissorColidiu()[(int) getId() - 1] = false; //seta como false sua possivel colisao
            // semaforo para proteger as variaveis compartilhadas que sao usadas apenas para interface
            // e animacao correta do codigo e identificar colisoes
            cT.getSemaforoVerificadorColisao().release();
          } catch (InterruptedException e) {
          }
		}// fim do metodo CamadaAcessoAoMeioReceptoraCsmaNaoPersistente

		void CamadaAcessoAoMeioReceptoraCsmaPersistente(int quadro[]) {
			setTransmitindoFalse(); // Terminou o uso do meio de transmissao
			try {
				// semaforo para proteger as variaveis compartilhadas que sao usadas apenas para
				// e animacao correta do codigo, interface e identificar colisoes
				cT.getSemaforoVerificadorColisao().acquire();
				if (cT.getTransmissorColidiu()[(int) getId() - 1] == true) {
					cT.setEscutaMeioTransmissao(true); // Libera meio de transmissao(finalizou a transmissao)
					// Essa Mensagem Colidiu e Chegou com erros, nao faz nada e aguarda o reenvio
				} else {
					confirmationPersistente = true; // COnfirmacao da mensagem chegada
					cT.setEscutaMeioTransmissao(true); // Libera meio de transmissao(finalizou a transmissao)
					confirmacaoEnvioProximoQuadro = false;
					CamadaEnlaceDadosReceptora(quadro); // chama proxima camada
					System.out.println("Transmissor [" + getId() + "] Encerrou Sua Transmissao");
					System.out.println("<------------------------------------------------->");
				}
				cT.getTransmissorColidiu()[(int) getId() - 1] = false; // seta como false sua possivel colisao
				// semaforo para proteger as variaveis compartilhadas que sao usadas apenas para
				// interface e animacao correta do codigo, e identificar colisoes
				cT.getSemaforoVerificadorColisao().release();
			} catch (InterruptedException e) {
			}
		}// fim do metodo CamadaAcessoAoMeioReceptoraCsmaPersistente

		void CamadaAcessoAoMeioReceptoraCsmaPPersistente(int quadro[]) {
			setTransmitindoFalse(); // Terminou o uso do meio de transmissao
			try {
				// semaforo para proteger as variaveis compartilhadas que sao usadas apenas para
				// e animacao correta do codigo, interface e identificar colisoes
				cT.getSemaforoVerificadorColisao().acquire();
				if (cT.getTransmissorColidiu()[(int) getId() - 1] == true) {
					cT.setEscutaMeioTransmissao(true); // Libera meio de transmissao(finalizou a transmissao)
					// Essa Mensagem Colidiu e Chegou com erros, nao faz nada e aguarda o reenvio
				} else {
					confirmationPersistente = true; // COnfirmacao da mensagem chegada
					cT.setEscutaMeioTransmissao(true); // Libera meio de transmissao(finalizou a transmissao)
					confirmacaoEnvioProximoQuadro = false;
					CamadaEnlaceDadosReceptora(quadro); // chama proxima camada
					System.out.println("Transmissor [" + getId() + "] Encerrou Sua Transmissao");
					System.out.println("<------------------------------------------------->");
				}
				cT.getTransmissorColidiu()[(int) getId() - 1] = false; // seta como false sua possivel colisao
				// semaforo para proteger as variaveis compartilhadas que sao usadas apenas para
				// interface e animacao correta do codigo, e identificar colisoes
				cT.getSemaforoVerificadorColisao().release();
			} catch (InterruptedException e) {
			}
		}// fim do metodo CamadaAcessoAoMeioReceptoraCsmaPPersistente

		void CamadaAcessoAoMeioReceptoraCsmaCD(int quadro[]) {
			setTransmitindoFalse(); // Terminou o uso do meio de transmissao
			try {
				// semaforo para proteger as variaveis compartilhadas que sao usadas apenas para
				// e animacao correta do codigo, interface e identificar colisoes
				cT.getSemaforoVerificadorColisao().acquire();
					cT.setEscutaMeioTransmissao(true); // Libera meio de transmissao(finalizou a transmissao)
					confirmacaoEnvioProximoQuadro = false;
					CamadaEnlaceDadosReceptora(quadro); // chama proxima camada
					System.out.println("Transmissor [" + getId() + "] Encerrou Sua Transmissao");
					System.out.println("<------------------------------------------------->");
				cT.getSemaforoVerificadorColisao().release();
			} catch (InterruptedException e) {
			}
			// algum codigo aqui
		}// fim do metodo CamadaAcessoAoMeioReceptoraCsmaCD

		public void CamadaEnlaceDadosReceptora (int quadro []) {
			int quadroControleErro[] = CamadaEnlaceDadosReceptoraControleDeErro(quadro);
			if(quadroControleErro == null){
				//AplicacaoReceptora(null);
				int quadroFLUXO[] = CamadaEnlaceDadosReceptoraControleDeFluxo(null);
			}
			else{
				int quadroDesenquadrado[] = CamadaEnlaceDadosReceptoraEnquadramento(quadroControleErro);
				int quadroFLUXO[] = CamadaEnlaceDadosReceptoraControleDeFluxo(quadroDesenquadrado);
				//chama proxima camada
				CamadaDeAplicacaoReceptora(quadroFLUXO);
			}
			}//fim do metodo CamadaEnlaceDadosReceptora

			int[] CamadaEnlaceDadosReceptoraControleDeErro(int quadro[]) {
				int tipoDeControleDeErro = cT.getControleErro(); // alterar de acordo com o teste
				int[] quadroSemErro;
				switch (tipoDeControleDeErro) {
					case 0: // bit de paridade par
						quadroSemErro = CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadePar(quadro);
						break;
					case 1: // bit de paridade impar
						quadroSemErro = CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadeImpar(quadro);
						break;
					case 2: // CRC
						quadroSemErro = CamadaEnlaceDadosReceptoraControleDeErroCRC(quadro);
						break;
					case 3: // codigo de hamming
						quadroSemErro = CamadaEnlaceDadosReceptoraControleDeErroCodigoDeHamming(quadro);
						break;
					default:
						quadroSemErro = quadro;
						break;
				}// fim do switch/case
				return quadroSemErro;
			}// fim do metodo CamadaEnlaceDadosReceptoraControleDeErro

			int[] CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadePar(int quadro[]) {
				int posicaoRemocao = ((getQtdBitsInsercaoBits() % 32) - 1);
				int indexArray = 0;
				if (getQtdBitsInsercaoBits() % 32 == 0)
					indexArray = (getQtdBitsInsercaoBits() / 32);
				else
					indexArray = (getQtdBitsInsercaoBits() / 32) + 1;
		
				int somatorioBITS1 = 0;
				// For até o tamanho da Mensagem
				for (int i = getQtdBitsInsercaoBits() - 1; i >= 0; i--) {
					int bitQuadro = i % 32;
					int mascara = 1 << bitQuadro;
					int Bit = (quadro[i / 32] & mascara) >> bitQuadro; // Pega o Bit na posicao da Mascara&Quadro na posicao desloc
																														 // Bit
					// Estrutura de IF que manipula bit por Bit
					if (Bit == 1 || Bit == -1) {
						somatorioBITS1++;
					}
				} // Fim For Bits
				if (somatorioBITS1 % 2 == 0) { // Se Tiver Quantidade Par de Bits, Insere um Bit 1 no ultimo Bit da mensagem
					// Tira a Informação de Controle do Array de Bits
					quadro[indexArray - 1] = quadro[indexArray - 1] | (0 << posicaoRemocao);
					setQtdBitsInsercaoBits(getQtdBitsInsercaoBits() - 1);
					return quadro;
				} else {
					return null;
				}
			}// fim do metodo CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadePar

			int[] CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadeImpar(int quadro[]) {
				int posicaoRemocao = ((getQtdBitsInsercaoBits() % 32) - 1);
				int indexArray = 0;
				if (getQtdBitsInsercaoBits() % 32 == 0)
					indexArray = (getQtdBitsInsercaoBits() / 32);
				else
					indexArray = (getQtdBitsInsercaoBits() / 32) + 1;
		
				int somatorioBITS1 = 0;
				// For até o tamanho da Mensagem
				for (int i = getQtdBitsInsercaoBits() - 1; i >= 0; i--) {
					int bitQuadro = i % 32;
					int mascara = 1 << bitQuadro;
					int Bit = (quadro[i / 32] & mascara) >> bitQuadro; // Pega o Bit na posicao da Mascara&Quadro na posicao desloc
																														 // Bit
					// Estrutura de IF que manipula bit por Bit
					if (Bit == 1 || Bit == -1) {
						somatorioBITS1++;
					}
				} // Fim For Bits
				if (somatorioBITS1 % 2 != 0) { // Se Tiver Quantidade Par de Bits, Insere um Bit 1 no ultimo Bit da mensagem
					// Tira a Informação de Controle do Array de Bits
					quadro[indexArray - 1] = quadro[indexArray - 1] | (0 << posicaoRemocao);
					setQtdBitsInsercaoBits(getQtdBitsInsercaoBits() - 1);
					return quadro;
				} else {
					return null;
				}
			}// fim do metodo CamadaEnlaceDadosReceptoraControleDeErroBitDeParidadeImpar

			int[] CamadaEnlaceDadosReceptoraControleDeErroCRC(int quadro[]) {
				int[] quadroSemControle;
				String PolinomioCRC32 = "100000100110000010001110110110111";
				String verifyRESTO = divisaoBinariaResto(ExibirBinarioControleErro(quadro), PolinomioCRC32);
				// Validacao se A mensagem Chegou Corretamente (Sem ERROS)
				for (int i = 0; i < 32; i++) {
					if (verifyRESTO.charAt(i) != '0') {
						return null;
					}
				}
				// Definindo Tamanho do Array sem o Controle de Erro
				if ((getQtdBitsInsercaoBits() - 32) % 32 == 0) {
					quadroSemControle = new int[((getQtdBitsInsercaoBits() - 32) / 32)+1];
				} else {
					quadroSemControle = new int[(((getQtdBitsInsercaoBits() - 32) / 32) + 1)+1];
				}
				int bitsAnterior = getQtdBitsInsercaoBits() - 33;
		
				for (int i = getQtdBitsInsercaoBits() - 1; i >= 32; i--) {
					int bitQuadro = i % 32;
					int mascara = 1 << bitQuadro;
					int Bit = (quadro[i / 32] & mascara) >> bitQuadro; // Pega o Bit na posicao da Mascara&Quadro na posicao desloc
																														 // Bit
					if (Bit == 1 || Bit == -1) {
						int bitQuadroSemControle = bitsAnterior % 32;
						quadroSemControle[bitsAnterior / 32] = quadroSemControle[bitsAnterior / 32] | (1 << bitQuadroSemControle);
					}
					bitsAnterior--;
				}
				quadroSemControle[quadroSemControle.length-1] = quadro[quadro.length-1];
				setQtdBitsInsercaoBits((getQtdBitsInsercaoBits() - 32));
				return quadroSemControle;
			}// fim do metodo CamadaEnlaceDadosReceptoraControleDeErroCRC

			int[] CamadaEnlaceDadosReceptoraControleDeErroCodigoDeHamming(int quadro[]) {
				String StringBinariaRecebida = ExibirBinarioControleErro(quadro);
		
				if (decodificarHamming(StringBinariaRecebida) == null) {
					return null;
				}
		
				StringBuilder mensagemBinaria = new StringBuilder(decodificarHamming(StringBinariaRecebida));
				mensagemBinaria.reverse(); // Invertendo os bits para inserir corretamente no array
		
				// Definindo o tamanho do array sem Controle de Erro
				int tamanhoQuadro;
				if (mensagemBinaria.length() % 32 == 0) {
					tamanhoQuadro = (mensagemBinaria.length() / 32);
				} else {
					tamanhoQuadro = (mensagemBinaria.length() / 32) + 1;
				}
		
				// Criando o Array com o novo Tamanho e Inserindo os Bits
				// Por meio de Mascara nele
				int[] arraySemControleERRO = new int[tamanhoQuadro+1];
				for (int i = (mensagemBinaria.length() - 1); i >= 0; i--) {
					int bitQuadro = i % 32;
					if (mensagemBinaria.charAt(i) == '1') {
						arraySemControleERRO[i / 32] = arraySemControleERRO[i / 32] | (1 << bitQuadro);
					}
				}
				arraySemControleERRO[arraySemControleERRO.length-1] = quadro[quadro.length-1];
				setQtdBitsInsercaoBits(mensagemBinaria.length());
				return arraySemControleERRO;
			}// fim do metodo CamadaEnlaceDadosReceptoraControleDeErroCodigoDeHamming

			public int[] CamadaEnlaceDadosReceptoraControleDeFluxo(int quadro[]) {
        int tipoDeControleDeFluxo = cT.getControleFluxo(); // alterar de acordo com o teste
        int[] quadroFluxo;
        switch (tipoDeControleDeFluxo) {
            case 0: // protocolo de janela deslizante de 1 bit
                quadroFluxo = CamadaEnlaceDadosReceptoraJanelaDeslizanteUmBit(quadro);
                break;
            case 1: // protocolo de janela deslizante go-back-n
                quadroFluxo = CamadaEnlaceDadosReceptoraJanelaDeslizanteGoBackN(quadro);
                break;
            case 2: // protocolo de janela deslizante com retransmissão seletiva
                quadroFluxo = CamadaEnlaceDadosReceptoraJanelaDeslizanteComRetransmissaoSeletiva(quadro);
                break;
            default:
            quadroFluxo = quadro;
            break;
        }// fim do switch/case
        return quadroFluxo;
    }// fim do metodo CamadaEnlaceDadosReceptoraControleDeFluxo

		public int[] CamadaEnlaceDadosReceptoraJanelaDeslizanteUmBit(int quadro[]) {
			if( quadro == null){
					qtdReenvios++;
					//System.out.println("Quadro Chegou com Erro, Reenviando...");
					return null;
			}
			else{
							setvalueACK(ContAcks, quadro[quadro.length-1]);
							//System.out.println("Valor do Ackermann: "+ quadro[quadro.length-1]);
							//System.out.println("Quantidade de Reenvios do Quadro: "+ qtdReenvios);
							qtdReenvios = 0;
							ContAcks++;
					return quadro;
			}
	}// fim do metodo CamadaEnlaceDadosReceptoraJanelaDeslizanteUmBit

	public int[] CamadaEnlaceDadosReceptoraJanelaDeslizanteGoBackN(int quadro[]) {
		if( quadro == null){ // Quadro Chegou com Erro
				//System.out.println("Quadro [CHEGOU COM ERRO], Reenviando ele e seus Subsequentes...");
				return null;
		}
		else{ //Verifica Se os Quadros Anteriores chegaram Corretamente
				boolean AcceptQuadro = true;
				int[] AcksTotais = getBooleanACKvalue();
				if(quadro[quadro.length-1] != 0){
						for(int i = 0; i < quadro[quadro.length-1]; i++){
								if(AcksTotais[i] != i){
										AcceptQuadro = false;
								}
						}
				}
				if(AcceptQuadro){ // Caso tenham Chegado ele Envia o  Ack de volta ao Transmissor e  envia o Quadro
										setvalueACK(ContAcks, quadro[quadro.length-1]); // Setando valor do Ack
										//System.out.println("Valor do Ackermann: "+ quadro[quadro.length-1]);
										setTamanhoJanela(getTamanhoJanela()-1); // liberando a janela Deslizante

										if(quadro[quadro.length-1] == getTamanhoEnquadros()-1){
												setRetSELET(true); // Ultimo Quadro Confirmado, Exibir Mensagem na tela
										}
								ContAcks++;
								return quadro;
						}
						else{ // Caso n tenha Chegado, Descarta o Quadro
								//System.out.println("Quadro ["+ quadro[quadro.length-1] + "] Chegou Correto Mas seu/seus Anterior Não e foi descartado\n");
								return null;
						}
		}
}// fim do metodo CamadaEnlaceDadosReceptoraJanelaDeslizanteGoBackN

public int[] CamadaEnlaceDadosReceptoraJanelaDeslizanteComRetransmissaoSeletiva(int quadro[]) {
	if( quadro == null){
			//System.out.println("Quadro Chegou com Erro, Reenviando...");
			return null;
	}
	else{
					setvalueACK(quadro[quadro.length-1], quadro[quadro.length-1]); // Setando valor do Ack
					setindiceFluxoRetransmissaoSeletiva(ContAcks, quadro[quadro.length-1]);
					//System.out.println("Valor do Ackermann: "+ quadro[quadro.length-1]);
					setTamanhoJanela(getTamanhoJanela()-1); // liberando a janela Deslizante
					ContAcks++;
					if(ContAcks == getTamanhoEnquadros()){
							setRetSELET(true);
					}
			return quadro;
	}
}// fim do CamadaEnlaceDadosReceptoraJanelaDeslizanteComRetransmissaoSeletiva

public int[] CamadaEnlaceDadosReceptoraEnquadramento (int quadro []) {
	int tipoDeEnquadramento = cT.getEnquadramento(); //alterar de acordo com o teste
	if (caracteresAnterior % 4 == 0)
	 tamanhoDesenquadro = (caracteresAnterior / 4);
else
	tamanhoDesenquadro =((caracteresAnterior / 4) + 1);
	int quadroDesenquadrado [] = new int[tamanhoDesenquadro];

	switch (tipoDeEnquadramento) {
	case 0 : //contagem de caracteres
	quadroDesenquadrado = CamadaEnlaceDadosReceptoraEnquadramentoContagemDeCaracteres(quadro);
	break;
	}//fim do switch/case
	return quadroDesenquadrado;
	}//fim do metodo CamadaEnlaceDadosReceptoraEnquadramento

	public int[] CamadaEnlaceDadosReceptoraEnquadramentoContagemDeCaracteres (int quadro []) {
    int positionBit;
    StringBuilder stringBinaria = new StringBuilder(ExibirBinarioControleErro(quadro));
    StringBuilder Return = new StringBuilder();
    int verify = stringBinaria.length() - 8;
    for(int i = stringBinaria.length()-1; i>=0; i--){
      if(i >= verify){
        //Informacao de Controle
      }
      else{
        Return.insert(0, stringBinaria.toString().charAt(i));
      }
    }

    int[] quadroParcial = new int[(Return.length()/8)+1];
    positionBit = Return.length()-1;
    for(int j = 0; j <= Return.length()-1; j++){
      if (Return.toString().charAt(positionBit) == '1') {
      quadroParcial[j/32] = quadroParcial[j/32] | (1 << j);
      }
    positionBit--;
    }
    setQtdBitsInsercaoBits(Return.length());
    quadroParcial[quadroParcial.length-1] = quadro[quadro.length-1];
    
    return quadroParcial;
  }//fim do metodo CamadaEnlaceDadosReceptoraContagemDeCaracteres

	void CamadaDeAplicacaoReceptora(int quadro[]) {
		if(quadro != null){
		int deslocBit = 0;
		StringBuilder Mensagem = new StringBuilder();

		for (int i = 0; i < (getQtdBitsInsercaoBits()/8); i++) { // For ate o tamanho da Mensagem
			StringBuilder Char = new StringBuilder();
			for (int j = 0; j < 8; j++) { // For para cada Bit
				int mascara = 1 << deslocBit; // Mascara com bit 1 na Posicao deslocBit
				int Bit = (quadro[i / 4] & mascara) >> deslocBit; // Pega o Bit na posicao da Mascara&Quadro
				if (Bit == -1) {
					Bit = Bit * -1;
				}
				Char.insert(0, Bit); // insere o bit no caractere
				deslocBit++;
			}

			int aux = Integer.parseInt(Char.toString(), 2); // converte o binario em inteiro
			Mensagem.append((char) aux); // converte o inteiro em char
		}
		AplicacaoReceptora(Mensagem.toString());
		//System.out.println("Quadro Descriptografado: " + Mensagem.toString());
	}
	}// fim do metodo CamadaDeAplicacaoReceptora
	void AplicacaoReceptora(String mensagem) {
		if(cT.getControleFluxo() == 2 && mensagem != null){ // Verifica o tipo de Controle de fluxo
			setBufferMensagens(mensagem); // Salva em um Buffer a Msg de texto
			if(getRetSELET()){ // Chegou a Mensagem Completa
				ordenarArrayEArrayList(getFluxoRetransmissaoSeletiva(), getBufferMensagens());
			}
		}
		else{
			if(mensagem != null){
				String aux = getMensagemFinal();
				aux = aux + mensagem;
				setMensagemFinal(aux);
				if(getMensagemFinal().length() == tamanhoMsgTotal){
					System.out.println("Mensagem Final: "+ getMensagemFinal());
          cT.setaMENSAGEMtela(getMensagemFinal(), (int)getId());
					cT.resetReenvio();
				}
				}
			}
	}// fim do metodo AplicacaoReceptora














			///////////////////////////////////////////////////////////////////
			// METODOS E VARIAVEIS PARA AUXILIAR TODA A COMPUTACAO ENVOLVIDA //
			///////////////////////////////////////////////////////////////////
			int qtdVezesAparece = 0;
			int qtdVezesAparece2 = 0;
			int tamanhoEnquadros = 0;
			int TamanhoQuadro = 0;
			int NumCaracteres = 0;
			int inicialCaracteres = 0;
			int caracteresAnterior = 0;
			int qtdEnquadros = 0;
			int qtdBitsInsercaoBits = 0;
			int ContAcks = 0;
			int caracAux = 0;
			int tamanhoJanela = 0;
			int[] booleanACK;
			int[] qtdBitsControleFluxo;
			int miliTemporizador = 0;
			int[] fluxoRetransmissaoSeletiva;
			int qtdReenvios = 0;
			boolean RetransmissaoSelet = false;
			int tamanhoDesenquadro = 0;
			ArrayList<String> bufferMensagens = new ArrayList<>();
			String MensagemFinal = "";
			int tamanhoMsgTotal =0;
			boolean confirmationPersistente = false;
      		boolean confirmationAloha = false;
			boolean confirmacaoEnvioProximoQuadro = true;
			boolean ControleColisaoPpersist = true;
			boolean aguardoCSMACD = false;
			boolean reenvioCSMACD = false;



			public boolean getreenvioCSMACD(){
				return reenvioCSMACD;
			}
			public void setREENVIOCSMA(Boolean aux){
				reenvioCSMACD = aux;
			}

			public boolean getAguardoCSMACD(){
				return aguardoCSMACD;
			}
			public void setAguardoCSMA(Boolean aux){
				aguardoCSMACD = aux;
			}

			public void setNumCaracteres(int numCaracteres) {
				NumCaracteres = numCaracteres;
			}
			public void setTamanhoMsgTotal(int tamanhoMsgTotal) {
					this.tamanhoMsgTotal = tamanhoMsgTotal;
			}
			public int getTamanhoMsgTotal() {
					return tamanhoMsgTotal;
			}

			public int getNumCaracteres() {
				return NumCaracteres;
			}
			public boolean getControleColisaoPpersit(){
				return ControleColisaoPpersist;
			}

			public void setControleColisaoPpersist(Boolean value){
				ControleColisaoPpersist = value;
			}

			public int getInicialCaracteres() {
				return inicialCaracteres;
			}

			public void setInicialCaracteres(int inicialCaracteres) {
				this.inicialCaracteres = inicialCaracteres;
			}

			// define o tamanho do array, com base que cada array Binario cabe 4 caracteres
			// e Manchester 2 carac
			public int setTamanhoArray(int tipoDeCodificacao) {
				switch (tipoDeCodificacao) {
					case 0:
						if (NumCaracteres % 4 == 0)
							return (NumCaracteres / 4);
						else
							return ((NumCaracteres / 4) + 1);

					default:
						if (NumCaracteres % 2 == 0)
							return (NumCaracteres / 2);
						else
							return ((NumCaracteres / 2) + 1);
				}
			}

			public String charParaBinario(char caractere) { // Converte um Char em Binario
				StringBuilder binario = new StringBuilder(8);
				for (int i = 7; i >= 0; i--) { // For do tamanho dos Bits de um Caractere
					int bit = (caractere >> i) & 1; // desloca o bit do caractere direita do por i posicoes e aplica a mascara
					binario.append(bit); // concatena o bit na string
				}
				return binario.toString();
			}

			// define o tamanho do array de Enquadramento, com base que cada array Binario 4
			// Verifica tambem no case 1 o tamanho total do quadro, verificando quantas fags
			// Para Obter o Tamanho ideal do quadro
			public void setNumCaracteresEnquadramento(int tipoDeEnquadramento, int[] quadro) {
				switch (tipoDeEnquadramento) {
					case 0: // Contagem de Caracteres
						caracteresAnterior = NumCaracteres;
						if (NumCaracteres % 3 == 0) {
							setQtdEnquadros((NumCaracteres / 3));
							NumCaracteres = ((NumCaracteres / 3) + NumCaracteres);
						} else {
							setQtdEnquadros(((NumCaracteres / 3) + 1));
							NumCaracteres = ((NumCaracteres / 3) + (NumCaracteres + 1));
						}
						setQtdBitsInsercaoBits(NumCaracteres * 8);
						break; // Fim Contagem de Caracteres
					default:
						break;
				}
			}

			public int getQtdEnquadros() {
				return qtdEnquadros;
			}

			public void setQtdEnquadros(int qtdEnquadros) {
				this.qtdEnquadros = qtdEnquadros;
			}

			public void setQtdBitsInsercaoBits(int qtdBitsInsercaoBits) {
				this.qtdBitsInsercaoBits = qtdBitsInsercaoBits;
			}

			public int getQtdBitsInsercaoBits() {
				return qtdBitsInsercaoBits;
			}

			public int setDeslocamentoBIT(int controllerDeslocamento, int deslocamento) {
				if (controllerDeslocamento % 4 == 0) {
					deslocamento = 7;
				} else {
					deslocamento += 16;
				}
				return deslocamento;
			}

			// Define o Tamanho Ideal para o Array com informacao de controle de erros
			public int setNumBITScontroleErro(int tipoDeControle) {
				int tamanhoQuadro = 0;
				switch (tipoDeControle) {
					case 2:
						if ((getQtdBitsInsercaoBits() + 32) % 32 == 0) {
							tamanhoQuadro = ((getQtdBitsInsercaoBits() + 32) / 32);
						} else {
							tamanhoQuadro = (((getQtdBitsInsercaoBits() + 32) / 32) + 1);
						}
						caracAux = NumCaracteres;
						if ((getQtdBitsInsercaoBits() + 32) % 8 == 0) {
							NumCaracteres = (getQtdBitsInsercaoBits() + 32) / 8;
						} else {
							NumCaracteres = ((getQtdBitsInsercaoBits() + 32) / 8) + 1;
						}
						return tamanhoQuadro;
					case 3:
						caracAux = NumCaracteres;
						return 1; // Retorno do Tamanho é Definido no metodo do Controle

					default: // Bit Par ou Bit Impar
						if ((getQtdBitsInsercaoBits() + 1) % 32 == 0) {
							tamanhoQuadro = ((getQtdBitsInsercaoBits() + 1) / 32);
						} else {
							tamanhoQuadro = (((getQtdBitsInsercaoBits() + 1) / 32) + 1);
						}
						caracAux = NumCaracteres;
						if ((getQtdBitsInsercaoBits() + 1) % 8 == 0) {
							NumCaracteres = (getQtdBitsInsercaoBits() + 1) / 8;
						} else {
							NumCaracteres = ((getQtdBitsInsercaoBits() + 1) / 8) + 1;
						}

						return tamanhoQuadro;
					// Fim Bit Par ou Bit Impar
				}
			}

			public void setBooleanACK(int quantidade) {
				booleanACK = new int[quantidade];
				for (int i = 0; i < booleanACK.length; i++) {
					booleanACK[i] = -1;
				}
			}

			public String ExibirBinarioControleErro(int[] bits) { // exibe os valores binarios/bits do array
				StringBuilder Binario = new StringBuilder();
				for (int i = getQtdBitsInsercaoBits() - 1; i >= 0; i--) {
					int bitQuadro = i % 32;
					int mascara = 1 << bitQuadro;
					int Bit = (bits[i / 32] & mascara) >> bitQuadro; // Pega o Bit na posicao da Mascara&Quadro na posicao desloc
																														// Bit
					if (Bit == -1) {
						Bit = Bit * -1;
					}
					Binario.append(Bit); // insere o bit na string
				} // Fim For Bits
				return Binario.toString(); // retorna o binario
			}

			public String ExibirBinarioControleFluxo(int[] bits, int qtdBits) { // exibe os valores binarios/bits do array
				StringBuilder Binario = new StringBuilder();
				for (int i = qtdBits - 1; i >= 0; i--) {
					int bitQuadro = i % 32;
					int mascara = 1 << bitQuadro;
					int Bit = (bits[i / 32] & mascara) >> bitQuadro; // Pega o Bit na posicao da Mascara&Quadro na posicao desloc
																														// Bit
					if (Bit == -1) {
						Bit = Bit * -1;
					}
					Binario.append(Bit); // insere o bit na string
				} // Fim For Bits
				return Binario.toString(); // retorna o binario
			}

			public void setQtdBitsControleFluxo(int[] qtdBitsControleFluxo) {
				this.qtdBitsControleFluxo = qtdBitsControleFluxo;
			}

			public int[] getQtdBitsControleFluxo() {
				return qtdBitsControleFluxo;
			}

			// Metodo q Insere os bits de controle do Codigo de hamming
			public String codificarHamming(String dados) {
				int tamanhoEntrada = dados.length();
				StringBuilder dadoCodificado = new StringBuilder();

				// Loop para processar a entrada em segmentos de 4 bits
				// Devido estarmos usando a codificacao de hamming (7,4) existe
				// a necessidade de separar a entrada
				for (int i = 0; i < tamanhoEntrada; i += 4) {
					// Determina o tamanho do segmento atual (pode ser menor que 4 bits no último
					// segmento)
					int subTamanho = Math.min(4, tamanhoEntrada - i);
					String subEntrada = dados.substring(i, i + subTamanho);

					// Preenche com zeros à esquerda, se o segmento for menor que 4 bits
					while (subEntrada.length() < 4) {
						subEntrada = "0" + subEntrada;
					}

					// Converte o segmento de entrada em um array de inteiros (bits)
					int[] entradaBits = new int[4];
					for (int j = 0; j < 4; j++) {
						entradaBits[j] = Character.getNumericValue(subEntrada.charAt(j));
					}

					/*
					 * Os bits de paridade p1, p2 e p3 são calculados com base nos bits da
					 * entradaBits.
					 * Cada bit de paridade é calculado usando operações XOR (^)
					 * entre os bits correspondentes do segmento.
					 */
					int p1 = entradaBits[0] ^ entradaBits[1] ^ entradaBits[3];
					int p2 = entradaBits[0] ^ entradaBits[2] ^ entradaBits[3];
					int p3 = entradaBits[1] ^ entradaBits[2] ^ entradaBits[3];

					// Montagem da subSequência codificada
					dadoCodificado.append(p1).append(p2).append(entradaBits[0]).append(p3).append(entradaBits[1])
							.append(entradaBits[2]).append(entradaBits[3]);
				}

				// Retorna a sequência de saída completa
				return dadoCodificado.toString();
			}

			// Realiza a Decodificacao de Hamming
			public String decodificarHamming(String dadoRecebido) {
				int tamanhoEntrada = dadoRecebido.length();
				StringBuilder dadoDecodificado = new StringBuilder();

				for (int i = 0; i < tamanhoEntrada; i += 7) {
					// Determina o tamanho do segmento atual (pode ser menor que 7 bits no último
					// segmento)
					int subTamanho = Math.min(7, tamanhoEntrada - i);
					String subEntrada = dadoRecebido.substring(i, i + subTamanho);

					// Preenche com zeros à esquerda, se o segmento for menor que 7 bits
					while (subEntrada.length() < 7) {
						subEntrada = "0" + subEntrada;
					}

					// Converte a subEntrada em um array de inteiros (bits)
					int[] entradaBits = new int[7];
					for (int j = 0; j < 7; j++) {
						entradaBits[j] = Character.getNumericValue(subEntrada.charAt(j));
					}

					// Calcula os bits de paridade (p1, p2 e p3)
					int p1 = entradaBits[0] ^ entradaBits[2] ^ entradaBits[4] ^ entradaBits[6];
					int p2 = entradaBits[1] ^ entradaBits[2] ^ entradaBits[5] ^ entradaBits[6];
					int p3 = entradaBits[3] ^ entradaBits[4] ^ entradaBits[5] ^ entradaBits[6];

					// Calcula o índice do bit de erro
					int bitErro = p1 + p2 * 2 + p3 * 4;

					// Verifica se há erro na sequência
					if (bitErro > 0) {
						// Se houver erro, retorne null
						return null;
					}

					// Monta a subSequência decodificada
					dadoDecodificado.append(entradaBits[2]).append(entradaBits[4]).append(entradaBits[5]).append(entradaBits[6]);
				}

				// Retorna a sequência de saída completa
				return dadoDecodificado.toString();
			}

			// Método que realiza a Divisão Binária com a operação de Xor
			public String divisaoBinariaResto(String dividendo, String divisor) {
				// int comprimentoDividendo = dividendo.length();
				int comprimentoDivisor = divisor.length();
				// Inicializa o resto com o valor do dividendo
				StringBuilder resto = new StringBuilder(dividendo);
				// Loop principal para realizar a divisão binária
				while (resto.length() >= comprimentoDivisor) {
					// Verifica se o bit mais à esquerda do resto é '1'
					if (resto.charAt(0) == '1') {
						// Realiza a operação XOR bit a bit entre o resto e o divisor
						for (int i = 0; i < comprimentoDivisor; i++) {
							char bitAtual = resto.charAt(i);
							char bitDivisor = divisor.charAt(i);
							// Se os bits forem iguais, o resultado é '0', caso contrário, é '1'
							if (bitAtual == bitDivisor) {
								resto.setCharAt(i, '0');
							} else {
								resto.setCharAt(i, '1');
							}
						}
					} else {
						// Se o bit mais à esquerda do resto for '0', removemos esse bit
						resto.deleteCharAt(0);
					}
				}
				// Se o resto estiver vazio, o resultado é zero
				if (resto.length() == 0) {
					return "0";
				}
				// Retorna o resultado da divisão binária (resto)
				return resto.toString();
			}

			// metodo para alternar os valores do ACK no Deslizante 1 Bit
			public int alternaACK(int i) {
				if (i % 2 == 0) {
					return 0;
				} else {
					return 1;
				}
			}
			public void resetVariaveis(){
			tamanhoEnquadros = 0;
			TamanhoQuadro = 0;
			NumCaracteres = 0;
			inicialCaracteres = 0;
			caracteresAnterior = 0;
			qtdEnquadros = 0;
			qtdBitsInsercaoBits = 0;
			ContAcks = 0;
			caracAux = 0;
			tamanhoJanela = 0;
			 miliTemporizador = 0;
			qtdReenvios = 0;
			RetransmissaoSelet = false;
			tamanhoDesenquadro = 0;
			bufferMensagens = new ArrayList<>();
			MensagemFinal = "";
			tamanhoMsgTotal = 0;
			}

			public void setMiliTemporizador(int miliTemporizador) {
				this.miliTemporizador = miliTemporizador;
			}

			public int getMiliTemporizador() {
				return miliTemporizador;
			}

			public int getBooleanACK(int indice) {
				return booleanACK[indice];
			}

			public int getTamanhoJanela() {
				return tamanhoJanela;
			}

			public void setTamanhoJanela(int tamanhoJanela) {
				this.tamanhoJanela = tamanhoJanela;
			}

			public int getTamanhoEnquadros() {
				return tamanhoEnquadros;
			}

			public void setTamanhoEnquadros(int tamanhoEnquadros) {
				this.tamanhoEnquadros = tamanhoEnquadros;
			}

			public void setTamanhoFluxoRetransmissaoSeletiva(int tamanho) {
				fluxoRetransmissaoSeletiva = new int[tamanho];
			}

			public int[] getFluxoRetransmissaoSeletiva() {
				return fluxoRetransmissaoSeletiva;
			}

			public void setvalueACK(int indice, int value) {
				booleanACK[indice] = value;
			}

			public int[] getBooleanACKvalue() {
				return booleanACK;
			}

			public void setRetSELET(boolean cond) {
				RetransmissaoSelet = cond;
			}

			public void setindiceFluxoRetransmissaoSeletiva(int indice, int valor) {
				fluxoRetransmissaoSeletiva[indice] = valor;
			}

			public boolean getRetSELET() {
				return RetransmissaoSelet;
			}

			// Método para ordenar um array e um ArrayList correspondente
			public void ordenarArrayEArrayList(int[] array, ArrayList<String> arrayList) {
				// Crie uma lista de índices ordenados
				List<Integer> indicesOrdenados = new ArrayList<>();
				for (int i = 0; i < array.length; i++) {
					indicesOrdenados.add(i);
				}

				// Ordene a lista de indices com base nos valores correspondentes no array de
				// inteiros
				Collections.sort(indicesOrdenados, (a, b) -> Integer.compare(array[a], array[b]));

				// Crie uma nova lista de strings para armazenar os resultados ordenados
				ArrayList<String> arrayListOrdenado = new ArrayList<>(arrayList.size());

				// Preencha a lista ordenada usando os indices ordenados
				for (int i : indicesOrdenados) {
					arrayListOrdenado.add(arrayList.get(i));
				}

				// Atualize os parametros com os resultados ordenados
				for (int i = 0; i < array.length; i++) {
					array[i] = array[i];
				}
				arrayList.clear();
				arrayList.addAll(arrayListOrdenado);
				//System.out.println("<=======================================================> ");
				//System.out.println("Ordem que os ACK's Chegaram: " + Arrays.toString(array));
				// setEnquadArea("Ordem que os ACK's Chegaram: " + Arrays.toString(array));
				System.out.println("Mensagem Completa: "+ concatenarArrayList(arrayListOrdenado));
			}

			// Método para concatenar cada posicao de um ArrayList em uma string
			public static String concatenarArrayList(ArrayList<String> arrayList) {
				StringBuilder resultado = new StringBuilder();

				// Percorra o ArrayList e adicione cada elemento a string resultante
				for (String elemento : arrayList) {
					resultado.append(elemento);
				}

				// Converta a StringBuilder para uma string antes de retornar
				return resultado.toString();
			}

			public void setBufferMensagens(String Mensagem) {
				bufferMensagens.add(Mensagem);
			}

			public String getMensagemFinal() {
				return MensagemFinal;
			}
			public int timeAleatorioNaoPersist() {
				Random random = new Random();
				int numeroAleatorio = random.nextInt(1001) + 500; // Gera um número entre 0 e 1000 e adiciona 500
				return numeroAleatorio;
		}
		
      public int timeAleatorioALOHA(){
        Random random = new Random();
        int numeroAleatorio = random.nextInt(2501) + 1500;  // Gera entre 1500 e 4000
        return numeroAleatorio;
    }
    
			public void setMensagemFinal(String mensagemFinal) {
				MensagemFinal = mensagemFinal;
			}

			public void setTransmitindoTrue(){
				Platform.runLater(() -> {
					transmitindo.setVisible(true);
				});
			}
			public void setTransmitindoFalse(){
				Platform.runLater(() -> {
					transmitindo.setVisible(false);
				});
			}

			public ArrayList<String> getBufferMensagens() {
				return bufferMensagens;
			}
			public int getQtdVezesAparece() {
					return qtdVezesAparece;
			}
			public int getQtdVezesAparece2() {
					return qtdVezesAparece2;
			}

      public static int timeSinc() {
        Random random = new Random();
        int numeroAleatorio = random.nextInt(4);  // Gera entre 0 (inclusive) e 3
        return numeroAleatorio;
    }

		public static int Probabilidade50p() {
			Random random = new Random();
			int numeroAleatorio = random.nextInt(2);  // Gera 0 ou 1
			return numeroAleatorio;
	}
    

}