package uz.mq.mybaby;

public class HistoryModel {
    String date;
    String id;
    ApiMaster.Result result;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ApiMaster.Result getResult() {
        return result;
    }

    public void setResult(ApiMaster.Result result) {
        this.result = result;
    }

    public HistoryModel(String date, String id, ApiMaster.Result result) {
        this.date = date;
        this.id = id;
        this.result = result;
    }
}
