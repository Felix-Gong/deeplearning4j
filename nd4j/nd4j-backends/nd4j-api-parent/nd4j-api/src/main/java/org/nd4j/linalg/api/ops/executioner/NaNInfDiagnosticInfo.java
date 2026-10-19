/*
 *  ******************************************************************************
 *  *
 *  *
 *  * This program and the accompanying materials are made available under the
 *  * terms of the Apache License, Version 2.0 which is available at
 *  * https://www.apache.org/licenses/LICENSE-2.0.
 *  ******************************************************************************
 */

package org.nd4j.linalg.api.ops.executioner;

import org.nd4j.linalg.api.ops.CustomOp;
import org.nd4j.linalg.api.ops.Op;
import org.nd4j.linalg.api.ops.OpContext;

public class NaNInfDiagnosticInfo {

    public enum IssueType {
        NAN("NaN"),
        INF("Infinity");

        private final String label;

        IssueType(String label) {
            this.label = label;
        }

        public String label() {
            return label;
        }
    }

    private final Object op;
    private final OpContext opContext;
    private final IssueType issueType;
    private final int matchCount;

    private NaNInfDiagnosticInfo(Op op, OpContext opContext, IssueType issueType, int matchCount) {
        this.op = op;
        this.opContext = opContext;
        this.issueType = issueType;
        this.matchCount = matchCount;
    }

    private NaNInfDiagnosticInfo(CustomOp op, OpContext opContext, IssueType issueType, int matchCount) {
        this.op = op;
        this.opContext = opContext;
        this.issueType = issueType;
        this.matchCount = matchCount;
    }

    public static NaNInfDiagnosticInfo fromOp(Op op, OpContext oc, IssueType issueType, int matchCount) {
        return new NaNInfDiagnosticInfo(op, oc, issueType, matchCount);
    }

    public static NaNInfDiagnosticInfo fromCustomOp(CustomOp op, OpContext oc, IssueType issueType,
                                                   int matchCount, int outputIndex, boolean isInput) {
        return new NaNInfDiagnosticInfo(op, oc, issueType, matchCount);
    }

    public OpContext opContext() {
        return opContext;
    }

    public IssueType issueType() {
        return issueType;
    }

    public int matchCount() {
        return matchCount;
    }

    private String opName() {
        if (op instanceof Op) {
            return ((Op) op).opName();
        }
        if (op instanceof CustomOp) {
            String n = ((CustomOp) op).opName();
            return n != null ? n : "custom";
        }
        return "null";
    }

    public String toDetailedMessage() {
        return "Op [" + opName() + "] produced " + matchCount + " "
                + issueType.label() + " value(s) in its output";
    }
}
