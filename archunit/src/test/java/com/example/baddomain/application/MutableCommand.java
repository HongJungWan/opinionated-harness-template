package com.example.baddomain.application;

// 위반: Command 가 가변(비-final 필드 + setter) (COMMAND_IS_IMMUTABLE).
public class MutableCommand {
    private String name;
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
}
