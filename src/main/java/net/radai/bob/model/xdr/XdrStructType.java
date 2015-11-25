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

package net.radai.bob.model.xdr;

import net.radai.bob.model.Identifiable;
import net.radai.bob.model.Scope;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Radai Rosenblatt
 */
public class XdrStructType extends XdrType implements Scope {
    private final Scope parentScope;
    private List<XdrDeclaration> fields = new ArrayList<>(); //field order is important

    public XdrStructType(Scope parentScope) {
        this.parentScope = parentScope;
    }

    @Override
    public XdrTypes getType() {
        return XdrTypes.STRUCT;
    }

    public void addField(XdrDeclaration field) {
        fields.add(field);
    }

    @Override
    public Identifiable resolve(String identifier) {
        for (XdrDeclaration field : fields) {
            if (field.getIdentifier().equals(identifier)) {
                return field;
            }
        }
        return null;
    }

    @Override
    public Scope getParent() {
        return parentScope;
    }
}
