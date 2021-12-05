package uz.mq.mybaby;

public class Request {
    String action;
    String fileName;

    public Request(String action, String fileName) {
        this.action = action;
        this.fileName = fileName;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
