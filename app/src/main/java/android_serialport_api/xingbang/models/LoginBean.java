package android_serialport_api.xingbang.models;

import java.io.Serializable;
import java.util.List;

public class LoginBean implements Serializable {


    private String uPhone;
    private String uPwd;
    private String uCid;
    private String uCName;
    private String uFName;
    private String uIDCard;
    private List<LstDTO> lst;
    private String status;
    private String msg;

    public String getUPhone() {
        return uPhone;
    }

    public void setUPhone(String uPhone) {
        this.uPhone = uPhone;
    }

    public String getUPwd() {
        return uPwd;
    }

    public void setUPwd(String uPwd) {
        this.uPwd = uPwd;
    }

    public String getUCid() {
        return uCid;
    }

    public void setUCid(String uCid) {
        this.uCid = uCid;
    }

    public String getUCName() {
        return uCName;
    }

    public void setUCName(String uCName) {
        this.uCName = uCName;
    }

    public String getUFName() {
        return uFName;
    }

    public void setUFName(String uFName) {
        this.uFName = uFName;
    }

    public String getUIDCard() {
        return uIDCard;
    }

    public void setUIDCard(String uIDCard) {
        this.uIDCard = uIDCard;
    }

    public List<LstDTO> getLst() {
        return lst;
    }

    public void setLst(List<LstDTO> lst) {
        this.lst = lst;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static class LstDTO {
        private String uPhone;
        private String uPwd;
        private String uCid;
        private String uCName;
        private String uFName;
        private String uIDCard;

        public String getUPhone() {
            return uPhone;
        }

        public void setUPhone(String uPhone) {
            this.uPhone = uPhone;
        }

        public String getUPwd() {
            return uPwd;
        }

        public void setUPwd(String uPwd) {
            this.uPwd = uPwd;
        }

        public String getUCid() {
            return uCid;
        }

        public void setUCid(String uCid) {
            this.uCid = uCid;
        }

        public String getUCName() {
            return uCName;
        }

        public void setUCName(String uCName) {
            this.uCName = uCName;
        }

        public String getUFName() {
            return uFName;
        }

        public void setUFName(String uFName) {
            this.uFName = uFName;
        }

        public String getUIDCard() {
            return uIDCard;
        }

        public void setUIDCard(String uIDCard) {
            this.uIDCard = uIDCard;
        }

        @Override
        public String toString() {
            return "LstDTO{" +
                    "uPhone='" + uPhone + '\'' +
                    ", uPwd='" + uPwd + '\'' +
                    ", uCid='" + uCid + '\'' +
                    ", uCName='" + uCName + '\'' +
                    ", uFName='" + uFName + '\'' +
                    ", uIDCard='" + uIDCard + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "LoginBean{" +
                "uPhone='" + uPhone + '\'' +
                ", uPwd='" + uPwd + '\'' +
                ", uCid='" + uCid + '\'' +
                ", uCName='" + uCName + '\'' +
                ", uFName='" + uFName + '\'' +
                ", uIDCard='" + uIDCard + '\'' +
                ", lst=" + lst +
                ", status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
