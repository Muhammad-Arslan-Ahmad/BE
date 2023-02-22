package com.elastic.payload;

import com.elastic.common.enums.MessageSortColumn;
import com.elastic.common.enums.SortOrder;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchMessageRequest implements Serializable {

    Integer from;
    Integer size;
    MessageSortColumn column;
    SortOrder sortOrder;

    String id;
    String query;
}
