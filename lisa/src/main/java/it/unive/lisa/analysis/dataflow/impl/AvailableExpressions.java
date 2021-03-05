package it.unive.lisa.analysis.dataflow.impl;

import it.unive.lisa.analysis.dataflow.DataflowElement;
import it.unive.lisa.analysis.dataflow.PossibleForwardDataflowDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class AvailableExpressions implements DataflowElement< // instances of this class represent a single element of the dataflow domain
        PossibleForwardDataflowDomain<AvailableExpressions>, // the dataflow domain to be used with this element is
        // a PossibleForwardDataflowDomain containing
        // instances of ReachingDefinitions
        AvailableExpressions // the concrete type of dataflow elements, that must be the same of this class
        > {
    
    private Identifier identifier;
    
    private Set<AvailableExpressions> availableExpressions;
    
    public AvailableExpressions() {
        
        this.availableExpressions = new HashSet<>();
        this.identifier = null;
    }
    
    @Override
    public Identifier getIdentifier() {
        
        return this.identifier;
    }
    
    @Override
    public String toString() {
        
        return "AvailableExpressions{" +
                "identifier=" + identifier +
                ", availableExpressions=" + availableExpressions +
                '}';
    }
    
    @Override
    public Collection<AvailableExpressions> gen(Identifier id, ValueExpression expression, ProgramPoint pp, PossibleForwardDataflowDomain<AvailableExpressions> domain) {
        
       
        return this.availableExpressions;
    }
    
    @Override
    public Collection<Identifier> kill(Identifier id, ValueExpression expression, ProgramPoint pp, PossibleForwardDataflowDomain<AvailableExpressions> domain) {
        
        var s = new HashSet<Identifier>();
        s.add(this.identifier);
        
        
        return s;
    }
    
    @Override
    public boolean equals(final Object o) {
        
        if (this == o) {
            return true;
        }
        if ((o == null) || (this.getClass() != o.getClass())) {
            return false;
        }
        
        final AvailableExpressions that = (AvailableExpressions) o;
        
        return Objects.equals(this.identifier, that.identifier) && Objects.equals(this.availableExpressions, that.availableExpressions);
    }
    
    @Override
    public int hashCode() {
        
        int result = (this.identifier != null) ? this.identifier.hashCode() : 0;
        result = (31 * result) + ((this.availableExpressions != null) ? this.availableExpressions.hashCode() : 0);
        return result;
    }
    
}
