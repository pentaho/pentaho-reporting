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

package org.pentaho.reporting.designer.core.util;

import org.pentaho.reporting.designer.core.util.dnd.ClipboardManager;
import org.pentaho.reporting.libraries.base.util.SystemInformation;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.xmlns.writer.CharacterEntityParser;
import org.pentaho.reporting.libraries.xmlns.writer.HtmlCharacterEntities;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

@SuppressWarnings( { "HardcodedFileSeparator" } )
public class SystemInformationDialog extends CommonDialog {
  private class CopyAction extends AbstractAction {
    private CopyAction() {
      putValue( Action.NAME,
        UtilMessages.getInstance().getString( "SystemInformationDialog.CopyToClipboard" ) ); // NON-NLS
    }

    public void actionPerformed( final ActionEvent e ) {
      ClipboardManager.getManager().setRawContent( new StringSelection( getSystemInformationAsText() ) );
    }
  }

  private JEditorPane editorPane;

  public SystemInformationDialog()
    throws HeadlessException {
    init();
  }

  public SystemInformationDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  public SystemInformationDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    setTitle( UtilMessages.getInstance().getString( "SystemInformationDialog.Title" ) );

    editorPane = new JEditorPane();
    editorPane.setEditable( false );
    editorPane.setContentType( "text/html" );//NON-NLS
    editorPane.setText( getSystemInformationAsHTML() );
    final HTMLDocument htmlDocument = (HTMLDocument) editorPane.getDocument();
    htmlDocument.getStyleSheet().addRule( "body { font-family:sans-serif; }" );//NON-NLS
    editorPane.setCaretPosition( 0 );

