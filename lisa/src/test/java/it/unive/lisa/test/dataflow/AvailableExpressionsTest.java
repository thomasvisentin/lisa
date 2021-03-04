package it.unive.lisa.test.dataflow;

import it.unive.lisa.AnalysisException;
import it.unive.lisa.AnalysisSetupException;
import it.unive.lisa.LiSA;
import it.unive.lisa.LiSAFactory;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.dataflow.DefiniteForwardDataflowDomain;
import it.unive.lisa.analysis.dataflow.impl.AvailableExpressions;
import it.unive.lisa.program.Program;
import it.unive.lisa.test.imp.IMPFrontend;
import it.unive.lisa.test.imp.ParsingException;
import org.junit.Test;

public class AvailableExpressionsTest {
    @Test
    public void testAvailableExpressions() throws AnalysisException, ParsingException {
        LiSA lisa = new LiSA();
        lisa.setAbstractState(LiSAFactory.getDefaultFor(AbstractState.class,
                LiSAFactory.getDefaultFor(HeapDomain.class),
                new DefiniteForwardDataflowDomain<>(
                        new AvailableExpressions())));


        lisa.setDumpAnalysis(true);
        lisa.setWorkdir("test-outputs/available-expressions");
        Program impProgram = IMPFrontend.processFile("available-expressions.imp");
        lisa.setProgram(impProgram);
        lisa.run();

    }
}
