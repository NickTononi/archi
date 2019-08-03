/**
 * This program and the accompanying materials
 * are made available under the terms of the License
 * which accompanies this distribution in the file LICENSE.txt
 */
package com.archimatetool.editor.model.commands;

import org.eclipse.gef.commands.Command;

import com.archimatetool.model.IFeatures;
import com.archimatetool.model.IFeaturesEList;

/**
 * Command for setting an Archi Feature
 * 
 * @author Phillip Beauvoir
 */
public class FeatureCommand extends Command {
    
    private IFeaturesEList features;
    private String name, value, oldValue;

    public FeatureCommand(String label, IFeatures featuresObject, String name, Object value, Object defaultValue) {
        setLabel(label);
        features = featuresObject.getFeatures();
        this.name = name;
        this.value = value.toString();
        oldValue = features.getString(name, defaultValue.toString());
    }

    @Override
    public void execute() {
        features.putString(name, value);
    }

    @Override
    public void undo() {
        features.putString(name, oldValue);
    }
    
    @Override
    public boolean canExecute() {
        return value != null && !value.equals(oldValue);
    }

    @Override
    public void dispose() {
        features = null;
    }
}
