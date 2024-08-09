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
 * Copyright (c) 2016 - 2017 Hitachi Vantara..  All rights reserved.
 */

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
