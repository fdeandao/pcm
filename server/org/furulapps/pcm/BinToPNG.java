/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.furulapps.pcm;

/**
 *
 * @author koferdo
 */
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Calendar;
import java.util.zip.Inflater;
import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import net.nikr.dds.*;

/**
 *
 * @author koferdo
 */
public class BinToPNG {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException, IOException, Exception {
        //deflat(args[0]);
        byte[] data = read(new File(args[0]));
        byte[] output = descom(data);
        saveFile(splitBytes(output), args[0] + ".unzlib.dds");
        saveImage(args[0] + ".unzlib.dds");
        /*ByteArrayOutputStream out = saveImage(descom(read(new File(args[0]))));
        byte [] buf = out.toByteArray();
        saveFile(buf, args[0] + ".png");*/
        /*byte[] data = read(new File(args[0]));
        for(int i=0; i<10; i++){
            System.out.println(i + "|" + data[i]);
        }*/
    }

    public static byte[] descom(byte[] input) {
        byte[] result = new byte[descomSize(input)];
        try {
            Inflater decompresser = new Inflater(false);
            decompresser.setInput(input, 16, input.length - 16);
            int resultLength = decompresser.inflate(result);
            decompresser.end();
        } catch (java.util.zip.DataFormatException ex) {
            ex.printStackTrace();
        }
        return result;
    }
    
    public static int descomSize(byte[] input) {
        int size = 0;
        byte[] result = new byte[input.length * 2];
        try {
            Inflater decompresser = new Inflater(false);
            decompresser.setInput(input, 16, input.length - 16);
            size = decompresser.inflate(result);
            decompresser.end();
        } catch (java.util.zip.DataFormatException ex) {
            ex.printStackTrace();
        }
        return size;
    }

    public static byte[] read(File file) throws IOException {

        /*if (file.length() > MAX_FILE_SIZE) {
         throw new FileTooBigException(file);
         }*/
        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        try {
            byte[] buffer = new byte[4096];
            ous = new ByteArrayOutputStream();
            ios = new FileInputStream(file);
            int read = 0;
            while ((read = ios.read(buffer)) != -1) {
                ous.write(buffer, 0, read);
            }
        } finally {
            try {
                if (ous != null) {
                    ous.close();
                }
            } catch (IOException e) {
            }

            try {
                if (ios != null) {
                    ios.close();
                }
            } catch (IOException e) {
            }
        }
        return ous.toByteArray();
    }

    public static void saveFile(byte[] bytes, String path) throws FileNotFoundException, IOException {
        FileOutputStream stream = new FileOutputStream(path);
        try {
            stream.write(bytes);
        } finally {
            stream.close();
        }
    }
    public static byte [] splitBytes(byte[] bytes) {
        byte [] data = null;
        byte [] check = new byte[]{68,68,83,32};
        int cut=0;
        for(int i=0; i<bytes.length; i+=4){
            if(i+3 <= bytes.length){
                if(bytes[i] == check[0] &&
                        bytes[i + 1] == check[1] &&
                        bytes[i + 2] == check[2] &&
                        bytes[i + 3] == check[3]){
                cut = i;
                break;
                }
            }
        }
        if(cut > 0){
            data = Arrays.copyOfRange(bytes, cut, bytes.length);
        }
        return data;
    }
    
    public static ByteArrayOutputStream saveImage(String file) throws FileNotFoundException, IOException{
        File f = new File(file);
        OutputStream out=new ByteArrayOutputStream();
        FileImageInputStream fileImageInputStream = new FileImageInputStream(f);
        DDSImageReader instance = new DDSImageReader( new DDSImageReaderSpi() );        
        try{
            instance.setInput(fileImageInputStream);
            BufferedImage image =  instance.read(0);
            ImageIO.write(image, "PNG", out);            
        }finally{
            fileImageInputStream.close();
            instance.dispose();
            try{
                f.delete();
            }catch(Exception e){
                System.err.println(e.getMessage());
            }
        }
        return (ByteArrayOutputStream)out;
    }
    
    public static ByteArrayOutputStream saveImageOut(byte [] data) throws FileNotFoundException, IOException{
        OutputStream out=new ByteArrayOutputStream();
        InputStream inputStream = new ByteArrayInputStream(data);
        DDSImageReader instance = new DDSImageReader( new DDSImageReaderSpi() );
        instance.setInput(inputStream);
        BufferedImage image =  instance.read(0);
        ImageIO.write(image, "PNG", out);
        return (ByteArrayOutputStream)out;
    }
    
    public static ByteArrayOutputStream getImagePNG(String path) throws FileNotFoundException, IOException{
        String fileName = "../generate/files/__tmp_" + Calendar.getInstance().getTimeInMillis() + ".unzlib.dds";
        byte[] data = read(new File(path));
        byte[] output = descom(data);
        saveFile(splitBytes(output), fileName);
        return saveImage(fileName);
    }
}
