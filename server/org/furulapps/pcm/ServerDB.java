package org.furulapps.pcm;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteJob;
import com.almworks.sqlite4java.SQLiteQueue;
import com.almworks.sqlite4java.SQLiteStatement;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author koferdo
 */
public class ServerDB {

    //static private SQLiteConnection db;
    static private String dbfile;
    static private Properties prop;
    static final private String SELPLAYERCVS = "select id, HEX(name) name, HEX(shirtname) shirtname, hex(nationality) nationality, HEX(linkedface) linkedface, HEX(faceslot) faceslot, HEX(linkedhair) linkedhair, HEX(hairslot) hairslot, HEX(clubteam) clubteam, HEX(nationalteam) nationalteam, HEX(isconfig) isconfig from playercsv where ";
    static final private String SELPLAYERMAP = "select id, HEX(facefile) facefile, HEX(hairfile) hairfile, HEX(realname) realname, HEX(existsface) existsface, HEX(existshair) existshair from playermap where ";
    static final private String SELPLAYERFILE = "select HEX(idfile) idfile, HEX(filename) filename, HEX(shortfilename) shortfilename, HEX(isused) isused from playerfile where ";
    static final private String SELFILE2PLAYER = "select hex(idfile) idfile, hex(filename) filename, hex(shortfilename) shortfilename, hex(isused) isused, hex(1) type from playerfile\n"
            + "where exists(select 1 from playermap where id=? and existsface=1\n"
            + "and (lower(playerfile.shortfilename)=lower(facefile)))\n"
            + "union\n"
            + "select hex(idfile) idfile, hex(filename) filename, hex(shortfilename) shortfilename, hex(isused) isused, hex(2) type from playerfile\n"
            + "where exists(select 1 from playermap where id=? and existsface=0\n"
            + "and (lower(playerfile.shortfilename)=lower(facefile)))\n"
            + "union\n"
            + "select hex(idfile) idfile, hex(filename) filename, hex(shortfilename) shortfilename, hex(isused) isused, hex(3) type from playerfile\n"
            + "where exists(select 1 from playermap where id=? and existshair=1\n"
            + "and (lower(playerfile.shortfilename)=lower(hairfile)))\n"
            + "union\n"
            + "select hex(idfile) idfile, hex(filename) filename, hex(shortfilename) shortfilename, hex(isused) isused, hex(4) type from playerfile\n"
            + "where exists(select 1 from playermap where id=? and existshair=0\n"
            + "and (lower(playerfile.shortfilename)=lower(hairfile)))";
    static final private String SELFILE2PLAYERDEEP = "select hex(idfile) idfile, hex(filename) filename, hex(shortfilename) shortfilename, hex(isused) isused, hex(5) type from playerfile\n"
            + "where exists(select 1 from playercsv where id=? and\n"
            + "lower(shortfilename) like '%'||lower(replace(name,' ','%'))||'%')\n"
            + "union\n"
            + "select hex(idfile) idfile, hex(filename) filename, hex(shortfilename) shortfilename, hex(isused) isused, hex(6) type from playerfile\n"
            + "where exists(select 1 from playercsv where id=? and\n"
            + "lower(shortfilename) like '%'||lower(replace(shirtname,' ','%'))||'%')\n"
            + "union\n"
            + "select hex(idfile) idfile, hex(filename) filename, hex(shortfilename) shortfilename, hex(isused) isused, hex(7) type from playerfile\n"
            + "where exists(select 1 from playercsv where id=? and\n"
            + "lower(shortfilename) like '%'||lower(replace(nationality,' ','%'))||'%')\n"
            + "union\n"
            + "select hex(idfile) idfile, hex(filename) filename, hex(shortfilename) shortfilename, hex(isused) isused, hex(8) type from playerfile\n"
            + "where exists(select 1 from playercsv where id=? and trim(clubteam) <> '' and\n"
            + "lower(shortfilename) like '%'||lower(replace(clubteam,' ','%'))||'%')\n"
            + "union\n"
            + "select hex(idfile) idfile, hex(filename) filename, hex(shortfilename) shortfilename, hex(isused) isused, hex(9) type from playerfile\n"
            + "where exists(select 1 from playercsv where id=? and trim(nationalteam) <> '' and\n"
            + "lower(shortfilename) like '%'||lower(replace(nationalteam,' ','%'))||'%')";

