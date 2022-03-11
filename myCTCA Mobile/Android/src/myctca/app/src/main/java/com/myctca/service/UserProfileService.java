package com.myctca.service;

import com.myctca.common.AppSessionManager;
import com.myctca.model.MyCTCAUserProfile;

public class UserProfileService {

    private static UserProfileService userProfileService;

    public static UserProfileService getInstance() {
        if (userProfileService == null) {
            return new UserProfileService();
        }
        return userProfileService;
    }

    public MyCTCAUserProfile getMyCtcaUserProfile() {
        return AppSessionManager.getInstance().getUserProfile();
    }
}
