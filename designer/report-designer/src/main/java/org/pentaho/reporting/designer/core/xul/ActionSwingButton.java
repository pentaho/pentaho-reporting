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
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.ui.xul.XulComponent;
import org.pentaho.ui.xul.XulDomContainer;
import org.pentaho.ui.xul.dom.Element;
import org.pentaho.ui.xul.swing.tags.SwingButton;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ActionSwingButton extends SwingButton implements DesignerContextComponent {
  private static final Log logger = LogFactory.getLog( ActionSwingMenuitem.class );

  private class ActionChangeHandler implements PropertyChangeListener {
    private static final String ENABLED = "enabled";
    private static final String SELECTED = "selected";

    /**
     * Receives notification of a property change event.
     *
     * @param event the property change event.
     */
    public void propertyChange( final PropertyChangeEvent event ) {
      try {
        final String propertyName = event.getPropertyName();
        final Action actionImpl = getActionImpl();
        if ( ENABLED.equals( propertyName ) ) {
          setDisabled( actionImpl.isEnabled() == false );
        } else if ( SELECTED.equals( propertyName ) ) {
          setSelected( Boolean.TRUE.equals( event.getNewValue() ) );
        } else if ( propertyName.equals( Action.SMALL_ICON ) ) {
          setIcon( (Icon) actionImpl.getValue( Action.SMALL_ICON ) );
        } else if ( propertyName.equals( Action.NAME ) ) {
          setLabel( (String) actionImpl.getValue( Action.NAME ) );
        } else if ( propertyName.equals( Action.SHORT_DESCRIPTION ) ) {
          ActionSwingButton.this.setTooltiptext( (String)
            actionImpl.getValue( Action.SHORT_DESCRIPTION ) );
        }

        if ( propertyName.equals( Action.ACCELERATOR_KEY ) ) {

          final KeyStroke oldVal = (KeyStroke) event.getOldValue();
          if ( oldVal != null ) {
            getButton().unregisterKeyboardAction( oldVal );
          }
          final Object o = actionImpl.getValue( Action.ACCELERATOR_KEY );
          if ( o instanceof KeyStroke ) {
            final KeyStroke k = (KeyStroke) o;
            getButton().registerKeyboardAction( actionImpl, k, JComponent.WHEN_IN_FOCUSED_WINDOW );
          }
        } else if ( propertyName.equals( Action.MNEMONIC_KEY ) ) {
          final Object o = actionImpl.getValue( Action.MNEMONIC_KEY );
          if ( o != null ) {
            if ( o instanceof Character ) {
              final Character c = (Character) o;
              getButton().setMnemonic( c.charValue() );
            } else if ( o instanceof Integer ) {
              final Integer c = (Integer) o;
              getButton().setMnemonic( c.intValue() );
            }
          } else {
            getButton().setMnemonic( -1 );
          }
        }
      } catch ( Exception e ) {
        ActionSwingButton.logger.warn( "Error on PropertyChange in ActionButton: ", e );//NON-NLS
      }
    }
  }

  private Action action;
  private String actionClass;
  private ActionChangeHandler actionChangeHandler;
  private ReportDesignerContext reportDesignerContext;
  private boolean parentIsToolbar;

  public ActionSwingButton( final Element self,
                            final XulComponent parent,
                            final XulDomContainer domContainer,
                            final String tagName ) {
    super( self, parent, domContainer, tagName );
    this.actionChangeHandler = new ActionChangeHandler();

    final AbstractButton button = getButton();
    parentIsToolbar = computeParentToolbar( parent );
    if ( parentIsToolbar ) {
      button.putClientProperty( "hideActionText", Boolean.TRUE );//NON-NLS
    }
  }

  private boolean computeParentToolbar( XulComponent parent ) {
    while ( parent != null ) {
      if ( parent.getManagedObject() instanceof JToolBar ) {
        return true;
      }
      parent = parent.getParent();
    }
    return false;
  }

  protected void setButton( final AbstractButton button ) {
    final AbstractButton oldButton = getButton();
    oldButton.setAction( null );
    super.setButton( button );
    if ( parentIsToolbar ) {
      button.putClientProperty( "hideActionText", Boolean.TRUE );//NON-NLS
    }
    button.setAction( action );
  }

  protected Action getActionImpl() {
    return action;
  }

  public String getAction() {
    return actionClass;
  }

  public void setAction( final String action ) {
    if ( this.action != null ) {
      uninstallAction( this.action );
    }
    this.actionClass = action;
    if ( this.actionClass != null ) {
      this.action = (Action) ObjectUtilities.loadAndInstantiate( actionClass, ActionSwingMenuitem.class, Action.class );
    }
    if ( this.action != null ) {
      installAction( this.action );
    }
  }

  protected void setIcon( final Icon icon ) {
    // here we by-pass the Xul-Framework, as the icon can be a computed one.
    getButton().setIcon( icon );
  }

  protected void uninstallAction( final Action oldAction ) {
    if ( oldAction != null ) {
      getButton().setAction( null );
      final Object o = oldAction.getValue( Action.ACCELERATOR_KEY );
      if ( o instanceof KeyStroke ) {
        final KeyStroke k = (KeyStroke) o;
        getButton().unregisterKeyboardAction( k );
      }
      oldAction.removePropertyChangeListener( actionChangeHandler );
    }
  }

  protected void installAction( final Action newAction ) {
    if ( newAction != null ) {
      setTooltiptext( (String) action.getValue( Action.SHORT_DESCRIPTION ) );
      setLabel( (String) action.getValue( Action.NAME ) );
      setDisabled( action.isEnabled() == false );
      setIcon( (Icon) action.getValue( Action.SMALL_ICON ) );

      getButton().setAction( newAction );
      newAction.addPropertyChangeListener( actionChangeHandler );

      final Object o = newAction.getValue( Action.ACCELERATOR_KEY );
      if ( o instanceof KeyStroke ) {
        final KeyStroke k = (KeyStroke) o;
        getButton().registerKeyboardAction( newAction, k, JComponent.WHEN_IN_FOCUSED_WINDOW );
      }

      if ( newAction instanceof ToggleStateAction ) {
        final ToggleStateAction tsa = (ToggleStateAction) action;
        setSelected( tsa.isSelected() );
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

  public void setLabel( final String label ) {
    if ( parentIsToolbar == false ) {
      super.setLabel( label );
    }
  }
}
