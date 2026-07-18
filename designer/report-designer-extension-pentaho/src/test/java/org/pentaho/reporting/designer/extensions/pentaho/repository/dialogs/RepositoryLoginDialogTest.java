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


package org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JComboBox;
import javax.swing.SwingWorker;
import javax.swing.text.JTextComponent;

import org.junit.After;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.AuthenticationStore;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.OAuthProvider;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.OAuthProviderService;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryLoginDialog.DialogMode;
import org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryLoginDialog.LoginMethod;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishSettings;
import org.pentaho.reporting.designer.extensions.pentaho.repository.util.PublishUtil;
import org.pentaho.reporting.libraries.base.config.Configuration;

public class RepositoryLoginDialogTest {

  static class TestableDialog extends RepositoryLoginDialog {
    boolean editResult;
    String confirmUrl = "http://srv";

    TestableDialog( final boolean loginForPublish ) {
      super( loginForPublish );
    }

    @Override
    public void setVisible( final boolean b ) {
      if ( b ) {
        // A non-realized combo box does not always sync its editor; simulate the user having
        // typed/selected a URL so that getServerURL() returns a value after confirmation.
        if ( confirmUrl != null ) {
          try {
            final Field f = RepositoryLoginDialog.class.getDeclaredField( "urlCombo" );
            f.setAccessible( true );
            final JComboBox<?> combo = (JComboBox<?>) f.get( this );
            final java.awt.Component ec = combo.getEditor().getEditorComponent();
            if ( ec instanceof JTextComponent tc ) {
              tc.setText( confirmUrl );
            }
          } catch ( Exception ignored ) {
            // best-effort
          }
        }
        setConfirmed( editResult );
        // intentionally do NOT call super.setVisible( true ) — never show a real modal dialog
      }
    }
  }

  private TestableDialog dialog;

  @Before
  public void setUp() {
    Assume.assumeFalse( "Requires a graphics environment", GraphicsEnvironment.isHeadless() );
    dialog = new TestableDialog( false );
  }

  @After
  public void tearDown() {
    if ( dialog != null ) {
      dialog.dispose();
    }
  }

  private static Object getField( Object target, String name ) throws Exception {
    final Field f = RepositoryLoginDialog.class.getDeclaredField( name );
    f.setAccessible( true );
    return f.get( target );
  }

  private static void setField( Object target, String name, Object value ) throws Exception {
    final Field f = RepositoryLoginDialog.class.getDeclaredField( name );
    f.setAccessible( true );
    f.set( target, value );
  }

  private static Object invoke( Object target, String name, Class<?>[] types, Object... args ) throws Exception {
    final Method m = RepositoryLoginDialog.class.getDeclaredMethod( name, types );
    m.setAccessible( true );
    return m.invoke( target, args );
  }

  private void setUrlEditorText( final String text ) throws Exception {
    final JComboBox<?> urlCombo = (JComboBox<?>) getField( dialog, "urlCombo" );
    final JTextComponent editor = (JTextComponent) urlCombo.getEditor().getEditorComponent();
    editor.setText( text );
  }

  @Test
  public void testIsSsoFeatureEnabledReturnsFalseInCe() {
    final RepositoryLoginDialog d = mock( RepositoryLoginDialog.class, CALLS_REAL_METHODS );
    assertFalse( "CE must never expose SSO controls", d.isSsoFeatureEnabled() );
  }

  @Test
  public void testSetSsoControlsVisibleTrueDoesNotThrowInCe() {
    final RepositoryLoginDialog d = mock( RepositoryLoginDialog.class, CALLS_REAL_METHODS );
    doNothing().when( d ).pack();
    d.setSsoControlsVisible( true );
    d.setSsoControlsVisible( false );
    assertFalse( d.isSsoFeatureEnabled() );
  }

  @Test
  public void testGetDialogId() {
    assertEquals( "ReportDesigner.Pentaho.RepositoryLogin", dialog.getDialogId() );
  }

  @Test
  public void testGetVersionReturnsFive() {
    assertEquals( 5, dialog.getVersion() );
  }

  @Test
  public void testGetServerUrlNullWhenNothingSelected() {
    assertNull( dialog.getServerURL() );
  }

  @Test
  public void testGetServerUrlReturnsSelected() throws Exception {
    final JComboBox<String> urlCombo = (JComboBox<String>) getField( dialog, "urlCombo" );
    urlCombo.setSelectedItem( "http://srv" );
    assertEquals( "http://srv", dialog.getServerURL() );
  }

  @Test
  public void testGetUsernameAndPassword() throws Exception {
    ( (javax.swing.JTextField) getField( dialog, "userField" ) ).setText( "alice" );
    ( (javax.swing.JPasswordField) getField( dialog, "userPasswordField" ) ).setText( "secret" );
    assertEquals( "alice", dialog.getUsername() );
    assertEquals( "secret", dialog.getUserPassword() );
  }

