package fi.iki.elonen;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.nio.charset.Charset;

public class ServerRunner {
    public static void run(Class serverClass) {
        try {
            executeInstance((NanoHTTPD) serverClass.newInstance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static String getDefaultCharSet() {
    	OutputStreamWriter writer = new OutputStreamWriter(new ByteArrayOutputStream());
    	String enc = writer.getEncoding();
    	return enc;
    }

    public static void executeInstance(NanoHTTPD server) {
        try{
            System.setProperty("file.encoding","UTF-8");
            Field charset = Charset.class.getDeclaredField("defaultCharset");
            charset.setAccessible(true);
            charset.set(null,null);
        }catch(NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e){
            System.out.println(e.getMessage());
        }
        System.out.println("Default Charset=" + Charset.defaultCharset());
        System.out.println("file.encoding=" + System.getProperty("file.encoding"));
    	System.out.println("Default Charset=" + Charset.defaultCharset());
    	System.out.println("Default Charset in Use=" + getDefaultCharSet());
        try {
            server.start();
        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
            System.exit(-1);
        }

        System.out.println("Server started, Hit Enter to stop.\n");

        try {
            System.in.read();
        } catch (Throwable ignored) {
        }

        server.stop();
        System.out.println("Server stopped.\n");
    }
}