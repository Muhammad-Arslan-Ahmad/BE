package com.elastic.common.enums;

public enum RoleSortColumn {
    ROLE_NAME("roleName.keyword"),
    JOB_TITLE("jobTitle.keyword"),
    CREATED_AT("createdAt");

    private final String value;

    RoleSortColumn(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
