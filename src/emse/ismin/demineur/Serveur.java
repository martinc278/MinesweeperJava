package emse.ismin.demineur;

import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Serveur extends JFrame implements Runnable{

    ServeurGui guiServer;
    Socket socket;
    ServerSocket gestSock;
    private HashMap<String, DataOutputStream> listOut = new HashMap<>();
    private HashMap<String, DataInputStream> listIn = new HashMap();

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
            //Lancement du gestionnaire de sockets
            gestSock = new ServerSocket(Demineur.PORT);

            //On lance les threads pour attendre le client
            new Thread(this).start();

        } catch(IOException e){
            e.printStackTrace();
        }
    }

    public void run(){
        try {
            socket = gestSock.accept(); //new client
            guiServer.addMsg("Nouveau client");

            //lancement du client suivant
            new Thread(this).start();

            //ouverture des in/out
            DataOutputStream out =new DataOutputStream(socket.getOutputStream());
            DataInputStream in = new DataInputStream(socket.getInputStream());

            //boucle  d'attente des infos des clients
            String joueur = in.readUTF();
            guiServer.addMsg(joueur);

            //stockage dans 2 collections
            listOut.put(joueur, out);
            listIn.put(joueur, in);

            //Boucle infinie d'attente des instructions joueur
            while(true){
                String instruction = in.readUTF();
            }

            //redispach aux autres si nécessaire

        } catch (IOException e) {
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

    public void startPartie() {
        try {
            gestSock.close();
            guiServer.addMsg("Server Closed, game can start");
            for(Map.Entry<String, DataOutputStream> i : listOut.entrySet()){
                i.getValue().writeInt(2); //return le out pour chacune des entrées
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}