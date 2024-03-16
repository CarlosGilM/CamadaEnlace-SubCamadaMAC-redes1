/* ***************************************************************
* Autor............: Carlos Gil Martins da Silva
* Matricula........: 202110261
* Inicio...........: 08/10/2023
* Ultima alteracao.: 13/10/2023
* Nome.............: Controller Principal
* Funcao...........: Controla toda a a parte de interface, por
meio de imagens, botoes e tudo que se faz necessario
****************************************************************/

package control;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Semaphore;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.Comunicacao;
import model.TransmissorReceptor;

public class controllerPrincipal implements Initializable {

  @FXML private ChoiceBox<String> boxSelect;
  @FXML private ChoiceBox<String> boxSelect2;
  @FXML private ChoiceBox<String> boxSelectControle;
  @FXML private ChoiceBox<Integer> boxSelectTaxa;
  @FXML private ChoiceBox<String> fluxoSelect;
  @FXML private ChoiceBox<String> acessoSelect;
  @FXML private ImageView startScreen;
  @FXML private Button buttonSelect;
  @FXML private ImageView mainScreen;
  @FXML private ImageView buttonSend;
  @FXML private TextArea textBox1;
  @FXML private TextArea textBox2;
  @FXML private TextArea textBox3;
  @FXML private TextArea textBox4;
  @FXML private TextArea textBox5;
  @FXML private TextArea finalText;
  @FXML private ImageView transmitindo1;
  @FXML private ImageView transmitindo2;
  @FXML private ImageView transmitindo3;
  @FXML private ImageView transmitindo4;
  @FXML private ImageView transmitindo5;
  @FXML private ImageView imgColisao;
  // Imagens dos Sinais
  @FXML private ImageView signalAlto1;
  @FXML private ImageView signalAlto2;
  @FXML private ImageView signalAlto3;
  @FXML private ImageView signalAlto4;
  @FXML private ImageView signalAlto5;
  @FXML private ImageView signalAlto6;
  @FXML private ImageView signalAlto7;
  @FXML private ImageView signalAlto8;
  @FXML private ImageView signalBaixo1;
  @FXML private ImageView signalBaixo2;
  @FXML private ImageView signalBaixo3;
  @FXML private ImageView signalBaixo4;
  @FXML private ImageView signalBaixo5;
  @FXML private ImageView signalBaixo6;
  @FXML private ImageView signalBaixo7;
  @FXML private ImageView signalBaixo8;
  @FXML private ImageView signalPe1;
  @FXML private ImageView signalPe2;
  @FXML private ImageView signalPe3;
  @FXML private ImageView signalPe4;
  @FXML private ImageView signalPe5;
  @FXML private ImageView signalPe6;
  @FXML private ImageView signalPe7;
  @FXML private ImageView signalPe8;
  @FXML private ImageView manchester1;
  @FXML private ImageView manchester2;
  @FXML private ImageView manchester3;
  @FXML private ImageView manchester4;
  @FXML private ImageView manchester5;
  @FXML private ImageView manchester6;
  @FXML private ImageView manchester7;
  @FXML private ImageView manchester8;
  @FXML private ImageView manchester9;
  @FXML private ImageView manchester10;
  @FXML private ImageView manchester11;
  @FXML private ImageView manchester12;
  @FXML private ImageView manchester13;
  @FXML private ImageView manchester14;
  @FXML private ImageView manchester15;
  @FXML private ImageView manchester16;
  @FXML private ImageView buttonCodif;

  int codificacao = -1; // codificacao escolhida
  int enquadramento = 3; // Enquadramento escolhido
  int controleErro = 0; // Controle de Erro Escolhido
  int taxaErro = 0; // taxa de erro escolhida
  int controleFluxo = 0; // fluxo escolhido
  int controleDeAcessoAoMeio = 0; //acesso ao meio
  int contagemTransmissores = 0;
  
  // variaveis de controle/comparacao
  String comparationSignal = "1";
  int comparationBinary = 0;
  Boolean contrBoolean = false;
  Boolean contrVisualizacao = false;
  Boolean escutaMeioTransmissao = true; //Liberado Para Transmitir
  Boolean[] CsmaCDColision;
  Boolean[] verificandoColisao;
  
