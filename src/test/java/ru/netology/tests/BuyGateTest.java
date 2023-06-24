package ru.netology.tests;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import ru.netology.data.DataHelper;
import ru.netology.data.SqlHelper;
import ru.netology.pages.PaymentMethod;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.data.DataHelper.*;
import static ru.netology.data.SqlHelper.*;


public class BuyGateTest {
    PaymentMethod paymentMethod = new PaymentMethod();

    @BeforeAll
    public static void setUpAll() {

        SelenideLogger.addListener("allure", new AllureSelenide());

    }

    @BeforeEach
    public void openPage() {

        open("http://localhost:8080");

    }


    @AfterEach
    void cleanDB() {

        SqlHelper.databaseCleanUp();

    }


    @AfterAll
    public static void tearDownAll() {

        SelenideLogger.removeListener("allure");
    }

    @Test
    @SneakyThrows
    @DisplayName("Покупка валидной картой")
    public void shouldPayDebitValidCard() {
        paymentMethod.payDebitCard();
        var info = getApprovedCard();
        paymentMethod.sendingValidData(info);
        paymentMethod.bankApproved();
        var expected = DataHelper.getStatusFirstCard();
        var paymentInfo = SqlHelper.getPaymentInfo();
        var orderInfo = SqlHelper.getOrderInfo();
        var expectedAmount = "45000";
        assertEquals(expected, getPaymentInfo().getStatus());
        assertEquals(paymentInfo.getTransaction_id(), orderInfo.getPayment_id());
        assertEquals(expectedAmount, paymentInfo.getAmount());
    }


    @Test
    @SneakyThrows
    @DisplayName("Покупка дебетовой невалидной картой")
    void shouldPayDebitDeclinedCard() {
        paymentMethod.payDebitCard();
        var info = DataHelper.getDeclinedCard();
        paymentMethod.sendingNotValidData(info);
        paymentMethod.bankDeclined();
        var paymentStatus = getPaymentInfo();
        assertEquals("DECLINED", paymentStatus);
    }

    @Test
    @DisplayName("Покупка дебетовой картой без заполнения полей")
    void shouldEmptyFormDebitCard() {
        paymentMethod.payDebitCard();
        paymentMethod.pressButtonForContinue();
        paymentMethod.emptyForm();

    }

    @Test
    @DisplayName("Покупка дебетовой картой без заполнения поля карты, остальные поля - валидные данные")
    void shouldEmptyFieldCardFormDebit() {
        paymentMethod.payDebitCard();
        var info = DataHelper.getEmptyCardNumber();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldCardNumberError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой при заполнения поля карты одной цифрой, остальные поля - валидные данные")
    public void shouldOneNumberInFieldCardFormDebit() {
        paymentMethod.payDebitCard();
        var info = DataHelper.getOneNumberCardNumber();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldCardNumberError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой при заполнения поля карты 15 цифрами, остальные поля - валидные данные")
    public void shouldFifteenNumberInFieldCardNumberFormDebit() {
        paymentMethod.payDebitCard();
        var info = DataHelper.getFifteenNumberCardNumber();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldCardNumberError();
    }

    @Test
    @DisplayName("Покупка картой не из БД, остальные поля - валидные данные")
    public void shouldFakerCardNumberFormDebit() {
        paymentMethod.payDebitCard();
        var info = DataHelper.getFakerNumberCardNumber();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFakerCardNumber();
    }


    @Test
    @DisplayName("Покупка дебетовой картой без заполнения поля месяц, остальные поля - валидные данные")
    public void shouldEmptyFieldMonthFormDebit() {
        paymentMethod.payDebitCard();
        var info = getEmptyMonth();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldMonthError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой c заполнением поля месяц одной цифрой, остальные поля - валидные данные")
    public void shouldOneNumberInFieldMonthFormDebit() {
        paymentMethod.payDebitCard();
        var info = getOneNumberMonth();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldMonthError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой: в поле месяц предыдущий от текущего, остальные поля -валидные данные")
    public void shouldFieldWithPreviousMonthFormDebit() {
        paymentMethod.payDebitCard();
        var info = getPreviousMonthInField();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldMonthError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой: в поле месяц нулевой (не существующий) месяц" +
            " остальные поля - валидные данные")
    public void shouldFieldWithZeroMonthFormDebit() {
        paymentMethod.payDebitCard();
        var info = getZeroMonthInField();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldMonthError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой: в поле месяц тринадцатый (не существующий) месяц" +
            " остальные поля - валидные данные")
    public void shouldFieldWithThirteenMonthFormDebit() {
        paymentMethod.payDebitCard();
        var info = getThirteenMonthInField();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldMonthError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой без заполнения поля год, остальные поля - валидные данные")
    public void shouldEmptyFieldYearFormDebit() {
        paymentMethod.payDebitCard();
        var info = getEmptyYear();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldYearError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой: заполнение поля год, предыдущим годом от текущего" +
            " остальные поля - валидные данные")
    public void shouldPreviousYearFieldYearFormDebit() {
        paymentMethod.payDebitCard();
        var info = getPreviousYearInField();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldYearError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой: заполнение поля год, на шесть лет больше чем текущий" +
            " остальные поля - валидные данные")
    public void shouldPlusSixYearFieldYearFormDebit() {
        paymentMethod.payDebitCard();
        var info = getPlusSixYearInField();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldYearError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой: поле владелец пустое, остальные - валидные данные")
    public void shouldEmptyFieldNameFormDebit() {
        paymentMethod.payDebitCard();
        var info = getApprovedCard();
        paymentMethod.sendingEmptyNameValidData(info);
        paymentMethod.sendingValidDataWithFieldNameError();
    }


    @Test
    @DisplayName("Покупка дебетовой картой: заполнение поля владелец спец. символами" +
            " остальные поля - валидные данные")
    public void shouldSpecialSymbolInFieldNameFormDebit() {
        paymentMethod.payDebitCard();
        var info = getSpecialSymbolInFieldName();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldNameError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой: заполнение  поля владелец цифрами" +
            " остальные поля - валидные данные")
    public void shouldNumberInFieldNameFormDebit() {
        paymentMethod.payDebitCard();
        var info = getNumberInFieldName();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldNameError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой: заполнение поля владелец кириллицей" +
            " остальные поля - валидные данные")
    public void shouldEnglishNameInFieldNameFormDebit() {
        paymentMethod.payDebitCard();
        var info = DataHelper.getRusName();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldNameError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой: поле владелец только фамилия, остальные поля - валидные данные")
    public void shouldOnlySurnameFormDebit() {
        paymentMethod.payDebitCard();
        var info = DataHelper.getOnlySurnameInFieldName();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldNameError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой: поле CVV пустое" +
            " остальные поля - валидные данные")
    public void shouldEmptyCVVInFieldCVVFormDebit() {
        paymentMethod.payDebitCard();
        var info = getEmptyCVVInFieldCVV();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldCVVError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой: поле CVV одно число" +
            " остальные поля - валидные данные")
    public void shouldOneNumberInFieldCVVFormDebit() {
        paymentMethod.payDebitCard();
        var info = getOneNumberInFieldCVV();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldCVVError();
    }

    @Test
    @DisplayName("Покупка дебетовой картой: поле CVV двумя числами" +
            " остальные поля - валидные данные")
    public void shouldTwoNumberInFieldCVVАFormDebit() {
        paymentMethod.payDebitCard();
        var info = getOTwoNumberInFieldCVV();
        paymentMethod.sendingValidData(info);
        paymentMethod.sendingValidDataWithFieldCVVError();
    }
}