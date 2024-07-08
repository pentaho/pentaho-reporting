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
* Copyright (c) 2024 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.settings;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.util.DrawSelectionType;
import org.pentaho.reporting.designer.core.util.Unit;
import org.pentaho.reporting.engine.classic.core.metadata.MaturityLevel;
import org.pentaho.reporting.engine.classic.core.metadata.MetaData;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.ColorUtility;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.WeakEventListenerList;
import org.pentaho.reporting.libraries.designtime.swing.settings.LocaleSettings;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

import java.awt.*;
import java.util.Locale;
import java.util.TimeZone;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * User: Martin Date: 07.02.2006 Time: 14:14:08
 */
public class WorkspaceSettings implements LocaleSettings {
  private static final String SHOW_LAUNCHER_KEY = "ShowLauncher";

  private static WorkspaceSettings instance;
  private static final String GRID_DIVISIONS_KEY = "GridDivisions";
  private static final String REPORT_DESIGNER_BOUNDS_KEY = "ReportDesignerBounds";
  private static final String FIELD_PALETTE_BOUNDS_KEY = "FieldPaletteBounds";
  private static final String LNF_KEY = "lnf";
  private static final String SNAP_TO_GRID_KEY = "SnapToGrid";
  private static final String SHOW_GRID_KEY = "ShowGrid";
  private static final String ALWAYS_DRAW_ELEMENT_BORDER_KEY = "AlwaysDrawElementBorder";
  private static final String LAST_PROMPTED_VERSION_UPDATE_KEY = "LastPromptedVersionUpdate";
  private static final String GRID_SIZE_KEY = "GridSize";
  private static final String UNIT_KEY = "Unit";
  private static final String OFFLINE_MODE_KEY = "OfflineMode";
  private static final String SPLASH_SCREEN_VISIBLE = "SplashScreenVisible";
  private static final String EXPERIMENTAL_FEATURES_KEY = "ExperimentalFeatures";
  private static final String NON_CORE_FEATURES_KEY = "NonCoreFeatures";
  private static final String DEBUG_FEATURES_KEY = "DebugFeatures";
  private static final String MATURITY_LEVEL_KEY = "MaturityLevel";
  private static final String FIELD_SELECTOR_VISIBLE_KEY = "FieldSelectorVisible";
  private static final String DRAW_SELECTION_TYPE_KEY = "DrawSelectionType";
  private static final String SNAP_TO_GUIDE_LINES_KEY = "SnapToGuideLines";
  private static final String SNAP_TO_ELEMENTS_KEY = "SnapToElements";
  private static final String SHOW_ELEMENT_ALIGNMENT_HINTS_KEY = "ShowElementAlignmentHints";
  private static final String OVERLAPPING_ELEMENT_HIGHLIGHT_KEY = "Overlapping-Element-Highlight";
  private static final String ELEMENT_DISPLAY_STYLE_KEY = "ElementDisplayStyle";
  private static final String DISPLAY_STYLE_NAMES = "Names";
  private static final String DISPLAY_STYLE_VALUES = "Values";
  private static final String DATE_FORMAT_PATTERN = "DateFormatPattern";
  private static final String TIME_FORMAT_PATTERN = "TimeFormatPattern";
  private static final String DATETIME_FORMAT_PATTERN = "DatetimeFormatPattern";
  public static final String DATETIME_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss.SSSS";
  public static final String TIME_FORMAT_DEFAULT = "HH:mm:ss.SSSS";
  public static final String DATE_FORMAT_DEFAULT = "yyyy-MM-dd";
  private static final String STORE_PASSWORDS = "StorePasswords";
  private static final String CONNECTION_TIMEOUT = "ConnectionTimeout";
  private static final String SHOW_INDEX_COLUMNS = "ShowIndexColumns";
  private static final String SHOW_DEPRECATED_ITEMS = "ShowDeprecatedItems";
  private static final String SHOW_EXPERT_ITEMS = "ShowExpertItems";
  private static final String REOPEN_LAST_REPORT = "ReopenLastReport";
  private static final String SNAP_THRESHOLD = "SnapThreshold";
  private static final String GRID_COLOR = "GridColor";
  private static final String GUIDE_COLOR = "GuideColor";
  private static final String ALIGNMENT_HINT_COLOR = "AlignmentHintColor";
  private static final String OVERLAP_HINT_COLOR = "OverlapErrorColor";

