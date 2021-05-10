package android_serialport_api.xingbang.models;

/**
 * Created by xingbang on 2019/7/11.
 */

public class FaceBean {

    /**
     * result : {"sftg":1,"name":"刘洪刚"}
     * isSuccess : true
     */

    private ResultBean result;
    private String isSuccess;
    private String errCode;
    private String errMsg;

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public String getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(String isSuccess) {
        this.isSuccess = isSuccess;
    }
    public String getErrCode() {
        return errCode;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }
    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public static class ResultBean {
        /**
         * sftg : 1
         * name : 刘洪刚
         */

        private int sftg;
        private String name;

        public int getSftg() {
            return sftg;
        }

        public void setSftg(int sftg) {
            this.sftg = sftg;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
