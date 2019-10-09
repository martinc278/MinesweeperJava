package emse.ismin.demineur;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Case extends JPanel implements MouseListener {
    private final static int DIM=50 ;
    String txt = "X";
    private int x,y, nb_vois;
    private Demineur demin;
    private boolean click =false;

    public Case(int x, int y, Demineur demin){
        this.x = x;
        this.y=y;
        this.demin = demin;

        setPreferredSize(new Dimension(DIM, DIM));
        addMouseListener(this);
    }

    public void paintComponent(Graphics gc){
        //super.paintComponent(gc); // appel méthode mère (efface le dessin précedent)
        //gc.drawString(txt, 10,10); // dessin du texte à la position 10, 10
        gc.setColor(Color.gray); // gris
        gc.fillRect(1,1, getWidth(), getHeight());

        if((!demin.getConnected()&&click) || (demin.getConnected()&&!demin.isWonConnected())){
            super.paintComponent(gc);
                if ((!demin.getConnected()&&demin.getChamp().isMIN(x, y)) || (demin.getConnected()&&demin.getBombeConnected())) {
                    BufferedImage image;
                    try {
                        image = ImageIO.read(new File("img/bombe.png"));
                        gc.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    gc.setColor(new Color(0, 150, 136));
                    if(demin.getConnected()){
                        nb_vois = demin.getNBVoisConnected();
                        //gc.setColor(demin.getColorConnected());
                        setBackground(demin.getColorConnected());
                        gc.setFont(new Font("default", Font.BOLD, 16));

                    } else{
                        nb_vois = demin.getChamp().nbVoisins(x, y);
                    }
                    gc.drawString(String.valueOf(nb_vois), getWidth() / 2, getHeight() / 2);
                }
            /*gc.setColor(Color.black); // cyan
            gc.fillRect(1,1, getWidth(), getHeight());*/
        }
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        //Inc le nm de mimes découvertes
        if(!click && !demin.getChamp().isMIN(x,y) && !demin.getLost()){
            demin.setNbCasesDecouvertes(demin.getNbCasesDecouvertes()+1);
        }

        click =true;
        if(!demin.getConnected()) {
            if (!demin.getLost()) {
                //demarrage de la partie
                if (!demin.isStarted()) {
                    demin.getGui().getCompteur().startCpt();
                    demin.setStarted(true);
                    demin.setLost(false);
                }
                repaint();

                //tombe sur une mine
                if (demin.getChamp().isMIN(x, y)) {
                    demin.getGui().getCompteur().stopCpt();
                    JOptionPane.showMessageDialog(null, "You loose !!!");
                    demin.setLost(true);
                } else {
                }
            }
            //Si j'ai gagné
            if (demin.isWin()) {
                demin.getGui().getCompteur().stopCpt();
                JOptionPane.showMessageDialog(null, "You WIN \n Score : " + demin.getGui().getCompteur().getVal());
            }
        }
        if(demin.getConnected() && !demin.getLost()){
            try {
                demin.out.writeInt(8);
                demin.out.writeInt(this.x);
                demin.out.writeInt(this.y);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseReleased(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseEntered(MouseEvent mouseEvent) {

    }

    @Override
    public void mouseExited(MouseEvent mouseEvent) {

    }

    public void newPartie() {
        click=false;
        repaint();
    }
}
