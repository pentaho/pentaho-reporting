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

package org.pentaho.reporting.tools.configeditor.editor;

import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.tools.configeditor.model.ConfigDescriptionEntry;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

/**
 * The text key editor is used to edit a free form text.
 *
 * @author Thomas Morgner
 */
public class TextKeyEditor extends AbstractKeyEditor {
  /**
   * An handler class that validates the content whenever a change in the text document occurs.
   *
   * @author Thomas Morgner
   */
  private class DocumentChangeHandler implements DocumentListener {
    /**
     * Default Constructor.
     */
    private DocumentChangeHandler() {
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    public void changedUpdate( final DocumentEvent e ) {
      validateContent();
    }

    /**
     * Gives notification that a portion of the document has been removed.  The range is given in terms of what the view
     * last saw (that is, before updating sticky positions).
     *
     * @param e the document event
     */
    public void removeUpdate( final DocumentEvent e ) {
      validateContent();
    }

    /**
     * Gives notification that there was an insert into the document.  The range given by the DocumentEvent bounds the
     * freshly inserted region.
     *
     * @param e the document event
     */
    public void insertUpdate( final DocumentEvent e ) {
      validateContent();
    }
  }

  /**
   * The editor component for the key content.
   */
  private final JTextField content;
  /**
   * the label that names the content.
   */
  private final JLabel entryLabel;
  /**
   * a carrier component that acts as content pane.
   */
  private final JPanel entryLabelCarrier;

  /**
   * Creates a new text key editor for the given configuration and description entry. The given display name will be
   * used as label text.
   *
   * @param config      the report configuration from where to read the configuration values.
   * @param entry       the entry description supplies the meta data.
   * @param displayName the label content.
   */
  public TextKeyEditor( final HierarchicalConfiguration config,
                        final ConfigDescriptionEntry entry,
                        final String displayName ) {
    super( config, entry );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout( 5, 0 ) );
    entryLabel = new JLabel( displayName );
    entryLabel.setToolTipText( entry.getDescription() );

    entryLabelCarrier = new JPanel();
    entryLabelCarrier.setLayout( new BorderLayout() );
    entryLabelCarrier.add( entryLabel );
    contentPane.add( entryLabelCarrier, BorderLayout.WEST );

    content = new JTextField();
    content.getDocument().addDocumentListener( new DocumentChangeHandler() );

    contentPane.add( content, BorderLayout.CENTER );
    setContentPane( contentPane );
    reset();
  }

  /**
   * This method validates the content of the text field. In this implementation no validation is done and all text is
   * accepted.
   */
  public void validateContent() {
    setValidInput( true );
  }

  /**
   * Resets the value to the defaults from the report configuration.
   */
  public void reset() {
    content.setText( loadValue() );
  }

  /**
   * Stores the input as new value for the report configuration. This method does nothing, if the content is not valid.
   */
  public void store() {
    if ( isValidInput() ) {
      if ( isEnabled() ) {
        storeValue( content.getText() );
      } else {
        deleteValue();
      }
    }
  }

  /**
   * Returns the content from the input field.
   *
   * @return the input field text.
   */
  public String getContent() {
    return content.getText();
  }

  /**
   * Sets whether or not this component is enabled. A component which is enabled may respond to user input, while a
   * component which is not enabled cannot respond to user input.  Some components may alter their visual representation
   * when they are disabled in order to provide feedback to the user that they cannot take input.
   *
   * @param enabled defines, whether this editor will be enabled.
   * @see java.awt.Component#isEnabled
   */
  public void setEnabled( final boolean enabled ) {
    super.setEnabled( enabled );
    content.setEnabled( enabled );
  }

  /**
   * Defines the preferred width of the label.
   *
   * @param width the new preferred width.
   */
  public void setLabelWidth( final int width ) {
    final Dimension prefSize = entryLabel.getPreferredSize();
    entryLabelCarrier.setPreferredSize( new Dimension( width, prefSize.height ) );
  }

  /**
   * Returns the preferred width of the label.
   *
   * @return the preferred width.
   */
  public int getLabelWidth() {
    final Dimension prefSize = entryLabel.getPreferredSize();
    if ( prefSize != null ) {
      return prefSize.width;
    }
    return 0;
  }
}