    super.init();
    getConfirmAction().putValue( Action.NAME, UtilMessages.getInstance().getString( "SystemInformationDialog.Close" ) );
  }

  protected void performInitialResize() {
    pack();
    GUIUtils.ensureMinimumDialogSize( this, 400, 300 );
    GUIUtils.ensureMaximumDialogSize( this, 800, 600 );

    LibSwingUtil.centerDialogInParent( this );
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.SystemInformation";
  }

  public void performShow() {
    super.performEdit();
  }

  protected Component createContentPane() {
    return new JScrollPane( editorPane );
  }

  protected Action[] getExtraActions() {
    return new Action[] { new CopyAction() };
  }

  protected boolean hasCancelButton() {
    return false;
  }

  @SuppressWarnings( { "HardcodedLineSeparator" } )
  private String getSystemInformationAsHTML() {
    final CharacterEntityParser cep = HtmlCharacterEntities.getEntityParser();
    final StringBuilder sb = new StringBuilder( 10000 );
    sb.append( "<html><body>\n" );//NON-NLS

    sb.append( "<h1>" );//NON-NLS
    sb.append(
      cep.encodeEntities( UtilMessages.getInstance().getString( "SystemInformationDialog.SystemProperties.Title" ) ) );
    sb.append( "</h1>" );//NON-NLS
    formatMap( cep, sb, new TreeMap( System.getProperties() ) );
    sb.append( "<br>" );//NON-NLS

    //environment
    sb.append( "<h1>" );//NON-NLS
    sb.append(
      cep.encodeEntities( UtilMessages.getInstance().getString( "SystemInformationDialog.Environment.Title" ) ) );
    sb.append( "</h1>" );//NON-NLS
    formatMap( cep, sb, new TreeMap<String, String>( System.getenv() ) );
    sb.append( "<br>" );//NON-NLS

    //other
    sb.append( "<h1>" );//NON-NLS
    sb.append( cep.encodeEntities( UtilMessages.getInstance().getString( "SystemInformationDialog.Other.Title" ) ) );
    sb.append( "</h1>" );//NON-NLS
    formatMap( cep, sb, SystemInformation.getOtherProperties() );


    sb.append( "</body></html>\n" );//NON-NLS

    return sb.toString();
  }

  @SuppressWarnings( { "HardcodedLineSeparator" } )
  private String getSystemInformationAsText() {
    final CharacterEntityParser cep = HtmlCharacterEntities.getEntityParser();
    final StringBuilder sb = new StringBuilder( 10000 );

    final String sysPropTitle =
      UtilMessages.getInstance().getString( "SystemInformationDialog.SystemProperties.Title" );
    sb.append( sysPropTitle );
    sb.append( "\n" );//NON-NLS
    sb.append( printUnderline( sysPropTitle.length() ) );
    sb.append( "\n" );//NON-NLS
    sb.append( "\n" );//NON-NLS
    formatMapText( cep, sb, new TreeMap( System.getProperties() ) );
    sb.append( "\n" );//NON-NLS

    //environment
    sb.append( "\n" );//NON-NLS
    final String envTitle = UtilMessages.getInstance().getString( "SystemInformationDialog.Environment.Title" );
    sb.append( envTitle );
    sb.append( "\n" );//NON-NLS
    sb.append( printUnderline( envTitle.length() ) );
    sb.append( "\n" );//NON-NLS
    sb.append( "\n" );//NON-NLS
    formatMapText( cep, sb, new TreeMap<String, String>( System.getenv() ) );
    sb.append( "\n" );//NON-NLS

    //other
    sb.append( "\n" );//NON-NLS
    final String otherTitle = UtilMessages.getInstance().getString( "SystemInformationDialog.Other.Title" );
    sb.append( otherTitle );
    sb.append( "\n" );//NON-NLS
    sb.append( printUnderline( otherTitle.length() ) );
    sb.append( "\n" );//NON-NLS
    sb.append( "\n" );//NON-NLS
    formatMapText( cep, sb, SystemInformation.getOtherProperties() );
    sb.append( "\n" );//NON-NLS
    return sb.toString();
  }

  private String printUnderline( final int length ) {
    final StringBuilder b = new StringBuilder( length );
    for ( int i = 0; i < length; i += 1 ) {
      b.append( "=" );
    }
    return b.toString();
  }

  private void formatMapText( final CharacterEntityParser cep,
                              final StringBuilder sb,
                              final Map environmentMap ) {
    if ( environmentMap.isEmpty() ) {
      return;
    }

    for ( final Object entryRaw : environmentMap.entrySet() ) {
      final Map.Entry entry = (Map.Entry) entryRaw;
      final String key = String.valueOf( entry.getKey() );
      String value = (String) entry.getValue();
      if ( value != null ) {
        value = cep.encodeEntities( value );
        value = value.replace( "\n", "\\n" );//NON-NLS
        value = value.replace( "\f", "\\f" );//NON-NLS
        value = value.replace( "\r", "\\r" );//NON-NLS
        if ( value.length() > 80 ) {
          value = value.replace( File.pathSeparator, File.pathSeparator + "\n          " );//NON-NLS
        }
      }
      sb.append( key );
      sb.append( "=" );//NON-NLS
      sb.append( value );
      sb.append( "\n" );//NON-NLS
    }
  }

  private void formatMap( final CharacterEntityParser cep,
                          final StringBuilder sb,
                          final Map environmentMap ) {
    if ( environmentMap.isEmpty() ) {
      return;
    }

    sb.append( "<table>\n" );//NON-NLS
    for ( final Object entryRaw : environmentMap.entrySet() ) {
      final Map.Entry entry = (Map.Entry) entryRaw;
      final String key = String.valueOf( entry.getKey() );
      String value = (String) entry.getValue();
      if ( value != null ) {
        value = cep.encodeEntities( value );
        value = value.replace( "\n", "\\n" );//NON-NLS
        value = value.replace( "\f", "\\f" );//NON-NLS
        value = value.replace( "\r", "\\r" );//NON-NLS
        if ( value.length() > 80 ) {
          value = value.replace( File.pathSeparator, File.pathSeparator + "<br>\n" );//NON-NLS
        }
      }
      sb.append( "<tr valign=\"top\"><td>" );//NON-NLS
      sb.append( cep.encodeEntities( key ) );
      sb.append( "</td><td>" );//NON-NLS
      sb.append( value );
      sb.append( "</td></tr>\n" );//NON-NLS
    }
    sb.append( "</table>" );//NON-NLS
  }


}
