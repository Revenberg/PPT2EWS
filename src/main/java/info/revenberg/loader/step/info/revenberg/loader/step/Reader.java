package info.revenberg.loader.step;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Reader {

    private File folder = null;
    private static int stop = 9999;

    public static String location = "";

    public Reader(String location) throws IOException {
        Reader.stop += SongDef.getList().size();
        if (location.substring(location.length() - 1) == "/") {
            Reader.location = location;
        } else {
            Reader.location = location + "/";
        }
        this.read();
    }

    public void search(final String pattern, final File folder, final String pre) throws IOException {
        for (final File f : folder.listFiles()) {
            if (Reader.stop > SongDef.getList().size()) {
                if (f.isDirectory()) {
                    if (pre != "") {
                        search(pattern, f, pre + "/" + f.getName() + "/");
                    } else {
                        search(pattern, f, f.getName() + "/");
                    }
                }
                if (f.isFile()) {
                    if (f.getName().matches(pattern)) {
                        System.out.println(new SongDef(location + pre + f.getName()));
                    }
                }
            }
        }
    }

    public void read() throws IOException {
        if (folder == null) {
            folder = new File(location);
            search(".*", folder, "");
        }
    }

    public static void main(String[] args) throws IOException {
        SongDef.readFromDisk();

        List<SongDef> list = SongDef.getList();
        for (int i = list.size() - 1; i >= 0; i--) {
            SongDef song = list.get(i);
            if (song.getLocation() == "null" ) {
                SongDef.removeFromList(i);
            }
        }
        new Reader("d:/pptx");

        list = SongDef.getList();
        for (int i = 0; i < list.size(); i++) {
            SongDef song = list.get(i);
            // System.out.println(song);
        }
    }
}