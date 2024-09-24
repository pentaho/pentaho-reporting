/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
