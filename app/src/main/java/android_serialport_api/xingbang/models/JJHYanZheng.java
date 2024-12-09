package android_serialport_api.xingbang.models;

import java.util.List;
/**
 * 金建华验证
 * */
public class JJHYanZheng {

    private DataDTO data;
    private int code;
    private Object message;
    private Object headers;
    private int excuteTime;
    private boolean success;
    private List<String> errors;

    public DataDTO getData() {
        return data;
    }

    public void setData(DataDTO data) {
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public Object getHeaders() {
        return headers;
    }

    public void setHeaders(Object headers) {
        this.headers = headers;
    }

    public int getExcuteTime() {
        return excuteTime;
    }

    public void setExcuteTime(int excuteTime) {
        this.excuteTime = excuteTime;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public static class DataDTO {
        private String checkRecordId;
        private Object deviceNO;
        private Object projectCode;
        private boolean isPass;
        private String msg;
        private Object userId;
        private Object userName;
        private String checkTime;
        private int expires;
        private boolean canForceDetonate;
        private String description;

        public String getCheckRecordId() {
            return checkRecordId;
        }

        public void setCheckRecordId(String checkRecordId) {
            this.checkRecordId = checkRecordId;
        }

        public Object getDeviceNO() {
            return deviceNO;
        }

        public void setDeviceNO(Object deviceNO) {
            this.deviceNO = deviceNO;
        }

        public Object getProjectCode() {
            return projectCode;
        }

        public void setProjectCode(Object projectCode) {
            this.projectCode = projectCode;
        }

        public boolean isIsPass() {
            return isPass;
        }

        public void setIsPass(boolean isPass) {
            this.isPass = isPass;
        }

        public String getMsg() {
            return msg;
        }

        public void setMsg(String msg) {
            this.msg = msg;
        }

        public Object getUserId() {
            return userId;
        }

        public void setUserId(Object userId) {
            this.userId = userId;
        }

        public Object getUserName() {
            return userName;
        }

        public void setUserName(Object userName) {
            this.userName = userName;
        }

        public String getCheckTime() {
            return checkTime;
        }

        public void setCheckTime(String checkTime) {
            this.checkTime = checkTime;
        }

        public int getExpires() {
            return expires;
        }

        public void setExpires(int expires) {
            this.expires = expires;
        }

        public boolean isCanForceDetonate() {
            return canForceDetonate;
        }

        public void setCanForceDetonate(boolean canForceDetonate) {
            this.canForceDetonate = canForceDetonate;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        @Override
        public String toString() {
            return "DataDTO{" +
                    "checkRecordId='" + checkRecordId + '\'' +
                    ", deviceNO=" + deviceNO +
                    ", projectCode=" + projectCode +
                    ", isPass=" + isPass +
                    ", msg='" + msg + '\'' +
                    ", userId=" + userId +
                    ", userName=" + userName +
                    ", checkTime='" + checkTime + '\'' +
                    ", expires=" + expires +
                    ", canForceDetonate=" + canForceDetonate +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "JJHYanZheng{" +
                "data=" + data +
                ", code=" + code +
                ", message=" + message +
                ", headers=" + headers +
                ", excuteTime=" + excuteTime +
                ", success=" + success +
                ", errors=" + errors +
                '}';
    }
}
