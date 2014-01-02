/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.example;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;

import uk.ac.bolton.archimate.editor.model.DiagramModelUtils;
import uk.ac.bolton.archimate.editor.model.IEditorModelManager;
import uk.ac.bolton.archimate.editor.model.IModelImporter;
import uk.ac.bolton.archimate.model.IArchimateElement;
import uk.ac.bolton.archimate.model.IArchimateFactory;
import uk.ac.bolton.archimate.model.IArchimateModel;
import uk.ac.bolton.archimate.model.IArchimatePackage;
import uk.ac.bolton.archimate.model.IDiagramModel;
import uk.ac.bolton.archimate.model.IDiagramModelArchimateConnection;
import uk.ac.bolton.archimate.model.IDiagramModelArchimateObject;
import uk.ac.bolton.archimate.model.IDiagramModelComponent;
import uk.ac.bolton.archimate.model.IDiagramModelObject;
import uk.ac.bolton.archimate.model.IFolder;
import uk.ac.bolton.archimate.model.IRelationship;


/**
 * Example Model Importer
 * Shows basic import routines.
 * You will need to read in the file somehow and model it in JDOM or DOM Sax parser or some representation and map
 * this to Archi elements.
 * 
 * @author Phillip Beauvoir
 */
@SuppressWarnings("nls")
public class MyImporter implements IModelImporter {

    String MY_EXTENSION_WILDCARD = "*.mex"; //$NON-NLS-1$
    
    // ID -> Object lookup table
    Map<String, EObject> idLookup;

    @Override
    public void doImport() throws IOException {
        File file = askOpenFile();
        if(file == null) {
            return;
        }
        
        // Load in the file and get its information here.
        // Assuming you load in the data in some way, perhaps with JDOM, or a SAX Parser ot text reader then you will
        // have a representation of it in memory that you need to map to Archi elements.
        
        
        // Here is some example raw data in String format. This is a very simple example so the data
        // is not in the best format. There is no error checking either.
        
        // Elements
        String[] elements = {
                // Type, Name, ID
                "BusinessActor", "Actor", "elementID1",
                "BusinessRole", "Client", "elementID2",
                "BusinessFunction", "My Function", "elementID3"
        };
        
        // Relationships
        String[] relations = {
                // Type, Name, ID, sourceID, targetID
                "AssignmentRelationship", "Assigned to", "relID1", "elementID1", "elementID2",
                "UsedByRelationship", "", "relID2", "elementID1", "elementID3",
                "AssociationRelationship", "", "relID3", "elementID2", "elementID3"
        };
        
        // Views
        String[] views = {
                // Name, ID
                "A View", "view1",
                "Another View", "view2"
        };
        
        // View elements
        String[] viewElements = {
                // ID of parent View, ID of referenced element, x, y, width, height
                "view1", "elementID1", "10", "10", "-1", "-1",
                "view1", "elementID2", "310", "10", "-1", "-1",
                "view1", "elementID3", "310", "110", "-1", "-1",
                "view2", "elementID2", "10", "10", "-1", "-1",
                "view2", "elementID3", "10", "110", "-1", "-1"
        };
        
        // View connections
        String[] viewConnections = {
                // ID of parent View, ID of relationship
                "view1", "relID1",
                "view1", "relID2",
                "view2", "relID3",
        };

        // Create the model...
        
        // Create a new Archimate Model and set its defaults
        IArchimateModel model = IArchimateFactory.eINSTANCE.createArchimateModel();
        model.setDefaults();
        model.setName("My Model");
        
        // Create and add elements matching imported data
        // If an ID is not provided for an element then a unique ID will be generated when the model element is added to a parent
        // model element, otherwise you can use your own IDs provided in the input data.
        // Let's use an ID -> EObject mapping table for convenience
        idLookup = new HashMap<String, EObject>();
        
        // Create and add model elements
        for(int i = 0; i < elements.length;) {
            String type = elements[i++];
            String name = elements[i++];
            String id = elements[i++];
            createAndAddArchimateElement(model, (EClass)IArchimatePackage.eINSTANCE.getEClassifier(type), name, id);
        }
        
        // Create and add model relationships and set source and target elements
        for(int i = 0; i < relations.length;) {
            String type = relations[i++];
            String name = relations[i++];
            String id = relations[i++];
            String sourceID = relations[i++];
            String targetID = relations[i++];
            IRelationship relationship = createAndAddArchimateRelationship(model, (EClass)IArchimatePackage.eINSTANCE.getEClassifier(type), name, id);
            
            // Find source and target elements from their IDs in the lookup table
            IArchimateElement source = (IArchimateElement)idLookup.get(sourceID);
            IArchimateElement target = (IArchimateElement)idLookup.get(targetID);
            relationship.setSource(source);
            relationship.setTarget(target);
        }
        
        // Create and add diagram views
        for(int i = 0; i < views.length;) {
            String name = views[i++];
            String id = views[i++];
            createAndAddView(model, name, id);
        }

        // Add diagram elements to views
        for(int i = 0; i < viewElements.length;) {
            String viewID = viewElements[i++];
            String refID = viewElements[i++];
            int x = Integer.parseInt(viewElements[i++]);
            int y = Integer.parseInt(viewElements[i++]);
            int width = Integer.parseInt(viewElements[i++]);
            int height = Integer.parseInt(viewElements[i++]);
            
            IDiagramModel diagramModel = (IDiagramModel)idLookup.get(viewID);
            IArchimateElement element = (IArchimateElement)idLookup.get(refID);
            createAndAddElementToView(diagramModel, element, x, y, width, height);
        }
        
        // Add diagram connections to views
        for(int i = 0; i < viewConnections.length;) {
            String viewID = viewConnections[i++];
            String relationshipID = viewConnections[i++];
            
            IDiagramModel diagramModel = (IDiagramModel)idLookup.get(viewID);
            IRelationship relationship = (IRelationship)idLookup.get(relationshipID);
            createAndAddConnectionsToView(diagramModel, relationship);
        }
        
        // And open the Model in the Editor
        IEditorModelManager.INSTANCE.openModel(model);
    }
    