    static {
        try {
            prop = new Properties();
            prop.load(new FileInputStream("../conf.properties"));
            /*queue = new SQLiteQueue(new File(prop.getProperty("dbpcmjava")));
             queue.start();*/
            dbfile = prop.getProperty("dbpcmjava");
            startDB();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerDB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerDB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServerDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /*private static void open() throws SQLiteException {
     db = new SQLiteConnection(new File(prop.getProperty("dbpcmjava")));
     db.open(true);
     //db.exec("pragma encoding=\"UTF-8\"");
     }

     private static void close() {
     db.dispose();
     }*/
    private static void stopDB(SQLiteQueue queue) throws InterruptedException {
        queue.flush();
        queue.stop(true).join();
    }

    private static SQLiteQueue startDB() throws InterruptedException {
        SQLiteQueue queue = new SQLiteQueue(new File(dbfile));
        queue.start();
        return queue;
    }

    private static String hexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character

            sb.append((char) decimal);

            temp.append(decimal);
        }
        //System.err.println(sb.toString());
        return sb.toString();
    }

    private static long getCount(String query, SQLiteConnection db) throws SQLiteException {
        long count = 0;
        SQLiteStatement st = db.prepare("select count(0) from (" + query + ")");
        try {
            if (st.step()) {
                count = st.columnLong(0);
            }
        } finally {
            st.dispose();
        }
        return count;
    }

    private static String getPlayersCSV(final String where, final int start, final int count) throws SQLiteException, UnsupportedEncodingException, InterruptedException {
        SQLiteQueue queue = startDB();
        String ret = queue.execute(new SQLiteJob<String>() {
            @Override
            protected String job(SQLiteConnection db) throws SQLiteException, InterruptedException, UnsupportedEncodingException {
                StringBuilder buff = new StringBuilder();
                buff.append("{\"playerscsv\": [");
                SQLiteStatement st = db.prepare(SELPLAYERCVS + where + " LIMIT " + count + " OFFSET " + start);
                try {
                    while (st.step()) {
                        boolean isConfig = false;
                        buff.append("{\"id\": \"")
                                .append(st.columnString(0))
                                .append("\",");
                        for (int i = 1; i < 11; i++) {
                            buff.append("\"").append(st.getColumnName(i)).append("\":\"").append(hexToString(st.columnString(i))).append(i != 10 ? "\"," : "\"");
                            if (st.getColumnName(i).equals("isconfig") && hexToString(st.columnString(i)).equals("1")) {
                                isConfig = true;

                            }
                        }
                        if (isConfig) {
                            buff.append(",\"config\": ")
                                    .append(getPlayersMap("ID=" + st.columnString(0), 0, 99));
                        }
                        buff.append("},");
                    }
                } finally {
                    st.dispose();
                }
                int comma = buff.lastIndexOf(",");
                if (comma > 0) {
                    buff = buff.deleteCharAt(comma);
                }
                buff.append("], \"count\": \"");
                buff.append(getCount(SELPLAYERCVS + where, db));
                buff.append("\"}");
                return buff.toString();
            }
        }).complete();
        stopDB(queue);
        return ret;
    }

    private static String convertCountryClub(String country, String club, String query) {
        String ret = ("all".equals(country) ? "1=1" : "NATIONALITY='" + country + "'") + " and " + ("all".equals(club) ? "1=1" : "(CLUBTEAM like '" + club + "' or (NATIONALTEAM) like '" + club + "')");
        if (query.equals("") || ("all".equals(country) && ("all".equals(club)))) {
            return ret;
        } else {
            return query + ret + ")";
        }
    }

