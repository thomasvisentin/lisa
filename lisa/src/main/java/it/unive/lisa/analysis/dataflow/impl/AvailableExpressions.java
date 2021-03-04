package it.unive.lisa.analysis.dataflow.impl;

import java.util.Collection;
import java.util.HashSet;

import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.PossibleForwardDataflowDomain;
import it.unive.lisa.analysis.dataflow.DefiniteForwardDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;

// An element of the reaching definitions dataflow domain.
public class AvailableExpressions
        implements DataflowElement< // instances of this class represent a single element of the dataflow domain
        DefiniteForwardDataflowDomain<AvailableExpressions>, // the dataflow domain to be used with this element is
        // a DefiniteForwardDataflowDomain containing
        // instances of AvailableExpressions
        AvailableExpressions // the concrete type of dataflow elements, that must be the same of this class
        > {

    // the variable defined
    private final Identifier variable;
    // the program point where the variable has been defined
    private final ValueExpression expr;

    // this constructor will be used when creating the abstract state
    public AvailableExpressions() {
        this(null, null);
    }

    // this constructor is what we actually use to create dataflow elements
    private AvailableExpressions(Identifier variable, ValueExpression pp) {
        this.expr = pp;
        this.variable = variable;
    }

    // instances of this class will end up in collections, so it is a good practice to
    // implement equals and hashCode methods

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((expr == null) ? 0 : expr.hashCode());
        result = prime * result + ((variable == null) ? 0 : variable.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AvailableExpressions other = (AvailableExpressions) obj;
        if (expr == null) {
            if (other.expr != null)
                return false;
        } else if (!expr.equals(other.expr))
            return false;
        if (variable == null) {
            if (other.variable != null)
                return false;
        } else if (!variable.equals(other.variable))
            return false;
        return true;
    }

    // we want to pretty print information inside the .dot files, so we redefine the
    // toString method

    @Override
    public String toString() {
        return "(AE_exit: " + variable + ", " + expr + ")";
    }

    // the variable we are referring to
    @Override
    public Identifier getIdentifier() {
        return this.variable;
    }

    // the gen function: which elements are we generating when we are performing an assignment
    @Override
    public Collection<AvailableExpressions> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
                                                DefiniteForwardDataflowDomain<AvailableExpressions> domain) {
        AvailableExpressions generated = new AvailableExpressions(id, expression);
        Collection<AvailableExpressions> result = new HashSet<>();
        result.add(generated);
        return result;
    }

    // the kill function: which variables are we killing when we perform an assignment
    @Override
    public Collection<Identifier> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
                                       DefiniteForwardDataflowDomain<AvailableExpressions> domain) {
        Collection<Identifier> result = new HashSet<>();
        result.add(id);
        return result;
    }
}
