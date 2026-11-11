package qrsimple.franz.com.qrsimple.feature.generator

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import qrsimple.franz.com.qrsimple.feature.history.FakeQrHistoryRepository

@OptIn(ExperimentalCoroutinesApi::class)
class GeneratorViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var fakeRepository: FakeQrHistoryRepository
    private lateinit var viewModel: GeneratorViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        fakeRepository = FakeQrHistoryRepository()
        viewModel = GeneratorViewModel(fakeRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `build raw payload for url adds https if missing`() {
        viewModel.onTypeSelected(GeneratorType.URL)
        viewModel.onUrlChanged("kabrima.com")
        val payload = viewModel.buildRawPayload()
        assertEquals("https://kabrima.com", payload)
    }

    @Test
    fun `build raw payload for url keeps existing https`() {
        viewModel.onTypeSelected(GeneratorType.URL)
        viewModel.onUrlChanged("https://kabrima.com/docs")
        val payload = viewModel.buildRawPayload()
        assertEquals("https://kabrima.com/docs", payload)
    }

    @Test
    fun `build raw payload for wifi formats correctly`() {
        viewModel.onTypeSelected(GeneratorType.WIFI)
        viewModel.onWifiSsidChanged("OfficeWifi")
        viewModel.onWifiPasswordChanged("Secret123")
        viewModel.onWifiEncryptionChanged("WPA")

        val payload = viewModel.buildRawPayload()
        assertEquals("WIFI:S:OfficeWifi;T:WPA;P:Secret123;;", payload)
    }

    @Test
    fun `build raw payload for whatsapp formats wa me link`() {
        viewModel.onTypeSelected(GeneratorType.WHATSAPP)
        viewModel.onWaCountryCodeChanged("+591")
        viewModel.onWaPhoneChanged("70012345")
        viewModel.onWaMessageChanged("Hola amigo")

        val payload = viewModel.buildRawPayload()
        assertTrue(payload.startsWith("https://wa.me/59170012345?text=Hola"))
    }

    @Test
    fun `build raw payload for email formats mailto link`() {
        viewModel.onTypeSelected(GeneratorType.EMAIL)
        viewModel.onEmailAddressChanged("info@kabrima.com")
        viewModel.onEmailSubjectChanged("Soporte")

        val payload = viewModel.buildRawPayload()
        assertTrue(payload.startsWith("mailto:info@kabrima.com?subject=Soporte"))
    }

    @Test
    fun `build raw payload for plain text returns text`() {
        viewModel.onTypeSelected(GeneratorType.TEXT)
        viewModel.onTextChanged("Lista de compras")

        val payload = viewModel.buildRawPayload()
        assertEquals("Lista de compras", payload)
    }

    @Test
    fun `generateQr with empty input shows error toast`() = runTest {
        viewModel.onTypeSelected(GeneratorType.TEXT)
        viewModel.onTextChanged("")
        viewModel.generateQr()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertNotNull(state.errorToast)
    }
}
