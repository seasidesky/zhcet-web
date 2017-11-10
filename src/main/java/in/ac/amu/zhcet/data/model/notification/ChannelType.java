package in.ac.amu.zhcet.data.model.notification;

import in.ac.amu.zhcet.utils.StringUtils;

public enum ChannelType {
    DEPARTMENT,
    COURSE,
    SECTION,
    FACULTY,
    GENDER,
    STUDENT;

    @Override
    public String toString() {
        return StringUtils.capitalizeFirst(name());
    }
}
