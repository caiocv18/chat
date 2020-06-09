package br.ucb.chat.jobs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ExitJob extends Thread {

    private ServerSocket ss;
    public ArrayList<Socket> sockets;

    public ExitJob(ServerSocket ss) {
        this.ss = ss;
        this.sockets = new ArrayList<Socket>();
    }

    @Override
    public void run() {
        super.run();

        // Inicia nosso stream de dados usando o console
        BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in));

        while (true) {

            // Variável para ter controle da saída do aplicativo
            int option = 1;
            
            try {
                // Recebe do usuário um valor pelo console usando o userInput.readLine()
                option = Integer.parseInt(userInput.readLine());
            } catch (NumberFormatException | IOException e) {
                e.printStackTrace();
            }

            // Se for 0 o input do usuário no console, mata o Server Socket e todos os sockets do Arraylist
            if(option == 0){
                try {
                    //Fechar o serverSocket usando o método close
                    this.ss.close();

                    // Itera em cima da variável sockets, passa por cada uma delas e fecha usando o close()
                    for (Socket socket : this.sockets) {
                        socket.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}