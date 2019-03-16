import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class testPhone {

    private static BufferedReader reader;
    private static PrintWriter writer;
    private String phone, Host;
    testPhone(String phone, String Host) {
        this.phone = phone; this.Host = Host;
    }

    String test() throws IOException {
        return Forming(InitTest(Host, phone));
    }

    private String Forming(BufferedReader rd) throws IOException {
        Boolean Shut = true;
        StringBuilder mes = new StringBuilder("&#");
        String message;
        String []reply = {
                "A->ground AC voltage             (V)   ",
                "B->ground AC voltage             (V)   ",
                "A->B      AC voltage             (V)",
                "A->ground DC voltage             (V)   ",
                "B->ground DC voltage             (V)   ",
                "A->B      DC voltage             (V)",
                "A->ground insulation resistance  (ohm)   ",
                "B->ground insulation resistance  (ohm)   ",
                "A->B insulation resistance       (ohm)",
                "A->B loop resistance             (ohm)  ",
                "A->B polarity reversal resistance(ohm)  ",
                "A->ground capacitance            (uF)   ",
                "B->ground capacitance            (uF)   ",
                "A->B capacitance                 (uF)  ",
                "telephone number not existing",
                "line mixes with others",
                "Phone not connected",
                "Wire break",
                "line grounding",
                "Mixed with itself",
                "Self-mixed (small resistance, self-mixed)",
                "Off-hook",
                "Normal",
                "earth leakage",
                "AB grounding",
                "Contact with the power line",
                "poor insulation"
        };

        String []reply_rus = {
                "A->земля (~V):",
                "B->земля (~V):",
                "A->B (~V):",
                "A->земля (-V):",
                "B->земля (-V):",
                "A->B (-V):",
                "A->земля (R из.):",
                "B->земля (R из.):",
                "A->B (R из.):",
                "A->B (R шлейфа):",
                "A->B (R поляр.):",
                "A->земля(емк.):",
                "B->земля(емк.):",
                "A->B(емкость):",
                "Номер не существует",
                "провод сообщается с другим проводом",
                "Телефон не подключен",
                "Обрыв линии",
                "провод - земля",
                "короткое на линии",
                "Короткое между проводами",
                "Трубка не на месте",
                "Норма.",
                "пониженная изоляция",
                "AB земля",
                "контакт с линией электропередач",
                "недостаточная изоляция"
        };

        while(Shut) {
            message = rd.readLine();
            for( int i = 0; i < reply.length ; i++)
                if(message.contains(reply[i]))
                    mes.append(message.trim().replace(reply[i], reply_rus[i])).append("|");

            if( message.contains("Conclusion")  ) {
                mes = new StringBuilder(mes.toString().replace("Conclusion                                ", "Результат:"));
                writer.print("exit");
                writer.print("exit");
                writer.print("exit");
                Shut = false;
            }

            if(message.contains("Failure")) {
                mes = new StringBuilder(mes.toString().replace("Failure", "Ошибка:"));
                writer.print("exit");
                writer.print("exit");
                writer.print("exit");
                Shut = false;
            }
        }return mes.toString();

    }

    private BufferedReader InitTest(String Host, String phone) throws IOException {
        int PortNumber = 23;
        String[] command = {
                "enable",
                "configure terminal",
                "test",
                "pots loop-line-test telno " + phone + " busy 0",
                "\n"
        };

        Socket sock = new Socket(Host, PortNumber);

        reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        writer = new PrintWriter(sock.getOutputStream(), true);

        login(" name:", "md");
        login(" password:", "1");

        for (String str : command) {
            writer.println(str);
            //    System.out.println(read(str + "\r\n"));
        }
        return reader;
    }

    private void login(String s, String i) throws IOException {
        while (true) {
            String mess = read(s);
            System.out.println(mess);
            if (mess.contains(s)) {
                writer.println(i);
                break;
            }
        }
    }

    private String read(String str) throws IOException {
        StringBuilder s = new StringBuilder();
        do {
            s.append(String.valueOf((char) reader.read()));
        } while (!s.toString().contains(str));
        return s.toString();
    }
}



