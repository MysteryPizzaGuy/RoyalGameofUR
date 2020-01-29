package com.smiletic.royalur.XML;

import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Action")
public class Action {
    private int fromx;
    private int fromy;
    private int tox;
    private int toy;
    public static enum Types {MOVED,CREATED;}

    public Types getType() {
        return type;
    }

    public void setType(Types type) {
        this.type = type;
    }

    private int team;

    public Action() {
    }

    private Types type;

    public Action(int tox, int toy, int fromx, int fromy, int team, Types type) {
        this.fromx = fromx;
        this.fromy = fromy;
        this.tox = tox;
        this.toy = toy;
        this.team = team;
        this.type = type;
    }

    public int getFromx() {
        return fromx;
    }

    public void setFromx(int fromx) {
        this.fromx = fromx;
    }

    public int getFromy() {
        return fromy;
    }

    public void setFromy(int fromy) {
        this.fromy = fromy;
    }

    public int getTox() {
        return tox;
    }

    public void setTox(int tox) {
        this.tox = tox;
    }

    public int getToy() {
        return toy;
    }

    public void setToy(int toy) {
        this.toy = toy;
    }

    public int getTeam() {
        return team;
    }

    public void setTeam(int team) {
        this.team = team;
    }


}
