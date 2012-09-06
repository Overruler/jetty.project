//========================================================================
//Copyright 2012-2012 Mort Bay Consulting Pty. Ltd.
//------------------------------------------------------------------------
//All rights reserved. This program and the accompanying materials
//are made available under the terms of the Eclipse Public License v1.0
//and Apache License v2.0 which accompanies this distribution.
//The Eclipse Public License is available at
//http://www.eclipse.org/legal/epl-v10.html
//The Apache License v2.0 is available at
//http://www.opensource.org/licenses/apache2.0.php
//You may elect to redistribute this code under either of these licenses.
//========================================================================

package org.eclipse.jetty.client;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Destination;
import org.eclipse.jetty.client.api.Response;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.Assert;
import org.junit.Test;

public class HttpClientTest extends AbstractHttpClientServerTest
{
    @Test
    public void test_GET_NoResponseContent() throws Exception
    {
        start(new EmptyHandler());

        Response response = client.GET("http://localhost:" + connector.getLocalPort()).get(5, TimeUnit.SECONDS);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.status());
    }

    @Test
    public void test_GET_ResponseContent() throws Exception
    {
        final byte[] data = new byte[]{0, 1, 2, 3, 4, 5, 6, 7};
        start(new AbstractHandler()
        {
            @Override
            public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException
            {
                response.getOutputStream().write(data);
                baseRequest.setHandled(true);
            }
        });

        ContentResponse response = client.GET("http://localhost:" + connector.getLocalPort()).get(5, TimeUnit.SECONDS);

        Assert.assertNotNull(response);
        Assert.assertEquals(200, response.status());
        byte[] content = response.content();
        Assert.assertArrayEquals(data, content);
    }

    @Test
    public void test_DestinationCount() throws Exception
    {
        start(new EmptyHandler());

        client.GET("http://localhost:" + connector.getLocalPort()).get(5, TimeUnit.SECONDS);

        List<Destination> destinations = client.getDestinations();
        Assert.assertNotNull(destinations);
        Assert.assertEquals(1, destinations.size());
        Destination destination = destinations.get(0);
        Assert.assertNotNull(destination);
    }
}