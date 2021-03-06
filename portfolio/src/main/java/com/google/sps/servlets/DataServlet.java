// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns nametag text. */
@WebServlet("/nametag-text")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    // Initial list of nametag greetings
    ArrayList<String> greetings = new ArrayList<>();
    greetings.add("hi & welcome");
    greetings.add("to the portfolio");
    greetings.add("of ashley granitto");

    // Send JSON-converted greetings list as the response
    String json = convertToJson(greetings);
    response.setContentType("application/json;");
    response.getWriter().println(json);
  }

  // Converts a String-type ArrayList into a JSON string using the Gson library. 
  private String convertToJson(ArrayList<String> list) {
    Gson gson = new Gson();
    String json = gson.toJson(list);
    return json;
  }
}
