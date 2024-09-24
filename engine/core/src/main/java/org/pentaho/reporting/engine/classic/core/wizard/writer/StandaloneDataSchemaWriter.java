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

package org.pentaho.reporting.engine.classic.core.wizard.writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MetaAttributeNames;
import org.pentaho.reporting.engine.classic.core.layout.output.GenericOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DataAttributes;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaDefinition;
import org.pentaho.reporting.engine.classic.core.wizard.DataSchemaRule;
import org.pentaho.reporting.engine.classic.core.wizard.DefaultDataAttributeContext;
import org.pentaho.reporting.engine.classic.core.wizard.DirectFieldSelectorRule;
import org.pentaho.reporting.engine.classic.core.wizard.GlobalRule;
import org.pentaho.reporting.engine.classic.core.wizard.MetaSelector;
import org.pentaho.reporting.engine.classic.core.wizard.MetaSelectorRule;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Locale;

public class StandaloneDataSchemaWriter {
  private static final Log logger = LogFactory.getLog( StandaloneDataSchemaWriter.class );
  private DataAttributeContext context;

  public StandaloneDataSchemaWriter() {
    context = new DefaultDataAttributeContext( new GenericOutputProcessorMetaData(), Locale.US );
  }

  public void write( final DataSchemaDefinition definition, final OutputStream outputStream, final String encoding )
    throws IOException {
    if ( definition == null ) {
      throw new NullPointerException();
    }
    if ( outputStream == null ) {
      throw new NullPointerException();
    }
    if ( encoding == null ) {
      throw new NullPointerException();
    }

    final XmlWriter writer =
        new XmlWriter( new OutputStreamWriter( outputStream, encoding ), new DefaultTagDescription() );
    writer.writeXmlDeclaration( encoding );

    final AttributeList rootAttributes = new AttributeList();
    rootAttributes.addNamespaceDeclaration( "", ClassicEngineBoot.DATASCHEMA_NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "core", MetaAttributeNames.Core.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "database", MetaAttributeNames.Database.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "expressions", MetaAttributeNames.Expressions.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "formatting", MetaAttributeNames.Formatting.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "numeric", MetaAttributeNames.Numeric.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "style", MetaAttributeNames.Style.NAMESPACE );

    writer.writeTag( ClassicEngineBoot.DATASCHEMA_NAMESPACE, "data-schema", rootAttributes, XmlWriterSupport.OPEN );

    final DataSchemaRule[] globalRules = definition.getGlobalRules();
    final DataSchemaRule[] indirectRules = definition.getIndirectRules();
    final DataSchemaRule[] directRules = definition.getDirectRules();
    try {

      for ( int i = 0; i < globalRules.length; i++ ) {
        final DataSchemaRule rule = globalRules[i];
        if ( rule instanceof GlobalRule ) {
          writer.writeTag( ClassicEngineBoot.DATASCHEMA_NAMESPACE, "global-mapping", XmlWriterSupport.OPEN );
          writeAttributes( writer, rule.getStaticAttributes() );
          writer.writeCloseTag();
        }
      }

      for ( int i = 0; i < indirectRules.length; i++ ) {
        final DataSchemaRule rule = indirectRules[i];
        if ( rule instanceof MetaSelectorRule ) {
          final MetaSelectorRule selectorRule = (MetaSelectorRule) rule;
          writer.writeTag( ClassicEngineBoot.DATASCHEMA_NAMESPACE, "indirect-mapping", XmlWriterSupport.OPEN );
          final MetaSelector[] selectors = selectorRule.getSelectors();
          for ( int j = 0; j < selectors.length; j++ ) {
            final MetaSelector selector = selectors[j];
            final AttributeList selectorAttributeList = new AttributeList();
            selectorAttributeList.setAttribute( ClassicEngineBoot.DATASCHEMA_NAMESPACE, "domain", selector.getDomain() );
            selectorAttributeList.setAttribute( ClassicEngineBoot.DATASCHEMA_NAMESPACE, "name", selector.getName() );
            final String stringValue = ConverterRegistry.toAttributeValue( selector.getValue() );
            selectorAttributeList.setAttribute( ClassicEngineBoot.DATASCHEMA_NAMESPACE, "value", stringValue );
            writer.writeTag( ClassicEngineBoot.DATASCHEMA_NAMESPACE, "match", selectorAttributeList,
                XmlWriterSupport.CLOSE );
          }
          writeAttributes( writer, rule.getStaticAttributes() );
          writer.writeCloseTag();
        }
      }

      for ( int i = 0; i < directRules.length; i++ ) {
        final DataSchemaRule rule = directRules[i];
        if ( rule instanceof DirectFieldSelectorRule ) {
          final DirectFieldSelectorRule fieldSelectorRule = (DirectFieldSelectorRule) rule;
          writer.writeTag( ClassicEngineBoot.DATASCHEMA_NAMESPACE, "direct-mapping", "fieldame", fieldSelectorRule
              .getFieldName(), XmlWriterSupport.OPEN );
          writeAttributes( writer, rule.getStaticAttributes() );
          writer.writeCloseTag();
        }
      }
    } catch ( BeanException e ) {
      StandaloneDataSchemaWriter.logger.warn( "Failed to write data-schema: Reason: ", e );
      throw new IOException( "Failed to write declared data-schema attribute" );
    }
    writer.writeCloseTag();
    writer.flush();
  }

  private void writeAttributes( final XmlWriter writer, final DataAttributes attributes ) throws BeanException,
    IOException {
    final AttributeList attributeList = new AttributeList();

    final String[] domains = attributes.getMetaAttributeDomains();
    for ( int i = 0; i < domains.length; i++ ) {
      final String domain = domains[i];
      final String[] names = attributes.getMetaAttributeNames( domain );
      for ( int j = 0; j < names.length; j++ ) {
        final String name = names[j];
        final Object value = attributes.getMetaAttribute( domain, name, null, context );
        final String stringValue = ConverterRegistry.toAttributeValue( value );
        attributeList.setAttribute( domain, name, stringValue );
      }
    }
    writer
        .writeTag( ClassicEngineBoot.DATASCHEMA_NAMESPACE, "column-attributes", attributeList, XmlWriterSupport.CLOSE );
  }

}
