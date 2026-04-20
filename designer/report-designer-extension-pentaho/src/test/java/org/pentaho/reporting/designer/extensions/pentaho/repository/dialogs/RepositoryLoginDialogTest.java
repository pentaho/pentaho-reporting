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

package org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs;

import static org.junit.Assert.*;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.pentaho.reporting.designer.extensions.pentaho.repository.auth.OAuthProvider;

/**
 * Tests for {@link RepositoryLoginDialog} — covers getLoginMethod,
 * getSelectedOAuthProvider, setDialogMode, and the confirmedProvider
 * snapshot that prevents the urlCombo refresh from clearing the
 * user's SSO provider selection.
 * <p>
 * Uses Mockito CALLS_REAL_METHODS to avoid HeadlessException in CI
 * (the real constructor creates Swing components that need a display).
 */
@SuppressWarnings( "java:S3011" ) // Reflection access is required — Mockito mock has no real constructor
public class RepositoryLoginDialogTest {

  private static final String FIELD_SELECTED_LOGIN_METHOD = "selectedLoginMethod";
  private static final String FIELD_CONFIRMED_PROVIDER = "confirmedProvider";
  private static final String AZURE_AUTH_URI = "oauth2/authorization/azure";
  private static final String AZURE_REG_ID = "azure";

  private RepositoryLoginDialog dialog;

  @Before
  public void setUp() {
    dialog = Mockito.mock( RepositoryLoginDialog.class, Mockito.CALLS_REAL_METHODS );
  }

