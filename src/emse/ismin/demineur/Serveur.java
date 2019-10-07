package emse.ismin.demineur;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Serveur extends JFrame implements Runnable{

    ServeurGui guiServer;
    Socket socket;
    ServerSocket gestSock;
    private HashMap<String, DataOutputStream> listOut = new HashMap<>();
    private HashMap<String, DataInputStream> listIn = new HashMap();
    private HashMap<DataOutputStream, Color> listColor = new HashMap<>();

    private Champ ch = new Champ(Level.MEDUIM); //champ crée par le serveur

    public static final int START = 2;
    private boolean gameStarted = false;
    public static final int START_CPT = 4;
    private static final int SET_LOST_FALSE = 5;
    private static final int IS_MINES = 6;
    private static final int IS_NOT_MINE = 7;
    public static final int COORDONNEES = 8;
    public static final int IS_CLICKED = 9;

    public boolean getGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    /***
     * Constructeur de serveur
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
     * Main de serveur, lance le constructeur
     * @param arg classic main
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

    /***
     * Thread de gestion des clients, un Thread par client et toujours un en plus qui attend les nouveaux clients
     */
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
            Random random= new Random();
            Random random1= new Random();
            Random random2= new Random();
            listColor.put(out, new Color(random.nextInt(256), random1.nextInt(256), random2.nextInt(256)));

            //Boucle infinie d'attente des instructions joueur
            while(true){
                int instruction = in.readInt();
                if(instruction==COORDONNEES){
                    int x = in.readInt();
                    int y = in.readInt();
                    guiServer.addMsg("\nCoordonnée reçues : x="+String.valueOf(x)+" y="+String.valueOf(y));
                    verifMines(x,y, out);
                }
            }

            //redispach aux autres si nécessaire

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /***
     * Méthode synchronized qui est appelée poour vérifier si une case cache une mine, par un client. Elle gère les cas de mine, pas mine et les gestion de mappage qui quel joueur a cliqué sur quel case
     * @param x ligne de la case cliquée
     * @param y colonne de la case cliquée
     * @param out modélisation du client qui l'appelle
     */
    synchronized private void verifMines(int x, int y, DataOutputStream out) {
        try {
            if(ch.getTableConnected(x,y)=="unclicked"){ //Cas de bombe
                if(ch.isMIN(x,y)){
                    for(Map.Entry<String, DataOutputStream> i : listOut.entrySet()){ //On Parcours toute la collection pour dire à tout le monde qu'il y a une bombe découverte par qqn
                        ch.setTableConnected(x,y,nomJoueurConnected(out));
                        i.getValue().writeInt(IS_MINES); //return le out pour chacune des entrées et envoie que la partie peut commencer
                        i.getValue().writeInt(x);
                        i.getValue().writeInt(y);
                    }
                }
                else{ //cas de non bombe
                    for(Map.Entry<String, DataOutputStream> i : listOut.entrySet()){ //On Parcours toute la collection pour que tout le monde voit le nombre de voisins
                        ch.setTableConnected(x,y,nomJoueurConnected(out));
                        i.getValue().writeInt(IS_NOT_MINE); //On envoie à tous les clients que (x,y) n'est pas une mine et le nombre de voisins
                        i.getValue().writeInt(x);
                        i.getValue().writeInt(y);
                        i.getValue().writeInt(ch.nbVoisins(x,y));
                        i.getValue().writeUTF(nomJoueurConnected(out));
                        i.getValue().writeInt(colorJoueurConnected(out).getRGB());
                    }
                }
            }else{ //Si qqn a déjà cliqué sur la case, on dit qui
                out.writeInt(IS_CLICKED); //return le out pour chacune des entrées et envoie que la partie peut commencer
                out.writeUTF(ch.getTableConnected(x,y));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /***
     * renvoie le string du joueur qui correpond à un canal out
     * @param out canal out connu
     * @return String de l'ID joueur correspondant
     */
    private String nomJoueurConnected(DataOutputStream out) {
        for(Map.Entry<String, DataOutputStream> i : listOut.entrySet()){ //On Parcours toute la collection
            if(i.getValue()==out){
                return i.getKey();//On envoie à tous les clients que (x,y) n'est pas une mine et le nombre de voisins
            }
        }
        return "0";
    }

    /***
     * Renvoie la couleur assignée par le serveur à un joueur, repéré par son canal de communication out
     * @param out client dont on veut connaitre la couleur
     * @return la couleur du joueur
     */
    private Color colorJoueurConnected(DataOutputStream out){
        for(Map.Entry<DataOutputStream, Color> i : listColor.entrySet()){ //On Parcours toute la collection
            if(i.getKey()==out){
                return i.getValue();//On envoie à tous les clients que (x,y) n'est pas une mine et le nombre de voisins
            }
        }
        return null;
    }

    /***
     * Stop le serveur  - INUTILISÉE
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

    /***
     * Méthode appelée par le click sur la case "Start Partie" du serveur, génère le champ et notifie à tous les joueurs que la partie peut commencer
     */
    public void startPartie() {
        try {
            gestSock.close();
            guiServer.addMsg("Server Closed, game can start");
            setGameStarted(true);
            for(Map.Entry<String, DataOutputStream> i : listOut.entrySet()){ //On Parcours toute la collection
                i.getValue().writeInt(START); //return le out pour chacune des entrées et envoie que la partie peut commencer
                i.getValue().writeInt(SET_LOST_FALSE);
            }

            ch.placeMines();
            ch.afficherMines();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}