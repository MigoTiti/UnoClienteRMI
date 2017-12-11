package unoclientermi.telas;

import interfacesRMI.PartidaRMI;
import java.util.List;
import java.util.Optional;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import unoclientermi.UnoClienteRMI;
import estruturasRMI.Carta;
import java.rmi.RemoteException;
import unoclientermi.estruturas.CartaVisual;
import unoclientermi.rede.Comunicador;

public class PartidaTela {

    private int numeroJogador;
    private int vezDoJogador;
    private boolean correnteCompra;
    private int correnteCompraQuantidade;
    private boolean emUno;
    private boolean jaComprou;
    private boolean jogoFinalizado;

    private Carta cartaNaMesa;

    private List<Carta> maoDoJogador;

    private Comunicador comunicador;
    private PartidaRMI partida;

    private StackPane cartaNaMesaPane;

    private int corSelecionadaTemp;

    private Button jogar;
    private Button comprar;
    private Label vezJogador;
    private Label contagemCorrenteLabel;
    private Label jogadorLabel;
    private ListView<Carta> listaCartas;
    private StackPane cartaPreview;

    private static PartidaTela instance;

    private PartidaTela() {
        listaCartas = new ListView<>();
        maoDoJogador = null;
    }

    public static PartidaTela getInstance() {
        if (instance == null) {
            instance = new PartidaTela();
        }

        return instance;
    }

    public static void deleteInstance() {
        instance = null;
    }

