package org.pentaho.reporting.designer.extensions.connectioneditor;

import javax.swing.*;
import javax.swing.event.ListDataListener;
import java.util.Vector;

public class EditorComboBoxModel extends AbstractListModel {
  private DefaultComboBoxModel model;
  private Vector<Object> data;

  public EditorComboBoxModel() {
    data = new Vector<Object>();
    model = new DefaultComboBoxModel( data );
  }

  public void setSelectedItem( final Object anObject ) {
    model.setSelectedItem( anObject );
  }

  public Object getSelectedItem() {
    return model.getSelectedItem();
  }

  public int getSize() {
    return model.getSize();
  }

  public Object getElementAt( final int index ) {
    return model.getElementAt( index );
  }

  public int getIndexOf( final Object anObject ) {
    return model.getIndexOf( anObject );
  }

  public void addElement( final Object anObject ) {
    model.addElement( anObject );
  }

  public void updateElementAt( final Object anObject, final int index ) {
    data.set( index, anObject );
    fireContentsChanged( this, index, index );
  }

  public void insertElementAt( final Object anObject, final int index ) {
    model.insertElementAt( anObject, index );
  }

  public void removeElementAt( final int index ) {
    model.removeElementAt( index );
  }

  public void removeElement( final Object anObject ) {
    model.removeElement( anObject );
  }

  public void removeAllElements() {
    model.removeAllElements();
  }

  public void addListDataListener( final ListDataListener l ) {
    model.addListDataListener( l );
    super.addListDataListener( l );
  }

  public void removeListDataListener( final ListDataListener l ) {
    model.removeListDataListener( l );
    super.removeListDataListener( l );
  }
}
