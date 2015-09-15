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

package net.radai.bob.parser;

import net.radai.bob.model.XdrConstant;
import net.radai.bob.model.XdrDeclaration;
import net.radai.bob.model.XdrIdentifiable;
import net.radai.bob.model.XdrScope;

import java.util.HashMap;
import java.util.Map;

/**
 * not thread safe
 * @author Radai Rosenblatt
 */
public class ResultsContainer implements XdrScope {
    private final Map<String, XdrConstant> constants = new HashMap<>();
    private final Map<String, XdrDeclaration> types = new HashMap<>();

    public void register(XdrConstant constant) {
        verifyUnused(constant.getIdentifier());
        constants.put(constant.getIdentifier(), constant);
    }

    public void register(XdrDeclaration type) {
        verifyUnused(type.getIdentifier());
        types.put(type.getIdentifier(), type);
    }

    @Override
    public XdrIdentifiable resolve(String identifier) {
        XdrConstant constant = getConstant(identifier);
        if (constant != null) {
            return constant;
        }
        return getType(identifier);
    }

    @Override
    public XdrScope getParent() {
        return null; //top level
    }

    public XdrConstant getConstant(String name) {
        return constants.get(name);
    }

    public XdrDeclaration getType(String name) {
        return types.get(name);
    }

    private void verifyUnused(String name) throws IllegalArgumentException {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("must provide a valid name");
        }
        if (resolve(name) != null) {
            throw new IllegalArgumentException("name " + name + " already defined within scope");
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (!constants.isEmpty()) {
            sb.append(constants.size()).append(" consts");
        }
        if (sb.length() == 0) {
            return "empty";
        }
        return sb.toString();
    }
}
