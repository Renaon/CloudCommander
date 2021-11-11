package ru.cloud.cloudcommander;

import java.io.File;
import java.util.List;

public interface XtraCommunicate {

    public String getFilename();
    public void setFilename(String filename);

    public String getCommand();
    public void setCommand(String command);

    public List<File> getFiles();
    public void setFiles(List<File> files);

    public int getValue();
    public void setValue(int value);

    public String getMessage();
    public void setMessage(String message);

    public void setFile(byte[] file);
    public byte[] getFile();

    public void setAnswer(String answer);
    public String getAnswer();

}
