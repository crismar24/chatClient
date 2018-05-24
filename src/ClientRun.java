import javax.swing.*;

public class ClientRun {
    public static void main(String[] args) {
        Client yury;
        yury = new Client("127.0.0.1");
        yury.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        yury.start();
    }
}
