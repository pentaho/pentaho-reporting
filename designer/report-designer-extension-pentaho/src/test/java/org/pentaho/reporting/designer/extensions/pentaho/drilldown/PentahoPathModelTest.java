/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.auth.AuthenticationData;
import org.pentaho.reporting.designer.core.auth.GlobalAuthenticationStore;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;

public class PentahoPathModelTest {

  private static final String SERVER_PATH = "http://PathToMyServer.com/";

  ReportDesignerContext reportDesignerContext;
  GlobalAuthenticationStore authStore;
  AuthenticationData authData;

  @Before
  public void setUp() throws Exception {
    reportDesignerContext = mock( ReportDesignerContext.class );
    authData = mock( AuthenticationData.class );
    authStore = mock( GlobalAuthenticationStore.class );
  }

  @Test
  public void testPentahoPathModel() {
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
  }

  @Test
  public void testRegisterGetExtension() {
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    model.registerExtension( "pdf", true, true, "adobePDF" );
    assertTrue( model.getExtensions()[0].equals( "pdf" ) );
    model.registerExtension( "xls", false, false, "MSExcel" );
    assertTrue( Arrays.asList( model.getExtensions() ).contains( "xls" ) );
    assertTrue( Arrays.asList( model.getExtensions() ).contains( "pdf" ) );
  }

  @Test
  public void testSetGetLoginData() {
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    model.setLoginData( authData );
    assertEquals( authData, model.getLoginData() );
  }

  @Test
  public void testGetServerPath() {
    doReturn( SERVER_PATH ).when( authData ).getUrl();
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    model.setLoginData( authData );
    assertTrue( SERVER_PATH.equals( model.getServerPath() ) );
  }

  @Test
  public void testSetServerPath() {
    doReturn( authStore ).when( reportDesignerContext ).getGlobalAuthenticationStore();
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    assertNull( model.getServerPath() );
    model.setServerPath( SERVER_PATH );
    assertTrue( SERVER_PATH.equals( model.getServerPath() ) );
  }

  @Test
  public void testSetIsUseRemoteServer() {
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    assertFalse( model.isUseRemoteServer() );
    model.setUseRemoteServer( true );
    assertTrue( model.isUseRemoteServer() );
  }

  @Test
  public void testSetIsHideParameterUi() {
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    assertFalse( model.isHideParameterUi() );
    model.setHideParameterUi( true );
    assertTrue( model.isHideParameterUi() );
  }

  @Test
  public void testGetSetLocalPath() {
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    assertNull( model.getLocalPath() );
    model.setLocalPath( SERVER_PATH );
    assertTrue( SERVER_PATH.equals( model.getLocalPath() ) );
  }

  @Test
  public void testSetLocalPathFromParameter() {
    DrillDownParameter drillDownParameterSolution = new DrillDownParameter( "solution" );
    DrillDownParameter drillDownParameterPath = new DrillDownParameter( "path" );
    DrillDownParameter drillDownParameterName = new DrillDownParameter( "name" );

    drillDownParameterSolution.setFormulaFragment( "mySolution" );
    drillDownParameterPath.setFormulaFragment( "path/to/my/file" );
    drillDownParameterName.setFormulaFragment( "myFileName" );

    DrillDownParameter[] parameters = { drillDownParameterSolution, drillDownParameterPath, drillDownParameterName };
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    model.setLocalPathFromParameter( parameters );
    assertNull( model.getLocalPath() );
  }

  @Test
  public void testGetPath() {
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    model.setLocalPath( "MySolution/path/to/my/file/myFile.txt" );
    assertTrue( "path/to/my/file".equals( model.getPath() ) );
  }

  @Test
  public void testGetName() {
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    model.setLocalPath( "MySolution/path/to/my/file/myFile.txt" );
    assertTrue( "myFile.txt".equals( model.getName() ) );
  }

  @Test
  public void testGetSolution() {
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    model.setLocalPath( "MySolution/path/to/my/file/myFile.txt" );
    assertTrue( "MySolution".equals( model.getSolution() ) );
  }

  @Test
  public void testAddPropertyChangeListenerStringPropertyChangeListener() {
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    PropertyChangeListener listener = new PropertyChangeListener() {

      @Override
      public void propertyChange( PropertyChangeEvent evt ) {
      }
    };
    model.addPropertyChangeListener( "propertyName", listener );
    assertEquals( model.getChangeListeners().getPropertyChangeListeners().length, 1 );
  }

  @Test
  public void testRemovePropertyChangeListenerStringPropertyChangeListener() {
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    PropertyChangeListener listener = new PropertyChangeListener() {

      @Override
      public void propertyChange( PropertyChangeEvent evt ) {
      }
    };
    model.addPropertyChangeListener( "propertyName", listener );
    assertEquals( model.getChangeListeners().getPropertyChangeListeners().length, 1 );
    model.removePropertyChangeListener( "propertyName", listener );
    assertEquals( model.getChangeListeners().getPropertyChangeListeners().length, 0 );
  }

  @Test
  public void testAddPropertyChangeListenerPropertyChangeListener() {
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    PropertyChangeListener listener = new PropertyChangeListener() {

      @Override
      public void propertyChange( PropertyChangeEvent evt ) {
      }
    };
    model.addPropertyChangeListener( listener );
    assertEquals( model.getChangeListeners().getPropertyChangeListeners().length, 1 );
  }

  @Test
  public void testRemovePropertyChangeListenerPropertyChangeListener() {
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    PropertyChangeListener listener = new PropertyChangeListener() {

      @Override
      public void propertyChange( PropertyChangeEvent evt ) {
      }
    };
    model.addPropertyChangeListener( listener );
    assertEquals( model.getChangeListeners().getPropertyChangeListeners().length, 1 );
    model.removePropertyChangeListener( listener );
    assertEquals( model.getChangeListeners().getPropertyChangeListeners().length, 0 );
  }

  @Test
  public void testGetDrillDownProfile() {
    PentahoPathModel model = new PentahoPathModel( reportDesignerContext );
    assertNotNull( model );
    model.setLocalPath( "MySolution/path/to/my/file/myFile.txt" );
    model.registerExtension( ".txt", true, true, "text" );
    String profile = model.getDrillDownProfile();
  }

}