  private static final Color GRID_COLOR_DEFAULT = new Color( 228, 228, 228 );
  private static final Color GUIDE_COLOR_DEFAULT = new Color( 0, 139, 237 );
  private static final Color ALIGNMENT_HINT_COLOR_DEFAULT = new Color( 228, 228, 228 );
  private static final Color OVERLAP_HINT_COLOR_DEFAULT = new Color( 255, 128, 128 );

  public static synchronized WorkspaceSettings getInstance() {
    if ( instance == null ) {
      instance = new WorkspaceSettings();
    }
    return instance;
  }

  private Preferences properties;
  private WeakEventListenerList settingsListeners;

  private WorkspaceSettings() {
    properties = Preferences.userRoot().node( "org/pentaho/reporting/designer/workspace-settings" ); // NON-NLS
    settingsListeners = new WeakEventListenerList();
  }

  public void flush() {
    try {
      properties.flush();
    } catch ( BackingStoreException e ) {
      // ignore, we cant do anything about it.
      e.printStackTrace();
    }
  }

  private void put( final String key, final String value ) {
    if ( key == null ) {
      throw new IllegalArgumentException( "key must not be null" );
    }

    if ( StringUtils.isEmpty( value ) ) {
      properties.remove( key );
    } else {
      properties.put( key, value );
    }
    fireSettingsChanged();
  }

  private boolean getBoolean( final String key, final boolean defaultValue ) {
    final String value = properties.get( key, null );
    if ( value == null ) {
      return defaultValue;
    }
    return Boolean.valueOf( value );
  }

  private String getString( final String key ) {
    return properties.get( key, null );
  }

  public void addSettingsListener( final SettingsListener listener ) {
    settingsListeners.add( SettingsListener.class, listener );
  }

  public void removeSettingsListener( final SettingsListener listener ) {
    settingsListeners.remove( SettingsListener.class, listener );
  }

  public void fireSettingsChanged() {
    final SettingsListener[] listeners = settingsListeners.getListeners( SettingsListener.class );
    for ( int i = 0; i < listeners.length; i++ ) {
      final SettingsListener listener = listeners[ i ];
      listener.settingsChanged();
    }
  }

  public boolean isReopenLastReport() {
    return getBoolean( REOPEN_LAST_REPORT, false );
  }

  public void setReopenLastReport( final boolean b ) {
    put( REOPEN_LAST_REPORT, String.valueOf( b ) );
  }

  public boolean isShowExpertItems() {
    return getBoolean( SHOW_EXPERT_ITEMS, true );
  }

  public void setShowExpertItems( final boolean b ) {
    put( SHOW_EXPERT_ITEMS, String.valueOf( b ) );
  }

  public boolean isSplashScreenVisible() {
    return getBoolean( SPLASH_SCREEN_VISIBLE, true );
  }

  public void setSplashScreenVisible( final boolean b ) {
    put( SPLASH_SCREEN_VISIBLE, String.valueOf( b ) );
  }

  public boolean isShowDeprecatedItems() {
    return getBoolean( SHOW_DEPRECATED_ITEMS, false );
  }

  public void setShowDeprecatedItems( final boolean b ) {
    put( SHOW_DEPRECATED_ITEMS, String.valueOf( b ) );
  }

  public boolean isShowOverlappingElements() {
    return getBoolean( OVERLAPPING_ELEMENT_HIGHLIGHT_KEY, true );
  }

  public void setShowOverlappingElements( final boolean b ) {
    put( OVERLAPPING_ELEMENT_HIGHLIGHT_KEY, String.valueOf( b ) );
  }

  public boolean isShowElementAlignmentHints() {
    return properties.getBoolean( SHOW_ELEMENT_ALIGNMENT_HINTS_KEY, false );
  }

  public void setShowElementAlignmentHints( final boolean showElementAlignmentHints ) {
    properties.put( SHOW_ELEMENT_ALIGNMENT_HINTS_KEY, String.valueOf( showElementAlignmentHints ) );
    fireSettingsChanged();
  }


  public boolean isSnapToElements() {
    return properties.getBoolean( SNAP_TO_ELEMENTS_KEY, true );
  }


  public void setSnapToElements( final boolean snapToElements ) {
    properties.put( SNAP_TO_ELEMENTS_KEY, String.valueOf( snapToElements ) );
    fireSettingsChanged();
  }

  public boolean isSnapToGuideLines() {
    return properties.getBoolean( SNAP_TO_GUIDE_LINES_KEY, false );
  }


  public void setSnapToGuideLines( final boolean snapToElements ) {
    properties.put( SNAP_TO_GUIDE_LINES_KEY, String.valueOf( snapToElements ) );
    fireSettingsChanged();
  }

