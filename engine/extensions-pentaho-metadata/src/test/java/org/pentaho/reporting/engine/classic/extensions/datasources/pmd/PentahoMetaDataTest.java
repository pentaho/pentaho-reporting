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

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd;

import org.pentaho.metadata.model.Domain;
import org.pentaho.metadata.model.IPhysicalColumn;
import org.pentaho.metadata.model.IPhysicalTable;
import org.pentaho.metadata.model.SqlPhysicalModel;
import org.pentaho.metadata.model.concept.types.AggregationType;
import org.pentaho.metadata.model.concept.types.LocalizedString;
import org.pentaho.metadata.repository.IMetadataDomainRepository;
import org.pentaho.metadata.repository.InMemoryMetadataDomainRepository;
import org.pentaho.metadata.util.XmiParser;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.ParameterDataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.designtime.datafactory.DesignTimeDataFactoryContext;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.testsupport.DataSourceTestBase;
import org.pentaho.reporting.engine.classic.core.util.CloseableTableModel;
import org.pentaho.reporting.engine.classic.core.wizard.ConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaCompiler;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultConceptQueryMapper;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataSchemaDefinition;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @noinspection HardCodedStringLiteral
 */
public class PentahoMetaDataTest extends DataSourceTestBase {
  private static class MultipleAggregationTestConnectionProvider extends PmdConnectionProvider {
    private static final long serialVersionUID = 2672461111722673121L;

    public IMetadataDomainRepository getMetadataDomainRepository( final String domainId,
                                                                  final ResourceManager resourceManager,
                                                                  final ResourceKey contextKey,
                                                                  final String xmiFile )
      throws ReportDataFactoryException {
      try {
        final InputStream stream = createStream( resourceManager, contextKey, xmiFile );
        try {
          final InMemoryMetadataDomainRepository repo = new InMemoryMetadataDomainRepository();
          final XmiParser parser = new XmiParser();
          final Domain domain = parser.parseXmi( stream );
          // add a couple of agg types to the quantity ordered physical column
          final IPhysicalTable table =
            ( (SqlPhysicalModel) domain.getPhysicalModels().get( 0 ) ).getPhysicalTables().get( 7 );
          final IPhysicalColumn col = table.getPhysicalColumns().get( 3 );
          final List<AggregationType> list = new ArrayList<AggregationType>();
          list.add( AggregationType.SUM );
          list.add( AggregationType.AVERAGE );
          col.setAggregationList( list );
          domain.setId( domainId );
          repo.storeDomain( domain, true );
          return repo;
        } finally {
          stream.close();
        }
      } catch ( final Exception e ) {
        throw new ReportDataFactoryException( "The Specified XMI File is invalid: " + xmiFile, e );
      }
    }

  }

  private static final String QUERY =
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
      "<mql>" +
      "  <domain_type>relational</domain_type>" +
      "  <domain_id>steel-wheels</domain_id>" +
      "  <model_id>BV_HUMAN_RESOURCES</model_id>" +
      "  <model_name>Human Resources</model_name>" +
      "  <options>" +
      "     <disable_distinct>true</disable_distinct>" +
      "  </options>" +
      "  <selections>" +
      "    <selection>" +
      "       <view>BC_EMPLOYEES_</view>" +
      "       <column>BC_EMPLOYEES_FIRSTNAME</column>" +
      "    </selection>" +
      "    <selection>" +
      "       <view>BC_EMPLOYEES_</view>" +
      "       <column>BC_EMPLOYEES_LASTNAME</column>" +
      "    </selection>" +
      "    <selection>" +
      "      <view>BC_EMPLOYEES_</view>" +
      "      <column>BC_EMPLOYEES_EMPLOYEENUMBER</column>" +
      "    </selection>" +
      "    <selection>" +
      "      <view>BC_EMPLOYEES_</view>" +
      "      <column>BC_EMPLOYEES_EMAIL</column>" +
      "    </selection>" +
      "  </selections>" +
      "  <constraints/>" +
      "  <orders>" +
      "    <order>" +
      "      <direction>asc</direction>" +
      "      <view_id>BC_OFFICES_</view_id>" +
      "      <column_id>BC_OFFICES_COUNTRY</column_id>" +
      "    </order>" +
      "    <order>" +
      "      <direction>asc</direction>" +
      "      <view_id>BC_OFFICES_</view_id>" +
      "      <column_id>BC_OFFICES_STATE</column_id>" +
      "    </order>" +
      "  </orders>" +
      "</mql>";

