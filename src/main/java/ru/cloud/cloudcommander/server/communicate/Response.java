package ru.cloud.cloudcommander.server.communicate;

import ru.cloud.cloudcommander.CloudFacade;

import java.util.List;

public class Response implements CloudFacade {
    private String filename;
    private int value;
    private String command;
    private List<String> fileList;
    private String message;
    private byte[] file;
    private String answer;
    private long position;

    @Override
    public long getPosition(){
        return position;
    }

    @Override
    public void setPosition(long position) {
        this.position = position;
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
