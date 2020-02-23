package com.darren.architect_day01.data.entity;

import com.google.gson.annotations.Since;
import com.google.gson.annotations.Until;

public class SinceUntilSample {
    @Since(4)
    public String since;
    @Until(5)
    public String until;
}