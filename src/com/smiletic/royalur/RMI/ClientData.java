package com.smiletic.royalur.RMI;

import com.smiletic.royalur.Model.User;

public class ClientData {
    private static final long serialVersionUID= 122412521512354L;
    public User user;
    public ClientInterface clientInterface;

    public ClientData(User user, ClientInterface clientInterface) {
        this.user = user;
        this.clientInterface = clientInterface;
    }
}
