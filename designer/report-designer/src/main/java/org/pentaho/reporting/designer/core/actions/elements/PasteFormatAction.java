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
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.dnd.ClipboardManager;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ReportAttributeMap;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.StyleMetaData;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public final class PasteFormatAction extends AbstractElementSelectionAction implements ChangeListener {
  private static class PasteFormatUndoEntry implements UndoEntry {
    private InstanceID element;
    private ReportAttributeMap<Object> oldAttributes;
    private ReportAttributeMap<Object> newAttributes;
    private Object[] oldStyleData;
    private Object[] newStyleData;

    private PasteFormatUndoEntry( final InstanceID element,
                                  final ReportAttributeMap<Object> oldAttributes,
                                  final ReportAttributeMap<Object> newAttributes,
                                  final Object[] oldStyleData,
                                  final Object[] newStyleData ) {
      this.element = element;
      this.oldAttributes = oldAttributes.clone();
      this.newAttributes = newAttributes.clone();
      this.oldStyleData = oldStyleData.clone();
      this.newStyleData = newStyleData.clone();
    }

    public void undo( final ReportDocumentContext renderContext ) {
      final Element target = (Element) ModelUtility.findElementById( renderContext.getReportDefinition(), element );

      final ElementStyleSheet styleSheet = target.getStyle();
      final StyleKey[] keys = StyleKey.getDefinedStyleKeys();
      for ( int i = 0; i < oldStyleData.length; i++ ) {
        final Object o = oldStyleData[ i ];
        if ( o != null ) {
          styleSheet.setStyleProperty( keys[ i ], o );
        }
      }

      final String[] namespaces = oldAttributes.getNameSpaces();
      for ( int i = 0; i < namespaces.length; i++ ) {
        final String namespace = namespaces[ i ];
        final String[] names = oldAttributes.getNames( namespace );
        for ( int j = 0; j < names.length; j++ ) {
          final String name = names[ j ];
          target.setAttribute( namespace, name, oldAttributes.getAttribute( namespace, name ), false );
        }
      }

      target.notifyNodePropertiesChanged();
    }

    public void redo( final ReportDocumentContext renderContext ) {
      final Element target = (Element) ModelUtility.findElementById( renderContext.getReportDefinition(), element );

      final ElementStyleSheet styleSheet = target.getStyle();
      final StyleKey[] keys = StyleKey.getDefinedStyleKeys();
      for ( int i = 0; i < newStyleData.length; i++ ) {
        final Object o = newStyleData[ i ];
        if ( o != null ) {
          styleSheet.setStyleProperty( keys[ i ], o );
        }
      }

      final String[] namespaces = newAttributes.getNameSpaces();
      for ( int i = 0; i < namespaces.length; i++ ) {
        final String namespace = namespaces[ i ];
        final String[] names = newAttributes.getNames( namespace );
        for ( int j = 0; j < names.length; j++ ) {
          final String name = names[ j ];
          target.setAttribute( namespace, name, newAttributes.getAttribute( namespace, name ), false );
        }
      }

      target.notifyNodePropertiesChanged();
    }

    public UndoEntry merge( final UndoEntry newEntry ) {
      return null;
    }
  }

  private enum ClipboardStatus {
    EMPTY,           // Nothing valid in the clipboard
    UNKNOWN,         // Not checked, as we have no insertation point
    GENERIC_ELEMENT  // its a generic element, insertation point has been checked to be valid.
  }

  private ClipboardStatus clipboardStatus;

  private boolean selectionActive;

  public PasteFormatAction() {
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getPasteIcon() );
    putValue( Action.NAME, ActionMessages.getString( "PasteFormatAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "PasteFormatAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "PasteFormatAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "PasteFormatAction.Accelerator" ) );

    setEnabled( false );

    ClipboardManager.getManager().addChangeListener( this );

    // update from system clipboard status
    stateChanged( null );
  }

  protected void selectedElementPropertiesChanged( final ReportModelEvent event ) {
  }

  protected void updateSelection() {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      setSelectionActive( false );
      return;
    }

    final Object rawLeadSelection = activeContext.getSelectionModel().getLeadSelection();
    if ( rawLeadSelection == null ) {
      setSelectionActive( false );
      return;
    }
    setSelectionActive( true );
  }

  public void setSelectionActive( final boolean selectionActive ) {
    this.selectionActive = selectionActive;
    if ( selectionActive ) {
      setEnabled( clipboardStatus == ClipboardStatus.GENERIC_ELEMENT );
    } else {
      setEnabled( false );
    }
  }


  public ClipboardStatus getClipboardStatus() {
    return clipboardStatus;
  }

  public void setClipboardStatus( final ClipboardStatus clipboardStatus ) {
    this.clipboardStatus = clipboardStatus;
    if ( clipboardStatus != ClipboardStatus.GENERIC_ELEMENT ) {
      setEnabled( false );
    } else {
      setEnabled( selectionActive );
    }
  }


  public void stateChanged( final ChangeEvent e ) {
    if ( ClipboardManager.getManager().isDataAvailable() ) {
      setClipboardStatus( ClipboardStatus.GENERIC_ELEMENT );
    } else {
      setClipboardStatus( ClipboardStatus.EMPTY );
    }
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    final DocumentContextSelectionModel selectionModel1 = getSelectionModel();
    if ( selectionModel1 == null ) {
      return;
    }
    final List<Element> visualElements = selectionModel1.getSelectedElementsOfType( Element.class );
    if ( visualElements.isEmpty() ) {
      return;
    }

    if ( ClipboardManager.getManager().isDataAvailable() == false ) {
      return;
    }
    try {

      final Object[] data1 = ClipboardManager.getManager().getContents();
      if ( data1.length == 0 || data1[ 0 ] instanceof Element == false ) {
        return;
      }

      final Element data = (Element) data1[ 0 ];

      // copy all styles ..
      final ElementStyleSheet styleSheet = data.getStyle();
      final StyleKey[] definedPropertyNamesArray = styleSheet.getDefinedPropertyNamesArray();

      final String elementType = data.getElementTypeName();
      final Object formatString = data.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING );

      final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();
      for ( Element element : visualElements ) {
        final ElementMetaData metaData = element.getMetaData();
        final ElementStyleSheet elementStyleSheet = element.getStyle();

        final Object[] oldStyleData = new Object[ StyleKey.getDefinedStyleKeyCount() ];
        final Object[] newStyleData = new Object[ StyleKey.getDefinedStyleKeyCount() ];
        for ( int j = 0; j < definedPropertyNamesArray.length; j++ ) {
          final StyleKey styleKey = definedPropertyNamesArray[ j ];
          if ( styleKey == null ) {
            continue;
          }
          final StyleMetaData styleDescr = metaData.getStyleDescription( styleKey );
          if ( styleDescr == null ) {
            // skip if the target element does not have that style ..
            continue;
          }

          if ( isFiltered( styleKey, styleDescr ) ) {
            continue;
          }
          oldStyleData[ styleKey.identifier ] = elementStyleSheet.getStyleProperty( styleKey, null );
          final Object newValue = styleSheet.getStyleProperty( styleKey );
          newStyleData[ styleKey.identifier ] = newValue;
          elementStyleSheet.setStyleProperty( styleKey, newValue );
        }

        final ReportAttributeMap<Object> oldAttributes = new ReportAttributeMap<Object>();
        final ReportAttributeMap<Object> newAttributes = new ReportAttributeMap<Object>();
        if ( ObjectUtilities.equal( elementType, element.getElementTypeName() ) ) {
          final Object attribute =
            element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING );
          oldAttributes.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING, attribute );
          newAttributes.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING, formatString );

          element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING, formatString );
        }

        final PasteFormatUndoEntry undoEntry =
          new PasteFormatUndoEntry( element.getObjectID(), oldAttributes, newAttributes, oldStyleData, newStyleData );
        undos.add( undoEntry );
      }
      getActiveContext().getUndo().addChange( ActionMessages.getString( "PasteFormatAction.UndoName" ),
        new CompoundUndoEntry( undos.toArray( new UndoEntry[ undos.size() ] ) ) );
    } catch ( final UnsupportedFlavorException e1 ) {
      UncaughtExceptionsModel.getInstance().addException( e1 );
    } catch ( IOException e1 ) {
      UncaughtExceptionsModel.getInstance().addException( e1 );
    }
  }

  private boolean isFiltered( final StyleKey styleKey, final StyleMetaData styleDescr ) {
    if ( styleDescr.isDeprecated() ) {
      return true;
    }
    if ( ElementStyleKeys.POS_X.equals( styleKey ) ) {
      return true;
    }
    if ( ElementStyleKeys.POS_Y.equals( styleKey ) ) {
      return true;
    }
    if ( ElementStyleKeys.MIN_WIDTH.equals( styleKey ) ) {
      return true;
    }
    if ( ElementStyleKeys.MIN_HEIGHT.equals( styleKey ) ) {
      return true;
    }
    if ( ElementStyleKeys.WIDTH.equals( styleKey ) ) {
      return true;
    }
    if ( ElementStyleKeys.HEIGHT.equals( styleKey ) ) {
      return true;
    }
    if ( ElementStyleKeys.MAX_WIDTH.equals( styleKey ) ) {
      return true;
    }
    if ( ElementStyleKeys.MAX_HEIGHT.equals( styleKey ) ) {
      return true;
    }
    if ( BandStyleKeys.LAYOUT.equals( styleKey ) ) {
      return true;
    }
    return false;
  }
}
