import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;

class StartSocket {
    static StartSocket start_soc;

    private static ArrayList<ClientThread> ClientList  = new ArrayList<>();

    StartSocket() {
        start_soc = this;
        StartServer();
    }

    private void StartServer() {

        try {
            ServerSocket ss = new ServerSocket( config.PORT_SEND );
            get_incident.start_bd();
            System.out.println("Сервер старт на порту " +config.PORT_SEND + ", порт postgersql " + config.PORT);

            do {
                Socket incoming = ss.accept();
                ClientThread client = new ClientThread(incoming);
                Thread t = new Thread(client);
                t.start();
            } while (ss.isBound());
        } catch (IOException ex) {  System.out.println("Server internal error " + ex.getMessage());        }
    }

    static Array registrationObs(ClientThread o) throws SQLException {

        System.out.println(o.getT());
        for(ClientThread i: ClientList)
            if(i.getT().getName().equals(o.getT().getName()))
                i.getT().interrupt();

        ClientList.removeIf(se -> (se.getT().getName()).equals(o.getT().getName()));
        ClientList.add(o);

        Array kodSity = get_incident.get_worker(o);

        if(  kodSity == null)
            o.sendmessage("Вы не авторизованы.\n Введите ваше имя и инициалы - ваш город\n и поставте перед ним звездочку.\n Например: *Петров К.С.-Аша");
        else {
            o.sendmessage("&*" + o.getMyName());

            for (ClientThread c : ClientList)
                System.out.println(c.getT().getName());
            return kodSity;
        }
        return null;
    }

    static synchronized void removeObs(ClientThread o) {
        o.getT().interrupt();
        System.out.println("Удаляю " + o.getT().getName());
        ClientList.remove(o);
    }

    void notifyObs() {
        for(ClientThread client: ClientList) {

           // get_incident.SendInquiry(client);
            System.out.println("Послано " + client.getT().getName());
        }
    }

    void notifyall(String message) {
        for(ClientThread client: ClientList)
            client.sendmessage(message);
    }
}
