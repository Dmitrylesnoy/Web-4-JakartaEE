package com.lab.web.beans;

import java.io.Serializable;

import jakarta.ejb.Stateful;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Stateful
public class Point implements Serializable {
    private float x;
    private float y;
    private float r;
    private boolean isGraphClick = false;
}
