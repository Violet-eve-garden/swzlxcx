package com.sysu.swzl.pojo;

public class Type {
    private Long id;

    private String name;

    private String explanation;

    public Type(Long id, String name, String explanation) {
        this.id = id;
        this.name = name;
        this.explanation = explanation;
    }

    public Type() {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation == null ? null : explanation.trim();
    }
}