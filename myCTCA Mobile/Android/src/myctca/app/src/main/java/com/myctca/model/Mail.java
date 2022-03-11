package com.myctca.model;

import com.myctca.util.MyCTCADateUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by tomackb on 1/11/18.
 */

public class Mail implements Serializable {

    public static final int DO_REFRESH = 500;
    public static final int CANCEL_REFRESH = 501;

    private String mailMessageId;
    private String from;
    private String to;
    private List<CareTeam> selectedTo;
    private String subject;
    private String comments;
    private String sent;
    private String parentMessageId;
    private boolean isRead;
    private int messageType;
    private String folderName;

    public String getMailMessageId() {
        return mailMessageId;
    }

    public void setMailMessageId(String mailMessageId) {
        this.mailMessageId = mailMessageId;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public List<CareTeam> getSelectedTo() {
        return selectedTo;
    }

    public List<String> getSelectedToNames(){
        List<String> careTeamNames = new ArrayList<>();
        for(CareTeam team: getSelectedTo()){
            careTeamNames.add(team.getName());
        }
        return careTeamNames;
    }

    public void setSelectedTo(List<CareTeam> selectedTo) {
        this.selectedTo = selectedTo;
    }

    public String getCommaSeparatedToList() {

        StringBuilder commaSeparatedTo = new StringBuilder();
        for (String toName : getSelectedToNames()) {
            if (commaSeparatedTo.toString().equals("")) {
                commaSeparatedTo.append(toName);
            } else {
                commaSeparatedTo.append(",  ").append(toName);
            }
        }
        return commaSeparatedTo.toString();
    }

    public String getSubject() {
        return subject;
    }

    public String getComments() {
        return comments;
    }

    public Date getSent() {
        return MyCTCADateUtils.convertTimestampToLocalDate(sent, "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }

    public String getMonthDaySentString() {
        return MyCTCADateUtils.getMonthDateStr(MyCTCADateUtils.convertTimestampToLocalDate(sent, "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
    }

    public String getParentMessageId() {
        return parentMessageId;
    }

    public boolean getRead() {
        return isRead;
    }

    public int getMessageType() {
        return messageType;
    }

    public String getFolderName() {
        return folderName;
    }
}
