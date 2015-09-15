/*
 * This file is part of Bob.
 *
 * Bob is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Bob is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 *
 * You should have received a copy of the GNU Lesser Public License
 * along with Bob. If not, see <http://www.gnu.org/licenses/>.
 */

package net.radai.bob.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Radai Rosenblatt
 */
public class XdrUnionType extends XdrType {

    private final XdrDeclaration discriminant;
    private Map<Set<XdrValue>, XdrDeclaration> arms = new HashMap<>(); //the null key is the default arm

    @Override
    public XdrTypes getType() {
        return XdrTypes.UNION;
    }

    public XdrUnionType(XdrDeclaration discriminant) {
        this.discriminant = discriminant;
    }

    public XdrDeclaration getDiscriminant() {
        return discriminant;
    }

    public void addArm(Set<XdrValue> caseValues, XdrDeclaration declaration) {
        if (caseValues == null) {
            //a default arm
            if (arms.containsKey(null)) {
                throw new IllegalArgumentException("default case already defined");
            }
        } else {
            if (caseValues.isEmpty()) {
                throw new IllegalArgumentException("case must have values");
            }
            for (Set<XdrValue> existingCaseValues : arms.keySet()) {
                if (existingCaseValues == null) {
                    continue;
                }
                for (XdrValue existingCaseValue : existingCaseValues) {
                    if (caseValues.contains(existingCaseValue)) {
                        throw new IllegalArgumentException("union already has an arm defined for " + existingCaseValue);
                    }
                }
            }
        }
        arms.put(caseValues, declaration);
    }
}
