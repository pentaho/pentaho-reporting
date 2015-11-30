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
 * Copyright (c) 2000 - 2015 Pentaho Corporation, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.awt.Color;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.modules.gui.base.ParameterReportControllerPane;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;

public class TextFieldParameterComponentTest {

  private static final String ENTRY_NAME = "entry_name";

  private TextFieldParameterComponent comp;
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
    doReturn( "utc" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TIMEZONE, parameterContext );

    doReturn( resourceBundleFactory ).when( parameterContext ).getResourceBundleFactory();
    doReturn( locale ).when( resourceBundleFactory ).getLocale();
    doReturn( TimeZone.getDefault() ).when( resourceBundleFactory ).getTimeZone();

    comp = new TextFieldParameterComponent( entry, parameterContext, updateContext );

    verify( updateContext ).addChangeListener( any( ChangeListener.class ) );
    assertThat( comp.getColumns(), is( equalTo( 60 ) ) );
  }

  @Test
  public void testInitializeNullValue() throws Exception {
    doReturn( null ).when( updateContext ).getParameterValue( ENTRY_NAME );

    comp.initialize();

    assertThat( comp.getText(), is( equalTo( StringUtils.EMPTY ) ) );
    assertThat( comp.getBackground(), is( equalTo( Color.WHITE ) ) );
  }

  @Test
  public void testInitializeFormattedValue() throws Exception {
    doReturn( 512000.8978 ).when( updateContext ).getParameterValue( ENTRY_NAME );

    comp.initialize();

    assertThat( comp.getText(), is( equalTo( "512,000.90" ) ) );
    assertThat( comp.getBackground(), is( equalTo( Color.WHITE ) ) );
  }

  @Test
  public void testInitializeErrorFormattedValue() throws Exception {
    final CountDownLatch latch = new CountDownLatch( 1 );
    doReturn( "error value" ).when( updateContext ).getParameterValue( ENTRY_NAME );

    // initialize() will change the document, which invokes "handler", which uses "SwingUtils.invokeLater(..)" to
    // do it work. The logic in init() depends on being able to finish configuring the text-component before
    // the handler kicks in. If init() is not called from within the AWT-EDT, then we get a race condition.
    SwingUtilities.invokeLater( new Runnable() {

      @Override
      public void run() {
        // this schedules tasks via "runLater(..)"
        comp.initialize();

        // Let all scheduled tasks run first, then signal that the test is finished.
        SwingUtilities.invokeLater( new Runnable() {

          @Override
          public void run() {
            latch.countDown();
          }
        } );
      }
    } );

    latch.await( 100, TimeUnit.MILLISECONDS );

    assertThat( comp.getText(), is( equalTo( "error value" ) ) );
    assertThat( comp.getBackground(), is( equalTo( Color.RED ) ) ); // should set value from TextComponentEditHandler
  }
}
