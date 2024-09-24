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

package org.pentaho.reporting.designer.core.util.table;

import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.DataSchemaUtility;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.util.DataSchemaFieldDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchema;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.EmptyDataAttributes;
import org.pentaho.reporting.libraries.designtime.swing.ColorUtility;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;

public class CellEditorUtility {
  private CellEditorUtility() {
  }


  public static String[] getExcelColorsAsText() {
    final Color[] excelColors = ColorUtility.getPredefinedExcelColors();
    final String[] textColors = new String[ excelColors.length ];
    for ( int i = 0; i < excelColors.length; i++ ) {
      final Color excelColor = excelColors[ i ];
      final String color = Integer.toHexString( excelColor.getRGB() & 0x00ffffff );
      final StringBuffer retval = new StringBuffer( 7 );
      retval.append( '#' );
      final int fillUp = 6 - color.length();
      for ( int x = 0; x < fillUp; x++ ) {
        retval.append( '0' );
      }
      retval.append( color );
      textColors[ i ] = retval.toString();
    }
    return textColors;
  }

  public static String[] getQueryNames( final ReportDesignerContext designerContext ) {
    if ( designerContext == null ) {
      return new String[ 0 ];
    }

    final ReportDocumentContext reportContext = designerContext.getActiveContext();
    if ( reportContext == null ) {
      return new String[ 0 ];
    }

    AbstractReportDefinition definition = reportContext.getReportDefinition();
    final LinkedHashSet<String> names = new LinkedHashSet<String>();
    while ( definition != null ) {
      final CompoundDataFactory dataFactoryElement = (CompoundDataFactory) definition.getDataFactory();
      final int dataFactoryCount = dataFactoryElement.size();
      for ( int i = 0; i < dataFactoryCount; i++ ) {
        final DataFactory dataFactory = dataFactoryElement.getReference( i );
        final String[] queryNames = dataFactory.getQueryNames();
        names.addAll( Arrays.asList( queryNames ) );
      }
      if ( definition instanceof SubReport ) {
        final Section parentSection = definition.getParentSection();
        definition = (AbstractReportDefinition) parentSection.getReportDefinition();
      } else {
        definition = null;
      }
    }
    return names.toArray( new String[ names.size() ] );
  }


  public static FieldDefinition[] getFields( final ReportDesignerContext designerContext,
                                             final String[] extraFields ) {
    if ( designerContext == null ) {
      return new FieldDefinition[ 0 ];
    }

    final ReportDocumentContext reportContext = designerContext.getActiveContext();
    if ( reportContext == null ) {
      return new FieldDefinition[ 0 ];
    }

    return getFields( reportContext, extraFields );
  }

  public static FieldDefinition[] getFields( final ReportDocumentContext reportContext, final String[] extraFields ) {
    final ContextAwareDataSchemaModel model = reportContext.getReportDataSchemaModel();
    final String[] columnNames = model.getColumnNames();
    final ArrayList<FieldDefinition> fields = new ArrayList<FieldDefinition>( columnNames.length + extraFields.length );
    final DataSchema dataSchema = model.getDataSchema();
    final DefaultDataAttributeContext dataAttributeContext = new DefaultDataAttributeContext();

    for ( int i = 0; i < extraFields.length; i++ ) {
      final String extraField = extraFields[ i ];
      fields.add( new DataSchemaFieldDefinition( extraField, new EmptyDataAttributes(), dataAttributeContext ) );
    }

    for ( int i = columnNames.length - 1; i >= 0; i -= 1 ) {
      final String columnName = columnNames[ i ];
      final DataAttributes attributes = dataSchema.getAttributes( columnName );
      if ( attributes == null ) {
        throw new IllegalStateException( "No data-schema for field with name '" + columnName + '\'' );
      }
      if ( DataSchemaUtility.isFiltered( attributes, dataAttributeContext ) ) {
        continue;
      }
      fields.add( new DataSchemaFieldDefinition( columnName, attributes, dataAttributeContext ) );
    }

    return fields.toArray( new FieldDefinition[ fields.size() ] );
  }

  public static String[] getFieldsAsString( final ReportDocumentContext designerContext,
                                            final String[] extraFields ) {
    final FieldDefinition[] fields = getFields( designerContext, extraFields );
    return convertToColumnNames( fields );
  }

  public static String[] convertToColumnNames( final FieldDefinition[] fields ) {
    final String[] fieldsAsString = new String[ fields.length ];
    for ( int i = 0; i < fields.length; i++ ) {
      FieldDefinition field = fields[ i ];
      fieldsAsString[ i ] = field.getName();
    }
    return fieldsAsString;
  }

  public static String[] getFieldsAsString( final ReportDesignerContext designerContext,
                                            final String[] extraFields ) {
    final FieldDefinition[] fields = getFields( designerContext, extraFields );
    return convertToColumnNames( fields );
  }

  public static String[] getGroups( final ReportDesignerContext designerContext ) {
    if ( designerContext == null ) {
      return new String[ 0 ];
    }

    final ReportDocumentContext reportContext = designerContext.getActiveContext();
    if ( reportContext == null ) {
      return new String[ 0 ];
    }

    return ModelUtility.getGroups( reportContext.getReportDefinition() );
  }
}
