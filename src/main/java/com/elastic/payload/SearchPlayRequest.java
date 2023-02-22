package com.elastic.payload;

import com.elastic.common.enums.PlaySortColumn;
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
public class SearchPlayRequest implements Serializable {

    Integer from;
    Integer size;
    PlaySortColumn column;
    SortOrder sortOrder;

    String id;
    String query;
}
