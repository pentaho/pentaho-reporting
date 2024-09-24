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
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.MetaTableModel;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.cache.IndexedTableModel;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.DefaultTableModel;

public class DataSchemaCompilerTest extends TestCase {
  private static class TestTableModel extends DefaultTableModel implements MetaTableModel {
    private DefaultDataAttributes cellAttributes;
    private DefaultDataAttributes columnAttributes;
    private DefaultDataAttributes tableAttributes;

    private TestTableModel() {
      cellAttributes = new DefaultDataAttributes();
      columnAttributes = new DefaultDataAttributes();
      tableAttributes = new DefaultDataAttributes();

      setColumnIdentifiers( new String[] { "A", "B" } );
      setRowCount( 1 );
      // no values needed for rule-evaluation ..
    }

    public DefaultDataAttributes getCellAttributes() {
      return cellAttributes;
    }

    public DefaultDataAttributes getColumnAttributes() {
      return columnAttributes;
    }

    public DataAttributes getCellDataAttributes( int row, int column ) {
      return cellAttributes;
    }

    public boolean isCellDataAttributesSupported() {
      return cellAttributes.isEmpty() == false;
    }

    public DataAttributes getColumnAttributes( int column ) {
      return columnAttributes;
    }

    public DefaultDataAttributes getTableAttrs() {
      return tableAttributes;
    }

    public DataAttributes getTableAttributes() {
      return tableAttributes;
    }
  }

  public DataSchemaCompilerTest() {
  }

