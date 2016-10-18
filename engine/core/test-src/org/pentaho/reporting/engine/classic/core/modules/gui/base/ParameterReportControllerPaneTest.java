/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2016 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import org.pentaho.reporting.engine.classic.core.parameters.AbstractParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;

/**
 * Created by dima.prokopenko@gmail.com on 9/20/2016.
 */
public class ParameterReportControllerPaneTest {

  private AbstractParameter entry = new PlainParameter( "junit_parameter" );
  private ParameterReportControllerPane pane = new ParameterReportControllerPane();

  @Before
  public void before() {
    entry.setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.LABEL, "labelValue" );
  }

  @Test
  public void computeSwingLabelTest() {
    entry.setParameterAttribute( ParameterAttributeNames.Swing.NAMESPACE,
      ParameterAttributeNames.Swing.LABEL, "swingName" );
    String label = pane.computeLabel( entry );
    assertNotNull( label );
    assertEquals( "swingName", label );
  }

  @Test
  public void computeLabelTest() {
    String label = pane.computeLabel( entry );
    assertNotNull( label );
    assertEquals( "labelValue", label );
  }

  @Test
  public void computeTranslatedTest() {
    AbstractParameter entry = mock( AbstractParameter.class );

    when( entry.getParameterAttribute(
      anyString(),
      eq( ParameterAttributeNames.Swing.LABEL ),
      any()
    ) ).thenReturn( null );

    when( entry.getTranslatedParameterAttribute(
      anyString(),
      eq( ParameterAttributeNames.Core.LABEL ),
      any()
    ) ).thenReturn( "label" );

    String label = pane.computeLabel( entry );

    assertNotNull( label );
    assertEquals( "label", label );
  }
}
