package ru.cloud.cloudcommander.server.communicate;

import ru.cloud.cloudcommander.XtraCommunicate;

import java.io.File;
import java.util.List;

public class Request implements XtraCommunicate{
    private String filename;
    private int value;
    private String command;
    private List<String> fileList;
    private String message;
    private byte[] file;
    private String answer;

    @Override
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    @Override
    public String getAnswer() {
        return this.answer;
    }

    @Override
    public byte[] getFile() {
        return file;
    }

    @Override
    public void setFile(byte[] file){
        this.file = file;
    }

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
    public List<String> getFiles() {
        return fileList;
    }

    @Override
    public void setFiles(List<String> files) {
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
