package br.ucb.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import br.ucb.chat.core.Client;
public class ChatListener implements ActionListener{

    // Esse metodo ira ser acionado toda vez que eu clicar em enviar ou no meu teclado ENTER
    @Override
    public void actionPerformed(ActionEvent e) {
        // Aqui, eu envio o texto que esta na minha textField, digitada pelo usuário
        Client.out.println(Client.textField.getText());

        // Limpa o textField, ja que ja enviamos o texto acima
        Client.textField.setText("");
    }
    
}