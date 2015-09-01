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
 * along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.radai.bob.parser;

import net.radai.bob.model.XdrConstant;

import java.util.HashMap;
import java.util.Map;

/**
 * not thread safe
 * @author Radai Rosenblatt
 */
public class ResultsContainer {
    private final Map<String, XdrConstant> constants = new HashMap<>();

    public void register(XdrConstant constant) {
        verifyUnused(constant.getName());
        constants.put(constant.getName(), constant);
    }

    public XdrConstant getConstant(String name) {
        return constants.get(name);
    }

    private void verifyUnused(String name) throws IllegalArgumentException {
        if (constants.containsKey(name)) {
            throw new IllegalArgumentException("name " + name + " already in use by " + constants.get(name));
        }
    }
}
