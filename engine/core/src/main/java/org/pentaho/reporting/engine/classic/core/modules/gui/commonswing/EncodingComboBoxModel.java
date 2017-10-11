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

package org.pentaho.reporting.engine.classic.core.modules.gui.commonswing;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.util.Messages;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;

/**
 * A model for the 'encoding' combo box. This combobox model presents a selection for all available string encodings.
 *
 * @author Thomas Morgner.
 */
public class EncodingComboBoxModel implements ComboBoxModel {
  private static final Log logger = LogFactory.getLog( EncodingComboBoxModel.class );
  /**
   * The property that defines which encodings are available in the export dialogs.
   */
  public static final String AVAILABLE_ENCODINGS =
      "org.pentaho.reporting.engine.classic.core.modules.gui.base.EncodingsAvailable"; //$NON-NLS-1$

  /**
   * The encodings available properties value for all properties.
   */
  public static final String AVAILABLE_ENCODINGS_ALL = "all"; //$NON-NLS-1$
  /**
   * The encodings available properties value for properties defined in the properties file.
   */
  public static final String AVAILABLE_ENCODINGS_FILE = "file"; //$NON-NLS-1$
  /**
   * The encodings available properties value for no properties defined. The encoding selection will be disabled.
   */
  public static final String AVAILABLE_ENCODINGS_NONE = "none"; //$NON-NLS-1$

  /**
   * The name of the properties file used to define the available encodings. The property points to a resources in the
   * classpath, not to a real file!
   */
  public static final String ENCODINGS_DEFINITION_FILE =
      "org.pentaho.reporting.engine.classic.core.modules.gui.base.EncodingsFile"; //$NON-NLS-1$

  /**
   * The default name for the encoding properties file. This property defaults to &quot;
   * /org/pentaho/reporting/engine/classic/core/jfreereport-encodings.properties&quot;.
   */
  public static final String ENCODINGS_DEFINITION_FILE_DEFAULT =
      "org/pentaho/reporting/engine/classic/core/modules/gui/commonswing/jfreereport-encodings.properties";

  //$NON-NLS-1$

  /**
   * An encoding comparator.
   */
  private static class EncodingCarrierComparator implements Comparator, Serializable {
    protected EncodingCarrierComparator() {
    }

    /**
     * Compares its two arguments for order. Returns a negative integer, zero, or a positive integer as the first
     * argument is less than, equal to, or greater than the second.
     *
     * @param o1
     *          the first object to be compared.
     * @param o2
     *          the second object to be compared.
     * @return a negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater
     *         than the second.
     * @throws java.lang.ClassCastException
     *           if the arguments' types prevent them from being compared by this Comparator.
     */
    public int compare( final Object o1, final Object o2 ) {
      final EncodingCarrier e1 = (EncodingCarrier) o1;
      final EncodingCarrier e2 = (EncodingCarrier) o2;
      final String name1 = e1.getName();
      final String name2 = e2.getName();
      if ( name1 == null && name2 == null ) {
        return 0;
      }
      if ( name1 == null ) {
        return -1;
      }
      if ( name2 == null ) {
        return 1;
      }
      return name1.toLowerCase().compareTo( name2.toLowerCase() );
    }

    /**
     * Returns <code>true</code> if this object is equal to <code>o</code>, and <code>false</code> otherwise.
     *
     * @param o
     *          the object.
     * @return A boolean.
     */
    public boolean equals( final Object o ) {
      if ( o == null ) {
        return false;
      }
      return getClass().equals( o.getClass() );
    }

    /**
     * All comparators of this type are equal.
     *
     * @return A hash code.
     */
    public int hashCode() {
      return getClass().hashCode();
    }
  }

  /**
   * An encoding carrier.
   */
  private static class EncodingCarrier {
    /**
     * The encoding name.
     */
    private String name;

    /**
     * The encoding description.
     */
    private String description;

    /**
     * The display name.
     */
    private String displayName;

    /**
     * Creates a new encoding.
     *
     * @param name
     *          the name (<code>null</code> not permitted).
     * @param description
     *          the description.
     */
    protected EncodingCarrier( final String name, final String description ) {
      this.name = name;
      this.description = description;
      if ( name == null ) {
        this.displayName = null;
      } else {
        final StringBuffer dName = new StringBuffer( 100 );
        dName.append( name );
        dName.append( " (" ); //$NON-NLS-1$
        dName.append( description );
        dName.append( ')' );
        this.displayName = dName.toString();
      }
    }