  String MensagemFinal = "";
  int miliTemporizador = 0;
  boolean controlVisu = true;
  boolean exibeDados = true;

  // Array de imagens, para setar as ondas
  ImageView[] arraySignalBaixo;
  ImageView[] arraySignalAlto;
  ImageView[] arraySignalPe;
  ImageView[] arraySignalManchester0;
  ImageView[] arraySignalManchester1;
  int sleep = 300;

  TransmissorReceptor Tr1;
  TransmissorReceptor Tr2;
  TransmissorReceptor Tr3;
  TransmissorReceptor Tr4;
  TransmissorReceptor Tr5;
  Comunicacao Cm;

    Semaphore SemaforoVerificadorColisao = new Semaphore(1); // semaforo para proteger variavel compartilhada
  //que sera incrementada toda vez q um transmissor entrar no meio de tranmissao
  boolean [] TransmissorUsandoMeio = new boolean[5]; // quais estao usando o meio no momento
  boolean [] TransmissorColidiu = new boolean[5]; //quais colidiram ou nao

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    // Setando as imagens em cada array correspondente e as opcoes de codificacao
    boxSelect.getItems().addAll("Binaria", "Manchester", "Manchester Diferencial");
    boxSelect2.getItems().addAll("Contagem de Caracteres");
    boxSelectControle.getItems().addAll("CRC", "Hamming");
    fluxoSelect.getItems().addAll("Janela Deslizante Um Bit");
    acessoSelect.getItems().addAll("Aloha Puro", "Slotted Aloha", "CSMA Nao Persistente", "CSMA Persistente", "CSMA P Persistente", "CSMA CD");
    boxSelectTaxa.getItems().addAll(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    ImageView[] SuportB = { signalBaixo1, signalBaixo2, signalBaixo3, signalBaixo4, signalBaixo5, signalBaixo6,
        signalBaixo7, signalBaixo8 };
    arraySignalBaixo = SuportB;

    ImageView[] SuportA = { signalAlto1, signalAlto2, signalAlto3, signalAlto4, signalAlto5, signalAlto6, signalAlto7,
        signalAlto8 };
    arraySignalAlto = SuportA;

    ImageView[] SuportP = { signalPe1, signalPe2, signalPe3, signalPe4, signalPe5, signalPe6, signalPe7, signalPe8 };
    arraySignalPe = SuportP;

    ImageView[] SuportM0 = { manchester1, manchester2, manchester3, manchester4, manchester5, manchester6, manchester7,
        manchester8 };
    arraySignalManchester0 = SuportM0;

    ImageView[] SuportM1 = { manchester9, manchester10, manchester11, manchester12, manchester13, manchester14,
        manchester15, manchester16 };
    arraySignalManchester1 = SuportM1;

    Cm = new Comunicacao();
    Cm.setControlador(this);

  }

  public Comunicacao getCm() {
    return Cm;
  }

  public Boolean[] getCsmaCDColision() {
      return CsmaCDColision;
  }
  public Boolean[] getVerificandoColisao() {
      return verificandoColisao;
  }
  public void setCsmaCDColision(Boolean[] csmaCDColision) {
      CsmaCDColision = csmaCDColision;
  }

