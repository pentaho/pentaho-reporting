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

package org.pentaho.reporting.libraries.base.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

/**
 * An utility class to ease up using property-file resource bundles.
 * <p/>
 * The class support references within the resource bundle set to minimize the occurence of duplicate keys. References
 * are given in the format:
 * <pre>
 * a.key.name=@referenced.key
 * </pre>
 * <p/>
 * A lookup to a key in an other resource bundle should be written by
 * <pre>
 * a.key.name=@@resourcebundle_name@referenced.key
 * </pre>
 *
 * @author Thomas Morgner
 * @noinspection HardCodedStringLiteral
 */
public class ResourceBundleSupport {
  /**
   * A logger for debug-messages.
   */
  private static final Log logger = LogFactory.getLog( ResourceBundleSupport.class );

  /**
   * The resource bundle that will be used for local lookups.
   */
  private ResourceBundle resources;

  /**
   * A cache for string values, as looking up the cache is faster than looking up the value in the bundle.
   */
  private HashMap<String, String> cache;
  /**
   * The current lookup path when performing non local lookups. This prevents infinite loops during such lookups.
   */
  private HashSet<String> lookupPath;

  /**
   * The name of the local resource bundle. This property is only used for debugging and logging.
   */
  private String resourceBase;

  /**
   * The locale for this bundle.
   */
  private Locale locale;

  private ClassLoader sourceClassLoader;
  private static final Integer INVALID_MNEMONIC = new Integer( 0 );

  /**
   * Creates a new instance.
   *
   * @param locale      the locale that should be used to load the resource-bundle.
   * @param baseName    the base name of the resource bundle, a fully qualified class name
   * @param classLoader the class-loader from where to load resources.
   */
  public ResourceBundleSupport( final Locale locale, final String baseName, final ClassLoader classLoader ) {
    this( locale, ResourceBundle.getBundle( baseName, locale, classLoader ), baseName, classLoader );
  }

  /**
   * Creates a new instance.
   *
   * @param locale         the locale for which this resource bundle is created.
   * @param resourceBundle the resourcebundle
   * @param baseName       the base name of the resource bundle, a fully qualified class name
   * @param classLoader    the class-loader from where to load resources.
   */
  public ResourceBundleSupport( final Locale locale,
                                final ResourceBundle resourceBundle,
                                final String baseName,
                                final ClassLoader classLoader ) {
    if ( locale == null ) {
      throw new NullPointerException( "Locale must not be null" );
    }
    if ( resourceBundle == null ) {
      throw new NullPointerException( "Resources must not be null" );
    }
    if ( baseName == null ) {
      throw new NullPointerException( "BaseName must not be null" );
    }
    if ( classLoader == null ) {
      throw new NullPointerException( "ClassLoader must not be null" );
    }
    this.sourceClassLoader = classLoader;
    this.locale = locale;
    this.resources = resourceBundle;
    this.resourceBase = baseName;
    this.cache = new HashMap<String, String>();
    this.lookupPath = new HashSet<String>();
  }

  /**
   * Creates a new instance.
   *
   * @param locale         the locale for which the resource bundle is created.
   * @param resourceBundle the resourcebundle
   * @param classLoader    the class-loader from where to load resources.
   */
  public ResourceBundleSupport( final Locale locale,
                                final ResourceBundle resourceBundle,
                                final ClassLoader classLoader ) {
    this( locale, resourceBundle, resourceBundle.toString(), classLoader );
  }

  /**
   * The base name of the resource bundle.
   *
   * @return the resource bundle's name.
   */
  protected final String getResourceBase() {
    return this.resourceBase;
  }

  /**
   * Gets a string for the given key from this resource bundle or one of its parents. If the key is a link, the link is
   * resolved and the referenced string is returned instead.
   *
   * @param key the key for the desired string
   * @return the string for the given key
   * @throws NullPointerException               if <code>key</code> is <code>null</code>
   * @throws java.util.MissingResourceException if no object for the given key can be found
   * @throws ClassCastException                 if the object found for the given key is not a string
   */
  public synchronized String strictString( final String key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    final String retval = this.cache.get( key );
    if ( retval != null ) {
      return retval;
    }
    this.lookupPath.clear();
    return internalGetString( key );
  }

