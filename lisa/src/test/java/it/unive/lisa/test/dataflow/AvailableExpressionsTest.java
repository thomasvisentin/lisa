package it.unive.lisa.test.dataflow;

import it.unive.lisa.AnalysisException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.dataflow.DefiniteForwardDataflowDomain;
import it.unive.lisa.analysis.dataflow.PossibleForwardDataflowDomain;
import it.unive.lisa.analysis.dataflow.impl.AvailableExpressions;
import it.unive.lisa.program.Program;
import it.unive.lisa.test.imp.IMPFrontend;
import it.unive.lisa.test.imp.ParsingException;
import org.junit.Test;

public class AvailableExpressionsTest {

    @Test
    public void testAvailableExpressions() throws AnalysisException, ParsingException {
        // first step: create a lisa instance
        LiSA lisa = new LiSA();

        ValueDomain<?> valueDomain = new DefiniteForwardDataflowDomain<>(new AvailableExpressions());

        HeapDomain<?> heapDomain = LiSAFactory.getDefaultFor(HeapDomain.class);

        AbstractState<?, ?, ?> abstractState = LiSAFactory.getDefaultFor(AbstractState.class, heapDomain, valueDomain);

        // we can finally tell lisa which is the abstract state for the analysis
        lisa.setAbstractState(abstractState);

        lisa.setDumpAnalysis(true);

        lisa.setWorkdir("test-outputs/available-expressions");

        Program impProgram = IMPFrontend.processFile("reaching-definitions.imp");
        lisa.setProgram(impProgram);

        lisa.run();

    }
}
