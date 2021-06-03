package logging;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeLogger {
    FileWriter ts_tj;
    long tsStart = 0;
    long tsEnd = 0;
    long ts = 0;
    long tjStart = 0;
    long tjEnd = 0;
    long tj = 0;
    long gap = 0;

    public TimeLogger(String path) throws IOException {
        String logDirPath = path + "logs".replace("/", File.separator);
        File logDir = new File(logDirPath);
        if(!logDir.exists()){
            logDir.mkdir();
        }

        Date date = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String finalPath = logDirPath + "/log_" + formatter.format(date) + ".log";
        finalPath = finalPath.replace("/", File.separator);
        File ts_tj_file = new File(finalPath);

        ts_tj = new FileWriter(ts_tj_file, true);
    }

    public void startTSTimer() {
        tsStart = System.nanoTime();
    }

    public void endTSTimer() {
        tsEnd = System.nanoTime();
        ts = tsEnd - tsStart;
    }

    public void startTJTimer() {
        tjStart = System.nanoTime();
    }

    public void pause() {
        gap = System.nanoTime();
    }

    public void resume()  {
        gap = System.nanoTime() - gap;
    }

    public void endTJTimer() {
        tjEnd = System.nanoTime();
        tj = tjStart - tjEnd - gap;

    }

    public void write() throws IOException {
        ts_tj.write(ts + "," + tj);
        ts_tj.flush();
    }

    public void end() throws IOException {
        long tsStart = 0;
        long tsEnd = 0;
        long ts = 0;
        long tjStart = 0;
        long tjEnd = 0;
        long tj = 0;
        ts_tj.close();
    }
}