    private static String convertDataLike(String[] fields, String data) {
        if (data.equals("all")) {
            return " 1=1 ";
        } else {
            String ret = "(";
            for (int i = 0; i < fields.length; i++) {
                ret += " lower(" + fields[i].toLowerCase() + ") like '%" + data.toLowerCase().replaceAll(" ", "%") + "%' " + (i == fields.length - 1 ? ")" : " or ");
            }
            return ret;
        }
    }

    public static String getPlayers(String country, String club, String name, int start, int count) throws SQLiteException, UnsupportedEncodingException, InterruptedException {
        /* from CSV */

        return getPlayersCSV(
                convertDataLike(new String[]{"NAME", "SHIRTNAME"}, name) + " and " + convertCountryClub(country, club, ""), start, count);
    }

    public static String getPlayersNoConfig(String country, String club, String name, int start, int count) throws SQLiteException, UnsupportedEncodingException, InterruptedException {
        /* from CSV */

        return getPlayersCSV(
                convertDataLike(new String[]{"NAME", "SHIRTNAME"}, name) + " and ISCONFIG = 0 and " + convertCountryClub(country, club, ""), start, count);
    }

    private static String getPlayersMapIn(final String where, final int start, final int count) throws SQLiteException, UnsupportedEncodingException, InterruptedException {
        SQLiteQueue queue = startDB();
        String ret = queue.execute(new SQLiteJob<String>() {
            @Override
            protected String job(SQLiteConnection db) throws SQLiteException, InterruptedException {
                StringBuilder buff = new StringBuilder();
                buff.append("{\"playersmap\": [");
                SQLiteStatement st = db.prepare(SELPLAYERMAP + where + " LIMIT " + count + " OFFSET " + start);
                try {
                    while (st.step()) {
                        buff.append("{\"id\": \"")
                                .append(st.columnString(0))
                                .append("\",");
                        for (int i = 1; i < 6; i++) {
                            buff.append("\"").append(st.getColumnName(i)).append("\":\"").append(hexToString(st.columnString(i))).append(i != 5 ? "\"," : "\"");
                        }
                        buff.append("},");
                    }
                } finally {
                    st.dispose();
                }
                int comma = buff.lastIndexOf(",");
                if (comma > 0) {
                    buff = buff.deleteCharAt(comma);
                }
                buff.append("], \"count\": \"");
                buff.append(getCount(SELPLAYERMAP + where, db));
                buff.append("\"}");
                return buff.toString().replace("\\", "\\\\");
            }
        }).complete();
        stopDB(queue);
        return ret;
    }

    public static String getPlayersMap(String path, int start, int count) throws SQLiteException, UnsupportedEncodingException, InterruptedException {
        /* from Map */
        return getPlayersMapIn(convertDataLike(new String[]{"REALNAME", "FACEFILE", "HAIRFILE"}, path), start, count);
    }

    public static String getPlayersMapConflic(String path, int start, int count) throws SQLiteException, UnsupportedEncodingException, InterruptedException {
        /* from Map */
        return getPlayersMapIn(
                convertDataLike(new String[]{"REALNAME", "FACEFILE", "HAIRFILE"}, path) + " and " + "(EXISTSFACE=0 or EXISTSHAIR=0 or not exists(select 1 from PLAYERCSV where PLAYERCSV.id=PLAYERMAP.id)) ", start, count);
    }

