package emse.ismin.demineur;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Serveur extends JFrame{

    ServeurGui guiServer;
    Socket socket;
    ServerSocket gestSock;
    /***
     *
     */
    Serveur(){
        System.out.println("Démarrage du server");
            //On crée l'interface graphique liée au serveur
            guiServer = new ServeurGui(this);

            //On affiche tout ce qui est lié au server
            setContentPane(guiServer);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            pack();
            setVisible(true);

            startServeur();
            //stopServeur();
    }

    /***
     *
     * @param arg
     */
    public static void main(String[] arg){
        new Serveur();
    }

    /***
     * Attente des clients & démarrage du serveur
     */
    public void startServeur() {
        guiServer.addMsg("Attente des clients");
        try{
            gestSock = new ServerSocket(Demineur.PORT);
            socket = gestSock.accept();
        } catch(IOException e){
            e.printStackTrace();
        }
    }

    /***
     * Stop le serveur
     */
    private void stopServeur() {
        try {
            //sortie.close();
            //entree.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}