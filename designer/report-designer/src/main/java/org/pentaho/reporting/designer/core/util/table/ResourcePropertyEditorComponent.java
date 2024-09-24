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

package org.pentaho.reporting.designer.core.util.table;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.UtilMessages;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ClassicEngineFactoryParameters;
import org.pentaho.reporting.libraries.base.util.FilesystemFilter;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.event.DocumentChangeHandler;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.CommonFileChooser;
import org.pentaho.reporting.libraries.designtime.swing.filechooser.FileChooserService;
import org.pentaho.reporting.libraries.designtime.swing.propertyeditors.ValidatingPropertyEditorComponent;
import org.pentaho.reporting.libraries.repository.DefaultMimeRegistry;
import org.pentaho.reporting.libraries.resourceloader.LoaderParameterKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * A UI component which will capture a resource name / location and if that resource should be linked externally or
 * embedded in the report.
 *
 * @author Thomas Morgner
 * @author David Kincade
 * @author Ezequiel Cuellar
 */
public class ResourcePropertyEditorComponent extends JComponent implements ValidatingPropertyEditorComponent {
  /**
   * Enumeration defining the possible values for Document Locations
   */
  public enum DocumentLocation {
    LINK, EMBED
  }

  private static final DefaultMimeRegistry mimeRegistry = new DefaultMimeRegistry();

  private JRadioButton linkToRadio;
  private JRadioButton embedRadio;
  private JTextField sourceTextField;
  private ReportDocumentContext reportRenderContext;
  private Object currentValue;
  private String lastTextValue;
  private DocumentLocation lastDocumentLocation;
  private boolean disableEvents;

  /**
   * Constructor which sets up the location of all the elements on this component
   *
   * @param reportRenderContext the report render context for the current report
   */
  public ResourcePropertyEditorComponent( final ReportDocumentContext reportRenderContext ) {
    // Get the information used to embed items in the document bundle
    if ( reportRenderContext == null ) {
      throw new NullPointerException( "The ReportRenderContext must not be null" );
    }
    this.reportRenderContext = reportRenderContext;

    final SourceChangeHandler changeHandler = new SourceChangeHandler();
    sourceTextField = new JTextField( 35 );
    sourceTextField.getDocument().addDocumentListener( changeHandler );

    linkToRadio = new JRadioButton( UtilMessages.getInstance().getString( "ResourcePropertyEditorComponent.LinkTo" ) );
    linkToRadio.setSelected( true );
    linkToRadio.addChangeListener( changeHandler );

    embedRadio = new JRadioButton( UtilMessages.getInstance().getString( "ResourcePropertyEditorComponent.Embed" ) );
    embedRadio.addChangeListener( changeHandler );

    final JPanel identifierSelectorPanel = new JPanel( new BorderLayout() );
    identifierSelectorPanel.add( sourceTextField, BorderLayout.CENTER );
    identifierSelectorPanel.add( new JButton( new FileSelectAction() ), BorderLayout.EAST );

    final ButtonGroup buttonGroup = new ButtonGroup();
    buttonGroup.add( linkToRadio );
    buttonGroup.add( embedRadio );

    final JPanel identifierPane = new JPanel( new BorderLayout() );
    identifierPane.setBorder( BorderFactory.createEmptyBorder( 0, 6, 0, 0 ) );
    identifierPane
      .add( new JLabel( UtilMessages.getInstance().getString( "ResourcePropertyEditorComponent.SourceLabel" ) ),
        BorderLayout.NORTH );
    identifierPane.add( identifierSelectorPanel, BorderLayout.CENTER );

    final JPanel selectionPanel = new JPanel( new GridLayout( 2, 1 ) );
    selectionPanel.setBorder( BorderFactory.createEmptyBorder( 0, 5, 0, 0 ) );
    selectionPanel.add( linkToRadio );
    selectionPanel.add( embedRadio );

    setLayout( new BorderLayout() );
    add( identifierPane, BorderLayout.CENTER );
    add( selectionPanel, BorderLayout.SOUTH );
  }

