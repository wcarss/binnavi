/*
Copyright 2015 Google Inc. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.google.security.zynamics.binnavi.Gui.CodeBookmarks;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;


import com.google.common.base.Preconditions;
import com.google.security.zynamics.binnavi.Gui.Actions.CActionProxy;
import com.google.security.zynamics.binnavi.Gui.CodeBookmarks.Actions.CDeleteBookmarkAction;
import com.google.security.zynamics.binnavi.models.Bookmarks.code.CCodeBookmarkManager;

/**
 * Table control that is used to display code bookmarks.
 */
public final class CCodeBookmarkTable extends JTable {
  /**
   * Used for serialization.
   */
  private static final long serialVersionUID = -3705762683090007816L;

  /**
   * Bookmark table model.
   */
  private final CCodeBookmarkTableModel m_model;

  /**
   * Bookmark manager that manages the displayed bookmarks.
   */
  private final CCodeBookmarkManager m_bookmarkManager;

  /**
   * Creates a new bookmark table.
   *
   * @param bookmarkManager Bookmark manager that manages the displayed bookmarks.
   */
  public CCodeBookmarkTable(final CCodeBookmarkManager bookmarkManager) {
    m_bookmarkManager =
        Preconditions.checkNotNull(bookmarkManager, "IE01310: Bookmark Manager can't be null");
    m_model = new CCodeBookmarkTableModel(bookmarkManager);

    setModel(m_model);

    getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    addMouseListener(new InternalMouseListener());
  }

  /**
   * Shows a context menu.
   *
   * @param event Mouse-event that triggered the context menu.
   */
  private void showPopup(final MouseEvent event) {
    int[] rows = getSelectedRows();

    // If at most one row is selected, select the row
    // where the click happened.
    if ((rows.length == 0) || (rows.length == 1)) {
      final int row = rowAtPoint(event.getPoint());
      final int column = columnAtPoint(event.getPoint());

      // Apparently no row was properly hit
      if ((row == -1) || (column == -1)) {
        return;
      }

      changeSelection(row, column, false, false);
      rows = getSelectedRows();
    }

    final JPopupMenu menu = new JPopupMenu();

    menu.add(new JMenuItem(CActionProxy.proxy(new CDeleteBookmarkAction(m_bookmarkManager, rows))));

    menu.show(event.getComponent(), event.getX(), event.getY());
  }

  /**
   * Frees allocated resources.
   */
  public void dispose() {
    m_model.dispose();
  }

  /**
   * Listener that shows a context menu when the user right-clicks on the table.
   */
  private class InternalMouseListener extends MouseAdapter {
    @Override
    public void mousePressed(final MouseEvent event) {
      if (event.isPopupTrigger()) {
        showPopup(event);
      }
    }

    @Override
    public void mouseReleased(final MouseEvent event) {
      if (event.isPopupTrigger()) {
        showPopup(event);
      }
    }
  }
}
