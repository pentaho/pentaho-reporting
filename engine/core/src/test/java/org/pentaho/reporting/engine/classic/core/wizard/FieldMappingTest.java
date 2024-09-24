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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.wizard;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.designtime.DefaultDesignTimeContext;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;

public class FieldMappingTest extends TestCase {
  public FieldMappingTest() {
  }

  public FieldMappingTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testParameterMapping() {
    final PlainParameter parameter = new PlainParameter( "P", Number.class );
    final DefaultParameterDefinition defaultParameterDefinition = new DefaultParameterDefinition();
    defaultParameterDefinition.addParameterDefinition( parameter );

    final MasterReport report = new MasterReport();
    report.setParameterDefinition( defaultParameterDefinition );

    final DefaultDesignTimeContext context = new DefaultDesignTimeContext( report );
    final DataSchema dataSchema = context.getDataSchemaModel().getDataSchema();
    final DataAttributes attributes = dataSchema.getAttributes( "P" );
    final Object o =
        attributes.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.TYPE, Class.class,
            new DefaultDataAttributeContext() );
    assertEquals( "Number.class expected", Number.class, o );
  }
}
