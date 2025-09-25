package com.ftn.model;

import org.kie.api.definition.type.Position;

public class Component {
    @Position(0)
    private String name;
    @Position(1)
    private String parent;

    public Component(String name, String parent) {
        this.name = name;
        this.parent = parent;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getParent() { return parent; }
    public void setParent(String parent) { this.parent = parent; }


    @Override
    public String toString() {
        return "Component{" +
                "name='" + name + '\'' +
                ", parent='" + parent + '\'' +
                '}';
    }
}
