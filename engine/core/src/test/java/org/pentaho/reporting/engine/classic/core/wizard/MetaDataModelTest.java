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
