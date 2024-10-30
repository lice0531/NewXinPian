package android_serialport_api.mx.xingbang.models;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xingbang on 2019/3/18.
 */

public class DanLingBean implements Serializable {

    /**
     * cwxx : 0
     * sqrq : 2019-03-18 16:35:46
     * sbbhs : [{"sbbh":"XBTS0003"}]
     * zbqys : {"zbqy":[{"zbqymc":"山东德州","zbqyjd":"116.456535","zbqywd":"37.427541","zbqybj":"5000","zbqssj":null,"zbjzsj":null}]}
     * jbqys : {"jbqy":[]}
     * lgs : {"lg":[{"uid":"4870314100020","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100021","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100022","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100023","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100024","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100025","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100026","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100027","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100028","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100029","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100061","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100071","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100078","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100080","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100081","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100082","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100083","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100084","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100085","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100086","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100087","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100088","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100089","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100090","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100091","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100092","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100093","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100094","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100095","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100096","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100097","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100098","yxq":"","gzm":"","gzmcwxx":"3"},{"uid":"4870314100099","yxq":"","gzm":"","gzmcwxx":"3"}]}
     */

    private String cwxx;//申请错误信息 错误信息代码如下
    private String cwxxms;//申请错误信息 错误信息代码如下
    private String sqrq;//申请日期
    private ZbqysBean zbqys;//起爆器编码
    private JbqysBean jbqys;//准爆区域
    private LgsBean lgs;//每一条代表一发雷管的密码
    private List<SbbhsBean> sbbhs;

    public String getCwxx() {
        return cwxx;
    }

    public void setCwxx(String cwxx) {
        this.cwxx = cwxx;
    }
    public String getCwxxms() {
        return cwxxms;
    }

    public void setCwxxms(String cwxxms) {
        this.cwxxms = cwxx;
    }

    public String getSqrq() {
        return sqrq;
    }

    public void setSqrq(String sqrq) {
        this.sqrq = sqrq;
    }

    public ZbqysBean getZbqys() {
        return zbqys;
    }

    public void setZbqys(ZbqysBean zbqys) {
        this.zbqys = zbqys;
    }

    public JbqysBean getJbqys() {
        return jbqys;
    }

    public void setJbqys(JbqysBean jbqys) {
        this.jbqys = jbqys;
    }

    public LgsBean getLgs() {
        return lgs;
    }

    public void setLgs(LgsBean lgs) {
        this.lgs = lgs;
    }

    public List<SbbhsBean> getSbbhs() {
        return sbbhs;
    }

    public void setSbbhs(List<SbbhsBean> sbbhs) {
        this.sbbhs = sbbhs;
    }

    @Override
    public String toString() {
        return "{" +
                "cwxx='" + cwxx + '\'' +
                ", sqrq='" + sqrq + '\'' +
                ", zbqys=" + zbqys +
                ", jbqys=" + jbqys +
                ", lgs=" + lgs +
                ", sbbhs=" + sbbhs +
                '}';
    }

    public static class ZbqysBean implements Serializable{
        private List<ZbqyBean> zbqy;

        public List<ZbqyBean> getZbqy() {
            return zbqy;
        }

        public void setZbqy(List<ZbqyBean> zbqy) {
            this.zbqy = zbqy;
        }

        public static class ZbqyBean implements Serializable{
            /**
             * zbqymc : 山东德州
             * zbqyjd : 116.456535
             * zbqywd : 37.427541
             * zbqybj : 5000
             * zbqssj : null
             * zbjzsj : null
             */

            private String zbqymc;//准爆区域名称
            private String zbqyjd;//准爆区域中心位置经度
            private String zbqywd;//准爆区域中心位置纬度
            private String zbqybj;//准爆区域半径
            private Object zbqssj;//准爆起始时间
            private Object zbjzsj;//准爆截止时间

            public String getZbqymc() {
                return zbqymc;
            }

            public void setZbqymc(String zbqymc) {
                this.zbqymc = zbqymc;
            }

            public String getZbqyjd() {
                return zbqyjd;
            }

            public void setZbqyjd(String zbqyjd) {
                this.zbqyjd = zbqyjd;
            }

            public String getZbqywd() {
                return zbqywd;
            }

            public void setZbqywd(String zbqywd) {
                this.zbqywd = zbqywd;
            }

            public String getZbqybj() {
                return zbqybj;
            }

            public void setZbqybj(String zbqybj) {
                this.zbqybj = zbqybj;
            }

            public Object getZbqssj() {
                return zbqssj;
            }

            public void setZbqssj(Object zbqssj) {
                this.zbqssj = zbqssj;
            }

            public Object getZbjzsj() {
                return zbjzsj;
            }

            public void setZbjzsj(Object zbjzsj) {
                this.zbjzsj = zbjzsj;
            }

            @Override
            public String toString() {
                return "{" +
                        "zbqymc='" + zbqymc + '\'' +
                        ", zbqyjd='" + zbqyjd + '\'' +
                        ", zbqywd='" + zbqywd + '\'' +
                        ", zbqybj='" + zbqybj + '\'' +
                        ", zbqssj=" + zbqssj +
                        ", zbjzsj=" + zbjzsj +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "ZbqysBean{" +
                    "zbqy=" + zbqy +
                    '}';
        }
    }

    public static class JbqysBean implements Serializable{
        private List<?> jbqy;

        public List<?> getJbqy() {
            return jbqy;
        }

        public void setJbqy(List<?> jbqy) {
            this.jbqy = jbqy;
        }

        @Override
        public String toString() {
            return "{" +
                    "jbqy=" + jbqy +
                    '}';
        }
    }

    public static class LgsBean implements Serializable{
        private List<LgBean> lg;

        public List<LgBean> getLg() {
            return lg;
        }

        public void setLg(List<LgBean> lg) {
            this.lg = lg;
        }

        public static class LgBean implements Serializable{
            /**
             * uid : 4870314100020
             * yxq :
             * gzm :
             * gzmcwxx : 3
             */

            private String uid;//雷管UID码
            private String yxq;//工作码有效期
            private String gzm;//工作码
            private String gzmcwxx;//雷管工作码错误信息，比如：雷管已用、雷管已被公安机关列

            public String getUid() {
                return uid;
            }

            public void setUid(String uid) {
                this.uid = uid;
            }

            public String getYxq() {
                return yxq;
            }

            public void setYxq(String yxq) {
                this.yxq = yxq;
            }

            public String getGzm() {
                return gzm;
            }

            public void setGzm(String gzm) {
                this.gzm = gzm;
            }

            public String getGzmcwxx() {
                return gzmcwxx;
            }

            public void setGzmcwxx(String gzmcwxx) {
                this.gzmcwxx = gzmcwxx;
            }

            @Override
            public String toString() {
                return "{" +
                        "uid='" + uid + '\'' +
                        ", yxq='" + yxq + '\'' +
                        ", gzm='" + gzm + '\'' +
                        ", gzmcwxx='" + gzmcwxx + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "{" +
                    "lg=" + lg +
                    '}';
        }
    }

    public static class SbbhsBean implements Serializable{
        /**
         * sbbh : XBTS0003
         */

        private String sbbh;

        public String getSbbh() {
            return sbbh;
        }

        public void setSbbh(String sbbh) {
            this.sbbh = sbbh;
        }

        @Override
        public String toString() {
            return "{" +
                    "sbbh='" + sbbh + '\'' +
                    '}';
        }
    }
}
