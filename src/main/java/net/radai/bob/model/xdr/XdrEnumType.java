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

import java.util.*;

/**
 * @author Radai Rosenblatt
 */
public class XdrEnumType extends XdrType {
    private LinkedHashMap<String, XdrValue> values = new LinkedHashMap<>(); //preserve order of declaration

    @Override
    public XdrTypes getType() {
        return XdrTypes.ENUM;
    }

    public LinkedHashMap<String, XdrValue> getValues() {
        return values;
    }

    public void add(String identifier, XdrValue value) {
        if (values.putIfAbsent(identifier, value) != null) {
            throw new IllegalArgumentException("enum already contained identifier " + identifier);
        }
    }

    public XdrValue get(String identifier) {
        if (!values.containsKey(identifier)) {
            throw new IllegalArgumentException("unknown identifier " + identifier);
        }
        return values.get(identifier);
    }
}
