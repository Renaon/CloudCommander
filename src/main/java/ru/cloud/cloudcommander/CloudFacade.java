package ru.cloud.cloudcommander;

import java.io.Serializable;
import java.util.List;

public interface CloudFacade extends Serializable {

     String getFilename();
     void setFilename(String filename);

     String getCommand();
     void setCommand(String command);

     List<String> getFiles();
     void setFiles(List<String> files);

     String getMessage();
     void setMessage(String message);

     void setFile(byte[] file);
     byte[] getFile();

     void setPosition(long position);
     long getPosition();

     void setPassword(String password);
     String getPassword();

}
