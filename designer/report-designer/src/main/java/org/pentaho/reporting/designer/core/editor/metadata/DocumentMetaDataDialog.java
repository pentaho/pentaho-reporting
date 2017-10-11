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

package org.pentaho.reporting.designer.core.editor.metadata;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentMetaData;
import org.pentaho.reporting.libraries.docbundle.ODFMetaAttributeNames;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;

public class DocumentMetaDataDialog extends CommonDialog {
  public static class EditResult {
    private String oldAuthor;
    private String newAuthor;
    private String oldTitle;
    private String newTitle;
    private String oldSubject;
    private String newSubject;
    private String oldKeywords;
    private String newKeywords;
    private String oldDescription;
    private String newDescription;

    public EditResult( final String oldAuthor,
                       final String newAuthor,
                       final String oldTitle,
                       final String newTitle,
                       final String oldSubject,
                       final String newSubject,
                       final String oldKeywords,
                       final String newKeywords,
                       final String oldDescription, final String newDescription ) {
      this.oldAuthor = oldAuthor;
      this.newAuthor = newAuthor;
      this.oldTitle = oldTitle;
      this.newTitle = newTitle;
      this.oldSubject = oldSubject;
      this.newSubject = newSubject;
      this.oldKeywords = oldKeywords;
      this.newKeywords = newKeywords;
      this.oldDescription = oldDescription;
      this.newDescription = newDescription;
    }

    public String getOldAuthor() {
      return oldAuthor;
    }

    public String getNewAuthor() {
      return newAuthor;
    }

    public String getOldTitle() {
      return oldTitle;
    }

    public String getNewTitle() {
      return newTitle;
    }

    public String getOldSubject() {
      return oldSubject;
    }

    public String getNewSubject() {
      return newSubject;
    }

    public String getOldKeywords() {
      return oldKeywords;
    }

    public String getNewKeywords() {
      return newKeywords;
    }

    public String getOldDescription() {
      return oldDescription;
    }

    public String getNewDescription() {
      return newDescription;
    }
  }

  private JLabel fileNameLabel;
  private JLabel locationLabel;
  private JLabel creationDateLabel;
  private JLabel modifiedDateLabel;
  private JLabel sizeLabel;

  private JTextField authorField;
  private JTextField titleField;
  private JTextField subjectField;
  private JTextField keywordsField;
  private JTextArea descriptionField;

  /**
   * Creates a non-modal dialog without a title and without a specified <code>Frame</code> owner.  A shared, hidden
   * frame will be set as the owner of the dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public DocumentMetaDataDialog()
    throws HeadlessException {
    init();
  }

  /**
   * Creates a non-modal dialog without a title with the specified <code>Frame</code> as its owner.  If
   * <code>owner</code> is <code>null</code>, a shared, hidden frame will be set as the owner of the dialog.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner the <code>Frame</code> from which the dialog is displayed
   * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public DocumentMetaDataDialog( final Frame owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  /**
   * Creates a non-modal dialog without a title with the specified <code>Dialog</code> as its owner.
   * <p/>
   * This constructor sets the component's locale property to the value returned by
   * <code>JComponent.getDefaultLocale</code>.
   *
   * @param owner the non-null <code>Dialog</code> from which the dialog is displayed
   * @throws java.awt.HeadlessException if GraphicsEnvironment.isHeadless() returns true.
   * @see java.awt.GraphicsEnvironment#isHeadless
   * @see javax.swing.JComponent#getDefaultLocale
   */
  public DocumentMetaDataDialog( final Dialog owner )
    throws HeadlessException {
    super( owner );
    init();
  }

