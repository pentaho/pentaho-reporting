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

package org.pentaho.reporting.engine.classic.core.metadata.propertyeditors;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyEditor;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.util.StrokeUtility;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

/**
 * @author wseyler
 */
public class BasicStrokeEditor implements PropertyEditor {

  private BasicStroke value;

  private PropertyChangeSupport propertyChangeSupport;

  public BasicStrokeEditor() {
    super();
    value = new BasicStroke();
    propertyChangeSupport = new PropertyChangeSupport( this );
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.beans.PropertyEditor#addPropertyChangeListener(java.beans.PropertyChangeListener)
   */
  public void addPropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.addPropertyChangeListener( listener );
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.beans.PropertyEditor#removePropertyChangeListener(java.beans.PropertyChangeListener)
   */
  public void removePropertyChangeListener( final PropertyChangeListener listener ) {
    propertyChangeSupport.removePropertyChangeListener( listener );
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.beans.PropertyEditor#setValue(java.lang.Object)
   */
  public void setValue( final Object value ) {
    final Object oldValue = this.value;
    if ( value instanceof BasicStroke ) {
      this.value = (BasicStroke) value;
    } else {
      this.value = null;
    }
    propertyChangeSupport.firePropertyChange( null, oldValue, this.value );
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.beans.PropertyEditor#getValue()
   */
  public Object getValue() {
    return value;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.beans.PropertyEditor#getAsText()
   */
  public String getAsText() {
    if ( value == null ) {
      return "";
    }

    final BorderStyle borderStyle = StrokeUtility.translateStrokeStyle( value );
    final float width = value.getLineWidth();
    return borderStyle + ", " + width;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.beans.PropertyEditor#setAsText(java.lang.String)
   */
  public void setAsText( final String text ) throws IllegalArgumentException {
    final String[] strings = StringUtils.split( text, "," );
    if ( strings.length == 0 ) {
      setValue( null );
      return;
    }
    if ( strings.length == 1 ) {
      final float v = ParserUtil.parseFloat( strings[0].trim(), -1 );
      if ( v < 0 ) {
        setValue( BorderStyle.getBorderStyle( strings[0].trim() ) );
        return;
      }
      setValue( new BasicStroke( v ) );
      return;
    }
    if ( strings.length > 2 ) {
      return;
    }

    float width = ParserUtil.parseFloat( strings[0].trim(), -1 );
    if ( width < 0 ) {
      width = ParserUtil.parseFloat( strings[1].trim(), -1 );
    }
    if ( width < 0 ) {
      setValue( null );
      return;
    }
    BorderStyle style = BorderStyle.getBorderStyle( strings[0].trim() );
    if ( style == null ) {
      style = BorderStyle.getBorderStyle( strings[1].trim() );
    }
    if ( style == null ) {
      setValue( null );
    } else {
      setValue( style.createStroke( width ) );
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.beans.PropertyEditor#supportsCustomEditor()
   */
  public boolean supportsCustomEditor() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.beans.PropertyEditor#getCustomEditor()
   */
  public Component getCustomEditor() {
    return new StrokeEditorComponent( this );
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.beans.PropertyEditor#isPaintable()
   */
  public boolean isPaintable() {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.beans.PropertyEditor#paintValue(java.awt.Graphics, java.awt.Rectangle)
   */
  public void paintValue( final Graphics gfx, final Rectangle box ) {
    if ( gfx instanceof Graphics2D ) {
      final Graphics2D graphics2D = (Graphics2D) gfx;
      graphics2D.setStroke( value );
      graphics2D.drawLine( box.x, box.height / 2 + box.y, box.x + box.width, box.height / 2 + box.y );
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.beans.PropertyEditor#getJavaInitializationString()
   */
  public String getJavaInitializationString() {
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.beans.PropertyEditor#getTags()
   */
  public String[] getTags() {
    return null;
  }

  private static class BorderStyleRenderer extends DefaultListCellRenderer {
    public Component getListCellRendererComponent( final JList list, final Object value, final int index,
        final boolean isSelected, final boolean cellHasFocus ) {
      final JLabel label = (JLabel) super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
      if ( value instanceof BorderStyle ) {
        final BorderStyle style = (BorderStyle) value;
        final StrokeIcon strokeIcon = new StrokeIcon( style.createStroke( 2 ) );
        label.setIcon( strokeIcon );
      } else {
        label.setIcon( null );
      }
      label.setText( "" );
      return label;
    }
  }

  private static class StrokeEditorComponent extends JPanel {
    private class DashSelectionHandler implements ActionListener {
      private final JComboBox dashComboBox;

      public DashSelectionHandler( final JComboBox dashComboBox ) {
        this.dashComboBox = dashComboBox;
      }

      public void actionPerformed( final ActionEvent e ) {
        setDashType( (BorderStyle) dashComboBox.getSelectedItem() );
      }
    }

    private class WidthUpdateHandler implements DocumentListener {
      private JTextField textField;

      public WidthUpdateHandler( final JTextField textField ) {
        this.textField = textField;
      }

      /**
       * Gives notification that there was an insert into the document. The range given by the DocumentEvent bounds the
       * freshly inserted region.
       *
       * @param e
       *          the document event
       */
      public void insertUpdate( final DocumentEvent e ) {
        final String s = this.textField.getText();
        try {
          setStrokeWidth( Float.parseFloat( s.trim() ) );
        } catch ( Exception ex ) {
          // ignored
        }
      }

      /**
       * Gives notification that a portion of the document has been removed. The range is given in terms of what the
       * view last saw (that is, before updating sticky positions).
       *
       * @param e
       *          the document event
       */
      public void removeUpdate( final DocumentEvent e ) {
        insertUpdate( e );
      }

      /**
       * Gives notification that an attribute or set of attributes changed.
       *
       * @param e
       *          the document event
       */
      public void changedUpdate( final DocumentEvent e ) {
        insertUpdate( e );
      }
    }

    private float width;
    private BorderStyle borderStyle;
    private SamplePanel samplePanel;
    private BasicStrokeEditor editor;

    public StrokeEditorComponent( final BasicStrokeEditor editor ) {
      this.editor = editor;
      samplePanel = new SamplePanel();

      final BasicStroke vb = (BasicStroke) editor.getValue();
      if ( vb != null ) {
        width = vb.getLineWidth();
        borderStyle = StrokeUtility.translateStrokeStyle( vb );
      } else {
        width = 0;
        borderStyle = BorderStyle.NONE;
      }
      initGUI();
    }

    public void setStrokeWidth( final float width ) {
      if ( this.width == width ) {
        return;
      }
      this.width = width;
      if ( borderStyle == null || borderStyle == BorderStyle.NONE ) {
        this.editor.setValue( null );
        this.samplePanel.setSampleValue( null );
      } else {
        final Stroke stroke = borderStyle.createStroke( width );
        this.editor.setValue( stroke );
        this.samplePanel.setSampleValue( stroke );
      }
    }

    public void setDashType( final BorderStyle type ) {
      if ( this.borderStyle == type ) {
        return;
      }
      this.borderStyle = type;

      if ( borderStyle == null || borderStyle == BorderStyle.NONE ) {
        this.editor.setValue( null );
        this.samplePanel.setSampleValue( null );
      } else {
        final Stroke stroke = borderStyle.createStroke( width );
        this.editor.setValue( stroke );
        this.samplePanel.setSampleValue( stroke );
      }
    }

    /**
     *
     */
    private void initGUI() {
      this.setLayout( new GridBagLayout() );
      final GridBagConstraints constraints = new GridBagConstraints();

      // Add the width label
      constraints.gridx = 0;
      constraints.gridy = 0;
      constraints.anchor = GridBagConstraints.EAST;
      this.add( new JLabel( "Width:" ), constraints );

      // Add the spinner with its model
      constraints.gridx = 1;
      constraints.gridy = 0;
      constraints.anchor = GridBagConstraints.WEST;
      final JTextField strokeWidthField = new JTextField();
      strokeWidthField.getDocument().addDocumentListener( new WidthUpdateHandler( strokeWidthField ) );
      strokeWidthField.setText( String.valueOf( width ) );
      strokeWidthField.setColumns( 6 );
      this.add( strokeWidthField, constraints );

      // Add the dash Label
      constraints.gridx = 0;
      constraints.gridy = 1;
      constraints.anchor = GridBagConstraints.CENTER;
      this.add( new JLabel( "Dashes:" ), constraints );

      // Add the dash comboBox
      constraints.gridx = 1;
      constraints.gridy = 1;

      final JComboBox dashComboBox =
          new JComboBox( new Object[] { BorderStyle.SOLID, BorderStyle.DASHED, BorderStyle.DOTTED,
            BorderStyle.DOT_DASH, BorderStyle.DOT_DOT_DASH } );
      dashComboBox.setRenderer( new BorderStyleRenderer() );
      dashComboBox.setSelectedItem( borderStyle );
      dashComboBox.addActionListener( new DashSelectionHandler( dashComboBox ) );
      this.add( dashComboBox, constraints );

      // Add the sample box
      constraints.gridx = 0;
      constraints.gridy = 2;
      constraints.gridwidth = GridBagConstraints.REMAINDER;
      constraints.fill = GridBagConstraints.HORIZONTAL;

      this.add( samplePanel, constraints );
    }
  }

  private static class SamplePanel extends JPanel {
    private Stroke sampleValue;

    public SamplePanel() {
      setBorder( BorderFactory.createTitledBorder( "Sample" ) );
      this.setMinimumSize( new Dimension( 40, 40 ) );
    }

    public void paintComponent( final Graphics g ) {
      super.paintComponent( g );
      if ( sampleValue != null ) {
        final Graphics2D g2d = (Graphics2D) g;
        final Stroke origStroke = g2d.getStroke();
        final Color origColor = g2d.getColor();
        final Shape origClip = g2d.getClip();

        g2d.setStroke( sampleValue );

        g2d.drawLine( getInsets().left + 1, ( getHeight() / 2 ) + 1, getWidth() - ( getInsets().right + 1 ),
            ( getHeight() / 2 ) + 1 );

        g2d.setClip( origClip );
        g2d.setStroke( origStroke );
        g2d.setColor( origColor );
      }

    }

    public Stroke getSampleValue() {
      return sampleValue;
    }

    public void setSampleValue( final Stroke sampleValue ) {
      this.sampleValue = sampleValue;
      repaint();
    }
  }

  private static class StrokeIcon implements Icon {
    private Stroke basicStroke;

    private StrokeIcon( final Stroke basicStroke ) {
      this.basicStroke = basicStroke;
    }

    public void paintIcon( final Component c, final Graphics g, final int x, final int y ) {
      final Graphics2D g2d = (Graphics2D) g;
      final Color origColor = g2d.getColor();
      final Object hint = g2d.getRenderingHint( RenderingHints.KEY_ANTIALIASING );
      g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
      final Stroke origStroke = g2d.getStroke();

      g2d.translate( x, y );

      g2d.setColor( Color.BLACK );
      g2d.setStroke( basicStroke );

      g2d.drawLine( 0, getIconHeight() / 2, getIconWidth(), getIconHeight() / 2 );
      g2d.setColor( origColor );

      g2d.translate( -x, -y );
      g2d.setStroke( origStroke );
      g2d.setRenderingHint( RenderingHints.KEY_ANTIALIASING, hint );
    }

    public int getIconWidth() {
      return 100;
    }

    public int getIconHeight() {
      return 20;
    }
  }
}
