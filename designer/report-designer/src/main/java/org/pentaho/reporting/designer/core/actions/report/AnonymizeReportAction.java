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

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.Anonymizer;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.filter.types.MessageType;
import org.pentaho.reporting.engine.classic.core.util.AbstractStructureVisitor;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentMetaData;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AnonymizeReportAction extends AbstractReportContextAction {
  private static class ConvertReportTask extends AbstractStructureVisitor implements Runnable {
    private MasterReport report;
    private Anonymizer anonymizer;

    private ConvertReportTask( final MasterReport report ) {
      this.report = report;
      this.anonymizer = new Anonymizer();
    }

    public void run() {
      try {
        super.inspect( report );
        final DocumentBundle bundle = report.getBundle();
        final DocumentMetaData metaData = bundle.getMetaData();
        if ( metaData instanceof WriteableDocumentMetaData ) {
          WriteableDocumentMetaData w = (WriteableDocumentMetaData) metaData;
          w.setBundleAttribute( ODFMetaAttributeNames.Meta.NAMESPACE, ODFMetaAttributeNames.Meta.KEYWORDS, null );
          w.setBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.CREATOR,
            null );
          w.setBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE,
            ODFMetaAttributeNames.DublinCore.DESCRIPTION, null );
          w.setBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.SUBJECT,
            null );
          w.setBundleAttribute( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.TITLE,
            null );
        }
      } catch ( Exception e ) {
        UncaughtExceptionsModel.getInstance().addException( e );
      }
    }

    protected void inspectElement( final ReportElement element ) {
      try {
        if ( element.getElementType() instanceof LabelType ) {
          final Object attribute = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
          element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE,
            anonymizer.anonymize( attribute ) );
        }
        if ( element.getElementType() instanceof MessageType ) {
          final Object attribute = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE );
          element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE,
            anonymizer.anonymizeMessage( attribute ) );
        }
      } catch ( BeanException e ) {
        throw new RuntimeException( e );
      }

    }

  }

  public AnonymizeReportAction() {
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getLayoutBandsIcon() );
    putValue( Action.NAME, ActionMessages.getString( "AnonymizeReportAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "AnonymizeReportAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "AnonymizeReportAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "AnonymizeReportAction.Accelerator" ) );
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getActiveContext();
    if ( activeContext == null ) {
      return;
    }

    final Thread thread = new Thread( new ConvertReportTask( getActiveContext().getContextRoot() ) );
    thread.setName( "AnonymizeReport-Worker" );// NON-NLS
    thread.setDaemon( true );
    BackgroundCancellableProcessHelper.executeProcessWithCancelDialog( thread, null,
      getReportDesignerContext().getView().getParent(), ActionMessages.getString( "AnonymizeReportAction.TaskTitle" ) );

  }
}
