/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package minesweeper;

/**
 *
 * @author Vinicius Ratzke Servelo
 */
public class Minesweeper implements Runnable { // Para escolher a dificuldade apenas tire a linha de comentario da dificuldade desejada
//      GUI gui = new GUI(9, 9, 360, 455, 40, 5, 10);           // Modo Fácil
    GUI gui = new GUI(16, 16, 530, 620, 530/16, 4, 40);   // Modo Intermediário
//    GUI gui = new GUI(16, 30, 990, 620, 530/16, 3, 99);   // Modo Díficil
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        new Thread(new Minesweeper()).start();
    }
    
    @Override
    public void run() {
        while (true) {
            gui.repaint();
        }
    }
}