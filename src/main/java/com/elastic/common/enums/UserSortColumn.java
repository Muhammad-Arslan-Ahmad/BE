package com.elastic.common.enums;

public enum UserSortColumn {
    USER_NAME("userName.keyword"),
    CREATED_AT("createdAt");

    private final String value;

    UserSortColumn(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
