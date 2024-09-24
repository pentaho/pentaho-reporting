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

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd;

import junit.framework.Assert;
import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.DataFactoryContext;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SimplePmdDataFactoryTest extends TestCase {

  public void testGetContextKeyParentIdentifier() throws ReportDataFactoryException {
    SimplePmdDataFactory spmd = new SimplePmdDataFactory();
    ResourceKey rk = new ResourceKey( new Object(), "identifierRK1", null );
    DataFactoryContext ctx = mock( DataFactoryContext.class );
    ResourceBundleFactory rsf = mock( ResourceBundleFactory.class );
    when( ctx.getContextKey() ).thenReturn( rk );
    when( ctx.getResourceBundleFactory() ).thenReturn( rsf );

    HierarchicalConfiguration conf = mock ( HierarchicalConfiguration.class );
    conf.setConfigProperty( "org.pentaho.reporting.engine.classic.extensions.modules.pmd-datafactory.UseParentIdentifier", "false" );
    when ( ctx.getConfiguration() ).thenReturn( (Configuration) conf );

    spmd.initialize( ctx );

    Assert.assertEquals( "identifierRK1", spmd.getContextKeyParentIdentifier() );
    ResourceKey rk2 = new ResourceKey( new Object(), "identifierRK2", null );

    rk = new ResourceKey( rk2, new Object(), "identifierRK1", null );
    when( ctx.getContextKey() ).thenReturn( rk );
    spmd.initialize( ctx );

    Assert.assertEquals( "identifierRK2", spmd.getContextKeyParentIdentifier() );
  }
}