  /**
   * Returns the current value of the property as a <code>ResourceKey</code>. <br/> If the key is embedded, additional
   * information will be stored in the <code>Factory Parameters</code> in the following way: <table>
   * <row><th>key</th><th>value</th></row> <row><td>original_value</td><td>The string version of the key that was
   * original entered</td></row> <row><td>embedded</td><td>Indicates if the resource should be / is embedded in the
   * document bundle</td></row> </table>
   *
   * @return the ResourceKey being created in this
   */
  public Object getValue() {
    // If nothing has changed since the last check, we should return the current value
    // instead of recomputing it
    final String currentTextValue = sourceTextField.getText();
    final DocumentLocation currentDocumentLocation = getDocumentLocation();
    if ( currentTextValue.equals( lastTextValue ) &&
      currentDocumentLocation.equals( lastDocumentLocation ) ) {
      return currentValue;
    }

    if ( StringUtils.isEmpty( currentTextValue ) ) {
      lastTextValue = currentTextValue;
      lastDocumentLocation = currentDocumentLocation;
      return null;
    }
    // Try to compute a ResourceKey from the current data
    try {
      currentValue = createResourceKey( currentTextValue, currentDocumentLocation );
    } catch ( Exception ignored ) {
      // The text does not yield a valid ResourceKey
      currentValue = currentTextValue;
    }

    lastTextValue = currentTextValue;
    lastDocumentLocation = currentDocumentLocation;
    return currentValue;
  }

  /**
   * Creates a ResourceKey based on the current values in this component.
   *
   * @param source the current location used in the creation of the resource key
   * @param docLoc the link/embed flag used in the creation of the resource key
   * @return the newly created ResourceKey
   * @throws ResourceKeyCreationException indicates the current values do not generate a valid ResourceKey
   */
  private Object createResourceKey( final String source,
                                    final DocumentLocation docLoc )
    throws ResourceKeyCreationException, IOException, ResourceLoadingException {
    if ( StringUtils.isEmpty( source, true ) ) {
      return null;
    }

    // If we are embedding the key, create some factory patameters
    if ( DocumentLocation.EMBED.equals( docLoc ) ) {
      final String mimeType = mimeRegistry.getMimeType( source );
      final String pattern = "resources/image{0}" + IOUtils.getInstance().getFileExtension( source ); // NON-NLS
      final Map<LoaderParameterKey, String> parameters = new HashMap<LoaderParameterKey, String>();
      parameters.put( ClassicEngineFactoryParameters.ORIGINAL_VALUE, source );
      parameters.put( ClassicEngineFactoryParameters.MIME_TYPE, mimeType );
      parameters.put( ClassicEngineFactoryParameters.PATTERN, pattern );
      parameters.put( ClassicEngineFactoryParameters.EMBED, "true" ); // NON-NLS
      // create an embedded key in here.
      final ResourceKey key = ResourceKeyUtils.toResourceKey
        ( source, reportRenderContext.getResourceManager(),
          reportRenderContext.getReportDefinition().getContentBase(), parameters );
      return ResourceKeyUtils.embedResourceInKey( reportRenderContext.getResourceManager(),
        key, key.getFactoryParameters() );
    }

    // See if we can create a valid resource key
    final ResourceKey contentBaseKey = reportRenderContext.getReportDefinition().getContentBase();
    final ResourceKey resourceKey = ResourceKeyUtils.toResourceKey
      ( source, reportRenderContext.getResourceManager(), contentBaseKey, Collections.EMPTY_MAP );
    if ( isDerivedKey( resourceKey, contentBaseKey ) ) {
      return source;
    }
    return resourceKey;
  }

  private boolean isDerivedKey( final ResourceKey key, final ResourceKey contentKey ) {
    if ( contentKey == null ) {
      return false;
    }
    if ( contentKey.getSchema().equals( key.getSchema() ) ) {
      return true;
    }
    return isDerivedKey( key, contentKey.getParent() );
  }

  /**
   * Sets the value of the source field
   *
   * @param value the current string value
   */
  public void setValue( final String value ) {
    sourceTextField.setText( value );
    setDocumentLocation( DocumentLocation.LINK );
    fireValueChangeEvent();
  }


  /**
   * Sets the value of the source field
   *
   * @param object the current value
   */
  public void setValue( final Object object ) {
    // Nothing to do if nothing is changing
    if ( ObjectUtilities.equal( currentValue, object ) ) {
      return;
    }

    try {
      disableEvents = true;
      // Set the internal fields properly
      if ( object == null ) {
        sourceTextField.setText( "" );
        setDocumentLocation( DocumentLocation.LINK );
      } else if ( object instanceof ResourceKey ) {
        // Is the resource key an embedded resource key?
        final ResourceKey resourceKey = (ResourceKey) object;
        final Object originalValue =
          resourceKey.getFactoryParameters().get( ClassicEngineFactoryParameters.ORIGINAL_VALUE );
        if ( originalValue != null ) {
          sourceTextField.setText( String.valueOf( originalValue ) );
          setDocumentLocation( DocumentLocation.EMBED );
        } else {
          sourceTextField.setText( resourceKey.getIdentifierAsString() );
          setDocumentLocation( DocumentLocation.LINK );
        }
      } else {
        sourceTextField.setText( String.valueOf( object ) );
        setDocumentLocation( DocumentLocation.LINK );
      }

      // Save the current values as the latest values
      currentValue = object;
    } finally {
      disableEvents = false;
    }
    // Let everyone know there has been a change in values
    fireValueChangeEvent();
  }

