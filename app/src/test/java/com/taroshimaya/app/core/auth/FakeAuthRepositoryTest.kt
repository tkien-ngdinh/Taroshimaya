package com.taroshimaya.app.core.auth

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

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
    fun switchRole_whileSignedOut_onlyChangesGuestRole() = runTest {
        val repo = FakeAuthRepository()
        repo.switchRole(UserRole.READER)
        val s = repo.currentSession.value!!
        assertFalse(s.isSignedIn)
        assertEquals(UserRole.READER, s.role)
    }

    @Test
    fun signOut_returnsToGuest() = runTest {
        val repo = FakeAuthRepository()
        repo.signInWithRole(UserRole.ADMIN)
        repo.signOut()
        assertEquals(FakeAuthRepository.GUEST_SESSION, repo.currentSession.value)
    }
}
