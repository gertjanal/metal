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

package io.parsingdata.metal.expression.logical;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.Expression;

/**
 * A {@link BinaryLogicalExpression} that implements the logical AND operator.
 */
public class And extends BinaryLogicalExpression {

    public And(final Expression left, final Expression right) {
        super(left, right);
    }

    @Override
    public boolean eval(final Environment environment, final Encoding encoding) {
        return left.eval(environment, encoding) && right.eval(environment, encoding);
    }

}
