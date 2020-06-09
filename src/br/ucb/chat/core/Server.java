package br.ucb.chat.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import br.ucb.chat.jobs.ConversationHandlerJob;
import br.ucb.chat.jobs.ExitJob;

public class Server {
    /**Atributos do servidor */
    public static ArrayList<String> userNames = new ArrayList<String>();
    public static ArrayList<PrintWriter> printWriters = new ArrayList<PrintWriter>();
    
    public static void main(String[] args) throws IOException {
        System.out.println("Esperando por clientes...");

        // Criação do Server Socket, o que permite novas conexões com clientes
        // Método accept do serversocket para retornar os sockets conectados
        ServerSocket ss = new ServerSocket(9806);

        // Iniciando nossa thread
        ExitJob exitJob = new ExitJob(ss);
        exitJob.start();

        while (true) {
            // Após estabelecer contato com o ServerSocket, um socket de comunicação
            // (cliente - server) eh criado
            Socket soc = ss.accept();
            System.out.println("Conexao estabelecida com o cliente! " + soc);

            // Criando o ConversationHandler Job
            ConversationHandlerJob handler = new ConversationHandlerJob(soc);

            // Dados início a thread que vai coordenar a conversa
            handler.start();

            // Adiciona o socket na seleção do job
            exitJob.sockets.add(soc);
        }

    }

}