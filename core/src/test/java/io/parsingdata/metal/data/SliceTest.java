/*
 * Copyright 2013-2020 Netherlands Forensic Institute
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

import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TEN;
import static java.math.BigInteger.ZERO;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.eq;
import static io.parsingdata.metal.Shorthand.last;
import static io.parsingdata.metal.Shorthand.len;
import static io.parsingdata.metal.Shorthand.post;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.toByteArray;
import static io.parsingdata.metal.data.ParseGraph.NONE;
import static io.parsingdata.metal.data.ParseState.createFromByteStream;
import static io.parsingdata.metal.data.Slice.createFromBytes;
import static io.parsingdata.metal.data.selection.ByName.getValue;
import static io.parsingdata.metal.util.EncodingFactory.enc;
import static io.parsingdata.metal.util.EnvironmentFactory.env;
import static io.parsingdata.metal.util.ParseStateFactory.stream;

import java.math.BigInteger;
import java.util.Optional;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import io.parsingdata.metal.util.InMemoryByteStream;
import io.parsingdata.metal.util.ReadTrackingByteStream;

public class SliceTest {

    @Rule public final ExpectedException thrown = ExpectedException.none();

    @Test
    public void lazyRead() {
        final ReadTrackingByteStream stream = new ReadTrackingByteStream(new InMemoryByteStream(toByteArray(1, 2, 3, 0, 0, 4, 1)));
        final Optional<ParseState> result =
            seq(def("a", con(3)),
                post(def("b", con(2)), eq(con(0, 0))),
                def("c", con(1)),
                post(def("d", con(1)), eq(con(1)))).parse(env(createFromByteStream(stream), enc()));
        assertTrue(result.isPresent());
        assertTrue(stream.containsAll(3, 4, 6));
        assertTrue(stream.containsNone(0, 1, 2, 5));
    }

    @Test
    public void lazyLength() {
        final ReadTrackingByteStream stream = new ReadTrackingByteStream(new InMemoryByteStream(toByteArray(1, 2, 3, 0, 0, 0, 4, 1)));
        final Optional<ParseState> result =
            seq(def("a", con(3)),
                post(def("b", last(len(last(ref("a"))))), eq(con(0, 0, 0))),
                def("c", con(1)),
                post(def("d", last(len(last(ref("c"))))), eq(con(1)))).parse(env(createFromByteStream(stream), enc()));
        assertTrue(result.isPresent());
        assertTrue(stream.containsAll(3, 4, 5, 7));
        assertTrue(stream.containsNone(0, 1, 2, 6));
    }

    @Test
    public void retrieveDataFromSliceWithNegativeLimit() {
        thrown.expect(IllegalArgumentException.class);
        thrown.expectMessage("Argument limit may not be negative.");
        assertArrayEquals(new byte[] {}, Slice.createFromSource(new ConstantSource(new byte[] { 0, 1, 2, 3 }), ZERO, BigInteger.valueOf(4)).get().getData(BigInteger.valueOf(-1)));
    }


    @Test
    public void retrievePartialDataFromSlice() {
        assertArrayEquals(new byte[] { 0 }, Slice.createFromSource(new ConstantSource(new byte[] { 0, 1, 2, 3 }), ZERO, BigInteger.valueOf(4)).get().getData(ONE));
        assertArrayEquals(new byte[] { 0, 1, 2, 3 }, Slice.createFromSource(new ConstantSource(new byte[] { 0, 1, 2, 3 }), ZERO, BigInteger.valueOf(4)).get().getData(TEN));
    }

    @Test
    public void sliceToString() {
        final ParseValue pv1 = new ParseValue("name", NONE, createFromBytes(new byte[]{1, 2}), enc());
        assertEquals("Slice(ConstantSource(0x0102)@0:2)", pv1.slice().toString());
        final ParseState oneValueParseState = stream().add(pv1);
        final ParseState twoValueParseState = oneValueParseState.add(new ParseValue("name2", NONE, Slice.createFromSource(new DataExpressionSource(ref("name"), 0, oneValueParseState, enc()), ZERO, BigInteger.valueOf(2)).get(), enc()));
        final String dataExpressionSliceString = getValue(twoValueParseState.order, "name2").slice().toString();
        assertTrue(dataExpressionSliceString.startsWith("Slice(DataExpressionSource(NameRef(name)[0]("));
        assertTrue(dataExpressionSliceString.endsWith(")@0:2)"));
    }

}
