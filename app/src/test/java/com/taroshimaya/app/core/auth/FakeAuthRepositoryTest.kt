package com.taroshimaya.app.core.auth

import com.taroshimaya.app.core.di.DefaultAppContainer
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Locale

class FakeAuthRepositoryTest {

    @Test
    fun startsSignedOutAsGuestUser() {
        val session = FakeAuthRepository().currentSession.value
        assertNotNull(session)
        assertFalse(session!!.isSignedIn)
        assertEquals(UserRole.USER, session.role)
        assertNull(session.email)
    }

    @Test
    fun signInWithRole_givesStableDistinctUserPerRole() = runTest {
        val repo = FakeAuthRepository()
        val uids = UserRole.entries.map { role ->
            repo.signInWithRole(role)
            val s = repo.currentSession.value!!
            assertTrue(s.isSignedIn)
            assertEquals(role, s.role)
            assertNotNull(s.email)
            s.uid
        }
        assertEquals(3, uids.toSet().size)

        repo.signInWithRole(UserRole.READER)
        assertEquals(uids[UserRole.READER.ordinal], repo.currentSession.value!!.uid)
    }

    @Test
    fun switchRole_whileSignedIn_changesRoleAndStaysSignedIn() = runTest {
        val repo = FakeAuthRepository()
        repo.signInWithRole(UserRole.USER)
        val before = repo.currentSession.value!!

        repo.switchRole(UserRole.ADMIN)

        val after = repo.currentSession.value!!
        assertTrue(after.isSignedIn)
        assertEquals(UserRole.ADMIN, after.role)
        assertNotEquals(before.uid, after.uid)
    }

    @Test
    fun switchRole_whileSignedOut_throwsAndKeepsGuestSession() = runTest {
        val repo = FakeAuthRepository()

        UserRole.entries.forEach { role ->
            assertThrows(IllegalStateException::class.java) {
                runBlocking { repo.switchRole(role) }
            }
            assertEquals(FakeAuthRepository.GUEST_SESSION, repo.currentSession.value)
        }
    }

    @Test
    fun switchRole_afterSignOut_throws() = runTest {
        val repo = FakeAuthRepository()
        repo.signInWithRole(UserRole.ADMIN)
        repo.signOut()

        assertThrows(IllegalStateException::class.java) {
            runBlocking { repo.switchRole(UserRole.ADMIN) }
        }
        assertEquals(FakeAuthRepository.GUEST_SESSION, repo.currentSession.value)
    }

    @Test
    fun switchRole_cyclesThroughAllThreeRolesAndBack() = runTest {
        val repo = FakeAuthRepository()
        repo.signInWithRole(UserRole.USER)
        val start = repo.currentSession.value

        listOf(UserRole.READER, UserRole.ADMIN, UserRole.USER).forEach { role ->
            repo.switchRole(role)
            assertEquals(FakeAuthRepository.fakeSessionFor(role), repo.currentSession.value)
        }
        assertEquals(start, repo.currentSession.value)
    }

    @Test
    fun fakeSessions_haveDocumentedFixedValues() {
        val expected = mapOf(
            UserRole.USER to Triple("fake-user-001", "Fake User", "user@fake.taroshimaya.test"),
            UserRole.READER to Triple("fake-reader-001", "Fake Reader", "reader@fake.taroshimaya.test"),
            UserRole.ADMIN to Triple("fake-admin-001", "Fake Admin", "admin@fake.taroshimaya.test"),
        )
        expected.forEach { (role, values) ->
            val s = FakeAuthRepository.fakeSessionFor(role)
            assertEquals(values.first, s.uid)
            assertEquals(values.second, s.displayName)
            assertEquals(values.third, s.email)
        }
        assertEquals("fake-guest", FakeAuthRepository.GUEST_SESSION.uid)
        assertEquals("Taroshimaya Guest", FakeAuthRepository.GUEST_SESSION.displayName)
    }

    @Test
    fun fakeSessions_doNotDependOnDefaultLocale() {
        val original = Locale.getDefault()
        try {
            Locale.setDefault(Locale.forLanguageTag("tr-TR"))
            assertEquals("fake-admin-001", FakeAuthRepository.fakeSessionFor(UserRole.ADMIN).uid)
            assertEquals("Fake Reader", FakeAuthRepository.fakeSessionFor(UserRole.READER).displayName)
        } finally {
            Locale.setDefault(original)
        }
    }

    @Test
    fun appContainer_authRepository_supportsRoleSwitching() = runTest {
        val repo = DefaultAppContainer().authRepository
        repo.signInWithRole(UserRole.USER)
        repo.switchRole(UserRole.ADMIN)

        val s = repo.currentSession.value!!
        assertTrue(s.isSignedIn)
        assertEquals(UserRole.ADMIN, s.role)
        assertEquals("fake-admin-001", s.uid)
    }

    @Test
    fun signOut_returnsToGuest() = runTest {
        val repo = FakeAuthRepository()
        repo.signInWithRole(UserRole.ADMIN)
        repo.signOut()
        assertEquals(FakeAuthRepository.GUEST_SESSION, repo.currentSession.value)
    }
}
