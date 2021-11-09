package ru.cloud.cloudcommander.server.communicate;

import ru.cloud.cloudcommander.XtraCommunicate;

import java.io.File;
import java.util.List;

public class Request implements XtraCommunicate {
    private String filename;
    private int value;
    private String command;
    private List<File> fileList;
    private String message;


    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public void setCommand(String command) {
        this.command = command;
    }

    @Override
    public List<File> getFiles() {
        return fileList;
    }

    @Override
    public void setFiles(List<File> files) {
        this.fileList = files;
    }

    @Override
    public int getValue() {
        return value;
    }

    @Override
    public void setValue(int value) {
        this.value = value;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public void setMessage(String message) {
        this.message = message;
    }
}
