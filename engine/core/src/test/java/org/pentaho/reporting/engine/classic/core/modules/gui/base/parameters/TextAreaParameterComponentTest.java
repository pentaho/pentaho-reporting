/*
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
 * Copyright (c) 2000 - 2024 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import java.awt.Color;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import org.apache.commons.lang3.StringUtils;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.ParameterReportControllerPane;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;

public class TextAreaParameterComponentTest {

  private static final String ENTRY_NAME = "entry_name";

  private TextAreaParameterComponent comp;
  private ParameterUpdateContext updateContext;

  @Before
  public void setUp() {
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );
    ParameterContext parameterContext = mock( ParameterContext.class );
    updateContext = mock( ParameterUpdateContext.class );
    ResourceBundleFactory resourceBundleFactory = mock( ResourceBundleFactory.class );
    Locale locale = new Locale( "test_test" );

    doReturn( ENTRY_NAME ).when( entry ).getName();
    doReturn( Number.class ).when( entry ).getValueType();
    doReturn( "#,###,##0.00" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.DATA_FORMAT, parameterContext );
    doReturn( "#,###,##0.00" ).when( entry ).getTranslatedParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.DATA_FORMAT, parameterContext );
    doReturn( "utc" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.TIMEZONE, parameterContext );

    doReturn( resourceBundleFactory ).when( parameterContext ).getResourceBundleFactory();
    doReturn( locale ).when( resourceBundleFactory ).getLocale();
    doReturn( TimeZone.getDefault() ).when( resourceBundleFactory ).getTimeZone();

    comp = new TextAreaParameterComponent( entry, parameterContext, updateContext );

    verify( updateContext ).addChangeListener( any( ChangeListener.class ) );
    JTextArea textArea = findTextArea( comp );
    assertThat( textArea, is( notNullValue() ) );
    assertThat( textArea.getColumns(), is( equalTo( 60 ) ) );
    assertThat( textArea.getRows(), is( equalTo( 10 ) ) );
  }

  @Test
  public void testInitializeNullValue() throws Exception {
    doReturn( null ).when( updateContext ).getParameterValue( ENTRY_NAME );

    comp.initialize();

    JTextArea textArea = findTextArea( comp );
    assertThat( textArea.getText(), is( equalTo( StringUtils.EMPTY ) ) );
    assertThat( comp.getBackground(), is( not( equalTo( ParameterReportControllerPane.ERROR_COLOR ) ) ) );
  }

  @Test
  public void testInitializeFormattedValue() throws Exception {
    doReturn( 512000.8978 ).when( updateContext ).getParameterValue( ENTRY_NAME );

    comp.initialize();

    JTextArea textArea = findTextArea( comp );
    assertThat( textArea, is( notNullValue() ) );
    assertThat( textArea.getText(), is( equalTo( "512,000.90" ) ) );
    assertThat( comp.getBackground(), is( not( equalTo( ParameterReportControllerPane.ERROR_COLOR ) ) ) );
  }

  @Test
  public void testInitializeErrorFormattedValue() throws Exception {
    final CountDownLatch latch = new CountDownLatch( 1 );
    doReturn( "error value" ).when( updateContext ).getParameterValue( ENTRY_NAME );

    comp.initialize();

    SwingUtilities.invokeLater( () -> latch.countDown() );

    latch.await( 100, TimeUnit.MILLISECONDS );

    JTextArea textArea = findTextArea( comp );
    assertThat( textArea, is( notNullValue() ) );
    assertThat( textArea.getText(), is( equalTo( "error value" ) ) );
    assertThat( textArea.getBackground(), is( equalTo( Color.RED ) ) );
    assertThat( comp.getBackground(), is( equalTo( ParameterReportControllerPane.ERROR_COLOR ) ) );
  }

  private JTextArea findTextArea( TextAreaParameterComponent comp ) {
    JTextArea result = null;
    if ( comp.getComponentCount() > 0 ) {
      for ( int i = 0; i < comp.getComponentCount(); i++ ) {
        if ( comp.getComponent( i ) instanceof JViewport ) {
          JViewport viewport = (JViewport) comp.getComponent( i );
          result = (JTextArea) viewport.getView();
        }
      }
    }
    return result;
  }
}
