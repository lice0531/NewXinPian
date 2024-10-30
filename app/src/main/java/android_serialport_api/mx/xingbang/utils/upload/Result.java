package android_serialport_api.mx.xingbang.utils.upload;

/**
 * 执行每一个动作后响应的结果，包括成功的和失败的.
 */
public class Result {

    /**
     * 响应的内容.
     */
    private String response;

    /**
     * 响应的结果.
     */
    private boolean succeed;

    /**
     * 响应的时间.
     */
    private String time;



    /**
     * 存储路径
     */
    private String path;

    /**
     * 构造方法.
     *
     * @param res 响应的内容
     */
    public Result(String res) {
        this.response = res;
    }

    /**
     * 构造方法.
     *
     * @param suc 响应的结果
     * @param ti 响应的时间
     * @param res 响应的内容
     * @param res 存储路径
     */
    public Result(boolean suc, String ti, String res, String path) {
        this.succeed = suc;
        this.time = ti;
        this.response = res;
        this.path = path;
    }

    /**
     * 得到响应内容.
     * @return 响应内容
     */
    public String getResponse() {
        return response;
    }

    /**
     * 设置相应内容.
     *
     * @param response 响应内容
     */
    public void setResponse(String response) {
        this.response = response;
    }

    /**
     * 得到相应结果.
     *
     * @return 相应结果
     */
    public boolean isSucceed() {
        return succeed;
    }

    /**
     * 设置响应结果.
     *
     * @param succeed 响应结果
     */
    public void setSucceed(boolean succeed) {
        this.succeed = succeed;
    }

    /**
     * 得到响应时间.
     *
     * @return 响应时间
     */
    public String getTime() {
        return time;
    }

    /**
     * 设置响应时间.
     *
     * @param time 响应时间
     */
    public void setTime(String time) {
        this.time = time;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}