import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;

class testMT20 {
    String phone, Host;
    testMT20(String phone, String Host) {
        this.phone = phone; this.Host = Host;
    }

    String test() throws IOException, NotBoundException {
        return Forming(InitTest(Host, phone));
    }

    private String Forming(ArrayList<String> l){
        String[] name_line ={
                "~V между А и землей:\t ",         // L1
                "~V между В и землей:\t ",         // L2
                "-V между А и землей:\t",         // L3
                "-V между В и землей:\t ",         // L4
                "R.из между А и землей:\t",       // L5
                "R.из между В и землей:\t",       // L6
                "R.из между А и В:\t",            // L7
                "Емкость линии:\t"                // L8
        };
        int i = 0;
        StringBuilder str = new StringBuilder("&#");
        for(String s : l)
            str.append(name_line[i++]).append(s).append("|");
        return str.toString();
    }

    private ArrayList<String> InitTest(String Host, String phone) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(Host, 2099);
        MyRemote service = (MyRemote) registry.lookup("ServicePhone");
        return (ArrayList<String>) service.sayHello(phone);
    }

}
