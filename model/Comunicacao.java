/* ***************************************************************
* Autor............: Carlos Gil Martins da Silva
* Matricula........: 202110261
* Inicio...........: 31/08/2023
* Ultima alteracao.: 05/09/2023
* Nome.............: Comunicacao
* Funcao...........: Recebe os Bits manipulados e passa para
o fluxo B que será passado para o Receptor logo em seguida, tambem
faz o controle e animação da onda de sinais por meio da utilizacao de
threads
****************************************************************/

package model;

import control.controllerPrincipal;

public class Comunicacao {

  controllerPrincipal cG = new controllerPrincipal(); // Instanciando e Criando o Controller
  // Metodo Utilizado para Setar um Controlador em Comum em Todas Thread
  public void setControlador(controllerPrincipal controle) {
    this.cG = controle;
  }

  int MaiorFluxo = 0;
  public void MeioDeComunicacao(int[] fluxoBrutoDeBits, TransmissorReceptor cT) {

    try { 
      cG.getSemaforoVerificadorColisao().acquire();
      for(int i =0; i < 5; i++){
        if(cG.getTransmissorUsandoMeio()[i] == true){ // se alguem ja esta usando o meio de transmissao
          cG.getTransmissorColidiu()[i] = true; // esse alguem colidiu com quem entrou
          cG.getTransmissorColidiu()[(int) cT.getId()-1] = true; // quem entrou colidiu com ele
          System.out.println("Houve Colisao :)");
          cG.setColisaoIMG(true);
        } 
      }
      cG.getTransmissorUsandoMeio()[(int) cT.getId()-1] = true; // Transmissor esta usando o meio nesse momento
      cG.getSemaforoVerificadorColisao().release();
    } 
    catch (InterruptedException e) {}
    if(cG.getControleDeAcessoAoMeio() == 5){ // CSMA CD, SE COLIDIR JA PARA AUTOMATICAMNETE
      try {
        Thread.sleep(200);
      } catch (InterruptedException e) {}
      if(cG.getTransmissorColidiu()[(int) cT.getId()-1] == true){
      System.out.println("Colisao no Transmissor ["+ cT.getId() + "] Inicialmente e foi Interrompida");
       cT.setAguardoCSMA(true); // Libera para verfificar de Retransmissao ou nao
       cT.setREENVIOCSMA(true); // Informa que colidiu e pede para Retransmitir
       cG.setColisaoIMG(false);
      }
      else{
        cT.setAguardoCSMA(true); // Libera para verfificar de Retransmissao ou nao
       cT.setREENVIOCSMA(false); // Informa que colidiu e pede para Retransmitir
    int[] fluxoBrutoDeBitsPontoA;
    int[] fluxoBrutoDeBitsPontoB = new int[fluxoBrutoDeBits.length]; // Seta o tamanho do Array
    fluxoBrutoDeBitsPontoA = fluxoBrutoDeBits;
    //System.out.println("\nQuadro Enviado : " + cT.ExibirBinarioControleErro(fluxoBrutoDeBitsPontoA));
    new Thread(() -> {
      switch(cG.getCodificacao()){
        case 0: // SETA A FORMA DE ONDA BINARIA
          for (int i = cT.getQtdBitsInsercaoBits()-1; i >= 0; i--) {
            int numeroRandom = (int) (Math.random() * 100) + 1; // Gera um número entre 1 (inclusive) e 100 (inclusive)
            int bitQuadro = i % 32;
            int mascara = 1 << bitQuadro;

            int Bit = (fluxoBrutoDeBitsPontoA[i / 32] & mascara) >> bitQuadro; // Pega o Bit
            
            if (numeroRandom <= cG.getTaxaErro()) { // If que verifica se vai ter erro ou nao
              // Estrutura de IF que manipula bit por Bit
              if (Bit == 1 || Bit == -1) {
                Bit = 0;
                fluxoBrutoDeBitsPontoB[i / 32] = fluxoBrutoDeBitsPontoB[i / 32] | (0 << bitQuadro);
              } else {
                Bit = 1;
                fluxoBrutoDeBitsPontoB[i / 32] = fluxoBrutoDeBitsPontoB[i / 32] | (1 << bitQuadro);
              }
            }
            else if (Bit == 1 || Bit == -1) {
              Bit = 1;
                fluxoBrutoDeBitsPontoB[i / 32] = fluxoBrutoDeBitsPontoB[i / 32] | (1 << bitQuadro);
            }
            cG.adiantaSignal(cG.getCodificacao()); // Adianta os Sinais Mostrados na Tela
            cG.updateSignalBinario(Bit, bitQuadro); // Seta o bit pego do Array no Sinal da Tela
            try {
              Thread.sleep(11); // 
            } catch (InterruptedException e) {
            }
          } // Fim For Bits
        break;


        default: // SETA A FORMA DE ONDA MANCHESTER DIFERENCIAL OU NORMAL       
        // SETANDO A FORMA DE ONDA
        int controladorParBits = 0;
        boolean booleanParBits = false;
        StringBuilder parBit = new StringBuilder();
            for (int i = cT.getQtdBitsInsercaoBits()-1; i >= 0; i--) {
              controladorParBits++;
              if(controladorParBits % 2 == 0 && controladorParBits != 0){
                booleanParBits = true;
                controladorParBits = 0;
              }
              int numeroRandom = (int) (Math.random() * 100) + 1; // Gera um número entre 1 (inclusive) e 100 (inclusive)
              int bitQuadro = i % 32;
              int mascara = 1 << bitQuadro;
              int Bit = (fluxoBrutoDeBitsPontoA[i / 32] & mascara) >> bitQuadro; // Pega o Bit
              if (numeroRandom <= cG.getTaxaErro()) { // If que verifica se vai ter erro ou nao
                // Estrutura de IF que manipula bit por Bit
                if (Bit == 1 || Bit == -1) {
                  Bit = 0;
                  fluxoBrutoDeBitsPontoB[i / 32] = fluxoBrutoDeBitsPontoB[i / 32] | (0 << bitQuadro);
                } else {
                  Bit = 1;
                  fluxoBrutoDeBitsPontoB[i / 32] = fluxoBrutoDeBitsPontoB[i / 32] | (1 << bitQuadro);
                }
              }
              else if (Bit == 1 || Bit == -1) {
                Bit = 1;
                  fluxoBrutoDeBitsPontoB[i / 32] = fluxoBrutoDeBitsPontoB[i / 32] | (1 << bitQuadro);
              }
            parBit.append(Bit); // Insere o Bit no parBit que será um par de Bits
            if(booleanParBits){
              cG.adiantaSignal(cG.getCodificacao()); // Adianta os Sinais Mostrados na Tela
              cG.updateSignalManchester(parBit.toString()); // Seta o bit pego do Array no Sinal da Tela
              booleanParBits = false;
              parBit = new StringBuilder();
            }
            try {
              Thread.sleep(11); // Sleep com o valor do Slider
            } catch (InterruptedException e) {
            }
          } // Fim For Bits
          break;
        } // Fim Switch
        //System.out.println("Quadro Recebido: " + cT.ExibirBinarioControleErro(fluxoBrutoDeBitsPontoB));
        fluxoBrutoDeBitsPontoB[fluxoBrutoDeBitsPontoB.length-1] = fluxoBrutoDeBitsPontoA[fluxoBrutoDeBitsPontoA.length-1];
        cG.disableWave();
        
        try {
          cG.getSemaforoVerificadorColisao().acquire();
          int count = 0;
          cG.getTransmissorUsandoMeio()[(int) cT.getId()-1] = false; // Transmissor não está mais usando o meio
          for (int i = 0; i < 5; i++) {
              if (cG.getTransmissorUsandoMeio()[i]) { // Verifica se tem alguém usando o meio
                  count++; // Incrementando os transmissores no meio
              }
          }
          if (count < 2) { // Existe apenas 1 transmissor usando o meio nesse momento
              cG.setColisaoIMG(false); // Desativa a imagem de colisão
          }
          // Se o Controle de Acesso ao Meio for o CSMA/CD  e ele tiver colidido, ele descarta logo o quadro
          // Descartando Abrupdamente e economizando tempo e largura de banda
          if(cG.getControleDeAcessoAoMeio() == 5 && cG.getTransmissorColidiu()[(int) cT.getId()-1] == true){
            
          }
      } catch (InterruptedException e) {
          // Tratar exceção
      } finally {
          cG.getSemaforoVerificadorColisao().release(); // Liberar semáforo mesmo em caso de exceção
      }
      
      cT.CamadaFisicaReceptora(fluxoBrutoDeBitsPontoB); // Chama o Receptor
    }).start(); // Fim thread
      }

    } // Fim IF
    else{
    int[] fluxoBrutoDeBitsPontoA;
    int[] fluxoBrutoDeBitsPontoB = new int[fluxoBrutoDeBits.length]; // Seta o tamanho do Array
    fluxoBrutoDeBitsPontoA = fluxoBrutoDeBits;
    //System.out.println("\nQuadro Enviado : " + cT.ExibirBinarioControleErro(fluxoBrutoDeBitsPontoA));
    new Thread(() -> {
      switch(cG.getCodificacao()){
        case 0: // SETA A FORMA DE ONDA BINARIA
          for (int i = cT.getQtdBitsInsercaoBits()-1; i >= 0; i--) {
            int numeroRandom = (int) (Math.random() * 100) + 1; // Gera um número entre 1 (inclusive) e 100 (inclusive)
            int bitQuadro = i % 32;
            int mascara = 1 << bitQuadro;

            int Bit = (fluxoBrutoDeBitsPontoA[i / 32] & mascara) >> bitQuadro; // Pega o Bit
            
            if (numeroRandom <= cG.getTaxaErro()) { // If que verifica se vai ter erro ou nao
              // Estrutura de IF que manipula bit por Bit
              if (Bit == 1 || Bit == -1) {
                Bit = 0;
                fluxoBrutoDeBitsPontoB[i / 32] = fluxoBrutoDeBitsPontoB[i / 32] | (0 << bitQuadro);
              } else {
                Bit = 1;
                fluxoBrutoDeBitsPontoB[i / 32] = fluxoBrutoDeBitsPontoB[i / 32] | (1 << bitQuadro);
              }
            }
            else if (Bit == 1 || Bit == -1) {
              Bit = 1;
                fluxoBrutoDeBitsPontoB[i / 32] = fluxoBrutoDeBitsPontoB[i / 32] | (1 << bitQuadro);
            }
            cG.adiantaSignal(cG.getCodificacao()); // Adianta os Sinais Mostrados na Tela
            cG.updateSignalBinario(Bit, bitQuadro); // Seta o bit pego do Array no Sinal da Tela
            try {
              Thread.sleep(11); // 
            } catch (InterruptedException e) {
            }
          } // Fim For Bits
        break;


        default: // SETA A FORMA DE ONDA MANCHESTER DIFERENCIAL OU NORMAL       
        // SETANDO A FORMA DE ONDA
        int controladorParBits = 0;
        boolean booleanParBits = false;
        StringBuilder parBit = new StringBuilder();
            for (int i = cT.getQtdBitsInsercaoBits()-1; i >= 0; i--) {
              controladorParBits++;
              if(controladorParBits % 2 == 0 && controladorParBits != 0){
                booleanParBits = true;
                controladorParBits = 0;
              }
              int numeroRandom = (int) (Math.random() * 100) + 1; // Gera um número entre 1 (inclusive) e 100 (inclusive)
              int bitQuadro = i % 32;
              int mascara = 1 << bitQuadro;
              int Bit = (fluxoBrutoDeBitsPontoA[i / 32] & mascara) >> bitQuadro; // Pega o Bit
              if (numeroRandom <= cG.getTaxaErro()) { // If que verifica se vai ter erro ou nao
                // Estrutura de IF que manipula bit por Bit
                if (Bit == 1 || Bit == -1) {
                  Bit = 0;
                  fluxoBrutoDeBitsPontoB[i / 32] = fluxoBrutoDeBitsPontoB[i / 32] | (0 << bitQuadro);
                } else {
                  Bit = 1;
                  fluxoBrutoDeBitsPontoB[i / 32] = fluxoBrutoDeBitsPontoB[i / 32] | (1 << bitQuadro);
                }
              }
              else if (Bit == 1 || Bit == -1) {
                Bit = 1;
                  fluxoBrutoDeBitsPontoB[i / 32] = fluxoBrutoDeBitsPontoB[i / 32] | (1 << bitQuadro);
              }
            parBit.append(Bit); // Insere o Bit no parBit que será um par de Bits
            if(booleanParBits){
              cG.adiantaSignal(cG.getCodificacao()); // Adianta os Sinais Mostrados na Tela
              cG.updateSignalManchester(parBit.toString()); // Seta o bit pego do Array no Sinal da Tela
              booleanParBits = false;
              parBit = new StringBuilder();
            }
            try {
              Thread.sleep(11); // Sleep com o valor do Slider
            } catch (InterruptedException e) {
            }
          } // Fim For Bits
          break;
        } // Fim Switch
        //System.out.println("Quadro Recebido: " + cT.ExibirBinarioControleErro(fluxoBrutoDeBitsPontoB));
        fluxoBrutoDeBitsPontoB[fluxoBrutoDeBitsPontoB.length-1] = fluxoBrutoDeBitsPontoA[fluxoBrutoDeBitsPontoA.length-1];
        cG.disableWave();
        
        try {
          cG.getSemaforoVerificadorColisao().acquire();
          int count = 0;
          cG.getTransmissorUsandoMeio()[(int) cT.getId()-1] = false; // Transmissor não está mais usando o meio
          for (int i = 0; i < 5; i++) {
              if (cG.getTransmissorUsandoMeio()[i]) { // Verifica se tem alguém usando o meio
                  count++; // Incrementando os transmissores no meio
              }
          }
          if (count < 2) { // Existe apenas 1 transmissor usando o meio nesse momento
              cG.setColisaoIMG(false); // Desativa a imagem de colisão
          }
          // Se o Controle de Acesso ao Meio for o CSMA/CD  e ele tiver colidido, ele descarta logo o quadro
          // Descartando Abrupdamente e economizando tempo e largura de banda
          if(cG.getControleDeAcessoAoMeio() == 5 && cG.getTransmissorColidiu()[(int) cT.getId()-1] == true){
            
          }
      } catch (InterruptedException e) {
          // Tratar exceção
      } finally {
          cG.getSemaforoVerificadorColisao().release(); // Liberar semáforo mesmo em caso de exceção
      }
      
      cT.CamadaFisicaReceptora(fluxoBrutoDeBitsPontoB); // Chama o Receptor
    }).start(); // Fim thread
    }

  } // Fim meio de Comunic
  }// fim do metodo MeioDeTransmissao