package emse.ismin.demineur;

import javax.swing.*;
import java.awt.*;

public class Compteur extends JPanel implements Runnable {
    private Thread processTime;
    private int cpt = 0; //compteur
    private static int WIDTH = 60;
    private static int HEIGHT = 30;


    Compteur(){
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
    }
    @Override
    public void run() {
        while (processTime != null) {
            try {
                processTime.sleep(1000);
                cpt = cpt + 1;
                repaint();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            }

        }

    public void paintComponent(Graphics gc){
        super.paintComponent(gc);
        gc.setColor(new Color(255,87,34)); //Material design orange pour le compteur
        gc.drawString(String.valueOf(cpt), getWidth()/2, getHeight()/2);
        gc.setColor(new Color(0,150,136)); //Material design vert pour le cadre autour
        gc.drawRect(0,0, getWidth()-1, getHeight()-1);

    }

    /***
     * Démarrage du compteur
     */
    public void startCpt(){
        cpt = 0;
        processTime = new Thread(this);
        processTime.start();
    }

    /***
     * Fin du compteur
     */
    public void stopCpt(){
        processTime = null;
    }

    public int getVal(){
        return cpt;
    }
}
