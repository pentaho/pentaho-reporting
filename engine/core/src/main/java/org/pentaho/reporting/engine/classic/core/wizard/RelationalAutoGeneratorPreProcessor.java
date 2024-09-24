/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.wizard;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.GroupDataBody;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.GroupDataBodyType;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.states.datarow.DefaultFlowController;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;

import java.util.ArrayList;

/**
 * A class that automatically generates report-elements and summary functions based on the report-data-source. This
 * functionality is equal to the report-design wizard, but does not require user-interaction and unlike the old wizard,
 * this method adapts to changing data-sets.
 *
 * @author Thomas Morgner
 */
public class RelationalAutoGeneratorPreProcessor extends AbstractReportPreProcessor {
  private static class AutoGeneratorFieldDescription {
    private String fieldName;
    private ElementType targetType;
    private Number widthHint;
    private Boolean hideDuplicateValues;

    public AutoGeneratorFieldDescription( final String fieldName, final ElementType targetType, final Number widthHint,
        final Boolean hideDuplicateValues ) {
      this.fieldName = fieldName;
      this.targetType = targetType;
      this.widthHint = widthHint;
      this.hideDuplicateValues = hideDuplicateValues;
    }

    public Boolean getHideDuplicateValues() {
      return hideDuplicateValues;
    }

    public String getFieldName() {
      return fieldName;
    }

    public ElementType getTargetType() {
      return targetType;
    }

    public Number getWidthHint() {
      return widthHint;
    }
  }

  public RelationalAutoGeneratorPreProcessor() {
  }

  public MasterReport performPreProcessing( final MasterReport definition, final DefaultFlowController flowController )
    throws ReportProcessingException {
    if ( definition == null ) {
      throw new NullPointerException();
    }
    if ( flowController == null ) {
      throw new NullPointerException();
    }
    final MasterReport report = (MasterReport) definition.clone();
    generate( report, flowController );
    return report;
  }

  public SubReport performPreProcessing( final SubReport definition, final DefaultFlowController flowController )
    throws ReportProcessingException {
    if ( definition == null ) {
      throw new NullPointerException();
    }
    if ( flowController == null ) {
      throw new NullPointerException();
    }

    final SubReport report = (SubReport) definition.clone();
    generate( report, flowController );
    return report;
  }

