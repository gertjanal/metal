package io.parsingdata.metal;

import static io.parsingdata.metal.Shorthand.cho;
import static io.parsingdata.metal.Shorthand.con;
import static io.parsingdata.metal.Shorthand.def;
import static io.parsingdata.metal.Shorthand.pre;
import static io.parsingdata.metal.Shorthand.eqNum;
import static io.parsingdata.metal.Shorthand.ref;
import static io.parsingdata.metal.Shorthand.repn;
import static io.parsingdata.metal.Shorthand.seq;
import static io.parsingdata.metal.Shorthand.sub;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.junit.Test;

import io.parsingdata.metal.data.Environment;
import io.parsingdata.metal.data.ParseGraph;
import io.parsingdata.metal.data.ParseItem;
import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.data.ParseValue;
import io.parsingdata.metal.encoding.ByteOrder;
import io.parsingdata.metal.encoding.Encoding;
import io.parsingdata.metal.token.Token;
import io.parsingdata.metal.util.InMemoryByteStream;

public class SubTest {

    private static final Token DIRECTORY = new Directory();

    @Test
    public void test() throws IOException {

        byte[] root = {
                0x1, //  0. DirectoryID
                0x3, //  1. NumberOfEntries
                0x1, //  2. Entry1.1 EntryID
                11,  //  3. Entry1.1 Pointer to data
                0x0, //  4. Entry1.1 Type LEAF

                0x2, //  5. Entry1.2 EntryID
                15,  //  6. Entry1.2 Pointer to directory 1
                0x1, //  7. Entry1.2 Type DIRECTORY

                0x1, //  8. Entry1.3 EntryID
                23,  //  9. Entry1.3 Pointer to data
                0x0, // 10. Entry1.3 Type LEAF

                'E', // 11. Entry1.1 Data
                'N', // 12.
                '1', // 13.
                '1', // 14.

                0x1, // 15. DirectoryID
                0x2, // 16. NumberOfEntries

                0x1, //  17. Entry2.1 EntryID
                27,  //  18. Entry2.1 Pointer to data
                0x0, //  19. Entry2.1 Type LEAF

                0x1, //  20. Entry2.2 EntryID
                31,  //  21. Entry2.2 Pointer to data
                0x0, //  22. Entry2.2 Type LEAF

                'E', // 23. Entry1.3 Data
                'N', // 24.
                '1', // 25.
                '3', // 26.

                'E', // 27. Entry2.1 Data
                'N', // 28.
                '2', // 29.
                '1', // 30.

                'E', // 31. Entry2.2 Data
                'N', // 32.
                '2', // 33.
                '2'  // 34.
        };
        
        Environment env = new Environment(new InMemoryByteStream(root));
        Encoding enc = new Encoding(false, StandardCharsets.UTF_8, ByteOrder.LITTLE_ENDIAN);
        ParseResult result = DIRECTORY.parse(env, enc);
        ParseGraph graph = result.getEnvironment().order;
        
        step(graph);
    }
    
    private static void step(final ParseItem item) {
        if (!item.isGraph()) {
            if (item.isValue()) {
                ParseValue value = item.asValue();
                String line = value.getFullName() + ": ";
                if (value.getValue().length == 1) {
                    line += value.asNumeric().intValue();
                }
                else {
                    line += value.asString();
                }
                System.out.println(line);
            }
            return;
        }
        if (item.asGraph().head == null) {
            return;
        }
        step(item.asGraph().tail);
        step(item.asGraph().head);
    }

    static class Directory extends Token {

        private static Token LEAF = def("Name", 4);

        private Token _struct;

        Directory() {
            super(null);

            Token entry = seq(
                    def("EntryID", 1), def("Pointer", 1),
                    cho(
                        seq(
                            def("Type", 1, eqNum(con(0))), // 0 == entry
                            sub(LEAF, ref("Pointer"))
                        ), seq(
                            def("Type", 1, eqNum(con(1))), // 1 == dir
                            sub(this, ref("Pointer")))));

            _struct = seq(
                def("DirectoryID", 1),
                def("NumberOfEntries", 1),
                repn(entry, ref("NumberOfEntries")));
        }

        @Override
        protected ParseResult parseImpl(String scope, Environment env, Encoding enc) throws IOException {
            return _struct.parse(env, enc);
        }
    }
}