  private static final String PARAMETRIZED_QUERY = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<mql>" +
    "<domain_id>steel-wheels</domain_id>" +
    "<model_id>BV_ORDERS</model_id>" +
    "<options>" +
    "<disable_distinct>false</disable_distinct>" +
    "</options>" +
    "<parameters>" +
    "<parameter defaultValue=\"Shipped\" name=\"oStatus\" type=\"STRING\"/>" +
    "</parameters>" +
    "<selections>" +
    "<selection>" +
    "<view>BC_CUSTOMER_W_TER_</view>" +
    "<column>BC_CUSTOMER_W_TER_CUSTOMERNUMBER</column>" +
    "<aggregation>NONE</aggregation>" +
    "</selection>" +
    "<selection>" +
    "<view>BC_CUSTOMER_W_TER_</view>" +
    "<column>BC_CUSTOMER_W_TER_CUSTOMERNAME</column>" +
    "<aggregation>NONE</aggregation>" +
    "</selection>" +
    "<selection>" +
    "<view>CAT_ORDERS</view>" +
    "<column>BC_ORDERS_ORDERNUMBER</column>" +
    "<aggregation>NONE</aggregation>" +
    "</selection>" +
    "<selection>" +
    "<view>CAT_ORDERS</view>" +
    "<column>BC_ORDERDETAILS_TOTAL</column>" +
    "<aggregation>SUM</aggregation>" +
    "</selection>" +
    "<selection>" +
    "<view>CAT_ORDERS</view>" +
    "<column>BC_ORDERS_STATUS</column>" +
    "<aggregation>NONE</aggregation>" +
    "</selection>" +
    "<selection>" +
    "<view>CAT_ORDERS</view>" +
    "<column>BC_ORDERS_COMMENTS</column>" +
    "<aggregation>NONE</aggregation>" +
    "</selection>" +
    "</selections>" +
    "<constraints>" +
    "<constraint>" +
    "<operator/>" +
    "<condition>[CAT_ORDERS.BC_ORDERS_STATUS] = [param:oStatus]</condition>" +
    "</constraint>" +
    "</constraints>" +
    "<orders/>" +
    "</mql>";

  private static final String MULTIPLE_AGG_QUERY = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
    "<mql>" +
    "<domain_id>steel-wheels</domain_id>" +
    "<model_id>BV_ORDERS</model_id>" +
    "<options>" +
    "<disable_distinct>false</disable_distinct>" +
    "</options>" +
    "<selections>" +
    "<selection>" +
    "<view>BC_CUSTOMER_W_TER_</view>" +
    "<column>BC_CUSTOMER_W_TER_TERRITORY</column>" +
    "<aggregation>NONE</aggregation>" +
    "</selection>" +
    "<selection>" +
    "<view>CAT_ORDERS</view>" +
    "<column>BC_ORDERDETAILS_QUANTITYORDERED</column>" +
    "<aggregation>SUM</aggregation>" +
    "</selection>" +
    "<selection>" +
    "<view>CAT_ORDERS</view>" +
    "<column>BC_ORDERDETAILS_QUANTITYORDERED</column>" +
    "<aggregation>AVERAGE</aggregation>" +
    "</selection>" +
    "</selections>" +
    "<constraints>" +
    "</constraints>" +
    "<orders/>" +
    "</mql>";

  public static final String[][] QUERIES_AND_RESULTS = new String[][] {
    { QUERY, "query-results.txt", "design-time-query-results.txt" },
    //    {PARAMETRIZED_QUERY, "query-results-2.txt", "design-time-query-results-2.txt"},
  };

