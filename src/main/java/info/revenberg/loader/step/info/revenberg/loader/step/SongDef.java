package info.revenberg.loader.step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class SongDef implements Serializable {
    private static final long serialVersionUID = -5615546692601633759L;

    String fileName;
    String bundleName;
    String songName;
    String location;
    static String unzipLocation = "d:/tmp";

    static List<SongDef> list = new ArrayList<SongDef>();
    List<VersDef> verzen;

    public SongDef(String fileName) throws IOException {
        if (!findFilename(fileName)) {
            if (fileName.toLowerCase().contains(".pptx")) {
                processSong(fileName);
            }
        }
    }

    public void setBundleAndSongnameFromFilename(String fileName) throws UnsupportedEncodingException {
        this.setFileName(fileName);
        String[] s = fileName.replace(Reader.location, "").split("/");
        String bundleName = "";
        for (int i = 0; i < s.length - 1; i++)
            if (bundleName == "") {
                bundleName = s[i];
            } else {
                bundleName += " - " + s[i];
            }
        String songName = s[s.length - 1].replace(".pptx", "");

        this.setBundleName(bundleName);
        this.setSongName(songName);
    }

    public void processSong(String fileName) throws IOException {
        setBundleAndSongnameFromFilename(fileName);
        String fileNameDest = URLEncoder.encode(this.getBundleName() + "-" + this.getSongName(), "UTF-8");
        this.setLocation(unzipLocation + "/" + fileNameDest);

        List<String> t1 = FileService.unzip(fileName, unzipLocation + "/_" + fileNameDest);
        this.verzen = new ArrayList<VersDef>();

        for (String temp : t1) {
            if (temp.contains(".png")) {
                storeVerse(temp);
            }
        }
        FileService.deleteFolderIfExists(new File(unzipLocation + "/_" + fileNameDest));

        list.add(this);
        writeToDisk();
    }

    public void storeVerse(String tempFile) throws IOException {
        VersDef vers = new VersDef(this, tempFile);
        this.verzen.add(vers);
    }

    private boolean findFilename(String fileName) {
        fileName = fileName.toLowerCase();
        for (int i = 0; i < list.size(); i++) {
            SongDef song = list.get(i);
            if (fileName.equals(song.getFileName())) {
                return true;
            }
        }
        return false;
    }

    public static void writeToDisk() {
        try {
            FileOutputStream writeData = new FileOutputStream(unzipLocation + "/SongDef.ser");
            ObjectOutputStream writeStream = new ObjectOutputStream(writeData);

            writeStream.writeObject(getList());
            writeStream.flush();
            writeStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void readFromDisk() {
        try {
            FileInputStream readData = new FileInputStream(unzipLocation + "/SongDef.ser");
            ObjectInputStream readStream = new ObjectInputStream(readData);

            list = (ArrayList<SongDef>) readStream.readObject();
            readStream.close();
        } catch (FileNotFoundException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<SongDef> getList() {
        return SongDef.list;
    }

    private String getFileName() {
        return this.fileName;
    }

    private void setFileName(String fileName) {
        this.fileName = fileName.toLowerCase();
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBundleName() {
        return this.bundleName;
    }

    public void setBundleName(String bundleName) throws UnsupportedEncodingException {
        this.bundleName = Normalizer.normalize(bundleName.trim(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
                .replace("  ", " ").replace("'", "").trim();
    }

    public String getSongName() {
        return this.songName;
    }

    public void setSongName(String songName) throws UnsupportedEncodingException {
        this.songName = Normalizer.normalize(songName.trim(), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "")
                .replace("  ", " ").replace("'", "").trim();
    }

    @Override
    public String toString() {
        String str = "{" + " location='" + this.getLocation() + "'" + ", bundleName='" + getBundleName() + "'"
                + ", songName='" + getSongName() + "'";
        str += " [ ";
        if (verzen != null) {
            for (int i = 0; i < verzen.size(); i++) {
                VersDef vers = verzen.get(i);
                if (i > 0) {
                    str += ", ";
                }
                str += vers.toString();
            }
        }
        str += " ] ";
        str += " }";
        return str;
    }

    public static void removeFromList(int i) {
        list.remove(i);
        writeToDisk();
    }

}
