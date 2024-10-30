package android_serialport_api.mx.xingbang.models;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DanLingOffLinBean implements Serializable {

    @SerializedName("lgs")
    private Lgs lgs;
    @SerializedName("jbqys")
    private Jbqys jbqys;
    @SerializedName("zbqys")
    private Zbqys zbqys;
    @SerializedName("sqrq")
    private String sqrq;
    @SerializedName("cwxx")
    private String cwxx;
    @SerializedName("sbbhs")
    private List<Sbbhs> sbbhs;

    public Lgs getLgs() {
        return lgs;
    }

    public void setLgs(Lgs lgs) {
        this.lgs = lgs;
    }

    public Jbqys getJbqys() {
        return jbqys;
    }

    public void setJbqys(Jbqys jbqys) {
        this.jbqys = jbqys;
    }

    public Zbqys getZbqys() {
        return zbqys;
    }

    public void setZbqys(Zbqys zbqys) {
        this.zbqys = zbqys;
    }

    public String getSqrq() {
        return sqrq;
    }

    public void setSqrq(String sqrq) {
        this.sqrq = sqrq;
    }

    public String getCwxx() {
        return cwxx;
    }

    public void setCwxx(String cwxx) {
        this.cwxx = cwxx;
    }

    public List<Sbbhs> getSbbhs() {
        return sbbhs;
    }

    public void setSbbhs(List<Sbbhs> sbbhs) {
        this.sbbhs = sbbhs;
    }

    public static class Lgs implements Serializable {
        @SerializedName("lg")
        private List<Lg> lg;

        public List<Lg> getLg() {
            return lg;
        }

        public void setLg(List<Lg> lg) {
            this.lg = lg;
        }

        public static class Lg implements Serializable {
            @SerializedName("gzmcwxx")
            private String gzmcwxx;
            @SerializedName("uid")
            private String uid;
            @SerializedName("fbh")
            private String fbh;
            @SerializedName("yxq")
            private String yxq;
            @SerializedName("sjlx")
            private String sjlx;
            @SerializedName("gzm")
            private String gzm;

            public String getGzmcwxx() {
                return gzmcwxx;
            }

            public void setGzmcwxx(String gzmcwxx) {
                this.gzmcwxx = gzmcwxx;
            }

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getFbh() {
                return fbh;
            }

            public void setFbh(String fbh) {
                this.fbh = fbh;
            }

            public String getYxq() {
                return yxq;
            }

            public void setYxq(String yxq) {
                this.yxq = yxq;
            }

            public String getSjlx() {
                return sjlx;
            }

            public void setSjlx(String sjlx) {
                this.sjlx = sjlx;
            }

            public String getGzm() {
                return gzm;
            }

            public void setGzm(String gzm) {
                this.gzm = gzm;
            }
        }
    }

    public static class Jbqys implements Serializable {
        @SerializedName("jbqy")
        private List<?> jbqy;

        public List<?> getJbqy() {
            return jbqy;
        }

        public void setJbqy(List<?> jbqy) {
            this.jbqy = jbqy;
        }
    }

    public static class Zbqys implements Serializable {
        @SerializedName("zbqy")
        private List<Zbqy> zbqy;

        public List<Zbqy> getZbqy() {
            return zbqy;
        }

        public void setZbqy(List<Zbqy> zbqy) {
            this.zbqy = zbqy;
        }

        public static class Zbqy implements Serializable {
            @SerializedName("zbqybj")
            private String zbqybj;
            @SerializedName("zbqymc")
            private String zbqymc;
            @SerializedName("zbqywd")
            private String zbqywd;
            @SerializedName("zbqssj")
            private Object zbqssj;
            @SerializedName("zbqyjd")
            private String zbqyjd;
            @SerializedName("zbjzsj")
            private Object zbjzsj;

            public String getZbqybj() {
                return zbqybj;
            }

            public void setZbqybj(String zbqybj) {
                this.zbqybj = zbqybj;
            }

            public String getZbqymc() {
                return zbqymc;
            }

            public void setZbqymc(String zbqymc) {
                this.zbqymc = zbqymc;
            }

            public String getZbqywd() {
                return zbqywd;
            }

            public void setZbqywd(String zbqywd) {
                this.zbqywd = zbqywd;
            }

            public Object getZbqssj() {
                return zbqssj;
            }

            public void setZbqssj(Object zbqssj) {
                this.zbqssj = zbqssj;
            }

            public String getZbqyjd() {
                return zbqyjd;
            }

            public void setZbqyjd(String zbqyjd) {
                this.zbqyjd = zbqyjd;
            }

            public Object getZbjzsj() {
                return zbjzsj;
            }

            public void setZbjzsj(Object zbjzsj) {
                this.zbjzsj = zbjzsj;
            }
        }
    }

    public static class Sbbhs implements Serializable {
        @SerializedName("sbbh")
        private String sbbh;

        public String getSbbh() {
            return sbbh;
        }

        public void setSbbh(String sbbh) {
            this.sbbh = sbbh;
        }
    }
}
