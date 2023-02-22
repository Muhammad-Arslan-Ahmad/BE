/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.elastic.model;

import java.util.List;
import java.util.Map;
import lombok.*;
import lombok.experimental.FieldDefaults;

/**
 *
 * @author anwar
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Narration {
    String narration;
    Integer order;
    Map<String, String> items;
}