  public DrawSelectionType getDrawSelectionType() {
    final String unitText = properties.get( DRAW_SELECTION_TYPE_KEY, DrawSelectionType.OUTLINE.toString() );
    try {
      return DrawSelectionType.valueOf( unitText );
    } catch ( Exception e ) {
      // ignored ..
      return DrawSelectionType.OUTLINE;
    }
  }

  public void setDrawSelectionType( final DrawSelectionType unit ) {
    if ( unit == null ) {
      throw new IllegalArgumentException( "DrawSelectionType must not be null" );
    }
    properties.put( DRAW_SELECTION_TYPE_KEY, String.valueOf( unit ) );
    fireSettingsChanged();
  }

  public void setBounds( final Rectangle rectangle ) {
    final String value = LibSwingUtil.rectangleToString( rectangle );
    properties.put( REPORT_DESIGNER_BOUNDS_KEY, value );
  }

  public Rectangle getBounds() {
    final String boundsAsText = properties.get( REPORT_DESIGNER_BOUNDS_KEY, "" );
    return LibSwingUtil.parseRectangle( boundsAsText );
  }

  public void setFieldPaletteBounds( final Rectangle rectangle ) {
    final String value = LibSwingUtil.rectangleToString( rectangle );
    properties.put( FIELD_PALETTE_BOUNDS_KEY, value );
  }

  public Rectangle getFieldPaletteBounds() {
    final String theReportDesignerBounds = properties.get( FIELD_PALETTE_BOUNDS_KEY, "" );
    return LibSwingUtil.parseRectangle( theReportDesignerBounds );
  }

  public void setLNF( final String lnf ) {
    properties.put( LNF_KEY, lnf );
    fireSettingsChanged();
    flush();
  }

  public String getLNF() {
    return properties.get( LNF_KEY, null );
  }

  public boolean isSnapToGrid() {
    return properties.getBoolean( SNAP_TO_GRID_KEY, false );
  }

  public void setSnapToGrid( final boolean snapToGrid ) {
    properties.put( SNAP_TO_GRID_KEY, String.valueOf( snapToGrid ) );
    fireSettingsChanged();
  }

  public boolean isShowGrid() {
    return properties.getBoolean( SHOW_GRID_KEY, true );
  }

  public void setShowGrid( final boolean showGrid ) {
    properties.put( SHOW_GRID_KEY, String.valueOf( showGrid ) );
    fireSettingsChanged();
  }

  public boolean isAlwaysDrawElementFrames() {
    return properties.getBoolean( ALWAYS_DRAW_ELEMENT_BORDER_KEY, false );
  }

  public void setAlwaysDrawElementFrames( final boolean alwaysDrawElementFrames ) {
    properties.put( ALWAYS_DRAW_ELEMENT_BORDER_KEY, String.valueOf( alwaysDrawElementFrames ) );
    fireSettingsChanged();
  }

  public double getGridSize() {
    final String gridSizeStr = properties.get( GRID_SIZE_KEY, null );
    return ParserUtil.parseFloat( gridSizeStr, 5 );
  }

  public void setGridSize( final double gridSize ) {
    properties.put( GRID_SIZE_KEY, String.valueOf( gridSize ) );
    fireSettingsChanged();
  }

  public int getGridDivisions() {
    final String gridDivisionStr = properties.get( GRID_DIVISIONS_KEY, null );
    return ParserUtil.parseInt( gridDivisionStr, 10 );
  }

  public void setGridDivisions( final int gridDivisions ) {
    properties.put( GRID_DIVISIONS_KEY, String.valueOf( gridDivisions ) );
    fireSettingsChanged();
  }

  public Unit getUnit() {
    // default unit is INCH, per PRD-986
    final String unitText = properties.get( UNIT_KEY, Unit.INCH.toString() );
    try {
      return Unit.valueOf( unitText );
    } catch ( Exception e ) {
      // ignored
      return Unit.INCH;
    }
  }

  public void setUnit( final Unit unit ) {
    if ( unit == null ) {
      throw new IllegalArgumentException( "unit must not be null" );
    }
    properties.put( UNIT_KEY, String.valueOf( unit ) );
    fireSettingsChanged();
  }

  public boolean isOfflineMode() {
    return properties.getBoolean( OFFLINE_MODE_KEY, false );
  }

  public void setOfflineMode( final boolean snapToGrid ) {
    properties.put( OFFLINE_MODE_KEY, String.valueOf( snapToGrid ) );
    fireSettingsChanged();
  }

  public boolean isExperimentalFeaturesVisible() {
    return getMaturityLevel().isExperimental();
  }