  public DataSchemaCompilerTest( String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testEmptyRuleEvaluation() throws ReportDataFactoryException {
    final DefaultDataSchemaDefinition def = new DefaultDataSchemaDefinition();

    final DataSchemaCompiler compiler = new DataSchemaCompiler( def, new DefaultDataAttributeContext() );
    final TestTableModel data = new TestTableModel();
    data.getTableAttrs().setMetaAttribute( "test", "test1", DefaultConceptQueryMapper.INSTANCE, "test" );
    data.getColumnAttributes().setMetaAttribute( "test", "test2", DefaultConceptQueryMapper.INSTANCE, "test" );
    // Cell attributes will not be copied into the schema, as they can be different for each cell.

    final DataSchema dataSchema = compiler.compile( data );
    final DataAttributes attributes = dataSchema.getAttributes( "A" );
    assertEquals( "test", attributes.getMetaAttribute( "test", "test1", null, new DefaultDataAttributeContext() ) );
    assertEquals( "test", attributes.getMetaAttribute( "test", "test2", null, new DefaultDataAttributeContext() ) );

  }

  public void testGlobalRuleEvaluation() throws ReportDataFactoryException {
    final DefaultDataAttributes global = new DefaultDataAttributes();
    global.setMetaAttribute( "global", "global-test", DefaultConceptQueryMapper.INSTANCE, "global-test" );

    final DefaultDataAttributeReferences refs = new DefaultDataAttributeReferences();
    refs.setReference( "global", "global-ref", new StaticDataAttributeReference( "test", "test1" ) );

    final DefaultDataSchemaDefinition def = new DefaultDataSchemaDefinition();
    def.addRule( new GlobalRule( global, refs ) );
    final DataSchemaCompiler compiler = new DataSchemaCompiler( def, new DefaultDataAttributeContext() );
    final TestTableModel data = new TestTableModel();
    data.getTableAttrs().setMetaAttribute( "test", "test1", DefaultConceptQueryMapper.INSTANCE, "test" );
    data.getColumnAttributes().setMetaAttribute( "test", "test2", DefaultConceptQueryMapper.INSTANCE, "test" );
    // Cell attributes will not be copied into the schema, as they can be different for each cell.

    final DataSchema dataSchema = compiler.compile( data );
    final DataAttributes attributes = dataSchema.getAttributes( "A" );
    assertEquals( "global-test", attributes.getMetaAttribute( "global", "global-test", null,
        new DefaultDataAttributeContext() ) );
    assertEquals( "test", attributes.getMetaAttribute( "test", "test1", null, new DefaultDataAttributeContext() ) );
    assertEquals( "test", attributes.getMetaAttribute( "global", "global-ref", null, new DefaultDataAttributeContext() ) );
  }

  public void testDirectFieldRules() throws ReportDataFactoryException {
    final DefaultDataAttributes global = new DefaultDataAttributes();
    global.setMetaAttribute( "global", "global-test", DefaultConceptQueryMapper.INSTANCE, "global-test" );

    final DefaultDataAttributeReferences refs = new DefaultDataAttributeReferences();
    refs.setReference( "global", "global-ref", new StaticDataAttributeReference( "test", "test1" ) );

    final DefaultDataSchemaDefinition def = new DefaultDataSchemaDefinition();
    def.addRule( new DirectFieldSelectorRule( "A", global, refs ) );
    final DataSchemaCompiler compiler = new DataSchemaCompiler( def, new DefaultDataAttributeContext() );
    final TestTableModel data = new TestTableModel();
    data.getTableAttrs().setMetaAttribute( "test", "test1", DefaultConceptQueryMapper.INSTANCE, "test" );
    data.getColumnAttributes().setMetaAttribute( "test", "test2", DefaultConceptQueryMapper.INSTANCE, "test" );
    // Cell attributes will not be copied into the schema, as they can be different for each cell.

    final DataSchema dataSchema = compiler.compile( data );
    DataAttributes attributes = dataSchema.getAttributes( "A" );
    assertEquals( "global-test", attributes.getMetaAttribute( "global", "global-test", null,
        new DefaultDataAttributeContext() ) );
    assertEquals( "test", attributes.getMetaAttribute( "test", "test1", null, new DefaultDataAttributeContext() ) );
    assertEquals( "test", attributes.getMetaAttribute( "global", "global-ref", null, new DefaultDataAttributeContext() ) );

    attributes = dataSchema.getAttributes( "B" );
    assertNotSame( "global-test", attributes.getMetaAttribute( "global", "global-test", null,
        new DefaultDataAttributeContext() ) );
    assertEquals( "test", attributes.getMetaAttribute( "test", "test1", null, new DefaultDataAttributeContext() ) );
    assertNotSame( "test", attributes
        .getMetaAttribute( "global", "global-ref", null, new DefaultDataAttributeContext() ) );
  }

  public void testMetaSelectorRules() throws ReportDataFactoryException {
    final DefaultDataAttributes global = new DefaultDataAttributes();
    global.setMetaAttribute( "global", "global-test", DefaultConceptQueryMapper.INSTANCE, "global-test" );

    final DefaultDataAttributeReferences refs = new DefaultDataAttributeReferences();
    refs.setReference( "global", "global-ref", new StaticDataAttributeReference( "test", "test1" ) );

    final MetaSelector[] selectors = new MetaSelector[1];
    selectors[0] = new MetaSelector( "test", "test1", "test" );

    final DefaultDataSchemaDefinition def = new DefaultDataSchemaDefinition();
    def.addRule( new MetaSelectorRule( selectors, global, refs ) );
    final DataSchemaCompiler compiler = new DataSchemaCompiler( def, new DefaultDataAttributeContext() );
    final TestTableModel data = new TestTableModel();
    data.getTableAttrs().setMetaAttribute( "test", "test1", DefaultConceptQueryMapper.INSTANCE, "test" );
    data.getColumnAttributes().setMetaAttribute( "test", "test2", DefaultConceptQueryMapper.INSTANCE, "test" );
    // Cell attributes will not be copied into the schema, as they can be different for each cell.

    final DataSchema dataSchema = compiler.compile( data );
    DataAttributes attributes = dataSchema.getAttributes( "A" );
    assertEquals( "global-test", attributes.getMetaAttribute( "global", "global-test", null,
        new DefaultDataAttributeContext() ) );
    assertEquals( "test", attributes.getMetaAttribute( "test", "test1", null, new DefaultDataAttributeContext() ) );
    assertEquals( "test", attributes.getMetaAttribute( "global", "global-ref", null, new DefaultDataAttributeContext() ) );

    attributes = dataSchema.getAttributes( "B" );
    assertEquals( "global-test", attributes.getMetaAttribute( "global", "global-test", null,
        new DefaultDataAttributeContext() ) );
    assertEquals( "test", attributes.getMetaAttribute( "test", "test1", null, new DefaultDataAttributeContext() ) );
    assertEquals( "test", attributes.getMetaAttribute( "global", "global-ref", null, new DefaultDataAttributeContext() ) );
  }

  public void testDataSchemaForPlainTables() throws ReportDataFactoryException {
    final DefaultTableModel model = new DefaultTableModel();
    model.addColumn( "Test" );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();

    final DataSchemaDefinition schemaDefinition = DataSchemaUtility.parseDefaults( mgr );
    final DataSchemaCompiler compiler =
        new DataSchemaCompiler( schemaDefinition, new DefaultDataAttributeContext(), mgr );
    final DataSchema compiledSchema = compiler.compile( model );
    final DataAttributes attributes = compiledSchema.getAttributes( "Test" );
    assertNotNull( attributes );
    assertEquals( "Test", attributes.getMetaAttribute( MetaAttributeNames.Formatting.NAMESPACE,
        MetaAttributeNames.Formatting.LABEL, String.class, new DefaultDataAttributeContext() ) );
  }

  public void testDataSchemaForPlainIndexTables() throws ReportDataFactoryException {
    final DefaultTableModel model = new DefaultTableModel();
    model.addColumn( "Test" );
    model.addColumn( "Test2" );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();

    final DataSchemaDefinition schemaDefinition = DataSchemaUtility.parseDefaults( mgr );
    final DataSchemaCompiler compiler =
        new DataSchemaCompiler( schemaDefinition, new DefaultDataAttributeContext(), mgr );
    final DataSchema compiledSchema = compiler.compile( new IndexedTableModel( model ) );
    final DataAttributes attributes = compiledSchema.getAttributes( "::column::0" );
    assertNotNull( attributes );
    assertEquals( "Test", attributes.getMetaAttribute( MetaAttributeNames.Formatting.NAMESPACE,
        MetaAttributeNames.Formatting.LABEL, String.class, new DefaultDataAttributeContext() ) );

    final DataAttributes attributes2 = compiledSchema.getAttributes( "::column::1" );
    assertNotNull( attributes2 );
    assertEquals( "Test2", attributes2.getMetaAttribute( MetaAttributeNames.Formatting.NAMESPACE,
        MetaAttributeNames.Formatting.LABEL, String.class, new DefaultDataAttributeContext() ) );
  }
}
