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

package org.pentaho.reporting.designer.core.actions.elements;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.filter.MessageFormatSupport;
import org.pentaho.reporting.engine.classic.core.filter.types.DateFieldType;
import org.pentaho.reporting.engine.classic.core.filter.types.MessageType;
import org.pentaho.reporting.engine.classic.core.filter.types.NumberFieldType;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import java.awt.event.ActionEvent;
import java.awt.Image;
import javax.swing.Action;
import javax.swing.ImageIcon;
import java.beans.BeanInfo;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class MorphAction extends AbstractElementSelectionAction implements SettingsListener {
  private static final String CORE_NAMESPACE = AttributeNames.Core.NAMESPACE;
  private static final String ELEMENT_TYPE_ATTRIBUE = AttributeNames.Core.ELEMENT_TYPE;
  private static final String FIELD_ATTRIBUTE = AttributeNames.Core.FIELD;
  private static final String VALUE_ATTRIBUTE = AttributeNames.Core.VALUE;
  private static final String FORMAT_ATTRIBUTE = AttributeNames.Core.FORMAT_STRING;

  private final ElementMetaData metaData;
  private ElementType targetElementType;

  public MorphAction( final ElementType elementType ) {
    targetElementType = elementType;
    this.metaData = elementType.getMetaData();
    putValue( Action.NAME, metaData.getDisplayName( Locale.getDefault() ) );

    final Image image = metaData.getIcon( Locale.getDefault(), BeanInfo.ICON_COLOR_32x32 );
    if ( image != null ) {
      putValue( Action.SMALL_ICON, new ImageIcon( image ) );
    }

    settingsChanged();
    WorkspaceSettings.getInstance().addSettingsListener( this );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  public void settingsChanged() {
    setVisible( WorkspaceSettings.getInstance().isVisible( metaData ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final DocumentContextSelectionModel selectionModel1 = getSelectionModel();
    if ( selectionModel1 == null ) {
      return;
    }
    final List<Element> visualElements = selectionModel1.getSelectedElementsOfType( Element.class );
    if ( visualElements.isEmpty() ) {
      return;
    }

    final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();
    final ElementMetaData targetMetaData = targetElementType.getMetaData();
    for ( Element visualElement : visualElements ) {
      if ( visualElement instanceof Section ) {
        continue;
      }
      if ( ObjectUtilities.equal( visualElement.getElementTypeName(), targetElementType.getMetaData().getName() ) ) {
        continue;
      }
      if ( targetMetaData.getReportElementType().equals( ElementMetaData.TypeClassification.SUBREPORT )
        && !visualElement.getMetaData().getReportElementType().equals( ElementMetaData.TypeClassification.SUBREPORT ) ) {
        continue;
      }

      final ReportAttributeMap oldAttributes = visualElement.getAttributes();

      final ElementMetaData data = visualElement.getMetaData();
      final AttributeMetaData[] datas = data.getAttributeDescriptions();
      for ( int j = 0; j < datas.length; j++ ) {
        final AttributeMetaData metaData = datas[ j ];
        final AttributeMetaData attributeMetaData =
          targetMetaData.getAttributeDescription( metaData.getNameSpace(), metaData.getName() );
        if ( attributeMetaData == null ) {
          visualElement.setAttribute( metaData.getNameSpace(), metaData.getName(), null );
          visualElement.setAttributeExpression( metaData.getNameSpace(), metaData.getName(), null );
        }
      }

      visualElement.setElementType( targetElementType );

      // -------------------- manipulate the value and field attributes so that we preserve as much as possible
      // ---------------------
      // get the source and destination types
      final ElementType srcType = (ElementType) oldAttributes.getAttribute( CORE_NAMESPACE, ELEMENT_TYPE_ATTRIBUE );
      // Check if the source-type is a message field. In that case, we have to map the pattern into a field and
      // format definition
      if ( srcType instanceof MessageType ) {
        final String message = (String) oldAttributes.getAttribute( CORE_NAMESPACE, VALUE_ATTRIBUTE );
        visualElement.setAttribute( CORE_NAMESPACE, VALUE_ATTRIBUTE, null );
        try {

          final MessageFormatSupport fmt = new MessageFormatSupport();
          fmt.setFormatString( message );
          final String rawFormat = fmt.getCompiledFormat();
          final MessageFormat msg = new MessageFormat( rawFormat );
          final Format[] subFormats = msg.getFormats();

          if ( targetElementType instanceof DateFieldType ) {
            final int sdf = findFirstDateFormat( subFormats );
            if ( sdf != -1 ) {
              final SimpleDateFormat df = (SimpleDateFormat) subFormats[ sdf ];

              final String[] fields = fmt.getFields();
              visualElement.setAttribute( CORE_NAMESPACE, FIELD_ATTRIBUTE, fields[ sdf ] );
              visualElement.setAttribute( CORE_NAMESPACE, FORMAT_ATTRIBUTE, df.toPattern() );
            }
          } else if ( targetElementType instanceof NumberFieldType ) {
            final int sdf = findFirstNumberFormat( subFormats );
            if ( sdf != -1 ) {
              final DecimalFormat df = (DecimalFormat) subFormats[ sdf ];

              final String[] fields = fmt.getFields();
              visualElement.setAttribute( CORE_NAMESPACE, FIELD_ATTRIBUTE, fields[ sdf ] );
              visualElement.setAttribute( CORE_NAMESPACE, FORMAT_ATTRIBUTE, df.toPattern() );
            }
          } else {
            final String[] fields = fmt.getFields();
            if ( fields.length > 0 ) {
              visualElement.setAttribute( CORE_NAMESPACE, FIELD_ATTRIBUTE, fields[ 0 ] );
            } else {
              visualElement.setAttribute( CORE_NAMESPACE, VALUE_ATTRIBUTE, message );
            }
          }
        } catch ( Exception ex ) {
          visualElement.setAttribute( CORE_NAMESPACE, VALUE_ATTRIBUTE, message );
        }
      } else if ( targetElementType instanceof MessageType ) {
        // validate that these are the correct combination to use this enhanced morphing.
        // This stuff only applies if we're morphing TO a Message Type field
        final String srcField = (String) oldAttributes
          .getAttribute( CORE_NAMESPACE, FIELD_ATTRIBUTE ); // and morphing FROM Number, Date, or Text Type field
        final String formatString = (String) oldAttributes.getAttribute( CORE_NAMESPACE, FORMAT_ATTRIBUTE );
        final StringBuilder buffer = new StringBuilder();
        buffer.append( "$(" ).append( srcField ); //$NON-NLS-1$
        if ( srcType instanceof NumberFieldType ) {
          buffer.append( ", number" ); //$NON-NLS-1$
          if ( formatString != null && formatString.length() > 0 ) {
            buffer.append( ", " ).append( formatString ); //$NON-NLS-1$
          }
        } else if ( srcType instanceof DateFieldType ) {
          buffer.append( ", date" ); //$NON-NLS-1$
          if ( formatString != null && formatString.length() > 0 ) {
            buffer.append( ", " ).append( formatString ); //$NON-NLS-1$
          }
        }
        buffer.append( ")" ); //$NON-NLS-1$
        visualElement.setAttribute( CORE_NAMESPACE, VALUE_ATTRIBUTE, buffer.toString() );
      }

      final ReportAttributeMap newAttributes = visualElement.getAttributes();
      undos.add( new MorphUndoEntry( visualElement.getObjectID(), oldAttributes, newAttributes ) );
    }

    getActiveContext().getUndo().addChange( ActionMessages.getString( "MorphAction.UndoName" ),
      new CompoundUndoEntry( undos.toArray( new UndoEntry[ undos.size() ] ) ) );
  }

  private int findFirstDateFormat( final Format[] formats ) {
    for ( int j = 0; j < formats.length; j++ ) {
      final Format format = formats[ j ];
      if ( format instanceof SimpleDateFormat ) {
        return j;
      }
    }
    return -1;
  }

  private int findFirstNumberFormat( final Format[] formats ) {
    for ( int j = 0; j < formats.length; j++ ) {
      final Format format = formats[ j ];
      if ( format instanceof DecimalFormat ) {
        return j;
      }
    }
    return -1;
  }

  private static class MorphUndoEntry implements UndoEntry {
    private InstanceID target;
    private ReportAttributeMap oldAttributes;
    private ReportAttributeMap newAttributes;

    private MorphUndoEntry( final InstanceID target,
                            final ReportAttributeMap oldAttributes,
                            final ReportAttributeMap newAttributes ) {
      this.target = target;
      this.oldAttributes = oldAttributes;
      this.newAttributes = newAttributes;
    }

    public void undo( final ReportDocumentContext renderContext ) {
      final Element element = (Element) ModelUtility.findElementById( renderContext.getReportDefinition(), target );
      final String[] attributeNamespaces = element.getAttributeNamespaces();
      for ( int i = 0; i < attributeNamespaces.length; i++ ) {
        final String attributeNamespace = attributeNamespaces[ i ];
        final String[] attributeNames = element.getAttributeNames( attributeNamespace );
        for ( int j = 0; j < attributeNames.length; j++ ) {
          final String attributeName = attributeNames[ j ];
          element.setAttribute( attributeNamespace, attributeName, null, false );
        }
      }

      final String[] namespaces = oldAttributes.getNameSpaces();
      for ( int i = 0; i < namespaces.length; i++ ) {
        final String attributeNamespace = namespaces[ i ];
        final String[] attributeNames = oldAttributes.getNames( attributeNamespace );
        for ( int j = 0; j < attributeNames.length; j++ ) {
          final String attributeName = attributeNames[ j ];
          element.setAttribute( attributeNamespace, attributeName,
            oldAttributes.getAttribute( attributeNamespace, attributeName ), false );
        }
      }
      element.notifyNodePropertiesChanged();
    }

    public void redo( final ReportDocumentContext renderContext ) {
      final Element element = (Element) ModelUtility.findElementById( renderContext.getReportDefinition(), target );
      final String[] attributeNamespaces = element.getAttributeNamespaces();
      for ( int i = 0; i < attributeNamespaces.length; i++ ) {
        final String attributeNamespace = attributeNamespaces[ i ];
        final String[] attributeNames = element.getAttributeNames( attributeNamespace );
        for ( int j = 0; j < attributeNames.length; j++ ) {
          final String attributeName = attributeNames[ j ];
          element.setAttribute( attributeNamespace, attributeName, null, false );
        }
      }

      final String[] namespaces = newAttributes.getNameSpaces();
      for ( int i = 0; i < namespaces.length; i++ ) {
        final String attributeNamespace = namespaces[ i ];
        final String[] attributeNames = newAttributes.getNames( attributeNamespace );
        for ( int j = 0; j < attributeNames.length; j++ ) {
          final String attributeName = attributeNames[ j ];
          element.setAttribute( attributeNamespace, attributeName,
            newAttributes.getAttribute( attributeNamespace, attributeName ), false );
        }
      }
      element.notifyNodePropertiesChanged();
    }

    public UndoEntry merge( final UndoEntry newEntry ) {
      return null;
    }
  }


}