    public void iniciarTela(Comunicador comunicador, PartidaRMI partida) {
        this.comunicador = comunicador;
        this.partida = partida;
        cartaNaMesa = null;
        corSelecionadaTemp = -1;
        jogar = new Button("Jogar");
        comprar = new Button("Comprar");
        vezJogador = new Label();
        jogadorLabel = new Label();
        contagemCorrenteLabel = new Label("Contagem corrente: 0");
        cartaPreview = new StackPane();
        numeroJogador = -1;
        vezDoJogador = 1;
        correnteCompra = false;
        correnteCompraQuantidade = 0;
        emUno = false;
        jaComprou = false;
        jogoFinalizado = false;
        listaCartas = new ListView<>();
        maoDoJogador = null;

        BorderPane root;
        BorderPane campo;

        cartaNaMesaPane = new StackPane();

        campo = new BorderPane(cartaNaMesaPane);

        root = new BorderPane(campo);

        Button voltar = new Button("Sair da partida");
        voltar.setOnAction(value -> {

        });
        HBox hbox = new HBox(voltar);
        hbox.setPadding(new Insets(10));

        HBox opcoes = new HBox();
        opcoes.setSpacing(10);

        jogar.setDisable(true);
        jogar.setOnAction(value -> {
            if (!jogoFinalizado) {
                if (numeroJogador == vezDoJogador) {
                    Carta cartaJogada = listaCartas.getSelectionModel().getSelectedItem();

                    if (validarJogada(cartaJogada)) {
                        if (jaComprou) {
                            jaComprou = false;
                            comprar.setText("Comprar");
                        }
                        if (cartaJogada.getCor() == Carta.COR_PRETA) {
                            Dialog<Integer> dialog = new Dialog<>();
                            dialog.setTitle("Escolha a cor desejada");
                            dialog.setResizable(true);

                            int tamanho = 200;

                            Color vermelhoInicial = Color.rgb(96, 0, 0);
                            Color azulInicial = Color.rgb(0, 0, 96);
                            Color amareloInicial = Color.rgb(215, 215, 0);
                            Color verdeInicial = Color.rgb(0, 96, 0);

                            Color vermelhoSelecionado = Color.RED;
                            Color azulSelecionado = Color.BLUE;
                            Color amareloSelecionado = Color.YELLOW;
                            Color verdeSelecionado = Color.GREEN;

                            Rectangle vermelho = new Rectangle(tamanho, tamanho, vermelhoInicial);
                            Rectangle azul = new Rectangle(tamanho, tamanho, azulInicial);
                            Rectangle amarelo = new Rectangle(tamanho, tamanho, amareloInicial);
                            Rectangle verde = new Rectangle(tamanho, tamanho, verdeInicial);

                            GridPane grid = new GridPane();
                            grid.add(vermelho, 1, 1);
                            grid.add(azul, 2, 1);
                            grid.add(amarelo, 1, 2);
                            grid.add(verde, 2, 2);
                            dialog.getDialogPane().setContent(grid);

                            ButtonType buttonTypeOk = new ButtonType("Escolher", ButtonBar.ButtonData.OK_DONE);
                            dialog.getDialogPane().getButtonTypes().add(buttonTypeOk);

                            final Button okButton = (Button) dialog.getDialogPane().lookupButton(buttonTypeOk);
                            okButton.setDisable(true);

                            vermelho.setOnMouseClicked(event -> {
                                vermelho.setFill(vermelhoSelecionado);
                                azul.setFill(azulInicial);
                                verde.setFill(verdeInicial);
                                amarelo.setFill(amareloInicial);
                                corSelecionadaTemp = Carta.COR_VERMELHA;
                                okButton.setDisable(false);
                            });

                            azul.setOnMouseClicked(event -> {
                                azul.setFill(azulSelecionado);
                                vermelho.setFill(vermelhoInicial);
                                verde.setFill(verdeInicial);
                                amarelo.setFill(amareloInicial);
                                corSelecionadaTemp = Carta.COR_AZUL;
                                okButton.setDisable(false);
                            });

                            amarelo.setOnMouseClicked(event -> {
                                amarelo.setFill(amareloSelecionado);
                                vermelho.setFill(vermelhoInicial);
                                azul.setFill(azulInicial);
                                verde.setFill(verdeInicial);
                                corSelecionadaTemp = Carta.COR_AMARELA;
                                okButton.setDisable(false);
                            });

                            verde.setOnMouseClicked(event -> {
                                verde.setFill(verdeSelecionado);
                                vermelho.setFill(vermelhoInicial);
                                azul.setFill(azulInicial);
                                amarelo.setFill(amareloInicial);
                                corSelecionadaTemp = Carta.COR_VERDE;
                                okButton.setDisable(false);
                            });

                            dialog.setResultConverter((ButtonType b) -> {
                                if (b == buttonTypeOk) {
                                    return corSelecionadaTemp;
                                }

                                corSelecionadaTemp = -1;
                                return null;
                            });

                            Optional<Integer> result = dialog.showAndWait();

                            if (result.isPresent()) {
                                Integer novaCor = result.get();
                                cartaJogada.setCor(novaCor);

                                jogarCarta(cartaJogada);
                            }
                        } else {
                            jogarCarta(cartaJogada);
                        }
                    } else {
                        UnoClienteRMI.enviarMensagemErro("Jogada inválida");
                    }
                } else {
                    UnoClienteRMI.enviarMensagemErro("Espere a sua vez");
                }
            } else {
                UnoClienteRMI.enviarMensagemErro("O jogo já acabou");
            }
        });

        comprar.setOnAction(value -> {
            if (!jogoFinalizado) {
                if (numeroJogador == vezDoJogador) {
                    if (!jaComprou) {
                        comprar.setText("Pular");
                        jaComprou = true;

                        comprarCarta();
                    } else {
                        try {
                            comprar.setText("Comprar");
                            jaComprou = false;
                            this.partida.pularJogada();
                        } catch (RemoteException ex) {
                            UnoClienteRMI.exibirException(ex);
                        }
                    }
                    
                    listaCartas.getSelectionModel().clearSelection();
                } else {
                    UnoClienteRMI.enviarMensagemErro("Espere a sua vez");
                }
            } else {
                UnoClienteRMI.enviarMensagemErro("O jogo já acabou");
            }
        });

        opcoes.getChildren().addAll(jogar, comprar);

        VBox vboxMao = new VBox(listaCartas, cartaPreview, opcoes);
        vboxMao.setPadding(new Insets(10));
        vboxMao.setSpacing(30);

        setPreview(null);

        VBox vboxTop = new VBox(jogadorLabel, vezJogador, contagemCorrenteLabel);
        vboxTop.setPadding(new Insets(10));
        vboxTop.setAlignment(Pos.CENTER);

        root.setRight(vboxTop);
        root.setLeft(vboxMao);
        root.setBottom(hbox);

        UnoClienteRMI.setScene(root);

        new Thread(() -> iniciarJogo()).start();
    }

    private void comprarCarta() {
        try {
            if (emUno) {
                emUno = false;
                this.partida.declararSaiuUno(numeroJogador);
            }

            this.partida.comprarCarta();
        } catch (RemoteException ex) {
            UnoClienteRMI.exibirException(ex);
        }
    }

    private void jogarCarta(Carta cartaJogada) {
        try {
            if (listaCartas.getItems().size() == 2 && !emUno) {
                this.partida.declararUno(numeroJogador);
                emUno = true;
            }

            if (listaCartas.getItems().size() == 1 && emUno) {
                this.partida.declararVitoria(numeroJogador);
            }

            this.partida.jogarCarta(cartaJogada);
            listaCartas.getItems().remove(listaCartas.getSelectionModel().getSelectedIndex());
            setPreview(null);
            listaCartas.getSelectionModel().clearSelection();
        } catch (RemoteException ex) {
            UnoClienteRMI.exibirException(ex);
        }
    }

