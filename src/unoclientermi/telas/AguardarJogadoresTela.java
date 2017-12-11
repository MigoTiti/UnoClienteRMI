package unoclientermi.telas;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import interfacesRMI.GerenciadorPartidasRMI;
import interfacesRMI.PartidaRMI;
import unoclientermi.UnoClienteRMI;
import unoclientermi.rede.Comunicador;

public class AguardarJogadoresTela {
    
    public void iniciarTela(GerenciadorPartidasRMI gerenciador, Comunicador comunicador, PartidaRMI partida) {
        Text texto = new Text("Aguardando jogadores");
        texto.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

        ProgressIndicator pi = new ProgressIndicator();

        VBox vBoxCentro = new VBox(texto, pi);
        vBoxCentro.setSpacing(20);
        vBoxCentro.setAlignment(Pos.CENTER);
        BorderPane painel = new BorderPane(vBoxCentro);

        Button voltar = new Button("Voltar");
        voltar.setOnAction(event -> {
            new EscolhaTela().iniciarTela(gerenciador, comunicador);
        });

        HBox hBoxBaixo = new HBox(voltar);
        hBoxBaixo.setPadding(new Insets(15));

        painel.setBottom(hBoxBaixo);

        UnoClienteRMI.setScene(painel);
        
        new Thread(() -> aguardarJogadores(comunicador, partida)).start();
    }
    
    private void aguardarJogadores(Comunicador c, PartidaRMI partida) {
        while (!c.isTodosJogadoresConectados()) {
            Thread.yield();
        }
        
        PartidaTela.getInstance().iniciarTela(c, partida);
    }
}