  public PentahoMetaDataTest() {
  }

  protected DataFactory createDataFactory( final String query ) throws ReportDataFactoryException {
    final PmdDataFactory pmdDataFactory = new PmdDataFactory();
    pmdDataFactory.setConnectionProvider( new PmdConnectionProvider() );
    pmdDataFactory.setXmiFile( "src/test/resources/metadata/metadata.xmi" );
    pmdDataFactory.setDomainId( "steel-wheels" );
    pmdDataFactory.setQuery( "default", query, null, null );
    initializeDataFactory( pmdDataFactory );
    return pmdDataFactory;
  }

  public void testDataSchemaCompiler() throws Exception {

    final PmdDataFactory pmdDataFactory = new PmdDataFactory();
    pmdDataFactory.setConnectionProvider( new PmdConnectionProvider() );
    pmdDataFactory.setXmiFile( "src/test/resources/metadata/metadata.xmi" );
    pmdDataFactory.setDomainId( "steel-wheels" );
    pmdDataFactory.initialize( new DesignTimeDataFactoryContext() );

    try {
      pmdDataFactory.setQuery( "default", QUERY, null, null );
      final CloseableTableModel tableModel =
        (CloseableTableModel) pmdDataFactory.queryData( "default", new ParameterDataRow() );
      try {
        final DefaultDataSchemaDefinition def = new DefaultDataSchemaDefinition();
        final DataSchemaCompiler compiler = new DataSchemaCompiler( def, new DefaultDataAttributeContext() );
        final DataSchema dataSchema = compiler.compile( tableModel );
        final String[] names = dataSchema.getNames();
        assertEquals( 4, names.length );
        assertEquals( "BC_EMPLOYEES_FIRSTNAME", names[ 0 ] );
        assertEquals( "BC_EMPLOYEES_LASTNAME", names[ 1 ] );
        assertEquals( "BC_EMPLOYEES_EMPLOYEENUMBER", names[ 2 ] );
        assertEquals( "BC_EMPLOYEES_EMAIL", names[ 3 ] );

        final DataAttributes attributes = dataSchema.getAttributes( names[ 2 ] );
        // assert that formatting-label is not a default mapper
        final ConceptQueryMapper mapper = attributes.getMetaAttributeMapper( MetaAttributeNames.Formatting.NAMESPACE,
          MetaAttributeNames.Formatting.LABEL );
        if ( mapper instanceof DefaultConceptQueryMapper ) {
          fail( "Formatting::label should be a LocalizedString instead of a default-mapper" );
        }

        final Object value = attributes.getMetaAttribute( MetaAttributeNames.Formatting.NAMESPACE,
          MetaAttributeNames.Formatting.LABEL, null, new DefaultDataAttributeContext() );
        if ( value instanceof LocalizedString == false ) {
          fail( "Formatting::label should be a LocalizedString" );
        }

        final Object label = attributes.getMetaAttribute( MetaAttributeNames.Formatting.NAMESPACE,
          MetaAttributeNames.Formatting.LABEL, String.class, new DefaultDataAttributeContext( Locale.US ) );
        if ( label instanceof String == false ) {
          fail( "Formatting::label should be a String" );
        }

        final Object elementAlignment = attributes.getMetaAttribute( MetaAttributeNames.Style.NAMESPACE,
          MetaAttributeNames.Style.HORIZONTAL_ALIGNMENT, null, new DefaultDataAttributeContext( Locale.US ) );
        if ( "right".equals( elementAlignment ) == false ) {
          fail( "Style::horizontal-alignment should be a String of value 'right'" );
        }

        final DataAttributes attributes2 = dataSchema.getAttributes( names[ 0 ] );
        final Object elementAlignment2 = attributes2.getMetaAttribute( MetaAttributeNames.Style.NAMESPACE,
          MetaAttributeNames.Style.HORIZONTAL_ALIGNMENT, null, new DefaultDataAttributeContext( Locale.US ) );
        if ( "left".equals( elementAlignment2 ) == false ) {
          fail( "Style::horizontal-alignment should be a String of value 'right'" );
        }

      } finally {
        tableModel.close();
      }
    } finally {

      pmdDataFactory.close();
    }
  }


