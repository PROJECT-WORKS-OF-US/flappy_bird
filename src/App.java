import javax.swing.*;


public class App {
    public static void main(String[] args) throws Exception {
       int boardWidth=360;
       int boardHeight=640;

       JFrame frame =new JFrame("FlappyBird");
    //    frame.setVisible(true);
       frame.setSize(boardWidth,boardHeight);
       frame.setLocationRelativeTo(null);
       frame.setResizable(false);
       frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    Flappybird falppybird=new Flappybird();
    frame.add(falppybird);
    frame.pack();
    falppybird.requestFocus();
    frame.setVisible(true);
    }
}
