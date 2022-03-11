package com.myctca.model;

import com.myctca.util.MyCTCADateUtils;

/**
 * Created by tomackb on 2/16/18.
 */

public class ImagingDoc {

    public String id;
    public String itemName;
    public String documentDate;
    public String notes;

    public String getId() {
        return id;
    }

    public String getDocumentDate() {
        return documentDate;
    }

    public String getItemName() {
        return itemName;
    }

    public String getNotes() {
        return notes;
    }

    public String getSlashedDateFullYearString() {
        return MyCTCADateUtils.getSlashedDateFullYearStr(MyCTCADateUtils.convertShortStringToLocalDate(documentDate));
    }
}
