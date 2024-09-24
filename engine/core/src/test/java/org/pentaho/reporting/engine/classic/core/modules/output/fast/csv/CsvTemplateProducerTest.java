/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2019 - 2024 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast.csv;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.pentaho.reporting.engine.classic.core.filter.types.AutoLayoutBoxType;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.model.ParagraphRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.context.NodeLayoutProperties;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.template.TemplatingOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableContentProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableRectangle;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVTableModule;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;
import org.mockito.junit.MockitoJUnitRunner;

/**
 * @author David Griffen
 */
@RunWith( MockitoJUnitRunner.class )
public class CsvTemplateProducerTest {

  private static final String COMPLETE_TEMPLATE = CSVTableModule.SEPARATOR_DEFAULT + CSVTableModule.SEPARATOR_DEFAULT
    + CSVTableModule.SEPARATOR_DEFAULT + System.lineSeparator() + "$(0)" + CSVTableModule.SEPARATOR_DEFAULT
    + "$(0)" + CSVTableModule.SEPARATOR_DEFAULT + "$(0)" + System.lineSeparator() + CSVTableModule.SEPARATOR_DEFAULT
    + CSVTableModule.SEPARATOR_DEFAULT + CSVTableModule.SEPARATOR_DEFAULT + System.lineSeparator();

  private StyleSheet styleSheet;
  private SheetLayout sheetLayout;
  private NodeLayoutProperties nodeLayoutProperties;
  private OutputProcessorMetaData metaData;
  private TableContentProducer contentProducer;
  private CsvTemplateProducer csvTemplateProducer;

  @Before
  public void setUp() throws Exception {
    styleSheet = mock( StyleSheet.class );
    sheetLayout = mock( SheetLayout.class );
    metaData = mock( OutputProcessorMetaData.class );
    nodeLayoutProperties = mock( NodeLayoutProperties.class );
    HierarchicalConfiguration configuration = mock( HierarchicalConfiguration.class );
    when( metaData.getConfiguration() ).thenReturn( configuration );
    when( configuration.getConfigProperty( CSVTableModule.SEPARATOR,
      CSVTableModule.SEPARATOR_DEFAULT ) ).thenReturn( CSVTableModule.SEPARATOR_DEFAULT );
    contentProducer = spy( new TableContentProducer( sheetLayout, metaData ) );
    csvTemplateProducer = new CsvTemplateProducer( metaData, sheetLayout, null );
  }

  /**
   * [PRD-5412] Verifying the method will produce the appropriate CSV template when the content is null, if it contains
   * information, and if it is an empty spanned cell (it still needs to print a Content Separated Value character if
   * a column/row is empty).
   */
  @Test
  public void testProduceTemplate() {
    InstanceID instanceId = mock( InstanceID.class );
    LogicalPageBox pageBox = mock( LogicalPageBox.class );
    RenderBox content = mock( ParagraphRenderBox.class );
    TableRectangle rectangle = mock( TableRectangle.class );
    mockStatic( TemplatingOutputProcessor.class );
    Long contentOffset = 0L;


    when( TemplatingOutputProcessor.produceTableLayout( pageBox, sheetLayout, metaData ) ).thenReturn( contentProducer );
    when( contentProducer.getColumnCount() ).thenReturn( 3 );
    when( contentProducer.getFinishedRows() ).thenReturn( 0 );
    when( contentProducer.getFilledRows() ).thenReturn( 3 );

    when( content.isCommited() ).thenReturn( true );
    when( content.getX() ).thenReturn( 100000L );
    when( content.getY() ).thenReturn( 100000L );
    when( content.getWidth() ).thenReturn( 185600000L );
    when( content.getHeight() ).thenReturn( 6200000L );
    when( sheetLayout.getTableBounds( content.getX(), content.getY() + contentOffset,
      content.getWidth(), content.getHeight(), null ) ).thenReturn( rectangle );

    when( content.getNodeLayoutProperties() ).thenReturn( nodeLayoutProperties );
    when( nodeLayoutProperties.getInstanceId() ).thenReturn( instanceId );

    // First line should be null content, to show that it will print the separators only.
    for ( int i = 0; i < 3; i++ ) {
      when( contentProducer.getContent( 0, i ) ).thenReturn( null );
    }
    // Second line should print content, we're mocking it with a single InstanceID, so it will print the same
    // value multiple times (3x)
    for ( int i = 0; i < 3; i++ ) {
      when( contentProducer.getContent( 1, i ) ).thenReturn( content );
      when( contentProducer.getContentOffset( 1, i ) ).thenReturn( contentOffset );
      when( rectangle.isOrigin( i, 1 ) ).thenReturn( true );
    }
    // Third line would contain content, but it's actually an empty spanned cell, so it should see this and still
    // print a separator for the CSV template. We have to print the separator to signify a move to the next column
    for ( int i = 0; i < 3; i++ ) {
      when( contentProducer.getContent( 2, i ) ).thenReturn( content );
      when( contentProducer.getContentOffset( 2, i ) ).thenReturn( contentOffset );
      when( rectangle.isOrigin( i, 2 ) ).thenReturn( false );
    }

    csvTemplateProducer.produceTemplate( pageBox );
    assertEquals( csvTemplateProducer.getTemplate(), COMPLETE_TEMPLATE );
  }
}
