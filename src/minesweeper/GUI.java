/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package minesweeper;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Date;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author Vinicius Ratzke Servelo;
 */
public class GUI extends JFrame {
    // Variaveis do jogo (mudar a dificuldade)
    private int rows;
    private int cols;
    private int rowSize, colSize;
    private int buttonSize;
    private int spacing;
    private int bombs;
    
    // Mouse
    private int mouseX, mouseY;
    
    // Probabilidade da bomba
    private double probability;
    
    // variaveis de controle
    private int[][] neighbours;
    private boolean[][] mines;
    private boolean[][] revealed;
    private boolean[][] flagged;
    
    // variavel da carinha feliz
    private int smileX, smileY, smileCenterX, smileCenterY;
    private int happiness = 1;
    
    // tempo
    private Date startDate;
    private int seconds = 0;
    private int timeX;
    private int timeY;
    
    // variaveis do estado do jogo
    private int difficulty = 1;
    private boolean victory = false;
    private boolean defeat = false;
    private final String winMessage = "Winner winner chicken dinner!";
    
    // criar os itens
    final JMenuItem setEasy = new JMenuItem("Easy");
    final JMenuItem setMedium = new JMenuItem("Medium");
    final JMenuItem setHard = new JMenuItem("Hard");
    final JMenuItem exitAction = new JMenuItem("Exit");
    
    // posicao da mina clicada
    private int mineRow, mineCol;
    
    public GUI(int rows, int cols, int rowsSize, int colsSize, int buttonSize, int spacing, int bombs) {
        this.initialize(rows, cols, rowsSize, colsSize, buttonSize, spacing, bombs, false);
    }
    
