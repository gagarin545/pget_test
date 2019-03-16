import org.snmp4j.smi.OID;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

class changeShpd  extends Store {
    static ArrayList <String> profil = new ArrayList<>(
            Arrays.asList(
                    "*SNR7/2048_512/ADSL_2_2+/AB",
                    "*SNR10/256_256/ADSL_2_2+/AB",
                    "*SNR10/2048_512/ADSL_2_2+/AB",
                    "*SNR10/5120_768/ADSL_2_2+/AB",
                    "*SNR10/10048_864/ADSL_2_2+/AB",
                    "*SNR10/10048_1056/ADSL_2_2+/AB",
                    "*SNR10/15000_1300/ADSL_2_2+/AB",
                    "*SNR10/25000_3000/ADSL_2_2+/AB",
                    "*SNR12/10048_864/ADSL_2_2+/AB",
                    "*SNR12/15000_1000/ADSL_2_2+/AB",
                    "*SNR15/25000_3000/ADSL_2_2+/AB"));

    private static String ip;

    changeShpd(String ip, String s1) {
        changeShpd.ip = ip;
    }

    static String test() throws IOException {
        ArrayList <String> p = new ArrayList<>();
        String SMI = ".1.3.6.1.4.1.1286.1.3.";
        String pre[] = new String[0];

        for( int z = 0; ip.indexOf(' ', z + 1) != -1;  z = ip.indexOf(' ' , z + 1 ) + 1 )
            p.add(ip.substring(z , ip.indexOf(' ' , z + 1 )).trim());

        for(String s: p)
            System.out.println("->!" + s);
        String ifindex = String.valueOf(binary.Nport( p.get(1), p.get(2).trim(),"00"));

        SnmpCommand client = new SnmpCommand("udp:" + p.get(0) + "/161") {
            @Override
            public int hashCode() {
                return super.hashCode();
            }
        };

        client.start();

        System.out.println(client.getAsString(new OID(SMI + "3.1.1.2.131072"), ""));

        switch (client.getAsString(new OID(".1.3.6.1.2.1.1.1.0"), "")){
            case "ECI telecom HiFOCuS broadband access system":
                switch (client.getAsString(new OID(SMI + "3.1.1.2.131072"), "")) {
                    case "IPNI_APP_2.52.35":
                        pre = new String[]{
                                "18.1.5.1.1.1.2.", // установка профайла
                                "18.2.3.1.1.3.",     // ресет
                                "18.2.3.1.1.4."
                        };
                        break;
                    case "IPNI_HB_APP_2.00.73":
                    case "se_10.01.64":
                        pre = new String[]{
                                "9.1.5.1.1.1.2.",   //установка профайла
                                "9.2.3.1.1.3.",    // ресет
                                "9.2.3.1.1.4."
                        };
                        break;
                    case "IPNI_HB_APP_5.61.59":
                        break;
                    default:
                        System.out.println(client.getAsString(new OID(SMI + "3.1.1.2.131072"), ""));
                }
                break;
        }

        if( p.size() == 4) {
            client.getAsString(new OID(SMI + pre[0] + ifindex), profil.get(Integer.parseInt(p.get(3))));
            client.getAsString(new OID(SMI + pre[1] + ifindex+ ".1"), 5);
            System.out.println("-->" + client.getAsString(new OID(SMI + pre[2] + ifindex), ""));
        }

        return "&$|" +  MyStrOps.myRelSS(MyStrOps::getprof, client.getAsString(new OID(SMI + pre[0] + ifindex), ""));
    }
}
