package emse.ismin.demineur;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.net.ServerSocket;

public class ServeurGui extends JPanel {

    private JButton startBut = new JButton("Start Partie");
    private Serveur serveur;
    private JTextArea msAreas = new JTextArea();

    ServeurGui(Serveur serveur){
        this.serveur = serveur;

        setLayout(new BorderLayout());
        msAreas.setEditable(false);
        add(new JLabel("Serveur Démineur 2019"), BorderLayout.NORTH);
        add(msAreas, BorderLayout.CENTER);
        add(startBut, BorderLayout.SOUTH);

    }

    /***
     * Quand on veut ajouter du texte dans la texte box de l'interface serveur, on appelle cette fonction
     * @param ch texte à afficher
     */
    public void addMsg(String ch){
        msAreas.append(ch);
    }
}
