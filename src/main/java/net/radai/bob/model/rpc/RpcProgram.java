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

package net.radai.bob.model.rpc;

import net.radai.bob.model.Identifiable;
import net.radai.bob.model.Scope;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Radai Rosenblatt
 */
public class RpcProgram implements Identifiable, Scope {
    private final Scope parentScope;
    private String name;
    private BigInteger programNumber;
    private List<RpcProgramVersion> versions = new ArrayList<>();

    public RpcProgram(Scope parentScope) {
        this.parentScope = parentScope;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigInteger getProgramNumber() {
        return programNumber;
    }

    public void setProgramNumber(BigInteger programNumber) {
        this.programNumber = programNumber;
    }

    public void add(RpcProgramVersion version) {
        if (resolve(version.getIdentifier()) != null || resolve(version.getVersionNumber()) != null) {
            throw new IllegalArgumentException("version name and number must be unique within a program");
        }
        versions.add(version);
    }

    @Override
    public String getIdentifier() {
        return getName();
    }

    @Override
    public RpcProgramVersion resolve(String identifier) {
        for (RpcProgramVersion version : versions) {
            if (version.getIdentifier().equals(identifier)) {
                return version;
            }
        }
        return null;
    }

    public RpcProgramVersion resolve(BigInteger versionNumber) {
        for (RpcProgramVersion version : versions) {
            if (version.getVersionNumber().equals(versionNumber)) {
                return version;
            }
        }
        return null;
    }

    @Override
    public Scope getParent() {
        return parentScope;
    }

    @Override
    public String toString() {
        return name + " = " + programNumber;
    }
}
