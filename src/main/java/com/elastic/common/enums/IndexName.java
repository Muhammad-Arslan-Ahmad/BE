package com.elastic.common.enums;

public enum IndexName {
    INDEX_PROOF("index_proof"),
    INDEX_INDUSTRY("index_industry"),
    INDEX_PRODUCT("index_product"),
    INDEX_USER("index_user"),
    INDEX_MESSAGE("index_message"),
    INDEX_PLAY("index_play"),
    INDEX_ROLES("index_roles");


    private final String value;

    IndexName(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}
