/*
package network1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;


public class NewClient implements Runnable {


  private Socket client;
private BufferedReader in;
private PrintWriter out;
private ArrayList<NewClient> clients;

private String userName = null;

  public NewClient (Socket c,ArrayList<NewClient> clients) throws IOException
  {
    this.client = c;
    this.clients=clients;
    in= new BufferedReader (new InputStreamReader(client.getInputStream())); 
    out=new PrintWriter(client.getOutputStream(),true); 
  }
 
  public void run ()
  {
         try {
            // === ADD: طلب اسم المستخدم وكلمة المرور ===
            out.println("Enter username:");           // سيرفر يطلب الاسم
            String username = in.readLine();
            if (username == null) {
                client.close();
                return;
            }

            out.println("Enter password:");           // سيرفر يطلب الباسوورد
            String password = in.readLine();
            if (password == null) {
                client.close();
                return;
            }

            // نحمي الوصول للقوائم المشتركة بواسطة synchronized على القوائم في NewServer
            synchronized (NewServer.usernames) {
                int idx = NewServer.usernames.indexOf(username);
                if (idx == -1) {
                    // مستخدم جديد -> نسجله (نضيف الاسم والباسورد)
                    NewServer.usernames.add(username);
                    NewServer.passwords.add(password);
                    out.println("REGISTERED_SUCCESSFULLY");
                    System.out.println("New user registered: " + username);
                    this.userName = username;
                } else {
                    // مستخدم موجود -> نتحقق من الباسورد
                    String saved = NewServer.passwords.get(idx);
                    if (saved.equals(password)) {
                        out.println("LOGIN_SUCCESS");
                        System.out.println("User logged in: " + username);
                        this.userName = username;
                    } else {
                        out.println("WRONG_PASSWORD");
                        System.out.println("Failed login attempt for: " + username);
                        client.close();
                        return;
                    }
                }
            }

            // === بعد المصادقة نتابع العمل الأصلي: استقبال وارسال الرسائل ===
            // ملاحظة: احتفظت بشكل عام على منطقك الأصلي: القراءة من الكونسول وإرسالها للكل
            String request;
            while (true) {
                request = in.readLine();
                if (request == null) break;
                // لو تبين تسمعين إلى أوامر خاصة تقدر تفحصي request هنا
                outToAll((userName != null ? userName + ": " : "") + request);
            }
         }
   catch (IOException e){
       System.err.println("IO exception in new client class");
       System.err.println(e.getStackTrace());
   }
         
finally{
    out.close();
       try {
           in.close();
       } catch (IOException ex) {
          ex.printStackTrace();
       }
}
  }
    private void outToAll(String substring) {
for (NewClient aclient:clients){
   aclient.out.println(substring); 
}
    }
}
*/
package network1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;



public class NewClient implements Runnable {

    private final Socket client;
    private final BufferedReader in;
    private final PrintWriter out;
    private final ArrayList<NewClient> clients;

    private String userName = null;
    private String roomType = null;   // standard | premium | suite
    private String chosenDate = null; // YYYY-MM-DD

    public NewClient(Socket c, ArrayList<NewClient> clients) throws IOException {
        this.client = c;
        this.clients = clients;
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.out = new PrintWriter(client.getOutputStream(), true);
    }

    @Override
    public void run() {
        try {
            // === 1) طلب اسم المستخدم وكلمة المرور ===
            out.println("Enter username:");
            String username = in.readLine();
            if (username == null) {
                client.close();
                return;
            }

            out.println("Enter password:");
            String password = in.readLine();
            if (password == null) {
                client.close();
                return;
            }

            // نحمي الوصول لقوائم المستخدمين في السيرفر
            synchronized (NewServer.usernames) {
                int idx = NewServer.usernames.indexOf(username);
                if (idx == -1) {
                    // مستخدم جديد -> تسجيل جديد
                    NewServer.usernames.add(username);
                    NewServer.passwords.add(password);
                    out.println("REGISTERED_SUCCESSFULLY");
                    System.out.println("New user registered: " + username);
                    this.userName = username;
                } else {
                    // مستخدم موجود -> تحقق من الباسوورد
                    String saved = NewServer.passwords.get(idx);
                    if (saved.equals(password)) {
                        out.println("LOGIN_SUCCESS");
                        System.out.println("User logged in: " + username);
                        this.userName = username;
                    } else {
                        out.println("WRONG_PASSWORD");
                        System.out.println("Failed login attempt for: " + username);
                        client.close();
                        return;
                    }
                }
            }

            // ===== 2) Ask for ROOM TYPE =====
            out.println("Choose room type (standard/premium/suite):");
            roomType = in.readLine();

            // ===== 3) Ask for DATE =====
            out.println("Enter date (YYYY-MM-DD):");
            chosenDate = in.readLine();

            // ===== 4) Show available rooms =====
            out.println("AVAILABLE:");
            String list = listAvailableRooms();
            out.println(list.isEmpty() ? "No available rooms" : list);

            // ===== 5) Ask for room name and confirm =====
            out.println("Enter ROOM NAME to reserve (e.g., Sakura-1):");
            String roomName = in.readLine();

            String result = confirmReservation(roomName);
            out.println(result); // "OK,ReservationDone..." or error message

            out.println("Done. Goodbye.");

        } catch (IOException e) {
            System.out.println("Connection error: " + e.getMessage());
        } finally {
            closeQuiet();
        }
    }

    // List available rooms based on chosen type
    private String listAvailableRooms() {
        if (roomType == null || chosenDate == null) return "";
        String[] rooms;
        if ("standard".equalsIgnoreCase(roomType)) {
            rooms = NewServer.standardRooms;
        } else if ("premium".equalsIgnoreCase(roomType)) {
            rooms = NewServer.premiumRooms;
        } else if ("suite".equalsIgnoreCase(roomType)) {
            rooms = NewServer.suiteRooms;
        } else {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (String r : rooms) {
            sb.append(r).append(' ');
        }
        return sb.toString().trim();
    }

    // Confirm reservation
    private String confirmReservation(String roomName) {
        if (roomType == null || chosenDate == null || roomName == null || roomName.isEmpty()) {
            return "ERR,MissingData";
        }

        boolean exists = false;
        if ("standard".equalsIgnoreCase(roomType)) {
            for (String r : NewServer.standardRooms)
                if (r.equalsIgnoreCase(roomName)) { exists = true; break; }
        } else if ("premium".equalsIgnoreCase(roomType)) {
            for (String r : NewServer.premiumRooms)
                if (r.equalsIgnoreCase(roomName)) { exists = true; break; }
        } else if ("suite".equalsIgnoreCase(roomType)) {
            for (String r : NewServer.suiteRooms)
                if (r.equalsIgnoreCase(roomName)) { exists = true; break; }
        }

        if (!exists) return "ERR,RoomNotInSelectedType";
        return "OK,ReservationDone for " + roomName + " on " + chosenDate + " (" + roomType + ")";
    }

    // Safely close streams
    private void closeQuiet() {
        try { out.close(); } catch (Exception ignore) {}
        try { in.close(); } catch (Exception ignore) {}
        try { client.close(); } catch (Exception ignore) {}
    }
}