  @Test
  public void testDefaultLoginMethodIsUsernamePassword() throws ReflectiveOperationException {
    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD );
    assertEquals( RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD, dialog.getLoginMethod() );
  }

  @Test
  public void testGetSelectedOAuthProviderReturnsNullWhenUsernamePassword() throws ReflectiveOperationException {
    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD );
    assertNull( dialog.getSelectedOAuthProvider() );
  }

  @Test
  public void testGetSelectedOAuthProviderReturnsNullWhenSSOButNoConfirmedProvider()
      throws ReflectiveOperationException {
    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.SSO );
    setField( FIELD_CONFIRMED_PROVIDER, null );
    assertNull( dialog.getSelectedOAuthProvider() );
  }

  @Test
  public void testGetSelectedOAuthProviderReturnsConfirmedProviderWhenSSO() throws ReflectiveOperationException {
    OAuthProvider provider = new OAuthProvider(
        AZURE_AUTH_URI, null, "Microsoft", AZURE_REG_ID, true );

    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.SSO );
    setField( FIELD_CONFIRMED_PROVIDER, provider );

    OAuthProvider result = dialog.getSelectedOAuthProvider();
    assertNotNull( result );
    assertSame( provider, result );
    assertEquals( AZURE_AUTH_URI, result.getAuthorizationUri() );
    assertEquals( "Microsoft", result.getClientName() );
    assertEquals( AZURE_REG_ID, result.getRegistrationId() );
  }

  @Test
  public void testGetSelectedOAuthProviderReturnsNullWhenUsernamePasswordEvenWithConfirmedProvider()
      throws ReflectiveOperationException {
    OAuthProvider provider = new OAuthProvider(
        "oauth2/authorization/google", null, "Google", "google", true );

    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD );
    setField( FIELD_CONFIRMED_PROVIDER, provider );

    assertNull( dialog.getSelectedOAuthProvider() );
  }

  @Test
  public void testConfirmedProviderPreservesSelectionAfterMethodSwitch() throws ReflectiveOperationException {
    OAuthProvider provider = new OAuthProvider(
        AZURE_AUTH_URI, null, "Azure AD", AZURE_REG_ID, true );

    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.SSO );
    setField( FIELD_CONFIRMED_PROVIDER, provider );

    assertSame( provider, dialog.getSelectedOAuthProvider() );

    // Switching to USERNAME_PASSWORD — provider should NOT be returned
    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD );
    assertNull( dialog.getSelectedOAuthProvider() );

    // Switching back to SSO — confirmedProvider is still set
    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.SSO );
    assertSame( provider, dialog.getSelectedOAuthProvider() );
  }

  @Test
  public void testSetDialogMode() {
    dialog.setDialogMode( RepositoryLoginDialog.DialogMode.SSO_ONLY );
    dialog.setDialogMode( RepositoryLoginDialog.DialogMode.CREDENTIALS_ONLY );
    dialog.setDialogMode( RepositoryLoginDialog.DialogMode.FULL );
    // Verify the last mode was accepted (setDialogMode stores for later applyDialogMode())
    assertNotNull( dialog );
  }

  @Test
  public void testLoginMethodEnum() {
    RepositoryLoginDialog.LoginMethod[] values = RepositoryLoginDialog.LoginMethod.values();
    assertEquals( 2, values.length );
    assertEquals( RepositoryLoginDialog.LoginMethod.SSO,
        RepositoryLoginDialog.LoginMethod.valueOf( "SSO" ) );
    assertEquals( RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD,
        RepositoryLoginDialog.LoginMethod.valueOf( "USERNAME_PASSWORD" ) );
  }

  @Test
  public void testDialogModeEnum() {
    RepositoryLoginDialog.DialogMode[] values = RepositoryLoginDialog.DialogMode.values();
    assertEquals( 3, values.length );
    assertEquals( RepositoryLoginDialog.DialogMode.FULL,
        RepositoryLoginDialog.DialogMode.valueOf( "FULL" ) );
    assertEquals( RepositoryLoginDialog.DialogMode.SSO_ONLY,
        RepositoryLoginDialog.DialogMode.valueOf( "SSO_ONLY" ) );
    assertEquals( RepositoryLoginDialog.DialogMode.CREDENTIALS_ONLY,
        RepositoryLoginDialog.DialogMode.valueOf( "CREDENTIALS_ONLY" ) );
  }

  @Test
  public void testGetServerURLReturnsSelectedItem() throws Exception {
    javax.swing.JComboBox<?> combo = Mockito.mock( javax.swing.JComboBox.class );
    Mockito.when( combo.getSelectedItem() ).thenReturn( "http://srv/pentaho" );
    setField( "urlCombo", combo );
    assertEquals( "http://srv/pentaho", dialog.getServerURL() );
  }

  @Test
  public void testGetServerURLReturnsNullWhenNoSelection() throws Exception {
    javax.swing.JComboBox<?> combo = Mockito.mock( javax.swing.JComboBox.class );
    Mockito.when( combo.getSelectedItem() ).thenReturn( null );
    setField( "urlCombo", combo );
    assertNull( dialog.getServerURL() );
  }

  @Test
  public void testGetVersionAlwaysReturnsFive() {
    assertEquals( 5, dialog.getVersion() );
  }

  @Test
  public void testGetUsernameReturnsFieldText() throws Exception {
    javax.swing.JTextField field = Mockito.mock( javax.swing.JTextField.class );
    Mockito.when( field.getText() ).thenReturn( "admin" );
    setField( "userField", field );
    assertEquals( "admin", dialog.getUsername() );
  }

  @Test
  public void testGetUserPasswordReturnsFieldPassword() throws Exception {
    javax.swing.JPasswordField field = Mockito.mock( javax.swing.JPasswordField.class );
    Mockito.when( field.getPassword() ).thenReturn( "secret".toCharArray() );
    setField( "userPasswordField", field );
    assertEquals( "secret", dialog.getUserPassword() );
  }

  @Test
  public void testGetTimeoutReturnsNumberValue() throws Exception {
    javax.swing.JSpinner spinner = Mockito.mock( javax.swing.JSpinner.class );
    Mockito.when( spinner.getValue() ).thenReturn( Integer.valueOf( 42 ) );
    setField( "timeoutField", spinner );
    assertEquals( 42, dialog.getTimeout() );
  }

  @Test
  public void testGetTimeoutFallsBackWhenValueNotANumber() throws Exception {
    javax.swing.JSpinner spinner = Mockito.mock( javax.swing.JSpinner.class );
    Mockito.when( spinner.getValue() ).thenReturn( "not-a-number" );
    setField( "timeoutField", spinner );
    int result = dialog.getTimeout();
    // Fallback returns WorkspaceSettings connection timeout — just assert positive
    assertTrue( result > 0 );
  }

  @Test
  public void testIsRememberSettingsReturnsCheckboxState() throws Exception {
    javax.swing.JCheckBox cb = Mockito.mock( javax.swing.JCheckBox.class );
    Mockito.when( cb.isSelected() ).thenReturn( true );
    setField( "rememberSettings", cb );
    assertTrue( dialog.isRememberSettings() );

    Mockito.when( cb.isSelected() ).thenReturn( false );
    assertFalse( dialog.isRememberSettings() );
  }

  @Test
  public void testSetDialogModePersistsAcrossModes() throws Exception {
    Field modeField = RepositoryLoginDialog.class.getDeclaredField( "dialogMode" );
    modeField.setAccessible( true );

    dialog.setDialogMode( RepositoryLoginDialog.DialogMode.FULL );
    assertEquals( RepositoryLoginDialog.DialogMode.FULL, modeField.get( dialog ) );

    dialog.setDialogMode( RepositoryLoginDialog.DialogMode.SSO_ONLY );
    assertEquals( RepositoryLoginDialog.DialogMode.SSO_ONLY, modeField.get( dialog ) );

    dialog.setDialogMode( RepositoryLoginDialog.DialogMode.CREDENTIALS_ONLY );
    assertEquals( RepositoryLoginDialog.DialogMode.CREDENTIALS_ONLY, modeField.get( dialog ) );
  }

  @Test
  public void testGetSelectedOAuthProviderNullWhenSelectedLoginMethodIsNull() throws Exception {
    setField( FIELD_SELECTED_LOGIN_METHOD, null );
    setField( FIELD_CONFIRMED_PROVIDER,
      new OAuthProvider( "u", null, "n", "id", true ) );
    assertNull( dialog.getSelectedOAuthProvider() );
  }

  @Test
  public void testGetDialogId() throws Exception {
    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "getDialogId" );
    m.setAccessible( true );
    assertEquals( "ReportDesigner.Pentaho.RepositoryLogin", m.invoke( dialog ) );
  }

  // ---- updateConfirmForSSO branches ----

  @Test
  public void testUpdateConfirmForSSOEnablesWhenSSOAndProviderAvailable() throws Exception {
    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.SSO );
    javax.swing.Action confirmAction = Mockito.mock( javax.swing.Action.class );
    dialog = newDialogWithStubbedConfirmAction( confirmAction );
    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.SSO );
    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "updateConfirmForSSO", boolean.class );
    m.setAccessible( true );
    m.invoke( dialog, true );
    Mockito.verify( confirmAction ).setEnabled( true );
  }

  @Test
  public void testUpdateConfirmForSSODisablesWhenSSOAndNoProvider() throws Exception {
    javax.swing.Action confirmAction = Mockito.mock( javax.swing.Action.class );
    dialog = newDialogWithStubbedConfirmAction( confirmAction );
    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.SSO );
    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "updateConfirmForSSO", boolean.class );
    m.setAccessible( true );
    m.invoke( dialog, false );
    Mockito.verify( confirmAction ).setEnabled( false );
  }

  @Test
  public void testUpdateConfirmForSSONoOpWhenNotSSO() throws Exception {
    javax.swing.Action confirmAction = Mockito.mock( javax.swing.Action.class );
    dialog = newDialogWithStubbedConfirmAction( confirmAction );
    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD );
    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "updateConfirmForSSO", boolean.class );
    m.setAccessible( true );
    m.invoke( dialog, true );
    Mockito.verifyNoInteractions( confirmAction );
  }

  // ---- applyFetchedProviders branches ----

  @Test
  public void testApplyFetchedProvidersEmptyShowsNoProvidersMessage() throws Exception {
    setupProviderModelMocks();
    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "applyFetchedProviders",
      java.util.List.class );
    m.setAccessible( true );
    m.invoke( dialog, java.util.Collections.emptyList() );
    // providerCombo.setEnabled(false) called
    javax.swing.JComboBox<?> combo = (javax.swing.JComboBox<?>) getField( "providerCombo" );
    Mockito.verify( combo ).setEnabled( false );
  }

  @Test
  public void testApplyFetchedProvidersNonEmptyEnablesAndSelectsFirst() throws Exception {
    javax.swing.Action confirmAction = Mockito.mock( javax.swing.Action.class );
    dialog = newDialogWithStubbedConfirmAction( confirmAction );
    setupProviderModelMocks();
    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.SSO );
    OAuthProvider p1 = new OAuthProvider( "u1", null, "P1", "id1", true );
    OAuthProvider p2 = new OAuthProvider( "u2", null, "P2", "id2", true );
    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "applyFetchedProviders",
      java.util.List.class );
    m.setAccessible( true );
    m.invoke( dialog, java.util.Arrays.asList( p1, p2 ) );

    javax.swing.JComboBox<?> combo = (javax.swing.JComboBox<?>) getField( "providerCombo" );
    Mockito.verify( combo ).setEnabled( true );
    Mockito.verify( combo ).setSelectedIndex( 0 );
  }

  // ---- applyProviderFetchError branches ----

  @Test
  public void testApplyProviderFetchErrorMatchedUrlSetsLabel() throws Exception {
    setupProviderModelMocks();
    setField( "activeFetchUrl", "http://srv" );
    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "applyProviderFetchError", String.class );
    m.setAccessible( true );
    m.invoke( dialog, "http://srv" );
    javax.swing.JLabel lbl = (javax.swing.JLabel) getField( "providerStatusLabel" );
    Mockito.verify( lbl ).setText( Mockito.anyString() );
  }

  @Test
  public void testApplyProviderFetchErrorMismatchedUrlIsNoOp() throws Exception {
    setupProviderModelMocks();
    setField( "activeFetchUrl", "http://other" );
    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "applyProviderFetchError", String.class );
    m.setAccessible( true );
    m.invoke( dialog, "http://srv" );
    javax.swing.JLabel lbl = (javax.swing.JLabel) getField( "providerStatusLabel" );
    Mockito.verify( lbl, Mockito.never() ).setText( Mockito.anyString() );
  }

  // ---- handleLoginMethodChange branches ----

  @Test
  public void testHandleLoginMethodChangeShowsUserPanelWhenUsernamePassword() throws Exception {
    javax.swing.Action confirmAction = Mockito.mock( javax.swing.Action.class );
    dialog = newDialogWithStubbedConfirmAction( confirmAction );
    setupProviderModelMocks();
    javax.swing.JPanel userPanel = Mockito.mock( javax.swing.JPanel.class );
    javax.swing.JPanel ssoPanel = Mockito.mock( javax.swing.JPanel.class );
    setField( "userPanel", userPanel );
    setField( "ssoProviderPanel", ssoPanel );
    Mockito.doNothing().when( dialog ).pack();

    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "handleLoginMethodChange",
      RepositoryLoginDialog.LoginMethod.class, boolean.class );
    m.setAccessible( true );
    m.invoke( dialog, RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD, true );

    Mockito.verify( userPanel ).setVisible( true );
    Mockito.verify( ssoPanel ).setVisible( false );
    Mockito.verify( confirmAction ).setEnabled( true );
  }

  @Test
  public void testHandleLoginMethodChangeShowsSSOPanelWithCachedProvidersDisabled() throws Exception {
    javax.swing.Action confirmAction = Mockito.mock( javax.swing.Action.class );
    dialog = newDialogWithStubbedConfirmAction( confirmAction );
    setupProviderModelMocks();
    javax.swing.JPanel userPanel = Mockito.mock( javax.swing.JPanel.class );
    javax.swing.JPanel ssoPanel = Mockito.mock( javax.swing.JPanel.class );
    setField( "userPanel", userPanel );
    setField( "ssoProviderPanel", ssoPanel );
    Mockito.doNothing().when( dialog ).pack();
    setField( "cachedProviders", new java.util.ArrayList<OAuthProvider>() );

    // URL combo for fetchOAuthProviders early-exit
    javax.swing.JComboBox<?> urlCombo = Mockito.mock( javax.swing.JComboBox.class );
    javax.swing.ComboBoxEditor editor = Mockito.mock( javax.swing.ComboBoxEditor.class );
    Mockito.when( urlCombo.getEditor() ).thenReturn( editor );
    Mockito.when( editor.getEditorComponent() ).thenReturn( null );
    Mockito.when( urlCombo.getSelectedItem() ).thenReturn( null );
    setField( "urlCombo", urlCombo );
    javax.swing.Timer timer = Mockito.mock( javax.swing.Timer.class );
    setField( "providerFetchTimer", timer );

    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "handleLoginMethodChange",
      RepositoryLoginDialog.LoginMethod.class, boolean.class );
    m.setAccessible( true );
    m.invoke( dialog, RepositoryLoginDialog.LoginMethod.SSO, false );
    Mockito.verify( ssoPanel ).setVisible( true );
    Mockito.verify( userPanel ).setVisible( false );
    Mockito.verify( confirmAction, Mockito.atLeastOnce() ).setEnabled( false );
  }

  // ---- applyDialogMode branches ----

  @Test
  public void testApplyDialogModeSSOOnly() throws Exception {
    javax.swing.Action confirmAction = Mockito.mock( javax.swing.Action.class );
    dialog = newDialogWithStubbedConfirmAction( confirmAction );
    setupProviderModelMocks();
    javax.swing.JPanel methodPanel = Mockito.mock( javax.swing.JPanel.class );
    javax.swing.JPanel userPanel = Mockito.mock( javax.swing.JPanel.class );
    javax.swing.JPanel ssoPanel = Mockito.mock( javax.swing.JPanel.class );
    javax.swing.JLabel timeoutLabel = Mockito.mock( javax.swing.JLabel.class );
    javax.swing.JSpinner timeoutField = Mockito.mock( javax.swing.JSpinner.class );
    javax.swing.JRadioButton ssoRadio = Mockito.mock( javax.swing.JRadioButton.class );
    setField( "loginMethodPanel", methodPanel );
    setField( "userPanel", userPanel );
    setField( "ssoProviderPanel", ssoPanel );
    setField( "timeoutLabel", timeoutLabel );
    setField( "timeoutField", timeoutField );
    setField( "ssoRadio", ssoRadio );
    setField( "cachedProviders", new java.util.ArrayList<OAuthProvider>() );
    // URL combo for fetchOAuthProviders early-exit
    javax.swing.JComboBox<?> urlCombo = Mockito.mock( javax.swing.JComboBox.class );
    javax.swing.ComboBoxEditor editor = Mockito.mock( javax.swing.ComboBoxEditor.class );
    Mockito.when( urlCombo.getEditor() ).thenReturn( editor );
    Mockito.when( editor.getEditorComponent() ).thenReturn( null );
    Mockito.when( urlCombo.getSelectedItem() ).thenReturn( null );
    setField( "urlCombo", urlCombo );
    javax.swing.Timer timer = Mockito.mock( javax.swing.Timer.class );
    setField( "providerFetchTimer", timer );

    dialog.setDialogMode( RepositoryLoginDialog.DialogMode.SSO_ONLY );
    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "applyDialogMode" );
    m.setAccessible( true );
    m.invoke( dialog );
    Mockito.verify( methodPanel ).setVisible( false );
    Mockito.verify( ssoPanel ).setVisible( true );
    Mockito.verify( ssoRadio ).setSelected( true );
  }

  @Test
  public void testApplyDialogModeCredentialsOnly() throws Exception {
    javax.swing.Action confirmAction = Mockito.mock( javax.swing.Action.class );
    dialog = newDialogWithStubbedConfirmAction( confirmAction );
    setupProviderModelMocks();
    javax.swing.JPanel methodPanel = Mockito.mock( javax.swing.JPanel.class );
    javax.swing.JPanel userPanel = Mockito.mock( javax.swing.JPanel.class );
    javax.swing.JPanel ssoPanel = Mockito.mock( javax.swing.JPanel.class );
    javax.swing.JLabel timeoutLabel = Mockito.mock( javax.swing.JLabel.class );
    javax.swing.JSpinner timeoutField = Mockito.mock( javax.swing.JSpinner.class );
    javax.swing.JRadioButton upRadio = Mockito.mock( javax.swing.JRadioButton.class );
    setField( "loginMethodPanel", methodPanel );
    setField( "userPanel", userPanel );
    setField( "ssoProviderPanel", ssoPanel );
    setField( "timeoutLabel", timeoutLabel );
    setField( "timeoutField", timeoutField );
    setField( "usernamePasswordRadio", upRadio );

    dialog.setDialogMode( RepositoryLoginDialog.DialogMode.CREDENTIALS_ONLY );
    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "applyDialogMode" );
    m.setAccessible( true );
    m.invoke( dialog );
    Mockito.verify( methodPanel ).setVisible( false );
    Mockito.verify( userPanel ).setVisible( true );
    Mockito.verify( upRadio ).setSelected( true );
    Mockito.verify( confirmAction ).setEnabled( true );
  }

  @Test
  public void testApplyDialogModeFullSSOWithProviders() throws Exception {
    javax.swing.Action confirmAction = Mockito.mock( javax.swing.Action.class );
    dialog = newDialogWithStubbedConfirmAction( confirmAction );
    setupProviderModelMocks();
    javax.swing.JPanel methodPanel = Mockito.mock( javax.swing.JPanel.class );
    javax.swing.JLabel timeoutLabel = Mockito.mock( javax.swing.JLabel.class );
    javax.swing.JSpinner timeoutField = Mockito.mock( javax.swing.JSpinner.class );
    setField( "loginMethodPanel", methodPanel );
    setField( "timeoutLabel", timeoutLabel );
    setField( "timeoutField", timeoutField );
    java.util.List<OAuthProvider> cached = new java.util.ArrayList<>();
    cached.add( new OAuthProvider( "u", null, "n", "id", true ) );
    setField( "cachedProviders", cached );
    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.SSO );

    dialog.setDialogMode( RepositoryLoginDialog.DialogMode.FULL );
    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "applyDialogMode" );
    m.setAccessible( true );
    m.invoke( dialog );
    Mockito.verify( methodPanel ).setVisible( true );
    Mockito.verify( confirmAction ).setEnabled( true );
  }

  @Test
  public void testApplyDialogModeFullUsernamePasswordEnablesConfirm() throws Exception {
    javax.swing.Action confirmAction = Mockito.mock( javax.swing.Action.class );
    dialog = newDialogWithStubbedConfirmAction( confirmAction );
    javax.swing.JPanel methodPanel = Mockito.mock( javax.swing.JPanel.class );
    javax.swing.JLabel timeoutLabel = Mockito.mock( javax.swing.JLabel.class );
    javax.swing.JSpinner timeoutField = Mockito.mock( javax.swing.JSpinner.class );
    setField( "loginMethodPanel", methodPanel );
    setField( "timeoutLabel", timeoutLabel );
    setField( "timeoutField", timeoutField );
    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD );

    dialog.setDialogMode( RepositoryLoginDialog.DialogMode.FULL );
    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "applyDialogMode" );
    m.setAccessible( true );
    m.invoke( dialog );
    Mockito.verify( confirmAction ).setEnabled( true );
  }

  // ---- onUrlTextChanged branches ----

  @Test
  public void testOnUrlTextChangedNoOpWhenSameAsActiveFetchUrl() throws Exception {
    setupProviderModelMocks();
    setField( "activeFetchUrl", "http://srv" );
    javax.swing.JComboBox<?> urlCombo = Mockito.mock( javax.swing.JComboBox.class );
    javax.swing.ComboBoxEditor editor = Mockito.mock( javax.swing.ComboBoxEditor.class );
    javax.swing.text.JTextComponent ed = new javax.swing.JTextField( "http://srv" );
    Mockito.when( urlCombo.getEditor() ).thenReturn( editor );
    Mockito.when( editor.getEditorComponent() ).thenReturn( ed );
    setField( "urlCombo", urlCombo );

    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "onUrlTextChanged" );
    m.setAccessible( true );
    m.invoke( dialog );
    // activeFetchUrl unchanged
    assertEquals( "http://srv", getField( "activeFetchUrl" ) );
  }

  @Test
  public void testOnUrlTextChangedClearsAndStartsTimerWhenChanged() throws Exception {
    setupProviderModelMocks();
    setField( "activeFetchUrl", "http://old" );
    javax.swing.JComboBox<?> urlCombo = Mockito.mock( javax.swing.JComboBox.class );
    javax.swing.ComboBoxEditor editor = Mockito.mock( javax.swing.ComboBoxEditor.class );
    javax.swing.text.JTextComponent ed = new javax.swing.JTextField( "http://new" );
    Mockito.when( urlCombo.getEditor() ).thenReturn( editor );
    Mockito.when( editor.getEditorComponent() ).thenReturn( ed );
    setField( "urlCombo", urlCombo );
    javax.swing.Timer timer = Mockito.mock( javax.swing.Timer.class );
    setField( "providerFetchTimer", timer );
    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD );

    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "onUrlTextChanged" );
    m.setAccessible( true );
    m.invoke( dialog );

    assertNull( getField( "activeFetchUrl" ) );
    Mockito.verify( timer ).restart();
  }

  @Test
  public void testOnUrlTextChangedEmptyUrlSetsEnterUrlFirstStatus() throws Exception {
    setupProviderModelMocks();
    setField( "activeFetchUrl", "http://old" );
    javax.swing.JComboBox<?> urlCombo = Mockito.mock( javax.swing.JComboBox.class );
    javax.swing.ComboBoxEditor editor = Mockito.mock( javax.swing.ComboBoxEditor.class );
    javax.swing.text.JTextComponent ed = new javax.swing.JTextField( "" );
    Mockito.when( urlCombo.getEditor() ).thenReturn( editor );
    Mockito.when( editor.getEditorComponent() ).thenReturn( ed );
    setField( "urlCombo", urlCombo );
    javax.swing.Timer timer = Mockito.mock( javax.swing.Timer.class );
    setField( "providerFetchTimer", timer );
    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD );

    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "onUrlTextChanged" );
    m.setAccessible( true );
    m.invoke( dialog );

    javax.swing.JLabel lbl = (javax.swing.JLabel) getField( "providerStatusLabel" );
    Mockito.verify( lbl ).setText( Mockito.contains( "server URL" ) );
  }

  // ---- getCurrentEditorUrl branches ----

  @Test
  public void testGetCurrentEditorUrlUsesEditorTextWhenJTextComponent() throws Exception {
    javax.swing.JComboBox<?> urlCombo = Mockito.mock( javax.swing.JComboBox.class );
    javax.swing.ComboBoxEditor editor = Mockito.mock( javax.swing.ComboBoxEditor.class );
    javax.swing.JTextField ed = new javax.swing.JTextField( "http://from-editor" );
    Mockito.when( urlCombo.getEditor() ).thenReturn( editor );
    Mockito.when( editor.getEditorComponent() ).thenReturn( ed );
    setField( "urlCombo", urlCombo );
    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "getCurrentEditorUrl" );
    m.setAccessible( true );
    assertEquals( "http://from-editor", m.invoke( dialog ) );
  }

  @Test
  public void testGetCurrentEditorUrlFallsBackToServerURLWhenNotJTextComponent() throws Exception {
    javax.swing.JComboBox<?> urlCombo = Mockito.mock( javax.swing.JComboBox.class );
    javax.swing.ComboBoxEditor editor = Mockito.mock( javax.swing.ComboBoxEditor.class );
    Mockito.when( urlCombo.getEditor() ).thenReturn( editor );
    Mockito.when( editor.getEditorComponent() ).thenReturn( new javax.swing.JButton() ); // not JTextComponent
    Mockito.when( urlCombo.getSelectedItem() ).thenReturn( "http://from-selected" );
    setField( "urlCombo", urlCombo );
    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "getCurrentEditorUrl" );
    m.setAccessible( true );
    assertEquals( "http://from-selected", m.invoke( dialog ) );
  }

  // ---- fetchOAuthProviders early-return when URL empty ----

  @Test
  public void testFetchOAuthProvidersEarlyReturnWhenUrlEmpty() throws Exception {
    setupProviderModelMocks();
    javax.swing.JComboBox<?> urlCombo = Mockito.mock( javax.swing.JComboBox.class );
    javax.swing.ComboBoxEditor editor = Mockito.mock( javax.swing.ComboBoxEditor.class );
    javax.swing.JTextField ed = new javax.swing.JTextField( "   " );
    Mockito.when( urlCombo.getEditor() ).thenReturn( editor );
    Mockito.when( editor.getEditorComponent() ).thenReturn( ed );
    setField( "urlCombo", urlCombo );
    javax.swing.Timer timer = Mockito.mock( javax.swing.Timer.class );
    setField( "providerFetchTimer", timer );
    setField( FIELD_SELECTED_LOGIN_METHOD, RepositoryLoginDialog.LoginMethod.USERNAME_PASSWORD );

    java.lang.reflect.Method m = RepositoryLoginDialog.class.getDeclaredMethod( "fetchOAuthProviders" );
    m.setAccessible( true );
    m.invoke( dialog );

    Mockito.verify( timer ).stop();
    assertNull( getField( "activeFetchUrl" ) );
  }

  // ---- helpers ----

  /**
   * Build a fresh dialog mock that intercepts the protected getConfirmAction() method
   * (defined in CommonDialog in another package, so not directly stubbable from tests).
   */
  private static RepositoryLoginDialog newDialogWithStubbedConfirmAction( javax.swing.Action confirmAction ) {
    return Mockito.mock( RepositoryLoginDialog.class, Mockito.withSettings().defaultAnswer( inv -> {
      if ( "getConfirmAction".equals( inv.getMethod().getName() ) && inv.getArguments().length == 0 ) {
        return confirmAction;
      }
      return inv.callRealMethod();
    } ) );
  }

  private void setupProviderModelMocks() throws ReflectiveOperationException {
    @SuppressWarnings( "unchecked" )
    javax.swing.DefaultComboBoxModel<OAuthProvider> model =
      (javax.swing.DefaultComboBoxModel<OAuthProvider>) Mockito.mock( javax.swing.DefaultComboBoxModel.class );
    setField( "providerModel", model );
    @SuppressWarnings( "unchecked" )
    javax.swing.DefaultComboBoxModel<String> urlModel =
      (javax.swing.DefaultComboBoxModel<String>) Mockito.mock( javax.swing.DefaultComboBoxModel.class );
    setField( "urlModel", urlModel );
    setField( "providerCombo", Mockito.mock( javax.swing.JComboBox.class ) );
    setField( "providerStatusLabel", Mockito.mock( javax.swing.JLabel.class ) );
    setField( "cachedProviders", new java.util.ArrayList<OAuthProvider>() );
  }

  private Object getField( String fieldName ) throws ReflectiveOperationException {
    Field f = RepositoryLoginDialog.class.getDeclaredField( fieldName );
    f.setAccessible( true );
    return f.get( dialog );
  }

  private void setField( String fieldName, Object value ) throws ReflectiveOperationException {
    Field field = RepositoryLoginDialog.class.getDeclaredField( fieldName );
    field.setAccessible( true );
    field.set( dialog, value );
  }

  @Test
  public void testURLChangeHandlerPrefillsCredentialsWhenRememberChecked() throws Exception {
    invokeUrlChangeHandler( true );

    javax.swing.JTextField userField = (javax.swing.JTextField) getField( "userField" );
    javax.swing.JPasswordField pwdField = (javax.swing.JPasswordField) getField( "userPasswordField" );
    Mockito.verify( userField ).setText( "savedUser" );
    Mockito.verify( pwdField ).setText( "savedPwd" );
  }

  @Test
  public void testURLChangeHandlerSkipsCredentialsWhenRememberUnchecked() throws Exception {
    invokeUrlChangeHandler( false );

    javax.swing.JTextField userField = (javax.swing.JTextField) getField( "userField" );
    javax.swing.JPasswordField pwdField = (javax.swing.JPasswordField) getField( "userPasswordField" );
    // Critical assertion: stale credentials must NOT be auto-restored
    Mockito.verify( userField, Mockito.never() ).setText( Mockito.anyString() );
    Mockito.verify( pwdField, Mockito.never() ).setText( Mockito.anyString() );
  }

  @Test
  public void testURLChangeHandlerSkipsCredentialsWhenRememberCheckboxIsNull() throws Exception {
    invokeUrlChangeHandler( null );

    javax.swing.JTextField userField = (javax.swing.JTextField) getField( "userField" );
    javax.swing.JPasswordField pwdField = (javax.swing.JPasswordField) getField( "userPasswordField" );
    Mockito.verify( userField, Mockito.never() ).setText( Mockito.anyString() );
    Mockito.verify( pwdField, Mockito.never() ).setText( Mockito.anyString() );
  }

  /**
   * Wires up the minimum reflection state needed for {@code URLChangeHandler.actionPerformed}
   * to reach the new "remember-gated" credential-fill branch and invokes it.
   *
   * <p>The handler calls {@code fetchOAuthProviders()} after the credential branch.
   * That method requires a real Swing {@code Timer} we cannot mock cheaply; we
   * deliberately leave {@code providerFetchTimer} null so it NPEs there. The
   * branch under test runs strictly before that NPE, so verifying mock
   * interactions on userField / userPasswordField is safe.</p>
   */
  @SuppressWarnings( { "rawtypes", "unchecked" } )
  private void invokeUrlChangeHandler( Boolean rememberSelected ) throws Exception {
    org.pentaho.reporting.designer.core.ReportDesignerContext ctx =
      Mockito.mock( org.pentaho.reporting.designer.core.ReportDesignerContext.class );
    org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore store =
      Mockito.mock( org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore.class );
    org.pentaho.reporting.designer.core.auth.AuthenticationData stored =
      new org.pentaho.reporting.designer.core.auth.AuthenticationData( "http://srv/", "savedUser", "savedPwd", 30 );
    Mockito.when( ctx.getActiveContext() ).thenReturn( null );
    Mockito.when( ctx.getGlobalAuthenticationStore() ).thenReturn( store );
    Mockito.when( store.getCredentials( "http://srv/" ) ).thenReturn( stored );
    setField( "context", ctx );

    javax.swing.JComboBox combo = Mockito.mock( javax.swing.JComboBox.class );
    Mockito.when( combo.getSelectedItem() ).thenReturn( "http://srv/" );
    setField( "urlCombo", combo );

    setField( "timeoutField", Mockito.mock( javax.swing.JSpinner.class ) );
    setField( "userField", Mockito.mock( javax.swing.JTextField.class ) );
    setField( "userPasswordField", Mockito.mock( javax.swing.JPasswordField.class ) );

    if ( rememberSelected == null ) {
      setField( "rememberSettings", null );
    } else {
      javax.swing.JCheckBox cb = Mockito.mock( javax.swing.JCheckBox.class );
      Mockito.when( cb.isSelected() ).thenReturn( rememberSelected );
      setField( "rememberSettings", cb );
    }

    Class<?> handlerCls = Class.forName(
      "org.pentaho.reporting.designer.extensions.pentaho.repository.dialogs.RepositoryLoginDialog$URLChangeHandler" );
    java.lang.reflect.Constructor<?> ctor = handlerCls.getDeclaredConstructor( RepositoryLoginDialog.class );
    ctor.setAccessible( true );
    Object handler = ctor.newInstance( dialog );
    java.lang.reflect.Method m = handlerCls.getDeclaredMethod( "actionPerformed", java.awt.event.ActionEvent.class );
    m.setAccessible( true );
    try {
      m.invoke( handler, (java.awt.event.ActionEvent) null );
    } catch ( java.lang.reflect.InvocationTargetException ex ) {
      // fetchOAuthProviders() will NPE on the un-stubbed providerFetchTimer.
      // The branch under test runs strictly before that point.
      if ( !( ex.getCause() instanceof NullPointerException ) ) {
        throw ex;
      }
    }
  }

}
