/*
 * Copyright 2014 Malte Schütze
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

package net.boreeas.riotapi.loginqeue;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import jdk.nashorn.internal.parser.JSONParser;
import net.boreeas.riotapi.Shard;
import org.apache.cxf.jaxrs.client.WebClient;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by malte on 7/11/2014.
 */
public class LoginQueue {
    private WebTarget tgt;

    public LoginQueue(Shard shard) {
        tgt = ClientBuilder.newClient().target(shard.loginQueue).path("login-queue/rest/queue/authenticate");
    }

    public AuthResult getAuthToken(String user, String password) {
        String payload = String.format("payload=user=%s,password=%s", user, password);
        Response response = tgt.request().post(Entity.entity(payload, MediaType.APPLICATION_FORM_URLENCODED));


        if (response.getStatus() == 403) {
            throw new InvalidCredentialsException("Invalid username or password");
        } else if (response.getStatus() != 200) {
            throw new LoginException("Error " + response.getStatus());
        }


        String json;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) response.getEntity()))) {
            json = reader.readLine();
        } catch (IOException ex) {
            throw new LoginException("Error reading JSON", ex);
        }

        JsonObject result = new JsonParser().parse(json).getAsJsonObject();
        switch (result.get("status").getAsString()) {
            case "FAILED":
                throw new LoginException("Login failed: " + result.get("reason").getAsString());
            case "LOGIN":
                IngameCredentials credentials = new Gson().fromJson(result.get("inGameCredentials"), IngameCredentials.class);
                return new AuthResult(result.get("token").getAsString(), credentials);
            case "QUEUE":
                Type type = new TypeToken<List<Ticker>>(){}.getType();
                List<Ticker> tickers = new Gson().fromJson(result.get("tickers"), type);
                return new AuthResult(result.get("delay").getAsInt(), result.get("node").getAsInt(), tickers);
            default:
                throw new LoginException(result.toString());
        }
    }

    public QueueTimer waitInQueue(String user, String password) {
        QueueTimer timer = new QueueTimer(this, user, password);
        timer.start();
        return timer;
    }
}
