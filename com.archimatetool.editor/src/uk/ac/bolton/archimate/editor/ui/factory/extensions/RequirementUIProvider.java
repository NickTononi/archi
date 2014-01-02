/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package uk.ac.bolton.archimate.editor.ui.factory.extensions;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.gef.EditPart;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;

import uk.ac.bolton.archimate.editor.diagram.editparts.extensions.RequirementEditPart;
import uk.ac.bolton.archimate.editor.ui.ColorFactory;
import uk.ac.bolton.archimate.editor.ui.IArchimateImages;
import uk.ac.bolton.archimate.editor.ui.factory.AbstractElementUIProvider;
import uk.ac.bolton.archimate.model.IArchimatePackage;


/**
 * Requirement UI Provider
 * 
 * @author Phillip Beauvoir
 */
public class RequirementUIProvider extends AbstractElementUIProvider {

    public EClass providerFor() {
        return IArchimatePackage.eINSTANCE.getRequirement();
    }
    
    @Override
    public EditPart createEditPart() {
        return new RequirementEditPart();
    }

    @Override
    public String getDefaultName() {
        return Messages.RequirementUIProvider_0;
    }

    @Override
    public Image getImage() {
        return getImageWithUserFillColor(IArchimateImages.ICON_REQUIREMENT_FILLED_16);
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return getImageDescriptorWithUserFillColor(IArchimateImages.ICON_REQUIREMENT_FILLED_16);
    }

    @Override
    public Color getDefaultColor() {
        return ColorFactory.get(204, 204, 255);
    }
}