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

import java.util.Objects;

/**
 * @author Radai Rosenblatt
 */
public class XdrRefValue extends XdrValue {
    private String refName;

    public XdrRefValue(String refName) {
        this.refName = refName;
    }

    public String getRefName() {
        return refName;
    }

    public void setRefName(String refName) {
        this.refName = refName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        XdrRefValue that = (XdrRefValue) o;
        return Objects.equals(refName, that.refName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(refName);
    }

    @Override
    public String toString() {
        return refName;
    }
}