    public static String getClubs() throws InterruptedException {
        SQLiteQueue queue = startDB();
        String ret = queue.execute(new SQLiteJob<String>() {
            @Override
            protected String job(SQLiteConnection db) throws SQLiteException, InterruptedException {
                StringBuilder buff = new StringBuilder();
                buff.append("{\"club\": [");
                SQLiteStatement st = db.prepare("select hex(clubteam) from team");
                try {
                    while (st.step()) {
                        buff.append("{\"name\": \"")
                                .append(hexToString(st.columnString(0)))
                                .append("\"},");
                    }
                } finally {
                    st.dispose();
                }
                int comma = buff.lastIndexOf(",");
                if (comma > 0) {
                    buff = buff.deleteCharAt(comma);
                }
                buff.append("]}");
                return buff.toString();
            }
        }).complete();
        stopDB(queue);
        return ret;
    }

    public static String getCountries() throws SQLiteException, InterruptedException {
        SQLiteQueue queue = startDB();
        String ret = queue.execute(new SQLiteJob<String>() {
            @Override
            protected String job(SQLiteConnection db) throws SQLiteException, InterruptedException {
                StringBuilder buff = new StringBuilder();
                buff.append("{\"country\": [");
                SQLiteStatement st = db.prepare("select hex(nationality) from country order by nationality");
                try {
                    while (st.step()) {
                        buff.append("{\"name\": \"")
                                .append(hexToString(st.columnString(0)))
                                .append("\"},");
                    }
                } finally {
                    st.dispose();
                }
                int comma = buff.lastIndexOf(",");
                if (comma > 0) {
                    buff = buff.deleteCharAt(comma);
                }
                buff.append("]}");
                return buff.toString();
            }
        }).complete();
        stopDB(queue);
        return ret;
    }

    private static String getFiles(final String where, final int start, final int count) throws SQLiteException, InterruptedException {
        SQLiteQueue queue = startDB();
        String ret = queue.execute(new SQLiteJob<String>() {
            @Override
            protected String job(SQLiteConnection db) throws SQLiteException, InterruptedException {
                StringBuilder buff = new StringBuilder();
                buff.append("{\"files\": [");
                SQLiteStatement st = db.prepare(SELPLAYERFILE + where + " LIMIT " + count + " OFFSET " + start);
                try {
                    while (st.step()) {
                        buff.append("{");
                        for (int i = 0; i < 4; i++) {
                            buff.append("\"").append(st.getColumnName(i)).append("\":\"").append(hexToString(st.columnString(i))).append(i != 3 ? "\"," : "\"");
                        }
                        buff.append("},");
                    }
                } finally {
                    st.dispose();
                }
                int comma = buff.lastIndexOf(",");
                if (comma > 0) {
                    buff = buff.deleteCharAt(comma);
                }
                buff.append("], \"count\": \"");
                buff.append(getCount(SELPLAYERFILE + where, db));
                buff.append("\"}");

                return buff.toString().replace("\\", "\\\\");
            }
        }).complete();
        stopDB(queue);
        return ret;
    }

    public static String getFiles(int start, int count) throws SQLiteException, InterruptedException {
        return getFiles(" 1=1 ", start, count);
    }

    public static String getFilesConflic(int start, int count) throws SQLiteException, InterruptedException {
        return getFiles(" ISUSED=0 ", start, count);
    }

    public static String getFilesToPlayer(final int player, final boolean deep, final int start, final int count) throws SQLiteException, InterruptedException {
        SQLiteQueue queue = startDB();
        String ret = queue.execute(new SQLiteJob<String>() {
            @Override
            protected String job(SQLiteConnection db) throws SQLiteException, InterruptedException {
                StringBuilder buff = new StringBuilder();
                String orderLimit = " order by TYPE LIMIT " + count + " OFFSET " + start;
                String query = (deep ? SELFILE2PLAYER + " union " + SELFILE2PLAYERDEEP : SELFILE2PLAYER);
                buff.append("{\"filestoplayer\": [");
                query = query.replaceAll("\\?", player + "");
                SQLiteStatement st = db.prepare(query + orderLimit);
                try {
                    while (st.step()) {
                        buff.append("{");
                        for (int i = 0; i < 5; i++) {
                            buff.append("\"").append(st.getColumnName(i)).append("\":\"").append(hexToString(st.columnString(i))).append(i != 4 ? "\"," : "\"");
                        }
                        buff.append("},");
                    }
                } finally {
                    st.dispose();
                }

                int comma = buff.lastIndexOf(",");
                if (comma > 0) {
                    buff = buff.deleteCharAt(comma);
                }
                buff.append("], \"count\": \"");
                buff.append(getCount(query, db));
                buff.append("\"}");
                return buff.toString().replace("\\", "\\\\");
            }
        }).complete();
        stopDB(queue);
        return ret;

    }

