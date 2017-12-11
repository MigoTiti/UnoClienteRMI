package unoclientermi.telas;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
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

import unoclientermi.UnoClienteRMI;
import interfacesRMI.GerenciadorPartidasRMI;
import java.rmi.AlreadyBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import unoclientermi.rede.Comunicador;

public class ConectarAoServidorTela {
 
    GerenciadorPartidasRMI gerenciador;
    
    public void iniciarTela(String ip) {
        Text texto = new Text("Tentando conexão ao servidor");
        texto.setFont(Font.font("Arial", FontWeight.NORMAL, 20));

        ProgressIndicator pi = new ProgressIndicator();

        VBox vBoxCentro = new VBox(texto, pi);
        vBoxCentro.setSpacing(20);
        vBoxCentro.setAlignment(Pos.CENTER);
        BorderPane painel = new BorderPane(vBoxCentro);

        Button voltar = new Button("Voltar");
        voltar.setOnAction(event -> {
            UnoClienteRMI.createScene();
        });

        HBox hBoxBaixo = new HBox(voltar);
        hBoxBaixo.setPadding(new Insets(15));

        painel.setBottom(hBoxBaixo);

        UnoClienteRMI.setScene(painel);
        
        new Thread(() -> conectarAoServidor(ip)).start();
    }
    
    private void conectarAoServidor(String ip) {
        try {
            Registry registro = LocateRegistry.getRegistry(ip, UnoClienteRMI.PORTA_SERVIDOR);
            gerenciador = (GerenciadorPartidasRMI) registro.lookup("GerenciadorPartidas");
            
            int idJogador = gerenciador.conectarJogador();
            
            Comunicador c = new Comunicador(idJogador);
            registro.bind("Comunicador_" + idJogador, c);
            
            new EscolhaTela().iniciarTela(gerenciador, c);
        } catch (NotBoundException | RemoteException | AlreadyBoundException ex) {
            UnoClienteRMI.exibirException(ex);
        }
    }
}
