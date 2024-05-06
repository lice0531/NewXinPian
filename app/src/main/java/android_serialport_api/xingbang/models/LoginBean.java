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
    private String uProvince;
    private String uMarket;
    private String uCounty;
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

    public String getUProvince() {
        return uProvince;
    }

    public void setUProvince(String uProvince) {
        this.uProvince = uProvince;
    }

    public String getUMarket() {
        return uMarket;
    }

    public void setUMarket(String uMarket) {
        this.uMarket = uMarket;
    }

    public String getUCounty() {
        return uCounty;
    }

    public void setUCounty(String uCounty) {
        this.uCounty = uCounty;
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
        private String uProvince;
        private String uMarket;
        private String uCounty;

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

        public String getUProvince() {
            return uProvince;
        }

        public void setUProvince(String uProvince) {
            this.uProvince = uProvince;
        }

        public String getUMarket() {
            return uMarket;
        }

        public void setUMarket(String uMarket) {
            this.uMarket = uMarket;
        }

        public String getUCounty() {
            return uCounty;
        }

        public void setUCounty(String uCounty) {
            this.uCounty = uCounty;
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
                    ", uProvince='" + uProvince + '\'' +
                    ", uMarket='" + uMarket + '\'' +
                    ", uCounty='" + uCounty + '\'' +
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
                ", uProvince='" + uProvince + '\'' +
                ", uMarket='" + uMarket + '\'' +
                ", uCounty='" + uCounty + '\'' +
                ", lst=" + lst +
                ", status='" + status + '\'' +
                ", msg='" + msg + '\'' +
                '}';
    }
}
