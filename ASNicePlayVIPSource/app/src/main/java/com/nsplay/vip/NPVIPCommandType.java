package com.nsplay.vip;

/**
 * Created by user on 2016/9/8.
 */
public enum NPVIPCommandType {
    VIPModelState_Get(0),
    VIPMemberQualifications(1),
    GameToolsVIPModalInfo_Get(2),
    GameToolsGameBindVIP(3),
    CheckBinded(4),
    CheckVerifyCode(5),
    BindPhoneAccount(6),
    FianlCheckBinded(7),
    CommunicationBIND(8);


    private final int AuthTypevalue;

    private NPVIPCommandType(int value) {
        this.AuthTypevalue = value;
    }

    public int getIntValue() {
        return AuthTypevalue;
    }


    public static NPVIPCommandType fromInteger(int x) {
        switch(x) {
            case 0:
                return VIPModelState_Get;
            case 1:
                return VIPMemberQualifications;
            case 2:
                return GameToolsVIPModalInfo_Get;
            case 3:
                return GameToolsGameBindVIP;
            case 4:
                return CheckBinded;
            case 5:
                return CheckVerifyCode;
            case 6:
                return BindPhoneAccount;
            case 7:
                return FianlCheckBinded;
            case 8:
                return CommunicationBIND;
        }
        return null;
    }
}
