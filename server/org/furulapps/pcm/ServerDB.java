/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
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

    static private SQLiteConnection db;
    static private String dbfile;
    static private Properties prop;
    static final private String SELPLAYERCVS = "SELECT ID, hex(NAME) NAME, hex(SHIRTNAME) SHIRTNAME, hex(LINKEDFACE) LINKEDFACE, hex(FACESLOT) FACESLOT, hex(LINKEDHAIR) LINKEDHAIR, hex(HAIRSLOT) HAIRSLOT, hex(CLUBTEAM) CLUBTEAM, hex(NATIONALTEAM) NATIONALTEAM, hex(ISCONFIG) ISCONFIG FROM PLAYERCSV WHERE ";
    static final private String SELPLAYERCVSCNT = "SELECT count(0) FROM PLAYERCSV WHERE ";
    static final private String SELPLAYERMAP = "SELECT ID, hex(FACEFILE) FACEFILE, hex(HAIRFILE) HAIRFILE, hex(REALNAME) REALNAME, hex(EXISTSFACE) EXISTSFACE, hex(EXISTSHAIR) EXISTSHAIR FROM PLAYERMAP WHERE ";
    static final private String SELPLAYERMAPCNT = "SELECT count(0) FROM PLAYERMAP WHERE ";
    static final private String SELPLAYERFILE = "SELECT hex(IDFILE) IDFILE, hex(FILENAME) FILENAME, hex(SHORTFILENAME) SHORTFILENAME, hex(ISUSED) ISUSED FROM PLAYERFILE WHERE ";
    static final private String SELFILE2PLAYER = "select hex(IDFILE) IDFILE, hex(FILENAME) FILENAME, hex(SHORTFILENAME) SHORTFILENAME, hex(ISUSED) ISUSED, hex(1) TYPE from PLAYERFILE\n"
            + "where exists(select 1 from PLAYERMAP where id=? and EXISTSFACE=1\n"
            + "and (lower(PLAYERFILE.SHORTFILENAME)=lower(FACEFILE)))\n"
            + "union\n"
            + "select hex(IDFILE) IDFILE, hex(FILENAME) FILENAME, hex(SHORTFILENAME) SHORTFILENAME, hex(ISUSED) ISUSED, hex(2) TYPE from PLAYERFILE\n"
            + "where exists(select 1 from PLAYERMAP where id=? and EXISTSFACE=0\n"
            + "and (lower(PLAYERFILE.SHORTFILENAME)=lower(FACEFILE)))\n"
            + "union\n"
            + "select hex(IDFILE) IDFILE, hex(FILENAME) FILENAME, hex(SHORTFILENAME) SHORTFILENAME, hex(ISUSED) ISUSED, hex(3) TYPE from PLAYERFILE\n"
            + "where exists(select 1 from PLAYERMAP where id=? and EXISTSHAIR=1\n"
            + "and (lower(PLAYERFILE.SHORTFILENAME)=lower(HAIRFILE)))\n"
            + "union\n"
            + "select hex(IDFILE) IDFILE, hex(FILENAME) FILENAME, hex(SHORTFILENAME) SHORTFILENAME, hex(ISUSED) ISUSED, hex(4) TYPE from PLAYERFILE\n"
            + "where exists(select 1 from PLAYERMAP where id=? and EXISTSHAIR=0\n"
            + "and (lower(PLAYERFILE.SHORTFILENAME)=lower(HAIRFILE)))";
    static final private String SELFILE2PLAYERDEEP = "select hex(IDFILE) IDFILE, hex(FILENAME) FILENAME, hex(SHORTFILENAME) SHORTFILENAME, hex(ISUSED) ISUSED, hex(5) TYPE from PLAYERFILE\n"
            + "where exists(select 1 from PLAYERCSV where id=? and\n"
            + "lower(SHORTFILENAME) like '%'||lower(replace(NAME,' ','%'))||'%')\n"
            + "union\n"
            + "select hex(IDFILE) IDFILE, hex(FILENAME) FILENAME, hex(SHORTFILENAME) SHORTFILENAME, hex(ISUSED) ISUSED, hex(6) TYPE from PLAYERFILE\n"
            + "where exists(select 1 from PLAYERCSV where id=? and\n"
            + "lower(SHORTFILENAME) like '%'||lower(replace(SHIRTNAME,' ','%'))||'%')\n"
            + "union\n"
            + "select hex(IDFILE) IDFILE, hex(FILENAME) FILENAME, hex(SHORTFILENAME) SHORTFILENAME, hex(ISUSED) ISUSED, hex(7) TYPE from PLAYERFILE\n"
            + "where exists(select 1 from PLAYERCSV where id=? and\n"
            + "lower(SHORTFILENAME) like '%'||lower(replace(NATIONALITY,' ','%'))||'%')\n"
            + "union\n"
            + "select hex(IDFILE) IDFILE, hex(FILENAME) FILENAME, hex(SHORTFILENAME) SHORTFILENAME, hex(ISUSED) ISUSED, hex(8) TYPE from PLAYERFILE\n"
            + "where exists(select 1 from PLAYERCSV where id=? and trim(CLUBTEAM) <> '' and\n"
            + "lower(SHORTFILENAME) like '%'||lower(replace(CLUBTEAM,' ','%'))||'%')\n"
            + "union\n"
            + "select hex(IDFILE) IDFILE, hex(FILENAME) FILENAME, hex(SHORTFILENAME) SHORTFILENAME, hex(ISUSED) ISUSED, hex(9) TYPE from PLAYERFILE\n"
            + "where exists(select 1 from PLAYERCSV where id=? and trim(NATIONALTEAM) <> '' and\n"
            + "lower(SHORTFILENAME) like '%'||lower(replace(NATIONALTEAM,' ','%'))||'%')";

    static {
        try {
            prop = new Properties();
            prop.load(new FileInputStream("../conf.properties"));
            /*queue = new SQLiteQueue(new File(prop.getProperty("dbpcmjava")));
            queue.start();*/
            dbfile = prop.getProperty("dbpcmjava");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ServerDB.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ServerDB.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void open() throws SQLiteException {
        db = new SQLiteConnection(new File(prop.getProperty("dbpcmjava")));
        db.open(true);
        //db.exec("pragma encoding=\"UTF-8\"");
    }

    private static void close() {
        db.dispose();
    }
    private static void stopDB(SQLiteQueue queue) throws InterruptedException {
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

    private static long getCount(String query) throws SQLiteException {
        long count = 0;
        open();

        SQLiteStatement st = db.prepare(query);
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
            protected String job(SQLiteConnection db) throws SQLiteException, InterruptedException {
                StringBuilder buff = new StringBuilder();
                buff.append("{\"PLAYERSCSV\": [");
                SQLiteStatement st = db.prepare(SELPLAYERCVS + where + " LIMIT ? OFFSET ?");
                try {
                    st.bind(1, count);
                    st.bind(2, start);
                    while (st.step()) {
                        buff.append("{\"ID\": \"")
                                .append(st.columnString(0))
                                .append("\",");
                        for (int i = 1; i < 10; i++) {
                            buff.append("\"").append(st.getColumnName(i)).append("\":\"").append(hexToString(st.columnString(i))).append(i != 9 ? "\"," : "\"");
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
                buff.append("], \"COUNT\": \"");
                buff.append(getCount(SELPLAYERCVSCNT + where));
                buff.append("\"}");
                return buff.toString();
            }
        }).complete();
        stopDB(queue);
        return ret;
    }

    private static String convertCountryClub(String country, String club) {
        return ("all".equals(country) ? "1=1" : "NATIONALITY='" + country + "'") + " and " + ("all".equals(club) ? "1=1" : "CLUBTEAM='" + club + "'");
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
                convertDataLike(new String[]{"NAME", "SHIRTNAME"}, name) + " and " + convertCountryClub(country, club), start, count);
    }

    public static String getPlayersNoConfig(String country, String club, String name, int start, int count) throws SQLiteException, UnsupportedEncodingException, InterruptedException {
        /* from CSV */

        return getPlayersCSV(
                convertDataLike(new String[]{"NAME", "SHIRTNAME"}, name) + " and ISCONFIG = 0 and " + convertCountryClub(country, club), start, count);
    }

    private static String getPlayersMap(String where, int start, int count) throws SQLiteException, UnsupportedEncodingException {
        open();
        StringBuilder buff = new StringBuilder();
        buff.append("{\"PLAYERSMAP\": [");
        SQLiteStatement st = db.prepare(SELPLAYERMAP + where + " LIMIT ? OFFSET ?");
        try {
            st.bind(1, count);
            st.bind(2, start);
            while (st.step()) {
                buff.append("{\"ID\": \"")
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
        buff.append("], \"COUNT\": \"");
        buff.append(getCount(SELPLAYERMAPCNT + where));
        buff.append("\"}");
        close();
        return buff.toString().replace("\\", "\\\\");
    }

    public static String getPlayersMap(String country, String club, String name, int start, int count) throws SQLiteException, UnsupportedEncodingException {
        /* from Map */
        return getPlayersMap(
                convertDataLike(new String[]{"REALNAME", "FACEFILE", "HAIRFILE"}, name) + " and " + convertCountryClub(country, club), start, count);
    }

    public static String getPlayersMapConflic(String country, String club, String name, int start, int count) throws SQLiteException, UnsupportedEncodingException {
        /* from Map */
        return getPlayersMap(
                convertDataLike(new String[]{"REALNAME", "FACEFILE", "HAIRFILE"}, name) + " and " + "(EXISTSFACE=0 or EXISTSHAIR=0 or not exists(select 1 from PLAYERCSV where PLAYERCSV.id=PLAYERMAP.id)) and " + convertCountryClub(country, club), start, count);
    }

    public static String getClubs() throws InterruptedException {
        SQLiteQueue queue = startDB();
        String ret = queue.execute(new SQLiteJob<String>() {
            @Override
            protected String job(SQLiteConnection db) throws SQLiteException, InterruptedException {
                StringBuilder buff = new StringBuilder();
                buff.append("{\"CLUB\": [");
                SQLiteStatement st = db.prepare("select hex(CLUBTEAM) from TEAM");
                try {
                    while (st.step()) {
                        buff.append("{\"NAME\": \"")
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
                buff.append("{\"COUNTRY\": [");
                SQLiteStatement st = db.prepare("select hex(NATIONALITY) from COUNTRY order by NATIONALITY");
                try {
                    while (st.step()) {
                        buff.append("{\"NAME\": \"")
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

    private static String getFiles(String where, int start, int count) throws SQLiteException {
        open();
        StringBuilder buff = new StringBuilder();
        buff.append("{\"FILES\": [");
        SQLiteStatement st = db.prepare(SELPLAYERFILE + where + " LIMIT ? OFFSET ?");
        try {
            st.bind(1, count);
            st.bind(2, start);
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
        buff.append("]}");
        close();
        return buff.toString().replace("\\", "\\\\");
    }

    public static String getFiles(int start, int count) throws SQLiteException {
        return getFiles(" 1=1 ", start, count);
    }

    public static String getFilesConflic(int start, int count) throws SQLiteException {
        return getFiles(" ISUSED=0 ", start, count);
    }

    public static String getFilesToPlayer(int player, boolean deep) throws SQLiteException {
        open();
        StringBuilder buff = new StringBuilder();
        String query = (deep ? SELFILE2PLAYER + " union " + SELFILE2PLAYERDEEP : SELFILE2PLAYER) + " order by TYPE";
        buff.append("{\"FILESTOPLAYER\": [");
        SQLiteStatement st = db.prepare(query);
        for (int i = 1; i <= (deep ? 9 : 4); i++) {
            st.bind(i, player);
        }
        int founds = 0;
        try {
            while (st.step()) {
                founds++;
                buff.append("{");
                for (int i = 0; i < 5; i++) {
                    buff.append("\"").append(st.getColumnName(i)).append("\":\"").append(hexToString(st.columnString(i))).append(i != 4 ? "\"," : "\"");
                }
                buff.append("},");
            }
        } finally {
            st.dispose();
        }
        if (founds == 0 && !deep) {
            buff.delete(0, buff.length());
            buff.append("{\"FILESTOPLAYER\": [");
            st = db.prepare(SELFILE2PLAYERDEEP + " order by TYPE");
            for (int i = 1; i <= 5; i++) {
                st.bind(i, player);
            }
            try {
                while (st.step()) {
                    founds++;
                    buff.append("{");
                    for (int i = 0; i < 5; i++) {
                        buff.append("\"").append(st.getColumnName(i)).append("\":\"").append(hexToString(st.columnString(i))).append(i != 4 ? "\"," : "\"");
                    }
                    buff.append("},");
                }
            } finally {
                st.dispose();
            }
        }

        int comma = buff.lastIndexOf(",");
        if (comma > 0) {
            buff = buff.deleteCharAt(comma);
        }
        buff.append("]}");
        close();
        return buff.toString().replace("\\", "\\\\");
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
        return "SELECT ID, hex(NAME) NAME, hex(SHIRTNAME) SHIRTNAME, hex(LINKEDFACE) LINKEDFACE, hex(FACESLOT) FACESLOT, hex(LINKEDHAIR) LINKEDHAIR, hex(HAIRSLOT) HAIRSLOT, hex(CLUBTEAM) CLUBTEAM, hex(NATIONALTEAM) NATIONALTEAM, hex(ISCONFIG) ISCONFIG, hex(1) TYPE\n"
                + "FROM PLAYERCSV WHERE " + getSplitName(" lower(NAME) like '%?%' or lower(SHIRTNAME) like '%?%' ", fileName) + " \n"
                + "union\n"
                + "SELECT ID, hex(NAME) NAME, hex(SHIRTNAME) SHIRTNAME, hex(LINKEDFACE) LINKEDFACE, hex(FACESLOT) FACESLOT, hex(LINKEDHAIR) LINKEDHAIR, hex(HAIRSLOT) HAIRSLOT, hex(CLUBTEAM) CLUBTEAM, hex(NATIONALTEAM) NATIONALTEAM, hex(ISCONFIG) ISCONFIG, hex(2) TYPE\n"
                + "FROM PLAYERCSV WHERE " + (search.length > 2 ? " 1=1 " : " 1=0 ") + " and (lower(CLUBTEAM) like '%" + (search.length > 2 ? search[search.length - 2] : "") + "%' or lower(NATIONALTEAM) like '%" + (search.length > 2 ? search[search.length - 2] : "") + "%' or lower(NATIONALITY) like '%" + (search.length > 2 ? search[search.length - 2] : "") + "%') order by TYPE";
    }

    public static String getPlayersToFile(int idFile, boolean deep) throws SQLiteException {
        open();
        SQLiteStatement st = db.prepare("select hex(lower(SHORTFILENAME)) from PLAYERFILE where idfile=" + idFile);
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
                    buff.append("{\"PLAYERSCSV\": [");
                    st = db.prepare(getSelPlayer2File(fileName, deep ? search : new String[]{}));
                    try {
                        while (st.step()) {
                            buff.append("{\"ID\": \"")
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
                    buff.append("]}");
                } else {
                    buff.append("{\"error\": \"NOTHING FOUND\"}");
                }
            } else {
                buff.append("{\"error\": \"NOTHING FOUND\"}");
            }
        } finally {
            st.dispose();
        }
        close();
        return buff.toString().replace("\\", "\\\\");
    }

    public static ByteArrayOutputStream getFileById(String fileid) throws FileNotFoundException, IOException, SQLiteException {
        open();
        String fileName = "";
        SQLiteStatement st = db.prepare(SELPLAYERFILE + " IDFILE = " + fileid);
        try {
            if (st.step()) {
                fileName = hexToString(st.columnString(1));
            }
        } finally {
            st.dispose();
        }
        close();
        if (fileName.equals("")) {
            return new ByteArrayOutputStream();
        } else {
            return BinToPNG.getImagePNG(fileName);
        }
    }
}
