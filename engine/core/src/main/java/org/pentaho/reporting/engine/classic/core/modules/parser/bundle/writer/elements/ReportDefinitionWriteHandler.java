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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportPreProcessor;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleElementRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.ExpressionWriterUtility;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanUtility;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.beans.IntrospectionException;
import java.io.IOException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ReportDefinitionWriteHandler extends AbstractElementWriteHandler {
  public ReportDefinitionWriteHandler() {
  }

  /**
   * Writes a single element as XML structure.
   *
   * @param bundle
   *          the bundle to which to write to.
   * @param state
   *          the current write-state.
   * @param xmlWriter
   *          the xml writer.
   * @param element
   *          the element.
   * @throws IOException
   *           if an IO error occured.
   * @throws BundleWriterException
   *           if an Bundle writer.
   */
  public void writeElement( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final XmlWriter xmlWriter, final Element element ) throws IOException, BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( xmlWriter == null ) {
      throw new NullPointerException();
    }
    if ( element instanceof AbstractReportDefinition == false ) {
      throw new IllegalArgumentException();
    }

    final AttributeList rootAttributes = new AttributeList();
    rootAttributes.addNamespaceDeclaration( "", BundleNamespaces.LAYOUT );
    rootAttributes.addNamespaceDeclaration( "style", BundleNamespaces.STYLE );
    rootAttributes.addNamespaceDeclaration( "core", AttributeNames.Core.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "html", AttributeNames.Html.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "swing", AttributeNames.Swing.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "pdf", AttributeNames.Pdf.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "wizard", AttributeNames.Wizard.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "designtime", AttributeNames.Designtime.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "crosstab", AttributeNames.Crosstab.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "pentaho", AttributeNames.Pentaho.NAMESPACE );
    rootAttributes.addNamespaceDeclaration( "table", AttributeNames.Table.NAMESPACE );

    final AttributeList attList = createMainAttributes( element, xmlWriter, rootAttributes );
    xmlWriter.writeTag( BundleNamespaces.LAYOUT, "layout", attList, XmlWriterSupport.OPEN );

    final AbstractReportDefinition report = (AbstractReportDefinition) element;
    final ReportPreProcessor[] processors = report.getPreProcessors();
    for ( int i = 0; i < processors.length; i++ ) {
      final ReportPreProcessor processor = processors[i];
      // write the preprocessor ...
      try {
        xmlWriter.writeTag( BundleNamespaces.LAYOUT, "preprocessor", "class", processor.getClass().getName(),
            XmlWriterSupport.OPEN );
        writePreProcessor( xmlWriter, processor );
        xmlWriter.writeCloseTag();
      } catch ( IntrospectionException e ) {
        throw new BundleWriterException( "Failed to write pre-processor", e );
      } catch ( BeanException e ) {
        throw new BundleWriterException( "Failed to write pre-processor", e );
      }
    }
    if ( ExpressionWriterUtility.isElementLayoutExpressionActive( state ) ) {
      xmlWriter.writeTag( BundleNamespaces.LAYOUT, "layout-processors", XmlWriterSupport.OPEN );
      ExpressionWriterUtility.writeElementLayoutExpressions( bundle, state, xmlWriter );
      xmlWriter.writeCloseTag();
    }

    writeElementBody( bundle, state, element, xmlWriter );

    writeChildElement( bundle, state, xmlWriter, report.getReportHeader() );
    writeChildElement( bundle, state, xmlWriter, report.getRootGroup() );
    writeChildElement( bundle, state, xmlWriter, report.getReportFooter() );

    xmlWriter.writeCloseTag();
  }

  private void writeChildElement( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final XmlWriter xmlWriter, final Element re ) throws IOException, BundleWriterException {
    final BundleElementWriteHandler writeHandler = BundleElementRegistry.getInstance().getWriteHandler( re );
    writeHandler.writeElement( bundle, state, xmlWriter, re );
  }

  private void writePreProcessor( final XmlWriter writer, final ReportPreProcessor preProcessor )
    throws IntrospectionException, BeanException, IOException {
    if ( writer == null ) {
      throw new NullPointerException();
    }
    if ( preProcessor == null ) {
      throw new NullPointerException();
    }
    // the classic way, in case the expression does not provide any meta-data. This is
    // in the code for legacy reasons, as there are many expression implementations out there
    // that do not yet provide meta-data descriptions ..

    final BeanUtility beanUtility = new BeanUtility( preProcessor );
    final String[] propertyNames = beanUtility.getProperties();

    for ( int i = 0; i < propertyNames.length; i++ ) {
      final String key = propertyNames[i];
      final Object property = beanUtility.getProperty( key );
      final Class propertyType = beanUtility.getPropertyType( key );
      final String value = beanUtility.getPropertyAsString( key );
      if ( value != null && property != null ) {
        final AttributeList attList = new AttributeList();
        attList.setAttribute( BundleNamespaces.LAYOUT, "name", key );
        if ( BeanUtility.isSameType( propertyType, property.getClass() ) == false ) {
          attList.setAttribute( BundleNamespaces.LAYOUT, "class", property.getClass().getName() );
        }
        writer.writeTag( BundleNamespaces.LAYOUT, "property", attList, XmlWriterSupport.OPEN );
        writer.writeTextNormalized( value, false );
        writer.writeCloseTag();
      }
    }
  }

  protected boolean isFiltered( final AttributeMetaData attributeMetaData ) {
    if ( AttributeNames.Internal.NAMESPACE.equals( attributeMetaData.getNameSpace() ) ) {
      if ( AttributeNames.Internal.QUERY.equals( attributeMetaData.getName() ) ) {
        return true;
      }
      if ( AttributeNames.Internal.QUERY_LIMIT.equals( attributeMetaData.getName() ) ) {
        return true;
      }
      if ( AttributeNames.Internal.QUERY_TIMEOUT.equals( attributeMetaData.getName() ) ) {
        return true;
      }
      if ( AttributeNames.Internal.PREPROCESSORS.equals( attributeMetaData.getName() ) ) {
        return true;
      }
      if ( AttributeNames.Internal.STRUCTURE_FUNCTIONS.equals( attributeMetaData.getName() ) ) {
        return true;
      }
    } else if ( AttributeNames.Pentaho.NAMESPACE.equals( attributeMetaData.getNameSpace() ) ) {
      if ( AttributeNames.Pentaho.VISIBLE.equals( attributeMetaData.getName() ) ) {
        return true;
      }
    }
    return super.isFiltered( attributeMetaData );
  }

}
