package com.example.baddomain.application;

// 위반: application 레이어 입력이 *Request 명명 (REQUEST_INPUT_IS_COMMAND). Command 를 써야 함.
public class CreateThingRequest {
    private final String name;

    public CreateThingRequest(String name) {
        this.name = name;
    }

    public String name() {
        return name;
    }
}
