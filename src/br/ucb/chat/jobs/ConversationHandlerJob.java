package br.ucb.chat.jobs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

import br.ucb.chat.core.Server;

public class ConversationHandlerJob extends Thread {

    /** Declaração dos atributos */
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private String userName;

    /**Log */

    static FileWriter fileWriter;
    static BufferedWriter bufferedWriter;
    static PrintWriter logPrintWriter;

    /**Fim Log */

    /** Getters e Setters */
    public Socket getSocket() {
        return socket;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public BufferedReader getIn() {
        return in;
    }

    public void setIn(BufferedReader in) {
        this.in = in;
    }

    public PrintWriter getOut() {
        return out;
    }

    public void setOut(PrintWriter out) {
        this.out = out;
    }
    
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

    /**
     * Construtor ConversationHandlerJob
     * 
     * @throws IOException
     */
    public ConversationHandlerJob(Socket socket) throws IOException {
        // Set no Socket enviado por parâmetro
        setSocket(socket);

        // Configurar o FileWriter
        fileWriter = new FileWriter("C:\\Users\\caiov\\Google Drive\\5-Semestre\\05-Programacao-Concorrente-e-Distribuida\\exercicios\\Chat\\Logs.txt",true);
        bufferedWriter = new BufferedWriter(fileWriter);
        logPrintWriter = new PrintWriter(bufferedWriter,true);
    }

    /** Métodos core da Thread */
    @Override
    public void run() {
        super.run();

        // Instanciamos o InputStream e o OutputStream
        try {
            // Iniciamos o nosso In(receptor de dados) com o InputStream do Socket
            setIn(new BufferedReader(new InputStreamReader(getSocket().getInputStream())));
            // Iniciamos o nosso Out(transmisso de dados) com o OutputStream do Socket
            // Nesse caso, também usamos o autoflush para enviar automaticamente os dados
            // que estão atrelado ao PrintWritter sem ter que usar o método flush()
            setOut(new PrintWriter(getSocket().getOutputStream(), true));

            // Enviamos uma mensagem do servidor para o cliente solicitando o nome
            getOut().println("NAMEREQUIRED");

            // Pegamos o nome do usuário enviado pelo cliente. Ele ira aguardar enquanto o cliente nao envia 
            setUserName(getIn().readLine());

            //Se retornar nulo, mata a thread
            if(getUserName() == null){
                return;
            }

            // Se o nome for uncio, ou seja, o servidor não tiver esse nome, podemos adicionar no servidor
            if(!Server.userNames.contains(getUserName())){
                //Adiciona o nome unico
                Server.userNames.add(getUserName());
                
                //Dizemos para o cliente que o nome foi aceito
                getOut().println("NAMEACCEPTED");

                //Adiciona o PrintWriter (nosso OutputStream desse cliente) ao Servidor, para ele ter a referencia das mensagens
                Server.printWriters.add(getOut());
            }

            /**Envio de mensagens para todos os clientes */
            while(true){
                // Recebemos a mensagem vinda do cliente
                String clientMessage = getIn().readLine();
                
                // Verifica se a mensagem é nula
                if(clientMessage == null){
                    return;
                }

                // Fazendo o log
                logPrintWriter.println(getUserName() + ": " + clientMessage);
                // Fim do log

                // Se não for nula, vamos enviar para todos os clientes
                for (PrintWriter writer : Server.printWriters) {
                    writer.println(getUserName() + ": " + clientMessage); // Formatamos a mensagem a ser enviada para os clientes
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}