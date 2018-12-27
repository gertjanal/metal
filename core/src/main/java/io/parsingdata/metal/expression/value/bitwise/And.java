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

package io.parsingdata.metal.expression.value.bitwise;

import java.util.BitSet;
import java.util.Optional;

import io.parsingdata.metal.data.ParseState;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.expression.value.BinaryValueExpression;
import io.parsingdata.metal.expression.value.ConstantFactory;
import io.parsingdata.metal.expression.value.Value;
import io.parsingdata.metal.expression.value.ValueExpression;

/**
 * A {@link BinaryValueExpression} that implements the bitwise AND operator.
 */
public class And extends BinaryValueExpression {

    public And(final ValueExpression left, final ValueExpression right) {
        super(left, right);
    }

    @Override
    public Optional<Value> eval(final Value leftValue, final Value rightValue, final ParseState parseState, final Encoding encoding) {
        final BitSet leftBits = leftValue.asBitSet();
        leftBits.and(rightValue.asBitSet());
        return Optional.of(ConstantFactory.createFromBitSet(leftBits, leftValue.getValue().length, encoding));
    }

}
