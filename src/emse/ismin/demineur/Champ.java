package emse.ismin.demineur;

import javafx.scene.chart.StackedBarChart;

import java.util.* ;
/**
 * Champ Java Objet champ de mines
 * @author martincaron
 * @revision 0.0
 */


public class Champ {

    /**
     * Initilisation du tableau
     */
    private boolean [][] tabMines; //il faut faire le new mtn dans les constructeurs parce que ça va bouger selon les constructeur
    int dim;
    int nbmines;
    private static int NBMINES_EASY = 5;
    private static int NBMINES_HARD = 15;
    private static int NBMINES_MEDIUM = 10;
    private static int NBMINES_CUSTOM = 5;

    /***
     *
     * @return le nombre de mines
     */
    public int getNbmines() {
        return nbmines;
    }

    /**
     * Création du tableau
     */
    private Random alea = new Random();

    /**
     * On initilise le tableau des mines
     * @param x nb de lines
     * @param y nb de colonnes
     */
    private void init_table(int x, int y){
        tabMines = new boolean[x][y];
    }

    /**
     * Constructeur sans paramètres
     */
    public Champ() {
        nbmines = 5;
        init_table(5,5);
    }

    /**
     * Constructeur avec paramètre de type Level
     */
    public Champ(Level level){
        newPartie(level);
    }

    /**
     * Constructeur custom
     * @param x custom nb lignes
     * @param y custom nb colonnes
     */
    public Champ(int x,int y){
        init_table(x,y);
        nbmines = NBMINES_CUSTOM;
    }

    /**
     * On place les mines
     */
    void placeMines() {

        //Remise à 0
        for (int i=0; i<tabMines.length; i++) {
            for (int j = 0; j < tabMines[0].length; j++)
                tabMines[i][j] = false;
        }

        //Placement
        int i=0;
        while (i<nbmines) {
            int x = alea.nextInt(tabMines.length);
            int y = alea.nextInt(tabMines[0].length);

            if(!tabMines[x][y]){
                tabMines[x][y] = true;
                i++;
            }

        }
    }

    void afficherMines() {
        for (int i=0; i<tabMines.length; i++) {
            for (int j = 0; j < tabMines[0].length; j++)
                if (tabMines[i][j])
                    System.out.print("X");
                else
                    System.out.print("O");
            System.out.println();
        }

    }

    /**
     * Calcul du nombre de voisins pour un endroit donné
     * @param x ligne de la case à considérer
     * @param y colonne de la case à considérer
     * @return return le bombre de voisins
     */
    int nbVoisins(int x, int y){
        int count = 0;
        for (int i=x-1; i<=x+1; i++){
            for(int j=y-1; j<=y+1; j++){
                try{
                    if(tabMines[i][j])
                        count++;
                }
                catch (ArrayIndexOutOfBoundsException ignored){}
            }
        }
        return count;
    }

    boolean isMIN(int x, int y){
        return(tabMines[x][y]);
    }

    /**
     * Surcharge de la méthode d'affichage des Champs
     * @return rien de particuler, mais obligé pour la surcharge
     */
    public String toString(){
        for(int i=0; i<tabMines.length; i++) {
            for (int j = 0; j < tabMines[0].length; j++) {
                if (tabMines[i][j])
                    System.out.print("X");
                else
                    System.out.print(nbVoisins(i, j));
                }
            System.out.println();
        }
        return "";
    }

    public int getDimX(){
        return tabMines.length;
    }

    public int getDimY(){
        return tabMines[0].length;
    }

    public void newPartie(Level level){
        if(level==Level.EASY){
            this.nbmines = NBMINES_EASY;
            init_table(3,3);
        }
        if(level==Level.MEDUIM){
            nbmines = NBMINES_MEDIUM;
            init_table(20,20);
        }
        if(level==Level.HARD){
            nbmines = NBMINES_HARD;
            init_table(30,30);
        }
        //afficherMines();
    }


}