    private void initialize(int rows, int cols, int rowsSize, int colsSize, int buttonSize, int spacing, int bombs, boolean reset) {
        this.rows = rows;
        this.cols = cols;
        this.rowSize = rowsSize;
        this.colSize = colsSize;
        this.buttonSize = buttonSize;
        this.spacing = spacing;
        this.bombs = bombs;
        this.probability = (double) bombs / (rows * cols);
        this.mines = new boolean[rows][cols];
        this.neighbours = new int[rows][cols];
        this.revealed = new boolean[rows][cols];
        this.flagged = new boolean[rows][cols];
        this.mouseX = -100;
        this.mouseY = -100;
        this.smileX = rowsSize/2 - buttonSize/2;
        this.smileY = 5;
        this.smileCenterX = this.smileX + buttonSize/2;
        this.smileCenterY = this.smileY + buttonSize/2;
        this.timeX = rowsSize - 5/2 * buttonSize;
        this.timeY = 5;
        this.happiness = 1;
        this.startDate = new Date();
        this.victory = false;
        this.defeat = false;
        
        // setar as bombas e revelar
        boolean repeating = false;
        while (bombs > 0) {
            for (int col = 0; col < cols; col++) {
                for (int row = 0; row < rows; row++) {
                    if (bombs > 0 && Math.random() < probability && !mines[row][col]) {
                        mines[row][col] = true;
                        bombs--;
                    } else if (!repeating) {
                        mines[row][col] = false;
                    }
                    revealed[row][col] = false;
                    flagged[row][col] = false;
                }
            }
            repeating = true;
        }
        
        // vizinhos
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                int count = 0;
                
                // checar cima
                if (row > 0 && mines[row - 1][col]) {
                    count++;
                }
                                
                // checar baixo
                if (row < rows - 1 && mines[row + 1][col]) {
                    count++;
                }
                
                // checar esquerda
                if (col > 0 && mines[row][col - 1]) {
                    count++;
                }
                
                // checar direita
                if (col < cols - 1 && mines[row][col + 1]) {
                    count++;
                }
                
                // checar cima esquerda
                if (row > 0 && col > 0 && mines[row - 1][col - 1]) {
                    count++;
                }
                
                // checar cima direita
                if (row > 0 && col < cols - 1 && mines[row - 1][col + 1]) {
                    count++;
                }
                
                // checar baixo esquerda
                if (row < rows - 1 && col > 0 && mines[row + 1][col - 1]) {
                    count++;
                }
                
                // checar baixo direita
                if (row < rows - 1 && col < cols - 1 && mines[row + 1][col + 1]) {
                    count++;
                }
                
                neighbours[row][col] = count;
            }
        }
        
        this.setSize(rowSize, colSize);
        this.setLocationRelativeTo(null);

        // tabuleiro e configuracao da janela
        if (!reset) {
             // menu
            JMenuBar menuBar = new JMenuBar();

            // menu
            this.setJMenuBar(menuBar);

            // opcao do jogo
            JMenu gameMenu = new JMenu("Game");
            menuBar.add(gameMenu);

            // acoes do menu
            MenuActions menuActions = new MenuActions();
            this.setEasy.addActionListener(menuActions);
            this.setMedium.addActionListener(menuActions);
            this.setHard.addActionListener(menuActions);
            this.exitAction.addActionListener(menuActions);
            
            // botoes no menu (acabou que essa parte de escolher a dificuldade acabou não funcionando então para a escolha da dificuldade deve mexer no codigo,como esta explicado no minesweeper.java, peço desculpas professor
            gameMenu.add(setEasy);
            gameMenu.add(setMedium);
            gameMenu.add(setHard);
            gameMenu.addSeparator();
            gameMenu.add(exitAction);
            
            this.setTitle("Minesweeper");
            this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            this.setResizable(false);
            this.setVisible(true);

            Board board = new Board();
            this.setContentPane(board);

            Move move = new Move();
            this.addMouseMotionListener(move);

            Click click = new Click();
            this.addMouseListener(click);
        }
    }
    
    private void revealCell(int row, int col) {
        revealed[row][col] = true;
        if (flagged[row][col])
            bombs++;
        flagged[row][col] = false;
        if (!mines[row][col] && neighbours[row][col] == 0) {        // reveal parents of cells with 0 bombs around
            // checar cima
            if (row > 0 && !revealed[row - 1][col]) {
                revealCell(row -1, col);
            }

            // checar baixo
            if (row < rows - 1 && !revealed[row + 1][col]) {
                revealCell(row + 1, col);
            }

            // checar esquerda
            if (col > 0 && !revealed[row][col - 1]) {
                revealCell(row, col - 1);
            }

            // checar direita
            if (col < cols - 1 && !revealed[row][col + 1]) {
                revealCell(row, col + 1);
            }
                
            // checar cima esquerda
            if (row > 0 && col > 0 && !revealed[row - 1][col - 1]) {
                revealCell(row - 1, col - 1);
            }

            // checar cima direita
            if (row > 0 && col < cols - 1 && !revealed[row - 1][col + 1]) {
                revealCell(row - 1, col + 1);
            }

            // checar baixo esquerda
            if (row < rows - 1 && col > 0 && !revealed[row + 1][col - 1]) {
                revealCell(row + 1, col - 1);
            }

            // checar baixo direita
            if (row < rows - 1 && col < cols - 1 && !revealed[row + 1][col + 1]) {
                revealCell(row + 1, col + 1);
            }
        }
    }
    
    private boolean checkLose() {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                if (revealed[row][col] && mines[row][col])
                    return true;
            }
        }
        return false;
    }
    
    private boolean checkWin() {
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                if ((!revealed[row][col] && !flagged[row][col]) || mines[row][col] != flagged[row][col])
                    return false;
            }
        }
        return true;
    }
    
    private int checkGameStatus() {
        defeat = checkLose();
        victory = checkWin();
        if (victory) {              // mensagem de vitoria
            JOptionPane.showMessageDialog(new JFrame(), winMessage, "Ganhou!", JOptionPane.INFORMATION_MESSAGE);
        } else if (defeat) {        // revela as bombas
            for (int col = 0; col < cols; col++) {
                for (int row = 0; row < rows; row++) {
                    if (mines[row][col])
                        revealed[row][col] = true;
                }
            }
        }
        return defeat ? 2 : 1;
    }
    
    private void resetAll() {
        this.setDifficultAndInitialize();
    }
    
    private void setDifficultAndInitialize() {
        switch (difficulty) {
            case 1:
                this.initialize(9, 9, 360, 455, 40, 5, 10, true);
                break;
            case 2:
                this.initialize(16, 16, 530, 620, 530/16, 4, 40, true);
                break;
            default:
                this.initialize(16, 30, 990, 620, 530/16, 3, 99, true);
                break;
        }
    }
    
    private boolean insideSmile() {
        double mouseDistanceToCenter = Math.sqrt(Math.pow(mouseX - smileCenterX, 2) + Math.pow(mouseY - smileCenterY, 2));
        return mouseDistanceToCenter < buttonSize/2;
    }
    
    private int[] insideBox() {
        int positionX, positionY, buttonSide;
        
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                positionX = spacing + col * buttonSize;
                positionY = 10 + spacing + (row + 1) * buttonSize;
                buttonSide = buttonSize - 2 * spacing;
                if (mouseX >= positionX && mouseX <= positionX + buttonSide && mouseY >= positionY - spacing && mouseY <= positionY + buttonSide) {
                    return new int[]{ row, col };
                }
            }
        }
        
        return new int[]{ -1 };
    }
    
    public class Board extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            // fundo
            g.setColor(Color.DARK_GRAY);
            g.fillRect(0, 0, rowSize, colSize);
            
            int positionX, positionY, buttonSide;
            for (int col = 0; col < cols; col++) {
                for (int row = 0; row < rows; row++) {
                    // posicao e tamanho
                    positionX = spacing + col * buttonSize;
                    positionY = 10 + spacing + (row + 1) * buttonSize;
                    buttonSide = buttonSize - 2 * spacing;
                    
                    // botao padrao
                    g.setColor(Color.GRAY);

                    // efeito hover e se revelado muda a cor
                    if (revealed[row][col]) {
                        g.setColor(Color.DARK_GRAY);
                        if (mines[row][col] && row == mineRow && col == mineCol)
                            g.setColor(Color.RED);
                    } else if (mouseX >= positionX && mouseX <= positionX + buttonSide && mouseY >= positionY - spacing  && mouseY <= positionY + buttonSide) {
                        g.setColor(Color.LIGHT_GRAY);
                    }
                    
                    // pintar os botoes                   
                    g.fillRect(positionX, positionY, buttonSide, buttonSide);
                    
                    // caso o botao ja esteja revelado
                    if (revealed[row][col]) {
                        if (mines[row][col]) {
                            g.setColor(Color.BLACK);
                            g.setFont(new Font("Open sans", Font.BOLD, buttonSize * 3/4));
                            g.drawString("X", positionX + buttonSize/6, positionY + buttonSize * 2/3);
                            g.fillRect(positionX + buttonSize/3, positionY + buttonSize/15, buttonSize/10, buttonSize * 65/100);
                            g.fillRect(positionX + buttonSize/15, positionY + buttonSize/5 + buttonSize/8, buttonSize * 65/100, buttonSize/10);
                            g.fillOval(positionX - spacing + buttonSize/9 + buttonSize/8, positionY- spacing  + buttonSize/9 + buttonSize/8, buttonSize/2, buttonSize/2);
                            g.setColor(Color.WHITE);
                            g.fillOval(positionX + buttonSize/4, positionY + buttonSize/4, buttonSize/10, buttonSize/10);
                        } else {
                            g.setColor(Color.DARK_GRAY);
                            switch (neighbours[row][col]) {
                                case 1:
                                    g.setColor(Color.BLUE);
                                    break;
                                case 2:
                                    g.setColor(Color.GREEN);
                                    break;
                                case 3:
                                    g.setColor(Color.RED);
                                    break;
                                case 4:
                                    g.setColor(new Color(0, 0, 128));
                                    break;
                                case 5:
                                    g.setColor(new Color(178, 34, 34));
                                    break;
                                case 6: 
                                    g.setColor(new Color(72, 209, 204));
                                    break;
                                case 7:
                                    g.setColor(Color.PINK);
                                    break;
                                case 8:
                                    g.setColor(Color.BLACK);
                                    break;
                                default:
                                    break;
                            }
                            g.setFont(new Font("Tahoma", Font.BOLD, buttonSize * 2/3));
                            g.drawString(Integer.toString(neighbours[row][col]), positionX + buttonSize/6, positionY + buttonSize * 2/3);
                        }
                    } else if (flagged[row][col]) {             // if is flagged, show the flag
                        g.setColor(Color.BLACK);
                        g.fillRect(positionX + buttonSize/7, positionY + buttonSize/2, buttonSize/2, buttonSize/8);
                        g.fillRect(positionX + buttonSize/3, positionY + buttonSize/7, buttonSize/8, buttonSize/2);
                        g.fillRect(positionX + buttonSize/7, positionY + buttonSize/7, buttonSize/3, buttonSize/5);
                        g.setColor(Color.RED);
                        g.fillRect(positionX + buttonSize/7 + 2, positionY + buttonSize/7 + 2, buttonSize/3 - 4, buttonSize/5 - 4);
                    }
                }
            }
            
            // flags
            g.setColor(Color.BLACK);
            g.fillRect(0, 5, buttonSize * 2, buttonSize);
            g.setColor(defeat ? Color.RED : victory ? Color.GREEN : Color.WHITE);
            g.setFont(new Font("Digital-7", Font.PLAIN, buttonSize));
            g.drawString(String.format("%03d", bombs), 0, 5 + buttonSize - spacing);
            
            // carinha feliz
            g.setColor(Color.YELLOW);
            g.fillOval(smileX, smileY, buttonSize, buttonSize);
            g.setColor(Color.BLACK);
            
            switch (happiness) {
                case 1:
                    g.fillOval(smileX + buttonSize/5, smileY + buttonSize/4, buttonSize/4, buttonSize/4);
                    g.fillOval(smileX + buttonSize - 2 * spacing - buttonSize/5, smileY + buttonSize/4, buttonSize/4, buttonSize/4);
                    g.fillRect(smileX + buttonSize/5, smileY + buttonSize * 5/7, buttonSize * 5/8, buttonSize/10);
                    g.fillRect(smileX + buttonSize/7, smileY + buttonSize * 5/8, buttonSize * 1/8, buttonSize/8);
                    g.fillRect(smileX + buttonSize - spacing - buttonSize/7, smileY + buttonSize * 5/8, buttonSize * 1/8, buttonSize/8);
                    break;
                case 2:
                    g.setFont(new Font("Tahoma", Font.BOLD, buttonSize * 1/3));
                    g.drawString("X", smileX + buttonSize/5, smileY + buttonSize/2);
                    g.drawString("X", smileX + buttonSize - 2 * spacing - buttonSize/5, smileY + buttonSize/2);
                    g.fillRect(smileX + buttonSize/5, smileY + buttonSize * 4/7, buttonSize * 5/8, buttonSize/10);
                    g.fillRect(smileX + buttonSize/7, smileY + buttonSize * 5/8, buttonSize * 1/8, buttonSize/8);
                    g.fillRect(smileX + buttonSize - spacing - buttonSize/7, smileY + buttonSize * 5/8, buttonSize * 1/8, buttonSize/8);
                    break;
                default:
                    g.fillOval(smileX + buttonSize/5, smileY + buttonSize/4, buttonSize/4, buttonSize/4);
                    g.fillOval(smileX + buttonSize - 2 * spacing - buttonSize/5, smileY + buttonSize/4, buttonSize/4, buttonSize/4);
                    g.fillOval(smileX + buttonSize/3, smileY + buttonSize * 5/8, buttonSize/3, buttonSize/3);
                    break;
            }

            // tempo
            g.fillRect(timeX, timeY, buttonSize * 2, buttonSize);
            seconds = victory || defeat ? seconds : seconds >= 999 ? 999 : (int) (new Date().getTime() - startDate.getTime()) / 1000;
            g.setColor(defeat ? Color.RED : victory ? Color.GREEN : Color.WHITE);
            g.setFont(new Font("Digital-7", Font.PLAIN, buttonSize));
            g.drawString(String.format("%03d", seconds), timeX, timeY + buttonSize - spacing);
        }
    }
    
    public class Move implements MouseMotionListener {
        @Override
        public void mouseDragged(MouseEvent arg0) {
            
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            mouseX = e.getX();
            mouseY = e.getY() - 50;  // header
        }
        
    }
    
    public class Click implements MouseListener {
        @Override
        public void mouseClicked(MouseEvent e) {
            
        }

        @Override
        public void mousePressed(MouseEvent arg0) {
            happiness = defeat ? 2 : victory ? 1 : 3;
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            int[] buttonXY = insideBox();
            
            if (e.getButton() == MouseEvent.BUTTON1) {                  // clique mouse esquerdo
                if (!defeat && !victory && buttonXY[0] != -1) {
                    if (mines[buttonXY[0]][buttonXY[1]]) {
                        mineRow = buttonXY[0];
                        mineCol = buttonXY[1];
                    }
                    revealCell(buttonXY[0], buttonXY[1]);
                }

                if (insideSmile()) {
                    resetAll();
                }
            } else if (e.getButton() == MouseEvent.BUTTON3) {           // clique mouse direito
                if (!defeat && !victory && buttonXY[0] != -1) {
                    if (flagged[buttonXY[0]][buttonXY[1]]) {
                        flagged[buttonXY[0]][buttonXY[1]] = false;
                        bombs++;
                    } else if (!flagged[buttonXY[0]][buttonXY[1]] && bombs > 0) {
                        flagged[buttonXY[0]][buttonXY[1]] = true;
                        bombs--;
                    }
                }
            }

            happiness = checkGameStatus();
        }

        @Override
        public void mouseEntered(MouseEvent arg0) {
            
        }

        @Override
        public void mouseExited(MouseEvent arg0) {
            
        }
        
    }
    
    public class MenuActions implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource().equals(exitAction)) {
                System.exit(0);
            } else if (e.getSource().equals(setEasy)) {
                difficulty = 1;
            } else if (e.getSource().equals(setMedium)) {
                difficulty = 2;
            } else if (e.getSource().equals(setHard)) {
                difficulty = 3;
            }
            setDifficultAndInitialize();
        }
    }
}