    private boolean validarJogada(Carta cartaEscolhida) {
        int corCartaEscolhida = cartaEscolhida.getCor();
        int numeroCartaEscolhida = cartaEscolhida.getNumero();
        int corCartaNaMesa = cartaNaMesa.getCor();
        int numeroCartaNaMesa = cartaNaMesa.getNumero();

        if ((numeroCartaNaMesa == Carta.MAIS_DOIS || numeroCartaNaMesa == Carta.MAIS_QUATRO) && correnteCompra) {
            return numeroCartaEscolhida == Carta.MAIS_DOIS || numeroCartaEscolhida == Carta.MAIS_QUATRO;
        }

        if (numeroCartaEscolhida == Carta.MAIS_QUATRO || numeroCartaEscolhida == Carta.CORINGA) {
            return true;
        }

        return corCartaEscolhida == corCartaNaMesa || numeroCartaEscolhida == numeroCartaNaMesa;
    }

    private void iniciarJogo() {
        try {
            while (comunicador.getMaoDoUsuario() == null) {
                Thread.yield();
            }

            setMaoDoJogador(comunicador.getMaoDoUsuario());
            comunicador.setMaoDoUsuario(null);

            while (comunicador.getNumeroJogador() == 0) {
                Thread.yield();
            }

            setNumeroJogador(comunicador.getNumeroJogador());

            while (comunicador.getCartaNaMesa() == null) {
                Thread.yield();
            }

            setCartaNaMesa(comunicador.getCartaNaMesa());
            comunicador.setPrimeiraCarta(null);
        } catch (RemoteException ex) {
            UnoClienteRMI.exibirException(ex);
        }
    }

    public void adicionarCartas(List<Carta> cartas) {
        Platform.runLater(() -> {
            cartas.stream().forEach((carta) -> {
                maoDoJogador.add(carta);
                listaCartas.getItems().add(carta);
            });
        });
    }

    public void setCorrenteCompra(boolean corrente) {
        correnteCompra = corrente;
    }

    public void setNumeroJogador(int numero) {
        numeroJogador = numero;

        Platform.runLater(() -> {
            jogadorLabel.setText("Você é o jogador " + numeroJogador);

            if (numeroJogador == 1) {
                vezJogador.setText("Sua vez");
                vezJogador.setTextFill(Color.GREEN);
            } else {
                vezJogador.setText("Vez do Jogador 1");
                vezJogador.setTextFill(Color.RED);
            }
        });
    }

    public void setCorrenteCompraQuantidade(int contagem) {
        if (contagem != correnteCompraQuantidade) {
            correnteCompraQuantidade = contagem;

            Platform.runLater(() -> {
                contagemCorrenteLabel.setText("Contagem corrente: " + correnteCompraQuantidade);
            });
        }
    }

    public void setVezDoJogador(int jogador) {
        vezDoJogador = jogador;

        Platform.runLater(() -> {
            if (vezDoJogador == numeroJogador) {
                vezJogador.setText("Sua vez");
                vezJogador.setTextFill(Color.GREEN);
            } else {
                vezJogador.setText("Vez do jogador " + vezDoJogador);
                vezJogador.setTextFill(Color.RED);
            }
            listaCartas.getSelectionModel().clearSelection();
        });
    }

    private void setPreview(Carta c) {
        Platform.runLater(() -> {
            cartaPreview.getChildren().clear();

            if (c == null) {
                cartaPreview.getChildren().addAll(CartaVisual.gerarCartaVisual(-1, 0));
            } else {
                cartaPreview.getChildren().addAll(CartaVisual.gerarCartaVisual(c.getCor(), c.getNumero()));
            }
        });
    }

    public void setCartaNaMesa(Carta c) {
        cartaNaMesa = c;
        Platform.runLater(() -> {
            cartaNaMesaPane.getChildren().clear();
            cartaNaMesaPane.getChildren().addAll(CartaVisual.gerarCartaVisual(c.getCor(), c.getNumero()));
        });
    }

    private void setMaoDoJogador(List<Carta> maoDoJogador) {
        this.maoDoJogador = maoDoJogador;

        ObservableList<Carta> cartas = FXCollections.observableArrayList();

        for (int i = 0; i < maoDoJogador.size(); i++) {
            cartas.add(maoDoJogador.get(i));
        }

        listaCartas.setItems(cartas);
        listaCartas.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Carta> observable, Carta oldValue, Carta newValue) -> {
            jogar.setDisable(numeroJogador != vezDoJogador);
            setPreview(newValue);
        });
    }

    public void finalizarPartida() {
        this.jogoFinalizado = true;
    }
}
