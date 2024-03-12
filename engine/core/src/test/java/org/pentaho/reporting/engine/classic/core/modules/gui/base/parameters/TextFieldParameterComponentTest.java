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

import org.apache.commons.lang3.StringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.Timeout;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;

import javax.swing.SwingUtilities;
import javax.swing.event.ChangeListener;
import java.awt.Color;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.*;

public class TextFieldParameterComponentTest {

  private static final String ENTRY_NAME = "entry_name";

  private TextFieldParameterComponent comp;
  private ParameterUpdateContext updateContext;

  @Rule
  public Timeout globalTimeout = new Timeout( 10000 );


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

    comp = new TextFieldParameterComponent( entry, parameterContext, updateContext );

    verify( updateContext ).addChangeListener( any( ChangeListener.class ) );
    assertEquals( comp.getColumns(), 60 );
  }

  @Test
  public void testInitializeNullValue() throws Exception {
    doReturn( null ).when( updateContext ).getParameterValue( ENTRY_NAME );

    comp.initialize();

    assertEquals( comp.getText(), StringUtils.EMPTY );
    assertEquals( comp.getBackground(), Color.WHITE );
  }

  @Test
  public void testInitializeFormattedValue() throws Exception {
    doReturn( 512000.8978 ).when( updateContext ).getParameterValue( ENTRY_NAME );

    comp.initialize();

    assertEquals( comp.getText(), "512,000.90" );
    assertEquals( comp.getBackground(), Color.WHITE );
  }

  @Test
  public void testInitializeErrorFormattedValue() throws Exception {
    final CountDownLatch latch = new CountDownLatch( 1 );
    doReturn( "error value" ).when( updateContext ).getParameterValue( ENTRY_NAME );

    // initialize() will change the document, which invokes "handler", which uses "SwingUtils.invokeLater(..)" to
    // do it work. The logic in init() depends on being able to finish configuring the text-component before
    // the handler kicks in. If init() is not called from within the AWT-EDT, then we get a race condition.
    SwingUtilities.invokeLater( () -> {
      // this schedules tasks via "runLater(..)"
      comp.initialize();

      // Let all scheduled tasks run first, then signal that the test is finished.
      SwingUtilities.invokeLater( () -> latch.countDown() );
    } );

    latch.await();

    assertEquals( comp.getText(), "error value" );
    assertEquals( comp.getBackground(), Color.RED ); // should set value from TextComponentEditHandler
  }
}
