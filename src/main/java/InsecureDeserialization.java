import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        InputStream fileIn = new FileInputStream("/tmp/exploit.ser");
        ObjectInputStream in = new ObjectInputStream(fileIn);
        //Create a JEP290 Filter
        ObjectInputFilter myFilter =
                ObjectInputFilter.Config.createFilter("!default.User;!*;");
        //Configure the filter before deserialization
        in.setObjectInputFilter(myFilter);
        User deserializedUser = (User) in.readObject();

        //Confirm we got the rigth object
        System.out.println("Serialized: " + user);
        System.out.println("Deserialized: " + deserializedUser);
    }

}


