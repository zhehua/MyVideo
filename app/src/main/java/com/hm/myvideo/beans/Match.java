package com.hm.myvideo.beans;


public class Match {
    private String playTime;
    private String game;
    private String name;
    private String link;
    private String teamFlag;
    private String guestTeamName;
    private String guestTeamLink;
    private String masterTeamName;
    private String masterTeamLink;
    private Integer id;


    public String getPlayTime() {
        return playTime;
    }

    public void setPlayTime(String playTime) {
        this.playTime = playTime;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTeamFlag() {
        return teamFlag;
    }

    public void setTeamFlag(String teamFlag) {
        this.teamFlag = teamFlag;
    }

    public String getGuestTeamName() {
        return guestTeamName;
    }

    public void setGuestTeamName(String guestTeamName) {
        this.guestTeamName = guestTeamName;
    }

    public String getGuestTeamLink() {
        return guestTeamLink;
    }

    public void setGuestTeamLink(String guestTeamLink) {
        this.guestTeamLink = guestTeamLink;
    }

    public String getMasterTeamName() {
        return masterTeamName;
    }

    public void setMasterTeamName(String masterTeamName) {
        this.masterTeamName = masterTeamName;
    }

    public String getMasterTeamLink() {
        return masterTeamLink;
    }

    public void setMasterTeamLink(String masterTeamLink) {
        this.masterTeamLink = masterTeamLink;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
