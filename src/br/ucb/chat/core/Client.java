package br.ucb.chat.core;

import javax.swing.*;

import br.ucb.listeners.ChatListener;

import java.awt.FlowLayout;
import java.awt.event.WindowEvent;
import java.io.*;
import java.net.Socket;

public class Client {
    /** Front-End -- elementos visuais */

    // Criamos uma janela para abrigar nossos elementos visuais
    static JFrame chatWindow = new JFrame("Aplicacao de Chat");

    // Criamos uma área de texto que pode ser populada (receber novos textos de
    // outros clientes)
    static JTextArea chatArea = new JTextArea(22, 45); // é ordenado por linhas e colunas

    // Criamos a TextField, o local em que a inserção de texto é possível
    public static JTextField textField = new JTextField(40); // 40 = colunas

    // Criamos uma label, ou seja, um texto visual
    static JLabel blankLabel = new JLabel("                ");

    // Criar o botão de enviar
    static JButton sendButton = new JButton("Enviar!");

    /** Fim do Front End */

    /** Atributos do cliente - lado do usuário */
    public static BufferedReader in;
    public static PrintWriter out;

    // Criamos o construtor sem parâmetros para iniciar nosso programa
    Client() {
        // Para renderizar (fazer aparecer) e ajustar todos os elementos na tela,
        // precisamos de um coordenador: o FlowLayout
        chatWindow.setLayout(new FlowLayout());

        // Adicionando os elementos visuais na janela
        chatWindow.add(new JScrollPane(chatArea)); // Adiciona uma barra de rolagem caso o elemento aumente de tamnho
        chatWindow.add(blankLabel); // Adiciona uma label (texto) em branco para espaçamento
        chatWindow.add(textField); // Adicionamos a TextField (inserção de texto pelo usuário)
        chatWindow.add(sendButton); // Adicionamos o botão de envio

        chatWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Essa linha diz que se o usuário clicar em 'X', o
                                                                   // programa fecha
        chatWindow.setSize(475, 500); // Determinando qual será o tamanho da janela do programa no início, assim que
                                      // abrir
        chatWindow.setVisible(true); // Diz que o programa deve ser aberto em primeiro plano

        chatArea.setEditable(false); // Travamos o chatArea para poder receber somente novas strings do socket
        textField.setEditable(false); // Travamos o textField para poder receber input do usuário, ele só recebe
                                      // enquanto não conectar

        // Aqui, irei atrelar minha classe de ChatListener com meu botão enviar
        sendButton.addActionListener(new ChatListener());

        // Adicionamos também um listener para quando clicar em ENTER
        textField.addActionListener(new ChatListener());
    }

    /** Nossa Main está aqui */
    public static void main(String[] args) {
        Client client = new Client();
        // Metodo para iniciar o chat
        try {
            client.startChat();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /** Métodos Core do cliente */
    public void startChat() throws Exception {
        // Solicitamos uma string contendo o endereço de IP do servidor
        String ipAddress = JOptionPane.showInputDialog(chatWindow, "Digite o endereço IP do Chat:",
                "Endereço de IP obrigatorio!", JOptionPane.PLAIN_MESSAGE);

        // Instanciamos o Socket usando o IP inserido pelo usuário e a porta do servidor
        Socket soc = new Socket(ipAddress, 9806);

        // Iniciamos o nosso In (receptor de dados) com o InputStream do Socket
        in = new BufferedReader(new InputStreamReader(soc.getInputStream()));

        // Iniciamos o nosso Out (transmissor de dados) com o OutputStream do Socket,
        // dando autoflush
        out = new PrintWriter(soc.getOutputStream(), true);

        /** Adicionando um Window Listener */

        // Adiona novo listener para olhar os eventos da janela
        chatWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            
            // Captura o evento de fechar a janela
            @Override
            public void windowClosing(WindowEvent e) {

                try {

                    // fechando todos os streams e socket
                    soc.close();
                    in.close();
                    out.close();
                    
                } catch (IOException e1) {
                    e1.printStackTrace();
                }

                super.windowClosing(e);
            }

        });

        /** Fim do Window Listener */
        
        // Aqui ficara a nossa logica de envio e recepcao de mensagens do chat
        while(true){
            // Recebemos a mensagem do servidor
            String serverMsg = in.readLine();

            // Fazer uma ação em cima da mensagem do servidor
            if(serverMsg.contains("NAMEREQUIRED")){
                // Solicitamos um nome ao usuário via JOptionPane
                String userName = JOptionPane.showInputDialog(chatWindow, "Digite um nome:", "Nome eh obrigatorio!", JOptionPane.PLAIN_MESSAGE);
                
                // Envia o userName par ao servidor, onde ele sera armazena no atributo userName do ConversationHandlerJob
                out.println(userName);

            }else if(serverMsg.contains("NAMEACCEPTED")){
                // Ele entra aqui se o nome for aceito, ou seja, a mensagem foi "NAMEACCEPTED"
                //Desbloqueamos entao a edicao no TEXTFIELD para receber inputs de usuário
                textField.setEditable(true);
            }
            else{
                // Aqui iremos entao mostrar as mensagens enviadas por todos os clientes
                chatArea.append(serverMsg + "\n");
            }
        }
    }

}