  @FXML
  void clickSelected(MouseEvent event) { // Metodo para selecionar o tipo de codificacao
    if (boxSelect.getValue() != null && boxSelect2.getValue() != null && boxSelectControle.getValue() != null
        && boxSelectTaxa.getValue() != null && fluxoSelect.getValue() != null && acessoSelect.getValue() != null) {
      switch (boxSelect.getValue()) {
        case "Binaria":
          codificacao = 0;
          break;
        case "Manchester":
          codificacao = 1;
          break;
        case "Manchester Diferencial":
          codificacao = 2;
          break;
      } // fim Switch
      switch (boxSelectControle.getValue()) {
        case "CRC":
          controleErro = 2;
          break;
        case "Hamming":
          controleErro = 3;
          break;
      } // fim Switch
      switch (boxSelect2.getValue()) {
        case "Contagem de Caracteres":
          enquadramento = 0;
          break;
      } // fim Switch
      switch (fluxoSelect.getValue()) {
        case "Janela Deslizante Um Bit":
          controleFluxo = 0;
          break;
      } // fim Switch
      switch (acessoSelect.getValue()) {
        case "Aloha Puro":
          controleDeAcessoAoMeio = 0;
          break;
        case "Slotted Aloha":
          controleDeAcessoAoMeio = 1;
          break;
        case "CSMA Nao Persistente":
          controleDeAcessoAoMeio = 2;
          break;
        case "CSMA Persistente":
          controleDeAcessoAoMeio = 3;
          break;
        case "CSMA P Persistente":
          controleDeAcessoAoMeio = 4;
          break;
        case "CSMA CD":
          controleDeAcessoAoMeio = 5;
          break;
      } // fim Switch
      taxaErro = (boxSelectTaxa.getValue());
      if (codificacao == 0 && enquadramento == 3) {
        Alert alert = new Alert(AlertType.WARNING);
        alert.setTitle("Aviso");
        alert.setHeaderText("Ocorreu um erro!");
        alert.setContentText(
            "Devido a Sua Implementacao, O metodo de Enquadramento Violacao da Camada Fisica nao funciona com o tipo de codificaco binaria, selecione qualquer outra combinacao");

        alert.showAndWait();
      } else {
        disableStartScreen(); // desativa a tela inicial
      }
    } // fim if
    else {
      System.out.println("Selecione uma opcao");
    }
  }

  public void resetReenvio(){
    contagemTransmissores++;
    if(contagemTransmissores == 5){
      contagemTransmissores = 0;
      disableButtons();
    }
  }

  public void setMiliTemporizador(int miliTemporizador) {
    this.miliTemporizador = miliTemporizador;
  }

  public int getMiliTemporizador() {
    return miliTemporizador;
  }

  public void setFinalText(String RespostaFinal) {
    finalText.setText(RespostaFinal);
  }

  public void setMensagemFinal(String mensagemFinal) {
    MensagemFinal = mensagemFinal;
  }

  public Boolean getEscutaMeioTransmissao() {
      return escutaMeioTransmissao;
  }
  public void setEscutaMeioTransmissao(Boolean escutaMeioTransmissao) {
      this.escutaMeioTransmissao = escutaMeioTransmissao;
  }

  public Semaphore getSemaforoVerificadorColisao() {
      return SemaforoVerificadorColisao;
  }
  public boolean[] getTransmissorColidiu() {
      return TransmissorColidiu;
  }
  public boolean[] getTransmissorUsandoMeio() {
      return TransmissorUsandoMeio;
  }

  @FXML
  void clickButtonSend(MouseEvent event) { // Metodo quando clicar no botão de enviar a mensagem
    System.out.println("<<< --- Controle de Acesso ao Meio Escolhido --- >>> ["+ acessoSelect.getValue() + "]");
    textBox1.clear();
    textBox2.clear();
    textBox3.clear();
    textBox4.clear();
    textBox5.clear();

    Tr1 = new TransmissorReceptor(1, transmitindo1, "UesbT1");
    Tr1.setControlador(this);
    Tr1.start();

    Tr2 = new TransmissorReceptor(2, transmitindo2, "UesbT2");
    Tr2.setControlador(this);
    Tr2.start();

    Tr3 = new TransmissorReceptor(3, transmitindo3, "UesbT3");
    Tr3.setControlador(this);
    Tr3.start();
    
    Tr4 = new TransmissorReceptor(4, transmitindo4, "UesbT4");
    Tr4.setControlador(this);
    Tr4.start();
    
    Tr5 = new TransmissorReceptor(5, transmitindo5, "UesbT5");
    Tr5.setControlador(this);
    Tr5.start();


    disableButtons(); // desativa os botoes
    setMensagemFinal("");
    for (int i = 0; i < 8; i++) {
      arraySignalAlto[i].setVisible(false);
      arraySignalBaixo[i].setVisible(false);
      arraySignalPe[i].setVisible(false);
      arraySignalManchester0[i].setVisible(false);
      arraySignalManchester1[i].setVisible(false);
    }
  }

  public void setColisaoIMG(boolean bool){
    Platform.runLater(() -> {
    imgColisao.setVisible(bool);
        });
  }

