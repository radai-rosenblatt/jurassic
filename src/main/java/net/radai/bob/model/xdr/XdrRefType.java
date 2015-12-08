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

/**
 * @author Radai Rosenblatt
 */
public class XdrRefType extends XdrType {

    private final String refName;
    private final Scope scope;

    public XdrRefType(String refName, Scope scope) {
        this.refName = refName;
        this.scope = scope;
    }

    @Override
    public XdrTypes getType() {
        return XdrTypes.REF;
    }

    public String getRefName() {
        return refName;
    }

    public Scope getScope() {
        return scope;
    }

    public Identifiable resolve() {
        return scope.resolveRecursive(refName);
    }

    @Override
    public String toString() {
        return getType() + " (" + refName + ")";
    }
}
