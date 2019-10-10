package emse.ismin.demineur;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class Gui extends JPanel implements ActionListener {

    JButton butQuit = new JButton("Quit");
    JMenuItem jMenuItemQuitter = new JMenuItem("Quitter", KeyEvent.VK_Q);
    JMenuItem jMenuItemNouvellePartie = new JMenuItem("Nouvelle Partie");
    JMenuItem mEasy = new JMenuItem("EASY", KeyEvent.VK_E);
    JMenuItem mMedium = new JMenuItem("MEDIUM", KeyEvent.VK_M);
    JMenuItem mHard = new JMenuItem("HARD", KeyEvent.VK_H);

    //Définition des zones de texte pour
    private JTextField hostField = new JTextField(Demineur.HOSTNAME, 20);
    private JTextField pseudoField = new JTextField(Demineur.PSEUDO, 15);
    private JTextField portField = new JTextField(String.valueOf(Demineur.PORT), 15);
    private JButton connexionBut = new JButton("Connect");
    private JTextArea msgArea = new JTextArea("Bienvenue", 20, 20);

    //Ajout du pannel

    Compteur compteur;

    Demineur demin;

    /***
     * Getter de compteur - utilisé en local
     * @return
     */
    public Compteur getCompteur() {
        return compteur;
    }

    /***
     * Getter du tableau de cases
     * @return tableau de cases de la taille du champ
     */
    public Case[][] getTabCases() {
        return tabCases;
    }

    private Case[][] tabCases;

    JPanel pannelMines = new JPanel();

    /***
     * On crée le GUI du démineur dans les cas de réseau ou non, et on lui donne en paramètre le démineur dont il découle
     * @param demin démineur dont le GUI découle
     */
    Gui(Demineur demin){
       this.demin = demin;

       setLayout(new BorderLayout());

       //Title
       JLabel title = new JLabel("Bienvenu");

       //Création du compteur
       compteur = new Compteur(); //Quand on crée un compteur c'est un JPannel parce que Compteur extends (donc hérite) de JPannel, donc c'est un objet de typr JPannel

       //Ajout du compteur graphiquement
       JPanel pannelNord = new JPanel(new BorderLayout());
       pannelNord.add(title, BorderLayout.NORTH);
       pannelNord.add(compteur, BorderLayout.CENTER);

       //Pour que la personne se connecte, on ajoute les informations de connexion au sud de notre JPannel pannelNord
        JPanel pannelConnexion = new JPanel();
        pannelConnexion.add(new JLabel("Serveur"));
        pannelConnexion.add(hostField);
        pannelConnexion.add(pseudoField);
        pannelConnexion.add(portField);
        pannelConnexion.add(connexionBut);
        connexionBut.addActionListener(this);
        pannelNord.add(pannelConnexion, BorderLayout.SOUTH);

        //On ajoute notre pannelNord à notre JPannel Gui
        add(pannelNord, BorderLayout.NORTH);

       //Bouton
        JPanel pannelSud = new JPanel(new BorderLayout());
        pannelSud.add(msgArea, BorderLayout.NORTH);
        butQuit.addActionListener(this);
        pannelSud.add(butQuit, BorderLayout.SOUTH);
        add(pannelSud,BorderLayout.SOUTH);

       /**
        * Pannel du milieu qui display les mines et nb_voisins
        */

       placeCases();

       add(pannelMines, BorderLayout.CENTER);
       JMenuBar jMenuBar = new JMenuBar();
       JMenu jMenuPartie = new JMenu("Partie"); //Création du menu Partie
       JMenu jMenuAide = new JMenu(("Aide")); //Création du menu Aide
       jMenuBar.add(jMenuPartie); //Ajout du menu Partie à la toolbar
       jMenuBar.add(Box.createGlue()); //On ajoute de la place pour écrire à droite mtn
       jMenuBar.add(jMenuAide); //On ajoute à droite du coup le menu aide

       //Gestion de la nouvelle partie et le choix des niveaux
       JMenu menuLevel = new JMenu("Nouvelle partie avec choix du niveaux");
       menuLevel.add(mEasy); //On a défini les niveaux en dehors pour que l'action performed ait accès
       menuLevel.add(mMedium);
       menuLevel.add(mHard);
       jMenuPartie.add(menuLevel); //Ajout du menu de la nouvelle partie avec choix des niveau dans le menu partie
       mEasy.addActionListener(this); //Ajout des nouvelles parties à l'action performed
       mHard.addActionListener(this);
       mMedium.addActionListener(this);

       jMenuPartie.add(jMenuItemQuitter);
       jMenuPartie.add(jMenuItemNouvellePartie);
       jMenuItemQuitter.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.ALT_MASK)); //Racourci clavier cmd q pour quitter
       jMenuItemQuitter.addActionListener(this); //On ajoute au listener
       jMenuItemNouvellePartie.addActionListener(this);
       jMenuItemNouvellePartie.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.ALT_MASK));

       demin.setJMenuBar(jMenuBar);


   }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (actionEvent.getSource() == butQuit || actionEvent.getSource() == jMenuItemQuitter) {
            int rep = JOptionPane.showConfirmDialog(null, "êtes vous sur ?", "Bye Bye", JOptionPane.YES_NO_OPTION, JOptionPane.ERROR_MESSAGE);
            if (rep == JOptionPane.YES_OPTION) {
                Demineur.quit();
            }
        }
        else if (actionEvent.getSource() == jMenuItemNouvellePartie) {
            demin.getChamp().placeMines();
            newPartie();
        }
        else if(actionEvent.getSource()==mEasy){
            demin.getChamp().newPartie(Level.EASY);
            newPartie(Level.EASY);
            demin.setConnected(false);
        }
        else if(actionEvent.getSource()==mMedium){
            demin.getChamp().newPartie(Level.MEDUIM);
            newPartie(Level.MEDUIM);
            demin.setConnected(false);
        }
        else if(actionEvent.getSource()==mHard){
            demin.getChamp().newPartie(Level.HARD);
            newPartie(Level.HARD);
            demin.setConnected(false);
        }

        else if(actionEvent.getSource()==connexionBut){
            demin.connect2server(hostField.getText(), portField.getText(), pseudoField.getText());
        }

    }
    /**
     * Demande à toutes les cases de se réinitialiser
     */
    private void newPartie(){
        //Rémarrage du compteur
        getCompteur().stopCpt();
        demin.setStarted(false);
        demin.setLost(false);
        demin.setWin(false);

        for(int i=0; i<demin.getChamp().getDimX(); i++){
            for(int j=0; j<demin.getChamp().getDimY(); j++){
                tabCases[i][j].newPartie();
            }
        }
    }

    /***
     * Demande à toutes les cases de se réinitialiser
     * @param level
     */
    private void newPartie(Level level){
        //Rémarrage du compteur
        demin.setWonConnected(false);
        getCompteur().stopCpt();
        demin.setNbCasesDecouvertes(0);
        demin.setStarted(false);
        demin.setLost(false);
        pannelMines.removeAll();
        placeCases();
        demin.pack();
    }

    /***
     * Place les case dans le GUI dans le JPannel du milieu (pannelMines)
     */
    private void placeCases(){
        demin.setNbCasesDecouvertes(0);
        pannelMines.setLayout(new GridLayout(demin.getChamp().getDimX(),demin.getChamp().getDimY()));
        tabCases = new Case[demin.getChamp().getDimX()][demin.getChamp().getDimY()];
        for(int i=0; i<demin.getChamp().getDimX(); i++){
            for (int j=0; j<demin.getChamp().getDimY(); j++){
                tabCases[i][j]=new Case (i,j, demin);
                pannelMines.add(tabCases[i][j]);
            }
        }
    }

    /***
     * Méthode d'affichage utilisé du côté client en réseau pour afficher dans la chatbox
     * @param s string à afficher
     */
    public void addMsg(String s) {
        msgArea.append(s);
    }
}