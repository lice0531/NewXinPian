package android_serialport_api.mx.xingbang.services.sendmessge;

/**
 * Created by suwen on 2018/4/8.
 */

public class WebConfig {
    private int port;//端口
    private int maxParallels;//最大监听数

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxParallels() {
        return maxParallels;
    }

    public void setMaxParallels(int maxParallels) {
        this.maxParallels = maxParallels;
    }
}
