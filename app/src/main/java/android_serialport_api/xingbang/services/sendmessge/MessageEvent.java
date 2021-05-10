package android_serialport_api.xingbang.services.sendmessge;

public class MessageEvent {
    private String massage;
    public MessageEvent(String massage){
        this.massage = massage;
    }
    public String getMassage(){
        return massage;
    }
    public void setMassage(String massage){
        this.massage = massage;
    }
}
