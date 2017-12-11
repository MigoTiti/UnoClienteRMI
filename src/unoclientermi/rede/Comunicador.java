package unoclientermi.rede;

import interfacesRMI.ComunicadorJogadorRMI;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import estruturasRMI.Carta;
import unoclientermi.telas.PartidaTela;

public class Comunicador extends UnicastRemoteObject implements ComunicadorJogadorRMI, Serializable{
    
    private final int id;
    private boolean todosJogadoresConectados = false;
    
    private List<Carta> maoDoUsuario;
    private int numeroJogador;
    private Carta primeiraCarta;
    
    public List<Carta> getMaoDoUsuario() {
        return maoDoUsuario;
    }

    public int getNumeroJogador() {
        return numeroJogador;
    }
    
    public Carta getCartaNaMesa() {
        return primeiraCarta;
    }
    
    @Override
    public void retornarCompra(List<Carta> cartas) throws RemoteException {
        PartidaTela.getInstance().adicionarCartas(cartas);
    }
    
    @Override
    public void setContagemCorrente(boolean corrente, int contagem) throws RemoteException {
        PartidaTela.getInstance().setCorrenteCompraQuantidade(contagem);
        PartidaTela.getInstance().setCorrenteCompra(corrente);
    }
    
    @Override
    public void jogadorGanhou(int jogador) throws RemoteException {
        unoclientermi.UnoClienteRMI.enviarMensagemInfo((numeroJogador == jogador ? "Você" : "Jogador " + jogador) + " ganhou!");
        PartidaTela.getInstance().finalizarPartida();
    }
    
    @Override
    public void declararSaiuDeUno(int jogador) throws RemoteException {
        unoclientermi.UnoClienteRMI.enviarMensagemInfo((numeroJogador == jogador ? "Você" : "Jogador " + jogador) + " saiu do estado de Uno!");
    }
    
    @Override
    public void declararUno(int jogador) throws RemoteException {
        unoclientermi.UnoClienteRMI.enviarMensagemInfo((numeroJogador == jogador ? "Você" : "Jogador " + jogador) + " está em estado de Uno!");
    }
    
    @Override
    public void setProximoJogador(int jogador) throws RemoteException {
        PartidaTela.getInstance().setVezDoJogador(jogador);
    }
    
    @Override
    public void setCartaNaMesa(Carta c) throws RemoteException {
        PartidaTela.getInstance().setCartaNaMesa(c);
    }
    
    @Override
    public void setPrimeiraCarta(Carta c) throws RemoteException {
        primeiraCarta = c;
    }
    
    @Override
    public void setMaoDoUsuario(List<Carta> maoDoUsuario) throws RemoteException {
        this.maoDoUsuario = maoDoUsuario;
    }
    
    @Override
    public void setNumeroJogador(int numeroJogador) throws RemoteException {
        this.numeroJogador = numeroJogador;
    }
    
    @Override
    public int getId() throws RemoteException {
        return id;
    }
    
    public void reset() {
        todosJogadoresConectados = false;
    }
    
    public boolean isTodosJogadoresConectados() {
        return todosJogadoresConectados;
    }
    
    @Override
    public void informarPartidaCheia() throws RemoteException {
        unoclientermi.UnoClienteRMI.enviarMensagemErro("Partida cheia!");
    }
    
    @Override
    public void setTodosJogadoresConectados(boolean todosConectados) throws RemoteException {
        todosJogadoresConectados = todosConectados;
    }
    
    public Comunicador(int id) throws RemoteException{
        this.id = id;
    }
}
