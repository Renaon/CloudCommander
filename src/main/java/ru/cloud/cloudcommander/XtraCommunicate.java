package ru.cloud.cloudcommander;

import io.netty.handler.codec.serialization.ClassResolver;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public interface XtraCommunicate extends Serializable {

    public String getFilename();
    public void setFilename(String filename);

    public String getCommand();
    public void setCommand(String command);

    public List<String> getFiles();
    public void setFiles(List<String> files);

    public int getValue();
    public void setValue(int value);

    public String getMessage();
    public void setMessage(String message);

    public void setFile(byte[] file);
    public byte[] getFile();

    public void setAnswer(String answer);
    public String getAnswer();

}
