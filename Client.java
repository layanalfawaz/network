
package network1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client {
    private static final String Server_IP="192.168.100.70";
    private static final int Server_port=9090;
 
        public static void main(String[] args) throws IOException{
          try(Socket socket = new Socket (Server_IP,Server_port)) {
              
           BufferedReader serverIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));

            // نقرأ طلب اسم المستخدم من السيرفر ونرسله
            String prompt = serverIn.readLine();
            System.out.print(prompt + " ");
            String username = keyboard.readLine();
            out.println(username);

            // نقرأ طلب الباسوورد من السيرفر ونرسله
            prompt = serverIn.readLine();
            System.out.print(prompt + " ");
            String password = keyboard.readLine();
            out.println(password);

            // نقرأ الرد من السيرفر نتيجة التسجيل/الدخول
            String response = serverIn.readLine();
            System.out.println("Server: " + response);

            if ("WRONG_PASSWORD".equals(response)) {
                System.out.println("Authentication failed. Closing connection.");
                return;
            }

            // الآن بعد ما تم التسجيل/الدخول بنجاح، نشغّل thread لقراءة رسائل السيرفر المتبقية
            ServerConnection servcon = new ServerConnection(socket, serverIn);
            new Thread(servcon).start();

            // حلقة إرسال أوامر من الكيبورد (مثل ما عندك أصلاً)
            try {
                while (true) {
                    System.out.print("> ");
                    String command = keyboard.readLine();
                    if (command == null) break;
                    out.println(command);
                    if ("quit".equalsIgnoreCase(command)) break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }}
