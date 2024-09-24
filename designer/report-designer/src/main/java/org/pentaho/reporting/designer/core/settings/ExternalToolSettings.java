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

package org.pentaho.reporting.designer.core.settings;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 * User: Martin Date: 28.05.2006 Time: 09:58:37
 */
public class ExternalToolSettings {
  private static ExternalToolSettings instance;
  private static final String USE_DEFAULT_PDFVIEWER_KEY = "UseDefaultPDFViewer";
  private static final String CUSTOM_PDFVIEWER_EXECUTABLE_KEY = "CustomPDFViewerExecutable";
  private static final String CUSTOM_PDFVIEWER_PARAMETERS_KEY = "CustomPDFViewerParameters";
  private static final String USE_DEFAULT_RTFVIEWER_KEY = "UseDefaultRTFViewer";
  private static final String CUSTOM_RTFVIEWER_PARAMETERS_KEY = "CustomRTFViewerParameters";
  private static final String CUSTOM_RTFVIEWER_EXECUTABLE_KEY = "CustomRTFViewerExecutable";
  private static final String USE_DEFAULT_XLSVIEWER_KEY = "UseDefaultXLSViewer";
  private static final String CUSTOM_XLSVIEWER_EXECUTABLE_KEY = "CustomXLSViewerExecutable";
  private static final String CUSTOM_XLSVIEWER_PARAMETERS_KEY = "CustomXLSViewerParameters";
  private static final String USE_DEFAULT_CSVVIEWER_KEY = "UseDefaultCSVViewer";
  private static final String CUSTOM_CSVVIEWER_EXECUTABLE_KEY = "CustomCSVViewerExecutable";
  private static final String CUSTOM_CSVVIEWER_PARAMETERS_KEY = "CustomCSVViewerParameters";
  private static final String USE_DEFAULT_BROWSER_KEY = "UseDefaultBrowser";
  private static final String CUSTOM_BROWSER_EXECUTABLE_KEY = "CustomBrowserExecutable";
  private static final String CUSTOM_BROWSER_PARAMETERS_KEY = "CustomBrowserParameters";

  public static synchronized ExternalToolSettings getInstance() {
    if ( instance == null ) {
      instance = new ExternalToolSettings();
    }
    return instance;
  }

  private Preferences preferences;

  public ExternalToolSettings() {
    preferences = Preferences.userRoot().node
      ( "org/pentaho/reporting/designer/application-settings/external-tools-settings" ); // NON-NLS
  }

  public boolean isUseDefaultBrowser() {
    return preferences.getBoolean( USE_DEFAULT_BROWSER_KEY, true );
  }

  public void setUseDefaultBrowser( final boolean useDefaultBrowser ) {
    preferences.put( USE_DEFAULT_BROWSER_KEY, String.valueOf( useDefaultBrowser ) );
  }

  public String getCustomBrowserExecutable() {
    return preferences.get( CUSTOM_BROWSER_EXECUTABLE_KEY, null );
  }

  public void setCustomBrowserExecutable( final String customBrowserExecutable ) {
    preferences.put( CUSTOM_BROWSER_EXECUTABLE_KEY, String.valueOf( customBrowserExecutable ) );
  }

  public String getCustomBrowserParameters() {
    return preferences.get( CUSTOM_BROWSER_PARAMETERS_KEY, "{0}" );
  }

  public void setCustomBrowserParameters( final String customBrowserParameters ) {
    preferences.put( CUSTOM_BROWSER_PARAMETERS_KEY, String.valueOf( customBrowserParameters ) );
  }

  public boolean isUseDefaultPDFViewer() {
    return preferences.getBoolean( USE_DEFAULT_PDFVIEWER_KEY, true );
  }


  public void setUseDefaultPDFViewer( final boolean useDefaultPDFViewer ) {
    preferences.putBoolean( USE_DEFAULT_PDFVIEWER_KEY, useDefaultPDFViewer );
  }

  public String getCustomPDFViewerExecutable() {
    return preferences.get( CUSTOM_PDFVIEWER_EXECUTABLE_KEY, null );
  }

