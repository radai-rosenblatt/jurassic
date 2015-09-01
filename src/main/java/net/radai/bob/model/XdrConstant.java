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

package net.radai.bob.model;

import java.math.BigInteger;

/**
 * @author Radai Rosenblatt
 */
public class XdrConstant {
    private final String name;
    private final BigInteger value; //might be unsigned hyper (long), which java cant handle

    public XdrConstant(String name, BigInteger value) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("xdr constant name cannot be empty");
        }
        if (value == null) {
            throw new IllegalArgumentException("xdr constant must have a value");
        }
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public BigInteger getValue() {
        return value;
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }
}
