package com.example.adminsidedemoproject.Model;

public class DashboardModel {
    String name;
    String icon;
    String notification;

    public DashboardModel(String name, String icon, String notification) {
        this.name = name;
        this.icon = icon;
        this.notification = notification;
    }

    public DashboardModel() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setNotification(String notification) {
        this.notification = notification;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getNotification() {
        return notification;
    }
}
