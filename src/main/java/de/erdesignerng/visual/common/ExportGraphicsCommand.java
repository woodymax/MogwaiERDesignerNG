/**
 * Mogwai ERDesigner. Copyright (C) 2002 The Mogwai Project.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 */
package de.erdesignerng.visual.common;

import java.io.File;
import java.io.FileOutputStream;

import javax.swing.JFileChooser;

import org.jgraph.graph.CellView;
import org.jgraph.graph.DefaultGraphCell;

import de.erdesignerng.io.GenericFileFilter;
import de.erdesignerng.model.ModelItem;
import de.erdesignerng.visual.ExportType;
import de.erdesignerng.visual.cells.views.TableCellView;
import de.erdesignerng.visual.export.Exporter;

public class ExportGraphicsCommand extends UICommand {

    private Exporter exporter;

    private ExportType exportType;

    public ExportGraphicsCommand(ERDesignerComponent component, Exporter aExporter, ExportType aExportType) {
        super(component);
        exporter = aExporter;
        exportType = aExportType;
    }

    @Override
    public void execute() {
        if (exportType.equals(ExportType.ONE_PER_FILE)) {

            JFileChooser theChooser = new JFileChooser();
            theChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (theChooser.showSaveDialog(component.scrollPane) == JFileChooser.APPROVE_OPTION) {
                File theBaseDirectory = theChooser.getSelectedFile();

                CellView[] theViews = component.layoutCache.getAllViews();
                for (CellView theView : theViews) {
                    if (theView instanceof TableCellView) {
                        TableCellView theItemCellView = (TableCellView) theView;
                        DefaultGraphCell theItemCell = (DefaultGraphCell) theItemCellView.getCell();
                        ModelItem theItem = (ModelItem) theItemCell.getUserObject();

                        File theOutputFile = new File(theBaseDirectory, theItem.getName() + exporter.getFileExtension());
                        try {
                            exporter.exportToStream(theItemCellView.getRendererComponent(component.graph, false, false, false),
                                    new FileOutputStream(theOutputFile));
                        } catch (Exception e) {
                            component.worldConnector.notifyAboutException(e);
                        }
                    }
                }
            }

        } else {

            JFileChooser theChooser = new JFileChooser();
            GenericFileFilter theFilter = new GenericFileFilter(exporter.getFileExtension(), exporter
                    .getFileExtension()
                    + " File");
            theChooser.setFileFilter(theFilter);
            if (theChooser.showSaveDialog(component.scrollPane) == JFileChooser.APPROVE_OPTION) {

                File theFile = theFilter.getCompletedFile(theChooser.getSelectedFile());
                try {
                    exporter.fullExportToStream(component.graph, new FileOutputStream(theFile));
                } catch (Exception e) {
                    component.worldConnector.notifyAboutException(e);
                }
            }

        }
    }
}