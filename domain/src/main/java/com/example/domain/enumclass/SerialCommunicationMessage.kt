package com.example.domain.enumclass



enum class SerialCommunicationMessage(val message: String) {
    IcCardInsertRequest("카드를 IC 슬롯에 넣어 주세요"),
    NotExistRegisteredDevice("장치 등록 후 결제 진행 해 주시기 바랍니다"),
    PaymentProgressing("결제 진행 중 입니다.\n 잠시만 기다려 주세요"),
    CompletePayment("결제가 정상적으로 \n 완료 되었습니다."),
    FallBackMessage("FallBack 거래발생. 마그네틱으로 카드를 읽혀주세요");

    fun fallbackDetail(detailMessage: String): String{
        return message + detailMessage
    }
}