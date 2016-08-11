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

package io.parsingdata.metal.format;

import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;

import static io.parsingdata.metal.Shorthand.*;
import static io.parsingdata.metal.format.Callback.crc32;

public final class PNG {

    private PNG() {}

    private static final Token HEADER =
            seq("signature",
                def("highbit", con(1), eq(con(0x89))),
                def("PNG", con(3), eq(con("PNG"))),
                def("controlchars", con(4), eq(con(0x0d, 0x0a, 0x1a, 0x0a))));

    private static final Token FOOTER =
            seq("footer",
                def("footerlength", con(4), eqNum(con(0))),
                def("footertype", con(4), eq(con("IEND"))),
                def("footercrc32", con(4), eq(con(0xae, 0x42, 0x60, 0x82))));

    private static final Token STRUCT =
            seq("chunk",
                def("length", con(4)),
                def("chunktype", con(4), not(eq(con("IEND")))),
                def("chunkdata", last(ref("length"))),
                def("crc32", con(4), eq(crc32(cat(last(ref("chunktype")), last(ref("chunkdata")))))));

    public static final Token FORMAT =
            seq("PNG", new Encoding(),
                HEADER,
                rep(STRUCT),
                FOOTER);

}
