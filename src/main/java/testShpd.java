import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;

class testShpd {
    private String service;
    private String n_inc;
    private StringBuilder s;
    testShpd(String service, String n_inc) {
        this.service = service;
        this.n_inc = n_inc;
    }


    String test(ArrayList<String> str){

        String url_get_page[] = {
                "http://10.183.116.238/cgi-bin/getBRM.cgi?uslogin=",
                "http://10.183.116.238/cgi-bin/getTechData.php?svc=",   //  3513256145;PON;74-MIASS-AGG004-O6;10.228.36.197;3;7;5;;;;
                "http://10.183.116.238/cgi-bin/getline.cgi?uslogin="    //  ARGUS_port   : 74-MIASS-AGG004-O6(10.228.36.197)-3/7/5      ONT_distance : 0        Rx n/a
        };

        try {

        if( service.length() > 0) {
            s = new StringBuilder("&#Услуга : " + service.substring(1));
            s.append(Forming(InitTest(  url_get_page[0] + service.substring(1)))); //  User-Login: Start-time: BRAS: IP Address:  MAC Address:  Speed-Limit:  Output Policy Map:  Agent Circuit ID:
            String r = geting(InitTest(url_get_page[1] + service.substring(1)));
            if( r.length() > 0 )
                s.append(r);
            else
                s.append(Forming(InitTest(url_get_page[2] + service.substring(1))));
            System.out.println("s->" +s);
        }
        else {
            //ArrayList<String> str = get_incident.get_td(n_inc.substring(n_inc.indexOf('-') + 1));
            s = new StringBuilder("&#Услуга : " + str.get(0));
            for(String ss: str)
                System.out.println(ss);
            s.append(Forming(InitTest(  url_get_page[0] + str.get(0))));
            String r = SNMPManager.snmp_get(str);
            if(r.length() > 0 )
                s.append(r);
            else
                s.append(Forming(InitTest(url_get_page[2] + str.get(0))));
            System.out.println("s->" +s);
        }


        }catch (IOException e) {
            return s.toString() + "|! : Повторите тест позже|" + e + "|";
        }
        //catch (SQLException e) {            e.printStackTrace();            return s.toString() + "|! : Нет тех.данных|" + e + "|"; }

        return s.toString() + "|";
    }

    private String geting(BufferedReader rd) throws IOException {
        String r;
        ArrayList<String> list = new ArrayList<String>();

        r = rd.readLine();

        for (int a = r.indexOf(';') + 1; r.indexOf(';', a + 1) != -1; a = r.indexOf(';', a + 1) + 1)
            list.add(r.substring(a, r.indexOf(';', a)));

        System.out.println("list=" +  list);
        switch (list.get(0)) {
            case "FTTx":
            case "BShPD":
            case "N.A.":
                return "|Результат : услуга "+ r.substring(0, r.indexOf(';')) +" пока не тестируется.";
            default:
                try {
                    return SNMPManager.snmp_get( list);
                } catch (BindException e) {                    e.printStackTrace();                    return "";                }
        }
    }
    private BufferedReader InitTest(String url_get_page) throws IOException {

        URL url = new URL(url_get_page);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        if (connection != null) {
            connection.setReadTimeout(60 * 1000); // ожидание на 5 сек
            connection.setDoOutput(true); // соединение доступно для вывода
            connection.setUseCaches(false); // не использовать кэш
            connection.setRequestMethod("GET"); // метод post
            connection.setRequestProperty("connection", "keep-alive");
            connection.setRequestProperty("Charset", "UTF-8");
        }
        connection.connect();
        return new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF8"));
    }
    private String Forming(BufferedReader rd) throws IOException {
        StringBuilder mes = new StringBuilder("");
        String line;
        String[] StringKey = {
                "ARGUS_port  ",
                "IP Address:",
                "MAC Address:",
                "Speed-Limit:",
                "Port_Profile",
                "SNR         ",
                "Attenuation",
                "Link_Speed",
                "Output Policy Map:",
                "ONT_SW_Version   :",
                "ONT_Equipment-ID :",
                "Rx_Power     :",
                "OLT Rx",
                "ONT_distance :"};
        String[] StrKeyRus = {
                "Тех.данные",
                "IP адрес  :",
                "MAC адрес :",
                "M.скорость:",
                "Профиль   ",
                "Сигнал/шум",
                "Затухание",
                "Скорость",
                "Политика  :",
                "Версия ПО ONT :",
                "Тип оборуд.:",
                "Rx мощность:",
                "Затухание :",
                "Растояние :"};
        while ((line = rd.readLine()) != null) {

            if (line.startsWith("ARGUS_port") & line.indexOf("not_found") > 0) return "/ Записей не найдено.";
            for (int i = 0; i < StringKey.length; i++)
                if (line.startsWith(StringKey[i]))
                    if(!mes.toString().contains(StrKeyRus[i])) {
                        if (line.startsWith("ARGUS_port"))
                            mes.append("| Сет. имя :").append(line.substring(line.indexOf(":") + 1, line.indexOf("("))).append("| Ip адрес  : ").append(line.substring(line.indexOf("(") + 1, line.indexOf(")"))).append("| порт/слот  : ").append(line.substring(line.indexOf(")-") + 2));
                        else
                            mes.append("| ").append(line.replace(StringKey[i], StrKeyRus[i]));
                    }
        }
        return mes.toString();
    }

}