package network1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class NewServer {

    static ArrayList<NewClient> clients = new ArrayList<>();
    static ArrayList<String> usernames = new ArrayList<>();
    static ArrayList<String> passwords = new ArrayList<>();

    // ====== التواريخ المتاحة ======
    static String[] dates = {
        "2025-10-16", "2025-10-17", "2025-10-18",
        "2025-10-19", "2025-10-20", "2025-10-21", "2025-10-22"
    };

    // ====== القوالب الأساسية للغرف ======
    static String[] standardTemplate = {"Sakura-1", "Sakura-2", "Sakura-3", "Sakura-4", "Sakura-5"};
    static String[] premiumTemplate  = {"Fuji-1", "Fuji-2", "Fuji-3", "Fuji-4", "Fuji-5"};
    static String[] suiteTemplate    = {"Koi-1", "Koi-2", "Koi-3", "Koi-4", "Koi-5"};

    // ====== مصفوفات الغرف لكل تاريخ ======
    static String[][] standardRoomsPerDate = new String[dates.length][standardTemplate.length];
    static String[][] premiumRoomsPerDate  = new String[dates.length][premiumTemplate.length];
    static String[][] suiteRoomsPerDate    = new String[dates.length][suiteTemplate.length];

    static {
        // ننسخ القوالب إلى كل يوم
        for (int i = 0; i < dates.length; i++) {
            System.arraycopy(standardTemplate, 0, standardRoomsPerDate[i], 0, standardTemplate.length);
            System.arraycopy(premiumTemplate, 0, premiumRoomsPerDate[i], 0, premiumTemplate.length);
            System.arraycopy(suiteTemplate, 0, suiteRoomsPerDate[i], 0, suiteTemplate.length);
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(9090);
        System.out.println(" Server started on port 9090 ");

        while (true) {
            Socket client = serverSocket.accept();
            System.out.println("New client connected!");
            NewClient clientThread = new NewClient(client, clients);
            clients.add(clientThread);
            new Thread(clientThread).start();
        }
    }
}
