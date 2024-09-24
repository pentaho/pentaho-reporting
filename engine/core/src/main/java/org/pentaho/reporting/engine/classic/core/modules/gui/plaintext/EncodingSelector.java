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

package org.pentaho.reporting.engine.classic.core.modules.gui.plaintext;

import java.awt.BorderLayout;
import java.util.Locale;

import javax.swing.JComboBox;
import javax.swing.JPanel;

import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.EncodingComboBoxModel;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.helper.PrinterEncoding;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.helper.PrinterSpecification;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;

public class EncodingSelector extends JPanel {
  /**
   * Provides access to externalized strings
   */
  private static final Messages MESSAGES = new Messages( Locale.getDefault(), PlainTextExportGUIModule.BUNDLE_NAME,
      ObjectUtilities.getClassLoader( PlainTextExportGUIModule.class ) );

  public static class GenericPrinterSpecification implements PrinterSpecification {
    private static final byte[] EMPTY_ARRAY = new byte[0];

    public GenericPrinterSpecification() {
    }

    public String getDisplayName() {
      return getName();
    }

    /**
     * Returns the encoding definition for the given java encoding.
     *
     * @param encoding
     *          the java encoding that should be mapped into a printer specific encoding.
     * @return the printer specific encoding.
     * @throws IllegalArgumentException
     *           if the given encoding is not supported.
     */
    public PrinterEncoding getEncoding( final String encoding ) {
      if ( isEncodingSupported( encoding ) == false ) {
        throw new IllegalArgumentException( EncodingSelector.MESSAGES
            .getErrorString( "EncodingSelector.ERROR_0001_ENCODING_NOT_SUPPORTED" ) ); //$NON-NLS-1$
      }

      return new PrinterEncoding( encoding, encoding, encoding, GenericPrinterSpecification.EMPTY_ARRAY );
    }

    /**
     * Returns the name of the encoding mapping. This is usually the same as the printer model name.
     *
     * @return the printer model.
     */
    public String getName() {
      return EncodingSelector.MESSAGES.getString( "EncodingSelector.USER_GENERIC_PRINTER" ); //$NON-NLS-1$
    }

    /**
     * Checks whether the given Java-encoding is supported.
     *
     * @param encoding
     *          the java encoding that should be mapped into a printer specific encoding.
     * @return true, if there is a mapping, false otherwise.
     */
    public boolean isEncodingSupported( final String encoding ) {
      if ( EncodingRegistry.getInstance().isSupportedEncoding( encoding ) ) {
        // if already checked there, then use it ...
        return true;
      }
      return false;
    }

    /**
     * Returns true, if a given operation is supported, false otherwise.
     *
     * @param operationName
     *          the operation, that should be performed
     * @return true, if the printer will be able to perform that operation, false otherwise.
     */
    public boolean isFeatureAvailable( final String operationName ) {
      // we accept the first default that is offered, we do not even check the operation
      return true;
    }
  }

  private EncodingComboBoxModel encodingComboBoxModel;
  private JComboBox encodingComboBox;

  /**
   * Create a new JPanel with a double buffer and a flow layout
   */
  public EncodingSelector() {
    setLayout( new BorderLayout() );
    encodingComboBox = new JComboBox();
    add( encodingComboBox, BorderLayout.CENTER );
    setEncodings( new GenericPrinterSpecification(), Locale.getDefault() );
  }

  public String getSelectedEncoding() {
    return encodingComboBoxModel.getSelectedEncoding();
  }

  public void setSelectedEncoding( final String encoding ) {
    this.encodingComboBoxModel.setSelectedEncoding( encoding );
  }

  public void setEncodings( final PrinterSpecification printerSpecification, final Locale locale ) {
    if ( printerSpecification == null ) {
      throw new NullPointerException( EncodingSelector.MESSAGES
          .getErrorString( "EncodingSelector.ERROR_0002_NULL_SPECIFICATION" ) ); //$NON-NLS-1$
    }
    final EncodingComboBoxModel defaultEncodingModel = EncodingComboBoxModel.createDefaultModel( locale );

    final EncodingComboBoxModel retval = new EncodingComboBoxModel( locale );
    for ( int i = 0; i < defaultEncodingModel.getSize(); i++ ) {
      final String encoding = defaultEncodingModel.getEncoding( i );
      if ( printerSpecification.isEncodingSupported( encoding ) ) {
        final String description = defaultEncodingModel.getDescription( i );
        retval.addEncoding( encoding, description );
      }
    }
    retval.sort();
    final Object oldSelectedValue = encodingComboBox.getSelectedItem();
    encodingComboBox.setModel( retval );
    encodingComboBoxModel = retval;
    encodingComboBoxModel.setSelectedItem( oldSelectedValue );
  }
}
