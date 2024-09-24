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

package org.pentaho.reporting.engine.classic.wizard.writer;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterState;
import org.pentaho.reporting.engine.classic.wizard.WizardCoreModule;
import org.pentaho.reporting.engine.classic.wizard.WizardProcessorUtil;
import org.pentaho.reporting.engine.classic.wizard.model.DetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.GroupDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.WizardSpecification;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.DefaultTagDescription;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class WizardSpecificationWriteHandler implements BundleWriterHandler {
  public WizardSpecificationWriteHandler() {
  }

  /**
   * Returns a relatively high processing order indicating this BundleWriterHandler should be one of the last processed
   *
   * @return the relative processing order for this BundleWriterHandler
   */
  public int getProcessingOrder() {
    return 100000;
  }

  public String writeReport( final WriteableDocumentBundle bundle,
                             final BundleWriterState state )
    throws IOException, BundleWriterException {
    final AbstractReportDefinition report = state.getReport();
    try {
      final WizardSpecification specification =
        WizardProcessorUtil.loadWizardSpecification( report,
          state.getMasterReport().getResourceManager() );
      if ( specification == null ) {
        return null;
      }

      final BundleWriterState wizardFileState = new BundleWriterState( state, "wizard-specification.xml" );
      if ( bundle.isEntryExists( wizardFileState.getFileName() ) ) {
        // if already exists, remove the old one, enabling node creation of the most recent
        bundle.removeEntry( wizardFileState.getFileName() );
      }
      final OutputStream outputStream =
        new BufferedOutputStream( bundle.createEntry( wizardFileState.getFileName(), "text/xml" ) );
      bundle.getWriteableDocumentMetaData()
        .setEntryAttribute( wizardFileState.getFileName(), BundleUtilities.STICKY_FLAG, "true" );
      bundle.getWriteableDocumentMetaData()
        .setEntryAttribute( wizardFileState.getFileName(), BundleUtilities.HIDDEN_FLAG, "true" );
      final DefaultTagDescription tagDescription = new DefaultTagDescription();
      tagDescription.setNamespaceHasCData( WizardCoreModule.NAMESPACE, false );

      final XmlWriter xmlWriter = new XmlWriter
        ( new OutputStreamWriter( outputStream, "UTF-8" ), tagDescription, "  ", "\n" );

      final AttributeList rootAttrs = new AttributeList();
      rootAttrs.addNamespaceDeclaration( "wizard", WizardCoreModule.NAMESPACE );

      xmlWriter.writeTag( WizardCoreModule.NAMESPACE, "wizard-specification", rootAttrs, XmlWriter.OPEN );

      final RootBandWriterHandler rootBandWriterHandler = new RootBandWriterHandler();
      rootBandWriterHandler
        .writeReport( bundle, wizardFileState, xmlWriter, specification.getColumnHeader(), "column-header" );
      rootBandWriterHandler
        .writeReport( bundle, wizardFileState, xmlWriter, specification.getColumnFooter(), "column-footer" );


      xmlWriter.writeTag( WizardCoreModule.NAMESPACE, "detail-fields", XmlWriter.OPEN );
      final DetailFieldDefinition[] detailFieldDefinitions = specification.getDetailFieldDefinitions();
      final DetailFieldDefinitionWriteHandler detailWriteHandler = new DetailFieldDefinitionWriteHandler();
      for ( int i = 0; i < detailFieldDefinitions.length; i++ ) {
        final DetailFieldDefinition definition = detailFieldDefinitions[ i ];
        detailWriteHandler.writeReport( bundle, wizardFileState, xmlWriter, definition );
      }
      xmlWriter.writeCloseTag();

      xmlWriter.writeTag( WizardCoreModule.NAMESPACE, "group-definitions", XmlWriter.OPEN );
      final GroupDefinition[] groupDefinitions = specification.getGroupDefinitions();
      final GroupDefinitionWriteHandler groupDefinitionWriteHandler = new GroupDefinitionWriteHandler();
      for ( int i = 0; i < groupDefinitions.length; i++ ) {
        final GroupDefinition definition = groupDefinitions[ i ];
        groupDefinitionWriteHandler.writeReport( bundle, wizardFileState, xmlWriter, definition );
      }
      xmlWriter.writeCloseTag();

      final WatermarkDefinitionWriterHandler watermarkDefinitionWriterHandler = new WatermarkDefinitionWriterHandler();
      watermarkDefinitionWriterHandler
        .writeReport( bundle, wizardFileState, xmlWriter, specification.getWatermarkDefinition() );
      xmlWriter.writeCloseTag();
      xmlWriter.close();
      return wizardFileState.getFileName();
    } catch ( final ReportProcessingException e ) {
      throw new BundleWriterException( "Failed to load wizard-specifiation", e );
    }
  }
}