    private static String getSplitName(String query, String name) {
        String ret = "";
        String[] names = name.split(" ");
        for (int i = 0; i < names.length; i++) {
            ret += query.concat((i == names.length - 1) ? "" : " or ").replaceAll("\\?", names[i]);
        }
        return ret;
    }

    private static String getSelPlayer2File(String fileName, String[] search) {
        return "select id, hex(name) name, hex(shirtname) shirtname, hex(linkedface) linkedface, hex(faceslot) faceslot, hex(linkedhair) linkedhair, hex(hairslot) hairslot, hex(clubteam) clubteam, hex(nationalteam) nationalteam, hex(isconfig) isconfig, hex(1) type\n"
                + "from playercsv where " + getSplitName(" lower(NAME) like '%?%' or lower(SHIRTNAME) like '%?%' ", fileName) + " \n"
                + "union\n"
                + "SELECT id, hex(name) name, hex(shirtname) shirtname, hex(linkedface) linkedface, hex(faceslot) faceslot, hex(linkedhair) linkedhair, hex(hairslot) hairslot, hex(clubteam) clubteam, hex(nationalteam) nationalteam, hex(isconfig) isconfig, hex(2) type\n"
                + "FROM PLAYERCSV WHERE " + (search.length > 2 ? " 1=1 " : " 1=0 ") + " and (lower(CLUBTEAM) like '%" + (search.length > 2 ? search[search.length - 2] : "") + "%' or lower(NATIONALTEAM) like '%" + (search.length > 2 ? search[search.length - 2] : "") + "%' or lower(NATIONALITY) like '%" + (search.length > 2 ? search[search.length - 2] : "") + "%') order by TYPE";
    }

    public static String getPlayersToFile(final int idFile, final boolean deep, final int start, final int count) throws SQLiteException, InterruptedException {
        SQLiteQueue queue = startDB();
        String ret = queue.execute(new SQLiteJob<String>() {
            @Override
            protected String job(SQLiteConnection db) throws SQLiteException, InterruptedException {
                SQLiteStatement st = db.prepare("select hex(lower(shortfilename)) from playerfile where idfile=" + idFile);
                String fileName = "";
                StringBuilder buff = new StringBuilder();
                try {
                    if (st.step()) {
                        String[] search = hexToString(st.columnString(0))
                                .replaceAll(".bin", "")
                                .replaceAll("_hair", "")
                                .replaceAll("_face", "")
                                .replaceAll("_", "%")
                                .split("\\\\");
                        if (search.length > 0) {
                            fileName = search[search.length - 1];
                            fileName = fileName.indexOf(".") > 0 ? fileName.substring(fileName.indexOf(".") + 1, fileName.length()) : fileName;
                            buff.append("{\"playerscsv\": [");
                            String query = getSelPlayer2File(fileName, deep ? search : new String[]{});
                            st = db.prepare(query + " LIMIT " + count + " OFFSET " + start);
                            try {
                                while (st.step()) {
                                    buff.append("{\"id\": \"")
                                            .append(st.columnString(0))
                                            .append("\",");
                                    for (int i = 1; i < 11; i++) {
                                        buff.append("\"").append(st.getColumnName(i)).append("\":\"").append(hexToString(st.columnString(i))).append(i != 10 ? "\"," : "\"");
                                    }
                                    buff.append("},");
                                }
                            } finally {
                                st.dispose();
                            }
                            int comma = buff.lastIndexOf(",");
                            if (comma > 0) {
                                buff = buff.deleteCharAt(comma);
                            }
                            buff.append("], \"count\": \"");
                            buff.append(getCount(query, db));
                            buff.append("\"}");
                        } else {
                            buff.append("{\"error\": \"nothing found\"}");
                        }
                    } else {
                        buff.append("{\"error\": \"nothing found\"}");
                    }
                } finally {
                    st.dispose();
                }
                return buff.toString().replace("\\", "\\\\");
            }
        }).complete();
        stopDB(queue);
        return ret;
    }

