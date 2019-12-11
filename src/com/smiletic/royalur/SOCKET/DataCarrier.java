package com.smiletic.royalur.SOCKET;

import java.io.Serializable;

public class DataCarrier implements Serializable {
    public int oldx;
    public int oldy;

    public DataCarrier(int oldx, int oldy, int newx, int newy, int team,int type) {
        this.oldx = oldx;
        this.oldy = oldy;
        this.newx = newx;
        this.newy = newy;
        this.team = team;
        this.type = type;
    }

    public int newx;
    public int newy;
    public int team;
    public int type;

}
