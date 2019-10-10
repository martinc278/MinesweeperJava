package emse.ismin.demineur;

import javax.swing.*;
import java.awt.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 *
 */
public class Demineur extends JFrame implements Runnable{
    public static final String HOSTNAME = "localhost";
    public static final String PSEUDO = "Gros Bill";
    public static final int PORT = 10000;
    private Champ ch = new Champ(Level.MEDUIM);
    private boolean started = false;
    private Gui gui; //création de l'interface graphique
    private boolean lost;
    private boolean win;
    private int nbCasesDecouvertes = 0;
    private static final String FILENAME = "score.dat";
    public static final int MSG = 0;
    public static final int POS = 1;
    public static final int START = 2;
    public static final int WON = 3;
    public static final int START_CPT = 4;
    public static final int SET_LOST_FALSE = 5;
    public static final int IS_MINES = 6;
    public static final int IS_NOT_MINE = 7;
    public static final int COORDONNEES = 8;
    public static final int IS_CLICKED = 9;



    private boolean connected = false;
    private boolean bombeConnected = false;
    private boolean wonConnected = false;
    private int NBVoisConnected = 0;
    private Color colorConnected;
    private String joueurClicked;

    /***
     * getter de JoueurClicked, permet de savoir qui a cliqué, en mode connecté
     * @return string du joueur qui a cliqué
     */
    public String getJoueurClicked() {
        return joueurClicked;
    }

    /***
     *Setter du joueur qui a cliqué
     * @param joueurClicked string du joueur qui a cliqué sur la case
     */
    public void setJoueurClicked(String joueurClicked) {
        this.joueurClicked = joueurClicked;
    }

    /***
     * getter de wonConnected, permet de savoir si la partie a été gagnée déjà
     * @return la booléen qui donne l'état de la partie, won ou non
     */
    public boolean isWonConnected() {
        return wonConnected;
    }

    /***
     * setter de wonConnected, permet de set si la partie a été gagnée ou non
     * @param wonConnected indiquer le changement d'état sur la partie
     */
    public void setWonConnected(boolean wonConnected) {
        this.wonConnected = wonConnected;
    }


    /***
     * Getter de connected
     * @return
     */
    public boolean getConnected() {
        return connected;
    }

    /***
     * Setter de connected
     * @param connected
     */
    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    DataOutputStream out;
    DataInputStream in;
    Thread process;

    public int getNbCasesDecouvertes() {
        return nbCasesDecouvertes;
    }

    public void setNbCasesDecouvertes(int nbCasesDecouvertes) {
        this.nbCasesDecouvertes = nbCasesDecouvertes;
    }


    /***
     *
     * @return la valeur de lost
     */
    public boolean getLost() {
        return this.lost;
    }

    /***
     *
     * @param lost state
     */
    public void setLost(boolean lost) {
        this.lost = lost;
    }

    /***
     * getter de win, si on gagne la partie
     * @return le boolean qui donne l'état de la partie
     */
    public boolean getWin(){return this.win;}

    /***
     * Setter de win, état de la partie, gagnée ou non
     * @param win set l'état de la partie, boolean
     */
    public void setWin(boolean win){this.win = win;}

    /***
     *
     * @return if the party is started
     */
    public boolean isStarted(){return started;}

    /***
     *
     * @param started Party state
     */
    public void setStarted(boolean started){this.started=started;}

    public Gui getGui(){return this.gui;}

    /**
     * Construction de tout - constructeur de la classe, appelé par le main
     */
    public Demineur() {
        super("Démineur");
        ch.placeMines();
        ch.afficherMines();
        //System.out.println();
        //System.out.print(ch);

        gui = new Gui(this);

        setContentPane(gui);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }



    /**
     *
     * @param arg non utilisé
     */
    public static void main(String arg[]) {

        System.out.println("Bonjour et Bienvenue dans mon programme de Démineur");
        new Demineur();

    }

    public static void quit() {
        System.exit(0);
    }

    /***
     * Getter de champ, utilisé pour passer dans le GUI le plus souvent
     * @return
     */
    public Champ getChamp(){
        return ch;
    }

    /***
     * Fonction qui permet de savoir si toutes les cases qui ne contenaient pas de cases ont été cliquées
     * @return le booléen sur l'état de la partie, gangée ou pas
     */
    public boolean isWin() {
        /*System.out.println("nb cases decouvertes "+ nbCasesDecouvertes);
        System.out.println("nb mines "+ ch.getNbmines());
        System.out.println("dim X  "+ ch.getDimX());
        System.out.println("dim Y  "+ ch.getDimY());*/
        return nbCasesDecouvertes+ch.getNbmines() == ch.getDimX()*ch.getDimY();
        /*boolean win = nbCasesDecouvertes+ch.getNbmines() == ch.getDimX()*ch.getDimY();
        if(win){
            savScores();
            return win;
        }*/
    }

