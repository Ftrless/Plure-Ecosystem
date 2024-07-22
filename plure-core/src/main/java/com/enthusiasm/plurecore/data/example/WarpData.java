package com.enthusiasm.plurecore.data.example;

import com.enthusiasm.plurecore.data.annotation.DataAttribute;
import com.enthusiasm.plurecore.data.annotation.DataSerializable;

@DataSerializable(folder = "warp")
public class WarpData {
    @DataAttribute
    public String testData = "f";

    @DataAttribute
    public int testData2 = 1;

    @DataAttribute
    public float testData3 = 1.0f;

    @DataAttribute
    public double testData4 = 1.0d;
}