    public static ByteArrayOutputStream getFileById(final String fileid) throws FileNotFoundException, IOException, SQLiteException, InterruptedException {
        return getFile(" idfile = " + fileid);
    }

    public static ByteArrayOutputStream getFileByName(final String filename) throws FileNotFoundException, IOException, SQLiteException, InterruptedException {
        return getFile(" lower(shortfilename) = '" + filename.toLowerCase() + "'");
    }

    public static ByteArrayOutputStream getFile(final String where) throws FileNotFoundException, IOException, SQLiteException, InterruptedException {
        SQLiteQueue queue = startDB();
        String ret = queue.execute(new SQLiteJob<String>() {
            @Override
            protected String job(SQLiteConnection db) throws SQLiteException, InterruptedException, FileNotFoundException, IOException {
                String fileName = "";
                SQLiteStatement st = db.prepare(SELPLAYERFILE + where);
                try {
                    if (st.step()) {
                        fileName = hexToString(st.columnString(1));
                    }
                } finally {
                    st.dispose();
                }

                return fileName;
            }
        }).complete();
        stopDB(queue);
        if (ret.equals("")) {
            System.err.println("1");
            return new ByteArrayOutputStream();
        } else {
            return BinToPNG.getImagePNG(ret);
        }
    }
    
    public static String getDirectoryTree() throws SQLiteException, InterruptedException {
        SQLiteQueue queue = startDB();
        String ret = queue.execute(new SQLiteJob<String>() {
            @Override
            protected String job(SQLiteConnection db) throws SQLiteException, InterruptedException {
                SQLiteStatement st = db.prepare("select hex(lower(FILEDIR)) FILEDIR from CONFIG");
                String dir = "";
                try {
                    if (st.step()) {
                        dir = hexToString(st.columnString(0));   
                    }
                } finally {
                    st.dispose();
                }
                return dir.replace("\\", "\\\\");
            }
        }).complete();
        stopDB(queue);
        if(!ret.equals("")){
            File file = new File(ret);
            if(file.exists()){
                FilenameFilter filter = new OnlyDirectoryFFImp();
                return walkin(file, filter);
            }else{
                return "{\"error\": \"File not exists " + ret + "\"}";
            }
        }else{
            return "{\"error\": \"Config not found\"}";
        }
    }

    private static String walkin(File dir, FilenameFilter filter) {
        StringBuilder nodes = new StringBuilder().append("[");
        File listFile[] = dir.listFiles(filter);
        if (listFile != null) {
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].isDirectory()) {
                    nodes.append("{\"label\": \"").append(listFile[i].getName().toString()).append("\"");
                    File [] lsFilesTmp = listFile[i].listFiles(filter);
                    if(lsFilesTmp != null && lsFilesTmp.length > 0){
                        nodes.append(", \"children\" : ").append(walkin(listFile[i], filter));
                    }
                    nodes.append("}").append(listFile.length-1 == i ? "" : ",");
                }
            }
        }
        nodes.append("]");
        return nodes.toString();
    }
}

class OnlyDirectoryFFImp implements FilenameFilter {
  @Override
  public boolean accept(File current, String name) {
    return new File(current, name).isDirectory();
  }
}