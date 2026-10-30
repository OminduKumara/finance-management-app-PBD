package com.example.pbdassignment


import com.example.pbdassignment.feature.authentication.domain.ValidateEmail
import com.example.pbdassignment.feature.authentication.domain.ValidatePassword
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ValidateAuthUseCasesTest {

    private lateinit var validateEmail: ValidateEmail
    private lateinit var validatePassword: ValidatePassword

    @Before
    fun setUp() {
        validateEmail = ValidateEmail()
        validatePassword = ValidatePassword()
    }

    @Test
    fun `empty email returns error`() {
        val result = validateEmail("")
        assertFalse(result.successful)
        assertEquals("Email cannot be empty", result.errorMessage)
    }

    @Test
    fun `malformed email returns error`() {
        val result = validateEmail("kavindu@invalid")
        assertFalse(result.successful)
        assertEquals("Invalid email format", result.errorMessage)
    }

    @Test
    fun `valid email returns success`() {
        val result = validateEmail("kavindu.silva@startup.lk")
        assertTrue(result.successful)
    }

    @Test
    fun `password shorter than 6 characters returns error`() {
        val result = validatePassword("12345")
        assertFalse(result.successful)
        assertEquals("Password must be at least 6 characters", result.errorMessage)
    }

    @Test
    fun `valid password returns success`() {
        val result = validatePassword("SecurePass2026!")
        assertTrue(result.successful)
    }
}