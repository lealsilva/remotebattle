package br.remotebattle.remote.implementacao;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import br.remotebattle.dominio.Jogador;
import br.remotebattle.dominio.Jogo;
import br.remotebattle.remote.IJogoRemoto;

@SuppressWarnings("serial")
public class JogoRemoto extends UnicastRemoteObject implements IJogoRemoto {

	private Jogo jogo;
	private Jogador jogador;
	
	protected JogoRemoto(Jogo jogo) throws RemoteException {
		super();
		this.jogo = jogo;
		
		if(jogo.getJogador1()!=null && jogo.getJogador2()==null){
			this.jogador = jogo.getJogador1();
		} 
		else if(jogo.getJogador1()!=null && jogo.getJogador2()!=null){
			this.jogador = jogo.getJogador2();
			this.jogador.setOponente(jogo.getJogador1());
		}
	}
	
	public String entrarNoJogo(String nomeJogador) throws RemoteException {
	
		//Monta o nome do jogo baseado no nome do jogador
		String nomeJogo = ServicoJogos.montarNomeJogo(nomeJogador);
		
		//Se nao existe o jogo com o nome passado por parametro
		if(!ServicoJogos.existeJogo(nomeJogo)){
			
			try {
				//Seta o segundo jogador do jogo e o oponente do jogador
				this.jogo.setJogador2(nomeJogador);
				this.jogador.setOponente(this.jogo.getJogador2());
				
				//Cria o jogo remoto e publica
				IJogoRemoto jogoRemoto = new JogoRemoto(this.jogo);
				
				//Publica o novo jogo remoto
				ServicoJogos.publicarObjeto(nomeJogo, jogoRemoto);
				
				System.out.println("O jogador "+nomeJogador+" entrou no jogo!");
				System.out.println("\nJogo do Jogador1: "+this);
				System.out.println("\nJogo do Jogador2: "+jogoRemoto);
			
			} catch (MalformedURLException e) {
				//Erro. Volta o estado do jogador 2 para o estado inicial
				this.jogo.setJogador2(null);
				this.jogador.setOponente(null);
				
				System.err.println("Não foi possível publicar o jogo remoto!");
				e.printStackTrace();
				
				return null;
			}
			
			
			return nomeJogo;
		}
		
		return null;
		
	}
	
	public static IJogoRemoto getJogoRemoto(String nomeJogoRemoto){
		
		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}
		
		try {
			System.out.println("Recuperado o proxy do jogoRemoto de nome "+nomeJogoRemoto+"...");
			IJogoRemoto jogoRemoto = (IJogoRemoto) Naming.lookup(nomeJogoRemoto);
			
			return jogoRemoto;
			
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(e.getMessage());
		}
		
		return null;
	}

	public Jogo getJogo() {
		return jogo;
	}

	public void setJogo(Jogo jogo) {
		this.jogo = jogo;
	}
	
	public String toString(){
		String saida = "Jogo: \n"+
						"Jogador1: "+jogo.getJogador1()+"\n"+
						"Jogador2: "+jogo.getJogador2()+"\n\n"+
						"Jogador: \n"+
						"nome: "+jogador+"\n"+
						"oponente: "+jogador.getOponente()+"\n\n";
		
		return saida;
	}
}