  public void testMetaData() throws ReportDataFactoryException {
    final PmdDataFactory pmdDataFactory = new PmdDataFactory();
    pmdDataFactory.setConnectionProvider( new PmdConnectionProvider() );
    pmdDataFactory.setXmiFile( "src/test/resources/metadata/metadata.xmi" );
    pmdDataFactory.setDomainId( "steel-wheels" );
    pmdDataFactory.initialize( new DesignTimeDataFactoryContext() );
    pmdDataFactory.setQuery( "default", PARAMETRIZED_QUERY, null, null );

    final DataFactoryMetaData metaData = pmdDataFactory.getMetaData();
    final Object queryHash = metaData.getQueryHash( pmdDataFactory, "default", new StaticDataRow() );
    assertNotNull( queryHash );

    final PmdDataFactory pmdDataFactory2 = new PmdDataFactory();
    pmdDataFactory2.setConnectionProvider( new PmdConnectionProvider() );
    pmdDataFactory2.setXmiFile( "src/test/resources/metadata/metadata.xmi" );
    pmdDataFactory2.setDomainId( "steel-wheels" );
    pmdDataFactory2.initialize( new DesignTimeDataFactoryContext() );
    pmdDataFactory2.setQuery( "default", QUERY, null, null );
    pmdDataFactory2.setQuery( "default2", PARAMETRIZED_QUERY, null, null );

    assertNotEquals( "Physical Query is not the same", queryHash,
      metaData.getQueryHash( pmdDataFactory2, "default", new StaticDataRow() ) );
    assertEquals( "Physical Query is the same", queryHash,
      metaData.getQueryHash( pmdDataFactory2, "default2", new StaticDataRow() ) );

    final PmdDataFactory pmdDataFactory3 = new PmdDataFactory();
    pmdDataFactory3.setConnectionProvider( new PmdConnectionProvider() );
    pmdDataFactory3.setXmiFile( "src/test/resources/metadata/metadata.xmi" );
    pmdDataFactory3.setDomainId( "steel-wheels2" );
    pmdDataFactory3.setQuery( "default", QUERY, null, null );
    pmdDataFactory3.setQuery( "default2", PARAMETRIZED_QUERY, null, null );

    assertNotEquals( "Physical Connection is not the same", queryHash,
      metaData.getQueryHash( pmdDataFactory3, "default", new StaticDataRow() ) );
    assertNotEquals( "Physical Connection is the same", queryHash,
      metaData.getQueryHash( pmdDataFactory3, "default2", new StaticDataRow() ) );

    final PmdDataFactory pmdDataFactory4 = new PmdDataFactory();
    pmdDataFactory4.setConnectionProvider( new PmdConnectionProvider() );
    pmdDataFactory4.setXmiFile( "src/test/resources/metadata/metadata2.xmi" );
    pmdDataFactory4.setDomainId( "steel-wheels" );
    pmdDataFactory4.setQuery( "default", QUERY, null, null );
    pmdDataFactory4.setQuery( "default2", PARAMETRIZED_QUERY, null, null );

    assertNotEquals( "Physical Connection is not the same", queryHash,
      metaData.getQueryHash( pmdDataFactory4, "default", new StaticDataRow() ) );
    assertNotEquals( "Physical Connection is the same", queryHash,
      metaData.getQueryHash( pmdDataFactory4, "default2", new StaticDataRow() ) );
  }

