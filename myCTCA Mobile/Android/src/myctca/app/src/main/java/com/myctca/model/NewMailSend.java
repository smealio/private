package com.myctca.model;

import java.util.List;

public class NewMailSend {
    public String from;
    public List<CareTeam> to;
    public List<CareTeam> selectedTo;
    public String subject;
    public String comments;
    public String sent;
    public String parentMessageId;
    public int messageType;
    public String folderName;

}
