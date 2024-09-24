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

package org.pentaho.reporting.engine.classic.core.style;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.style.resolver.SimpleStyleResolver;

public class SimpleStyleResolverTest extends TestCase {
  public SimpleStyleResolverTest() {
  }

  public SimpleStyleResolverTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testStyleInheritance() {
    MasterReport report = new MasterReport();
    final ItemBand itemBand = report.getItemBand();

    report.getStyle().setStyleProperty( TextStyleKeys.FONT, "Dudadu" );

    ResolverStyleSheet styleSheet = new ResolverStyleSheet();
    new SimpleStyleResolver( true ).resolve( itemBand, styleSheet );
    Assert.assertEquals( "Dudadu", styleSheet.getStyleProperty( TextStyleKeys.FONT ) );

  }
}