  /**
   * Performs the lookup for the given key. If the key points to a link the link is resolved and that key is looked up
   * instead.
   *
   * @param key the key for the string
   * @return the string for the given key
   */
  protected String internalGetString( final String key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    if ( this.lookupPath.contains( key ) ) {
      throw new MissingResourceException
        ( "InfiniteLoop in resource lookup",
          getResourceBase(), this.lookupPath.toString() );
    }
    final String fromResBundle = this.resources.getString( key );
    if ( fromResBundle.length() > 0 && fromResBundle.charAt( 0 ) == '@' ) {
      if ( fromResBundle.length() > 1 && fromResBundle.charAt( 1 ) == '@' ) {
        // global forward ...
        final int idx = fromResBundle.indexOf( '@', 2 );
        if ( idx == -1 ) {
          throw new MissingResourceException
            ( "Invalid format for global lookup key.", getResourceBase(), key );
        }
        try {
          final ResourceBundle res = ResourceBundle.getBundle
            ( fromResBundle.substring( 2, idx ), locale, sourceClassLoader );
          return res.getString( fromResBundle.substring( idx + 1 ) );
        } catch ( Exception e ) {
          logger.error( "Error during global lookup", e );
          throw new MissingResourceException( "Error during global lookup", getResourceBase(), key );
        }
      } else {
        // local forward ...
        final String newKey = fromResBundle.substring( 1 );
        this.lookupPath.add( key );
        final String retval = internalGetString( newKey );

        this.cache.put( key, retval );
        return retval;
      }
    } else {
      this.cache.put( key, fromResBundle );
      return fromResBundle;
    }
  }

  /**
   * Returns an scaled icon suitable for buttons or menus.
   *
   * @param key   the name of the resource bundle key
   * @param large true, if the image should be scaled to 24x24, or false for 16x16
   * @return the icon.
   */
  public Icon getIcon( final String key, final boolean large ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    final String name = strictString( key );
    return createIcon( name, true, large );
  }

  /**
   * Returns an unscaled icon.
   *
   * @param key the name of the resource bundle key
   * @return the icon.
   */
  public Icon getIcon( final String key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    final String name = strictString( key );
    return createIcon( name, false, false );
  }

  /**
   * Returns the mnemonic stored at the given resourcebundle key. The mnemonic should be either the symbolic name of one
   * of the KeyEvent.VK_* constants (without the 'VK_') or the character for that key.
   * <p/>
   * For the enter key, the resource bundle would therefore either contain "ENTER" or "\n".
   * <pre>
   * a.resourcebundle.key=ENTER
   * an.other.resourcebundle.key=\n
   * </pre>
   *
   * @param key the resourcebundle key
   * @return the mnemonic
   */
  public Integer getMnemonic( final String key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    final String name = strictString( key );
    return createMnemonic( name );
  }


  /**
   * Returns the mnemonic stored at the given resourcebundle key. The mnemonic should be either the symbolic name of one
   * of the KeyEvent.VK_* constants (without the 'VK_') or the character for that key.
   * <p/>
   * For the enter key, the resource bundle would therefore either contain "ENTER" or "\n".
   * <pre>
   * a.resourcebundle.key=ENTER
   * an.other.resourcebundle.key=\n
   * </pre>
   *
   * @param key the resourcebundle key
   * @return the mnemonic or null, if the mnemonic is not defined.
   */
  public Integer getOptionalMnemonic( final String key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    final String name = getOptionalString( key );
    if ( name != null && name.length() > 0 ) {
      return createMnemonic( name );
    }
    return INVALID_MNEMONIC;
  }