  public void setCustomPDFViewerExecutable( final String customPDFViewerExecutable ) {
    preferences.put( CUSTOM_PDFVIEWER_EXECUTABLE_KEY, customPDFViewerExecutable );
  }

  public String getCustomPDFViewerParameters() {
    return preferences.get( CUSTOM_PDFVIEWER_PARAMETERS_KEY, "{0}" );
  }

  public void setCustomPDFViewerParameters( final String customPDFViewerParameters ) {
    preferences.put( CUSTOM_PDFVIEWER_PARAMETERS_KEY, customPDFViewerParameters );
  }

  public boolean isUseDefaultRTFViewer() {
    return preferences.getBoolean( USE_DEFAULT_RTFVIEWER_KEY, true );
  }

  public void setUseDefaultRTFViewer( final boolean useDefaultRTFViewer ) {
    preferences.putBoolean( USE_DEFAULT_RTFVIEWER_KEY, useDefaultRTFViewer );
  }

  public String getCustomRTFViewerExecutable() {
    return preferences.get( CUSTOM_RTFVIEWER_EXECUTABLE_KEY, null );
  }

  public void setCustomRTFViewerExecutable( final String customRTFViewerExecutable ) {
    preferences.put( CUSTOM_RTFVIEWER_EXECUTABLE_KEY, customRTFViewerExecutable );
  }

  public String getCustomRTFViewerParameters() {
    return preferences.get( CUSTOM_RTFVIEWER_PARAMETERS_KEY, "{0}" );
  }

  public void setCustomRTFViewerParameters( final String customRTFViewerParameters ) {
    preferences.put( CUSTOM_RTFVIEWER_PARAMETERS_KEY, customRTFViewerParameters );
  }

  public boolean isUseDefaultXLSViewer() {
    return preferences.getBoolean( USE_DEFAULT_XLSVIEWER_KEY, true );
  }

  public void setUseDefaultXLSViewer( final boolean useDefaultXLSViewer ) {
    preferences.putBoolean( USE_DEFAULT_XLSVIEWER_KEY, useDefaultXLSViewer );
  }

  public String getCustomXLSViewerExecutable() {
    return preferences.get( CUSTOM_XLSVIEWER_EXECUTABLE_KEY, null );
  }

  public void setCustomXLSViewerExecutable( final String customXLSViewerExecutable ) {
    preferences.put( CUSTOM_XLSVIEWER_EXECUTABLE_KEY, customXLSViewerExecutable );
  }

  public String getCustomXLSViewerParameters() {
    return preferences.get( CUSTOM_XLSVIEWER_PARAMETERS_KEY, "{0}" );
  }

  public void setCustomXLSViewerParameters( final String customXLSViewerParameters ) {
    preferences.put( CUSTOM_XLSVIEWER_PARAMETERS_KEY, customXLSViewerParameters );
  }

  public boolean isUseDefaultCSVViewer() {
    return preferences.getBoolean( USE_DEFAULT_CSVVIEWER_KEY, true );
  }

  public void setUseDefaultCSVViewer( final boolean useDefaultCSVViewer ) {
    preferences.putBoolean( USE_DEFAULT_CSVVIEWER_KEY, useDefaultCSVViewer );
  }

  public String getCustomCSVViewerExecutable() {
    return preferences.get( CUSTOM_CSVVIEWER_EXECUTABLE_KEY, null );
  }

  public void setCustomCSVViewerExecutable( final String customCSVViewerExecutable ) {
    preferences.put( CUSTOM_CSVVIEWER_EXECUTABLE_KEY, customCSVViewerExecutable );
  }

  public String getCustomCSVViewerParameters() {
    return preferences.get( CUSTOM_CSVVIEWER_PARAMETERS_KEY, "{0}" );
  }

  public void setCustomCSVViewerParameters( final String customCSVViewerParameters ) {
    preferences.put( CUSTOM_CSVVIEWER_PARAMETERS_KEY, customCSVViewerParameters );
  }

  public void flush() {
    try {
      preferences.flush();
    } catch ( BackingStoreException e ) {
      e.printStackTrace();
    }
  }
}
