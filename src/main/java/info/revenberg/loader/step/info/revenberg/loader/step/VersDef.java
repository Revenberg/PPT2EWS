package info.revenberg.loader.step;

import java.io.IOException;
import java.io.Serializable;
import info.revenberg.loader.step.line.FindLinesInImage;

public class VersDef implements Serializable {
    private static final long serialVersionUID = 3938614111689605974L;

    String FileName;
    int id;
    int lines;

    public VersDef() {
    }

    public VersDef(SongDef song, String tempFile) throws IOException {        
        String[] s1 = tempFile.split("/");
        String versName = s1[s1.length - 1];
        String ext = FileService.getExtension(tempFile);
        versName = versName.replace("." + ext, "").replace("image", "");

        this.id = Integer.parseInt(versName) - 1;
        this.lines = 0;
        this.FileName = "vers " + Integer.toString(this.getId()) + "." + FileService.getExtension(tempFile);
        FileService.copyFile(tempFile, song.getLocation(), this.getFileName());

        FindLinesInImage f = new FindLinesInImage(song.getLocation() + "/" + this.getFileName(), song.getLocation() + "/verzen",
                Integer.toString(this.getId()));       

        this.setLines(f.getLinesCounter());
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLines() {
        return this.lines;
    }

    public void setLines(int lines) {
        this.lines = lines;
    }

    @Override
    public String toString() {
        return "{" + "FileName='" + this.getFileName() + "', id=" + this.getId() + ", lines=" + this.getLines() + " }";
    }

    private String getFileName() {
        return this.FileName;
    }
}