  protected void generate( final AbstractReportDefinition definition, final DefaultFlowController flowController )
    throws ReportProcessingException {
    final GroupDataBody groupDataBody = (GroupDataBody) definition.getChildElementByType( GroupDataBodyType.INSTANCE );
    if ( groupDataBody == null ) {
      return;
    }

    final Band details = AutoGeneratorUtility.findGeneratedContent( groupDataBody.getItemBand() );
    final Band header = AutoGeneratorUtility.findGeneratedContent( groupDataBody.getDetailsHeader() );
    final Band footer = AutoGeneratorUtility.findGeneratedContent( groupDataBody.getDetailsFooter() );

    final ProcessingContext reportContext = flowController.getReportContext();
    final DefaultDataAttributeContext dac =
        new DefaultDataAttributeContext( reportContext.getOutputProcessorMetaData(), reportContext
            .getResourceBundleFactory().getLocale() );

    final DataRow dataRow = flowController.getMasterRow().getGlobalView();
    final DataSchema dataSchema = flowController.getMasterRow().getDataSchema();

    // final Locale locale = reportContext.getResourceBundleFactory().getLocale();
    final AutoGeneratorFieldDescription[] fieldDescriptions = computeFields( dataRow, dataSchema, dac );

    if ( fieldDescriptions == null || fieldDescriptions.length == 0 ) {
      // there are no fields, so what's the point of continuing ..
      return;
    }

    if ( details != null ) {
      details.clear();
      details.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "row" );
    }
    if ( header != null ) {
      header.clear();
      header.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "row" );
    }
    if ( footer != null ) {
      footer.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "row" );
      footer.clear();
    }

    final float[] widths = computeFieldWidths( fieldDescriptions, definition.getPageDefinition().getWidth() );
    for ( int i = 0; i < fieldDescriptions.length; i++ ) {
      final AutoGeneratorFieldDescription fieldDescription = fieldDescriptions[i];
      if ( header != null ) {
        final Element headerElement = AutoGeneratorUtility.generateHeaderElement( fieldDescription.getFieldName() );
        final ElementStyleSheet headerStyle = headerElement.getStyle();
        headerStyle.setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( widths[i] ) );
        header.addElement( headerElement );
      }

      if ( details != null ) {
        final Element detailsElement =
            AutoGeneratorUtility.generateDetailsElement( fieldDescription.getFieldName(), fieldDescription
                .getTargetType() );
        if ( Boolean.TRUE.equals( fieldDescription.getHideDuplicateValues() ) ) {
          detailsElement.setAttribute( AttributeNames.Wizard.NAMESPACE,
              AttributeNames.Wizard.ONLY_SHOW_CHANGING_VALUES, Boolean.TRUE );
        }

        final ElementStyleSheet detailsStyle = detailsElement.getStyle();
        detailsStyle.setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( widths[i] ) );
        details.addElement( detailsElement );
      }
    }
  }

  private float[] computeFieldWidths( final AutoGeneratorFieldDescription[] fieldDescriptions, final float pageWidth ) {
    final Float[] widths = new Float[fieldDescriptions.length];
    for ( int i = 0; i < fieldDescriptions.length; i++ ) {
      final AutoGeneratorFieldDescription description = fieldDescriptions[i];
      final Number number = description.getWidthHint();
      if ( number != null ) {
        final float value = number.floatValue();
        if ( value > 0 ) {
          widths[i] = new Float( value );
        }
      }
    }

    final float[] fieldWidths = AutoGeneratorUtility.computeFieldWidths( widths, pageWidth );

    // The field widths returned are a percentage ... lets adjust them to make them 100%
    // and if the widths are negative, the results should be negative as well
    float total = 0;
    for ( int i = 0; i < fieldWidths.length; ++i ) {
      total += fieldWidths[i];
    }
    final float scale = (float) ( fieldWidths[0] < 0 ? -100.0 : 100.0 );
    for ( int i = 0; i < fieldWidths.length; ++i ) {
      fieldWidths[i] = scale * ( fieldWidths[i] / total );
    }

    return fieldWidths;
  }

  private AutoGeneratorFieldDescription[] computeFields( final DataRow dataRow, final DataSchema dataSchema,
      final DataAttributeContext context ) {
    final ArrayList<AutoGeneratorFieldDescription> fields = new ArrayList<AutoGeneratorFieldDescription>();
    final String[] columnNames = dataRow.getColumnNames();
    for ( int i = 0; i < columnNames.length; i++ ) {
      final String name = columnNames[i];
      final DataAttributes attributes = dataSchema.getAttributes( name );
      if ( attributes == null ) {
        continue;
      }
      if ( AutoGeneratorUtility.isIgnorable( attributes, context ) ) {
        continue;
      }

      final Number width = AutoGeneratorUtility.createFieldWidth( attributes, context );
      final String fieldName = AutoGeneratorUtility.createFieldName( attributes, context );
      final ElementType targetType = AutoGeneratorUtility.createFieldType( attributes, context );
      final Boolean hideDuplicateItems =
          (Boolean) attributes.getMetaAttribute( MetaAttributeNames.Formatting.NAMESPACE,
              MetaAttributeNames.Formatting.HIDE_DUPLICATE_ITEMS, Number.class, context );
      fields.add( new AutoGeneratorFieldDescription( fieldName, targetType, width, hideDuplicateItems ) );
    }
    return fields.toArray( new AutoGeneratorFieldDescription[fields.size()] );

  }

}
