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

package org.pentaho.reporting.designer.core.xul;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.designer.core.DesignerContextComponent;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.DesignerContextAction;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.components.XulMenuitem;
import org.pentaho.ui.xul.dom.Element;
import org.pentaho.ui.xul.impl.AbstractXulComponent;
import org.pentaho.ui.xul.swing.SwingElement;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ActionSwingMenuitem extends SwingElement implements DesignerContextComponent, XulMenuitem {
  private class InlineActionForward implements ActionListener {
    protected InlineActionForward() {
    }

    public void actionPerformed( final ActionEvent evt ) {
      final String command = getCommand();
      if ( command != null ) {
        invoke( command );
      }
    }
  }

  private static final Log logger = LogFactory.getLog( ActionSwingMenuitem.class );

  protected class ActionChangeHandler implements PropertyChangeListener {
    protected ActionChangeHandler() {
    }

    /**
     * Receives notification of a property change event.
     *
     * @param event the property change event.
     */
    public void propertyChange( final PropertyChangeEvent event ) {
      try {
        final String propertyName = event.getPropertyName();
        final Action actionImpl = getActionImpl();
        if ( "selected".equals( propertyName ) ||//NON-NLS
          Action.SELECTED_KEY.equals( propertyName ) ) {
          setSelected( Boolean.TRUE.equals( event.getNewValue() ) );
        } else if ( "enabled".equals( propertyName ) )//NON-NLS
        {
          setDisabled( actionImpl.isEnabled() == false );
        } else if ( "visible".equals( propertyName ) ) //NON-NLS
        {
          setVisible( Boolean.TRUE.equals( event.getNewValue() ) );
        } else if ( propertyName.equals( Action.NAME ) ) {
          setLabel( (String) actionImpl.getValue( Action.NAME ) );
        } else if ( propertyName.equals( Action.SHORT_DESCRIPTION ) ) {
          ActionSwingMenuitem.this.setTooltiptext( (String)
            actionImpl.getValue( Action.SHORT_DESCRIPTION ) );
        }

        if ( propertyName.equals( Action.ACCELERATOR_KEY ) ) {
          refreshKeystroke( actionImpl );
        } else if ( propertyName.equals( Action.MNEMONIC_KEY ) ) {
          refreshMnemonic( actionImpl );
        }
      } catch ( Exception e ) {
        ActionSwingMenuitem.logger.warn( "Error on PropertyChange in ActionSwingMenuItem: ", e );//NON-NLS
      }
    }
  }

  private String image;
  private String onCommand;
  private JMenuItem menuitem;
  private Action action;
  private String actionClass;
  private ActionChangeHandler actionChangeHandler;
  private ReportDesignerContext reportDesignerContext;

  public static final String RADIO_MENUITEM = "radio-menuitem";
  public static final String CHECKBOX_MENUITEM = "checkbox-menuitem";
  public static final String MENUITEM = "menuitem";

  public ActionSwingMenuitem( final Element self,
                              final XulComponent parent,
                              final XulDomContainer domContainer,
                              final String tagName ) {
    this( tagName );
  }

  public ActionSwingMenuitem( final String tagName ) {
    super( tagName );
    this.actionChangeHandler = new ActionChangeHandler();
    this.menuitem = createComponent( tagName );
    this.menuitem.addActionListener( new InlineActionForward() );
    setManagedObject( menuitem );
  }

  protected ActionChangeHandler getActionChangeHandler() {
    return actionChangeHandler;
  }

  protected void setActionChangeHandler( final ActionChangeHandler actionChangeHandler ) {
    this.actionChangeHandler = actionChangeHandler;
  }

  protected JMenuItem createComponent( final String tagName ) {
    if ( RADIO_MENUITEM.equalsIgnoreCase( tagName ) ) {
      return new JRadioButtonMenuItem();
    } else if ( CHECKBOX_MENUITEM.equalsIgnoreCase( tagName ) ) {
      return new JCheckBoxMenuItem();
    } else {
      return new JMenuItem();
    }
  }

  public String getAcceltext() {
    return String.valueOf( menuitem.getAccelerator().getKeyChar() );
  }

  public String getAccesskey() {
    if ( menuitem.getDisplayedMnemonicIndex() == -1 ) {
      return null;
    }
    return String.valueOf( menuitem.getText().charAt( menuitem.getDisplayedMnemonicIndex() ) );
  }

  public Action getActionImpl() {
    return action;
  }

  public String getAction() {
    return actionClass;
  }

  public void setAction( final Action action ) {
    if ( this.action != null ) {
      this.action.removePropertyChangeListener( actionChangeHandler );
      uninstallAction( this.action );
    }
    if ( action != null ) {
      this.actionClass = action.getClass().getName();
      this.action = action;
    } else {
      this.actionClass = null;
      this.action = null;
    }
    if ( this.action != null ) {
      this.action.addPropertyChangeListener( actionChangeHandler );
      installAction( this.action );
    }
  }

  public void setAction( final String action ) {
    if ( this.action != null ) {
      this.action.removePropertyChangeListener( actionChangeHandler );
      uninstallAction( this.action );
    }
    this.actionClass = action;
    if ( this.actionClass != null ) {
      this.action = ObjectUtilities.loadAndInstantiate( actionClass, ActionSwingMenuitem.class, Action.class );
    }
    if ( this.action != null ) {
      this.action.addPropertyChangeListener( actionChangeHandler );
      installAction( this.action );
    }
  }

  protected void uninstallAction( final Action oldAction ) {
    if ( oldAction != null ) {
      menuitem.removeActionListener( oldAction );
      oldAction.removePropertyChangeListener( actionChangeHandler );

      final Object o = oldAction.getValue( Action.ACCELERATOR_KEY );
      if ( o instanceof KeyStroke ) {
        final KeyStroke k = (KeyStroke) o;
        menuitem.unregisterKeyboardAction( k );
      }
    }
  }

  protected void installAction( final Action newAction ) {
    if ( newAction != null ) {
      menuitem.addActionListener( newAction );
      newAction.addPropertyChangeListener( actionChangeHandler );

      setLabel( (String) ( newAction.getValue( Action.NAME ) ) );
      setTooltiptext( (String) ( newAction.getValue( Action.SHORT_DESCRIPTION ) ) );
      setDisabled( this.action.isEnabled() == false );

      refreshMnemonic( newAction );
      refreshKeystroke( newAction );


      final Object rawSelectedSwing = action.getValue( Action.SELECTED_KEY );
      if ( rawSelectedSwing != null ) {
        setSelected( Boolean.TRUE.equals( rawSelectedSwing ) );
      } else {
        final Object rawSelectedPrd = action.getValue( "selected" );
        setSelected( Boolean.TRUE.equals( rawSelectedPrd ) );
      }

      final Object rawVisible = action.getValue( "visible" );
      if ( rawVisible != null ) {
        setVisible( Boolean.TRUE.equals( rawVisible ) );
      }
    }
  }

  public void setReportDesignerContext( final ReportDesignerContext context ) {
    this.reportDesignerContext = context;
    if ( action instanceof DesignerContextAction ) {
      final DesignerContextAction dca = (DesignerContextAction) action;
      dca.setReportDesignerContext( reportDesignerContext );
    }
  }

  public ReportDesignerContext getReportDesignerContext() {
    return reportDesignerContext;
  }

  public boolean isDisabled() {
    return !menuitem.isEnabled();
  }

  public String getLabel() {
    return menuitem.getText();
  }

  public void setAcceltext( final String accel ) {
    menuitem.setAccelerator( KeyStroke.getKeyStroke( accel ) );
  }

  public void setAccesskey( final String accessKey ) {
    if ( accessKey == null || accessKey.length() == 0 ) {
      menuitem.setMnemonic( 0 );
    } else {
      menuitem.setMnemonic( accessKey.charAt( 0 ) );
    }
  }

  public void setDisabled( final boolean disabled ) {
    menuitem.setEnabled( !disabled );
  }

  public void setDisabled( final String disabled ) {
    menuitem.setEnabled( !Boolean.parseBoolean( disabled ) );
  }

  public void setVisible( final boolean visible ) {
    super.setVisible( visible );
    menuitem.setVisible( visible );

    final XulComponent parent = getParent();
    if ( parent instanceof AbstractXulComponent ) {
      final AbstractXulComponent parentComp = (AbstractXulComponent) parent;
      parentComp.layout();
    }
  }

  public void setLabel( final String label ) {
    menuitem.setText( label );
  }

  public String getImage() {
    return image;
  }

  public boolean isSelected() {
    return menuitem.isSelected();
  }

  public void setSelected( final boolean selected ) {
    menuitem.setSelected( selected );
  }

  public void setImage( final String image ) {
    this.image = image;
  }

  public String getCommand() {
    return this.onCommand;
  }

  public void setCommand( final String command ) {
    this.onCommand = command;
  }

  public String toString() {
    return getLabel();
  }

  private void refreshKeystroke( final Action actionImpl ) {
    final Object keyStroke = actionImpl.getValue( Action.ACCELERATOR_KEY );
    if ( keyStroke instanceof KeyStroke == false ) {
      setAcceltext( null );
    } else {
      setAcceltext( keyStroke.toString() );
    }
  }

  private void refreshMnemonic( final Action actionImpl ) {
    final Object o = actionImpl.getValue( Action.MNEMONIC_KEY );
    if ( o != null ) {
      if ( o instanceof Character ) {
        final Character c = (Character) o;
        setAccesskey( String.valueOf( c.charValue() ) );
      } else if ( o instanceof Integer ) {
        final Integer c = (Integer) o;
        setAccesskey( String.valueOf( c.intValue() ) );
      }
    } else {
      setAccesskey( "\0" );
    }
  }

}
