/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.hisp.dhis.android.sdk.core.network;

import com.squareup.okhttp.HttpUrl;

import org.hisp.dhis.android.sdk.core.models.Credentials;
import org.hisp.dhis.android.sdk.core.models.Session;

import org.hisp.dhis.android.sdk.core.network.RepositoryManager.ServerEndpoint;
import org.hisp.dhis.android.sdk.core.network.RepositoryManager.AuthInterceptor;

public final class DhisApi {
    private final ServerEndpoint endpoint;
    private final AuthInterceptor interceptor;
    private final IDhisApi dhisApi;

    private Session session;

    DhisApi(ServerEndpoint endpoint, AuthInterceptor interceptor, IDhisApi dhisApi) {
        this.endpoint = endpoint;
        this.interceptor = interceptor;
        this.dhisApi = dhisApi;
    }

    public void setSession(Session session) {
        this.session = session;

        HttpUrl url = null;
        Credentials credentials = null;

        if (session != null) {
            url = session.getServerUrl();
            credentials = session.getCredentials();
        }

        setServerUrl(url);
        setCredentials(credentials);
    }

    public Session getSession() {
        return session;
    }

    private void setServerUrl(HttpUrl httpUrl) {
        String url = null;
        if (httpUrl != null) {
            url = httpUrl.newBuilder()
                    .addPathSegment("api")
                    .build().toString();
        }
        endpoint.setServerUrl(url);
    }

    private void setCredentials(Credentials credentials) {
        String username = null;
        String password = null;

        if (credentials != null) {
            username = credentials.getUsername();
            password = credentials.getPassword();
        }
        interceptor.setUsername(username);
        interceptor.setPassword(password);
    }

    public IDhisApi getApi() {
        return dhisApi;
    }
}
