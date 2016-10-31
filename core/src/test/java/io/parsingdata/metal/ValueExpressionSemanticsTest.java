/*
 * Copyright 2013-2016 Netherlands Forensic Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.parsingdata.metal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.cat;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.stream;
import static io.parsingdata.metal.util.TokenDefinitions.any;

import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.UnaryValueExpression;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueOperation;
import io.parsingdata.metal.token.Token;

@RunWith(JUnit4.class)
public class ValueExpressionSemanticsTest {

    private final Token cat = seq(any("a"),
                                  any("b"),
                                  def("c", con(2), eq(cat(ref("a"), ref("b")))));

    @Test
    public void Cat() throws IOException {
        assertTrue(cat.parse(stream(1, 2, 1, 2), enc()).succeeded);
    }

    @Test
    public void CatNoMatch() throws IOException {
        assertFalse(cat.parse(stream(1, 2, 12, 12), enc()).succeeded);
    }

    @Test
    public void callback() throws IOException {
        final Environment data = stream(1, 2, 3, 4);
        def("a", 4, eq(new UnaryValueExpression(ref("a")) {
            @Override
            public OptionalValue eval(Value value, Environment environment, Encoding encoding) {
                return value.operation(new ValueOperation() {
                    @Override
                    public OptionalValue execute(Value value) {
                        return OptionalValue.of(value);
                    }
                });
            }
        })).parse(data, enc());
    }

}
