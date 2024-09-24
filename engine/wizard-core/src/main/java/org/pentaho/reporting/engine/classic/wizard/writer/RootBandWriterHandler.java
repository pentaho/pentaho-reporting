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
import org.pentaho.reporting.engine.classic.core.util.beans.ColorValueConverter;
import org.pentaho.reporting.engine.classic.core.util.beans.ElementAlignmentValueConverter;
import org.pentaho.reporting.engine.classic.wizard.WizardCoreModule;
import org.pentaho.reporting.engine.classic.wizard.model.RootBandDefinition;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.awt.*;

public class RootBandWriterHandler {

  public RootBandWriterHandler() {
  }

  public void writeReport( final WriteableDocumentBundle bundle,
                           final BundleWriterState state,
                           final XmlWriter writer,
                           final RootBandDefinition rootBandDefinition,
                           final String tagName ) throws BundleWriterException {
    try {
      final AttributeList attList = new AttributeList();

      final ColorValueConverter colorValueConverter = new ColorValueConverter();
      final Color backgroundColor = rootBandDefinition.getBackgroundColor();
      if ( backgroundColor != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "background-color",
          ColorValueConverter.colorToString( backgroundColor ) );
      }
      final Boolean bold = rootBandDefinition.getFontBold();
      if ( bold != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "bold", String.valueOf( bold ) );
      }
      final Color color = rootBandDefinition.getFontColor();
      if ( color != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "font-color",
          ColorValueConverter.colorToString( color ) );
      }
      final Boolean italic = rootBandDefinition.getFontItalic();
      if ( italic != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "italic", String.valueOf( italic ) );
      }
      final Boolean underline = rootBandDefinition.getFontUnderline();
      if ( underline != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "underline", String.valueOf( underline ) );
      }
      final Boolean strikethrough = rootBandDefinition.getFontStrikethrough();
      if ( strikethrough != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "strikethrough", String.valueOf( strikethrough ) );
      }
      final String fontName = rootBandDefinition.getFontName();
      if ( fontName != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "font-name", String.valueOf( fontName ) );
      }
      final Integer fontSize = rootBandDefinition.getFontSize();
      if ( fontSize != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "font-size", String.valueOf( fontSize ) );
      }
      final ElementAlignmentValueConverter elementAlignmentValueConverter = new ElementAlignmentValueConverter();
      final ElementAlignment horizontalAlignment = rootBandDefinition.getHorizontalAlignment();
      if ( horizontalAlignment != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "horizontal-align",
          elementAlignmentValueConverter.toAttributeValue( horizontalAlignment ) );
      }
      final ElementAlignment verticalAlignment = rootBandDefinition.getVerticalAlignment();
      if ( verticalAlignment != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "vertical-align",
          elementAlignmentValueConverter.toAttributeValue( verticalAlignment ) );
      }
      final Boolean repeat = rootBandDefinition.getRepeat();
      if ( repeat != null ) {
        attList.setAttribute( WizardCoreModule.NAMESPACE, "repeat", String.valueOf( repeat ) );
      }
      final boolean visible = rootBandDefinition.isVisible();
      attList.setAttribute( WizardCoreModule.NAMESPACE, "visible", String.valueOf( visible ) );
    } catch ( BeanException e ) {
      throw new BundleWriterException( "Failed to write bundle", e );
    }
  }
}
