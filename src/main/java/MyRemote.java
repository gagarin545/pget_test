import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MyRemote extends Remote {
    Object sayHello(String name) throws RemoteException;
}

