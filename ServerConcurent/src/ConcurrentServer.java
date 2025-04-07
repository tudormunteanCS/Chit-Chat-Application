import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConcurrentServer {
    private static Map<String, Socket> connectedClients = new HashMap<>();

    public static void main(String[] args) {
        ExecutorService executor = Executors.newFixedThreadPool(10); // You can adjust the pool size as needed

        try (ServerSocket serverSocket = new ServerSocket(12345)) {
            System.out.println("Așteptăm conexiuni de la clienți...");

            while (true) {
                // Așteaptă conectarea unui client
                Socket clientSocket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String email = in.readLine();
                System.out.println("S-a conectat clientul cu email: " + email);
                connectedClients.put(email, clientSocket);
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                // Use the executor to handle each client in a separate thread
                executor.execute(clientHandler);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void oneToOneCastPendingTable(String email) throws IOException {
        Socket clientSocket = connectedClients.get(email);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println("Load Pending Table");
    }

    public static void oneToOneCastMessages(String emailPrimitorMesaj, String emailTrimitatorMesaj) throws IOException {
        Socket clientSocket = connectedClients.get(emailPrimitorMesaj);
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        out.println("Load messages");
        out.println(emailTrimitatorMesaj);
    }
}

class ClientHandler implements Runnable {
    private Socket clientSocket;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            String comanda;
            while ((comanda = in.readLine()) != null) {
                System.out.println("Am primit de la client comanda: " + comanda);
                if (comanda.equals("Update Pending Friend Request")) {
                    String emailToRefreshPendingTable = in.readLine();
                    System.out.println("Am trimis la clientul cu emailul: " + emailToRefreshPendingTable + " sa isi remprospeteze tabelul Pending");
                    ConcurrentServer.oneToOneCastPendingTable(emailToRefreshPendingTable);
                } else if (comanda.equals("Update Messages")) {
                    String emailTrimitatorDeMesaje = in.readLine();
                    String emailClientDeUpdatatMesaje = in.readLine();
                    System.out.println("Am primit de la "+emailTrimitatorDeMesaje + " semnal sa ii remprospatez mesajele lui "+ emailClientDeUpdatatMesaje);
                    ConcurrentServer.oneToOneCastMessages(emailClientDeUpdatatMesaje,emailTrimitatorDeMesaje);

                } else {
                    out.println("Comanda invalida!");
                    System.out.println("Am trimis la client: Comanda invalida!");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
