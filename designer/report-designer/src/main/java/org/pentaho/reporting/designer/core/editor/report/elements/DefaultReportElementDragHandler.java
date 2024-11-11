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


package org.pentaho.reporting.designer.core.editor.report.elements;

import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.report.AbstractReportElementDragHandler;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.wizard.ContextAwareDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;

import java.util.Locale;

public class DefaultReportElementDragHandler extends AbstractReportElementDragHandler {
  public DefaultReportElementDragHandler() {
  }

  protected Element createElement( final ElementMetaData elementMetaData,
                                   final String fieldName,
                                   final ReportDocumentContext context ) throws InstantiationException {
    final ElementType type = elementMetaData.create();
    final Element visualElement = (Element) type.create();

    final ElementStyleSheet styleSheet = visualElement.getStyle();
    styleSheet.setStyleProperty( ElementStyleKeys.MIN_WIDTH, DEFAULT_WIDTH );
    styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT, DEFAULT_HEIGHT );

    type.configureDesignTimeDefaults( visualElement, Locale.getDefault() );
    if ( elementMetaData.getAttributeDescription( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD ) != null ) {
      visualElement.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, fieldName );
    }

    configureWizardProperties( fieldName, context, visualElement );

    return visualElement;
  }

  private void configureWizardProperties( final String fieldName,
                                          final ReportDocumentContext context,
                                          final Element visualElement ) {
    final ContextAwareDataSchemaModel model = context.getReportDataSchemaModel();
    if ( fieldName == null ) {
      return;
    }

    final DataAttributes attributes = model.getDataSchema().getAttributes( fieldName );
    final String source = (String) attributes.getMetaAttribute
      ( MetaAttributeNames.Core.NAMESPACE, MetaAttributeNames.Core.SOURCE, String.class,
        model.getDataAttributeContext() );
    if ( !MetaAttributeNames.Core.SOURCE_VALUE_TABLE.equals( source ) ) {
      return;
    }

    final AbstractReportDefinition report = context.getReportDefinition();
    final DataFactory dataFactory = ModelUtility.findDataFactoryForQuery( report, report.getQuery() );
    if ( dataFactory == null ) {
      return;
    }

    final DataFactoryMetaData data = dataFactory.getMetaData();
    if ( data.isFormattingMetaDataSource() ) {
      visualElement
        .setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_ATTRIBUTES, Boolean.TRUE );
      visualElement
        .setAttribute( AttributeNames.Wizard.NAMESPACE, AttributeNames.Wizard.ALLOW_METADATA_STYLING, Boolean.TRUE );
    }
  }

}
