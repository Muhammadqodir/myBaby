package uz.mq.mybaby;

public class ResponseModel {
    int accurancy;
    int result;

    public int getAccurancy() {
        return accurancy;
    }

    public void setAccurancy(int accurancy) {
        this.accurancy = accurancy;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public ResponseModel(int accurancy, int result) {
        this.accurancy = accurancy;
        this.result = result;
    }

    public ResponseModel() {
    }
}
