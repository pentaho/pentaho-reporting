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

package org.pentaho.reporting.engine.classic.core.modules.gui.print;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.util.Locale;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.pentaho.reporting.engine.classic.core.PageDefinition;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.modules.gui.common.GuiContext;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.base.util.ResourceBundleSupport;
import org.pentaho.reporting.libraries.designtime.swing.CommonDialog;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;

public class PageSetupDialog extends CommonDialog {
  private class PageSizeSelectionAction implements ActionListener {
    private PageSizeSelectionAction() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      final String selectedItem = (String) pageFormatBox.getSelectedItem();
      if ( selectedItem == null ) {
        return;
      }
      final Paper paper = PageFormatFactory.getInstance().createPaper( selectedItem );
      if ( paper == null ) {
        return;
      }
      pageWidthField.setText( String.valueOf( paper.getWidth() ) );
      pageHeightField.setText( String.valueOf( paper.getHeight() ) );
    }
  }

  private class RevalidateListener implements DocumentListener {
    private RevalidateListener() {
    }

    /**
     * Gives notification that there was an insert into the document. The range given by the DocumentEvent bounds the
     * freshly inserted region.
     *
     * @param e
     *          the document event
     */
    public void insertUpdate( final DocumentEvent e ) {
      changedUpdate( e );
    }

    /**
     * Gives notification that a portion of the document has been removed. The range is given in terms of what the view
     * last saw (that is, before updating sticky positions).
     *
     * @param e
     *          the document event
     */
    public void removeUpdate( final DocumentEvent e ) {
      changedUpdate( e );
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e
     *          the document event
     */
    public void changedUpdate( final DocumentEvent e ) {
      validateInputs( false );
    }
  }

  private class PageSizeCheckBoxSelectionAction implements ChangeListener {
    private PageSizeCheckBoxSelectionAction() {
    }

    /**
     * Invoked when the target of the listener has changed its state.
     *
     * @param e
     *          a ChangeEvent object
     */
    public void stateChanged( final ChangeEvent e ) {
      if ( userDefinedPageSizeBox.isSelected() ) {
        pageFormatBox.setEnabled( false );
        pageWidthField.setEnabled( true );
        pageHeightField.setEnabled( true );
      } else {
        pageFormatBox.setEnabled( true );
        pageWidthField.setEnabled( false );
        pageHeightField.setEnabled( false );
      }
    }
  }

  private class OrientationChangeListener implements ActionListener {
    private OrientationChangeListener() {
    }

    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed( final ActionEvent e ) {
      validateInputs( false );
    }
  }

  private JComboBox pageFormatBox;
  private JTextField pageHeightField;
  private JTextField pageWidthField;
  private JCheckBox portraitModeBox;
  private JCheckBox landscapeModeBox;
  private JTextField spanHorizontalField;
  private JTextField spanVerticalField;
  private JTextField marginTopField;
  private JTextField marginLeftField;
  private JTextField marginBottomField;
  private JTextField marginRightField;
  private GuiContext guiContext;
  private JCheckBox preDefinedPageSizeBox;
  private JCheckBox userDefinedPageSizeBox;
  private PageFormatPreviewPane previewPane;
  private ResourceBundleSupport messages;
  private static final String MESSAGES =
      "org.pentaho.reporting.engine.classic.core.modules.gui.print.messages.messages";

  public PageSetupDialog( final GuiContext guiContext ) {
    init( guiContext );
  }

  public PageSetupDialog( final GuiContext guiContext, final Frame owner ) {
    super( owner );
    init( guiContext );
  }

  public PageSetupDialog( final GuiContext guiContext, final Dialog owner ) {
    super( owner );
    init( guiContext );
  }

  protected void init( final GuiContext guiContext ) {
    messages =
        new ResourceBundleSupport( Locale.getDefault(), MESSAGES, ObjectUtilities
            .getClassLoader( PageSetupDialog.class ) );

    this.guiContext = guiContext;
    final RevalidateListener revalidateListener = new RevalidateListener();

    previewPane = new PageFormatPreviewPane();

    pageFormatBox = new JComboBox( new DefaultComboBoxModel( PageFormatFactory.getInstance().getPageFormats() ) );
    pageHeightField = new JTextField();
    pageHeightField.setColumns( 5 );
    pageHeightField.getDocument().addDocumentListener( revalidateListener );
    pageWidthField = new JTextField();
    pageWidthField.setColumns( 5 );
    pageWidthField.getDocument().addDocumentListener( revalidateListener );

    landscapeModeBox = new JCheckBox();
    landscapeModeBox.addActionListener( new OrientationChangeListener() );
    portraitModeBox = new JCheckBox();
    portraitModeBox.addActionListener( new OrientationChangeListener() );

    preDefinedPageSizeBox = new JCheckBox();
    preDefinedPageSizeBox.addChangeListener( new PageSizeCheckBoxSelectionAction() );
    userDefinedPageSizeBox = new JCheckBox();
    userDefinedPageSizeBox.addChangeListener( new PageSizeCheckBoxSelectionAction() );

    final ButtonGroup pageSizeGroup = new ButtonGroup();
    pageSizeGroup.add( preDefinedPageSizeBox );
    pageSizeGroup.add( userDefinedPageSizeBox );

    spanHorizontalField = new JTextField();
    spanHorizontalField.setColumns( 5 );
    spanHorizontalField.getDocument().addDocumentListener( revalidateListener );
    spanVerticalField = new JTextField();
    spanVerticalField.setColumns( 5 );
    spanVerticalField.getDocument().addDocumentListener( revalidateListener );
    marginTopField = new JTextField();
    marginTopField.setColumns( 5 );
    marginTopField.getDocument().addDocumentListener( revalidateListener );
    marginLeftField = new JTextField();
    marginLeftField.setColumns( 5 );
    marginLeftField.getDocument().addDocumentListener( revalidateListener );
    marginBottomField = new JTextField();
    marginBottomField.setColumns( 5 );
    marginBottomField.getDocument().addDocumentListener( revalidateListener );
    marginRightField = new JTextField();
    marginRightField.setColumns( 5 );
    marginRightField.getDocument().addDocumentListener( revalidateListener );

    final ButtonGroup orientationGroup = new ButtonGroup();
    orientationGroup.add( portraitModeBox );
    orientationGroup.add( landscapeModeBox );

    pageFormatBox.addActionListener( new PageSizeSelectionAction() );

    setDefaultCloseOperation( DISPOSE_ON_CLOSE );

    super.init();
  }

  protected String getDialogId() {
    return "core.PageSetup";
  }

  protected boolean validateInputs( final boolean onConfirm ) {
    if ( validateFields() ) {
      previewPane.setPageDefinition( createPageDefinition() );
      return true;
    }
    return false;
  }

  private boolean validateFields() {
    try {
      if ( ParserUtil.parseFloat( pageHeightField.getText(), -1 ) <= 0 ) {
        return false;
      }
      if ( ParserUtil.parseFloat( pageWidthField.getText(), -1 ) <= 0 ) {
        return false;
      }
      if ( ParserUtil.parseInt( spanHorizontalField.getText(), -1 ) <= 0 ) {
        return false;
      }
      if ( ParserUtil.parseInt( spanVerticalField.getText(), -1 ) <= 0 ) {
        return false;
      }
      if ( ParserUtil.parseFloat( marginTopField.getText(), -1 ) < 0 ) {
        return false;
      }
      if ( ParserUtil.parseFloat( marginLeftField.getText(), -1 ) < 0 ) {
        return false;
      }
      if ( ParserUtil.parseFloat( marginBottomField.getText(), -1 ) < 0 ) {
        return false;
      }
      if ( ParserUtil.parseFloat( marginRightField.getText(), -1 ) < 0 ) {
        return false;
      }
    } catch ( Exception e ) {
      return false;
    }
    return true;
  }

  private JPanel createOrientationPanel() {
    final Icon portraitIcon = guiContext.getIconTheme().getLargeIcon( getLocale(), "pagesetup.portrait" );
    final JLabel portraitLabel =
        new JLabel( messages.getString( "PageSetupDialog.Portrait" ), portraitIcon, SwingConstants.LEFT );
    portraitLabel.setLabelFor( portraitModeBox );

    final Icon landscapeIcon = guiContext.getIconTheme().getLargeIcon( getLocale(), "pagesetup.landscape" );
    final JLabel landscapeLabel =
        new JLabel( messages.getString( "PageSetupDialog.Landscape" ), landscapeIcon, SwingConstants.LEFT );
    landscapeLabel.setLabelFor( landscapeModeBox );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BoxLayout( contentPane, BoxLayout.X_AXIS ) );
    contentPane.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder( messages
        .getString( "PageSetupDialog.Orientation" ) ), BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) ) );
    contentPane.add( portraitModeBox );
    contentPane.add( portraitLabel );
    contentPane.add( Box.createHorizontalGlue() );
    contentPane.add( landscapeModeBox );
    contentPane.add( landscapeLabel );
    contentPane.add( Box.createHorizontalGlue() );
    return contentPane;
  }

  private JPanel createPageSpanningPanel() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );
    contentPane.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder( messages
        .getString( "PageSetupDialog.PageSpanning" ) ), BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.insets = new Insets( 0, 0, 0, 10 );
    contentPane.add( new JLabel( messages.getString( "PageSetupDialog.SheetsAcross" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 0;
    contentPane.add( new JLabel( messages.getString( "PageSetupDialog.SheetsDown" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weightx = 1;
    gbc.ipadx = 50;
    gbc.insets = new Insets( 0, 0, 0, 10 );
    contentPane.add( spanHorizontalField, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.weightx = 1;
    gbc.ipadx = 50;
    contentPane.add( spanVerticalField, gbc );

    return contentPane;
  }

  private JPanel createPageSizePanel() {
    final JLabel preDefinedPageSizeLabel = new JLabel( messages.getString( "PageSetupDialog.Standard" ) );
    preDefinedPageSizeLabel.setLabelFor( preDefinedPageSizeBox );

    final JLabel userDefinedPageSizeLabel = new JLabel( messages.getString( "PageSetupDialog.Custom" ) );
    userDefinedPageSizeLabel.setLabelFor( userDefinedPageSizeBox );

    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );
    contentPane.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder( messages
        .getString( "PageSetupDialog.PageSize" ) ), BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 0;
    contentPane.add( preDefinedPageSizeBox, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 4;
    gbc.weightx = 4;
    contentPane.add( preDefinedPageSizeLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridwidth = 4;
    contentPane.add( pageFormatBox, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 2;
    contentPane.add( userDefinedPageSizeBox, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.gridwidth = 4;
    contentPane.add( userDefinedPageSizeLabel, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 3;
    contentPane.add( new JLabel( messages.getString( "PageSetupDialog.Width" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 2;
    gbc.gridy = 3;
    contentPane.add( pageWidthField, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 3;
    gbc.gridy = 3;
    gbc.insets = new Insets( 0, 5, 0, 0 );
    contentPane.add( new JLabel( messages.getString( "PageSetupDialog.Height" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 4;
    gbc.gridy = 3;
    contentPane.add( pageHeightField, gbc );

    return contentPane;
  }

  private JPanel createPreviewPanel() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new BorderLayout() );
    contentPane.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder( messages
        .getString( "PageSetupDialog.Preview" ) ), BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) ) );

    contentPane.add( previewPane, BorderLayout.CENTER );
    return contentPane;
  }

  private JPanel createMarginsPanel() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );
    contentPane.setBorder( BorderFactory.createCompoundBorder( BorderFactory.createTitledBorder( messages
        .getString( "PageSetupDialog.Margins" ) ), BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 0;
    contentPane.add( new JLabel( messages.getString( "PageSetupDialog.Top" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 1;
    contentPane.add( marginTopField, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 2;
    contentPane.add( new JLabel( messages.getString( "PageSetupDialog.Left" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 3;
    contentPane.add( marginLeftField, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 2;
    gbc.gridy = 2;
    contentPane.add( new JLabel( messages.getString( "PageSetupDialog.Right" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 2;
    gbc.gridy = 3;
    contentPane.add( marginRightField, gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 4;
    contentPane.add( new JLabel( messages.getString( "PageSetupDialog.Bottom" ) ), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 5;
    contentPane.add( marginBottomField, gbc );

    return contentPane;
  }

  protected JPanel createContentPane() {
    final JPanel contentPane = new JPanel();
    contentPane.setLayout( new GridBagLayout() );
    contentPane.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 0;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add( createOrientationPanel(), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add( createPageSizePanel(), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 0;
    gbc.gridy = 2;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add( createMarginsPanel(), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add( createPageSpanningPanel(), gbc );

    gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.gridheight = 2;
    gbc.weightx = 1;
    gbc.weighty = 1;
    gbc.fill = GridBagConstraints.BOTH;
    contentPane.add( createPreviewPanel(), gbc );

    return contentPane;
  }

  public PageDefinition performSetup( final PageDefinition original ) {
    if ( original instanceof SimplePageDefinition ) {
      final SimplePageDefinition simplePageDefinition = (SimplePageDefinition) original;
      spanHorizontalField.setText( String.valueOf( simplePageDefinition.getPageCountHorizontal() ) );
      spanVerticalField.setText( String.valueOf( simplePageDefinition.getPageCountVertical() ) );

      final PageFormat pageFormat = simplePageDefinition.getPageFormat();
      copyPageFormatToFields( pageFormat );
    } else if ( original != null ) {
      spanHorizontalField.setText( null );
      spanVerticalField.setText( null );

      if ( original.getPageCount() > 0 ) {
        final PageFormat pageFormat = original.getPageFormat( 0 );
        copyPageFormatToFields( pageFormat );
      } else {
        preDefinedPageSizeBox.setSelected( true );
        pageWidthField.setText( null );
        pageHeightField.setText( null );
        pageFormatBox.setSelectedItem( "A4" );

        portraitModeBox.setSelected( true );

        marginLeftField.setText( "18" );
        marginTopField.setText( "18" );
        marginRightField.setText( "18" );
        marginBottomField.setText( "18" );
      }
    } else {
      spanHorizontalField.setText( "1" );
      spanVerticalField.setText( "1" );

      preDefinedPageSizeBox.setSelected( true );
      pageWidthField.setText( null );
      pageHeightField.setText( null );
      pageFormatBox.setSelectedItem( "A4" );

      portraitModeBox.setSelected( true );

      marginLeftField.setText( "18" );
      marginTopField.setText( "18" );
      marginRightField.setText( "18" );
      marginBottomField.setText( "18" );
    }

    if ( performEdit() == false ) {
      return original;
    }

    return createPageDefinition();
  }

  private void copyPageFormatToFields( final PageFormat pageFormat ) {
    final Paper paper = pageFormat.getPaper();
    final PageFormatFactory pageFormatFactory = PageFormatFactory.getInstance();
    final String formatName = pageFormatFactory.getPageFormatName( paper.getWidth(), paper.getHeight() );
    pageFormatBox.setSelectedItem( formatName );
    pageWidthField.setText( String.valueOf( paper.getWidth() ) );
    pageHeightField.setText( String.valueOf( paper.getHeight() ) );
    userDefinedPageSizeBox.setSelected( formatName == null );
    preDefinedPageSizeBox.setSelected( formatName != null );

    final boolean portraitMode = pageFormat.getOrientation() == PageFormat.PORTRAIT;
    portraitModeBox.setSelected( portraitMode );
    landscapeModeBox.setSelected( portraitMode == false );

    if ( portraitMode ) {
      marginLeftField.setText( String.valueOf( pageFormatFactory.getLeftBorder( paper ) ) );
      marginTopField.setText( String.valueOf( pageFormatFactory.getTopBorder( paper ) ) );
      marginRightField.setText( String.valueOf( pageFormatFactory.getRightBorder( paper ) ) );
      marginBottomField.setText( String.valueOf( pageFormatFactory.getBottomBorder( paper ) ) );
    } else {
      marginTopField.setText( String.valueOf( pageFormatFactory.getLeftBorder( paper ) ) );
      marginLeftField.setText( String.valueOf( pageFormatFactory.getBottomBorder( paper ) ) );
      marginBottomField.setText( String.valueOf( pageFormatFactory.getRightBorder( paper ) ) );
      marginRightField.setText( String.valueOf( pageFormatFactory.getTopBorder( paper ) ) );
    }
  }

  private SimplePageDefinition createPageDefinition() {
    final float pageWidth = ParserUtil.parseFloat( pageWidthField.getText(), 0 );
    final float pageHeight = ParserUtil.parseFloat( pageHeightField.getText(), 0 );
    final int spanHorizontal = ParserUtil.parseInt( spanHorizontalField.getText(), 0 );
    final int spanVertical = ParserUtil.parseInt( spanVerticalField.getText(), 0 );
    final float marginTop = ParserUtil.parseFloat( marginTopField.getText(), 0 );
    final float marginLeft = ParserUtil.parseFloat( marginLeftField.getText(), 0 );
    final float marginBottom = ParserUtil.parseFloat( marginBottomField.getText(), 0 );
    final float marginRight = ParserUtil.parseFloat( marginRightField.getText(), 0 );

    final Paper p = PageFormatFactory.getInstance().createPaper( pageWidth, pageHeight );
    final PageFormat pf;
    if ( portraitModeBox.isSelected() ) {
      PageFormatFactory.getInstance().setBorders( p, marginTop, marginLeft, marginBottom, marginRight );
      pf = PageFormatFactory.getInstance().createPageFormat( p, PageFormat.PORTRAIT );
    } else {
      // noinspection SuspiciousNameCombination
      PageFormatFactory.getInstance().setBorders( p, marginRight, marginTop, marginLeft, marginBottom );
      pf = PageFormatFactory.getInstance().createPageFormat( p, PageFormat.LANDSCAPE );
    }

    return new SimplePageDefinition( pf, spanHorizontal, spanVertical );
  }
}
