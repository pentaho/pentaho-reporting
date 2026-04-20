/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/

package org.pentaho.reporting.designer.extensions.pentaho.repository;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;

public class RepositorySessionManagerTest {

  private static final String ADMIN_USERNAME = "admin";

  @Before
  public void setUp() {
    RepositorySessionManager.getInstance().clearSession();
  }

  @After
  public void tearDown() {
    RepositorySessionManager.getInstance().clearSession();
  }

  @Test
  public void getInstanceReturnsSameInstance() {
    RepositorySessionManager instance1 = RepositorySessionManager.getInstance();
    RepositorySessionManager instance2 = RepositorySessionManager.getInstance();
    assertNotNull( instance1 );
    assertSame( instance1, instance2 );
  }

  @Test
  public void initialStateNoActiveSession() {
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
    assertNull( RepositorySessionManager.getInstance().getSession() );
    assertNull( RepositorySessionManager.getInstance().getLoggedInUser() );
  }

  @Test
  public void setSessionStoresValues() {
    AuthenticationData session = mock( AuthenticationData.class );
    RepositorySessionManager.getInstance().setSession( session, "testUser" );

    assertTrue( RepositorySessionManager.getInstance().hasActiveSession() );
    assertSame( session, RepositorySessionManager.getInstance().getSession() );
    assertEquals( "testUser", RepositorySessionManager.getInstance().getLoggedInUser() );
  }

  @Test
  public void clearSessionResetsState() {
    AuthenticationData session = mock( AuthenticationData.class );
    RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );
    assertTrue( RepositorySessionManager.getInstance().hasActiveSession() );

    RepositorySessionManager.getInstance().clearSession();

    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
    assertNull( RepositorySessionManager.getInstance().getSession() );
    assertNull( RepositorySessionManager.getInstance().getLoggedInUser() );
  }

  @Test
  public void clearSessionWhenAlreadyClear() {
    RepositorySessionManager.getInstance().clearSession();
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  @Test
  public void setSessionOverwritesPrevious() {
    AuthenticationData session1 = mock( AuthenticationData.class );
    AuthenticationData session2 = mock( AuthenticationData.class );

    RepositorySessionManager.getInstance().setSession( session1, "user1" );
    RepositorySessionManager.getInstance().setSession( session2, "user2" );

    assertSame( session2, RepositorySessionManager.getInstance().getSession() );
    assertEquals( "user2", RepositorySessionManager.getInstance().getLoggedInUser() );
  }

  @Test
  public void propertyChangeListenerFiredOnSetSession() {
    PropertyChangeListener listener = mock( PropertyChangeListener.class );
    RepositorySessionManager.getInstance().addPropertyChangeListener( listener );

    AuthenticationData session = mock( AuthenticationData.class );
    RepositorySessionManager.getInstance().setSession( session, "user" );

    verify( listener ).propertyChange( any( PropertyChangeEvent.class ) );

    RepositorySessionManager.getInstance().removePropertyChangeListener( listener );
  }

  @Test
  public void propertyChangeListenerFiredOnClearSession() {
    AuthenticationData session = mock( AuthenticationData.class );
    RepositorySessionManager.getInstance().setSession( session, "user" );

    PropertyChangeListener listener = mock( PropertyChangeListener.class );
    RepositorySessionManager.getInstance().addPropertyChangeListener( listener );

    RepositorySessionManager.getInstance().clearSession();

    verify( listener ).propertyChange( any( PropertyChangeEvent.class ) );

    RepositorySessionManager.getInstance().removePropertyChangeListener( listener );
  }

  @Test
  public void removePropertyChangeListenerNoMoreEvents() {
    PropertyChangeListener listener = mock( PropertyChangeListener.class );
    RepositorySessionManager.getInstance().addPropertyChangeListener( listener );
    RepositorySessionManager.getInstance().removePropertyChangeListener( listener );

    AuthenticationData session = mock( AuthenticationData.class );
    RepositorySessionManager.getInstance().setSession( session, "user" );

    verify( listener, never() ).propertyChange( any( PropertyChangeEvent.class ) );
  }

  @Test
  public void setSessionAllowsNullUsername() {
    AuthenticationData session = mock( AuthenticationData.class );
    RepositorySessionManager.getInstance().setSession( session, null );

    assertTrue( RepositorySessionManager.getInstance().hasActiveSession() );
    assertNull( RepositorySessionManager.getInstance().getLoggedInUser() );
  }

  @Test
  public void hasActiveSessionTrueAfterSet() {
    AuthenticationData session = mock( AuthenticationData.class );
    RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );
    assertTrue( RepositorySessionManager.getInstance().hasActiveSession() );
  }

  @Test
  public void hasActiveSessionFalseAfterClear() {
    AuthenticationData session = mock( AuthenticationData.class );
    RepositorySessionManager.getInstance().setSession( session, ADMIN_USERNAME );
    RepositorySessionManager.getInstance().clearSession();
    assertFalse( RepositorySessionManager.getInstance().hasActiveSession() );
  }
}
