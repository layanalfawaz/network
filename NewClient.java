
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