    /***
     * Fonction appelée par le bouton qui connect au serveur, les paramètre sont donnés dans le
     * @param host ip du serveur donné par le GUI
     * @param port port donné par le GUI
     * @param pseudo pseudo du joueur donné par le GUI
     */
    public void connect2server(String host, String port, String pseudo) {
        int port_int = Integer.parseInt(port);
        gui.addMsg("\nTrying to connect to :"+host+" : "+port_int);
        try{
            Socket sock = new Socket(host, port_int);
            gui.addMsg("\nConnexion OK");
            setConnected(true);
            setLost(false);

            process = new Thread(this);

            in = new DataInputStream(sock.getInputStream());
            out =new DataOutputStream(sock.getOutputStream());
            out.writeUTF(pseudo);

            process.start();

        } catch(UnknownHostException e){
            gui.addMsg("\nConnexion impossible");
            e.printStackTrace();
        }
        catch(IOException e){
            gui.addMsg("\nConnexion impossible");
            e.printStackTrace();
        }
    }

    /***
     * process d'écoute de ce qui vient du serveur, tourne toujours
     */
    public void run(){
        //boucle infinie
        while(process !=null){
            try{
                int cmd = in.readInt();
                if(cmd==START){ //en fct de ce que je lis : j'affiche les mines/numéros/fin de partie
                    gui.addMsg("\nLa partie peut commencer");
                } else if(cmd==POS){
                } else if(cmd==START_CPT){
                    this.getGui().getCompteur().startCpt();
                } else if(cmd== SET_LOST_FALSE){
                    setLost(false);
                } else if(cmd==IS_MINES && !isWonConnected()){
                    setBombeConnected(true);
                    int x = in.readInt();
                    int y = in.readInt();
                    getGui().getTabCases()[x][y].repaint();
                    setLost(true);
                    gui.addMsg("\nClick sur Bombe");
                    JOptionPane.showMessageDialog(null, "La partie a été perdue par "+in.readUTF());
                } else if(cmd==IS_NOT_MINE){
                    int x = in.readInt();
                    int y = in.readInt();
                    int nbVoisin = in.readInt();
                    setNBVoisConnected(nbVoisin);
                    String joueurClick = in.readUTF();
                    setJoueurClicked(joueurClick);
                    setColorConnected(in.readInt());
                    gui.addMsg("\nClick sur regular case par "+getJoueurClicked());
                    getGui().getTabCases()[x][y].repaint();
                } else if(cmd==IS_CLICKED){
                    gui.addMsg("\nLa case a déjà été cliquée par " + in.readUTF());
                } else if(cmd==WON){
                    JOptionPane.showMessageDialog(null, "La partie a été gagnée par "+in.readUTF());
                    setWonConnected(true);
                }
            } catch(IOException e){
                e.printStackTrace();
            }

        }

        //lecture dans in : lecture de la commande et lecture du joueur qui a cliqué en x;y


    }

    /***
     *setter de la couleur du joueur en mode connecté
     * @param readInt couleur codé sur un int
     */
    public void setColorConnected(int readInt) {
        this.colorConnected = new Color(readInt);
    }

    /***
     * Getter de la couleur d'un joueur en mode connecté,
     * @return la couleur d'un joueur en mode connecté
     */
    public Color getColorConnected(){
        return this.colorConnected;
    }

    /***
     * Indique s'il y a une bombe en mode connecté
     * @param b booléen de changement
     */
    private void setBombeConnected(boolean b) {
        this.bombeConnected = true;
    }

    /***
     * Renvoie true si il y a une bombe sur la case là où on a cliqué en mode client/server
     * @return
     */
    public boolean getBombeConnected() {
        return this.bombeConnected;
    }

    /***
     * Donne le nombre de voisins autour, en mode connecté
     * @return entier, nd de voisins
     */
    public int getNBVoisConnected() {
        return this.NBVoisConnected;
    }

    /***
     * Setter de l'attribut nombre de voisins
     * @param NBVoisConnected set le nombre de voisins (int)
     */
    public void setNBVoisConnected(int NBVoisConnected) {
        this.NBVoisConnected = NBVoisConnected;
    }

    /*private void savScores() {
        Path path = Paths.get(FILENAME);
        //Si le fichier n'existe pas
        if(!Files.exists(path)){
            for(int i = 0; i<Level.values().length; i++) {
                if(ch.getLevel())
            }
        }
    }*/
}