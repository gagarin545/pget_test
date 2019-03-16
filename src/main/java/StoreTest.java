import java.io.IOException;
import java.rmi.NotBoundException;
import java.sql.SQLException;
import java.util.ArrayList;

class StoreTest extends Store {

     String storeTest(String message) throws IOException, InterruptedException, NotBoundException {
         String s = "";
         switch (message.substring(1, 4)) {
             case "NNN":

                 System.out.println("послано:" + message );
                 try {
                     ArrayList<String> str = get_incident.get_td(message.substring(message.indexOf('-') + 1));
                     switch (str.get(2)) {
                         case "192.168.12.1":
                             test<testRTS, String> mytestrts = testRTS::new;
                             testRTS testrts = Store.MyClasFactory(mytestrts, str.get(0), str.get(2));
                             s = testrts.test();
                             break;
                         case "10.183.5.66":
                         case "10.183.5.67":
                             test<testMT20, String> mytestmt20 = testMT20::new;
                             testMT20 testmt20 = Store.MyClasFactory(mytestmt20, str.get(0), str.get(2));
                             s = testmt20.test();
                             break;
                         case "10.11.104.20":
                         case "10.11.104.21":
                             test<testPhone, String> mytestp = testPhone::new;
                             testPhone testp1 = Store.MyClasFactory(mytestp, str.get(0), str.get(2));
                             s = testp1.test();
                             break;
                         default:
                             test<testShpd, String> mytest = testShpd::new;
                             testShpd test = Store.MyClasFactory(mytest, "", message);
                             s = test.test(str);
                     }
                 } catch (SQLException e) {
                     e.printStackTrace();
                     s = toString() + "|! : Нет тех.данных|" + e + "|";
                 }

                 System.out.println("ШПД" + s);
                 break;
             case "777":
             case "770":
                 if(message.length() != 12)
                     s = ("&# Неверный логин : Введено " + message.length() + " символов, а должно быть 11");
                 else {
                     test<testShpd, String> mytest = testShpd::new;
                     testShpd test = Store.MyClasFactory(mytest,  message, "");
                     s = test.test(new ArrayList<>());
                 }
                 break;
             case "ШПД":
                 if(message.contains(";")) message = message.substring(0, message.indexOf(';')) + ' ';
                 System.out.println( message);
                 test<changeShpd, String> mytestchange = changeShpd::new;
                 changeShpd testchange = Store.MyClasFactory(mytestchange,  message.substring(4) , "");
                 s = testchange.test();
                 break;
             default:
                 s =( message.substring(1) + " Эта услуга не тестируется : !");
                 System.out.print("->ничего");
         }
         return s;
    }
}
