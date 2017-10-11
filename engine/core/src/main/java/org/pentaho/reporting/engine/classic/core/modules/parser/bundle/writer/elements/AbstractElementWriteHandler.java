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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.elements;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.ResourceReference;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleElementRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleElementWriteHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.ExpressionWriterUtility;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.StyleWriterUtility;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.beans.PropertyEditor;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * Provides a base implementation for element write handlers.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractElementWriteHandler implements BundleElementWriteHandler {
  private static final Log logger = LogFactory.getLog( AbstractElementWriteHandler.class );

  protected AbstractElementWriteHandler() {
  }

  protected void copyStaticResources( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final Element element ) throws BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( element == null ) {
      throw new NullPointerException();
    }

    final ResourceKey contentBase = element.getContentBase();
    if ( contentBase == null ) {
      // treat all resources as linked resources ..
      AbstractElementWriteHandler.logger.debug( "No content base, treating all content as linked." );
      return;
    }
    final ResourceKey defSource = element.getDefinitionSource();
    if ( defSource == null ) {
      // treat all resources as linked resources ..
      AbstractElementWriteHandler.logger.debug( "No report definition source, treating all content as linked." );
      return;
    }

    if ( ObjectUtilities.equal( contentBase.getParent(), defSource.getParent() ) == false ) {
      // treat all resources as linked resources ..
      AbstractElementWriteHandler.logger
          .debug( "Content base points to non-bundle location, treating all content as linked." );
      return;
    }

    final Object contentBasePathRaw = contentBase.getIdentifier();
    if ( contentBasePathRaw instanceof String == false ) {
      return;
    }

    final String contentBasePath = String.valueOf( contentBasePathRaw );
    final ResourceManager resourceManager = state.getMasterReport().getResourceManager();

    final ElementType type = element.getElementType();
    final ElementMetaData data = type.getMetaData();
    final AttributeMetaData[] datas = data.getAttributeDescriptions();
    for ( int i = 0; i < datas.length; i++ ) {
      final AttributeMetaData attributeMetaData = datas[i];
      if ( attributeMetaData.isTransient() ) {
        continue;
      }
      if ( isFiltered( attributeMetaData ) ) {
        continue;
      }

      final Object attValue = element.getAttribute( attributeMetaData.getNameSpace(), attributeMetaData.getName() );
      if ( attValue == null ) {
        continue;
      }
      final ResourceReference[] referencedResources =
          attributeMetaData.getReferencedResources( element, state.getMasterReport().getResourceManager(), attValue );
      for ( int j = 0; j < referencedResources.length; j++ ) {
        final ResourceReference reference = referencedResources[j];
        if ( reference.isLinked() ) {
          AbstractElementWriteHandler.logger.debug( "Linked Resource will not be copied into bundle: " + reference );
          continue;
        }

        final ResourceKey path = reference.getPath();
        final Object identifier = path.getIdentifier();
        if ( identifier instanceof String == false ) {
          AbstractElementWriteHandler.logger.warn( "Resource-Bundle-Key has no parseable path: " + path );
          continue;
        }

        final String identifierString = String.valueOf( identifier );
        final String relativePath = IOUtils.getInstance().createRelativePath( identifierString, contentBasePath );
        try {
          BundleUtilities.copyInto( bundle, relativePath, path, resourceManager );
        } catch ( Exception e ) {
          throw new BundleWriterException( "Failed to copy content from key " + path, e );
        }
        AbstractElementWriteHandler.logger.debug( "Copied " + path + " as " + relativePath );
      }
    }
  }

  protected boolean isFiltered( final AttributeMetaData attributeMetaData ) {
    if ( AttributeNames.Core.NAMESPACE.equals( attributeMetaData.getNameSpace() ) ) {
      if ( AttributeNames.Core.ELEMENT_TYPE.equals( attributeMetaData.getName() ) ) {
        return true;
      }
    }
    return false;
  }

  protected AttributeList createMainAttributes( final Element element, final XmlWriter writer ) {
    return createMainAttributes( element, writer, new AttributeList() );
  }

  protected AttributeList createMainAttributes( final Element element, final XmlWriter writer,
      final AttributeList attList ) {
    if ( element == null ) {
      throw new NullPointerException();
    }
    if ( writer == null ) {
      throw new NullPointerException();
    }
    if ( attList == null ) {
      throw new NullPointerException();
    }

    final ElementMetaData metaData = element.getElementType().getMetaData();
    final String[] attributeNamespaces = element.getAttributeNamespaces();
    for ( int i = 0; i < attributeNamespaces.length; i++ ) {
      final String namespace = attributeNamespaces[i];
      final String[] attributeNames = element.getAttributeNames( namespace );
      for ( int j = 0; j < attributeNames.length; j++ ) {
        final String name = attributeNames[j];
        final Object value = element.getAttribute( namespace, name );
        if ( value == null ) {
          continue;
        }

        final AttributeMetaData attrMeta = metaData.getAttributeDescription( namespace, name );
        if ( attrMeta == null ) {
          if ( value instanceof String ) {
            ensureNamespaceDefined( writer, attList, namespace );

            // preserve strings, but discard anything else. Until a attribute has a definition, we cannot
            // hope to understand the attribute's value. String-attributes can be expressed in XML easily,
            // and string is also how all unknown attributes are stored by the parser.
            attList.setAttribute( namespace, name, String.valueOf( value ) );
          }
          continue;
        }

        if ( attrMeta.isTransient() ) {
          continue;
        }
        if ( isFiltered( attrMeta ) ) {
          continue;
        }
        if ( attrMeta.isBulk() ) {
          continue;
        }

        ensureNamespaceDefined( writer, attList, namespace );
        try {
          final PropertyEditor propertyEditor = attrMeta.getEditor();
          if ( propertyEditor != null ) {
            propertyEditor.setValue( value );
            attList.setAttribute( namespace, name, propertyEditor.getAsText() );
          } else {
            final String attrValue = ConverterRegistry.toAttributeValue( value );
            attList.setAttribute( namespace, name, attrValue );
          }

        } catch ( BeanException e ) {
          AbstractElementWriteHandler.logger.warn( "Attribute '" + namespace + '|' + name
              + "' is not convertible with the bean-methods" );
        }
      }
    }
    return attList;
  }

  protected void ensureNamespaceDefined( final XmlWriter writer, final AttributeList attList, final String namespace ) {
    if ( writer.isNamespaceDefined( namespace ) == false && attList.isNamespaceUriDefined( namespace ) == false ) {
      final String prefix = ElementTypeRegistry.getInstance().getNamespacePrefix( namespace );
      if ( prefix != null ) {
        if ( writer.isNamespacePrefixDefined( prefix ) == false ) {
          attList.addNamespaceDeclaration( prefix, namespace );
          return;
        }
      }

      attList.addNamespaceDeclaration( "autoGenNs", namespace );
    }
  }

  protected void writeElementBody( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final Element element, final XmlWriter writer ) throws IOException, BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( element == null ) {
      throw new NullPointerException();
    }
    if ( writer == null ) {
      throw new NullPointerException();
    }

    StyleWriterUtility.writeStyleRule( BundleNamespaces.STYLE, "element-style", writer, element.getStyle() );
    writeStyleExpressions( bundle, state, element, writer );
    writeBulkAttributes( bundle, state, element, writer );
    writeAttributeExpressions( bundle, state, element, writer );
  }

  private void writeBulkAttributes( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final Element element, final XmlWriter writer ) throws IOException, BundleWriterException {
    final ElementMetaData metaData = element.getElementType().getMetaData();
    final String[] attributeNamespaces = element.getAttributeNamespaces();
    for ( int i = 0; i < attributeNamespaces.length; i++ ) {
      final String namespace = attributeNamespaces[i];
      final String[] attributeNames = element.getAttributeNames( namespace );
      for ( int j = 0; j < attributeNames.length; j++ ) {
        final String name = attributeNames[j];
        final Object value = element.getAttribute( namespace, name );
        if ( value == null ) {
          continue;
        }

        final AttributeMetaData attrMeta = metaData.getAttributeDescription( namespace, name );
        if ( attrMeta == null ) {
          continue;
        }

        if ( attrMeta.isTransient() ) {
          continue;
        }
        if ( isFiltered( attrMeta ) ) {
          continue;
        }
        if ( attrMeta.isBulk() == false ) {
          continue;
        }

        if ( "Resource".equals( attrMeta.getValueRole() ) ) {
          final AttributeList attList = new AttributeList();
          if ( attList.isNamespaceUriDefined( namespace ) == false && writer.isNamespaceDefined( namespace ) == false ) {
            attList.addNamespaceDeclaration( "autoGenNS", namespace );
          }

          if ( value instanceof URL ) {
            attList.setAttribute( AttributeNames.Core.NAMESPACE, "resource-type", "url" );
            writer.writeTag( namespace, attrMeta.getName(), attList, XmlWriter.OPEN );
            writer.writeTextNormalized( String.valueOf( value ), true );
            writer.writeCloseTag();
          } else if ( value instanceof ResourceKey ) {
            try {
              final ResourceKey key = (ResourceKey) value;
              final ResourceManager resourceManager = bundle.getResourceManager();
              final ResourceKey bundleKey = bundle.getBundleKey().getParent();
              final String serializedKey = resourceManager.serialize( bundleKey, key );
              attList.setAttribute( AttributeNames.Core.NAMESPACE, "resource-type", "resource-key" );
              writer.writeTag( namespace, attrMeta.getName(), attList, XmlWriter.OPEN );
              writer.writeTextNormalized( serializedKey, true );
              writer.writeCloseTag();
            } catch ( ResourceException re ) {
              logger.error( "Could not serialize the ResourceKey: " + re.getMessage(), re );
              throw new IOException( "Could not serialize the ResourceKey: ", re );
            }
          } else if ( value instanceof File ) {
            attList.setAttribute( AttributeNames.Core.NAMESPACE, "resource-type", "file" );
            writer.writeTag( namespace, attrMeta.getName(), attList, XmlWriter.OPEN );
            writer.writeTextNormalized( String.valueOf( value ), true );
            writer.writeCloseTag();
          } else if ( value instanceof String ) {
            attList.setAttribute( AttributeNames.Core.NAMESPACE, "resource-type", "local-ref" );
            writer.writeTag( namespace, attrMeta.getName(), attList, XmlWriter.OPEN );
            writer.writeTextNormalized( String.valueOf( value ), true );
            writer.writeCloseTag();
          } else {
            logger.warn( "Unknown value-type in resource-attribute " + namespace + ":" + name );
          }

          continue;
        }

        if ( "Expression".equals( attrMeta.getValueRole() ) && value instanceof Expression ) {
          // write attribute-expressions.
          final AttributeList attList = new AttributeList();
          attList.setAttribute( BundleNamespaces.LAYOUT, "attribute-namespace", namespace );
          attList.setAttribute( BundleNamespaces.LAYOUT, "attribute-name", name );
          ExpressionWriterUtility.writeExpressionCore( bundle, state, (Expression) value, writer,
              BundleNamespaces.LAYOUT, "expression", attList );
          continue;
        }

        try {
          final PropertyEditor propertyEditor = attrMeta.getEditor();
          if ( propertyEditor != null ) {

            propertyEditor.setValue( value );
            final String text = propertyEditor.getAsText();

            final AttributeList attList = new AttributeList();
            if ( attList.isNamespaceUriDefined( namespace ) == false && writer.isNamespaceDefined( namespace ) == false ) {
              attList.addNamespaceDeclaration( "autoGenNS", namespace );
            }
            writer.writeTag( namespace, attrMeta.getName(), attList, XmlWriter.OPEN );
            writer.writeTextNormalized( text, true );
            writer.writeCloseTag();
          } else {
            final String attrValue = ConverterRegistry.toAttributeValue( value );
            final AttributeList attList = new AttributeList();
            if ( attList.isNamespaceUriDefined( namespace ) == false && writer.isNamespaceDefined( namespace ) == false ) {
              attList.addNamespaceDeclaration( "autoGenNS", namespace );
            }
            writer.writeTag( namespace, attrMeta.getName(), attList, XmlWriter.OPEN );
            writer.writeTextNormalized( attrValue, true );
            writer.writeCloseTag();
          }

        } catch ( BeanException e ) {
          AbstractElementWriteHandler.logger.warn( "Attribute '" + namespace + '|' + name
              + "' is not convertible with the bean-methods" );
        }
      }
    }
  }

  protected void writeAttributeExpressions( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final Element element, final XmlWriter writer ) throws IOException, BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( element == null ) {
      throw new NullPointerException();
    }
    if ( writer == null ) {
      throw new NullPointerException();
    }

    // write attribute-expressions.
    final String[] attributeNamespaces = element.getAttributeExpressionNamespaces();
    for ( int i = 0; i < attributeNamespaces.length; i++ ) {
      final String namespace = attributeNamespaces[i];
      final String[] attributeNames = element.getAttributeExpressionNames( namespace );
      for ( int j = 0; j < attributeNames.length; j++ ) {
        final String name = attributeNames[j];
        final AttributeList attList = new AttributeList();
        attList.setAttribute( BundleNamespaces.LAYOUT, "namespace", namespace );
        attList.setAttribute( BundleNamespaces.LAYOUT, "name", name );
        final Expression ex = element.getAttributeExpression( namespace, name );
        ExpressionWriterUtility.writeExpressionCore( bundle, state, ex, writer, BundleNamespaces.LAYOUT,
            "attribute-expression", attList );
      }
    }
  }

  protected void writeStyleExpressions( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final Element element, final XmlWriter writer ) throws IOException, BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( element == null ) {
      throw new NullPointerException();
    }
    if ( writer == null ) {
      throw new NullPointerException();
    }

    // write style expressions.
    final Map<StyleKey, Expression> styleExpressions = element.getStyleExpressions();
    for ( final Map.Entry<StyleKey, Expression> entry : styleExpressions.entrySet() ) {
      final StyleKey key = entry.getKey();
      final Expression ex = entry.getValue();
      ExpressionWriterUtility.writeStyleExpression( bundle, state, ex, writer, key, BundleNamespaces.LAYOUT,
          "style-expression" );
    }
  }

  protected void writeChildElements( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final XmlWriter xmlWriter, final Section section ) throws IOException, BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( xmlWriter == null ) {
      throw new NullPointerException();
    }
    if ( section == null ) {
      throw new NullPointerException();
    }
    if ( section instanceof Band ) {
      // A band can only contain other, non-root level bands and subreports. Any root-level band encountered
      // should have been converted into a normal band by the author. Instead of silently failing or trying
      // to fix the bad code, let's fail with a clean exception and let the developer fix their code.
      for ( final Element e : section ) {
        if ( e instanceof RootLevelBand ) {
          throw new BundleWriterException( "This report cannot be saved. A normal band cannot contain other "
              + "root-level bands as children unless they are contained in a subreport." );
        }
        if ( e instanceof Section && e instanceof Band == false && e instanceof SubReport == false ) {
          // must be a group, or a group-body or a master-report. This is Invalid!
          throw new BundleWriterException( String.format( "This report cannot be saved. A normal band can only "
              + "contain other data elements, bands or subreports as children. You cannot add "
              + "structural elements such as '%s' here.", e.getElementTypeName() ) );
        }
      }
    }

    for ( final Element e : section ) {
      final BundleElementWriteHandler writeHandler = BundleElementRegistry.getInstance().getWriteHandler( e );
      writeHandler.writeElement( bundle, state, xmlWriter, e );
    }
  }

  protected void writeRootSubReports( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final XmlWriter xmlWriter, final RootLevelBand rootLevelBand ) throws IOException, BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( xmlWriter == null ) {
      throw new NullPointerException();
    }
    if ( rootLevelBand == null ) {
      throw new NullPointerException();
    }

    final SubReport[] reports = rootLevelBand.getSubReports();
    for ( int i = 0; i < reports.length; i++ ) {
      writeSubReport( bundle, state, xmlWriter, reports[i] );
    }
  }

  protected void writeSubReport( final WriteableDocumentBundle bundle, final BundleWriterState state,
      final XmlWriter xmlWriter, final SubReport subReport ) throws IOException, BundleWriterException {
    if ( bundle == null ) {
      throw new NullPointerException();
    }
    if ( state == null ) {
      throw new NullPointerException();
    }
    if ( xmlWriter == null ) {
      throw new NullPointerException();
    }
    if ( subReport == null ) {
      throw new NullPointerException();
    }

    final String absolutePath = computePath( state );
    final String directory = BundleUtilities.getUniqueName( bundle, absolutePath + "/subreport{0}" );
    bundle.createDirectoryEntry( directory, "application/vnd.pentaho.reporting.classic.subreport" );
    final String dirRelative = IOUtils.getInstance().createRelativePath( directory, absolutePath + "/." );
    final BundleWriterState subReportState = new BundleWriterState( state, subReport, dirRelative + '/' );
    state.getBundleWriter().writeSubReport( bundle, subReportState );

    final ParameterMapping[] inputMappings = subReport.getInputMappings();
    final ParameterMapping[] outputMappings = subReport.getExportMappings();
    ElementMetaData metaData = subReport.getMetaData();
    final String tagName = metaData.getName();
    final String namespace = metaData.getNamespace();
    if ( inputMappings.length == 0 && outputMappings.length == 0 ) {
      xmlWriter.writeTag( namespace, tagName, "href", '/' + subReportState.getFileName() + "content.xml",
          XmlWriterSupport.CLOSE );
    } else {
      xmlWriter.writeTag( namespace, tagName, "href", '/' + subReportState.getFileName() + "content.xml",
          XmlWriterSupport.OPEN );

      for ( int i = 0; i < inputMappings.length; i++ ) {
        final ParameterMapping mapping = inputMappings[i];
        final AttributeList attrs = new AttributeList();
        attrs.setAttribute( BundleNamespaces.LAYOUT, "master-fieldname", mapping.getName() );
        attrs.setAttribute( BundleNamespaces.LAYOUT, "detail-fieldname", mapping.getAlias() );
        xmlWriter.writeTag( BundleNamespaces.LAYOUT, "input-parameter", attrs, XmlWriterSupport.CLOSE );
      }

      for ( int i = 0; i < outputMappings.length; i++ ) {
        final ParameterMapping mapping = outputMappings[i];
        final AttributeList attrs = new AttributeList();
        attrs.setAttribute( BundleNamespaces.LAYOUT, "master-fieldname", mapping.getName() );
        attrs.setAttribute( BundleNamespaces.LAYOUT, "detail-fieldname", mapping.getAlias() );
        xmlWriter.writeTag( BundleNamespaces.LAYOUT, "output-parameter", attrs, XmlWriterSupport.CLOSE );
      }
      xmlWriter.writeCloseTag();
    }
  }

  private String computePath( final BundleWriterState state ) {
    final String absolutePathWithDummy = IOUtils.getInstance().getAbsolutePath( "dummy", state.getFileName() );
    final String absolutePath = absolutePathWithDummy.substring( 0, absolutePathWithDummy.length() - "dummy".length() );
    if ( absolutePath.endsWith( "/" ) ) {
      return absolutePath.substring( 0, absolutePath.length() - 1 );
    }
    return absolutePath;
  }
}