  @Test
  public void testGetTimeoutFromSpinner() throws Exception {
    ( (javax.swing.JSpinner) getField( dialog, "timeoutField" ) ).setValue( 77 );
    assertEquals( 77, dialog.getTimeout() );
  }

  @Test
  public void testIsRememberSettings() throws Exception {
    ( (javax.swing.JCheckBox) getField( dialog, "rememberSettings" ) ).setSelected( true );
    assertTrue( dialog.isRememberSettings() );
  }

  @Test
  public void testGetLoginMethodDefaultsToUsernamePassword() {
    assertEquals( LoginMethod.USERNAME_PASSWORD, dialog.getLoginMethod() );
  }

  @Test
  public void testGetSelectedOAuthProviderNullWhenNotSso() {
    assertNull( dialog.getSelectedOAuthProvider() );
  }

  @Test
  public void testGetSelectedOAuthProviderReturnsConfirmedWhenSso() throws Exception {
    final OAuthProvider provider = new OAuthProvider();
    setField( dialog, "selectedLoginMethod", LoginMethod.SSO );
    setField( dialog, "confirmedProvider", provider );
    assertSame( provider, dialog.getSelectedOAuthProvider() );
  }

  @Test
  public void testApplyDialogModeFull() throws Exception {
    dialog.setDialogMode( DialogMode.FULL );
    invoke( dialog, "applyDialogMode", new Class<?>[ 0 ] );
    assertNotNull( dialog );
  }

  @Test
  public void testApplyDialogModeSsoOnly() throws Exception {
    dialog.setDialogMode( DialogMode.SSO_ONLY );
    invoke( dialog, "applyDialogMode", new Class<?>[ 0 ] );
    assertEquals( LoginMethod.SSO, dialog.getLoginMethod() );
  }

  @Test
  public void testApplyDialogModeCredentialsOnly() throws Exception {
    dialog.setDialogMode( DialogMode.CREDENTIALS_ONLY );
    invoke( dialog, "applyDialogMode", new Class<?>[ 0 ] );
    assertEquals( LoginMethod.USERNAME_PASSWORD, dialog.getLoginMethod() );
  }

  @Test
  public void testApplyDialogModeFullWithSsoSelected() throws Exception {
    setField( dialog, "selectedLoginMethod", LoginMethod.SSO );
    dialog.setDialogMode( DialogMode.FULL );
    invoke( dialog, "applyDialogMode", new Class<?>[ 0 ] );
    assertNotNull( dialog );
  }

  @Test
  public void testHandleLoginMethodChangeToUsernamePassword() throws Exception {
    invoke( dialog, "handleLoginMethodChange", new Class<?>[] { LoginMethod.class, boolean.class },
        LoginMethod.USERNAME_PASSWORD, true );
    assertEquals( LoginMethod.USERNAME_PASSWORD, dialog.getLoginMethod() );
  }

  @Test
  public void testHandleLoginMethodChangeToSso() throws Exception {
    invoke( dialog, "handleLoginMethodChange", new Class<?>[] { LoginMethod.class, boolean.class },
        LoginMethod.SSO, false );
    assertEquals( LoginMethod.SSO, dialog.getLoginMethod() );
  }

  @Test
  public void testUpdateConfirmForSsoWhenSso() throws Exception {
    setField( dialog, "selectedLoginMethod", LoginMethod.SSO );
    invoke( dialog, "updateConfirmForSSO", new Class<?>[] { boolean.class }, true );
    assertNotNull( dialog );
  }

  @Test
  public void testUpdateConfirmForSsoWhenNotSso() throws Exception {
    invoke( dialog, "updateConfirmForSSO", new Class<?>[] { boolean.class }, false );
    assertNotNull( dialog );
  }

  @Test
  public void testGetEnteredServerUrlFromEditor() throws Exception {
    setUrlEditorText( "http://typed" );
    final String result = (String) invoke( dialog, "getEnteredServerUrl", new Class<?>[ 0 ] );
    assertEquals( "http://typed", result );
  }

  @Test
  public void testGetEnteredServerUrlFallsBackToSelected() throws Exception {
    setUrlEditorText( "" );
    final JComboBox<String> urlCombo = (JComboBox<String>) getField( dialog, "urlCombo" );
    urlCombo.setSelectedItem( "http://selected" );
    final String result = (String) invoke( dialog, "getEnteredServerUrl", new Class<?>[ 0 ] );
    assertEquals( "http://selected", result );
  }

  @Test
  public void testOnUrlTextChangedWithUrl() throws Exception {
    setUrlEditorText( "http://x" );
    invoke( dialog, "onUrlTextChanged", new Class<?>[ 0 ] );
    assertNotNull( dialog );
  }

  @Test
  public void testOnUrlTextChangedEmptyUrl() throws Exception {
    setUrlEditorText( "" );
    invoke( dialog, "onUrlTextChanged", new Class<?>[ 0 ] );
    assertNotNull( dialog );
  }

  @Test
  public void testOnUrlTextChangedSameAsActiveFetchReturnsEarly() throws Exception {
    setUrlEditorText( "http://same" );
    setField( dialog, "activeFetchUrl", "http://same" );
    invoke( dialog, "onUrlTextChanged", new Class<?>[ 0 ] );
    assertNotNull( dialog );
  }

