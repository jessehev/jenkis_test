package com.ut_sdk.pay;
import com.ut_sdk.pay.PayEntity;

interface IWBPay {
    void wbPay(boolean success, inout PayEntity payEntity);
}
