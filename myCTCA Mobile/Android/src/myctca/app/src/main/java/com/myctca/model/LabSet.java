package com.myctca.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by tomackb on 1/5/18.
 */

public class LabSet implements Serializable {
    private String orderId;
    private String name;

    private List<LabSetDetail> detail;

    // Getters
    public String getOrderId() {
        return orderId;
    }

    public String getName() {
        return name;
    }

    public List<LabSetDetail> getDetail() {
        return detail;
    }
}
