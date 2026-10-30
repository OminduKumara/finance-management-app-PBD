package com.example.pbdassignment


import com.example.pbdassignment.core.common.Resource
import com.example.pbdassignment.feature.authentication.domain.User
import com.example.pbdassignment.feature.authentication.domain.AuthRepository
import com.example.pbdassignment.feature.authentication.domain.Login
import com.example.pbdassignment.feature.authentication.domain.Register
import com.example.pbdassignment.feature.authentication.presentation.AuthUiEvent
import com.example.pbdassignment.feature.authentication.presentation.AuthViewModel
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AuthViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val mockRepository = mockk<AuthRepository>()
    private lateinit var viewModel: AuthViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        val login = Login(mockRepository)
        val register = Register(mockRepository)
        viewModel = AuthViewModel(login, register)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `login with valid credentials updates authSuccess to true`() = runTest(testDispatcher) {
        val user = User("uid123", "kavindu@test.com", "Kavindu Silva")
        coEvery { mockRepository.login(any(), any()) } returns Resource.Success(user)

        viewModel.onEvent(AuthUiEvent.OnEmailChanged("kavindu@test.com"))
        viewModel.onEvent(AuthUiEvent.OnPasswordChanged("password123"))
        viewModel.onEvent(AuthUiEvent.OnLoginSubmit)

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.authSuccess)
        assertEquals(false, state.isLoading)
    }

    @Test
    fun `login failure updates generalErrorMessage`() = runTest(testDispatcher) {
        coEvery { mockRepository.login(any(), any()) } returns Resource.Error("Invalid email or password.")

        viewModel.onEvent(AuthUiEvent.OnEmailChanged("kavindu@test.com"))
        viewModel.onEvent(AuthUiEvent.OnPasswordChanged("wrongpassword"))
        viewModel.onEvent(AuthUiEvent.OnLoginSubmit)

        testScheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertEquals("Invalid email or password.", state.generalErrorMessage)
        assertEquals(false, state.authSuccess)
    }
}