  public void setTransmissor(int id) {
    Platform.runLater(() -> {
      switch (id) {
        case 1:
          transmitindo1.setVisible(!transmitindo1.isVisible());
          break;
        case 2:
          transmitindo2.setVisible(!transmitindo2.isVisible());
          break;
        case 3:
          transmitindo3.setVisible(!transmitindo3.isVisible());
          break;
        case 4:
          transmitindo4.setVisible(!transmitindo4.isVisible());
          break;
        case 5:
          transmitindo5.setVisible(!transmitindo5.isVisible());
          break;

        default:
          break;
      }
    });
  }

  public void exibeDados(String aux){
    if(Tr1.getQtdVezesAparece() == 0){
      System.out.println(aux);
    }
    else{
      if(Tr1.getQtdVezesAparece2() == 0){
        System.out.println(aux);
      }
    }
  }

  public void disableButtons() { // Desativa/ativa os botoes
    if (!contrVisualizacao) {
      buttonSend.setVisible(false);
      buttonSend.setDisable(true);
      buttonCodif.setVisible(false);
      buttonCodif.setDisable(true);
      contrVisualizacao = !contrVisualizacao;
    } else {
      buttonSend.setVisible(true);
      buttonSend.setDisable(false);
      buttonCodif.setVisible(true);
      buttonCodif.setDisable(false);
      contrVisualizacao = !contrVisualizacao;
    }
  }
  public int getControleDeAcessoAoMeio() {
      return controleDeAcessoAoMeio;
  }

  public void updateSignalBinario(int bit, int deslocBit) { // Atualiza o sinal da onda binario
    Platform.runLater(() -> {
      // seta tudo como false
      arraySignalAlto[0].setVisible(false);
      arraySignalBaixo[0].setVisible(false);
      arraySignalPe[0].setVisible(false);

      // caso haja transicao ele ativa a posicao em pe
      if (comparationBinary != bit && deslocBit != 0) {
        arraySignalPe[0].setVisible(true);
      }
      // ativa o signal com base no bit
      if (bit == 0) {
        arraySignalBaixo[0].setVisible(true);
      } else {
        arraySignalAlto[0].setVisible(true);
      }
      comparationBinary = bit;
    });
  }

  public void disableWave() { // Atualiza o sinal da onda binario
    for (int i = 0; i < 8; i++) {
      arraySignalAlto[i].setVisible(false);
      arraySignalBaixo[i].setVisible(false);
      arraySignalPe[i].setVisible(false);
      arraySignalManchester0[i].setVisible(false);
      arraySignalManchester1[i].setVisible(false);
    }
  }


  public void updateSignalManchester(String bit) { // Atualiza o sinal da onda Manchester
    Platform.runLater(() -> {
      // seta tudo false
      arraySignalManchester0[0].setVisible(false);
      arraySignalManchester1[0].setVisible(false);
      arraySignalPe[0].setVisible(false);
      arraySignalAlto[0].setVisible(false);

      // compara para ver a necessidade de setar visualizacao do sinal de transicao
      if (bit.equals("11")) {
        if (comparationSignal.equals("01"))
          arraySignalPe[0].setVisible(true);
      }
      if (comparationSignal.equals("11")) {
        if (bit.equals("10")) {
          arraySignalPe[0].setVisible(true);
        }
      } else {
        if (comparationSignal.equals(bit)) {
          arraySignalPe[0].setVisible(true);
        }
      }
      // compara o par de bits recebido
      if (bit.equals("01")) {
        arraySignalManchester0[0].setVisible(true);
      } else if (bit.equals("10")) {
        arraySignalManchester1[0].setVisible(true);
      } else {
        arraySignalAlto[0].setVisible(true);
      }

      comparationSignal = bit;
    });
  }

