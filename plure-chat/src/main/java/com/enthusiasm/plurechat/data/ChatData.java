package com.enthusiasm.plurechat.data;

import com.enthusiasm.plurecore.data.annotation.DataAttribute;
import com.enthusiasm.plurecore.data.annotation.DataSerializable;

@DataSerializable(folder = "chat")
public class ChatData {
    @DataAttribute
    public String[] ignorableUUIDs;
}