  protected void init() {
    fileNameLabel = new JLabel();
    locationLabel = new JLabel();
    creationDateLabel = new JLabel();
    modifiedDateLabel = new JLabel();
    sizeLabel = new JLabel();

    authorField = new JTextField();
    titleField = new JTextField();
    subjectField = new JTextField();
    keywordsField = new JTextField();
    descriptionField = new JTextArea();

    setMinimumSize( new Dimension( 600, 400 ) );
    setTitle( Messages.getString( "DocumentMetaDataDialog.DialogTitle" ) );
    super.init();
  }

  protected void performInitialResize() {
    setSize( 800, 600 );
    LibSwingUtil.centerDialogInParent( this );
  }

  protected String getDialogId() {
    return "ReportDesigner.Core.DocumentMetaDataDialog";
  }

  protected Component createContentPane() {
    final JPanel generalPanel = new JPanel();
    generalPanel.setMinimumSize( new Dimension( 500, 300 ) );
    generalPanel.setLayout( new GridBagLayout() );
    generalPanel.setBorder( new EmptyBorder( 10, 0, 0, 0 ) );
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    generalPanel.add( new JLabel( IconLoader.getInstance().getAboutIcon() ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 2;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    generalPanel.add( fileNameLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 3;
    gbc.weightx = 1;
    gbc.ipadx = 250;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    generalPanel.add( new JSeparator(), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    generalPanel.add( new JLabel( Messages.getString( "DocumentMetaDataDialog.CreationDate" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 2;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    generalPanel.add( creationDateLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.gridwidth = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    generalPanel.add( new JLabel( Messages.getString( "DocumentMetaDataDialog.ModificationDate" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 3;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    generalPanel.add( modifiedDateLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    generalPanel.add( new JLabel( Messages.getString( "DocumentMetaDataDialog.Location" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 4;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    generalPanel.add( locationLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 5;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridwidth = 2;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    generalPanel.add( new JLabel( Messages.getString( "DocumentMetaDataDialog.Size" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 2;
    gbc.gridy = 5;
    gbc.weightx = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    gbc.fill = GridBagConstraints.HORIZONTAL;
    generalPanel.add( sizeLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 6;
    gbc.weighty = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.REMAINDER;
    generalPanel.add( new JPanel(), gbc );


    final JPanel descriptionPanel = new JPanel();
    descriptionPanel.setLayout( new GridBagLayout() );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridwidth = 1;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    descriptionPanel.add( new JLabel( Messages.getString( "DocumentMetaDataDialog.Author" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    descriptionPanel.add( authorField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridwidth = 1;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    descriptionPanel.add( new JLabel( Messages.getString( "DocumentMetaDataDialog.Title" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    descriptionPanel.add( titleField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridwidth = 1;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    descriptionPanel.add( new JLabel( Messages.getString( "DocumentMetaDataDialog.Subject" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    descriptionPanel.add( subjectField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.gridwidth = 1;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    descriptionPanel.add( new JLabel( Messages.getString( "DocumentMetaDataDialog.Keywords" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 3;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    descriptionPanel.add( keywordsField, gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 0;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.NORTHWEST;
    gbc.gridwidth = 1;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    descriptionPanel.add( new JLabel( Messages.getString( "DocumentMetaDataDialog.Description" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.gridx = 1;
    gbc.gridy = 4;
    gbc.anchor = GridBagConstraints.WEST;
    gbc.fill = GridBagConstraints.BOTH;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.insets = new Insets( 0, 5, 2, 5 );
    descriptionPanel.add( new JScrollPane( descriptionField ), gbc );


    final JTabbedPane tabbedPane = new JTabbedPane();
    tabbedPane.add( Messages.getString( "DocumentMetaDataDialog.General" ), generalPanel );
    tabbedPane.add( Messages.getString( "DocumentMetaDataDialog.Description" ), descriptionPanel );
    return tabbedPane;
  }

  public DocumentMetaData performEdit( final DocumentMetaData metaData,
                                       final ResourceManager resourceManager,
                                       ResourceKey bundleKey ) {
    final Date creationDate = (Date) metaData.getBundleAttribute
      ( ODFMetaAttributeNames.Meta.NAMESPACE, ODFMetaAttributeNames.Meta.CREATION_DATE );
    final String keyWords = (String) metaData.getBundleAttribute
      ( ODFMetaAttributeNames.Meta.NAMESPACE, ODFMetaAttributeNames.Meta.KEYWORDS );
    final String creator = (String) metaData.getBundleAttribute
      ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.CREATOR );
    final Date date = (Date) metaData.getBundleAttribute
      ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.DATE );
    final String description = (String) metaData.getBundleAttribute
      ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.DESCRIPTION );
    final String subject = (String) metaData.getBundleAttribute
      ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.SUBJECT );
    final String title = (String) metaData.getBundleAttribute
      ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.TITLE );

    if ( creationDate != null ) {
      creationDateLabel.setText( DateFormat.getDateTimeInstance().format( creationDate ) );
    } else {
      creationDateLabel.setText( null );
    }
    keywordsField.setText( keyWords );
    authorField.setText( creator );
    if ( date != null ) {
      modifiedDateLabel.setText( DateFormat.getDateTimeInstance().format( date ) );
    } else {
      modifiedDateLabel.setText( null );
    }
    descriptionField.setText( description );
    subjectField.setText( subject );
    titleField.setText( title );

    if ( bundleKey == null ) {
      locationLabel.setText( null );
      fileNameLabel.setText( null );
      sizeLabel.setText( null );
    } else {
      if ( bundleKey.getParent() != null ) {
        bundleKey = bundleKey.getParent();
      }

      final URL url = resourceManager.toURL( bundleKey );
      if ( url != null ) {
        final String file = url.getFile();
        final String fileName = IOUtils.getInstance().getFileName( file );
        final String location;
        final int idx = file.indexOf( fileName );
        if ( idx == -1 ) {
          location = null;
        } else {
          location = file.substring( 0, idx );
        }
        fileNameLabel.setText( fileName );
        locationLabel.setText( location );
      }

      sizeLabel.setText( null );
      try {
        final ResourceData resourceData = resourceManager.loadRawData( bundleKey );
        final Object size = resourceData.getAttribute( ResourceData.CONTENT_LENGTH );
        if ( size != null ) {
          sizeLabel.setText( String.valueOf( size ) );
        }
      } catch ( ResourceLoadingException e ) {
        // ignore ..
      }
    }

    if ( super.performEdit() == false ) {
      return null;
    }

    final MemoryDocumentMetaData o = new MemoryDocumentMetaData();
    if ( StringUtils.isEmpty( keywordsField.getText() ) == false ) {
      o.setBundleAttribute
        ( ODFMetaAttributeNames.Meta.NAMESPACE, ODFMetaAttributeNames.Meta.KEYWORDS, keywordsField.getText() );
    } else {
      o.setBundleAttribute
        ( ODFMetaAttributeNames.Meta.NAMESPACE, ODFMetaAttributeNames.Meta.KEYWORDS, null );
    }
    if ( StringUtils.isEmpty( authorField.getText() ) == false ) {
      o.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.CREATOR, authorField.getText() );
    } else {
      o.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.CREATOR, null );
    }
    if ( StringUtils.isEmpty( descriptionField.getText() ) == false ) {
      o.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.DESCRIPTION,
          descriptionField.getText() );
    } else {
      o.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.DESCRIPTION, null );
    }

    if ( StringUtils.isEmpty( subjectField.getText() ) == false ) {
      o.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.SUBJECT,
          subjectField.getText() );
    } else {
      o.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.SUBJECT, null );
    }

    if ( StringUtils.isEmpty( titleField.getText() ) == false ) {
      o.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.TITLE, titleField.getText() );
    } else {
      o.setBundleAttribute
        ( ODFMetaAttributeNames.DublinCore.NAMESPACE, ODFMetaAttributeNames.DublinCore.TITLE, null );
    }
    return o;
  }
}
