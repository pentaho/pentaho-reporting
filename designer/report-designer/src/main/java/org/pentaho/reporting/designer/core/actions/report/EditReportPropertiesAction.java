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

package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.metadata.DocumentMetaDataDialog;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentMetaData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class EditReportPropertiesAction extends AbstractReportContextAction {
  public EditReportPropertiesAction() {
    putValue( Action.NAME, ActionMessages.getString( "EditReportPropertiesAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "EditReportPropertiesAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditReportPropertiesAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "EditReportPropertiesAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }
    final ReportDesignerContext context = getReportDesignerContext();
    final Component parent = context.getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final DocumentMetaDataDialog dialog;
    if ( window instanceof JDialog ) {
      dialog = new DocumentMetaDataDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new DocumentMetaDataDialog( (JFrame) window );
    } else {
      dialog = new DocumentMetaDataDialog();
    }

    try {
      final MasterReport report = activeContext.getContextRoot();
      final DocumentBundle bundle = report.getBundle();
      final DocumentMetaData oldMetaData = (DocumentMetaData) bundle.getMetaData().clone();
      final DocumentMetaData result = dialog.performEdit( oldMetaData,
        report.getResourceManager(),
        report.getDefinitionSource() );

      if ( result == null ) {
        return;
      }

      final MetaDataEditUndoEntry undoEntry = new MetaDataEditUndoEntry( oldMetaData, result );
      undoEntry.redo( activeContext );
      activeContext.getUndo().addChange( ActionMessages.getString( "EditReportPropertiesAction.Text" ), undoEntry );
    } catch ( CloneNotSupportedException cne ) {
      // should not happen
      UncaughtExceptionsModel.getInstance().addException( cne );
    }
  }

  private static class MetaDataEditUndoEntry implements UndoEntry {
    private DocumentMetaData oldMetaData;
    private DocumentMetaData newMetaData;

    private MetaDataEditUndoEntry( final DocumentMetaData oldMetaData,
                                   final DocumentMetaData newMetaData ) {
      if ( oldMetaData == null ) {
        throw new NullPointerException();
      }
      if ( newMetaData == null ) {
        throw new NullPointerException();
      }
      this.oldMetaData = oldMetaData;
      this.newMetaData = newMetaData;
    }

    public void undo( final ReportDocumentContext renderContext ) {
      final MasterReport report = renderContext.getContextRoot();
      final WriteableDocumentBundle bundle = (WriteableDocumentBundle) report.getBundle();
      final WriteableDocumentMetaData metaData = bundle.getWriteableDocumentMetaData();
      metaData.setBundleAttribute
        ( ODFMetaAttributeNames.Meta.NAMESPACE, ODFMetaAttributeNames.Meta.KEYWORDS,
          oldMetaData.getBundleAttribute( ODFMetaAttributeNames.Meta.NAMESPACE, ODFMetaAttributeNames.Meta.KEYWORDS ) );
      metaData.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.CREATOR,
          oldMetaData.getBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE,
            ODFMetaAttributeNames.DublinCore.CREATOR ) );
      metaData.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.DESCRIPTION,
          oldMetaData.getBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE,
            ODFMetaAttributeNames.DublinCore.DESCRIPTION ) );
      metaData.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.SUBJECT,
          oldMetaData.getBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE,
            ODFMetaAttributeNames.DublinCore.SUBJECT ) );
      metaData.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.TITLE,
          oldMetaData
            .getBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.TITLE ) );
      report.notifyNodePropertiesChanged();
    }

    public void redo( final ReportDocumentContext renderContext ) {
      final MasterReport report = renderContext.getContextRoot();
      final WriteableDocumentBundle bundle = (WriteableDocumentBundle) report.getBundle();
      final WriteableDocumentMetaData metaData = bundle.getWriteableDocumentMetaData();
      metaData.setBundleAttribute
        ( ODFMetaAttributeNames.Meta.NAMESPACE, ODFMetaAttributeNames.Meta.KEYWORDS,
          newMetaData.getBundleAttribute( ODFMetaAttributeNames.Meta.NAMESPACE, ODFMetaAttributeNames.Meta.KEYWORDS ) );
      metaData.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.CREATOR,
          newMetaData.getBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE,
            ODFMetaAttributeNames.DublinCore.CREATOR ) );
      metaData.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.DESCRIPTION,
          newMetaData.getBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE,
            ODFMetaAttributeNames.DublinCore.DESCRIPTION ) );
      metaData.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.SUBJECT,
          newMetaData.getBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE,
            ODFMetaAttributeNames.DublinCore.SUBJECT ) );
      metaData.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.TITLE,
          newMetaData
            .getBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.TITLE ) );
      report.notifyNodePropertiesChanged();
    }

    public UndoEntry merge( final UndoEntry newEntry ) {
      return null;
    }
  }

}
