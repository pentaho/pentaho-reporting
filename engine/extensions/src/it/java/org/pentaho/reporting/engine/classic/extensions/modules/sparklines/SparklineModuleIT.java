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

package org.pentaho.reporting.engine.classic.extensions.modules.sparklines;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ExpressionRegistry;
import org.pentaho.reporting.libraries.base.boot.ModuleInfo;

public class SparklineModuleIT {

  private static final String EXPRESSION_ID =
      "org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineExpression";

  @Before
  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testInitialize() throws Exception {
    SparklineModule module = new SparklineModule();
    module.initialize( null );

    assertThat( module.getDescription(), is( equalTo( "Classes to support Sparkline-Mini-Charts" ) ) );
    assertThat( module.getMajorVersion(), is( equalTo( "3" ) ) );
    assertThat( module.getMinorVersion(), is( equalTo( "5" ) ) );
    assertThat( module.getPatchLevel(), is( equalTo( "0" ) ) );
    assertThat( module.getName(), is( equalTo( "ext-sparklines" ) ) );
    assertThat( module.getProducer(), is( equalTo( "The Pentaho Reporting Project" ) ) );

    ModuleInfo[] requiredModules = module.getRequiredModules();
    assertThat( requiredModules.length, is( equalTo( 1 ) ) );
    ModuleInfo requiredModule = requiredModules[0];
    assertThat( requiredModule.getModuleClass(),
        is( equalTo( "org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule" ) ) );
    assertThat( requiredModule.getMinorVersion(), is( equalTo( "5" ) ) );
    assertThat( requiredModule.getMajorVersion(), is( equalTo( "3" ) ) );
    assertThat( requiredModule.getPatchLevel(), is( equalTo( "0" ) ) );

    assertThat( ElementTypeRegistry.getInstance().getNamespacePrefix( SparklineModule.NAMESPACE ),
        is( equalTo( "sparkline" ) ) );
    assertThat( ElementTypeRegistry.getInstance().isElementTypeRegistered( "bar-sparkline" ), is( equalTo( true ) ) );
    assertThat( ElementTypeRegistry.getInstance().isElementTypeRegistered( "line-sparkline" ), is( equalTo( true ) ) );
    assertThat( ElementTypeRegistry.getInstance().isElementTypeRegistered( "pie-sparkline" ), is( equalTo( true ) ) );

    assertThat( ExpressionRegistry.getInstance().isExpressionRegistered( EXPRESSION_ID ), is( equalTo( true ) ) );
    ExpressionMetaData meta = ExpressionRegistry.getInstance().getExpressionMetaData( EXPRESSION_ID );
    assertThat( meta.getBundleLocation(),
        is( equalTo( "org.pentaho.reporting.engine.classic.extensions.modules.sparklines.SparklineExpressionBundle" ) ) );
    assertThat( meta.getPropertyDescriptions(), is( notNullValue() ) );
    assertThat( meta.getPropertyDescriptions().length, is( equalTo( 17 ) ) );
  }
}
