
import org.snmp4j.smi.OID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static java.lang.String.format;

class SNMPManager {

    static String snmp_get(ArrayList<String> td) throws IOException {
        String ip = td.get(2);
        String slot = td.get(3);
        String port = td.get(4);
        String ont = td.get(5);

        System.out.println("ip=" + ip + "sl=" + slot + "port=" + port +"ont=" + ont );
        String SMI = ".1.3.6.1.4.1.";
        String str [][] = null;
        StringBuilder sym = new StringBuilder();
        String typePdu = "";
        List<String> Status = Arrays.asList( "On", "off", "Тест");
        List<String> Speed = Arrays.asList( "(Fix-10M)", "(Fix-100M)", "(Fix-1G)", "(auto)", "(auto-10M)", "(auto-100M)", "(auto-1G)");

        SnmpCommand client = new SnmpCommand("udp:" + ip + "/161") {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        };

        if( !client.start()) {
            System.out.println("error");
            return "";
        }

        Function<String[], String> o = (oidS) -> {
            String s = "";
            try {
                switch (oidS[0]) {
                    case "|Порт 1: ":
                    case "|Порт 2: ":
                    case "|Порт 3: ":
                    case "|Порт 4: ":
                    case "|состояние:\t":
                    case "|Адм.состояние:\t":
                    case "|Опер.состояние:\t":
                        s = oidS[0] + (Status.get(Integer.parseInt(client.getAsString(new OID(oidS[1]), typePdu)) - 1));
                        break;
                    case "speed":
                        s = Speed.get(Integer.parseInt(client.getAsString(new OID(oidS[1]), typePdu)) - 1) ;
                        break;
                    case "|описание :\t":
                        s = oidS[0] + client.getAsString(new OID(oidS[1]), typePdu).replace('|', '!');
                        break;
                    case "|кодовое слово :\t":
                        s = oidS[0] + MyStrOps.myRelSS(MyStrOps::getks, client.getAsString(new OID(oidS[1]), typePdu).replace(":00","") + ':');
                        System.out.println("KS -> " + s);
                        break;
                    case "|Sn :\t":
                        s = oidS[0] + client.getAsString(new OID(oidS[1]), typePdu).replace(":", "");
                        break;
                    case "|затухание(дБм):\t":
                        s = oidS[0] + format("%.1f",(float) Integer.parseInt(client.getAsString(new OID(oidS[1]), typePdu)) / 100);
                        break;
                    case "|пред.скорость:\t":
                    case "|тек.скорость:\t":
                    case "|Max.скорость:\t":
                    case "":
                        s = oidS[0] + format("\t%d", Integer.parseInt(client.getAsString(new OID(oidS[1]), typePdu)) / 1000);
                        break;
                    case "(":
                        s = format(" vl(%d)", Integer.parseInt(client.getAsString(new OID(oidS[1]), typePdu)));
                        break;
                    case "|сигнал / шум:\t":
                    case "|затухан.линии:\t":
                    case "|затух.сигнала:\t":
                    case "-":
                        s = oidS[0].replace("-", "") + format("\t%.1f",(float) Integer.parseInt(client.getAsString(new OID(oidS[1]), typePdu)) / 10);
                        break;
                    default:
                        s = oidS[0] + client.getAsString(new OID(oidS[1]), typePdu);
                }
            } catch (IOException e) {                e.printStackTrace();            }
            return s;
        };

        System.out.println(client.getAsString(new OID(".1.3.6.1.2.1.1.1.0"), typePdu) + "  " + ip);
        SetSe se = new SetSe();
        switch (client.getAsString(new OID(".1.3.6.1.2.1.1.1.0"), typePdu)) {
            case "ECI telecom HiFOCuS broadband access system":
                switch (client.getAsString(new OID(SMI + "1286.1.3.3.1.1.2.131072"), typePdu)) {
                    case "IPNI_APP_2.52.35":
                        str =  se.getSe(String.valueOf(binary.Nport( slot, port, "00")), String.valueOf(binary.Nport( slot, port, "01")), ont, 2);   // комманды
                        break;
                    case "mini_ge_9.01.48":
                        slot = "7";
                        str =  se.getSe(String.valueOf(binary.Nport( slot, port, "00")), String.valueOf(binary.Nport( slot, port, "01")), ont, 1);  // комманды
                        break;
                    case "IPNI_HB_APP_2.00.73":
                    case "se_10.01.64":
                    case "se_10.01.65":
                        str =  se.getSe(String.valueOf(binary.Nport( slot, port, "00")), String.valueOf(binary.Nport( slot, port, "01")), ont, 1);  // комманды
                        break;
                    case "IPNI_HB_APP_5.61.59":
                    default:
                        sym.append("|").append(client.getAsString(new OID(SMI + "1286.1.3.3.1.1.2.131072"), typePdu));
                }
                break;

            case "Huawei Integrated Access Software":
                str =  se.getSe(String.valueOf(4194304000L + 8192 * Integer.parseInt(slot) + 256 * Integer.parseInt(port) ), port, ont, 3);  // комманды;
                break;

            case "24-port 10/100 Ethernet Switch":
                str = se.getSe("", "", ont, 4);     //комманды
                break;
        }
        try {
            for (String s[] : str)
                sym.append(o.apply(s));
        } catch (NullPointerException | NumberFormatException er) {    System.out.println(er + "|" + sym);     return ""; }

        return sym.toString();
    }
}