  /**
   * Returns the keystroke stored at the given resourcebundle key.
   * <p/>
   * The keystroke will be composed of a simple key press and the plattform's MenuKeyMask.
   * <p/>
   * The keystrokes character key should be either the symbolic name of one of the KeyEvent.VK_* constants or the
   * character for that key.
   * <p/>
   * For the 'A' key, the resource bundle would therefore either contain "VK_A" or "a".
   * <pre>
   * a.resourcebundle.key=VK_A
   * an.other.resourcebundle.key=a
   * </pre>
   *
   * @param key the resourcebundle key
   * @return the keystroke
   * @see java.awt.Toolkit#getMenuShortcutKeyMask()
   */
  public KeyStroke getKeyStroke( final String key ) {
    String name = strictString( key );
    if ( StringUtils.isEmpty( name ) ) {
      return null;
    }

    boolean explicitNone = false;
    int mask = 0;
    final StringTokenizer strtok = new StringTokenizer( name );
    while ( strtok.hasMoreTokens() ) {
      final String token = strtok.nextToken();
      if ( "shift".equals( token ) ) {
        mask |= KeyEvent.SHIFT_MASK;
      } else if ( "alt".equals( token ) ) {
        mask |= KeyEvent.ALT_MASK;
      } else if ( "ctrl".equals( token ) ) {
        mask |= KeyEvent.CTRL_MASK;
      } else if ( "meta".equals( token ) ) {
        mask |= KeyEvent.META_MASK;
      } else if ( "menu".equals( token ) ) {
        mask |= getMenuKeyMask();
      } else if ( "none".equals( token ) ) {
        explicitNone = true;
      } else {
        name = token;
      }
    }
    if ( explicitNone == true ) {
      mask = 0;
    } else if ( mask == 0 ) {
      mask = getMenuKeyMask();
    }
    //noinspection MagicConstant
    return KeyStroke.getKeyStroke( createMnemonic( name ).intValue(), mask );
  }

  /**
   * Returns the keystroke stored at the given resourcebundle key.
   * <p/>
   * The keystroke will be composed of a simple key press and a keystroke mask pattern. The pattern should be specified
   * via the words "shift", "alt", "ctrl", "meta" or "menu". Menu should be used to reference the platform specific menu
   * shortcut. For the sake of safety, menu should only be combined with "shift" and/or "alt" for menu keystrokes.
   * <p/>
   * The keystrokes character key should be either the symbolic name of one of the KeyEvent.VK_* constants or the
   * character for that key.
   * <p/>
   * For the 'A' key, the resource bundle would therefore either contain "VK_A" or "a".
   * <pre>
   * a.resourcebundle.key=VK_A
   * an.other.resourcebundle.key=a
   * </pre>
   *
   * @param key the resourcebundle key
   * @return the keystroke
   * @see java.awt.Toolkit#getMenuShortcutKeyMask()
   */
  public KeyStroke getOptionalKeyStroke( final String key ) {
    try {
      String name = getOptionalString( key );
      if ( StringUtils.isEmpty( name ) ) {
        return null;
      }

      boolean noneSelected = false;
      int mask = 0;
      final StringTokenizer strtok = new StringTokenizer( name );
      while ( strtok.hasMoreTokens() ) {
        final String token = strtok.nextToken();
        if ( "shift".equals( token ) ) {
          mask |= KeyEvent.SHIFT_MASK;
        } else if ( "alt".equals( token ) ) {
          mask |= KeyEvent.ALT_MASK;
        } else if ( "ctrl".equals( token ) ) {
          mask |= KeyEvent.CTRL_MASK;
        } else if ( "meta".equals( token ) ) {
          mask |= KeyEvent.META_MASK;
        } else if ( "menu".equals( token ) ) {
          mask |= getMenuKeyMask();
        } else if ( "none".equals( token ) ) {
          noneSelected = true;
        } else {
          name = token;
        }
      }
      if ( noneSelected ) {
        mask = 0;
      } else if ( mask == 0 ) {
        mask = getMenuKeyMask();
      }
      //noinspection MagicConstant
      return KeyStroke.getKeyStroke( createMnemonic( name ).intValue(), mask );
    } catch ( MissingResourceException mre ) {
      return null;
    }
  }

  /**
   * Returns the keystroke stored at the given resourcebundle key.
   * <p/>
   * The keystroke will be composed of a simple key press and the given KeyMask. If the KeyMask is zero, a plain
   * Keystroke is returned.
   * <p/>
   * The keystrokes character key should be either the symbolic name of one of the KeyEvent.VK_* constants or the
   * character for that key.
   * <p/>
   * For the 'A' key, the resource bundle would therefore either contain "VK_A" or "a".
   * <pre>
   * a.resourcebundle.key=VK_A
   * an.other.resourcebundle.key=a
   * </pre>
   *
   * @param key  the resourcebundle key
   * @param mask the key-moifier mask to be used to create the keystroke.
   * @return the keystroke that has been generated.
   * @see java.awt.Toolkit#getMenuShortcutKeyMask()
   */
  public KeyStroke getKeyStroke( final String key, final int mask ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    final String name = strictString( key );
    //noinspection MagicConstant
    return KeyStroke.getKeyStroke( createMnemonic( name ).intValue(), mask );
  }

