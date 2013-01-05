package io.gamecloud.tiktok.models;

import java.io.Serializable;

public class Clock implements Serializable {

    private static final long serialVersionUID = 1L;

    private int hh;
    private int mm;
    private int ss;

    public Clock(int hh, int mm, int ss) {
        this.hh = hh;
        this.mm = mm;
        this.ss = ss;
    }

    public int getHH() {
        return this.hh;
    }

    public int getMM() {
        return this.mm;
    }

    public int getSS() {
        return this.ss;
    }

    public void setHH(int hh) {
        this.hh = hh;
    }

    public void setMM(int mm) {
        this.mm = mm;
    }

    public void setSS(int ss) {
        this.ss = ss;
    }

    public String toString() {
        return hh + ":" + mm + ":" + ss;
    }
}
