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

package io.parsingdata.metal.token;

import static io.parsingdata.metal.Util.checkNotNull;

import java.io.IOException;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseRef;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.ValueExpression;

public class Sub extends Token {

    public final Token op;
    public final ValueExpression addr;

    public Sub(final Token op, final ValueExpression addr, final Encoding enc) {
        super(enc);
        this.op = checkNotNull(op, "op");
        this.addr = checkNotNull(addr, "addr");
    }

    @Override
    protected ParseResult parseImpl(final String scope, final Environment env, final Encoding enc) throws IOException {
        final OptionalValue ov = addr.eval(env, enc);
        if (!ov.isPresent()) { return new ParseResult(false, env); }
        final long ref = ov.get().asNumeric().longValue();
        if (env.order.hasGraphAtRef(ref)) {
            return new ParseResult(true, env.newEnv(env.order.add(new ParseRef(ref, this, env.sequenceId + 1)), env.input, env.offset));
        }
        final ParseResult res = op.parse(scope, env.newEnv(env.order.addBranch(this, env.sequenceId + 1), env.input, ref), enc);
        if (res.succeeded()) {
            return new ParseResult(true, env.newEnv(res.getEnvironment().order.closeBranch(), res.getEnvironment().input, env.offset));
        }
        return new ParseResult(false, env);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + op + ", " + addr + ")";
    }

}
