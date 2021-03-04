package it.unive.lisa.analysis.dataflow.impl;

import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteForwardDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;

import java.util.Collection;
import java.util.HashSet;

public class AvailableExpressions implements DataflowElement<DefiniteForwardDataflowDomain<AvailableExpressions>, AvailableExpressions> {
    private final Identifier variable;
    private final ProgramPoint programPoint;

    public AvailableExpressions() {
        this(null, null);
    }

    private AvailableExpressions(Identifier variable, ProgramPoint pp) {
        this.programPoint = pp;
        this.variable = variable;
    }

    @Override
    public int hashCode () {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((programPoint == null) ? 0 : programPoint.hashCode());
        result = prime * result + ((variable == null) ? 0 : variable.hashCode());
        return result;
    }

    @Override
    public boolean equals (Object obj){
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AvailableExpressions other = (AvailableExpressions) obj;
        if (programPoint == null) {
            if (other.programPoint != null)
                return false;
        } else if (!programPoint.equals(other.programPoint))
            return false;
        if (variable == null) {
            if (other.variable != null)
                return false;
        } else if (!variable.equals(other.variable))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + variable + ", " + programPoint + ")";
    }

    @Override
    public Identifier getIdentifier() {
        return this.variable;
    }

    // the gen function: which elements are we generating when we are performing an assignment
    @Override
    public Collection<AvailableExpressions> gen (Identifier id, ValueExpression expression, ProgramPoint pp,
        DefiniteForwardDataflowDomain < AvailableExpressions > domain){

        AvailableExpressions generated = new AvailableExpressions(id, pp);
        Collection<AvailableExpressions> result = new HashSet<>();
        result.add(generated);
        return result;
    }

    // the kill function: which variables are we killing when we perform an assignment
    @Override
    public Collection<Identifier> kill (Identifier id, ValueExpression expression, ProgramPoint pp,
        DefiniteForwardDataflowDomain < AvailableExpressions > domain){

        Collection<Identifier> result = new HashSet<>();
        result.add(id);
        return result;
    }

}

