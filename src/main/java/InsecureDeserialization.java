import java.io.*;
import java.util.Arrays;
import java.util.List;

public class InsecureDeserialization {
    /**
     * A method to replace the unsafe ObjectInputStream.readObject() method built into Java. This method
     * checks to be sure the classes referenced are safe, the number of objects is limited to something sane,
     * and the number of bytes is limited to a reasonable number. The returned Object is also cast to the
     * specified type.
     *
     * @param type Class representing the object type expected to be returned
     * @param safeClasses List of Classes allowed in serialized object being read
     * @param maxObjects long representing the maximum number of objects allowed inside the serialized object being read
     * @param maxBytes long representing the maximum number of bytes allowed to be read from the InputStream
     * @param in InputStream containing an untrusted serialized object
     * @return Object read from the stream (cast to the Class of the type parameter)
     * @throws IOException
     * @throws ClassNotFoundException
     */
    @SuppressWarnings("unchecked")
    public static  <T extends Object> T safeReadObject(Class<?> type, List<Class<?>> safeClasses, long maxObjects, long maxBytes, InputStream in ) throws IOException, ClassNotFoundException {
        // create n input stream limited to a certain number of bytes
        InputStream lis = new FilterInputStream( in ) {
            private long len = 0;
            public int read() throws IOException {
                int val = super.read();
                if (val != -1) {
                    len++;
                    checkLength();
                }
                return val;
            }
            public int read(byte[] b, int off, int len) throws IOException {
                int val = super.read(b, off, len);
                if (val > 0) {
                    len += val;
                    checkLength();
                }
                return val;
            }
            private void checkLength() throws IOException {
                if (len > maxBytes) {
                    throw new SecurityException("Security violation: attempt to deserialize too many bytes from stream. Limit is " + maxBytes);
                }
            }
        };
        // create an object input stream that checks classes and limits the number of objects to read
        ObjectInputStream ois = new ObjectInputStream( lis ) {
            private int objCount = 0;
            boolean b = enableResolveObject(true);
            protected Object resolveObject(Object obj) throws IOException {
                if ( objCount++ > maxObjects ) throw new SecurityException( "Security violation: attempt to deserialize too many objects from stream. Limit is " + maxObjects );
                Object object = super.resolveObject(obj);
                return object;
            }
            protected Class<?> resolveClass(ObjectStreamClass osc) throws IOException, ClassNotFoundException {
                Class<?> clazz = super.resolveClass(osc);
                if (
                        clazz.isArray() ||
                                clazz.equals(type) ||
                                clazz.equals(String.class) ||
                                Number.class.isAssignableFrom(clazz) ||
                                safeClasses.contains(clazz)
                ) return clazz;
                throw new SecurityException("Security violation: attempt to deserialize unauthorized " + clazz);
            }
        };
        // use the protected ObjectInputStream to read object safely and cast to T
        return (T) ois.readObject();
    }

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
        //InputStream in = new ObjectInputStream(fileIn);
        //User deserializedUser = (User) in.readObject();
        // read in the serialized object SAFELY
        List<Class<?>> safeClasses = Arrays.asList( User.class );
        User deserializedUser = (User) safeReadObject( User.class, safeClasses, 10, 50, fileIn );
        //Confirm we got the rigth object
        System.out.println("Serialized: " + user);
        System.out.println("Deserialized: " + deserializedUser);
    }
}
