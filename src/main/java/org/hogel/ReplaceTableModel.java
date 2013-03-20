package org.hogel;

import javax.swing.table.DefaultTableModel;

public class ReplaceTableModel extends DefaultTableModel {

    private static final long serialVersionUID = 1L;

    public ReplaceTableModel() {
        addColumn("置き換え前");
        addColumn("置き換え後");
    }

}