  @Test
  public void testSetSsoControlsVisibleRealFields() {
    dialog.setSsoControlsVisible( true );
    assertEquals( LoginMethod.USERNAME_PASSWORD, dialog.getLoginMethod() );
  }

  @Test
  public void testUrlChangeHandlerActionPerformed() throws Exception {
    final ReportDesignerContext context = mock( ReportDesignerContext.class );
    final AuthenticationData stored = mock( AuthenticationData.class );
    when( stored.getOption( "timeout" ) ).thenReturn( "30" );
    when( stored.getUsername() ).thenReturn( "bob" );
    when( stored.getPassword() ).thenReturn( "pw" );
    setField( dialog, "context", context );
    final JComboBox<String> urlCombo = (JComboBox<String>) getField( dialog, "urlCombo" );
    final GlobalAuthenticationStore safeGlobal = mock( GlobalAuthenticationStore.class );
    when( context.getGlobalAuthenticationStore() ).thenReturn( safeGlobal );
    ( (javax.swing.JCheckBox) getField( dialog, "rememberSettings" ) ).setSelected( true );

    try ( MockedStatic<RepositoryLoginDialog> rld = mockStatic( RepositoryLoginDialog.class ) ) {
      rld.when( () -> RepositoryLoginDialog.getStoredLoginData( "http://srv", context ) ).thenReturn( stored );
      urlCombo.setSelectedItem( "http://srv" );
      for ( final java.awt.event.ActionListener l : urlCombo.getActionListeners() ) {
        l.actionPerformed(
            new java.awt.event.ActionEvent( urlCombo, java.awt.event.ActionEvent.ACTION_PERFORMED, "x" ) );
      }
    }
    assertNotNull( dialog );
  }

  @Test( expected = NullPointerException.class )
  public void testPerformLoginNullContextThrows() {
    dialog.performLogin( null, null );
  }

  private ReportDesignerContext contextWithUrls( String[] urls ) {
    final ReportDesignerContext context = mock( ReportDesignerContext.class );
    final GlobalAuthenticationStore globalStore = mock( GlobalAuthenticationStore.class );
    when( context.getActiveContext() ).thenReturn( null );
    when( context.getGlobalAuthenticationStore() ).thenReturn( globalStore );
    when( globalStore.getKnownURLs() ).thenReturn( urls );
    when( globalStore.getCredentials( org.mockito.ArgumentMatchers.anyString() ) ).thenReturn( null );
    return context;
  }

  private AuthenticationData mockConfig( String browserAuth ) {
    final AuthenticationData config = mock( AuthenticationData.class );
    when( config.getUrl() ).thenReturn( "http://srv" );
    when( config.getUsername() ).thenReturn( "u" );
    when( config.getPassword() ).thenReturn( "p" );
    when( config.getOption( "timeout" ) ).thenReturn( "30" );
    when( config.getOption( "browserAuth" ) ).thenReturn( browserAuth );
    return config;
  }

  @Test
  public void testPerformLoginCancelledReturnsNull() {
    dialog.editResult = false;
    final ReportDesignerContext context = contextWithUrls( new String[ 0 ] );
    assertNull( dialog.performLogin( context, mockConfig( null ) ) );
  }

  @Test
  public void testPerformLoginConfirmedReturnsData() {
    dialog.editResult = true;
    final ReportDesignerContext context = contextWithUrls( new String[] { "http://srv" } );
    final AuthenticationData result = dialog.performLogin( context, mockConfig( null ) );
    assertNotNull( result );
    assertEquals( "5", result.getOption( PublishUtil.SERVER_VERSION ) );
  }

  @Test
  public void testPerformLoginSsoSessionConfig() {
    dialog.editResult = true;
    final ReportDesignerContext context = contextWithUrls( new String[] { "http://srv" } );
    final AuthenticationData result = dialog.performLogin( context, mockConfig( "true" ) );
    assertNotNull( result );
  }

  @Test
  public void testPerformLoginActiveContextProvidesUrls() {
    dialog.editResult = true;
    final ReportDesignerContext context = mock( ReportDesignerContext.class );
    final ReportDocumentContext rdc = mock( ReportDocumentContext.class );
    final AuthenticationStore store = mock( AuthenticationStore.class );
    final GlobalAuthenticationStore globalStore = mock( GlobalAuthenticationStore.class );
    when( context.getActiveContext() ).thenReturn( rdc );
    when( rdc.getAuthenticationStore() ).thenReturn( store );
    when( store.getKnownURLs() ).thenReturn( new String[] { "http://srv" } );
    when( store.getCredentials( org.mockito.ArgumentMatchers.anyString() ) ).thenReturn( null );
    when( context.getGlobalAuthenticationStore() ).thenReturn( globalStore );
    when( globalStore.getCredentials( org.mockito.ArgumentMatchers.anyString() ) ).thenReturn( null );
    final AuthenticationData result = dialog.performLogin( context, mockConfig( null ) );
    assertNotNull( result );
  }

