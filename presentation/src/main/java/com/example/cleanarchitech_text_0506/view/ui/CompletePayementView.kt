package com.example.cleanarchitech_text_0506.view.ui

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.os.bundleOf
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import androidx.navigation.compose.rememberNavController
import com.example.cleanarchitech_text_0506.R
import com.example.cleanarchitech_text_0506.enum.MainView
import com.example.cleanarchitech_text_0506.enum.PaymentType
import com.example.cleanarchitech_text_0506.enum.TransactionType
import com.example.cleanarchitech_text_0506.sealed.DeviceConnectSharedFlow
import com.example.cleanarchitech_text_0506.view.ui.theme.CleanArchitech_text_0506Theme
import com.example.cleanarchitech_text_0506.viewmodel.DirectPaymentViewModel
import com.example.cleanarchitech_text_0506.viewmodel.MainActivityViewModel
import com.example.cleanarchitech_text_0506.viewmodel.TestCommunicationViewModel
import com.example.cleanarchitech_text_0506.vo.CompletePaymentViewVO
import com.example.domain.dto.request.pay.RequestDirectCancelPaymentDto
import com.example.domain.dto.request.tms.RequestInsertPaymentDataDTO
import com.example.domain.sealed.ResponsePayAPI

@Composable
fun CompletePaymentView(
    navHostController: NavController,
    completePaymentViewVO: CompletePaymentViewVO?,
    mainActivityViewModel: MainActivityViewModel = hiltViewModel(),
    directPaymentViewModel: DirectPaymentViewModel = hiltViewModel(),
    testCommunicationViewModel: TestCommunicationViewModel = hiltViewModel()
) {
    CompletePaymentMainView(
        navHostController,
        completePaymentViewVO!!,
        mainActivityViewModel,
        directPaymentViewModel,
        testCommunicationViewModel.setDeviceType()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompletePaymentMainView(
    navHostController: NavController = rememberNavController(),
    completePaymentViewVO: CompletePaymentViewVO,
    mainActivityViewModel: MainActivityViewModel?,
    directPaymentViewModel: DirectPaymentViewModel?,
    testCommunicationViewModel: TestCommunicationViewModel?
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current
    var errorMessage by remember { mutableStateOf("") }
    var clickEvent by remember { mutableStateOf(CreditPaymentViewClickEvent.Empty) }

    directPaymentViewModel?.responseDirectPayment?.CollectAsEffect(
        block = {
            when (it) {
                is ResponsePayAPI.DirectCancelPaymentContent -> {
                    val completePaymentViewVo = CompletePaymentViewVO(
                        transactionType = TransactionType.Direct,
                        paymentType = PaymentType.Refund,
                        installment = completePaymentViewVO.installment,
                        trackId = it.responseDirectCancelPaymentDto.refund?.trackId!!,
                        cardNumber = completePaymentViewVO.cardNumber,
                        amount = it.responseDirectCancelPaymentDto.refund?.amount.toString(),
                        regDay = it.responseDirectCancelPaymentDto.result.create,
                        authCode = it.responseDirectCancelPaymentDto.refund?.authCd!!,
                        trxId = it.responseDirectCancelPaymentDto.refund?.trxId!!,
                    )
                    navHostController?.navigate(
                        MainView.CompletePayment.name,
                        bundleOf("responsePayAPI" to completePaymentViewVo),
                        NavOptions.Builder().setLaunchSingleTop(true).build()
                    )
                    Toast.makeText(context, "결제 취소가 완료 되었습니다", Toast.LENGTH_LONG).show()
                }
                else -> {}
            }
        }
    )

    when {
        testCommunicationViewModel != null -> {
            testCommunicationViewModel.deviceConnectSharedFlow.CollectAsEffect(
                block = {
                    when(it) {
                        is DeviceConnectSharedFlow.PaymentCompleteFlow -> {
                            val completePaymentViewVo = CompletePaymentViewVO(
                                transactionType = TransactionType.Offline,
                                paymentType = PaymentType.Refund,
                                installment = completePaymentViewVO.installment,
                                trackId = completePaymentViewVO.trackId!!,
                                cardNumber = completePaymentViewVO.cardNumber,
                                amount = completePaymentViewVO.amount,
                                regDay = completePaymentViewVO.regDay,
                                authCode = completePaymentViewVO.authCode,
                                trxId = it.responseInsertPaymentDataDTO.trxId!!,
                            )
                            navHostController?.navigate(
                                MainView.CompletePayment.name,
                                bundleOf("responsePayAPI" to completePaymentViewVo),
                                NavOptions.Builder().setLaunchSingleTop(true).build()
                            )
                            Toast.makeText(context, "결제 취소가 완료 되었습니다", Toast.LENGTH_LONG).show()
                        }
                        else -> {}
                    }
                }
            )

        }
    }

    when (clickEvent) {
        CreditPaymentViewClickEvent.ErrorDialog -> {
            errorDialog(
                message = errorMessage,
                onDismissRequest = { clickEvent = CreditPaymentViewClickEvent.Empty },
            )
        }
        else -> {}
    }

    if(testCommunicationViewModel != null) {
        serialCommunicationResult(
            testCommunicationViewModel = testCommunicationViewModel,
            navHostController = navHostController,
            dialogMessage = {
                errorMessage = it
                clickEvent = CreditPaymentViewClickEvent.ErrorDialog
            },
            paymentType = PaymentType.Refund
        )
    }

    Scaffold(
        topBar = {
            when (completePaymentViewVO.paymentType) {
                PaymentType.Approve -> {
                    topNavigationCompletePaymentView("결제 완료 페이지")
                }
                PaymentType.Refund -> {
                    topNavigationCompletePaymentView("결제 취소 완료 페이지")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .background(color = colorResource(id = R.color.grey7)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Column(
                modifier = Modifier
                    .width((screenWidth * 0.9).dp)
                    .wrapContentHeight()
                    .paint(
                        painterResource(id = R.drawable.payment_box),
                        contentScale = ContentScale.FillBounds
                    )
            ) {
                Column(
                    modifier = Modifier.padding(top = 10.dp, start = 30.dp, bottom = 40.dp)
                ) {
                    CompletePaymentContent(
                        modifier = Modifier.padding(top = 20.dp),
                        key = "전표번호",
                        value = completePaymentViewVO.trackId
                    )
                    CompletePaymentContent(
                        modifier = Modifier.padding(top = 20.dp),
                        key = "카드번호",
                        value = completePaymentViewVO.cardNumber
                    )
                    CompletePaymentContent(
                        modifier = Modifier.padding(top = 20.dp),
                        key = "금액",
                        value = completePaymentViewVO.amount
                    )
                    CompletePaymentContent(
                        modifier = Modifier.padding(top = 20.dp),
                        key = "승인일자",
                        value = completePaymentViewVO.regDay
                    )
                    CompletePaymentContent(
                        modifier = Modifier.padding(top = 20.dp),
                        key = "승인번호",
                        value = completePaymentViewVO.authCode
                    )
                    CompletePaymentContent(
                        modifier = Modifier.padding(top = 20.dp),
                        key = "거래번호",
                        value = completePaymentViewVO.trxId
                    )
                }

                Row(
                    modifier = Modifier
                        .padding(start = 10.dp, end = 20.dp, bottom = 50.dp)
                ) {
                    if (completePaymentViewVO.paymentType == PaymentType.Approve) {
                        bottomRowButton(
                            modifier = Modifier
                                .weight(1f)
                                .background(colorResource(id = R.color.red))
                                .clickable {
                                    when(completePaymentViewVO.transactionType) {
                                        TransactionType.Direct -> {
                                            directPaymentViewModel?.requestDirectCancelPayment(
                                                RequestDirectCancelPaymentDto(
                                                    payKey = mainActivityViewModel?.getUserInformation()?.payKey!!,
                                                    amount = completePaymentViewVO.amount,
                                                    rootTrxId = completePaymentViewVO.trxId,
                                                    rootTrxDay = completePaymentViewVO.regDay
                                                )
                                            )
                                        }
                                        TransactionType.Offline -> {
                                            testCommunicationViewModel?.requestOfflinePaymentCancel(
                                                RequestInsertPaymentDataDTO(
                                                    amount = Integer.parseInt(completePaymentViewVO.amount),
                                                    installment = completePaymentViewVO.installment,
                                                    authCd = completePaymentViewVO.authCode,
                                                    regDate = completePaymentViewVO.regDay,
                                                    token = mainActivityViewModel?.getUserInformation()?.key!!,
                                                    trxId = completePaymentViewVO.trxId
                                                )
                                            )
                                        }
                                    }
                                },
                            value = "취소"
                        )
                    }
                    bottomRowButton(
                        modifier = Modifier
                            .weight(1f)
                            .background(colorResource(id = R.color.teal_700)),
                        value = "PRINT"
                    )
                    bottomRowButton(
                        modifier = Modifier
                            .weight(1f)
                            .background(colorResource(id = R.color.blackbb)),
                        value = "문자\n영수증"
                    )
                    bottomRowButton(
                        modifier = Modifier
                            .weight(1f)
                            .background(colorResource(id = R.color.grey3)),
                        value = "이미지\n영수증"
                    )
                }
            }
        }
    }
}

@Composable
fun CompletePaymentContent(
    modifier: Modifier = Modifier,
    key: String,
    value: String,
    keyFontSize: TextUnit = 17.sp,
    valueFontSize: TextUnit = 17.sp
) {
    Column(
        modifier = Modifier
            .then(modifier)
    ) {
        Text(
            text = key,
            fontSize = keyFontSize,
            color = colorResource(id = R.color.grey2)
        )
        Text(
            text = value,
            fontSize = valueFontSize
        )
    }
}

@Composable
fun bottomRowButton(
    modifier: Modifier = Modifier,
    value: String
) {
    Column(
        modifier = Modifier
            .height(42.dp)
            .padding(start = 10.dp)
            .then(modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = value,
            lineHeight = 18.sp,
            color = colorResource(id = R.color.white),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun topNavigationCompletePaymentView(title: String) {
    CenterAlignedTopAppBar(
        title = {
            Text(title, fontWeight = FontWeight.Bold)
        },
        navigationIcon = {
            Icon(Icons.Default.ArrowBack, contentDescription = "Menu")
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun CompletePaymentMainPreView() {
    CleanArchitech_text_0506Theme{
        CompletePaymentMainView(
            completePaymentViewVO = CompletePaymentViewVO(
                TransactionType.Offline,
                PaymentType.Approve,
                "00",
                "TX200316016511",
                "5409-26**-****-****",
                "1,806,004원",
                "2020-03-16 14:02:02",
                "30034798",
                "T200316016511"
            ),
            mainActivityViewModel = null,
            directPaymentViewModel = null,
            testCommunicationViewModel = null
        )
    }
}

