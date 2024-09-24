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

package org.pentaho.reporting.engine.classic.wizard.writer;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ColorValueConverter;
import org.pentaho.reporting.engine.classic.core.util.beans.ElementAlignmentValueConverter;
import org.pentaho.reporting.engine.classic.wizard.WizardCoreModule;
import org.pentaho.reporting.engine.classic.wizard.model.DetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.Length;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.awt.*;
import java.io.IOException;

public class DetailFieldDefinitionWriteHandler {
  public DetailFieldDefinitionWriteHandler() {
  }

  public void writeReport( final WriteableDocumentBundle bundle,
                           final BundleWriterState wizardFileState,
                           final XmlWriter xmlWriter,
                           final DetailFieldDefinition definition ) throws BundleWriterException, IOException {
    try {
      final AttributeList attList = new AttributeList();

      final ColorValueConverter colorValueConverter = new ColorValueConverter();
      final Color backgroundColor = definition.getBackgroundColor();
      if ( backgroundColor != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "background-color",
          ColorValueConverter.colorToString( backgroundColor ) );
      }
      final Boolean bold = definition.getFontBold();
      if ( bold != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "bold", String.valueOf( bold ) );
      }
      final Color color = definition.getFontColor();
      if ( color != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "font-color",
          ColorValueConverter.colorToString( color ) );
      }
      final Boolean italic = definition.getFontItalic();
      if ( italic != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "italic", String.valueOf( italic ) );
      }
      final Boolean underline = definition.getFontUnderline();
      if ( underline != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "underline", String.valueOf( underline ) );
      }
      final Boolean strikethrough = definition.getFontStrikethrough();
      if ( strikethrough != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "strikethrough", String.valueOf( strikethrough ) );
      }
      final String fontName = definition.getFontName();
      if ( fontName != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "font-name", String.valueOf( fontName ) );
      }
      final Integer fontSize = definition.getFontSize();
      if ( fontSize != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "font-size", String.valueOf( fontSize ) );
      }
      final ElementAlignmentValueConverter elementAlignmentValueConverter = new ElementAlignmentValueConverter();
      final ElementAlignment horizontalAlignment = definition.getHorizontalAlignment();
      if ( horizontalAlignment != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "horizontal-align",
          elementAlignmentValueConverter.toAttributeValue( horizontalAlignment ) );
      }
      final ElementAlignment verticalAlignment = definition.getVerticalAlignment();
      if ( verticalAlignment != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "vertical-align",
          elementAlignmentValueConverter.toAttributeValue( verticalAlignment ) );
      }

      final Boolean distinctValues = definition.getOnlyShowChangingValues();
      if ( distinctValues != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "only-show-distinct", String.valueOf( distinctValues ) );
      }
      final Length width = definition.getWidth();
      if ( width != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "width", String.valueOf( width ) );
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
      xmlWriter.writeTag( WizardCoreModule.NAMESPACE, "detail-field", attList, XmlWriter.CLOSE );

    } catch ( BeanException e ) {
      throw new BundleWriterException( "Failed to write bundle", e );
    }
  }
}
