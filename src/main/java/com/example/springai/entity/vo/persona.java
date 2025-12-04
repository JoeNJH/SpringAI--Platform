package com.example.springai.entity.vo;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class persona {

    private String name;
    private Integer age;
    private String personality;
    private String writingStyle;
    private String speakingStyle;
    private String currentState;
    private String goal;
}