  public MaturityLevel getMaturityLevel() {
    MaturityLevel ml = MaturityLevel.Production;
    try {
      final String matLevel = ReportDesignerBoot.getInstance().getGlobalConfig().getConfigProperty
        ( "org.pentaho.reporting.designer.core.settings.MaturityLevel" );
      if ( matLevel != null ) {
        ml = MaturityLevel.valueOf( matLevel );
      }
    } catch ( final IllegalArgumentException e ) {
      // ignore ..
      ml = MaturityLevel.Production;
    }

    String defaultValue = properties.getBoolean( EXPERIMENTAL_FEATURES_KEY, false ) ?
      MaturityLevel.Development.toString() : ml.toString();

    try {
      String s = properties.get( MATURITY_LEVEL_KEY, defaultValue );
      if ( s != null ) {
        return MaturityLevel.valueOf( s );
      }
    } catch ( IllegalArgumentException e ) {
      // ignore ..
    }
    return MaturityLevel.Production;
  }

  public boolean isMatureFeature( final MaturityLevel ml ) {
    return ( getMaturityLevel().isMature( ml ) );
  }

  public boolean isMatureFeature( final MetaData ml ) {
    if ( ml == null ) {
      return false;
    }
    return ( getMaturityLevel().isMature( ml.getFeatureMaturityLevel() ) );
  }

  public boolean isVisible( final MetaData ml ) {
    if ( ml == null ) {
      return false;
    }
    if ( !isMatureFeature( ml ) ) {
      return false;
    }
    if ( isShowExpertItems() == false && ml.isExpert() ) {
      return false;
    }
    if ( isShowDeprecatedItems() == false && ml.isDeprecated() ) {
      return false;
    }

    return true;
  }


  public void setMaturityLevel( final MaturityLevel ml ) {
    if ( ml == null ) {
      properties.remove( MATURITY_LEVEL_KEY );
    } else {
      properties.put( MATURITY_LEVEL_KEY, ml.toString() );
    }
    fireSettingsChanged();
  }

  @Deprecated
  public void setExperimentalFeaturesVisible( final boolean snapToGrid ) {
    properties.put( EXPERIMENTAL_FEATURES_KEY, String.valueOf( snapToGrid ) );
    fireSettingsChanged();
  }

  public boolean isDebugFeaturesVisible() {
    return properties.getBoolean( DEBUG_FEATURES_KEY, false );
  }

  public void setDebugFeaturesVisible( final boolean snapToGrid ) {
    properties.put( DEBUG_FEATURES_KEY, String.valueOf( snapToGrid ) );
    fireSettingsChanged();
  }

  @Deprecated
  public boolean isNonCoreFeaturesVisible() {
    return properties.getBoolean( NON_CORE_FEATURES_KEY, false );
  }

  @Deprecated
  public void setNonCoreFeaturesVisible( final boolean snapToGrid ) {
    properties.put( NON_CORE_FEATURES_KEY, String.valueOf( snapToGrid ) );
    fireSettingsChanged();
  }

  public boolean isFieldSelectorVisible() {
    return properties.getBoolean( FIELD_SELECTOR_VISIBLE_KEY, false );
  }

  public void setFieldSelectorVisible( final boolean snapToGrid ) {
    properties.put( FIELD_SELECTOR_VISIBLE_KEY, String.valueOf( snapToGrid ) );
    fireSettingsChanged();
  }

  public void setShowLauncher( final boolean flag ) {
    properties.put( SHOW_LAUNCHER_KEY, String.valueOf( flag ) );
    fireSettingsChanged();
  }

  public boolean isShowLauncher() {
    return properties.getBoolean( SHOW_LAUNCHER_KEY, true );
  }

  public boolean isElementsDisplayNames() {
    return DISPLAY_STYLE_NAMES.equals( getString( ELEMENT_DISPLAY_STYLE_KEY ) );
  }

  public boolean isElementsDisplayValues() {
    final String object = getString( ELEMENT_DISPLAY_STYLE_KEY );
    return object == null || DISPLAY_STYLE_VALUES.equals( object );
  }

  public void setElementsDisplayNames() {
    put( ELEMENT_DISPLAY_STYLE_KEY, DISPLAY_STYLE_NAMES );
  }

  public void setElementsDisplayValues() {
    put( ELEMENT_DISPLAY_STYLE_KEY, DISPLAY_STYLE_VALUES );
  }

