package com.github.mpawelek.faces;

import javax.faces.component.FacesComponent;

/**
 * Created by michal on 2017-04-07.
 */
@FacesComponent(value="com.github.mpawelek.components.Div", namespace = "com.github.mpawelek.faces", createTag = true, tagName = "div")
public class Div extends HtmlCommand {

    @Override
    public String getTagName() {
        return "div";
    }
}
