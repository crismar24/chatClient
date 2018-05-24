import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class Client extends JFrame {
    private JTextField userInputText;
    private JTextArea chatWindow;
    private ObjectOutputStream outputStream;
    private ObjectInputStream inputStream;
    private String message;
    private String serverIP;
    private Socket socket;

    //constructor
    public Client(String host) {
        super("Клиентская часть");
        serverIP = host;
        userInputText = new JTextField();
        userInputText.setEditable(false);
        userInputText.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMessage(e.getActionCommand());
                userInputText.setText("");

            }
        });
        add(userInputText, BorderLayout.NORTH);
        chatWindow = new JTextArea();
        chatWindow.setBackground(Color.LIGHT_GRAY);
        add(new JScrollPane(chatWindow), BorderLayout.CENTER);
        setSize(300, 600);
        setVisible(true);
    }
    //запуск клиента
    public void start() {
        try {
            connectToServer();
            setupStreams();
            whileChatting();

        } catch (EOFException eofException) {
            showMessage("\nКлиент оборвал соединение!!!");

        } catch (IOException ioExceprion) {
            ioExceprion.printStackTrace();
        } finally {
            closeConnection();
        }
    }
    //подключаемся к серверу
    private void connectToServer() throws IOException {
        showMessage("\nПытаемся подключиться, чувачек...");
        //socket = new Socket(InetAddress.getByName(serverIP), 7777);
        socket = new Socket(serverIP, 7777);

        showMessage("\nТеперь ты подключен к: " + socket.getInetAddress().getHostAddress());
    }
    //настройка потоков для отправки и получения сообщений
    private void setupStreams() throws IOException {
        outputStream = new ObjectOutputStream(socket.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(socket.getInputStream());
        showMessage("\nЧувак, твои потоки готовы к работе!!!");
    }
    //обработка данных во время обращения
    private void whileChatting() throws IOException {
        readyToType(true);
        do {
            try {
                message = (String) inputStream.readObject();
                showMessage("\n"+ message);
            } catch (ClassNotFoundException classNotFoundExceprion) {
                showMessage("\nНепонятно!!!");
            }
        } while (!message.equals("СЕРВЕР - *"));
    }

    //закрытие потоков и сокетов
    private void closeConnection() {
        showMessage("\nЗакрываем соединение...");
        readyToType(false);
        try {
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    //отправка сообщений на сервер
    private void sendMessage(String message) {
        try {
            outputStream.writeObject("Клиент - " + socket.getInetAddress().getHostAddress() + " - " + message);
            outputStream.flush();
            showMessage("\nКлиент - " + socket.getInetAddress().getHostAddress() + " - " + message);
        } catch (IOException ioException) {
            chatWindow.append("\nЧто-то пошло не так во время отправки сообщений... ");
            //e.printStackTrace();
        }
    }

    //обновление окна чата
    private void showMessage(final String msg) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                chatWindow.append(msg);
            }
        });
    }

    //установка прав на ввод текста
    private void readyToType(final boolean tof) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                userInputText.setEditable(tof);
            }
        });
    }
}