  public void adiantaSignal(int codificacao) { // avanca o sinal 1 posicao a frente
    Platform.runLater(() -> {
      // atualiza a ultima posicao do array com base na sua anterior, ou seja
      // se estamos na posicao 7, seta a visualizao dela com base na visualizacao da
      // posicao 6
      switch (codificacao) {
        case 0:
          for (int i = 7; i > 0; i--) {
            arraySignalAlto[i].setVisible(arraySignalAlto[i - 1].isVisible());
            arraySignalBaixo[i].setVisible(arraySignalBaixo[i - 1].isVisible());
            arraySignalPe[i].setVisible(arraySignalPe[i - 1].isVisible());
          }
          break;
        case 1:
          for (int i = 7; i > 0; i--) {
            arraySignalManchester0[i].setVisible(arraySignalManchester0[i - 1].isVisible());
            arraySignalManchester1[i].setVisible(arraySignalManchester1[i - 1].isVisible());
            arraySignalPe[i].setVisible(arraySignalPe[i - 1].isVisible());
            arraySignalAlto[i].setVisible(arraySignalAlto[i - 1].isVisible());
          }
          break;
        case 2:
          for (int i = 7; i > 0; i--) {
            arraySignalManchester0[i].setVisible(arraySignalManchester0[i - 1].isVisible());
            arraySignalManchester1[i].setVisible(arraySignalManchester1[i - 1].isVisible());
            arraySignalPe[i].setVisible(arraySignalPe[i - 1].isVisible());
            arraySignalAlto[i].setVisible(arraySignalAlto[i - 1].isVisible());
          }
          break;
      }
    });
  }

  public void disableStartScreen() { // desativa a tela principal e tudo que participa dela
    acessoSelect.setVisible(false);
    acessoSelect.setDisable(true);

    fluxoSelect.setVisible(false);
    fluxoSelect.setDisable(true);

    boxSelect2.setVisible(false);
    boxSelect2.setDisable(true);
    boxSelect.setVisible(false);
    boxSelect.setDisable(true);
    boxSelectControle.setVisible(false);
    boxSelectControle.setDisable(true);
    boxSelectTaxa.setVisible(false);
    boxSelectTaxa.setDisable(true);

    startScreen.setVisible(false);
    startScreen.setDisable(true);

    buttonSelect.setVisible(false);
    buttonSelect.setDisable(true);
  }

  @FXML
  void alterarCodificacao(MouseEvent event) { // ativa a tela inicial e reseta todos valores ao inicio
    acessoSelect.setVisible(true);
    acessoSelect.setDisable(false);

    fluxoSelect.setVisible(true);
    fluxoSelect.setDisable(false);

    boxSelect2.setVisible(true);
    boxSelect2.setDisable(false);
    boxSelect.setVisible(true);
    boxSelect.setDisable(false);

    boxSelectControle.setVisible(true);
    boxSelectControle.setDisable(false);
    boxSelectTaxa.setVisible(true);
    boxSelectTaxa.setDisable(false);

    startScreen.setVisible(true);
    startScreen.setDisable(false);

    buttonSelect.setVisible(true);
    buttonSelect.setDisable(false);

    for (int i = 0; i < 8; i++) {
      arraySignalAlto[i].setVisible(false);
      arraySignalBaixo[i].setVisible(false);
      arraySignalPe[i].setVisible(false);
      arraySignalManchester0[i].setVisible(false);
      arraySignalManchester1[i].setVisible(false);
    }

    setMensagemFinal("");
    textBox1.clear(); // limpa o texto digitado
    textBox2.clear(); // limpa o texto digitado
    textBox3.clear(); // limpa o texto digitado
    textBox4.clear(); // limpa o texto digitado
    textBox5.clear(); // limpa o texto digitado
  }

  public int getCodificacao() {
    return codificacao;
  }

public void setaMENSAGEMtela(String Mensagem, int id) {
  TextArea[] textAreas = new TextArea[5];
  textAreas[0] = textBox1;
  textAreas[1] = textBox2;
  textAreas[2] = textBox3;
  textAreas[3] = textBox4;
  textAreas[4] = textBox5;

    for (int i=0; i<5; i++) {
      textAreas[i].clear();
        if (i != id-1) {
            textAreas[i].setText(Mensagem);
        }
    }
}


  public int getControleErro() {
    return controleErro;
  }

  public int getControleFluxo() {
    return controleFluxo;
  }

  public int getEnquadramento() {
    return enquadramento;
  }

  public int getTaxaErro() {
    return taxaErro;
  }
}
