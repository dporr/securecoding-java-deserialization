import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class InsecureDeserialization {

    public static void main(String args[]) throws Exception{
        //Serialize an user
        User user = new User("Hacker", 1337);
        FileOutputStream fos = new FileOutputStream("/tmp/user.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(user);
        oos.flush();
        oos.close();

        //Serialize exploit
        Exploit exploit = new Exploit("");
        FileOutputStream fos2 = new FileOutputStream("/tmp/exploit.ser");
        ObjectOutputStream oos2 = new ObjectOutputStream(fos2);
        oos2.writeObject(exploit);

        //Deserialize the user
        FileInputStream fileIn = new FileInputStream("/tmp/exploit.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        User deserializedUser = (User) in.readObject();

        //Confirm we got the rigth object
        System.out.println("Serialized: " + user);
        System.out.println("Deserialized: " + deserializedUser);
    }
}
