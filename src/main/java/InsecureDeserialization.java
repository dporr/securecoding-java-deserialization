import java.io.FileOutputStream;
import java.io.ObjectOutputStream;

public class InsecureDeserialization {

    public static void main(String args[]) throws Exception{
        User user = new User("Hacker", 1337);
        FileOutputStream fos = new FileOutputStream("/tmp/user.ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(user);
    }
}
