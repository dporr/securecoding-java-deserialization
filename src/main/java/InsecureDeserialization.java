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

        //Deserialize the user
        FileInputStream fileIn = new FileInputStream("/tmp/user.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        User deserializedUser = (User) in.readObject();
        //Confirm we got the rigth object
        System.out.println("Serialized: " + user);
        System.out.println("Deserialized: " + deserializedUser);
    }
}