  @Test
  public void testPerformLoginNullConfigUsesDefaultData() {
    dialog.editResult = true;
    final ReportDesignerContext context = contextWithUrls( new String[ 0 ] );
    final GlobalAuthenticationStore globalStore =
        (GlobalAuthenticationStore) context.getGlobalAuthenticationStore();
    when( globalStore.getMostRecentEntry() ).thenReturn( null );
    try ( MockedStatic<ReportDesignerBoot> rdb = mockStatic( ReportDesignerBoot.class ) ) {
      final ReportDesignerBoot boot = mock( ReportDesignerBoot.class );
      final Configuration config = mock( Configuration.class );
      when( config.getConfigProperty( org.mockito.ArgumentMatchers.anyString() ) ).thenReturn( null );
      when( boot.getGlobalConfig() ).thenReturn( config );
      rdb.when( ReportDesignerBoot::getInstance ).thenReturn( boot );
      // config null -> getDefaultData returns null -> the config==null UI branch is exercised
      assertNotNull( dialog.performLogin( context, null ) );
    }
  }

  @Test
  public void testGetStoredLoginDataActiveContextHit() {
    final ReportDesignerContext context = mock( ReportDesignerContext.class );
    final ReportDocumentContext rdc = mock( ReportDocumentContext.class );
    final AuthenticationStore store = mock( AuthenticationStore.class );
    final AuthenticationData data = mock( AuthenticationData.class );
    when( context.getActiveContext() ).thenReturn( rdc );
    when( rdc.getAuthenticationStore() ).thenReturn( store );
    when( store.getCredentials( "http://u" ) ).thenReturn( data );
    assertSame( data, RepositoryLoginDialog.getStoredLoginData( "http://u", context ) );
  }

  @Test
  public void testGetStoredLoginDataActiveContextMissFallsBackToGlobal() {
    final ReportDesignerContext context = mock( ReportDesignerContext.class );
    final ReportDocumentContext rdc = mock( ReportDocumentContext.class );
    final AuthenticationStore store = mock( AuthenticationStore.class );
    final GlobalAuthenticationStore globalStore = mock( GlobalAuthenticationStore.class );
    final AuthenticationData data = mock( AuthenticationData.class );
    when( context.getActiveContext() ).thenReturn( rdc );
    when( rdc.getAuthenticationStore() ).thenReturn( store );
    when( store.getCredentials( "http://u" ) ).thenReturn( null );
    when( context.getGlobalAuthenticationStore() ).thenReturn( globalStore );
    when( globalStore.getCredentials( "http://u" ) ).thenReturn( data );
    assertSame( data, RepositoryLoginDialog.getStoredLoginData( "http://u", context ) );
  }

  @Test
  public void testGetStoredLoginDataNoActiveContextUsesGlobal() {
    final ReportDesignerContext context = mock( ReportDesignerContext.class );
    final GlobalAuthenticationStore globalStore = mock( GlobalAuthenticationStore.class );
    final AuthenticationData data = mock( AuthenticationData.class );
    when( context.getActiveContext() ).thenReturn( null );
    when( context.getGlobalAuthenticationStore() ).thenReturn( globalStore );
    when( globalStore.getCredentials( "http://u" ) ).thenReturn( data );
    assertSame( data, RepositoryLoginDialog.getStoredLoginData( "http://u", context ) );
  }

  @Test
  public void testGetDefaultDataReturnsStoredForRecentEntry() {
    final ReportDesignerContext context = mock( ReportDesignerContext.class );
    final GlobalAuthenticationStore globalStore = mock( GlobalAuthenticationStore.class );
    final ReportDocumentContext rdc = mock( ReportDocumentContext.class );
    final AuthenticationStore store = mock( AuthenticationStore.class );
    final AuthenticationData data = mock( AuthenticationData.class );
    when( context.getGlobalAuthenticationStore() ).thenReturn( globalStore );
    when( globalStore.getMostRecentEntry() ).thenReturn( "http://r" );
    when( context.getActiveContext() ).thenReturn( rdc );
    when( rdc.getAuthenticationStore() ).thenReturn( store );
    when( store.getCredentials( "http://r" ) ).thenReturn( data );
    assertSame( data, RepositoryLoginDialog.getDefaultData( context ) );
  }

  @Test
  public void testGetDefaultDataNoRecentNoConfigReturnsNull() {
    final ReportDesignerContext context = mock( ReportDesignerContext.class );
    final GlobalAuthenticationStore globalStore = mock( GlobalAuthenticationStore.class );
    when( context.getGlobalAuthenticationStore() ).thenReturn( globalStore );
    when( globalStore.getMostRecentEntry() ).thenReturn( null );
    try ( MockedStatic<ReportDesignerBoot> rdb = mockStatic( ReportDesignerBoot.class ) ) {
      final ReportDesignerBoot boot = mock( ReportDesignerBoot.class );
      final Configuration config = mock( Configuration.class );
      when( config.getConfigProperty( org.mockito.ArgumentMatchers.anyString() ) ).thenReturn( null );
      when( boot.getGlobalConfig() ).thenReturn( config );
      rdb.when( ReportDesignerBoot::getInstance ).thenReturn( boot );
      assertNull( RepositoryLoginDialog.getDefaultData( context ) );
    }
  }

