package unoclientermi.telas;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import unoclientermi.UnoClienteRMI;
import estruturasRMI.PartidaListagem;
import interfacesRMI.GerenciadorPartidasRMI;
import interfacesRMI.PartidaRMI;
import unoclientermi.rede.Comunicador;

public class EscolhaTela {

    private GerenciadorPartidasRMI gerenciador;
    private Comunicador comunicador;

    public void iniciarTela(GerenciadorPartidasRMI gerenciador, Comunicador comunicador) {
        this.comunicador = comunicador;
        this.gerenciador = gerenciador;

        Button btnEntrar = new Button("Entrar em partida");
        btnEntrar.setOnAction((ActionEvent event) -> {
            new Thread(() -> entrarEmPartida()).start();
        });

        Button btnCriar = new Button("Criar partida");
        btnCriar.setOnAction((ActionEvent event) -> {
            criarPartida();
        });

        VBox vBox = new VBox(btnEntrar, btnCriar);
        vBox.setAlignment(Pos.CENTER);
        vBox.setPadding(new Insets(10));
        vBox.setSpacing(10);

        StackPane root = new StackPane(vBox);

        UnoClienteRMI.setScene(root);
    }

    private void entrarEmPartida() {
        try {
            List<PartidaListagem> partidas = gerenciador.listarPartidas();

            if (partidas.size() < 1) {
                UnoClienteRMI.enviarMensagemErro("Sem partidas disponíveis");
            } else {
                new EscolherPartidaTela().iniciarTela(gerenciador, partidas, comunicador);
            }
        } catch (RemoteException ex) {
            UnoClienteRMI.exibirException(ex);
        }

    }

    private void criarPartida() {
        Dialog<PartidaListagem> dialog = new Dialog<>();
        dialog.setTitle("Criar partida");
        dialog.setResizable(true);

        Label label1 = new Label("Nome: ");
        Label label2 = new Label("Número de jogadores: ");
        TextField text1 = new TextField("Teste");
        TextField text2 = new TextField("2");

        GridPane grid = new GridPane();
        grid.add(label1, 1, 1);
        grid.add(text1, 2, 1);
        grid.add(label2, 1, 2);
        grid.add(text2, 2, 2);
        dialog.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Criar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

        final Button okButton = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
        okButton.setDisable(true);

        text1.textProperty().addListener((observable, oldValue, newValue) -> {
            okButton.setDisable(newValue.trim().length() < 1);
        });

        text2.textProperty().addListener((observable, oldValue, newValue) -> {
            boolean ok;

            try {
                Integer.parseInt(newValue);
                ok = false;
            } catch (NumberFormatException ex) {
                ok = true;
            }

            okButton.setDisable(ok);
        });

        dialog.setResultConverter((ButtonType b) -> {
            if (b == buttonTypeOk) {
                return new PartidaListagem(Integer.parseInt(text2.getText()), 0, -1, text1.getText());
            }

            return null;
        });

        Optional<PartidaListagem> result = dialog.showAndWait();

        if (result.isPresent()) {
            try {
                PartidaListagem temp = result.get();
                PartidaRMI partida = gerenciador.criarPartida(temp.getNome(), temp.getNumeroJogadores(), comunicador.getId());

                new AguardarJogadoresTela().iniciarTela(gerenciador, comunicador, partida);

            } catch (RemoteException ex) {
                UnoClienteRMI.exibirException(ex);
            }
        }
    }
}
