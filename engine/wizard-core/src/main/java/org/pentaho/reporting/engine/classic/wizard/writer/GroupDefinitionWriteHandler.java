/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.wizard.writer;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ElementAlignmentValueConverter;
import org.pentaho.reporting.engine.classic.wizard.WizardCoreModule;
import org.pentaho.reporting.engine.classic.wizard.model.GroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupType;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

public class GroupDefinitionWriteHandler {
  public GroupDefinitionWriteHandler() {
  }

  public void writeReport( final WriteableDocumentBundle bundle,
                           final BundleWriterState wizardFileState,
                           final XmlWriter xmlWriter,
                           final GroupDefinition definition ) throws BundleWriterException, IOException {
    try {
      final AttributeList attList = new AttributeList();

      final ElementAlignmentValueConverter elementAlignmentValueConverter = new ElementAlignmentValueConverter();
      final ElementAlignment totalsHorizontalAlignment = definition.getTotalsHorizontalAlignment();
      if ( totalsHorizontalAlignment != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "totals-alignment",
          elementAlignmentValueConverter.toAttributeValue( totalsHorizontalAlignment ) );
      }
      final String nullString = definition.getNullString();
      if ( nullString != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "null-string", String.valueOf( nullString ) );
      }
      final String field = definition.getField();
      if ( field != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "field", String.valueOf( field ) );
      }
      final String displayName = definition.getDisplayName();
      if ( displayName != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "display-name", String.valueOf( displayName ) );
      }
      final Class aggreationFunction = definition.getAggregationFunction();
      if ( aggreationFunction != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "aggregation-function", String.valueOf(
          aggreationFunction.getName() ) );
      }
      final String dataFormat = definition.getDataFormat();
      if ( dataFormat != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "data-format", String.valueOf( dataFormat ) );
      }

      final String groupName = definition.getGroupName();
      if ( groupName != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "group-name", String.valueOf( groupName ) );
      }
      final String groupTotalsLabel = definition.getGroupTotalsLabel();
      if ( groupTotalsLabel != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "group-totals-label", String.valueOf( groupTotalsLabel ) );
      }
      final GroupType groupType = definition.getGroupType();
      attList.setAttribute( WizardCoreModule.NAMESPACE, "group-type", groupType.getType() );

      xmlWriter.writeTag( WizardCoreModule.NAMESPACE, "group-definition", attList, XmlWriter.CLOSE );

    } catch ( BeanException e ) {
      throw new BundleWriterException( "Failed to write bundle", e );
    }

  }
}