  public String getDateFormatPattern() {
    final String s = getString( DATE_FORMAT_PATTERN );
    if ( StringUtils.isEmpty( s ) ) {
      return DATE_FORMAT_DEFAULT;// NON-NLS
    }
    return s;
  }

  public void setDateFormatPattern( final String dateFormatPattern ) {
    put( DATE_FORMAT_PATTERN, dateFormatPattern );
  }

  public String getTimeFormatPattern() {
    final String s = getString( TIME_FORMAT_PATTERN );
    if ( StringUtils.isEmpty( s ) ) {
      return TIME_FORMAT_DEFAULT; // NON-NLS
    }
    return s;
  }

  public void setTimeFormatPattern( final String timeFormatPattern ) {
    put( TIME_FORMAT_PATTERN, timeFormatPattern );
  }

  public String getDatetimeFormatPattern() {
    final String s = getString( DATETIME_FORMAT_PATTERN );
    if ( StringUtils.isEmpty( s ) ) {
      return DATETIME_FORMAT_DEFAULT;// NON-NLS
    }
    return s;
  }

  public void setDatetimeFormatPattern( final String datetimeFormatPattern ) {
    put( DATETIME_FORMAT_PATTERN, datetimeFormatPattern );
  }

  public Locale getLocale() {
    return Locale.getDefault();
  }

  public TimeZone getTimeZone() {
    return TimeZone.getDefault();
  }

  public boolean isRememberPasswords() {
    return getBoolean( STORE_PASSWORDS, true );
  }

  public void setRememberPasswords( final boolean flag ) {
    put( STORE_PASSWORDS, String.valueOf( flag ) );
  }

  public int getConnectionTimeout() {
    final String connectionTimeoutStr = properties.get( CONNECTION_TIMEOUT, null );
    return ParserUtil.parseInt( connectionTimeoutStr, 30 );
  }

  public void setConnectionTimeout( final int connectionTimeout ) {
    put( CONNECTION_TIMEOUT, String.valueOf( connectionTimeout ) );
  }

  public boolean isShowIndexColumns() {
    return getBoolean( SHOW_INDEX_COLUMNS, false );
  }

  public void setShowIndexColumns( final boolean flag ) {
    put( SHOW_INDEX_COLUMNS, String.valueOf( flag ) );
  }

  public long getSnapThreshold() {
    final String snapThresholdStr = properties.get( SNAP_THRESHOLD, null );
    return StrictGeomUtility.toInternalValue( ParserUtil.parseInt( snapThresholdStr, 5 ) );
  }


  public Color getGridColor() {
    return getColor( GRID_COLOR, GRID_COLOR_DEFAULT );
  }

  public void setGridColor( final Color value ) {

    final String text = ColorUtility.toAttributeValue( value );
    if ( text == null ) {
      properties.remove( GRID_COLOR );
    } else {
      properties.put( GRID_COLOR, text );
    }
  }

  public Color getGuideColor() {
    return getColor( GUIDE_COLOR, GUIDE_COLOR_DEFAULT );
  }

  public void setGuideColor( final Color value ) {

    final String text = ColorUtility.toAttributeValue( value );
    if ( text == null ) {
      properties.remove( GUIDE_COLOR );
    } else {
      properties.put( GUIDE_COLOR, text );
    }
  }

  public Color getAlignmentHintColor() {
    return getColor( ALIGNMENT_HINT_COLOR, ALIGNMENT_HINT_COLOR_DEFAULT );
  }

  public void setAlignmentHintColor( final Color value ) {

    final String text = ColorUtility.toAttributeValue( value );
    if ( text == null ) {
      properties.remove( ALIGNMENT_HINT_COLOR );
    } else {
      properties.put( ALIGNMENT_HINT_COLOR, text );
    }
  }

  public Color getOverlapErrorColor() {
    return getColor( OVERLAP_HINT_COLOR, OVERLAP_HINT_COLOR_DEFAULT );
  }

  public void setOverlapErrorColor( final Color value ) {

    final String text = ColorUtility.toAttributeValue( value );
    if ( text == null ) {
      properties.remove( OVERLAP_HINT_COLOR );
    } else {
      properties.put( OVERLAP_HINT_COLOR, text );
    }
  }

  private Color getColor( final String property, final Color defaultValue ) {
    final String gridColorStr = properties.get( property, null );
    if ( gridColorStr == null ) {
      return defaultValue;
    }
    try {
      final Color color = ColorUtility.toPropertyValue( gridColorStr );
      if ( color == null ) {
        return defaultValue;
      }
      return color;
    } catch ( IllegalArgumentException e ) {
      return defaultValue;
    }
  }
}