  /**
   * Returns the keystroke stored at the given resourcebundle key.
   * <p/>
   * The keystroke will be composed of a simple key press and the given KeyMask. If the KeyMask is zero, a plain
   * Keystroke is returned.
   * <p/>
   * The keystrokes character key should be either the symbolic name of one of the KeyEvent.VK_* constants or the
   * character for that key.
   * <p/>
   * For the 'A' key, the resource bundle would therefore either contain "VK_A" or "a".
   * <pre>
   * a.resourcebundle.key=VK_A
   * an.other.resourcebundle.key=a
   * </pre>
   *
   * @param key  the resourcebundle key
   * @param mask the key-moifier mask to be used to create the keystroke.
   * @return the keystroke or null if the key is not defined.
   * @see java.awt.Toolkit#getMenuShortcutKeyMask()
   */
  public KeyStroke getOptionalKeyStroke( final String key, final int mask ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    final String name = getOptionalString( key );

    if ( name != null && name.length() > 0 ) {
      //noinspection MagicConstant
      return KeyStroke.getKeyStroke( createMnemonic( name ).intValue(), mask );
    }
    return null;
  }

  /**
   * Returns a JMenu created from a resource bundle definition.
   * <p/>
   * The menu definition consists of two keys, the name of the menu and the mnemonic for that menu. Both keys share a
   * common prefix, which is extended by ".name" for the name of the menu and ".mnemonic" for the mnemonic.
   * <p/>
   * <pre>
   * # define the file menu
   * menu.file.name=File
   * menu.file.mnemonic=F
   * </pre>
   * The menu definition above can be used to create the menu by calling <code>createMenu ("menu.file")</code>.
   *
   * @param keyPrefix the common prefix for that menu
   * @return the created menu
   */
  public JMenu createMenu( final String keyPrefix ) {
    if ( keyPrefix == null ) {
      throw new NullPointerException();
    }

    final JMenu retval = new JMenu();
    retval.setText( strictString( keyPrefix + ".name" ) );
    final Integer mnemonic = getOptionalMnemonic( keyPrefix + ".mnemonic" );
    if ( mnemonic != null ) {
      retval.setMnemonic( mnemonic.intValue() );
    }
    return retval;
  }

  /**
   * Returns a URL pointing to a resource located in the classpath. The resource is looked up using the given key.
   * <p/>
   * Example: The load a file named 'logo.gif' which is stored in a java package named 'org.jfree.resources':
   * <pre>
   * mainmenu.logo=org/jfree/resources/logo.gif
   * </pre>
   * The URL for that file can be queried with: <code>getResource("mainmenu.logo");</code>.
   *
   * @param key the key for the resource
   * @return the resource URL
   */
  public URL getResourceURL( final String key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    final String name = strictString( key );
    final URL in = sourceClassLoader.getResource( name );
    if ( in == null ) {
      logger.warn( "Unable to find file in the class path: " + name + "; key=" + key );
    }
    return in;
  }


  /**
   * Attempts to load an image from classpath. If this fails, an empty image icon is returned.
   *
   * @param resourceName the name of the image. The name should be a global resource name.
   * @param scale        true, if the image should be scaled, false otherwise
   * @param large        true, if the image should be scaled to 24x24, or false for 16x16
   * @return the image icon.
   */
  private ImageIcon createIcon( final String resourceName,
                                final boolean scale,
                                final boolean large ) {
    final URL in = sourceClassLoader.getResource( resourceName );

    if ( in == null ) {
      logger.warn( "Unable to find file in the class path: " + resourceName );
      return new ImageIcon( createTransparentImage( 1, 1 ) );
    }
    final Image img = Toolkit.getDefaultToolkit().createImage( in );
    if ( img == null ) {
      logger.warn( "Unable to instantiate the image: " + resourceName );
      return new ImageIcon( createTransparentImage( 1, 1 ) );
    }
    if ( scale ) {
      if ( large ) {
        return new ImageIcon( img.getScaledInstance( 24, 24, Image.SCALE_SMOOTH ) );
      }
      return new ImageIcon( img.getScaledInstance( 16, 16, Image.SCALE_SMOOTH ) );
    }
    return new ImageIcon( img );
  }

