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

package io.parsingdata.metal.data;

import io.parsingdata.metal.expression.value.OptionalValue;
import io.parsingdata.metal.expression.value.Value;
import org.junit.Test;

import static io.parsingdata.metal.data.OptionalValueList.EMPTY;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static junit.framework.TestCase.assertTrue;

public class OptionalValueListTest {

    @Test
    public void containsEmptyInTail() {
        assertTrue(EMPTY.add(OptionalValue.empty()).add(OptionalValue.of(new Value(new byte[] { 1, 2 }, enc()))).containsEmpty());
    }

}