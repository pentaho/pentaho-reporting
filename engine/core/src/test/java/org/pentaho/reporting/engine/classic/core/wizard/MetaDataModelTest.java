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
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.util.TypedMetaTableModel;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.TableModel;
import java.math.BigDecimal;
import java.util.Date;

public class MetaDataModelTest extends TestCase {
  public MetaDataModelTest() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  private TableModel queryData() throws ReportDataFactoryException {
    String[] columnNames = new String[4];
    Class[] columnTypes = new Class[4];
    columnNames[0] = "number";
    columnTypes[0] = BigDecimal.class;
    columnNames[1] = "string";
    columnTypes[1] = String.class;
    columnNames[2] = "boolean";
    columnTypes[2] = Boolean.class;
    columnNames[3] = "date";
    columnTypes[3] = Date.class;

    final TypedMetaTableModel metaTableModel = new TypedMetaTableModel( columnNames, columnTypes );
    for ( int idx = 0; idx < 4; idx += 1 ) {
      metaTableModel.setColumnAttribute( idx, MetaAttributeNames.Formatting.NAMESPACE,
          MetaAttributeNames.Formatting.LABEL, "Label " + columnNames[idx] );

      // these don't seem to get picked up
      metaTableModel.setColumnAttribute( idx, MetaAttributeNames.Formatting.NAMESPACE,
          MetaAttributeNames.Formatting.FORMAT, "#,###.00" );

      metaTableModel.setColumnAttribute( idx, MetaAttributeNames.Style.NAMESPACE,
          MetaAttributeNames.Style.HORIZONTAL_ALIGNMENT, ElementAlignment.CENTER );

      metaTableModel.setColumnAttribute( idx, MetaAttributeNames.Style.NAMESPACE, MetaAttributeNames.Style.ITALIC,
          Boolean.TRUE );
    }
    return metaTableModel;
  }

  public void testCode() throws ReportDataFactoryException {
    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();

    final DataSchemaDefinition schemaDef = DataSchemaUtility.parseDefaults( mgr );
    final DefaultDataAttributeContext context = new DefaultDataAttributeContext();
    final DataSchemaCompiler compiler = new DataSchemaCompiler( schemaDef, context, mgr );

    final DataSchema schema = compiler.compile( queryData() );
    final DataAttributes number = schema.getAttributes( "number" );
    assertEquals( "Label number", number.getMetaAttribute( MetaAttributeNames.Formatting.NAMESPACE,
        MetaAttributeNames.Formatting.LABEL, String.class, context ) );
    assertEquals( "number", number.getMetaAttribute( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.NAME,
        String.class, context ) );
    assertEquals( Boolean.TRUE, number.getMetaAttribute( MetaAttributeNames.Style.NAMESPACE,
        MetaAttributeNames.Style.ITALIC, Boolean.class, context ) );
  }
}