  /**
   * Creates the Mnemonic from the given String. The String consists of the name of the VK constants of the class
   * KeyEvent without VK_*.
   *
   * @param keyString the string
   * @return the mnemonic as integer
   */
  private Integer createMnemonic( final String keyString ) {
    if ( keyString == null ) {
      throw new NullPointerException( "Key is null." );
    }
    if ( keyString.length() == 0 ) {
      throw new IllegalArgumentException( "Key is empty." );
    }
    int character = keyString.charAt( 0 );
    if ( keyString.startsWith( "VK_" ) ) // NON-NLS
    {
      try {
        final Field f = KeyEvent.class.getField( keyString );
        final Integer keyCode = (Integer) f.get( null );
        character = keyCode.intValue();
      } catch ( Exception nsfe ) {
        // ignore the exception ...
      }
    }
    return Integer.valueOf( character );
  }

  /**
   * Returns the plattforms default menu shortcut keymask.
   *
   * @return the default key mask.
   */
  private int getMenuKeyMask() {
    try {
      return Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
    } catch ( UnsupportedOperationException he ) {
      // headless exception extends UnsupportedOperation exception,
      // but the HeadlessException is not defined in older JDKs...
      return InputEvent.CTRL_MASK;
    }
  }

  /**
   * Creates a transparent image.  These can be used for aligning menu items.
   *
   * @param width  the width.
   * @param height the height.
   * @return the created transparent image.
   */
  private BufferedImage createTransparentImage( final int width,
                                                final int height ) {
    final BufferedImage img = new BufferedImage( width, height, BufferedImage.TYPE_INT_ARGB );
    final int[] data = img.getRGB( 0, 0, width, height, null, 0, width );
    img.setRGB( 0, 0, width, height, data, 0, width );
    return img;
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key       the resourcebundle key
   * @param parameter the parameter for the message
   * @return the formated string
   */
  public String formatMessage( final String key, final Object parameter ) {
    return formatMessage( key, new Object[] { parameter } );
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key  the resourcebundle key
   * @param par1 the first parameter for the message
   * @param par2 the second parameter for the message
   * @return the formated string
   */
  public String formatMessage( final String key,
                               final Object par1,
                               final Object par2 ) {
    return formatMessage( key, new Object[] { par1, par2 } );
  }

  /**
   * Formats the message stored in the resource bundle (using a MessageFormat).
   *
   * @param key        the resourcebundle key
   * @param parameters the parameter collection for the message
   * @return the formated string
   */
  public String formatMessage( final String key, final Object[] parameters ) {
    final MessageFormat format = new MessageFormat( strictString( key ) );
    format.setLocale( getLocale() );
    return format.format( parameters );
  }

  public String getString( final String key, final Object[] parameters ) {
    try {
      return formatMessage( key, parameters );
    } catch ( MissingResourceException mre ) {
      logger.warn( "ResourceBundleSupport#getString(,,)", mre );
      return '!' + key + '!';
    }
  }

  public String getString( final String key ) {
    try {
      return strictString( key );
    } catch ( MissingResourceException mre ) {
      logger.warn( "ResourceBundleSupport#getString(,,)", mre );
      return '!' + key + '!';
    }
  }

  public String getOptionalString( final String key ) {
    try {
      return strictString( key );
    } catch ( Exception e ) {
      logger.trace( "Optional String is undefined", e );
      // ignore it
      return null;
    }
  }

  public String getString( final String key,
                           final Object par1 ) {
    try {
      return formatMessage( key, par1 );
    } catch ( MissingResourceException mre ) {
      logger.warn( "ResourceBundleSupport#getString(,,)", mre );
      return '!' + key + '!';
    }
  }


  public String getString( final String key,
                           final Object par1,
                           final Object par2 ) {
    try {
      return formatMessage( key, par1, par2 );
    } catch ( MissingResourceException mre ) {
      logger.warn( "ResourceBundleSupport#getString(,,)", mre );
      return '!' + key + '!';
    }
  }


  /**
   * Returns the current locale for this resource bundle.
   *
   * @return the locale.
   */
  public Locale getLocale() {
    return locale;
  }
}
