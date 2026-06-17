package com.example.baddomain.domain;

// 위반: 도메인 public setter (DOMAIN_NO_PUBLIC_SETTER).
public class SetterEntity {
    private String name;
    public void setName(String name) { this.name = name; }
    public String getName() { return name; }
}