  /**
   * Returns the current value of the <code>embed / link</code> radio button
   *
   * @return the current value of the document location
   */
  private DocumentLocation getDocumentLocation() {
    return ( linkToRadio.isSelected() ? DocumentLocation.LINK : DocumentLocation.EMBED );
  }

  /**
   * Sets the document location to the specified value
   *
   * @param documentLocation the value of the document location
   */
  private void setDocumentLocation( final DocumentLocation documentLocation ) {
    if ( DocumentLocation.LINK.equals( documentLocation ) ) {
      linkToRadio.setSelected( true );
      embedRadio.setSelected( false );
    } else {
      linkToRadio.setSelected( false );
      embedRadio.setSelected( true );
    }
  }

  /**
   * Indicates if the current value is a valid value. The current value is valid if the document location is LINK, or
   * the current value is a ResourceKey and the document location is embed.
   */
  public boolean isValidEditorValue() {
    if ( getDocumentLocation().equals( DocumentLocation.LINK ) ) {
      return true;
    }

    final Object o = getValue();
    return o instanceof ResourceKey;
  }

  /**
   * Notifies all the listeners that a value on this component has changed
   */
  private void fireValueChangeEvent() {
    if ( disableEvents ) {
      return;
    }

    final Object oldValue = currentValue;
    final Object newValue = getValue();
    firePropertyChange( null, oldValue, newValue );
  }

  /**
   * Notifies all the listeners that a value on this component has changed
   */
  private void fireRadioChangeEvent() {
    if ( disableEvents ) {
      return;
    }

    final Object oldValue = lastDocumentLocation;
    final Object newValue = getDocumentLocation();
    firePropertyChange( null, oldValue, newValue );
  }


  /**
   * The class which will handle the notification changes when the source text field changes.
   */
  private class SourceChangeHandler extends DocumentChangeHandler implements ChangeListener {
    private SourceChangeHandler() {
    }

    /**
     * Indicates the radio button has changed
     */
    public void stateChanged( final ChangeEvent e ) {
      fireRadioChangeEvent();
    }

    /**
     * Indicates the text value changed
     */
    protected void handleChange( final DocumentEvent e ) {
      fireValueChangeEvent();
    }
  }

  private class FileSelectAction extends AbstractAction {
    private FileSelectAction() {
      putValue( Action.NAME, ".." );
    }

    public void actionPerformed( final ActionEvent aEvt ) {
      final File reportContextFile = DesignTimeUtil.getContextAsFile( reportRenderContext.getReportDefinition() );

      final FileFilter[] filters = {
        new FilesystemFilter( ".properties", // NON-NLS
          Messages.getString( "BundledResourceEditor.PropertiesTranslations" ) ),
        new FilesystemFilter( new String[] { ".xml", ".report", ".prpt", ".prpti", ".prptstyle" }, // NON-NLS
          Messages.getString( "BundledResourceEditor.Resources" ), true ),
        new FilesystemFilter( new String[] { ".gif", ".jpg", ".jpeg", ".png", ".svg", ".wmf" }, // NON-NLS
          Messages.getString( "BundledResourceEditor.Images" ), true ),
      };

      final CommonFileChooser fileChooser = FileChooserService.getInstance().getFileChooser( "resources" );//NON-NLS
      fileChooser.setFilters( filters );

      final String sourceFile = sourceTextField.getText();
      if ( StringUtils.isEmpty( sourceFile ) == false ) {
        if ( reportContextFile != null ) {
          fileChooser.setSelectedFile( new File( reportContextFile.getParentFile(), sourceTextField.getText() ) );
        } else {
          fileChooser.setSelectedFile( new File( sourceTextField.getText() ) );
        }
      }

      if ( fileChooser.showDialog( ResourcePropertyEditorComponent.this, JFileChooser.OPEN_DIALOG ) == false ) {
        return;
      }

      final File file = fileChooser.getSelectedFile();
      if ( file == null ) {
        return;
      }

      final String path;
      if ( reportContextFile != null ) {
        path = IOUtils.getInstance().createRelativePath( file.getPath(), reportContextFile.getAbsolutePath() );
      } else {
        path = file.getPath();
      }

      sourceTextField.setText( path );
    }
  }
}