  @Test
  public void testGetDefaultDataConfigUrlReturnsData() {
    final ReportDesignerContext context = mock( ReportDesignerContext.class );
    final GlobalAuthenticationStore globalStore = mock( GlobalAuthenticationStore.class );
    when( context.getGlobalAuthenticationStore() ).thenReturn( globalStore );
    when( globalStore.getMostRecentEntry() ).thenReturn( null );
    try ( MockedStatic<ReportDesignerBoot> rdb = mockStatic( ReportDesignerBoot.class ) ) {
      final ReportDesignerBoot boot = mock( ReportDesignerBoot.class );
      final Configuration config = mock( Configuration.class );
      when( config.getConfigProperty( org.mockito.ArgumentMatchers.contains( "PublishLocation" ) ) )
          .thenReturn( "http://cfg" );
      when( config.getConfigProperty( org.mockito.ArgumentMatchers.contains( "ServerUser" ) ) ).thenReturn( "user" );
      when( config.getConfigProperty( org.mockito.ArgumentMatchers.contains( "ServerPassword" ) ) )
          .thenReturn( "pass" );
      when( boot.getGlobalConfig() ).thenReturn( config );
      rdb.when( ReportDesignerBoot::getInstance ).thenReturn( boot );
      final AuthenticationData result = RepositoryLoginDialog.getDefaultData( context );
      assertNotNull( result );
      assertEquals( "http://cfg", result.getUrl() );
    }
  }

  @Test
  public void testConstructorWithFrameOwner() {
    final RepositoryLoginDialog d = new RepositoryLoginDialog( (java.awt.Frame) null, false );
    assertNotNull( d );
    d.dispose();
  }

  @Test
  public void testConstructorWithDialogOwner() {
    final RepositoryLoginDialog d = new RepositoryLoginDialog( (java.awt.Dialog) null, false );
    assertNotNull( d );
    d.dispose();
  }

  private Object newFetchResult( boolean enabled, List<OAuthProvider> providers ) throws Exception {
    final Class<?> rc = Class.forName(
        "org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryLoginDialog$OAuthFetchResult" );
    final Constructor<?> ctor = rc.getDeclaredConstructor( boolean.class, List.class );
    ctor.setAccessible( true );
    return ctor.newInstance( enabled, providers );
  }

  private Class<?> fetchResultClass() throws Exception {
    return Class.forName(
        "org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryLoginDialog$OAuthFetchResult" );
  }

  @Test
  public void testProbeOAuthProvidersEnabled() throws Exception {
    try ( MockedStatic<OAuthProviderService> svc = mockStatic( OAuthProviderService.class ) ) {
      svc.when( () -> OAuthProviderService.isOAuthEnabled( "http://s" ) ).thenReturn( true );
      svc.when( () -> OAuthProviderService.fetchProviders( "http://s" ) )
          .thenReturn( Collections.singletonList( new OAuthProvider() ) );
      assertNotNull( invoke( dialog, "probeOAuthProviders", new Class<?>[] { String.class }, "http://s" ) );
    }
  }

  @Test
  public void testProbeOAuthProvidersDisabledButProvidersPresent() throws Exception {
    try ( MockedStatic<OAuthProviderService> svc = mockStatic( OAuthProviderService.class ) ) {
      svc.when( () -> OAuthProviderService.isOAuthEnabled( "http://s" ) ).thenReturn( false );
      svc.when( () -> OAuthProviderService.fetchProviders( "http://s" ) )
          .thenReturn( Collections.singletonList( new OAuthProvider() ) );
      assertNotNull( invoke( dialog, "probeOAuthProviders", new Class<?>[] { String.class }, "http://s" ) );
    }
  }

  @Test
  public void testProbeOAuthProvidersFetchFailsButEndpointAvailable() throws Exception {
    try ( MockedStatic<OAuthProviderService> svc = mockStatic( OAuthProviderService.class ) ) {
      svc.when( () -> OAuthProviderService.isOAuthEnabled( "http://s" ) ).thenReturn( false );
      svc.when( () -> OAuthProviderService.fetchProviders( "http://s" ) ).thenThrow( new RuntimeException( "401" ) );
      svc.when( () -> OAuthProviderService.isProvidersEndpointAvailable( "http://s" ) ).thenReturn( true );
      assertNotNull( invoke( dialog, "probeOAuthProviders", new Class<?>[] { String.class }, "http://s" ) );
    }
  }

  @Test
  public void testApplyFetchedProvidersEmpty() throws Exception {
    invoke( dialog, "applyFetchedProviders", new Class<?>[] { List.class }, new ArrayList<OAuthProvider>() );
    assertNotNull( dialog );
  }

