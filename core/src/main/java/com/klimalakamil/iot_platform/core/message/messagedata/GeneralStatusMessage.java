package com.klimalakamil.iot_platform.core.message.messagedata;

import com.klimalakamil.iot_platform.core.message.MessageData;

/**
 * Created by kamil on 19.01.17.
 */
public class GeneralStatusMessage implements MessageData {

    private GeneralCodes code;

    public GeneralStatusMessage(GeneralCodes code) {
        this.code = code;
    }

    public GeneralCodes getCode() {
        return code;
    }

    public void setCode(GeneralCodes code) {
        this.code = code;
    }
}
