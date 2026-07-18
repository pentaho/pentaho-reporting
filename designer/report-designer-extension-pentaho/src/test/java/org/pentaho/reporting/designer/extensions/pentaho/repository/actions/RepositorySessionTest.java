/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/


package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;

public class RepositorySessionTest {

  @Before
  public void setUp() {
    RepositorySession.clearSession();
  }

  @After
  public void tearDown() {
    RepositorySession.clearSession();
  }

  @Test
  public void testHasActiveSessionFalseWhenNoSession() {
    assertFalse( RepositorySession.hasActiveSession() );
  }

  @Test
  public void testHasActiveSessionTrueAfterSetSession() {
    RepositorySession.setSession( mock( AuthenticationData.class ) );
    assertTrue( RepositorySession.hasActiveSession() );
  }

  @Test
  public void testGetSessionNullInitially() {
    assertNull( RepositorySession.getSession() );
  }

  @Test
  public void testGetSessionReturnsStoredSession() {
    final AuthenticationData data = mock( AuthenticationData.class );
    RepositorySession.setSession( data );
    assertSame( data, RepositorySession.getSession() );
  }

  @Test
  public void testSetSessionNullDoesNotMarkActive() {
    RepositorySession.setSession( null );
    assertFalse( RepositorySession.hasActiveSession() );
    assertNull( RepositorySession.getSession() );
  }

  @Test
  public void testClearSessionRemovesStoredSession() {
    RepositorySession.setSession( mock( AuthenticationData.class ) );
    RepositorySession.clearSession();
    assertFalse( RepositorySession.hasActiveSession() );
    assertNull( RepositorySession.getSession() );
  }

  @Test
  public void testAddSessionChangeListenerNotifiedOnSetSession() {
    final List<AuthenticationData> received = new ArrayList<>();
    RepositorySession.addSessionChangeListener( received::add );
    final AuthenticationData data = mock( AuthenticationData.class );

    RepositorySession.setSession( data );

    // The last value added by this listener is the data just set
    assertFalse( received.isEmpty() );
    assertSame( data, received.get( received.size() - 1 ) );
  }

  @Test
  public void testAddSessionChangeListenerNotifiedOnClearSessionWithNull() {
    final List<AuthenticationData> received = new ArrayList<>();
    RepositorySession.addSessionChangeListener( received::add );

    RepositorySession.clearSession();

    assertFalse( received.isEmpty() );
    assertNull( received.get( received.size() - 1 ) );
  }

  @Test
  public void testAddSessionChangeListenerMultipleListenersAllInvoked() {
    final List<AuthenticationData> r1 = new ArrayList<>();
    final List<AuthenticationData> r2 = new ArrayList<>();
    RepositorySession.addSessionChangeListener( r1::add );
    RepositorySession.addSessionChangeListener( r2::add );

    final AuthenticationData data = mock( AuthenticationData.class );
    RepositorySession.setSession( data );

    assertFalse( r1.isEmpty() );
    assertFalse( r2.isEmpty() );
    assertSame( data, r1.get( r1.size() - 1 ) );
    assertSame( data, r2.get( r2.size() - 1 ) );
  }
}