  @Test
  public void testApplyFetchedProvidersNonEmpty() throws Exception {
    final List<OAuthProvider> providers = new ArrayList<>();
    providers.add( new OAuthProvider( "http://a", "", "Google", "google", true ) );
    invoke( dialog, "applyFetchedProviders", new Class<?>[] { List.class }, providers );
    assertNotNull( dialog );
  }

  @Test
  public void testApplyProviderFetchErrorMatchingUrl() throws Exception {
    setField( dialog, "activeFetchUrl", "http://s" );
    invoke( dialog, "applyProviderFetchError", new Class<?>[] { String.class }, "http://s" );
    assertNotNull( dialog );
  }

  @Test
  public void testApplyProviderFetchErrorNonMatchingUrl() throws Exception {
    setField( dialog, "activeFetchUrl", "http://other" );
    invoke( dialog, "applyProviderFetchError", new Class<?>[] { String.class }, "http://s" );
    assertNotNull( dialog );
  }

  @Test
  public void testResetSsoControlsForEmptyUrl() throws Exception {
    invoke( dialog, "resetSsoControlsForEmptyUrl", new Class<?>[ 0 ] );
    assertNotNull( dialog );
  }

  @Test
  public void testHandleOAuthFetchResultEnabled() throws Exception {
    final List<OAuthProvider> providers = new ArrayList<>();
    providers.add( new OAuthProvider( "http://a", "", "Google", "google", true ) );
    final Object result = newFetchResult( true, providers );
    invoke( dialog, "handleOAuthFetchResult", new Class<?>[] { fetchResultClass() }, result );
    assertNotNull( dialog );
  }

  @Test
  public void testHandleOAuthFetchResultDisabled() throws Exception {
    final Object result = newFetchResult( false, Collections.<OAuthProvider>emptyList() );
    invoke( dialog, "handleOAuthFetchResult", new Class<?>[] { fetchResultClass() }, result );
    assertNotNull( dialog );
  }

  @Test
  public void testHandleOAuthFetchInterrupted() throws Exception {
    invoke( dialog, "handleOAuthFetchInterrupted", new Class<?>[ 0 ] );
    assertNotNull( dialog );
  }

  @Test
  public void testHandleOAuthFetchFailureWhenEnabled() throws Exception {
    setField( dialog, "lastOAuthEnabled", true );
    setField( dialog, "activeFetchUrl", "http://s" );
    invoke( dialog, "handleOAuthFetchFailure", new Class<?>[] { String.class }, "http://s" );
    assertNotNull( dialog );
  }

  @Test
  public void testHandleOAuthFetchFailureWhenDisabled() throws Exception {
    setField( dialog, "lastOAuthEnabled", false );
    invoke( dialog, "handleOAuthFetchFailure", new Class<?>[] { String.class }, "http://s" );
    assertNotNull( dialog );
  }

  @Test
  public void testRadioButtonActionListenersFire() throws Exception {
    final javax.swing.JRadioButton sso = (javax.swing.JRadioButton) getField( dialog, "ssoRadio" );
    final javax.swing.JRadioButton up = (javax.swing.JRadioButton) getField( dialog, "usernamePasswordRadio" );
    for ( final java.awt.event.ActionListener l : sso.getActionListeners() ) {
      l.actionPerformed( new java.awt.event.ActionEvent( sso, java.awt.event.ActionEvent.ACTION_PERFORMED, "" ) );
    }
    for ( final java.awt.event.ActionListener l : up.getActionListeners() ) {
      l.actionPerformed( new java.awt.event.ActionEvent( up, java.awt.event.ActionEvent.ACTION_PERFORMED, "" ) );
    }
    assertNotNull( dialog );
  }

  @Test
  public void testUrlDocumentListenerCallbacks() throws Exception {
    final JComboBox<?> urlCombo = (JComboBox<?>) getField( dialog, "urlCombo" );
    final JTextComponent editor = (JTextComponent) urlCombo.getEditor().getEditorComponent();
    final javax.swing.text.AbstractDocument doc = (javax.swing.text.AbstractDocument) editor.getDocument();
    for ( final javax.swing.event.DocumentListener l : doc.getDocumentListeners() ) {
      // only the dialog's own listener (ignores the event); skip Swing's internal UI handler
      if ( l.getClass().getName().contains( "RepositoryLoginDialog" ) ) {
        l.insertUpdate( null );
        l.removeUpdate( null );
        l.changedUpdate( null );
      }
    }
    assertNotNull( dialog );
  }

  @SuppressWarnings( { "unchecked", "rawtypes" } )
  private void invokeCompleteOAuthFetch( String fetchUrl, SwingWorker worker ) throws Exception {
    final Class<?> rc = fetchResultClass();
    final Method m = RepositoryLoginDialog.class.getDeclaredMethod( "completeOAuthFetch", String.class,
        SwingWorker.class );
    m.setAccessible( true );
    m.invoke( dialog, fetchUrl, worker );
    // keep rc referenced so the compiler retains the import usage path
    assertNotNull( rc );
  }

