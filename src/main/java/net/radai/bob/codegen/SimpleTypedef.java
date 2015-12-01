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

package net.radai.bob.codegen;

/**
 * Created by Radai Rosenblatt
 */
public class SimpleTypedef extends Typedef {
    private final String name;
    private final Class<?> simpleType;
    private final boolean fixedSizeArray;
    private final int arraySizeLimit;

    public SimpleTypedef(String name, Class<?> simpleType) {
        this(name, simpleType, false, -1);
    }

    public SimpleTypedef(String name, Class<?> simpleType, boolean fixedSizeArray, int arraySizeLimit) {
        this.name = name;
        this.simpleType = simpleType;
        this.fixedSizeArray = fixedSizeArray;
        this.arraySizeLimit = arraySizeLimit;
    }

    public String getName() {
        return name;
    }

    public Class<?> getSimpleType() {
        return simpleType;
    }

    public boolean isFixedSizeArray() {
        return fixedSizeArray;
    }

    public int getArraySizeLimit() {
        return arraySizeLimit;
    }
}
