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
public class RpcProgramVersion implements Identifiable, Scope {
    private final Scope parentScope;
    private String identifier;
    private BigInteger versionNumber;
    private List<RpcProcedure> procedures = new ArrayList<>();

    public RpcProgramVersion(Scope parentScope) {
        this.parentScope = parentScope;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public BigInteger getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(BigInteger versionNumber) {
        this.versionNumber = versionNumber;
    }

    public void add(RpcProcedure procedure) {
        if (resolve(procedure.getName()) != null || resolve(procedure.getProcedureNumber()) != null) {
            throw new IllegalArgumentException("procedure name and number must be unique within version");
        }
        procedures.add(procedure);
    }

    @Override
    public RpcProcedure resolve(String procedureName) {
        for (RpcProcedure procedure : procedures) {
            if (procedure.getIdentifier().equals(procedureName)) {
                return procedure;
            }
        }
        return null;
    }

    public RpcProcedure resolve(BigInteger procedureNumber) {
        for (RpcProcedure procedure : procedures) {
            if (procedure.getProcedureNumber().equals(procedureNumber)) {
                return procedure;
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
        return identifier + " = " + versionNumber;
    }
}
