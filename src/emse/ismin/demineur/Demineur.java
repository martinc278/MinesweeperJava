package emse.ismin.demineur;

import javax.management.openmbean.InvalidOpenTypeException;
import javax.swing.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 */
public class Demineur extends JFrame implements Runnable{
    public static final String HOSTNAME = "localhost";
    public static final String PSEUDO = "Gros Bill";
    public static final int PORT = 10000;
    private Champ ch = new Champ(Level.EASY);
    private boolean started = false;
    private Gui gui; //création de l'interface graphique
    private boolean lost;
    private int nbCasesDecouvertes = 0;
    private static final String FILENAME = "score.dat";
    public static final int MSG = 0;
    public static final int POS = 1;
    public static final int START = 2;
    public static final int END = 3;

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
     * Construction de tout
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

    public Champ getChamp(){
        return ch;
    }

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

    public void connect2server(String host, String port, String pseudo) {
        int port_int = Integer.parseInt(port);
        gui.addMsg("\nTrying to connect to :"+host+" : "+port_int);
        try{
            Socket sock = new Socket(host, port_int);
            gui.addMsg("\nConnexion OK");

            process = new Thread(this);
            process.start();

            in = new DataInputStream(sock.getInputStream());
            out =new DataOutputStream(sock.getOutputStream());
            out.writeUTF(pseudo);

        } catch(UnknownHostException e){
            gui.addMsg("\nConnexion impossible");
            e.printStackTrace();
        }
        catch(IOException e){
            gui.addMsg("\nConnexion impossible");
            e.printStackTrace();
        }
    }

    public void run(){
        //boucle infinie
        while(process !=null){
            try{
                int cmd = in.readInt();
                if(cmd==MSG){ //en fct de ce que je lis : j'affiche les mines/numéros/fin de partie
                    String msg = in.readUTF();
                    gui.addMsg(msg);
                } else if(cmd==POS){

                }
            } catch(IOException e){
                e.printStackTrace();
            }

        }

        //lecture dans in : lecture de la commande et lecture du joueur qui a cliqué en x;y


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