    protected void createAndAddConnectionsToView(IDiagramModel diagramModel, IRelationship relationship) {
        List<IDiagramModelComponent> sources = DiagramModelUtils.findDiagramModelComponentsForElement(diagramModel, relationship.getSource());
        List<IDiagramModelComponent> targets = DiagramModelUtils.findDiagramModelComponentsForElement(diagramModel, relationship.getTarget());

        for(IDiagramModelComponent dmcSource : sources) {
            for(IDiagramModelComponent dmcTarget : targets) {
                IDiagramModelArchimateConnection dmc = IArchimateFactory.eINSTANCE.createDiagramModelArchimateConnection();
                dmc.setRelationship(relationship);
                dmc.connect((IDiagramModelObject)dmcSource, (IDiagramModelObject)dmcTarget);
                idLookup.put(dmc.getId(), dmc);
            }
        }
    }

    protected IDiagramModelArchimateObject createAndAddElementToView(IDiagramModel diagramModel, IArchimateElement element, int x, int y, int width, int height) {
        IDiagramModelArchimateObject dmo = IArchimateFactory.eINSTANCE.createDiagramModelArchimateObject();
        dmo.setArchimateElement(element);
        dmo.setBounds(x, y, width, height);
        diagramModel.getChildren().add(dmo);
        idLookup.put(dmo.getId(), dmo);
        return dmo;
    }

    protected IDiagramModel createAndAddView(IArchimateModel model, String name, String id) {
        IDiagramModel diagramModel = IArchimateFactory.eINSTANCE.createArchimateDiagramModel();
        diagramModel.setName(name);
        diagramModel.setId(id);
        IFolder folder = model.getDefaultFolderForElement(diagramModel);
        folder.getElements().add(diagramModel);
        idLookup.put(diagramModel.getId(), diagramModel);
        return diagramModel;
    }
    
    protected IRelationship createAndAddArchimateRelationship(IArchimateModel model, EClass type, String name, String id) {
        if(!IArchimatePackage.eINSTANCE.getRelationship().isSuperTypeOf(type)) {
            throw new IllegalArgumentException("Eclass type should be of relationship type");
        }
        
        return (IRelationship)createAndAddArchimateElement(model, type, name, id);
    }
    
    protected IArchimateElement createAndAddArchimateElement(IArchimateModel model, EClass type, String name, String id) {
        if(!IArchimatePackage.eINSTANCE.getArchimateElement().isSuperTypeOf(type)) {
            throw new IllegalArgumentException("Eclass type should be of archimate element type");
        }

        IArchimateElement element = (IArchimateElement)IArchimateFactory.eINSTANCE.create(type);
        element.setName(name);
        element.setId(id);
        IFolder folder = model.getDefaultFolderForElement(element);
        folder.getElements().add(element);
        idLookup.put(element.getId(), element);
        return element;
    }

    protected File askOpenFile() {
        FileDialog dialog = new FileDialog(Display.getCurrent().getActiveShell(), SWT.OPEN);
        dialog.setFilterExtensions(new String[] { MY_EXTENSION_WILDCARD, "*.*" } ); //$NON-NLS-1$
        String path = dialog.open();
        return path != null ? new File(path) : null;
    }
}