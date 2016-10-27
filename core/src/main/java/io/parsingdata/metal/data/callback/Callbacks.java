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

package io.parsingdata.metal.data.callback;

import static io.parsingdata.metal.Util.checkNotNull;

import io.parsingdata.metal.data.ParseResult;
import io.parsingdata.metal.token.Token;

public class Callbacks {

    public static final Callbacks NONE = new Callbacks(TokenCallbackList.EMPTY);

    public final Callback genericCallback;
    public final TokenCallbackList tokenCallbacks;

    public Callbacks(final Callback genericCallback, final TokenCallbackList tokenCallbacks) {
        this.genericCallback = genericCallback;
        this.tokenCallbacks = checkNotNull(tokenCallbacks, "tokenCallbacks");
    }

    public Callbacks(final Callback genericCallback) {
        this(genericCallback, TokenCallbackList.EMPTY);
    }

    public Callbacks(final TokenCallbackList tokenCallbacks) {
        this(null, tokenCallbacks);
    }

    public void handle(final Token token, final ParseResult result) {
        if (genericCallback != null) {
            genericCallback.handle(token, result);
        }
        handleCallbacks(tokenCallbacks, token, result);
    }

    private void handleCallbacks(final TokenCallbackList callbacks, final Token token, final ParseResult result) {
        if (callbacks.isEmpty()) { return; }
        if (callbacks.head.token == token) {
            callbacks.head.callback.handle(token, result);
        }
        handleCallbacks(callbacks.tail, token, result);
    }

    @Override
    public String toString() {
        return (genericCallback == null ? "" : "generic: " + genericCallback.toString() + "; ") +
                (tokenCallbacks.isEmpty() ? "" : "token: " + tokenCallbacks.toString());
    }

}