  public void testMultipleAggregations() throws Exception {
    final PmdDataFactory pmdDataFactory = new PmdDataFactory();
    pmdDataFactory.setConnectionProvider( new MultipleAggregationTestConnectionProvider() );
    pmdDataFactory.setXmiFile( "src/test/resources/metadata/metadata.xmi" );
    pmdDataFactory.setDomainId( "steel-wheels" );
    pmdDataFactory.initialize( new DesignTimeDataFactoryContext() );
    try {
      pmdDataFactory.setQuery( "default", MULTIPLE_AGG_QUERY, null, null );

      final CloseableTableModel tableModel =
        (CloseableTableModel) pmdDataFactory.queryData( "default", new ParameterDataRow() );
      try {
        final DefaultDataSchemaDefinition def = new DefaultDataSchemaDefinition();
        final DataSchemaCompiler compiler = new DataSchemaCompiler( def, new DefaultDataAttributeContext() );
        final DataSchema dataSchema = compiler.compile( tableModel );
        final String[] names = dataSchema.getNames();
        assertEquals( 3, names.length );
        assertEquals( "BC_CUSTOMER_W_TER_TERRITORY", names[ 0 ] );
        assertEquals( "BC_ORDERDETAILS_QUANTITYORDERED", names[ 1 ] );
        assertEquals( "BC_ORDERDETAILS_QUANTITYORDERED:AVERAGE", names[ 2 ] );
        final ByteArrayOutputStream sw = new ByteArrayOutputStream();
        final PrintStream out = new PrintStream( sw );
        generateCompareText( out, tableModel );
        compareLineByLine( "agg-query-results.txt", sw.toString() );
      } finally {
        tableModel.close();
      }
    } finally {
      pmdDataFactory.close();
    }
  }

  public void runGenerateMultiAgg() throws ReportDataFactoryException, IOException, SQLException {
    final PmdDataFactory pmdDataFactory = new PmdDataFactory();
    pmdDataFactory.setConnectionProvider( new MultipleAggregationTestConnectionProvider() );
    pmdDataFactory.setXmiFile( "src/test/resources/metadata/metadata.xmi" );
    pmdDataFactory.setDomainId( "steel-wheels" );
    pmdDataFactory.setQuery( "default", MULTIPLE_AGG_QUERY, null, null );
    pmdDataFactory.initialize( new DesignTimeDataFactoryContext() );

    generate( pmdDataFactory, "agg-query-results.txt" );
    generateDesignTime( pmdDataFactory, "design-time-agg-query-results.txt" );
  }

  public void testParameter() throws ReportDataFactoryException {
    final PmdDataFactory pmdDataFactory = new PmdDataFactory();
    pmdDataFactory.setConnectionProvider( new PmdConnectionProvider() );
    pmdDataFactory.setXmiFile( "src/test/resources/metadata/metadata.xmi" );
    pmdDataFactory.setDomainId( "steel-wheels" );
    pmdDataFactory.initialize( new DesignTimeDataFactoryContext() );
    pmdDataFactory.setQuery( "default", PARAMETRIZED_QUERY, null, null );
    pmdDataFactory.setQuery( "default2", QUERY, null, null );

    final DataFactoryMetaData metaData = pmdDataFactory.getMetaData();
    final String[] fields = metaData.getReferencedFields( pmdDataFactory, "default", new StaticDataRow() );
    assertNotNull( fields );
    assertEquals( 3, fields.length );
    assertEquals( "oStatus", fields[ 0 ] );
    assertEquals( DataFactory.QUERY_LIMIT, fields[ 1 ] );
    assertEquals( DataFactory.QUERY_TIMEOUT, fields[ 2 ] );

    final String[] fields2 = metaData.getReferencedFields( pmdDataFactory, "default2", new StaticDataRow() );
    assertNotNull( fields2 );
    assertEquals( 2, fields2.length );
    assertEquals( DataFactory.QUERY_LIMIT, fields2[ 0 ] );
    assertEquals( DataFactory.QUERY_TIMEOUT, fields2[ 1 ] );
  }

  public void testSaveAndLoad() throws Exception {
    runSaveAndLoad( QUERIES_AND_RESULTS );
  }

  public void testDerive() throws Exception {
    runDerive( QUERIES_AND_RESULTS );
  }

  public void testSerialize() throws Exception {
    runSerialize( QUERIES_AND_RESULTS );
  }

  public void testQuery() throws Exception {
    runTest( QUERIES_AND_RESULTS );
  }
}