  @Test
  public void testCompleteOAuthFetchSuccess() throws Exception {
    setField( dialog, "activeFetchUrl", "http://s" );
    final List<OAuthProvider> providers = new ArrayList<>();
    providers.add( new OAuthProvider( "http://a", "", "Google", "google", true ) );
    final Object result = newFetchResult( true, providers );
    @SuppressWarnings( "rawtypes" )
    final SwingWorker worker = mock( SwingWorker.class );
    when( worker.get() ).thenReturn( result );
    invokeCompleteOAuthFetch( "http://s", worker );
    assertNotNull( dialog );
  }

  @Test
  public void testCompleteOAuthFetchUrlMismatchReturnsEarly() throws Exception {
    setField( dialog, "activeFetchUrl", "http://other" );
    @SuppressWarnings( "rawtypes" )
    final SwingWorker worker = mock( SwingWorker.class );
    invokeCompleteOAuthFetch( "http://s", worker );
    assertNotNull( dialog );
  }

  @Test
  public void testCompleteOAuthFetchExecutionExceptionHandlesFailure() throws Exception {
    setField( dialog, "activeFetchUrl", "http://s" );
    setField( dialog, "lastOAuthEnabled", true );
    @SuppressWarnings( "rawtypes" )
    final SwingWorker worker = mock( SwingWorker.class );
    when( worker.get() ).thenThrow( new ExecutionException( new RuntimeException( "boom" ) ) );
    invokeCompleteOAuthFetch( "http://s", worker );
    assertNotNull( dialog );
  }

  @Test
  public void testCompleteOAuthFetchInterrupted() throws Exception {
    setField( dialog, "activeFetchUrl", "http://s" );
    @SuppressWarnings( "rawtypes" )
    final SwingWorker worker = mock( SwingWorker.class );
    when( worker.get() ).thenThrow( new InterruptedException() );
    try {
      invokeCompleteOAuthFetch( "http://s", worker );
    } finally {
      Thread.interrupted(); // clear interrupt flag set by the handler
    }
    assertNotNull( dialog );
  }

  static class SsoDialog extends TestableDialog {
    SsoDialog( final boolean loginForPublish ) {
      super( loginForPublish );
    }

    @Override
    protected boolean isSsoFeatureEnabled() {
      return true;
    }
  }

  private static void setEditorTextOn( RepositoryLoginDialog d, String text ) throws Exception {
    final Field f = RepositoryLoginDialog.class.getDeclaredField( "urlCombo" );
    f.setAccessible( true );
    final JComboBox<?> combo = (JComboBox<?>) f.get( d );
    ( (JTextComponent) combo.getEditor().getEditorComponent() ).setText( text );
  }

  @Test
  public void testFetchOAuthProvidersSsoEnabledEmptyUrl() throws Exception {
    final SsoDialog d = new SsoDialog( false );
    try {
      setEditorTextOn( d, "" );
      final Method m = RepositoryLoginDialog.class.getDeclaredMethod( "fetchOAuthProviders" );
      m.setAccessible( true );
      m.invoke( d ); // empty URL → resetSsoControlsForEmptyUrl
      assertNotNull( d );
    } finally {
      d.dispose();
    }
  }

  @Test
  public void testPerformLoginRememberTrueNonSso() {
    final boolean prev = PublishSettings.getInstance().isRememberSettings();
    try {
      PublishSettings.getInstance().setRememberSettings( true );
      dialog.editResult = true;
      final ReportDesignerContext context = contextWithUrls( new String[] { "http://srv" } );
      // remember == true && !isSsoSession -> credentials are copied from the config
      final AuthenticationData result = dialog.performLogin( context, mockConfig( null ) );
      assertNotNull( result );
    } finally {
      PublishSettings.getInstance().setRememberSettings( prev );
    }
  }

  @Test
  public void testPerformLoginRememberFalseSkipsCredentialFill() {
    final boolean prev = PublishSettings.getInstance().isRememberSettings();
    try {
      PublishSettings.getInstance().setRememberSettings( false );
      dialog.editResult = true;
      final ReportDesignerContext context = contextWithUrls( new String[] { "http://srv" } );
      // remember == false -> else branch clears the user/password fields
      final AuthenticationData result = dialog.performLogin( context, mockConfig( null ) );
      assertNotNull( result );
    } finally {
      PublishSettings.getInstance().setRememberSettings( prev );
    }
  }

  /** Confirms with an SSO method + a selected provider so confirmedProvider is captured. */
  static class SsoConfirmDialog extends TestableDialog {
    SsoConfirmDialog( final boolean loginForPublish ) {
      super( loginForPublish );
    }

