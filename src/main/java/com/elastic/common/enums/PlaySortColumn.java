package com.elastic.common.enums;

public enum PlaySortColumn {
    NAME("playName.keyword"),
    CREATED_AT("createdAt");

    private final String value;

    PlaySortColumn(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
