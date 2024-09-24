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
 *  Copyright (c) 2019 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast.html;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class FastHtmlPrinterTest {

  SheetLayout mockSharedSheetLayout;

  FastHtmlPrinter fastHtmlPrinter;
  ResourceManager resourceManager;

  FastHtmlContentItems mockContentItems;

  @Before
  public void setUp() {
    mockSharedSheetLayout = mock( SheetLayout.class );
    mockContentItems = mock( FastHtmlContentItems.class );

    resourceManager = new ResourceManager();
    fastHtmlPrinter = new FastHtmlPrinter( mockSharedSheetLayout, resourceManager, mockContentItems );

    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testShouldCacheStyle() {
    ReportElement reportElement = new Element();
    reportElement.setAttribute( "http://test.namespace.fixed", "fixedValue", "FIXED VALUE" );
    reportElement.setAttribute( "http://test.namespace", "value", "VALUE" );
    assertTrue( fastHtmlPrinter.shouldCacheStyle( reportElement ) );

    reportElement.setAttributeExpression( "http://test.namespace", "irrelevant", mock( Expression.class ) );
    assertTrue( fastHtmlPrinter.shouldCacheStyle( reportElement ) );

    reportElement.setAttributeExpression( "http://test.namespace", "value", mock( Expression.class ) );
    assertFalse( fastHtmlPrinter.shouldCacheStyle( reportElement ) );
  }
}
