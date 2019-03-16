import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

class testRTS {

    private String phone, Host;

    testRTS(String phone, String Host) {
        this.phone = phone; this.Host = Host;
    }

    String test() throws IOException {
       return Forming(InitTest( Host, phone));
    }

    private String Forming(BufferedReader rd) throws IOException {
        Boolean Shut = true;
        String mes;
        String message;
        String []reply = {
                "Result",
                "A-G AC voltage",
                "B-G AC voltage",
                "A-B AC voltage",
                "A-G DC voltage",
                "B-G DC voltage",
                "A-B DC voltage",
                "A-G resistance",
                "B-G resistance ",
                "A-B resistance",
                "A-G capacitance",
                "B-G capacitance",
                "A-B capacitance",
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
                "poor insulation",
                "Conclusion",

        };

        String []reply_rus = {
                "Результат",
                "A->земля (~V)",
                "B->земля (~V)",
                "A->B (~V) ",
                "A->земля (-V)",
                "B->земля (-V)",
                "A->B (-V) ",
                "A->земля (R из.)",
                "B->земля (R из.)",
                "A->B (R из.) ",
                "A->земля(емк.)",
                "B->земля(емк.)",
                "A->B(емкость)",
                "Номер не существует",
                "провод сообщается с другим проводом",
                "Телефон не подключен",
                "Обрыв линии",
                "провод - земля",
                "короткое на линии",
                "Короткое между проводами",
                "Трубка не на месте",
                "Норма",
                "пониженная изоляция",
                "AB земля",
                "контакт с линией электропередач",
                "недостаточная изоляция",
                "Итог",
        };

        StringBuilder mesBuilder = new StringBuilder("&#");
        while(Shut) {
            message = rd.readLine();
            System.out.println("->" +message);

            for( String s : reply)
               if(message.contains(s)) {
                   mesBuilder.append(message.trim().replace('=', ':')).append("|");
                   break;
               }
               if (message.contains("Conclusion") || message.contains("Failure"))
                   Shut = false;
        }
        mes = mesBuilder.toString();
        for (int q = 0; q < reply.length; q++)
            if (mes.contains(reply[q]))
                mes =  mes.replace(reply[q], reply_rus[q]);
        return mes;

    }

    private BufferedReader InitTest(String Host, String phone) throws IOException {
        int PortNumber = 6000;

        String[] command = {
                "LGI: OP=cc08, PWD=cc08;",
                "SET CWSON: SWT=OFF;",
                "STR RTSTI: TST=LL, SDN=K'" + phone + ", UTB=NOTEST;"
        };

        Socket sock = new Socket(Host, PortNumber);

        BufferedReader reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
        PrintWriter writer = new PrintWriter(sock.getOutputStream(), true);

        for (String str : command) {
            writer.println(str);
        }
        return reader;
    }

}
