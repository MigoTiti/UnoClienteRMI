package unoclientermi.telas;

import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import estruturasRMI.PartidaListagem;
import interfacesRMI.GerenciadorPartidasRMI;
import interfacesRMI.PartidaRMI;
import java.rmi.RemoteException;
import unoclientermi.UnoClienteRMI;
import unoclientermi.rede.Comunicador;

public class EscolherPartidaTela {
    
    private GerenciadorPartidasRMI gerenciador;
    private TableView<PartidaListagem> table;
    
    public void iniciarTela(GerenciadorPartidasRMI gerenciador, List<PartidaListagem> partidas, Comunicador comunicador) {
        this.gerenciador = gerenciador;
        
        ObservableList<PartidaListagem> partidasLista = FXCollections.observableArrayList();
        
        partidas.stream().forEach((partida) -> {
            partidasLista.add(partida);
        });
        
        BorderPane root;
        
        final Label label = new Label("Partidas disponíveis");
        label.setFont(new Font("Arial", 20));
        label.setAlignment(Pos.CENTER);
        
        table = new TableView<>();
        table.setEditable(false);
        
        TableColumn nomeColuna = new TableColumn("id");
        nomeColuna.setMinWidth(50);
        nomeColuna.setCellValueFactory(
                new PropertyValueFactory<>("id"));
        nomeColuna.setResizable(false);
 
        TableColumn idColuna = new TableColumn("Nome");
        idColuna.setMinWidth(100);
        idColuna.setCellValueFactory(
                new PropertyValueFactory<>("nome"));
        idColuna.setResizable(false);
 
        TableColumn nJogadoresColuna = new TableColumn("Capacidade");
        nJogadoresColuna.setMinWidth(100);
        nJogadoresColuna.setCellValueFactory(
                new PropertyValueFactory<>("numeroJogadores"));
        nJogadoresColuna.setResizable(false);
        
        TableColumn jogadoresConectadosColuna = new TableColumn("Jogadores conectados");
        jogadoresConectadosColuna.setMinWidth(150);
        jogadoresConectadosColuna.setCellValueFactory(
                new PropertyValueFactory<>("jogadoresConectados"));
        jogadoresConectadosColuna.setResizable(false);
 
        table.setItems(partidasLista);
        table.getColumns().addAll(idColuna, nomeColuna, nJogadoresColuna, jogadoresConectadosColuna);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        
        final VBox vbox = new VBox();
        vbox.setSpacing(20);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(label, table);
        vbox.setAlignment(Pos.CENTER);
        
        final HBox hbox = new HBox(vbox);
        hbox.setAlignment(Pos.CENTER);
        
        root = new BorderPane(hbox);
        
        Button voltarBotao = new Button("Voltar");
        voltarBotao.setOnAction((ActionEvent event) -> {
            new EscolhaTela().iniciarTela(gerenciador, comunicador);
        });
        
        Button entrarBotao = new Button("Entrar");
        entrarBotao.setOnAction((ActionEvent event) -> {
            PartidaListagem partidaSelecionada = table.getSelectionModel().getSelectedItem();
            
            if (partidaSelecionada != null) {
                try {
                    PartidaRMI partida = this.gerenciador.entrarEmPartida(partidaSelecionada.getId(), comunicador.getId());
                    
                    if (partida != null)
                        new AguardarJogadoresTela().iniciarTela(gerenciador, comunicador, partida);
                } catch (RemoteException ex) {
                    UnoClienteRMI.exibirException(ex);
                }
            } else {
                UnoClienteRMI.enviarMensagemErro("Escolha uma partida");
            }
        });
        
        final HBox hbox2 = new HBox(voltarBotao, entrarBotao);
        hbox2.setSpacing(10);
        hbox2.setPadding(new Insets(10));
        
        root.setBottom(hbox2);
        
        UnoClienteRMI.setScene(root);
    }
}