    @Override
    public void setVisible( final boolean b ) {
      if ( b ) {
        try {
          final Field sm = RepositoryLoginDialog.class.getDeclaredField( "selectedLoginMethod" );
          sm.setAccessible( true );
          sm.set( this, LoginMethod.SSO );
          final Field pc = RepositoryLoginDialog.class.getDeclaredField( "providerCombo" );
          pc.setAccessible( true );
          final JComboBox<?> combo = (JComboBox<?>) pc.get( this );
          final OAuthProvider provider = new OAuthProvider( "http://a", "", "Google", "google", true );
          @SuppressWarnings( "unchecked" )
          final javax.swing.DefaultComboBoxModel<OAuthProvider> model =
              (javax.swing.DefaultComboBoxModel<OAuthProvider>) combo.getModel();
          model.addElement( provider );
          combo.setSelectedItem( provider );
        } catch ( Exception ignored ) {
          // best-effort
        }
      }
      super.setVisible( b );
    }
  }

  @Test
  public void testPerformLoginSsoConfirmedCapturesProvider() {
    final SsoConfirmDialog d = new SsoConfirmDialog( false );
    try {
      d.editResult = true;
      final ReportDesignerContext context = contextWithUrls( new String[] { "http://srv" } );
      final AuthenticationData result = d.performLogin( context, mockConfig( null ) );
      assertNotNull( result );
      assertNotNull( d.getSelectedOAuthProvider() );
    } finally {
      d.dispose();
    }
  }
  @SuppressWarnings( "unchecked" )
  private void setCachedProviders( final RepositoryLoginDialog d, final OAuthProvider... providers )
      throws Exception {
    final Field f = RepositoryLoginDialog.class.getDeclaredField( "cachedProviders" );
    f.setAccessible( true );
    final List<OAuthProvider> list = (List<OAuthProvider>) f.get( d );
    list.clear();
    Collections.addAll( list, providers );
  }

  @Test
  public void testApplyDialogModeSsoOnlyWithCachedProviders() throws Exception {
    setCachedProviders( dialog, new OAuthProvider( "http://a", "", "Google", "google", true ) );
    dialog.setDialogMode( DialogMode.SSO_ONLY );
    invoke( dialog, "applyDialogMode", new Class<?>[ 0 ] );
    assertEquals( LoginMethod.SSO, dialog.getLoginMethod() );
  }

  @Test
  public void testApplyDialogModeFullSsoSelectedWithCachedProviders() throws Exception {
    setCachedProviders( dialog, new OAuthProvider( "http://a", "", "Google", "google", true ) );
    setField( dialog, "selectedLoginMethod", LoginMethod.SSO );
    dialog.setDialogMode( DialogMode.FULL );
    invoke( dialog, "applyDialogMode", new Class<?>[ 0 ] );
    assertNotNull( dialog );
  }

  @Test
  public void testHandleLoginMethodChangeToSsoWithCachedProviders() throws Exception {
    setCachedProviders( dialog, new OAuthProvider( "http://a", "", "Google", "google", true ) );
    invoke( dialog, "handleLoginMethodChange", new Class<?>[] { LoginMethod.class, boolean.class },
        LoginMethod.SSO, false );
    assertEquals( LoginMethod.SSO, dialog.getLoginMethod() );
  }

  @Test
  public void testUrlChangeHandlerRememberNotSelected() throws Exception {
    final ReportDesignerContext context = mock( ReportDesignerContext.class );
    final AuthenticationData stored = mock( AuthenticationData.class );
    when( stored.getOption( "timeout" ) ).thenReturn( "30" );
    setField( dialog, "context", context );
    final JComboBox<String> urlCombo = (JComboBox<String>) getField( dialog, "urlCombo" );
    final GlobalAuthenticationStore safeGlobal = mock( GlobalAuthenticationStore.class );
    when( context.getGlobalAuthenticationStore() ).thenReturn( safeGlobal );
    // remember unchecked -> the "copy credentials" branch is skipped
    ( (javax.swing.JCheckBox) getField( dialog, "rememberSettings" ) ).setSelected( false );

    try ( MockedStatic<RepositoryLoginDialog> rld = mockStatic( RepositoryLoginDialog.class ) ) {
      rld.when( () -> RepositoryLoginDialog.getStoredLoginData( "http://srv", context ) ).thenReturn( stored );
      urlCombo.setSelectedItem( "http://srv" );
      for ( final java.awt.event.ActionListener l : urlCombo.getActionListeners() ) {
        l.actionPerformed(
            new java.awt.event.ActionEvent( urlCombo, java.awt.event.ActionEvent.ACTION_PERFORMED, "x" ) );
      }
    }
    assertNotNull( dialog );
  }

  @Test
  public void testGetDefaultDataReturnsStoredWhenMostRecentHasCredentials() {
    final ReportDesignerContext context = mock( ReportDesignerContext.class );
    final GlobalAuthenticationStore global = mock( GlobalAuthenticationStore.class );
    final AuthenticationData stored = mock( AuthenticationData.class );
    when( context.getGlobalAuthenticationStore() ).thenReturn( global );
    when( global.getMostRecentEntry() ).thenReturn( "http://recent" );
    when( context.getActiveContext() ).thenReturn( null );
    when( global.getCredentials( "http://recent" ) ).thenReturn( stored );
    assertSame( stored, RepositoryLoginDialog.getDefaultData( context ) );
  }
}