    /**
     * Returns the name.
     *
     * @return The name.
     */
    public String getName() {
      return name;
    }

    /**
     * Returns the description.
     *
     * @return The description.
     */
    public String getDescription() {
      return description;
    }

    /**
     * Returns the display name (the regular name followed by the description in brackets).
     *
     * @return The display name.
     */
    public String getDisplayName() {
      return displayName;
    }

    /**
     * Returns <code>true</code> if the objects are equal, and <code>false</code> otherwise.
     *
     * @param o
     *          the object.
     * @return A boolean.
     */
    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( !( o instanceof EncodingCarrier ) ) {
        return false;
      }

      final EncodingCarrier carrier = (EncodingCarrier) o;

      if ( name == null ) {
        if ( carrier.name != null ) {
          return false;
        }
      } else if ( !name.equalsIgnoreCase( carrier.name ) ) {
        return false;
      }

      return true;
    }

    /**
     * Returns a hash code.
     *
     * @return The hash code.
     */
    public int hashCode() {
      if ( name == null ) {
        return 0;
      }
      return name.hashCode();
    }
  }

  /**
   * Storage for the encodings.
   */
  private final ArrayList encodings;

  /**
   * Storage for registered listeners.
   */
  private ArrayList listDataListeners;

  /**
   * The selected index.
   */
  private int selectedIndex;

  /**
   * The selected object.
   */
  private Object selectedObject;

  private ResourceBundle bundle;
  public static final String BUNDLE_NAME =
      "org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.encoding-names"; //$NON-NLS-1$

  private Messages messages;
  private String ENCODING_DEFAULT_DESCRIPTION;

  /**
   * Creates a new model.
   *
   * @param locale
   */
  public EncodingComboBoxModel( final Locale locale ) {
    bundle = ResourceBundle.getBundle( EncodingComboBoxModel.BUNDLE_NAME, locale );
    messages =
        new Messages( locale, SwingCommonModule.BUNDLE_NAME, ObjectUtilities.getClassLoader( SwingCommonModule.class ) );
    ENCODING_DEFAULT_DESCRIPTION = messages.getString( "EncodingComboBoxModel.USER_ENCODING_DEFAULT_DESCRIPTION" ); //$NON-NLS-1$
    encodings = new ArrayList();
    listDataListeners = null;
    selectedIndex = -1;
  }

  /**
   * Adds an encoding.
   *
   * @param name
   *          the name.
   * @param description
   *          the description.
   * @return <code>true</code> if the encoding is valid and added to the model, <code>false</code> otherwise.
   */
  public boolean addEncoding( final String name, final String description ) {
    if ( EncodingRegistry.getInstance().isSupportedEncoding( name ) ) {
      encodings.add( new EncodingCarrier( name, description ) );
    } else {
      return false;
    }

    fireContentsChanged();
    return true;
  }

  /**
   * Adds an encoding to the model without checking its validity.
   *
   * @param name
   *          the name.
   * @param description
   *          the description.
   */
  public void addEncodingUnchecked( final String name, final String description ) {
    encodings.add( new EncodingCarrier( name, description ) );
    fireContentsChanged();
  }

  public void removeEncoding( final String name ) {
    if ( encodings.remove( name ) ) {
      fireContentsChanged();
    }
  }

  /**
   * Make sure, that this encoding is defined and selectable in the combobox model.
   *
   * @param encoding
   *          the encoding that should be verified.
   */
  public void ensureEncodingAvailable( final String encoding ) {
    if ( encoding == null ) {
      throw new NullPointerException( messages
          .getErrorString( "EncodingComboBoxModel.ERROR_0001_ENCODING_CANNOT_BE_NULL" ) ); //$NON-NLS-1$
    }
    final String desc = getEncodingDescription( encoding );
    final EncodingCarrier ec = new EncodingCarrier( encoding, desc );
    if ( encodings.contains( ec ) == false ) {
      encodings.add( ec );
      fireContentsChanged();
    }
  }

  protected String getEncodingDescription( final String encoding ) {
    try {
      return bundle.getString( encoding );
    } catch ( Exception e ) {
      return ENCODING_DEFAULT_DESCRIPTION;
    }
  }

  /**
   * Sorts the encodings. Keep the selected object ...
   */
  public void sort() {
    final Object selectedObject = getSelectedItem();
    Collections.sort( encodings, new EncodingCarrierComparator() );
    setSelectedItem( selectedObject );
    fireContentsChanged();
  }

  /**
   * Notifies all registered listeners that the content of the model has changed.
   */
  protected void fireContentsChanged() {
    if ( listDataListeners == null ) {
      return;
    }
    fireContentsChanged( 0, getSize() );
  }

  /**
   * Notifies all registered listeners that the content of the model has changed.
   */
  protected void fireContentsChanged( final int start, final int length ) {
    if ( listDataListeners == null ) {
      return;
    }
    final ListDataEvent evt = new ListDataEvent( this, ListDataEvent.CONTENTS_CHANGED, start, length );
    for ( int i = 0; i < listDataListeners.size(); i++ ) {
      final ListDataListener l = (ListDataListener) listDataListeners.get( i );
      l.contentsChanged( evt );
    }
  }

  /**
   * Set the selected item. The implementation of this method should notify all registered <code>ListDataListener</code>
   * s that the contents have changed.
   *
   * @param anItem
   *          the list object to select or <code>null</code> to clear the selection
   */
  public void setSelectedItem( final Object anItem ) {
    selectedObject = anItem;
    if ( anItem instanceof String ) {
      final int size = getSize();
      for ( int i = 0; i < size; i++ ) {
        if ( anItem.equals( getElementAt( i ) ) ) {
          selectedIndex = i;
          fireContentsChanged( -1, -1 );
          return;
        }
      }
    }
    selectedIndex = -1;
    fireContentsChanged( -1, -1 );
  }

  /**
   * Returns the selected index.
   *
   * @return The index.
   */
  public int getSelectedIndex() {
    return selectedIndex;
  }

  /**
   * Defines the selected index for this encoding model.
   *
   * @param index
   *          the selected index or -1 to clear the selection.
   * @throws java.lang.IllegalArgumentException
   *           if the given index is invalid.
   */
  public void setSelectedIndex( final int index ) {
    if ( index == -1 ) {
      selectedIndex = -1;
      selectedObject = null;
      fireContentsChanged( -1, -1 );
      return;
    }
    if ( index < -1 || index >= getSize() ) {
      throw new IllegalArgumentException( messages.getErrorString( "EncodingComboBoxModel.ERROR_0001_INVALID_INDEX" ) ); //$NON-NLS-1$
    }
    selectedIndex = index;
    selectedObject = getElementAt( index );
    fireContentsChanged( -1, -1 );
  }

  /**
   * Returns the selected encoding.
   *
   * @return The encoding (name).
   */
  public String getSelectedEncoding() {
    if ( selectedIndex == -1 ) {
      return null;
    }
    final EncodingCarrier ec = (EncodingCarrier) encodings.get( selectedIndex );
    return ec.getName();
  }

  /**
   * Returns the selected item.
   *
   * @return The selected item or <code>null</code> if there is no selection
   */
  public Object getSelectedItem() {
    return selectedObject;
  }

  /**
   * Returns the length of the list.
   *
   * @return the length of the list
   */
  public int getSize() {
    return encodings.size();
  }

  /**
   * Returns the value at the specified index.
   *
   * @param index
   *          the requested index
   * @return the value at <code>index</code>
   */
  public Object getElementAt( final int index ) {
    final EncodingCarrier ec = (EncodingCarrier) encodings.get( index );
    return ec.getDisplayName();
  }

  /**
   * Adds a listener to the list that's notified each time a change to the data model occurs.
   *
   * @param l
   *          the <code>ListDataListener</code> to be added
   */
  public void addListDataListener( final ListDataListener l ) {
    if ( listDataListeners == null ) {
      listDataListeners = new ArrayList( 5 );
    }
    listDataListeners.add( l );
  }

  /**
   * Removes a listener from the list that's notified each time a change to the data model occurs.
   *
   * @param l
   *          the <code>ListDataListener</code> to be removed
   */
  public void removeListDataListener( final ListDataListener l ) {
    if ( listDataListeners == null ) {
      return;
    }
    listDataListeners.remove( l );
  }

  /**
   * Creates a default model containing a selection of encodings.
   *
   * @return The default model.
   */
  public static EncodingComboBoxModel createDefaultModel( final Locale locale ) {
    return createDefaultModel( locale, false );
  }

  public static EncodingComboBoxModel createDefaultModel( final Locale locale, boolean includeNull ) {
    final EncodingComboBoxModel ecb = new EncodingComboBoxModel( locale );
    if ( includeNull ) {
      ecb.addEncodingUnchecked( null, "" );
    }
    final String availEncs = getAvailableEncodings();
    final boolean allEncodings = availEncs.equalsIgnoreCase( AVAILABLE_ENCODINGS_ALL );

    if ( allEncodings || availEncs.equals( AVAILABLE_ENCODINGS_FILE ) ) {
      final String encFile = getEncodingsDefinitionFile();
      final InputStream in = ObjectUtilities.getResourceAsStream( encFile, EncodingComboBoxModel.class );
      if ( in == null ) {
        final Messages messages =
            new Messages( locale, SwingCommonModule.BUNDLE_NAME, ObjectUtilities
                .getClassLoader( SwingCommonModule.class ) );
        logger.warn( messages.getString( "EncodingComboBoxModel.WARN_ENCODING_FILE_NOT_FOUND", encFile ) ); //$NON-NLS-1$
      } else {
        try {
          // final Properties defaultEncodings = getDefaultEncodings();
          final Properties encDef = new Properties();
          final BufferedInputStream bin = new BufferedInputStream( in );
          try {
            encDef.load( bin );
          } finally {
            bin.close();
          }
          final Enumeration en = encDef.keys();
          while ( en.hasMoreElements() ) {
            final String enc = (String) en.nextElement();
            // if not set to "true"
            if ( "true".equalsIgnoreCase( encDef.getProperty( enc, "false" ) ) ) { //$NON-NLS-1$ //$NON-NLS-2$
              // if the encoding is disabled ...
              ecb.addEncoding( enc, ecb.getEncodingDescription( enc ) );
            }
          }
        } catch ( IOException e ) {
          final Messages messages =
              new Messages( locale, SwingCommonModule.BUNDLE_NAME, ObjectUtilities
                  .getClassLoader( SwingCommonModule.class ) );
          logger.warn( messages.getString( "EncodingComboBoxModel.WARN_ERROR_READING_ENCODING_FILE" ) + encFile, e ); //$NON-NLS-1$
        }
      }
    }
    return ecb;
  }

  /**
   * Returns the index of an encoding.
   *
   * @param encoding
   *          the encoding (name).
   * @return The index.
   */
  public int indexOf( final String encoding ) {
    return encodings.indexOf( new EncodingCarrier( encoding, null ) );
  }

  /**
   * Returns an encoding.
   *
   * @param index
   *          the index.
   * @return The index.
   */
  public String getEncoding( final int index ) {
    final EncodingCarrier ec = (EncodingCarrier) encodings.get( index );
    return ec.getName();
  }

  /**
   * Returns a description.
   *
   * @param index
   *          the index.
   * @return The description.
   */
  public String getDescription( final int index ) {
    final EncodingCarrier ec = (EncodingCarrier) encodings.get( index );
    return ec.getDescription();
  }

  /**
   * Defines the loader settings for the available encodings shown to the user. The property defaults to
   * AVAILABLE_ENCODINGS_ALL.
   *
   * @return either AVAILABLE_ENCODINGS_ALL, AVAILABLE_ENCODINGS_FILE or AVAILABLE_ENCODINGS_NONE.
   */
  public static String getEncodingsDefinitionFile() {
    return ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty( ENCODINGS_DEFINITION_FILE,
        ENCODINGS_DEFINITION_FILE_DEFAULT );
  }

  /**
   * Defines the loader settings for the available encodings shown to the user. The property defaults to
   * AVAILABLE_ENCODINGS_ALL.
   *
   * @return either AVAILABLE_ENCODINGS_ALL, AVAILABLE_ENCODINGS_FILE or AVAILABLE_ENCODINGS_NONE.
   */
  public static String getAvailableEncodings() {
    return ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty( AVAILABLE_ENCODINGS,
        AVAILABLE_ENCODINGS_ALL );
  }

  public void setSelectedEncoding( final String encoding ) {
    if ( encoding == null ) {
      selectedIndex = -1;
      selectedObject = null;
      return;
    }

    final int size = encodings.size();
    for ( int i = 0; i < size; i++ ) {
      final EncodingCarrier carrier = (EncodingCarrier) encodings.get( i );
      if ( encoding.equals( carrier.getName() ) ) {
        selectedIndex = i;
        selectedObject = carrier.getDisplayName();
        fireContentsChanged( -1, -1 );
        return;
      }
    }
    // default fall-back to have a valid value ..
    if ( size > 0 ) {
      selectedIndex = 0;
      selectedObject = getElementAt( 0 );
      fireContentsChanged( -1, -1 );
    }
  }
}
