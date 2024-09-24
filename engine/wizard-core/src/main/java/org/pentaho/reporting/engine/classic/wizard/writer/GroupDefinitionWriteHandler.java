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
