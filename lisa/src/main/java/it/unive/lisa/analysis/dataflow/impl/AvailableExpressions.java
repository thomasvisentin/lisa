package it.unive.lisa.analysis.dataflow.impl;

import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.DefiniteForwardDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

public class AvailableExpressions
        implements DataflowElement<
        DefiniteForwardDataflowDomain<AvailableExpressions>,

        AvailableExpressions> {

    private final Identifier variable;
    private final ProgramPoint programPoint;


    public AvailableExpressions(){
        this(null, null);
    }

    public AvailableExpressions(Identifier variable, ProgramPoint pp) {
        this.variable = variable;
        this.programPoint = pp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AvailableExpressions)) return false;
        AvailableExpressions that = (AvailableExpressions) o;
        return Objects.equals(variable, that.variable) &&
                Objects.equals(programPoint, that.programPoint);
    }

    @Override
    public int hashCode() {
        return Objects.hash(variable, programPoint);
    }

    @Override
    public String toString() {
        return "AvailableExpressions{" +
                "variable=" + variable +
                ", programPoint=" + programPoint +
                '}';
    }


    @Override
    public Identifier getIdentifier() {
        return this.variable;
    }

    @Override
    public Collection<AvailableExpressions> gen(Identifier id, ValueExpression expression, ProgramPoint pp,
                                                DefiniteForwardDataflowDomain<AvailableExpressions> domain) {
        AvailableExpressions generated = new AvailableExpressions(id, pp);
        Collection<AvailableExpressions> result = new HashSet<>();
        result.add(generated);
        return result;
    }

    @Override
    public Collection<Identifier> kill(Identifier id, ValueExpression expression, ProgramPoint pp,
                                       DefiniteForwardDataflowDomain<AvailableExpressions> domain) {
        Collection<Identifier> result = new HashSet<>();
        result.add(id);
        return result;
    }
}
