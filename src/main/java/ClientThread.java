import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.sql.Array;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;


public class ClientThread implements Runnable {

    private Socket s;//здесь будем хранить ссылку на наш сокет
    private InputStreamReader inStream;
    private OutputStreamWriter outStream;
    private PrintWriter out;
    private Thread t;
    private Array codSity;
    private String myname;
    private Integer kod_tit;
    private Date calendar = Calendar.getInstance().getTime();
    ClientThread(Socket s ) {   //конструктор,в который мы передаем
        this.s = s;

    }

    @Override
    public void run() {
        Scanner scanner;
        String message;
        ClientThread p = ClientThread.this;
        t = Thread.currentThread();

        try {
            inStream = new InputStreamReader(s.getInputStream(),"UTF8");//входящий поток данных
            outStream = new OutputStreamWriter(s.getOutputStream(),"UTF8");//исходящий поток
        } catch (IOException e) {                e.printStackTrace();            }

        while( s != null ){ //пока сокет "жив"
            StoreTest store = new StoreTest();
            SimpleDateFormat formatTime = new SimpleDateFormat("[HH:mm:ss]");
            out = new PrintWriter(outStream,true);//создаем объект, который будет писать в исходящий поток
            scanner=new Scanner(inStream);//слушем входящий поток

            while(scanner.hasNextLine()) {//если мы вручную не останавливаем сокет и есть сообщение
                message = scanner.nextLine();   //считываем его
                Date dat = new Date();
                System.out.println(message);
                try {
                    if (message.startsWith("#"))
                        switch (message.substring(0, 4)) {
                            case "#reg":
                                System.out.println(message + " взял в работу " + p.getMyName());
                                get_incident.get_work_incident(p, message.substring(4));
                                break;
                            case "#usr":
                                t.setName(message.substring(4, message.indexOf('#', 4)));
                                kod_tit = Integer.valueOf(message.substring(message.lastIndexOf('#') + 1));
                                codSity = StartSocket.registrationObs(p);
                                if ( codSity == null)
                                    break;
                                else
                                    System.out.println(codSity);
                            case "#inc":
                                if(codSity != null) {
                                    sendmessage("&~Вкл");
                                    get_incident.select(p, codSity);
                                }
                                break;
                            case "#get":
                                StartSocket.start_soc.notifyall(message.substring(4));
                                break;
                            default:
                                try {
                                    sendmessage(store.storeTest(message));
                                } catch (IOException | InterruptedException | NotBoundException e) {
                                    e.printStackTrace();
                                    System.out.println("-->" + e);
                                }
                        }
                    else {
                        if (message.startsWith("*")) {
                            get_incident.registration(message.substring(1), p);
                            codSity = StartSocket.registrationObs(p);
                            if ( codSity == null)
                                break;
                            else
                                System.out.println(codSity);
                        }
                        System.out.println(formatTime.format(dat) + message);
                        StartSocket.start_soc.notifyall(formatTime.format(dat) + message );
                    }
                }
                catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        StartSocket.removeObs(p);
    }



    Thread getT()   {       return t;       }

    void putMyName(String MyName) { this.myname = MyName;}
    Date getDateConnect() { return calendar; }
    String getMyName() { return myname;     }
    Integer getKod_tit() { return kod_tit;  }


    // функция отправки клиенту сообщения
    synchronized void sendmessage(String s) {        out.println(s);
    //  System.out.println(s);
    }

}
