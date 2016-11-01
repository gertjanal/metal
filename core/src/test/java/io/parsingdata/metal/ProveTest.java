package io.parsingdata.metal;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.util.EncodingFactory.le;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.selection.ByName;
import io.parsingdata.metal.expression.Expression;
import io.parsingdata.metal.expression.comparison.ComparisonExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.token.Token;

public class ProveTest {

    @Test
    public void test() throws Exception {
        final Environment environment = stream(0x09, 0x00); // binary: 0000 0000 0000 1001
        final Token token = def("value", 2, state(1));

        final ParseResult result = token.parse(environment, le());
        assertTrue(result.succeeded);

        final ParseGraph graph = result.environment.order;

        final int value = ByName.getValue(graph, "value").asNumeric().intValue() >> 2;
        assertEquals(2, value);
    }

    private static Expression state(final int state) {
        return new ComparisonExpression(null, con(state)) {
            @Override
            public boolean compare(final Value left, final Value right) {
                return right.asNumeric().longValue() == (left.asNumeric().longValue() & 0x03); // First 2 bits
            }
        };
    }
}
